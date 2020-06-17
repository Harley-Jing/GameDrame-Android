package com.harley.baselib.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.harley.baselib.R;
import com.harley.baselib.utils.SystemUIUtils;

public class BottomDialog extends BaseDiaolg{
    private Context context;

    private AlertDialog alertDialog;
    private View customView;
    private OnBindView onBindView;
    private boolean cancelable;
    private Theme theme;

    private LinearLayout containerParent;
    private RelativeLayout containerCustom;

    private DialogLifeCycleListener lifeCycleListener;

    public static class Builder {
        private Context context;
        private View customView;
        private OnBindView onBindView;
        private boolean cancelable = false;
        private Theme theme = Theme.LIGHT;

        private DialogLifeCycleListener lifeCycleListener;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setCancelable(boolean cancelable){
            this.cancelable = cancelable;
            return this;
        }

        public Builder setTheme(Theme theme){
            this.theme = theme;
            return this;
        }

        public Builder setCustomView(int customViewLayoutId, OnBindView onBindView){
            this.customView = LayoutInflater.from(context).inflate(customViewLayoutId, null);
            this.onBindView = onBindView;
            return this;
        }

        public Builder setDialogLifeCycleListener(DialogLifeCycleListener listener){
            this.lifeCycleListener = listener;
            return this;
        }

        public BottomDialog create(){
            return new BottomDialog(this);
        }

        public void show(){
            this.create().show();
        }
    }

    public BottomDialog(Builder builder){
        this.context = builder.context;
        this.customView = builder.customView;
        this.onBindView = builder.onBindView;
        this.cancelable = builder.cancelable;
        this.theme = builder.theme;

        this.lifeCycleListener = builder.lifeCycleListener;

        build();
        postDialogOnCreate(alertDialog, lifeCycleListener);
    }

    @Override
    protected void build(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.BottomDialog);
        builder.setCancelable(cancelable);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_bottom, null);
        containerParent = view.findViewById(R.id.container_parent);
        if (containerCustom != null) containerCustom.removeAllViews();
        containerCustom = view.findViewById(R.id.container_custom);

        ImageView ivTab = view.findViewById(R.id.img_tab);

        //避免系统导航栏遮挡UI
        if (SystemUIUtils.isNavigationBarShow((Activity)context)){
            TextView tvNavigationBar = view.findViewById(R.id.navigation_bar);
            ViewGroup.LayoutParams params = tvNavigationBar.getLayoutParams();
            params.height = SystemUIUtils.getNavigationBarHeight(context);
            tvNavigationBar.setLayoutParams(params);
            tvNavigationBar.setVisibility(View.VISIBLE);
        }

        switch (theme){
            case DARK:
                containerParent.setBackgroundResource(R.drawable.drawable_dialog_bottom_background_dark);
                ivTab.setBackgroundResource(R.drawable.drawable_share_material_tab_dark);
                break;
            case LIGHT:
                containerParent.setBackgroundResource(R.drawable.drawable_dialog_bottom_background_light);
                ivTab.setBackgroundResource(R.drawable.drawable_share_material_tab_light);
                break;
        }

        containerParent.setOnTouchListener(touchListener);
        ivTab.setVisibility(View.VISIBLE);
        containerCustom.setVisibility(View.VISIBLE);

        if (customView != null){
            containerCustom.removeAllViews();
            containerCustom.addView(customView);
            if (onBindView != null){
                onBindView.onBind(this, customView);
            }
        }

        alertDialog = builder.create();
        alertDialog.setView(view);

        setDialogSize();
    }

    private void setDialogSize(){

        if (alertDialog != null){
            Window window = alertDialog.getWindow();

            if (window != null){
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.width = SystemUIUtils.getScreenWidth(window);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    //兼容刘海屏
                    attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                }
                window.setAttributes(attributes);
                window.setGravity(Gravity.BOTTOM);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                    window.setNavigationBarColor(Color.TRANSPARENT);
                }
            }
        }
    }

    @Override
    public void show(){
        postDialogOnShow(alertDialog, lifeCycleListener);
    }

    @Override
    public void dismiss() {
        postDialogDismiss(alertDialog, lifeCycleListener);
    }

    @Override
    public boolean isShowing(){
        return alertDialog.isShowing();
    }

    public interface OnBindView {
        void onBind(BottomDialog dialog, View view);
    }

    private boolean isTouchDown;
    private float touchDownY;
    private float oldY;

    private View.OnTouchListener touchListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouchDown = true;
                    touchDownY = motionEvent.getY();
                    oldY = containerParent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isTouchDown){
                        float deltaY = motionEvent.getY() - touchDownY;
                        float aimY = containerParent.getY() + deltaY;
                        if (aimY > 0){
                            containerParent.setY(aimY);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isTouchDown) {
                        float deltaY = containerParent.getY() - oldY;

                        if (deltaY > (float)containerParent.getHeight() / 2) {
                            dismiss();
                        }

                        containerParent.animate().setDuration(300).translationY(0);
                    }
                    isTouchDown = false;
                    break;
            }
            return true;
        }
    };
}
