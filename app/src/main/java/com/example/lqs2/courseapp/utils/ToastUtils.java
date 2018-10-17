package com.example.lqs2.courseapp.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * toast工具类
 *
 * @author lqs2
 */
public class ToastUtils {

    private static Toast mTextToast;

    /**
     * 显示简单的toast
     *
     * @param context 上下文
     * @param msg     消息
     * @param t       时长
     */
    public static void showToast(Context context, String msg, int t) {
        if (null == mTextToast) {
            mTextToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        mTextToast.setDuration(t);
        mTextToast.setText(msg);
        mTextToast.show();
    }

    /**
     * 非在主线程显示简单的toast
     *
     * @param context  上下文
     * @param activity 当前活动
     * @param msg      消息
     * @param t        时长
     */
    public static void showToastOnMain(Context context, Activity activity, String msg, int t) {
        activity.runOnUiThread(() -> {
            if (null == mTextToast) {
                mTextToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
            }
            mTextToast.setDuration(t);
            mTextToast.setText(msg);
            mTextToast.show();
        });
    }


    /**
     * 在主现场上显示连接错误信息
     *
     * @param context  上下文
     * @param activity 当前活动
     */
    public static void showConnectErrorOnMain(Context context, Activity activity) {
        showToastOnMain(context, activity, "连接错误", Toast.LENGTH_SHORT);
    }

}
