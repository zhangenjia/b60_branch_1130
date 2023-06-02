package com.adayo.app.video;

import android.app.Application;

import com.adayo.app.video.player.PlayerManager;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AAOP_HSkinHelper.init(getApplicationContext(),
                              true,
                              true,
                              "AdayoVideo");
        PlayerManager.init();
    }
}
