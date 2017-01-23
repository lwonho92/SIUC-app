package com.example.my.sleepifucan.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MY on 2017-01-23.
 */

public class StringUtils {
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
}
