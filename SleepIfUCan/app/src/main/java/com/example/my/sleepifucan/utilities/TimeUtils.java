package com.example.my.sleepifucan.utilities;

import java.util.Calendar;

/**
 * Created by MY on 2017-01-23.
 */

public class TimeUtils {
    public static String[] DAY_STRING = {"일 ", "월 ", "화 ", "수 ", "목 ", "금 ", "토"};
    public static String[] COLORS = {"#000000", "#00AA00", "#888888"}; // black, green, gray.

    public static String buildTextColor(int day, boolean isOn) {
        String colorString = "";

        if(isOn) {
            for(String dayString : DAY_STRING) {
                colorString += "<font color=" + COLORS[day % 10] + ">" + dayString + "</font>";
                day /= 10;
            }
        } else {
            for(String dayString : DAY_STRING) {
                colorString += "<font color=" + COLORS[2] + ">" + dayString + "</font>";
            }
        }

        return colorString;
    }

    public static boolean isSettedDay(int day, int calDay) {
        int remain = 0;

        for(int i = 1; i <= calDay; i++) {
            remain = day % 10;
            day /= 10;
        }

        return remain == 1;
    }

    public static int transOffDay(int day, int calDay) {
        int tmp = (int) Math.pow(10, calDay - 1);
        return day - tmp;
    }

    public static Calendar getSetCalendar(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public static String getFormattedTime(int hourOfDay, int minute) {
        return String.format("%1$02d:%2$02d", hourOfDay, minute);
    }
}
