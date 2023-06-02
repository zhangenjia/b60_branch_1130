package com.adayo.app.setting.utils;

import android.content.Context;


public class UICommonUtils {
    public static int dpToPixel(Context context, float dipValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
