package com.harley.baselib.http.callback;

public abstract class OkUploadCallback extends OkCallback<String>{
    public abstract void inProgress(float progress, long total, long speed);
}
