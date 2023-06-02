package com.adayo.app.music.bt.btmusic.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String formatTime(long time) {
        if (time >= 60 * 60 * 1000) {
            long hours = time / (60 * 60 * 1000);
            StringBuilder sb = new StringBuilder();
            sb.append(hours);
            sb.append(":");
            sb.append(formatTime("mm:ss", (time - hours * 60 * 60 * 1000)));
            return sb.toString();
        } else {
            return formatTime("mm:ss", time);
        }
    }

    /**
     * 格式化时间 * @param format 格式化格式，基础格式为yyyy-MM-dd HH:mm:ss * @param currentTime * @return
     */
    public static String formatTime(String format, long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(time));
    }

}
