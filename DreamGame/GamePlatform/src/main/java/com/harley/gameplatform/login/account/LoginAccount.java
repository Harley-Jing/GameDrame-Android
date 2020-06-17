package com.harley.gameplatform.login.account;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginAccount {
    private String userId;
    private String token;
    private String sessionId;
    private int loginType;

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    private long loginTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String toString(){

        String account = "";
        try {
            JSONObject obj = new JSONObject();
            obj.put("userId", userId);
            obj.put("token", token);
            obj.put("sessionId", sessionId);
            obj.put("loginType", loginType);

            account = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return account;
    }
}
