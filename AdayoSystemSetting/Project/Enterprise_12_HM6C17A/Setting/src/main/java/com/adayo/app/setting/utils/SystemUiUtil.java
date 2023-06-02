package com.adayo.app.setting.utils;

import android.app.Activity;
import android.view.View;



public class SystemUiUtil {

    private SystemUiUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void setVisibility(Activity activity, int... visibilities) {
        int visibility = View.SYSTEM_UI_FLAG_VISIBLE;
        for (int i : visibilities) {
            visibility |= i;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
    }
}