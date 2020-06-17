package com.harley.baselib.dialog;

import android.app.Dialog;

public interface DialogLifeCycleListener {
    public void onCreate(Dialog dialog);
    public void onShow(Dialog dialog);
    public void onDismiss();
}
