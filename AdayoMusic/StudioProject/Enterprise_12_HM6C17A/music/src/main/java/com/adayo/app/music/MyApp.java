package com.adayo.app.music;

import android.app.Application;

import com.adayo.app.music.player.PlayerManager;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AAOP_HSkinHelper.init(getApplicationContext(),
                              true,
                              true,
                              "AdayoMusic");
        PlayerManager.init();
    }
}
