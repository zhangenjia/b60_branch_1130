package com.adayo.app.setting.skin;

import android.view.View;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class LanguageUtil {
    private static boolean isLanguage = true;

    private LanguageUtil() {
    }
    public static void setText(View view,int id){
        AAOP_HSkin.with(view)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT,id)
                .applyLanguage(false);

    }
}
