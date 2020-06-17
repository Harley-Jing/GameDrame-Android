package com.harley.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBtnHttp, mBtnFile, mBtnLogin, mBtnDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnHttp = findViewById(R.id.btn_http);
        mBtnFile = findViewById(R.id.btn_file);
        mBtnDialog = findViewById(R.id.btn_dialog);
        mBtnLogin = findViewById(R.id.btn_login);
        setOnClick();
    }

    private void setOnClick(){
        OnClick click = new OnClick();
        mBtnHttp.setOnClickListener(click);
        mBtnFile.setOnClickListener(click);
        mBtnDialog.setOnClickListener(click);
        mBtnLogin.setOnClickListener(click);
    }

    private class OnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()){
                case R.id.btn_http:
                    intent = new Intent(MainActivity.this, HttpActivity.class);
                    break;
                case R.id.btn_file:
                    intent = new Intent(MainActivity.this, FileActivity.class);
                    break;
                case R.id.btn_dialog:
                    intent = new Intent(MainActivity.this, DialogActivity.class);
                    break;
                case R.id.btn_login:
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    break;
            }
            startActivity(intent);
        }
    }
}