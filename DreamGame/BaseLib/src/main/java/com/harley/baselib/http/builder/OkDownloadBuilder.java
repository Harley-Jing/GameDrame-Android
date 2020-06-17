package com.harley.baselib.http.builder;

import android.os.Handler;
import android.text.TextUtils;

import com.harley.baselib.http.OkHttpUtils;
import com.harley.baselib.http.callback.OkDownloadCallback;
import com.harley.baselib.http.interceptor.LoggerInterceptor;
import com.harley.baselib.http.interceptor.RetryInterceptor;
import com.harley.baselib.utils.SharePreferenceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkDownloadBuilder extends OkHttpRequestBuilder<OkDownloadBuilder> {

    //任务定时器
    private Timer mTimer;
    private TimerTask mTimerTask;
    private String path;
    private String fileName;
    private long fileCurrentLength, filePreviousLength, fileTotalLength;
    private float progress;

    private Request okHttpRequest;
    private OkHttpClient okHttpClient;
    private Handler mDelivery;
    private Request.Builder mBuilder = new Request.Builder();

    public OkDownloadBuilder() {
        this.okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        this.mDelivery = OkHttpUtils.getInstance().getDelivery();
    }

    public OkDownloadBuilder build() {
        initBuilder();
        return this;
    }

    public void execute(final OkDownloadCallback callback){
        if (callback == null){
            return;
        }
        if (TextUtils.isEmpty(fileName)){
            fileName = (String) SharePreferenceUtils.get("" + tag, "");
        }
        if (callback.isRange()){
            File exFile = new File(path, fileName);
            if (exFile.exists()) {
                fileCurrentLength = exFile.length();
                mBuilder.addHeader("Range", "bytes=" + fileCurrentLength + "-");
            }
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onBefore();
            }
        });

        initTimer(callback);

        okHttpRequest = mBuilder.build();
        buildCall().enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkHttpUtils.getInstance().sendFailResultCallback(e, callback);
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                InputStream stream = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                if (TextUtils.isEmpty(fileName)){
                    fileName = getHeaderFileName(response);
                    if (TextUtils.isEmpty(fileName)){
                        OkHttpUtils.getInstance().sendFailResultCallback(new Exception("Please check filename"), callback);
                        return;
                    }
                }
                //储存下载文件名
                SharePreferenceUtils.put("" + tag, fileName);

                final File file = new File(dir, fileName);

                try {
                    stream = response.body().byteStream();
                    //如果当前长度就等于要下载的长度，那么此文件就是下载好的文件
                    if (fileCurrentLength == response.body().contentLength()) {
                        OkHttpUtils.getInstance().sendSuccessResultCallback(file, callback);
                    }

                    if (callback.isRange()) {
                        fileTotalLength = response.body().contentLength() + fileCurrentLength;
                    } else {
                        fileTotalLength = response.body().contentLength();
                    }

                    if (callback.isRange()) {
                        //这个方法是文件开始拼接
                        fos = new FileOutputStream(file, true);
                    } else {
                        //这个是不拼接，从头开始
                        fos = new FileOutputStream(file);
                    }

                    if (!callback.isRange()){
                        fileCurrentLength = 0;
                    }

                    while ((len = stream.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        fileCurrentLength += len;
                        final float curProgress =  (fileCurrentLength * 1.00f / fileTotalLength * 100);

                        //保留两位小数 四舍五入
                        BigDecimal bd = new BigDecimal((float) curProgress);
                        bd = bd.setScale(2, 4);
                        progress = bd.floatValue();
                    }
                    fos.flush();
                    //下载完成
                    SharePreferenceUtils.remove("" + tag);
                    OkHttpUtils.getInstance().sendSuccessResultCallback(file, callback);

                } catch (final Exception e) {
                    if(callback.isRange() && e instanceof SocketException){
                        mDelivery.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onAfter();
                                callback.pause(progress, fileTotalLength);
                            }
                        });
                    } else{
                        OkHttpUtils.getInstance().sendFailResultCallback(e, callback);
                    }
                } finally {
                    destroyTimer();
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException ignored) {

                    }
                }
            }
        });
    }

    private void initBuilder()
    {
        if (TextUtils.isEmpty(url)){
            throw new IllegalArgumentException("url can not be null.");
        }else {
            mBuilder.url(url);
        }

        if (tag == null){
            throw new IllegalArgumentException("tag can not be null.");
        }else {
            mBuilder.tag(tag);
        }

        if (TextUtils.isEmpty(path)){
            throw new IllegalArgumentException("path can not be null.");
        }

        mBuilder.cacheControl(CacheControl.FORCE_NETWORK);
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

    private void initTimer(final OkDownloadCallback callback) {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                //下载中更新进度条
                mDelivery.post(new Runnable() {
                    @Override
                    public void run() {
                        long speed = (fileCurrentLength - filePreviousLength) * 2;
                        callback.inProgress(progress, fileTotalLength, speed);
                        filePreviousLength = fileCurrentLength;
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

    public OkDownloadBuilder path(String path) {
        this.path = path;
        return this;
    }

    public OkDownloadBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return "";
        }
        return "";
    }
}