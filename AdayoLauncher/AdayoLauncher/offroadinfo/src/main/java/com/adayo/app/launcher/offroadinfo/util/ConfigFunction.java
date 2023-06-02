package com.adayo.app.launcher.offroadinfo.util;

import android.content.Context;
import android.util.Log;

import com.adayo.configurationinfo.ConfigurationWordInfo;

public class ConfigFunction {
    private static final String TAG = ConfigFunction.class.getSimpleName();
    private static ConfigFunction mConfigFunction;
    public static int  IS_CONFIG = 1;
    public static int  NOT_CONFIG = 0;
    private final ConfigurationWordInfo configurationWordInfo;
    private final int front_axle_lock;
    private final int rear_axle_lock;
    private final int wading_induction_sys;
    // byte 4 —— bit[6]前桥差速锁
    public static final String Front_Axle_Lock = "FrontAxleLock";
    // byte 4 —— bit[5]后桥差速锁
    public static final String Rear_Axle_Lock = "RearAxleLock";
    // byte 4 —— bit[4]涉水感应系统
    public static final String Wading_Induction_Sys = "WadingInductionSys";
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
        front_axle_lock = configurationWordInfo.getKey(Front_Axle_Lock);
        rear_axle_lock = configurationWordInfo.getKey(Rear_Axle_Lock);
        wading_induction_sys = configurationWordInfo.getKey(Wading_Induction_Sys);
        Log.d(TAG, "ConfigFunction: "+" front_axle_lock = "+front_axle_lock+" rear_axle_lock = "+rear_axle_lock+" wading_induction_sys = "+wading_induction_sys);
    }

    public int isConfigFront_Axle_Lock() {
        //前桥差速锁
        return front_axle_lock;
    }

    public int isConfigRear_Axle_Lock() {
        //后桥差速锁
        return rear_axle_lock;
    }

    public int isConfigWading_Induction_Sys() {
        //涉水感应系统
        return wading_induction_sys;
    }
}
