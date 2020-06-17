package com.harley.gameplatform.login.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.harley.baselib.http.OkHttpUtils;
import com.harley.baselib.http.callback.OkStringCallback;
import com.harley.baselib.utils.LogUtils;
import com.harley.baselib.utils.ResourceUtils;

class GoogleLogin extends BaseLogin {

    private Context context;
    private static final int RC_SIGN_IN = 9001;
    private final String KEY_WEB_CLIENT_ID = "google_login_web_client_id";
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    public GoogleLogin(Context context){
        this.context = context;

        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(ResourceUtils.getString(context, KEY_WEB_CLIENT_ID)))
                .requestEmail()
                .requestId()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        LogUtils.d(TAG, "google_login_web_client_id: " + context.getString(ResourceUtils.getString(context, KEY_WEB_CLIENT_ID)));
    }

    @Override
    public void login() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ((Activity)context).startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void logout() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity)context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    /**
     *
     * @param userId 115985119666157585508
     * @param token eyJhbGciOiJSUzI1NiIsImtpZCI6IjQ5MjcxMGE3ZmNkYjE1Mzk2MGNlMDFmNzYwNTIwYTMyYzg0NTVkZmYiLCJ0eXAiOiJKV1QifQ
     *
     *   {
     *       "iss": "https://accounts.google.com",
     *       "azp": "845353666140-s6v63av5kmviiuhnl71h8d6hu7obq0am.apps.googleusercontent.com",
     *       "aud": "845353666140-ultp1446926bp3aauvlp006vcnklh9v0.apps.googleusercontent.com",
     *       "sub": "115985119666157585508",
     *       "name": "harley jing",
     *       "picture": "https://lh4.googleusercontent.com/-b2-rwgnL3Vc/AAAAAAAAAAI/AAAAAAAAAAA/AMZuucn6QUIPWbwMA0RbS2olZhQgWu1gGQ/s96-c/photo.jpg",
     *       "given_name": "harley",
     *       "family_name": "jing",
     *       "locale": "zh-CN",
     *       "iat": "1591271461",
     *       "exp": "1591275061",
     *       "alg": "RS256",
     *       "kid": "492710a7fcdb153960ce01f760520a32c8455dff",
     *       "typ": "JWT"
     *     }
     */

    @Override
    public void verifyToken(String userId, String token) {
        OkHttpUtils.get()
                .url("https://oauth2.googleapis.com/tokeninfo")
                .addParams("id_token", token)
                .tag("google")
                .build()
                .execute(new OkStringCallback() {
                    @Override
                    public void onError(Exception e) {
                        LogUtils.e(TAG, "Google Http error: " + e.getMessage());

                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtils.d(TAG, "Google Http success: " + response);

                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String token = account.getIdToken();
                String userId = account.getId();
                verifyToken(userId, token);
            }

        } catch (ApiException e) {
            LogUtils.e(TAG, "Google sign in failed:" + e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}
