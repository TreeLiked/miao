package com.example.lqs2.courseapp.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mTextToast;
    private static Toast mViewToast;

    public static void showToast(Context context, String msg, int t) {
        if (null == mTextToast) {
            mTextToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        mTextToast.setDuration(t);
        mTextToast.setText(msg);
        mTextToast.show();
    }

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


    public static void showConnectErrorOnMain(Context context, Activity activity) {
        showToastOnMain(context, activity, "连接错误", Toast.LENGTH_SHORT);
    }
    public static void inSubThreadShowToast() {

    }

    public static void showToast(Context context, View view) {
        if (null == mViewToast) {
            mViewToast = new Toast(context);
        }
        mViewToast.setDuration(Toast.LENGTH_SHORT);
        mViewToast.setView(view);
        mViewToast.show();
    }
}
