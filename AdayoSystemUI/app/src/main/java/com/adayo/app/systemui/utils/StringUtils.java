package com.adayo.app.systemui.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
    public static String stringReplace(String str, String target ,String replacement) {
        if(null == str){
            return null;
        }
        return str.replace(target, replacement);
    }

    public static String timeToString(long time, String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }
}
