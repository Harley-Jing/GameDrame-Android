package com.harley.baselib.utils;

import android.content.Context;
import android.content.res.Resources;

public class ResourceUtils {
    private static String mPackageName;//接入游戏的包名
    private static Resources mResources;//接入游戏的包名

    public static void init(Context context) {
        mPackageName = context.getApplicationContext().getPackageName();
        mResources = context.getApplicationContext().getResources();
    }

    public static int getLayout(String resName) {
        return mResources.getIdentifier(resName, "layout", mPackageName);
    }

    public static int getDrawable(String resName) {
        return mResources.getIdentifier(resName, "drawable", mPackageName);
    }

    public static int getMipmap(String resName) {
        return mResources.getIdentifier(resName, "mipmap", mPackageName);
    }

    public static int getString(String resName) {
        return mResources.getIdentifier(resName, "string", mPackageName);
    }

    public static int getId(String resName) {
        return mResources.getIdentifier(resName, "id", mPackageName);
    }

    public static int getStyle(String resName) {
        return mResources.getIdentifier(resName, "style", mPackageName);
    }

    public static int getArray(String resName) {
        return mResources.getIdentifier(resName, "array", mPackageName);
    }

    public static int getColor(String resName) {
        return mResources.getIdentifier(resName, "color", mPackageName);
    }

    public static int getDimen(String resName) {
        return mResources.getIdentifier(resName, "dimen", mPackageName);
    }

    public static int getAnim(String resName) {
        return mResources.getIdentifier(resName, "anim", mPackageName);
    }

    public static int getStyleable(String resName) {
        return mResources.getIdentifier(resName, "styleable", mPackageName);
    }

    public static int[] getStyleableArray(String resName) {
        return getResourceIDsByName(resName, "styleable", mPackageName);
    }


    public static int getLayout(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "layout", packageName);
    }

    public static int getDrawable(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "drawable", packageName);
    }

    public static int getMipmap(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "mipmap", packageName);
    }

    public static int getString(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getApplicationContext().getResources();
        return resources.getIdentifier(resName, "string", packageName);
    }

    public static int getId(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getApplicationContext().getResources();
        return mResources.getIdentifier(resName, "id", packageName);
    }

    public static int getStyle(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "style", packageName);
    }

    public static int getArray(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "array", packageName);
    }

    public static int getColor(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "color", packageName);
    }

    public static int getDimen(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "dimen", packageName);
    }

    public static int getAnim(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "anim", packageName);
    }

    public static int getStyleable(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return resources.getIdentifier(resName, "styleable", packageName);
    }

    public static int[] getStyleableArray(Context context, String resName) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();
        return getResourceIDsByName(resName, "styleable", packageName);
    }
    /**
     * 利用反射，获取int数组格式的资源ID，例如styleable
     */
    private static int[] getResourceIDsByName(String resName, String resType, String packageName) {
        Class clsR = null;
        int[] ids = null;
        try {
            clsR = Class.forName(packageName + ".R");
            Class[] classes = clsR.getClasses();
            Class desClass = null;
            for (int i = 0; i < classes.length; i++) {
                String[] temp = classes[i].getName().split("\\$");
                if (temp.length >= 2) {
                    if (temp[1].equals(resType)) {
                        desClass = classes[i];
                        break;
                    }
                }
            }
            if (desClass != null) {
                ids = (int[]) desClass.getField(resName).get(resName);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
        return ids;
    }
}
