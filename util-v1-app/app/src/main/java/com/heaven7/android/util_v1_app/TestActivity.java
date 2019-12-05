package com.heaven7.android.util_v1_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.heaven7.core.util.MainWorker;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainWorker.postDelay(1000, new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(TestActivity.this, MainActivity.class));
            }
        });
    }
}
