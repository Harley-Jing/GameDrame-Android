package com.harley.gameplatform.login.callback;

import com.harley.gameplatform.login.account.LoginAccount;

public interface LoginResultCallback {
    public void onLogin(LoginAccount account);
}
