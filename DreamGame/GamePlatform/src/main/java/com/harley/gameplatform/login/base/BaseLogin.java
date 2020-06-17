package com.harley.gameplatform.login.base;

import android.content.Intent;

public abstract class BaseLogin {
    public abstract void login();
    public abstract void logout();
    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
    public abstract void verifyToken(String userId, String token);

    protected final String TAG = "Login";
}
