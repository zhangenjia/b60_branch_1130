package com.adayo.app.picture.utils;

import com.adayo.component.log.Trace;

public class TimeUtils {
    private static final String TAG = TimeUtils.class.getSimpleName();

    /**
     * 将秒转换为时分秒格式(00:00:00)的String，参数duration的单位是毫秒
     */
    public static String durationToHourtime(int duration) {
        //Trace.i(TAG, "[durationToHourtime] duration = " + duration);
        int durationTar = duration / 1000;
        int hour = (int) (durationTar / 3600);
        String showHour = hour < 10 ? "0" + hour : "" + hour;
        int min = (int) ((durationTar % 3600) / 60);
        String showMin = min < 10 ? "0" + min : "" + min;
        int sec = (int) (durationTar % 60);
        String showSec = sec < 10 ? "0" + sec : "" + sec;

        return showHour + ":" + showMin + ":" + showSec;
    }

    /**
     * 将秒转换为分秒格式(00:00)的String，参数duration的单位是毫秒
     */
    public static String drationToMintime(int duration) {
        Trace.i(TAG, "[drationToMintime] duration = " + duration);
        int durationTar = duration / 1000;

        int min = durationTar / 60;
        String showMin = min < 10 ? "0" + min : "" + min;
        int sec = durationTar % 60;
        String showSec = sec < 10 ? "0" + sec : "" + sec;

        return showMin + ":" + showSec;
    }

    /**
     * 根据时间自动转换成String，参数duration的单位是毫秒
     */
    public static String autoDurationToTimeString(int duration) {

        Trace.i(TAG, "demo [autoDurationToTimeString] duration = " + duration);
        if (duration >= 3600000) {
            return durationToHourtime(duration);
        } else {
            return drationToMintime(duration);
        }
    }

}
