package com.adayo.app.ats.function;

import android.content.Context;
import android.util.Log;

import com.adayo.configurationinfo.ConfigurationWordInfo;

public class ConfigFunction {

    private static ConfigFunction mConfigFunction;
    private static final String TAG = "ConfigFunction";
    private String carConfiguration;
    private final ConfigurationWordInfo configurationWordInfo;
    public static final String WADING_INDUCTION_SYS = "WadingInductionSys";
    private final int wading_induction_sys;

    /**
     * 首次初始化在MainActivity中
     *
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
        wading_induction_sys = configurationWordInfo.getKey(WADING_INDUCTION_SYS);
    }


    /**
     * 判断高低配
     */

    public boolean isConfigWadingInductionSys() {
        //涉水感应系统
        if (wading_induction_sys == 1) {
            return true;
        } else {
            return false;
        }
    }
}
