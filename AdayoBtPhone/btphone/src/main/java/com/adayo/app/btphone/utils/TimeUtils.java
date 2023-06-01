package com.adayo.app.btphone.utils;

import android.text.format.DateUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    public static String getMillisecoundToTime(long s) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        int a = (int) s / 1000 / 60;
        int b = (int) s / 1000 % 60;
        return decimalFormat.format(a) + ":" + decimalFormat.format(b);
    }


    public static boolean isYesterday(long timeStamp) {
        boolean ret = false;
        Calendar nowDate = Calendar.getInstance();
        Calendar oneDate = Calendar.getInstance();
        oneDate.setTime(new Date(timeStamp));
        if(nowDate.get(Calendar.YEAR) == oneDate.get(Calendar.YEAR)
        && nowDate.get(Calendar.DAY_OF_YEAR) == oneDate.get(Calendar.DAY_OF_YEAR) + 1) {
            ret = true;
        }
        return ret;
    }

    public static boolean isThisYear(long timeStamp) {
        boolean ret = false;
        Calendar nowDate = Calendar.getInstance();
        Calendar oneDate = Calendar.getInstance();
        oneDate.setTime(new Date(timeStamp));
        if(nowDate.get(Calendar.YEAR) == oneDate.get(Calendar.YEAR)) {
            ret = true;
        }
        return ret;
    }

    public static String getCallTime(long timeStamp) {
        String text = "";
        Date date = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat();

        if(DateUtils.isToday(timeStamp)) {
            format.applyPattern("HH :  mm");
        } else {
            format.applyPattern("yyyy / MM / dd");
        }
        text = format.format(date);
        return text;
    }
}
