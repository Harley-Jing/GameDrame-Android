package com.harley.baselib.dialog;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;

public abstract class BaseDiaolg {

    protected  Handler delivery;

    protected BaseDiaolg(){
        this.delivery = new Handler(Looper.getMainLooper());
    }

    public abstract void show();
    public abstract void dismiss();
    public abstract boolean isShowing();
    protected abstract void build();

    protected void postDialogOnCreate(final Dialog dialog, final DialogLifeCycleListener listener){
        delivery.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null){
                    listener.onCreate(dialog);
                }
            }
        });
    }

    protected void postDialogOnShow(final Dialog dialog, final DialogLifeCycleListener listener){
        delivery.post(new Runnable() {
            @Override
            public void run() {
                dialog.show();
                if (listener != null){
                    listener.onShow(dialog);
                }
            }
        });
    }

    protected void postDialogDismiss(final Dialog dialog, final DialogLifeCycleListener listener){
        delivery.post(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                if (listener != null){
                    listener.onDismiss();
                }
            }
        });
    }

    public enum Theme{
        LIGHT,
        DARK
    }
}
