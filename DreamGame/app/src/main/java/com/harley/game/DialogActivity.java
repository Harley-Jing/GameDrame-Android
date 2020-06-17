package com.harley.game;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.harley.baselib.dialog.BaseDiaolg;
import com.harley.baselib.dialog.BottomDialog;
import com.harley.baselib.dialog.BottomMessageDialog;
import com.harley.baselib.dialog.DialogLifeCycleListener;
import com.harley.baselib.dialog.FullscreenDialog;
import com.harley.baselib.dialog.LoadingDialog;

import java.util.Timer;
import java.util.TimerTask;

public class DialogActivity extends AppCompatActivity {

    private Button mBtnLoading, mBtnFullscreen, mBtnBottomMessage, mBtnBottom, mBtnDefault;
    private LoadingDialog loadingDialog;
    private FullscreenDialog fullscreenDialog;
    private BottomMessageDialog bottomMessageDialog;
    private BottomDialog bottomDialog;
    private AlertDialog alertDialog;

    private Timer cancelTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        mBtnLoading = findViewById(R.id.btn_loading);
        mBtnFullscreen = findViewById(R.id.btn_fullscreen);
        mBtnBottomMessage = findViewById(R.id.btn_bottom_message);
        mBtnBottom = findViewById(R.id.btn_bottom);
        mBtnDefault = findViewById(R.id.btn_default);
        setOnClick();

        loadingDialog = new LoadingDialog.Builder(this).create();

        fullscreenDialog = new FullscreenDialog.Builder(this)
                .setCustomView(R.layout.layout_webview, new FullscreenDialog.OnBindView() {
                    @Override
                    public void onBind(FullscreenDialog dialog, View view) {
                        WebView webView = view.findViewById(R.id.wv_baidu);

                        WebSettings webSettings = webView.getSettings();
                        webSettings.setJavaScriptEnabled(true);
                        webSettings.setLoadWithOverviewMode(true);
                        webSettings.setUseWideViewPort(true);
                        webSettings.setSupportZoom(false);
                        webSettings.setAllowFileAccess(true);
                        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                        webSettings.setLoadsImagesAutomatically(true);
                        webSettings.setDefaultTextEncodingName("utf-8");

                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                            }
                        });

                        webView.loadUrl("https://m.baidu.com");


                    }
                }).setCancelable(true).create();

        bottomMessageDialog = new BottomMessageDialog.Builder(this)
                .setTitle("标题")
                .setMessage("这是一个消息对话框")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomMessageDialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomMessageDialog.dismiss();
                    }
                })
                .setDialogLifeCycleListener(new DialogLifeCycleListener() {
                    @Override
                    public void onCreate(Dialog dialog) {

                    }

                    @Override
                    public void onShow(Dialog dialog) {

                    }

                    @Override
                    public void onDismiss() {

                    }
                })
                .setTheme(BaseDiaolg.Theme.DARK)
                .create();

        bottomDialog = new BottomDialog.Builder(this).create();

        alertDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                .setTitle("标题")
                .setMessage("这是一个消息对话框")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        }).setCancelable(false).create();
    }

    private void setOnClick(){
        OnClick click = new OnClick();
        mBtnLoading.setOnClickListener(click);
        mBtnFullscreen.setOnClickListener(click);
        mBtnBottomMessage.setOnClickListener(click);
        mBtnBottom.setOnClickListener(click);
        mBtnDefault.setOnClickListener(click);
    }

    private class OnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case R.id.btn_loading:
                    loadingDialog.show();
                    delayDismiss(v.getId());
                    break;
                case R.id.btn_bottom_message:
                    bottomMessageDialog.show();
                    break;
                case R.id.btn_bottom:
                    bottomDialog.show();
                    break;
                case R.id.btn_fullscreen:
                    fullscreenDialog.show();
                    break;
                case R.id.btn_default:
                    alertDialog.show();
                    break;
            }
        }
    }

    private void delayDismiss(final int id) {

        if (cancelTimer != null) cancelTimer.cancel();
        cancelTimer = new Timer();
        cancelTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                autoDimiss(id);
            }
        }, 3000);

    }

    private void autoDimiss(int id){
        switch (id){
            case R.id.btn_loading:
                loadingDialog.dismiss();
                break;
            case R.id.btn_bottom_message:
                bottomMessageDialog.dismiss();
                break;
            case R.id.btn_fullscreen:
                fullscreenDialog.dismiss();
                break;
        }
    }
}