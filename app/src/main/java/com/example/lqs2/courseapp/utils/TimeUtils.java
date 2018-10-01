package com.example.lqs2.courseapp.utils;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    private static SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat fmt1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static DateFormat fmt2 = new SimpleDateFormat("HH:mm");
    private static DateFormat fmt3 = new SimpleDateFormat("MM-dd HH:mm");


    public static String tweetPostTimeConvert(String timeStr) {
        if (!TextUtils.isEmpty(timeStr)) {
            StringBuilder builder = new StringBuilder();
            try {
                Date date1 = new Date();
                Date date2 = sdf0.parse(timeStr);
                int i = differentDays(date2, date1);
                if (i >= 365) {
                    builder.append(fmt1.format(date2));
                } else {
                    if (i == 1) {
                        builder.append("昨天 ").append(fmt2.format(date2));
                    } else if (i == 0) {
                        if (differentMinutes(date1, date2) > 1) {
                            builder.append("今天 ").append(fmt2.format(date2));
                        } else {
                            builder.append("刚刚");
                        }
                    } else {
                        builder.append(fmt3.format(date2));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.toString();
        } else {
            return "null";
        }
    }

    private static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

    private static long differentMinutes(Date date1, Date date2) {
        return (date1.getTime() - date2.getTime()) / 60000;
    }
}