package com.harley.baselib.http.builder;

import android.os.Handler;
import android.text.TextUtils;

import com.harley.baselib.http.OkHttpUtils;
import com.harley.baselib.http.callback.OkUploadCallback;
import com.harley.baselib.http.interceptor.LoggerInterceptor;
import com.harley.baselib.http.interceptor.RetryInterceptor;
import com.harley.baselib.http.request.CountingRequestBody;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkUploadBuilder extends OkHttpRequestBuilder<OkUploadBuilder> {
    private Request okHttpRequest;
    private OkHttpClient okHttpClient;
    private Handler mDelivery;
    private Request.Builder mBuilder = new Request.Builder();

    private List<FileInput> files = new ArrayList<>();

    private long currentLength, previousLength, totalLength;
    private float progress;
    private Timer mTimer;
    private TimerTask mTimerTask;

    public OkUploadBuilder() {
        this.okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        this.mDelivery = OkHttpUtils.getInstance().getDelivery();
    }

    public OkUploadBuilder build() {

        initBuilder();
        return this;
    }

    public void execute(final OkUploadCallback callback){
        if (callback == null){
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onBefore();
            }
        });

        RequestBody requestBodyProgress = new CountingRequestBody(buildRequestBody(), new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        currentLength = bytesWritten;
                        totalLength = contentLength;
                        final float curProgress =  (currentLength * 1.00f / totalLength * 100);

                        //保留两位小数 四舍五入
                        BigDecimal bd = new BigDecimal((float) curProgress);
                        bd = bd.setScale(2, 4);
                        progress = bd.floatValue();
                    }
                });
            }
        });

        initTimer(callback);

        okHttpRequest = mBuilder.post(requestBodyProgress).build();

        buildCall().enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                destroyTimer();
                OkHttpUtils.getInstance().sendFailResultCallback(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                destroyTimer();
                if (response.isSuccessful()){
                    OkHttpUtils.getInstance().sendSuccessResultCallback(response.body().string(), callback);
                }else {
                    OkHttpUtils.getInstance().sendFailResultCallback(new IOException("request failed, reponse's code is :" + response.code()), callback);
                }
            }
        });
    }
    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initBuilder()
    {
        if (TextUtils.isEmpty(url)){
            throw new IllegalArgumentException("url can not be null.");
        }else {
            mBuilder.url(url);
        }

        if (tag != null){
            mBuilder.tag(tag);
        }

        if (headers != null) {
            mBuilder.headers(Objects.requireNonNull(appendHeaders()));
        }

    }

    private Call buildCall() {
        OkHttpClient.Builder builder = okHttpClient.newBuilder();

        readTimeOut = readTimeOut > 0 ? readTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
        writeTimeOut = writeTimeOut > 0 ? writeTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
        connTimeOut = connTimeOut > 0 ? connTimeOut : OkHttpUtils.DEFAULT_MILLISECONDS;
        tryAgainCount = Math.max(tryAgainCount, 0);

        if (isDebug){
            builder.addInterceptor(new LoggerInterceptor());
        }

        OkHttpClient client = builder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                .addInterceptor(new RetryInterceptor(tryAgainCount))
                .build();

        return client.newCall(okHttpRequest);
    }

    private Headers appendHeaders()
    {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return null;

        for (String key : headers.keySet())
        {
            headerBuilder.add(key, Objects.requireNonNull(headers.get(key)));
        }
        return headerBuilder.build();
    }

    private RequestBody buildRequestBody(){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addParams(builder);

        for (int i = 0; i < files.size(); i++)
        {
            FileInput fileInput = files.get(i);
            RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
            builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
        }
        return builder.build();
    }

    public OkUploadBuilder params(Map<String, String> params)
    {
        this.params = params;
        return this;
    }

    public OkUploadBuilder addParams(String key, String val)
    {
        if (this.params == null)
        {
            params = new LinkedHashMap<>();
        }
        params.put(key, val);
        return this;
    }

    public OkUploadBuilder files(String key, Map<String, File> files)
    {
        for (String filename : files.keySet())
        {
            this.files.add(new FileInput(key, filename, files.get(filename)));
        }
        return this;
    }

    public OkUploadBuilder addFile(String key, String filename, File file)
    {
        files.add(new FileInput(key, filename, file));
        return this;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.addFormDataPart(key, Objects.requireNonNull(params.get(key)));
            }
        }
    }

    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try
        {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public static class FileInput
    {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file)
        {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString()
        {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }

    private void initTimer(final OkUploadCallback callback) {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                //下载中更新进度条
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        long speed = (currentLength - previousLength) * 2;
                        callback.inProgress(progress, totalLength, speed);
                        previousLength = currentLength;
                    }
                });
            }
        };
        //任务定时器一定要启动
        mTimer.schedule(mTimerTask, 500, 500);
    }

    private void destroyTimer() {
        if (mTimer != null && mTimerTask != null) {
            mTimerTask.cancel();
            mTimer.cancel();
            mTimerTask = null;
            mTimer = null;
        }
    }
}
