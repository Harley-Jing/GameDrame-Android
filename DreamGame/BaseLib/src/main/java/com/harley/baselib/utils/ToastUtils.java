package com.harley.baselib.utils;

import android.content.Context;
import android.widget.Toast;

import com.harley.baselib.application.BaseApplication;


/**
 * 防止重复点击toast，一直显示未隐藏
 */

public class ToastUtils {

    private static final Context mContext = BaseApplication.getContext();
    public static boolean isDebug = true;
    /** 之前显示的内容 */
    private static String oldMsg ;
    /** Toast对象 */
    private static Toast toast = null ;
    /** 第一次时间 */
    private static long oneTime = 0 ;
    /** 第二次时间 */
    private static long twoTime = 0 ;

    /**
     * 显示Toast
     * @param message //0123456789012345678901234567
     */
    public static void showToast(String message){
        if(toast == null){
            toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            toast.show() ;
            oneTime = System.currentTimeMillis() ;
        }else{
            twoTime = System.currentTimeMillis() ;
            if(message.equals(oldMsg)){
                if(twoTime - oneTime > Toast.LENGTH_SHORT){
                    if (isDebug){
                        toast.show() ;
                    }
                }
            }else{
                oldMsg = message ;
                toast.setText(message) ;
                if (isDebug){
                    toast.show() ;
                }
            }
        }
        oneTime = twoTime ;
    }
}
