package com.adayo.app.systemui.utils;

import android.text.TextUtils;

import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIConfigs;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.configurationinfo.ConfigurationWordInfo;

public class ConfigUtils {
    private static ConfigurationWordInfo configurationWordInfo;

    public static boolean isLow(){
        if(null == configurationWordInfo){
            configurationWordInfo = ConfigurationWordInfo.getInstance();
            configurationWordInfo.init(SystemUIApplication.getSystemUIContext());
        }
        String carConfiguration = configurationWordInfo.getCarConfiguration();
        return !TextUtils.isEmpty(carConfiguration) && SystemUIConfigs.LOW.equals(carConfiguration);
    }

    public static int getKey(String keyCode){
        if(null == configurationWordInfo){
            configurationWordInfo = ConfigurationWordInfo.getInstance();
            configurationWordInfo.init(SystemUIApplication.getSystemUIContext());
        }
        LogUtil.debugD(SystemUIContent.TAG, "keyCode = " + keyCode + " ; configurationWordInfo.getKey(keyCode) = " + configurationWordInfo.getKey(keyCode));
        return configurationWordInfo.getKey(keyCode);
    }
}
