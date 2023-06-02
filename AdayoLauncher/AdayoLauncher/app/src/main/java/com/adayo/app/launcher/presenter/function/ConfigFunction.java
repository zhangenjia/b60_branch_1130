package com.adayo.app.launcher.presenter.function;

import android.content.Context;
import android.util.Log;
import com.adayo.configurationinfo.ConfigurationWordInfo;
import static com.adayo.app.launcher.util.MyConstantsUtil.AppTAG;

public class ConfigFunction {

    private static final String TAG =  "ConfigFunction";
    private String carConfiguration;
    private final ConfigurationWordInfo configurationWordInfo;
    private static final int apaConfigured = 3;//
    private static final int cameraConfigured = 3;
    private static final int dvrConfigured= 1;//
    public static final int cardWith = 300;
    public static final int cardHeight = 322;
    private static ConfigFunction mConfigFunction;
    /**
     * 首次初始化在MainActivity中
     * @param context
     * @return
     */
    public static ConfigFunction getInstance(Context context) {
        if (null == mConfigFunction) {
            synchronized (ConfigFunction.class) {
                if (null == mConfigFunction) {
                    mConfigFunction = new ConfigFunction(context);
                }
            }
        }
        return mConfigFunction;
    }

    private ConfigFunction(Context context) {
        configurationWordInfo = ConfigurationWordInfo.getInstance();
        configurationWordInfo.init(context);
        carConfiguration = configurationWordInfo.getCarConfiguration();//判断高低配
    }

    /**
     * 是否配置avm
     */
    public boolean isAvmConfigured() {
        int cameraConfig = configurationWordInfo.getKey("CameraConfig");
        boolean b = cameraConfig == cameraConfigured ? true : false;
        Log.d(TAG, "IsAvmConfigured: " + b);
        return b;
//      return true;
    }

    /**
     * 是否配置apa
     */
    public boolean isApaConfigured() {
        int apaConfig = configurationWordInfo.getKey("CameraConfig");
        boolean b = apaConfig == apaConfigured ? true : false;
        Log.d(TAG, "IsAvmConfigured: " + b);
        isDvrConfigured();
        return b;
//      return true;
    }

    /**
     * 是否配置dvr
     */
    public boolean isDvrConfigured() {
        int dvrConfig = configurationWordInfo.getKey("HasDvrDevice");
        boolean b = dvrConfig == dvrConfigured ? true : false;
        Log.d(TAG, "isDvrConfigured: " + dvrConfig);
        return b;
//      return true;
    }

    /**
     * 判断高低配
     */
    public String getOffLineConfiguration() {
        Log.d(TAG, "getOffLineConfiguration: " + carConfiguration);
//        return "HM6C17A";
        return carConfiguration;
    }

}
