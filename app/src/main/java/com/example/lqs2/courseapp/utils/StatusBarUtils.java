package com.example.lqs2.courseapp.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;

import com.jaeger.library.StatusBarUtil;

/**
 * 状态栏辅助类
 *
 * @author lqs2
 */
public class StatusBarUtils {


    /**
     * 设置全透明
     *
     * @param activity 当前活动
     */
    public static void setStatusTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        android.app.ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * 状态栏字体颜色黑色，导航栏不透明
     *
     * @param activity 当前活动
     */
    public static void setStatusBarTransparentAndTextColorBlack(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        StatusBarUtil.setTranslucent(activity, 0);
    }

}
