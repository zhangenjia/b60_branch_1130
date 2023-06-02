package com.adayo.app.crossinf.util;

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
    public final static int FINISH_WADING_FUNCTION = 1001;
    public final static int START_WADING_FUNCTION = 1002;
    public final static int SWITCH_WADING_FRAGMENT = 1003;
    public static final String Versiondate = "20221020";
    public static final float TIREPRESSURECOEFFICIENT = 1.373f;//60can矩阵中固定的胎压系数(也可以从can上取到)
    public static final int SWITCHON = 1;//水深检测开关打开
    public static final int SWITCHOFF = 0;//水深检测开关关闭
    public static final String CROSSINF = "crossinf";//源管理接口map参数的key
    public static final String FINISH = "finish";//源管理接口map参数的key
    public static final String VEHICLEINFORMATION = "0";//车辆信息
    public static final String ENVIRONMENTALINFORMATION = "1";//环境信息
    public static final String WADINGDETECTION = "2";//涉水检测
    private boolean isRunning = false;//应用是否在运行
    private boolean isWadingRunning = false;//应用是否由涉水检测开关打开
    public static  final int INVALID = 15;
    public static final int FAULT = 14;
    public static final int LOCKREQUESTDENY = 4;
    public static final int DRIVINGMODESELECTERROR = 3;
    public static final int OVERSPEEDWARNING = 2;
    public static final int LOCKED = 1;
    public static final int UNLOCKED = 0;
    public static final int WADINGMOD = 24;
    public static  int CURRENTTHEME = 1;

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
