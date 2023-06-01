package com.adayo.app.camera;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.adayo.app.camera.skin.ImageNAttrHandler;
import com.adayo.app.camera.skin.SkinAttrs;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

import java.util.Locale;

public class AdayoCameraApplication extends Application {
    private static final String TAG = "AdayoCameraApplication";
    private String currLocale = "";


    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                SrcMngSwitchManager.getInstance().notifyAppFinished(AdayoSource.ADAYO_SOURCE_AVM, "com.adayo.app.camera");
                Log.e("AdayoCamera", TAG + " - uncaughtException: error", e);
                System.exit(0);
            }
        });
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        SrcMngSwitchManager.getInstance().notifyAppFinished(AdayoSource.ADAYO_SOURCE_AVM, "com.adayo.app.camera");
                        Log.e("AdayoCamera", TAG + " - Looper.loop(): error", e);
                        System.exit(0);
                    }
                }
            }
        });
        AAOP_HSkinHelper.init(getApplicationContext(), true, "AdayoCamera");
        AAOP_HSkin.getInstance().registerSkinAttrHandler(SkinAttrs.IMAGE_N, new ImageNAttrHandler());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Locale locale = newConfig.getLocales().get(0);
        if (currLocale.equals(locale.toString())) {
            Log.d("AdayoCamera", TAG + " - onConfigurationChanged: refreshLanguage failed because locale is not change , currLocale = " + currLocale + " , locale = " + locale.toString());
        } else {
            currLocale = locale.toString();
            AAOP_HSkin.getInstance().refreshLanguage();
        }
    }

    @Override
    public void onTerminate() {
        SrcMngSwitchManager.getInstance().notifyAppFinished(AdayoSource.ADAYO_SOURCE_AVM, "com.adayo.app.camera");
        Log.e("AdayoCamera", TAG + " - " + "onTerminate() called");
        super.onTerminate();
    }
}
