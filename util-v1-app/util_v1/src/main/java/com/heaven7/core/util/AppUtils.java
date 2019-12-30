package com.heaven7.core.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * the app utils
 * @since 1.1.7
 */
public final class AppUtils {
    /**
     * set the status bar
     * @param window the window
     * @param color the status bar color.
     */
    public static void setStatusBar(Window window, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0+
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | window.getAttributes().flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            miTrans(window, true);
        }
        String brand = Build.BRAND.trim().toUpperCase();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && brand.equals("XIAOMI")) {
            if (ColorUtils.calculateLuminance(color) >= 0.5) {
                miTrans(window, true);
            } else {
                miTrans(window, false);
            }
        }
    }
    public static void fitStatusBarTextColor(Window window, boolean blackFlag) {
        if (blackFlag) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
                String brand = Build.BRAND.trim().toUpperCase();
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && brand.equals("XIAOMI")) {
                    miTrans(window, true);
                } else {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } else {
                miTrans(window, true);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
    }
    public static void setStatusBar(Window window) {
        setStatusBar(window, Color.WHITE);
    }
    public static void fitStatusBarHeight(Activity activity) {
        new AndroidBug5497Workaround(activity);
    }
    private static void miTrans(Window window, boolean blackFlag) {
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (blackFlag) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static class AndroidBug5497Workaround {
        private View mChildOfContent;
        private int mChildPreviousVisibleHeight;
        private int contentHeight;
        private boolean first = true;
        private int statusBarHeight;

        private AndroidBug5497Workaround(Activity activity) {
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
            FrameLayout content = activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);

            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    if (first) {
                        contentHeight = mChildOfContent.getHeight();//compat huawei
                        first = false;
                    }
                    processContentHeight();
                }
            });
        }
        private void processContentHeight() {
            int childVisibleHeight = computeUsableHeight();

            if (childVisibleHeight != mChildPreviousVisibleHeight) {
                //
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
                int rootHeight = mChildOfContent.getRootView().getHeight();
                int heightDifference = rootHeight - childVisibleHeight;
                if (heightDifference > (rootHeight / 4)) {
                    // keyboard probably just became visible
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        //frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                        lp.height = rootHeight - heightDifference + statusBarHeight;
                    } else {
                        lp.height = rootHeight - heightDifference;
                    }
                } else {
                    lp.height = contentHeight;
                }
                mChildOfContent.requestLayout();
                mChildPreviousVisibleHeight = childVisibleHeight;
            }
        }
        //visible height of mChildOfContent
        private int computeUsableHeight() {
            Rect r = new Rect();
            mChildOfContent.getWindowVisibleDisplayFrame(r);
            return (r.bottom - r.top);
        }
    }
}
