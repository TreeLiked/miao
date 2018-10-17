package com.example.lqs2.courseapp.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * 测试一些东西
 *
 * @author lqs2
 */
public class Test {
    public static void main(String[] args) {

        Date weekMonday = getThisWeekMonday(new Date());
        Calendar now = Calendar.getInstance();
        System.out.println(now.get(Calendar.YEAR));
        System.out.println(now.get(Calendar.MONTH));
        System.out.println(now.get(Calendar.DAY_OF_MONTH));
        now.setTime(weekMonday);

        System.out.println(now.get(Calendar.YEAR));
        System.out.println(now.get(Calendar.MONTH));
        System.out.println(now.get(Calendar.DAY_OF_MONTH));

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
}
