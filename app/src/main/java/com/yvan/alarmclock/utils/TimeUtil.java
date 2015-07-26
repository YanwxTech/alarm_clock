package com.yvan.alarmclock.utils;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Yvan on 2015/7/7.
 */
public class TimeUtil {
    public static final String ONLY_ONCE = "只响一次";
    public static final String EVERYDAY = "每天";
    public static final String WEEKDAY = "周一至周五";

    //public static final String CUSTOM_DAY="";
    public static String intToWeekday(final int i) {
        String result = "";
        switch (i) {
            case 0:
                result = "周一";
                break;
            case 1:
                result = "周二";
                break;
            case 2:
                result = "周三";
                break;
            case 3:
                result = "周四";
                break;
            case 4:
                result = "周五";
                break;
            case 5:
                result = "周六";
                break;
            case 6:
                result = "周日";
                break;
        }
        return result;
    }

    public static int weekdayToInt(String weekday) {
        int result = 0;
        if ("周一".equals(weekday)) {
            result = 0;
        } else if ("周二".equals(weekday)) {
            result = 1;
        } else if ("周三".equals(weekday)) {
            result = 2;
        } else if ("周四".equals(weekday)) {
            result = 3;
        } else if ("周五".equals(weekday)) {
            result = 4;
        } else if ("周六".equals(weekday)) {
            result = 5;
        } else if ("周日".equals(weekday)) {
            result = 6;
        }
        return result;
    }

    public static String getAfterTime(String time, String days) {
        Calendar calendar = Calendar.getInstance();
        int nowDay = (calendar.getTime().getDay() + 6) % 7;
        int nowHour = calendar.getTime().getHours();
        int nowMinute = calendar.getTime().getMinutes();
        Log.i("nowTime", "weekday:" + nowDay + ",hour:" + nowHour + ",minute:" + nowMinute);
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);
        int spaceHour = hour - nowHour;
        int spaceMinute = minute - nowMinute;
        int spaceDay = 0;
        if (days.equals(ONLY_ONCE) || days.equals(EVERYDAY)) {
            spaceDay = 0;
        } else if (days.equals(WEEKDAY)) {
            if (spaceHour < 0 || spaceMinute < 0 && spaceHour == 0) {
                if (nowDay == 5 || nowDay == 6) {
                    spaceDay = 6 - nowDay;
                } else {
                    spaceDay = 0;
                }
            } else {
                if (nowDay == 5 || nowDay == 6) {
                    spaceDay = 7 - nowDay;
                } else {
                    spaceDay = 0;
                }
            }
        } else {
            String[] str_days = days.split(" ");
            int length = str_days.length;
            int[] int_days = new int[length];

            for (int i = 0; i < length; i++) {
                int_days[i] = weekdayToInt(str_days[i]);
            }
            int lastedDayNum = 0;
            for (int i = 0; i < length; i++) {
                if (int_days[i] >= nowDay) {
                    lastedDayNum = i;
                    break;
                }
            }
            if (spaceHour < 0 || (spaceMinute < 0 && spaceHour == 0)) {
                if (lastedDayNum + 1 < length && int_days[lastedDayNum] - nowDay == 0) {
                    spaceDay = int_days[lastedDayNum + 1] - nowDay - 1;
                } else {
                    spaceDay = int_days[lastedDayNum] - nowDay - 1;
                }

            } else {
                spaceDay = int_days[lastedDayNum] - nowDay;
            }
        }
        if (spaceMinute < 0) {
            spaceMinute += 60;
            spaceHour--;
        }
        if (spaceHour < 0) {
            spaceHour += 24;
        }
        spaceDay = (spaceDay + 7) % 7;
        String result = "";
        if (spaceDay == 0) {
            result = String.format("%1$d个小时%2$d分钟后响铃", spaceHour, spaceMinute);
        } else {
            result = String.format("%1$d天%2$d个小时%3$d分钟后响铃", spaceDay, spaceHour, spaceMinute);
        }
        return result;
    }
}
