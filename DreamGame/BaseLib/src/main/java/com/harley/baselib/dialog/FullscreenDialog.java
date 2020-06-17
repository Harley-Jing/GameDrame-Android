package com.harley.baselib.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.harley.baselib.R;
import com.harley.baselib.utils.DimentionUtils;
import com.harley.baselib.utils.SystemUIUtils;

public class FullscreenDialog extends BaseDiaolg{
    private Context context;

    private AlertDialog alertDialog;
    private String title;
    private View customView;
    private OnBindView onBindView;
    private boolean cancelable;
    private Theme theme;

    private LinearLayout containerParent;
    private RelativeLayout containerCustom;

    private DialogLifeCycleListener lifeCycleListener;

    public static class Builder {
        private Context context;
        private String title;
        private View customView;
        private OnBindView onBindView;
        private boolean cancelable = true;
        private Theme theme = Theme.LIGHT;

        private DialogLifeCycleListener lifeCycleListener;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
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

        public FullscreenDialog create(){
            return new FullscreenDialog(this);
        }

        public void show(){
            this.create().show();
        }
    }

    public FullscreenDialog(Builder builder){
        this.context = builder.context;
        this.title = builder.title;
        this.cancelable = builder.cancelable;
        this.customView = builder.customView;
        this.onBindView = builder.onBindView;
        this.theme = builder.theme;

        this.lifeCycleListener = builder.lifeCycleListener;

        build();
        postDialogOnCreate(alertDialog, lifeCycleListener);
    }

    @Override
    protected void build(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.BottomDialog);
        builder.setCancelable(cancelable);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_fullscreen, null);
        containerParent = view.findViewById(R.id.container_parent);
        if (containerCustom != null) containerCustom.removeAllViews();
        containerCustom = view.findViewById(R.id.container_custom);

        ImageView ivTab = view.findViewById(R.id.img_tab);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        switch (theme){
            case DARK:
                containerParent.setBackgroundResource(R.drawable.drawable_dialog_fullscreen_background_dark);
                ivTab.setBackgroundResource(R.drawable.drawable_share_material_tab_dark);
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_title_material_light));
                break;
            case LIGHT:
                containerParent.setBackgroundResource(R.drawable.drawable_dialog_fullscreen_background_light);
                ivTab.setBackgroundResource(R.drawable.drawable_share_material_tab_light);
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_title_material_dark));
                break;
        }

        containerParent.setOnTouchListener(touchListener);

        if (TextUtils.isEmpty(title)){
            ivTab.setVisibility(View.VISIBLE);
            tvTitle.setVisibility(View.GONE);
            tvTitle.setText("");
        }else {
            ivTab.setVisibility(View.GONE);
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }

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
                attributes.height = SystemUIUtils.getScreenHeight(window);
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
        void onBind(FullscreenDialog dialog, View view);
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
                        if (aimY < SystemUIUtils.getStatusBarHeight(context))
                            aimY = (float) (SystemUIUtils.getStatusBarHeight(context));
                        containerParent.setY(aimY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isTouchDown) {
                        float deltaY = containerParent.getY() - oldY;

                        if (deltaY > DimentionUtils.dip2px(context, 150)) {
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
