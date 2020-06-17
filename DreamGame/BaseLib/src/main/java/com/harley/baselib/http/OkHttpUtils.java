package com.harley.baselib.http;

import android.os.Handler;
import android.os.Looper;

import com.harley.baselib.http.builder.OkDownloadBuilder;
import com.harley.baselib.http.builder.OkGetBuilder;
import com.harley.baselib.http.builder.OkPostFormBuilder;
import com.harley.baselib.http.builder.OkPostStringBuilder;
import com.harley.baselib.http.builder.OkUploadBuilder;
import com.harley.baselib.http.callback.OkCallback;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class OkHttpUtils {

    //默认10s
    public static final long DEFAULT_MILLISECONDS = 10_000L;

    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    //这个handler的作用是把子线程切换主线程。在后面接口中的具体实现，就不需要用handler去回调了
    private Handler mDelivery;

    private OkHttpUtils(OkHttpClient okHttpClient)
    {
        if (okHttpClient == null)
        {
            mOkHttpClient = new OkHttpClient();
        } else
        {
            mOkHttpClient = okHttpClient;
        }

        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient)
    {
        if (mInstance == null)
        {
            synchronized (OkHttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance()
    {
        return initClient(null);
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public static OkGetBuilder get() {
        return new OkGetBuilder();
    }

    public static OkPostStringBuilder postString() {
        return new OkPostStringBuilder();
    }

    public static OkPostFormBuilder postForm() {
        return new OkPostFormBuilder();
    }

    public static OkDownloadBuilder download() {
        return new OkDownloadBuilder();
    }

    public static OkUploadBuilder upload() {
        return new OkUploadBuilder();
    }

    //tag取消网络请求
    public void cancelOkhttpTag(String tag) {
        Dispatcher dispatcher = mOkHttpClient.dispatcher();
        synchronized (dispatcher) {
            //请求列表里的，取消网络请求
            for (Call call : dispatcher.queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            //正在请求网络的，取消网络请求
            for (Call call : dispatcher.runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }

    public void sendFailResultCallback(final Exception e, final OkCallback callback)
    {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter();
                callback.onError(e);
            }
        });
    }

    public void sendSuccessResultCallback(final Object object, final OkCallback callback)
    {
        if (callback == null) return;

        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter();
                callback.onResponse(object);
            }
        });
    }
}
