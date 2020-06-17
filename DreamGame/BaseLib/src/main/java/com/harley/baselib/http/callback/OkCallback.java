package com.harley.baselib.http.callback;

public abstract class OkCallback<T> {
    /**
     * UI Thread
     *
     */
    public void onBefore()
    {
    }

    /**
     * UI Thread
     *
     */
    public void onAfter()
    {
    }

    /**
     * UI Thread
     *
     */
    public abstract void onError(Exception e);


    /**
     * UI Thread
     *
     */
    public abstract void onResponse(T response);
}
