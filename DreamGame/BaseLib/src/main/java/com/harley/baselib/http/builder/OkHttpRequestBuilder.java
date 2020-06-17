package com.harley.baselib.http.builder;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {
    protected String url;
    protected Object tag;
    protected Map<String, String> headers;
    protected Map<String, String> params;

    protected long readTimeOut;
    protected long writeTimeOut;
    protected long connTimeOut;
    protected int tryAgainCount = 0;//默认不重试
    protected boolean isDebug = true;

    public T url(String url)
    {
        this.url = url;
        return (T) this;
    }

    public T tag(Object tag)
    {
        this.tag = tag;
        return (T) this;
    }

    public T headers(Map<String, String> headers)
    {
        this.headers = headers;
        return (T) this;
    }

    public T addHeader(String key, String val)
    {
        if (this.headers == null)
        {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return (T) this;
    }

    /**
     *
     * @param readTimeOut MILLISECONDS
     * @return
     */
    public T readTimeOut(long readTimeOut)
    {
        this.readTimeOut = readTimeOut;
        return (T) this;
    }

    /**
     *
     * @param writeTimeOut MILLISECONDS
     * @return
     */
    public T writeTimeOut(long writeTimeOut)
    {
        this.writeTimeOut = writeTimeOut;
        return (T) this;
    }

    public T connTimeOut(long connTimeOut)
    {
        this.connTimeOut = connTimeOut;
        return (T) this;
    }

    public T tryAgainCount(int tryAgainCount) {
        this.tryAgainCount = tryAgainCount;
        return (T) this;
    }

    public T isDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return (T) this;
    }
}
