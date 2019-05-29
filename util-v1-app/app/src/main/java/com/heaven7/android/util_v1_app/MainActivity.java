package com.heaven7.android.util_v1_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.heaven7.core.util.Logger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.d(TAG, "onCreate", "");
    }
}
