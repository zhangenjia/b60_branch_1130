package com.adayo.app.radio.utils;

import android.view.View;

import com.adayo.app.radio.R;

/**
 * @author ADAYO-06
 */
public class ViewUtil {
    private static long differernce = 300;
    /**
     * * 防止连续点击类,一共有两个防止连续点击处理
     *
     * @return true 连点 false 没有
     */
    public static boolean isDoubleClick(View view) {
        long nowTime = System.currentTimeMillis();
        Object value = view.getTag(R.id.tag_click_time);
        if (value != null) {
            long lastTime = (long) value;
            if (nowTime - lastTime >= differernce) {
                view.setTag(R.id.tag_click_time, nowTime);
                return false;
            } else {
                return true;
            }
        } else {
            view.setTag(R.id.tag_click_time, nowTime);
            return false;
        }
    }
}
