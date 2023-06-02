package com.adayo.app.launcher.offroadinfo.util;

import java.lang.reflect.Method;

public class Constant {

    private static Constant mConstant;
    public static Constant getInstance() {
        if (null == mConstant) {
            synchronized (Constant.class) {
                if (null == mConstant) {
                    mConstant = new Constant();
                }
            }
        }
        return mConstant;
    }
    public static  int CURRENTTHEME = 1;
    public static final String Versiondate = "2021_9_6";
    public static final float TIREPRESSURECOEFFICIENT = 1.373f;//60can矩阵中固定的胎压系数(也可以从can上取到)
    public static final int SWITCHON = 1;//水深检测开关打开
    public static final int SWITCHOFF = 0;//水深检测开关关闭
    public static final String CROSSINF = "crossinf";//源管理接口map参数的key
    public static final String FINISH = "finish";//源管理接口map参数的key
    public static final String WADINGDETECTION = "wadingdetection";//涉水检测
    public static final String key = "key";//涉水检测
    private boolean isRunning = false;//应用是否在运行
    private boolean isWadingRunning = false;//应用是否由涉水检测开关打开
    public static final int INVALID = 15;
    public static final int FAULT = 14;
    public static final int LOCKREQUESTDENY = 4;
    public static final int DRIVINGMODESELECTERROR = 3;
    public static final int OVERSPEEDWARNING = 2;
    public static final int LOCKED = 1;
    public static final int UNLOCKED = 0;
    public static final int Error = 3;
    public static final int FOURL = 2;
    public static final int FOURH = 1;
    public static final int TWOWDORNORAWD = 0;
    public static String CURRENT_TAB = "2";
    public static final String VEHICLEINFORMATION = "1";
    public static final String ENVIRONMENTALINFORMATION = "2";
    public static int  IS_CONFIG = 1;
    public static int  NOT_CONFIG = 0;

    public void setAppIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean getAppIsRunning() {
        return isRunning;
    }

    public void setAppIsWadingRunning(boolean isWadingRunning) {
        this.isRunning = true;
        this.isWadingRunning = isWadingRunning;
    }

    public boolean getAppIsWadingRunning() {
        return isWadingRunning;
    }

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
