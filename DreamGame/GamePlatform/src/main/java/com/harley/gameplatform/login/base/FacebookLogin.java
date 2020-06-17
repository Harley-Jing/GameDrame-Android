package com.harley.gameplatform.login.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.harley.baselib.http.OkHttpUtils;
import com.harley.baselib.http.callback.OkStringCallback;
import com.harley.baselib.utils.LogUtils;

import java.util.Arrays;

public class FacebookLogin extends BaseLogin{

    private Context context;

    private CallbackManager callbackManager = null;

    public FacebookLogin(Context context){

        this.context = context;

        callbackManager = CallbackManager.Factory.create();
        FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                String userId = accessToken.getUserId();
                String token = accessToken.getToken();
                verifyToken(userId, token);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                LogUtils.d(TAG, "Facebook Login error: " + error.getMessage());
            }
        };
        LoginManager.getInstance().registerCallback(callbackManager, loginResultFacebookCallback);
    }

    public static void initialize(Context context){
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        AppEventsLogger.activateApp(context);
    }

    @Override
    public void login(){
        LoginManager.getInstance().logInWithReadPermissions((Activity)context, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void logout(){
        LoginManager.getInstance().logOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     * @param userd 150940753074775
     * @param token EAADkQUoLZCY8BAFH68ot2ShZCL0b4jMRFU28iBJ4ZBp06VIACziLjAo757eL1t3HXDsbCCKhYsURf9lYeH94LH7uCrczfVdZC0S0T2NU6UsMUa0KZAO5PPZB71icGzLwgJRwPX5i6Rfd60vd8dxZAHKavPxlrWMZB0I2UWvHsOIpMwWxVATeBP7v19zBi0CqKbbHeyFJG2fO9xiaWjs7FWqc9Is5ZBVDtRPoZD
     * {
     *      "data": {
     *                 "app_id": "250969066306959",
     *                 "type": "USER",
     *                 "application": "harleyGamePlatform",
     *                 "data_access_expires_at": 1599044766,
     *                 "expires_at": 1596430129,
     *                 "is_valid": true,
     *                 "issued_at": 1591246129,
     *                 "scopes": ["email", "public_profile"],
     *                 "user_id": "150940753074775"
     *             }
     * }
     */
     @Override
     public void verifyToken(final String userd, String token){
         //创建Facebook项目时,在后台可以获取
        String appId = "250969066306959";
        String appSecret = "47f6cf53c7d81dbc1e6831f82db2747c";

         OkHttpUtils.get()
                 .url("https://graph.facebook.com/debug_token")
                 .addParams("input_token", token)
                 .addParams("access_token", appId + "|" + appSecret)
                 .tag("facebook")
                 .build()
                 .execute(new OkStringCallback() {
                     @Override
                     public void onError(Exception e) {
                         LogUtils.e(TAG, "Facebook Http error: " + e.getMessage());

                     }

                     @Override
                     public void onResponse(String response) {
                         LogUtils.e(TAG, "Facebook Http success: " + response);
                     }
                 });
     }
}