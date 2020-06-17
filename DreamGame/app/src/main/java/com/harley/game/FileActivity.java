package com.harley.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.harley.baselib.utils.FileUtils;

import java.io.File;
import java.util.List;

public class FileActivity extends AppCompatActivity {

    private Button mBtnSave, mBtnLoad, mBTnLoadLine;
    private TextView mTvMessage;
    private EditText mEtText;
    private String filepath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mBtnSave = findViewById(R.id.btn_save);
        mBtnLoad = findViewById(R.id.btn_load);
        mBTnLoadLine = findViewById(R.id.btn_load_line);
        mEtText = findViewById(R.id.et_text);
        mTvMessage = findViewById(R.id.tv_message);

        final File file = new File(getFilesDir(), "file.txt");
        final String filePath = file.getPath();
        FileUtils.deleteFiles(filePath);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = mEtText.getText().toString();
                mTvMessage.setText("存储到文件的内容：" + content);
                FileUtils.SaveStringToFile(filePath, content, true);
            }
        });

        mBtnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = FileUtils.LoadFileToString(filePath);
                mTvMessage.setText("读取文件的内容：" + content);
            }
        });

        mBTnLoadLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "";
                List<String> contentList = FileUtils.LoadFileToStringList(filePath);
                if (contentList != null){
                    int i = 1;
                    for (String str : contentList){
                        content = content + "第" + i++ + "行：" + str + "\r\n";
                    }
                }
                mTvMessage.setText("读取文件的内容：" + content);
            }
        });
    }
}