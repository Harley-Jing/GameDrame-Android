package com.harley.baselib.utils;

import android.util.Log;

public class LogUtils {
    public static boolean isDebug = true;

    private final static String APP_TAG = "perfectLog";

    private static String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(LogUtils.class.getName())) {
                    continue;
                }
                return "[ Thread:" + Thread.currentThread().getName() + ", at " + st.getClassName() + "." + st.getMethodName()
                        + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]";
            }
        }
        return null;
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, getMsgFormat(msg));
        }
    }


    public static void d(String msg) {
        if (isDebug) {
            Log.d(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, getMsgFormat(msg));
        }
    }


    public static void i(String msg) {
        if (isDebug) {
            Log.i(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, getMsgFormat(msg));
        }
    }



    public static void w(String msg) {
        if (isDebug) {
            Log.w(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, getMsgFormat(msg));
        }
    }


    public static void e(String msg) {
        if (isDebug) {
            Log.e(APP_TAG, getMsgFormat(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, getMsgFormat(msg));
        }
    }

    /**
     * 输出格式定义
     */
    private static String getMsgFormat(String msg) {
        return msg + " ;" + getFunctionName();
    }
}
