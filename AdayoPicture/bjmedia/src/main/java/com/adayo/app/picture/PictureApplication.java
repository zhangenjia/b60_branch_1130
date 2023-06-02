package com.adayo.app.picture;


import com.adayo.app.base.BaseApplication;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

public class PictureApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AAOP_HSkinHelper
                .init(getApplicationContext(), "AdayoPicture");

    }
}
