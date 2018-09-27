package com.example.lqs2.courseapp.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.lqs2.courseapp.MyApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Tools {

    private static final float scale = MyApplication.getContext().getResources().getDisplayMetrics().density;

    private static final float scaledDensity = MyApplication.getContext().getResources().getDisplayMetrics().scaledDensity;

    /**
     * dp转成px
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转成dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        return (int) (pxValue / scale + 0.5f);
    }



//    public static int dip2px(Context context, float dipValue){
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int)(dipValue * scale + 0.5f);
//    }
//    public static int px2dip(Context context, float pxValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);
//    }

        public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static int getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                return 0;
        }
    }

    public static int getMonth() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.MONTH) + 1;
        return i;
    }

    public static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    public static int getCurrentMonthDay() {
        Calendar a = Calendar.getInstance();
        return a.get(Calendar.DAY_OF_MONTH);
    }


    public static int[] getTimeInterval(Date date) {
        int[] days = new int[7];
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // System.out.println("要计算日期为:" + sdf.format(cal.getTime())); // 输出要计算日期
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String imptimeBegin = sdf.format(cal.getTime());
        days[0] = Integer.parseInt(imptimeBegin);
        // System.out.println("所在周星期一的日期：" + imptimeBegin);
        cal.add(Calendar.DATE, 6);
        String imptimeEnd = sdf.format(cal.getTime());
        days[6] = Integer.parseInt(imptimeEnd);
        if (days[6] < days[0]) {
            for (int i = 1; i < days[6]; i++) {
                days[6 - i] = days[6] - i;
            }
            for (int i = 1; i < days.length; i++) {
                if (days[i] == 0) {
                    days[i] = days[0] + i;
                } else {
                    break;
                }
            }
        } else {
            for (int i = 0; i < 6; i++) {
                days[i + 1] = days[0] + i + 1;
            }
        }
        return days;
    }

    public static Bitmap getImageFromAssetsFile(Context context, String subDir, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(subDir + java.io.File.separator + fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                return BitmapFactory.decodeStream(am.open(subDir + File.separator + "default.png"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return image;
    }
}
