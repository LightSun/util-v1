package com.heaven7.core.util;

import android.view.View;

/**
 * A {@linkplain View.OnClickListener click listener} that debounces multiple clicks posted in the
 * same frame. A click on one button disables all buttons for that frame.
 */
public final class AvoidMultiClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long sLastClickTime = 0;

    private final View.OnClickListener mBase;

    private AvoidMultiClickListener(View.OnClickListener mBase) {
        this.mBase = mBase;
    }
    public static View.OnClickListener wrap(View.OnClickListener l){
        return new AvoidMultiClickListener(l);
    }
    @Override
    public final void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - sLastClickTime > MIN_CLICK_DELAY_TIME) {
            sLastClickTime = currentTime;
            mBase.onClick(v);
        }
    }
}
