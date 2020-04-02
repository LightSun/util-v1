package com.heaven7.android.util_v1_app;

import com.heaven7.android.util_v1_app.sample.TestRoundActivity;

import java.util.List;

/**
 * this class help we test ui.
 * Created by heaven7 on 2017/3/24 0024.
 */

public class TestMainActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(MainActivity.class, "MainActivity"));
        list.add(new ActivityInfo(TestActivity.class, "TestActivity"));
        list.add(new ActivityInfo(TestRoundActivity.class, "TestRoundActivity"));
    }
}

