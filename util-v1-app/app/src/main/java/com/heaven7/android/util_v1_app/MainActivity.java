package com.heaven7.android.util_v1_app;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    PermissionHelper mHelper = new PermissionHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.d(TAG, "onCreate", "");

        mHelper.startRequestPermission(Manifest.permission.CALL_PHONE, 1, new PermissionHelper.ICallback() {
            @Override
            public void onRequestPermissionResult(String requestPermission, int requestCode, boolean success) {
                Logger.d("MainActivity", "onRequestPermissionResult", "success = " + success);
            }
            @Override
            public boolean handlePermissionHadRefused(String requestPermission, int requestCode, Runnable task) {
                Logger.d("MainActivity", "handlePermissionHadRefused", "requestPermission = " + requestPermission);
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
