package com.example.lqs2.courseapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;


/**
 * 申请权限工具类代码
 *
 * @author lqs2
 */
public class PermissionUtils {

    public static final int CODE_READ_EXTERNAL_STORAGE = 7;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 8;

    /**
     * 检查写文件权限
     *
     * @param context 上下文
     * @return 是否拥有此权限
     */
    public static boolean checkWriteExtraStoragePermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 检查读文件权限
     *
     * @param context 上下文
     * @return 是否拥有此权限
     */
    public static boolean checkReadExtraStoragePermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求写文件权限
     *
     * @param context  上下文
     * @param activity 当前活动
     * @param code     请求码
     */
    public static void requestWritePermission(Context context, Activity activity, int code) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, code);
        }
    }

    /**
     * 请求读文件权限
     *
     * @param context  上下文
     * @param activity 当前活动
     * @param code     请求码
     */
    public static void requestReadPermission(Context context, Activity activity, int code) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, code);
        }
    }

}

