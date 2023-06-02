package com.adayo.app.systemui;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

import java.util.Locale;

public class SystemUIApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        AAOP_HSkinHelper
                .init(getApplicationContext(), "AdayoSystemUI");

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AAOP_HSkin.getInstance().refreshLanguage();
    }

    public static Context getSystemUIContext() {
        return context;
    }
}
