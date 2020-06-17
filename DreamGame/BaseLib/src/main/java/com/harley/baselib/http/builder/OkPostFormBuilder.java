package com.harley.baselib.http.builder;

import android.os.Handler;
import android.text.TextUtils;

import com.harley.baselib.http.OkHttpUtils;
import com.harley.baselib.http.callback.OkStringCallback;
import com.harley.baselib.http.interceptor.LoggerInterceptor;
import com.harley.baselib.http.interceptor.RetryInterceptor;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkPostFormBuilder extends OkHttpRequestBuilder<OkPostFormBuilder> {

    private Request okHttpRequest;
    private OkHttpClient okHttpClient;
    private Handler mDelivery;


    public OkPostFormBuilder() {
        this.okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        this.mDelivery = OkHttpUtils.getInstance().getDelivery();
    }

    public OkPostFormBuilder build() {

        initBuilder();
        return this;
    }

    public void execute(final OkStringCallback callback){
        if (callback == null){
            return;
        }

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onBefore();;
            }
        });

        buildCall().enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OkHttpUtils.getInstance().sendFailResultCallback(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
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
        Request.Builder builder = new Request.Builder();
        if (TextUtils.isEmpty(url)){
            throw new IllegalArgumentException("url can not be null.");
        }else {
            builder.url(url);
        }

        if (tag != null){
            builder.tag(tag);
        }

        if (headers != null) {
            builder.headers(appendHeaders());
        }

        okHttpRequest = builder.post(buildRequestBody()).build();
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
        FormBody.Builder builder = new FormBody.Builder();
        if (this.params != null && !this.params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.add(key, Objects.requireNonNull(params.get(key)));
            }
        }
        return builder.build();
    }

    public OkPostFormBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public OkPostFormBuilder addParams(String key, String val) {
        if (this.params == null) {
            this.params = new LinkedHashMap();
        }

        this.params.put(key, val);
        return this;
    }
}
