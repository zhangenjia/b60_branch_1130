package com.adayo.app.radio.ui.app;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.adayo.app.radio.attr.BlurImageAttrHandler;
import com.adayo.app.radio.attr.JsonAttrHandler;
import com.adayo.app.radio.utils.RadioAPPLog;
import com.adayo.app.radio.utils.Utils;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

/**
 * @author ADAYO-06
 */
public class RadioApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JsonAttrHandler jsonAttrHandler = new JsonAttrHandler();
        BlurImageAttrHandler blurImageAttrHandler = new BlurImageAttrHandler();
        AAOP_HSkinHelper.init(getApplicationContext(),true,"AdayoRadio");
        AAOP_HSkin.getInstance().registerSkinAttrHandler(Utils.ATTR_JSON_IMG_VIEW,jsonAttrHandler);
        AAOP_HSkin.getInstance().registerSkinAttrHandler(Utils.ATTR_BLUR_IMAGE,blurImageAttrHandler);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RadioAPPLog.i(RadioAPPLog.TAG, " onConfigurationChanged: newConfig = "+newConfig.getLocales().get(0));
        AAOP_HSkin.getInstance().refreshLanguage();
    }

}
