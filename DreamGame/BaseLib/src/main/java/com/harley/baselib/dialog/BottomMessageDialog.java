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

public class BottomMessageDialog extends BaseDiaolg{
    private Context context;

    private AlertDialog alertDialog;
    private String title, message, positiveText, negativeText;
    private boolean cancelable;
    private Theme theme;

    private LinearLayout containerParent;
    private RelativeLayout containerCustom;

    private DialogLifeCycleListener lifeCycleListener;

    private View.OnClickListener positiveListener, negativeListener;

    public static class Builder {
        private Context context;
        private String title, message, positiveText, negativeText;
        private boolean cancelable = false;
        private Theme theme = Theme.LIGHT;

        private DialogLifeCycleListener lifeCycleListener;
        private View.OnClickListener positiveListener, negativeListener;

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

        public Builder setDialogLifeCycleListener(DialogLifeCycleListener listener){
            this.lifeCycleListener = listener;
            return this;
        }

        public Builder setMessage(String message){
            this.message = message;
            return this;
        }

        public Builder setPositiveButton(String text, View.OnClickListener listener){
            this.positiveText = text;
            this.positiveListener = listener;
            return this;
        }

        public Builder setNegativeButton(String text, View.OnClickListener listener){
            this.negativeText = text;
            this.negativeListener = listener;
            return this;
        }

        public BottomMessageDialog create(){
            return new BottomMessageDialog(this);
        }

        public void show(){
            this.create().show();
        }
    }

    public BottomMessageDialog(Builder builder){
        this.context = builder.context;
        this.title = builder.title;
        this.cancelable = builder.cancelable;
        this.theme = builder.theme;

        this.message = builder.message;
        this.positiveText = builder.positiveText;
        this.negativeText = builder.negativeText;
        this.positiveListener = builder.positiveListener;
        this.negativeListener = builder.negativeListener;

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

        ImageView ivTab = view.findViewById(R.id.img_tab);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        LinearLayout containerDefault = view.findViewById(R.id.container_default);
        TextView tvMessage = view.findViewById(R.id.tv_message);
        Button btnPositive = view.findViewById(R.id.btn_positive);
        Button btnNegative = view.findViewById(R.id.btn_negative);

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
                tvMessage.setTextColor(ContextCompat.getColor(context, R.color.text_message_material_dark));
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_title_material_dark));
                btnPositive.setTextColor(ContextCompat.getColor(context, R.color.text_positive_material_dark));
                btnNegative.setTextColor(ContextCompat.getColor(context, R.color.text_negative_material_dark));
                break;
            case LIGHT:
                containerParent.setBackgroundResource(R.drawable.drawable_dialog_bottom_background_light);
                ivTab.setBackgroundResource(R.drawable.drawable_share_material_tab_light);
                tvMessage.setTextColor(ContextCompat.getColor(context, R.color.text_message_material_light));
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_title_material_light));
                btnPositive.setTextColor(ContextCompat.getColor(context, R.color.text_positive_material_light));
                btnNegative.setTextColor(ContextCompat.getColor(context, R.color.text_positive_material_light));
                break;
        }

        containerParent.setOnTouchListener(touchListener);
        containerDefault.setVisibility(View.VISIBLE);
        ivTab.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(title)){
            tvTitle.setText("");
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(message)){
            tvMessage.setText("");
        }else {
            tvMessage.setText(message);
        }

        if (TextUtils.isEmpty(positiveText)){
            btnPositive.setVisibility(View.GONE);
        }else {
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setText(positiveText);
            if (positiveListener != null){
                btnPositive.setOnClickListener(positiveListener);
            }
        }

        if (TextUtils.isEmpty(negativeText)){
            btnNegative.setVisibility(View.GONE);
        }else {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setText(negativeText);
            if (negativeListener != null){
                btnNegative.setOnClickListener(negativeListener);
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

                Configuration configuration = context.getResources().getConfiguration();
                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    attributes.width = SystemUIUtils.getScreenHeight(window);
                }

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
