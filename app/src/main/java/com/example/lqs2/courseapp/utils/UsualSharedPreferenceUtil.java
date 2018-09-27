package com.example.lqs2.courseapp.utils;

import android.content.Context;

public class UsualSharedPreferenceUtil {


    public static void loginOutDarkMe(Context c) {
        SharedPreferenceUtil.put(c, "darkme_un", "");
        SharedPreferenceUtil.put(c, "darkme_pwd", "");
        SharedPreferenceUtil.put(c, "remember_password_darkme", false);
    }


    public static void loginOutNjit(Context c) {
        SharedPreferenceUtil.put(c, "xh", "");
        SharedPreferenceUtil.put(c, "hasGetCourse", "");
        SharedPreferenceUtil.put(c, "hasLoginJW", false);
        SharedPreferenceUtil.put(c, "hasGetCourse", false);
        SharedPreferenceUtil.put(c, "remember_password_jw", false);
        SharedPreferenceUtil.put(c, "hasLoginJW", false);
        SharedPreferenceUtil.put(c, "courseSourceCode", "");
    }

    public static String getDarkmeAccount(Context c) {

        return (String) SharedPreferenceUtil.get(c, "darkme_un", "");
    }

    public static String getNjitAccount(Context c) {
        return (String) SharedPreferenceUtil.get(c, "xh", "");
    }


    public static boolean isDarkModeOn(Context c) {
        return (boolean) SharedPreferenceUtil.get(c, "toggle_dark_mode", false);
    }


}
