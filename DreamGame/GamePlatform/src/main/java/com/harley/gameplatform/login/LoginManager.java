package com.harley.gameplatform.login;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.harley.gameplatform.login.account.LoginAccount;

public class LoginManager {

    private Context mContext;
    private Handler mDelivery;

    private volatile static LoginManager mInstance;
    private LoginManager() {
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static LoginManager getInstance()
    {
        if (mInstance == null)
        {
            synchronized (LoginManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new LoginManager();
                }
            }
        }
        return mInstance;
    }

    public void login(){
        if (!hasLoggedIn()){

            return;
        }
    }

    private boolean hasLoggedIn(){
        return false;
    }



    public void onError(Exception e, int which){

    }

    public void onSuccess(LoginAccount account, int which){

    }
}
