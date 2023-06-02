package com.adayo.app.dvr.utils;

import android.util.Log;

/**
 * Created by Administrator on 2019/11/14 0014.
 */

public class FastClickCheck {
    private static final String TAG = "FastClickCheck";
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public static boolean isFastClick() {
        Log.d(TAG,"isFastClick");
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        Log.d(TAG,"curClickTime:" + curClickTime);
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
            Log.d(TAG,"flag = true");
            Log.d(TAG,"curClickTime:" + curClickTime + "   lastClickTime:" + lastClickTime);
        }
        lastClickTime = curClickTime;
        return flag;
    }
    public static boolean isFastClickTime(int minClickDelayTime) {
        Log.d(TAG,"isFastClick");
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        Log.d(TAG,"curClickTime:" + curClickTime);
        if ((curClickTime - lastClickTime) >= minClickDelayTime) {
            flag = true;
            Log.d(TAG,"flag = true");
            Log.d(TAG,"curClickTime:" + curClickTime + "   lastClickTime:" + lastClickTime);
        }
        lastClickTime = curClickTime;
        return flag;
    }
}
