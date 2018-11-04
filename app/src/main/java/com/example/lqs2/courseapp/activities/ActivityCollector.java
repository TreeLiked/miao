package com.example.lqs2.courseapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 自定义所有活动的父类，便于管理
 *
 * @author lqs2
 */
public class ActivityCollector extends AppCompatActivity {

    /**
     * 用来保存所有的活动
     */
    public static List<Activity> activities = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    /**
     * 将一个新的活动添加到list中
     *
     * @param activity 所添加的活动
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 删除一个活动
     *
     * @param activity 删除的活动
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
        activity.finish();
    }

    /**
     * 结束所有的活动，用来退出程序
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            activity.finish();
        }
        activities.clear();
//        杀死当前程序的进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}
