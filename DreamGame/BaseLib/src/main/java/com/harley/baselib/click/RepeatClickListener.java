package com.harley.baselib.click;

import android.view.View;

import com.harley.baselib.utils.NetWorkUtils;

public abstract class RepeatClickListener implements View.OnClickListener {
    // 防止快速点击默认等待时长为600ms
    private long DELAY_TIME = 600;
    private static long lastClickTime;

    @Override
    public void onClick(View v) {
        // 判断当前点击事件与前一次点击事件时间间隔是否小于阙值
        if (isFastDoubleClick()) {
            return;
        }

        boolean isNetworkOk = NetWorkUtils.isNetworkConnected(v.getContext());

        if (!isNetworkOk) {
            onNoNetworkClick(v);
            return;
        }

        onResultClick(v);
    }

    /**
     * 点击事件回调方法
     */
    public abstract void onResultClick(View v);

    /**
     * 点击事件--没有网络
     */
    public abstract void onNoNetworkClick(View v);

    /**
     * 设置默认快速点击事件时间间隔
     */
    public void setLastClickTime(long delay_time) {
        this.DELAY_TIME = delay_time;
    }

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < DELAY_TIME) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
