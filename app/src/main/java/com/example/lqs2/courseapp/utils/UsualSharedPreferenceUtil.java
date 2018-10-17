package com.example.lqs2.courseapp.utils;

import android.content.Context;

/**
 * 常用键值对存取
 *
 * @author lqs2
 */
public class UsualSharedPreferenceUtil {


    /**
     * 注销darkme登录
     *
     * @param c 上下文
     */
    public static void loginOutDarkMe(Context c) {
        SharedPreferenceUtil.put(c, "darkme_un", "");
        SharedPreferenceUtil.put(c, "darkme_pwd", "");
        SharedPreferenceUtil.put(c, "remember_password_darkme", false);
    }


    /**
     * 注销njit登录
     *
     * @param c 上下文
     */
    public static void loginOutNjit(Context c) {
        SharedPreferenceUtil.put(c, "xh", "");
        SharedPreferenceUtil.put(c, "hasGetCourse", "");
        SharedPreferenceUtil.put(c, "hasLoginJW", false);
        SharedPreferenceUtil.put(c, "hasGetCourse", false);
        SharedPreferenceUtil.put(c, "remember_password_jw", false);
        SharedPreferenceUtil.put(c, "hasLoginJW", false);
        SharedPreferenceUtil.put(c, "courseSourceCode", "");
    }

    /**
     * 获取darkme 账户
     *
     * @param c 上下文
     * @return 账户名
     */
    public static String getDarkmeAccount(Context c) {
        return (String) SharedPreferenceUtil.get(c, "darkme_un", "");
    }

    /**
     * 获取njit账户
     *
     * @param c 上下文
     * @return 学号
     */
    public static String getNjitAccount(Context c) {
        return (String) SharedPreferenceUtil.get(c, "xh", "");
    }

    /**
     * 是否开启了黑暗模式
     *
     * @param c 上下文
     * @return 打开/未打开
     */
    public static boolean isDarkModeOn(Context c) {
        return (boolean) SharedPreferenceUtil.get(c, "toggle_dark_mode", false);
    }


}
