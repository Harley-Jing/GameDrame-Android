package com.harley.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harley.baselib.click.RepeatClickListener;
import com.harley.baselib.dialog.LoadingDialog;
import com.harley.baselib.http.OkHttpUtils;
import com.harley.baselib.http.callback.OkDownloadCallback;
import com.harley.baselib.http.callback.OkStringCallback;
import com.harley.baselib.http.callback.OkUploadCallback;
import com.harley.baselib.utils.ToastUtils;

import java.io.File;

public class HttpActivity extends AppCompatActivity {

    private Button mBtnGet, mBtnPost, mBtnClean, mBtnDownload, mBtnPause, mBtnUpload;
    private TextView mTvMessage;
    private ProgressBar mPrograssBar;
    private File file;

    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_http);

        mPrograssBar = findViewById(R.id.pb_progress);
        mPrograssBar.setProgress(0);

        mBtnGet = findViewById(R.id.btn_get);
        mBtnPost = findViewById(R.id.btn_post);
        mBtnClean = findViewById(R.id.btn_clean);
        mTvMessage = findViewById(R.id.tv_message);
        mBtnDownload = findViewById(R.id.btn_download);
        mBtnPause = findViewById(R.id.btn_pause);
        mBtnUpload = findViewById(R.id.btn_upload);

        setClick();

        loadingDialog = new LoadingDialog.Builder(this).create();
    }

    private void setClick(){
        OnClick click = new OnClick();
        mBtnGet.setOnClickListener(click);
        mBtnPost.setOnClickListener(click);
        mBtnClean.setOnClickListener(click);
        mBtnDownload.setOnClickListener(click);
        mBtnPause.setOnClickListener(click);
        mBtnUpload.setOnClickListener(click);
    }

    private void getHttp(){
        OkHttpUtils.get().url("https://arkalist-kr.ulugame.com/fullServiceMange/getSign.action")
                .addParams("secretId", "6e8a9dd05c15329")
                .addParams("type", "2")
                .tag("GET")
                .build()
                .execute(new OkStringCallback() {
                    public void onBefore() {
                        loadingDialog.show();
                    }

                    public void onAfter() {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        mTvMessage.setText("失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        mTvMessage.setText("成功：" + response);
                    }
                });
    }

    private void postHttp(){
        OkHttpUtils.postString()
                .url("https://arkalist-kr.ulugame.com/fullServiceMange/pullAcountInfo.action")
                .content("{\"userCode\":\"1234567\"}")
                .tag("POST")
                .mediaType("application/json;charset=utf-8")
                .build()
                .execute(new OkStringCallback() {

                    public void onBefore() {
                        loadingDialog.show();
                    }

                    public void onAfter() {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        mTvMessage.setText("失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        mTvMessage.setText("成功：" + response);
                    }
                });
    }

    private void downloadHttp(){
        OkHttpUtils.download()
                .url("https://imtt.dd.qq.com/16891/apk/06AB1F5B0A51BEFD859B2B0D6B9ED9D9.apk?fsname=com.tencent.mobileqq_8.1.0_1232.apk&csr=1bbd")
                .path(HttpActivity.this.getCacheDir().getPath())
                .tag("download_qq")
                .build()
                .execute(new OkDownloadCallback() {
                    @Override
                    public boolean isRange() {
                        return true;
                    }

                    @Override
                    public void pause(float progress, long total) {
                        mTvMessage.setText("暂停：" + "Progress：" + progress + ", total: " + total);
                    }

                    @Override
                    public void inProgress(float progress, long total, long speed) {
                        mPrograssBar.setProgress((int)progress);
                        mTvMessage.setText("Progress：" + progress + ", total: " + total + ", speed: " + speed);
                    }

                    @Override
                    public void onError(Exception e) {
                        mTvMessage.setText("失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(File response) {
                        file = response;
                        mPrograssBar.setProgress(100);
                        mTvMessage.setText("成功：path: " + response.getPath() + ", name: " + response.getName());
                    }
                });
    }

    private void pauseHttp(){
        OkHttpUtils.getInstance().cancelOkhttpTag("download_qq");
    }

    private void uploadHttp(){

        OkHttpUtils.upload().url("http://192.168.0.34:8081/centralOperations/fileUpload.action")
                .tag("Upload_qq")
                .addFile("file", file.getName(), file)
                .build()
                .execute(new OkUploadCallback() {
                    @Override
                    public void inProgress(float progress, long total, long speed) {
                        mPrograssBar.setProgress((int)progress);
                        mTvMessage.setText("Progress：" + progress + ", total: " + total + ", speed: " + speed);
                    }

                    @Override
                    public void onError(Exception e) {
                        mTvMessage.setText("失败：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        mPrograssBar.setProgress(100);
                        mTvMessage.setText("成功：" + response);
                    }
                });
    }

    private class OnClick extends RepeatClickListener {

        @Override
        public void onResultClick(View v) {
            switch (v.getId()){
                case R.id.btn_get: {
                    getHttp();
                }
                break;
                case R.id.btn_post: {
                    postHttp();
                }
                break;
                case R.id.btn_download: {
                    downloadHttp();
                }
                break;
                case R.id.btn_pause: {
                    pauseHttp();
                }
                break;
                case R.id.btn_upload: {
                    uploadHttp();
                }
                break;
                case R.id.btn_clean: {
                    mTvMessage.setText("");
                    if (file != null && file.exists()){
                        file.delete();
                    }
                }
                break;
            }
        }

        @Override
        public void onNoNetworkClick(View v){
            ToastUtils.showToast("网络异常");
        }
    }
}