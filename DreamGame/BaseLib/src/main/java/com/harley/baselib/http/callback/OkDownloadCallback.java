package com.harley.baselib.http.callback;

import java.io.File;

public abstract class OkDownloadCallback extends OkCallback<File> {
    public abstract boolean isRange();
    public abstract void pause(float progress, long total);
    public abstract void inProgress(float progress, long total, long speed);
}
