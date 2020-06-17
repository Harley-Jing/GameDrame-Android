package com.harley.baselib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.UUID;

public class UniqueIdUtils {
    private static final String KEY_DEVICE_ID = "DeviceId";

    public static String getUniqueId(Context context){
        if (context == null)
            return "";

        String uniqueId = uniqueId = (String) SharePreferenceUtils.get(KEY_DEVICE_ID, "");
        if (TextUtils.isEmpty(uniqueId)){
            uniqueId = getAndroidId(context);
            if (TextUtils.isEmpty(uniqueId)){
                uniqueId = getUUId();
            }
            SharePreferenceUtils.put(KEY_DEVICE_ID, uniqueId);
        }

        return Md5Utils.getStringMd5(uniqueId);
    }
    /**
     * 获取AndroidId
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        if (context == null) {
            return "";
        }

        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (androidId.length() < 11 || androidId.length() > 16){
            androidId = "";
        }

        return (TextUtils.isEmpty(androidId) ? "" : androidId);
    }

    /**
     * 得到全局唯一UUId
     */
    public static String getUUId() {
        return UUID.randomUUID().toString();
    }
}
