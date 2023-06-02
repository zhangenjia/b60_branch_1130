package com.adayo.app.launcher.radio.constant;

/**
 * demo中用到的便于演示的工具类，主要用于模拟记录和获取当前皮肤
 */
public class Utils {
    public static final String TAG = "RadioCar";

    //所有皮肤唯一标识符
    public static final String SKIN_IDENTIFIER_WHITE = "white";
    public static final String SKIN_IDENTIFIER_BLACK = "black";
    public static final String SKIN_IDENTIFIER_YELLOW = "yellow";

    //拓展的换肤属性名
    public static final String ATTR_JSON_IMG_VIEW = "lottie_rawRes_car";
    //模糊背景换肤属性
    public static final String ATTR_BLUR_IMAGE = "blurImage";

    //模拟当前系统应用的皮肤
    private static volatile String SKIN_CURRENT = SKIN_IDENTIFIER_WHITE;

    public synchronized static String getCurrentSkin() {
        return SKIN_CURRENT;
    }

    public synchronized static void setCurrentSkin(String identifier) {
        SKIN_CURRENT = identifier;
    }
}
