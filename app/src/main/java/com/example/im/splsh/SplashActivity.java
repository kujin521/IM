package com.example.im.splsh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import com.example.im.R;
import com.example.im.activity.LoginActivity;
import com.example.im.utils.ThreadUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splsh);
        //停留三秒到主界面
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
