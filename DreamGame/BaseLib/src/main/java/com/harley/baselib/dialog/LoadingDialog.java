package com.harley.baselib.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.harley.baselib.R;

public class LoadingDialog extends BaseDiaolg{

    private Context context;
    private Handler delivery;

    private AlertDialog alertDialog;
    private AnimationDrawable animationDrawable;
    private String message;
    private boolean cancelable;

    public static class Builder {
        private Context context;
        private String message;
        private boolean cancelable = false;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setMessage(String message){
            this.message = message;
            return this;
        }

        public Builder setCancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public LoadingDialog create(){
            return new LoadingDialog(this);
        }

        public void show(){
            this.create().show();
        }
    }

    public LoadingDialog(Builder builder) {
        this.context = builder.context;
        this.message = builder.message;
        this.cancelable = builder.cancelable;
        this.delivery = new Handler(Looper.getMainLooper());

        build();
    }

    @Override
    protected void build(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.BaseDialog);
        builder.setCancelable(cancelable);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_loading, null);
        ImageView imageView = view.findViewById(R.id.iv_loading);
        TextView textView = view.findViewById(R.id.tv_message);

        animationDrawable = (AnimationDrawable) imageView.getDrawable();

        if (!TextUtils.isEmpty(message)){
            textView.setVisibility(View.VISIBLE);
            textView.setText(message);
        }

        alertDialog = builder.create();
        alertDialog.setView(view);

    }

    @Override
    public void show(){
        delivery.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.start();
                alertDialog.show();
            }
        });
    }

    @Override
    public void dismiss() {
        delivery.post(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean isShowing() {
        return alertDialog.isShowing();
    }
}
