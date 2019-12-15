package com.heaven7.android.util_v1_app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.heaven7.core.util.HighLightTextHelper;
import com.heaven7.core.util.MainWorker;

public class TestActivity extends AppCompatActivity {

    TextView mTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_test_entry);
        mTv = findViewById(R.id.tv);

        MainWorker.postDelay(1000, new Runnable() {
            @Override
            public void run() {
                testHighLightText();
               // startActivity(new Intent(TestActivity.this, MainActivity.class));
            }
        });
    }

    private void testHighLightText() {
        CharSequence text = new HighLightTextHelper.Builder()
                .setDefaultColor(Color.BLACK)
                .setHighLightColor(Color.RED)
                .setRawText("ba0aa1aaa2aaa3")
                .setHighLightText("a*")
                .setUseRegular(true)
                .build().getText();
        mTv.setText(text);
    }
}
