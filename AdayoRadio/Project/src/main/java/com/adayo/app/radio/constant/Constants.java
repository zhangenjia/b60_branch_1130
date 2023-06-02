package com.adayo.app.radio.constant;

/**
 * @author jwqi
 */
public class Constants {
    public static final int FM_MIN_FREQ = 8750;
    public static final int FM_MAX_FREQ = 10800;
    public static final int AM_MIN_FREQ = 531;
    public static final int AM_MAX_FREQ = 1602;
    public static final int BAND_AM = 1;
    public static final int BAND_FM = 0;
    public static final int RADIO_CALLBACK_UPDATEVIEW = 101;
    public static final int RADIO_THREADGETLIST_UPDATEVIEW = 102;
    public static final int RADIO_THREADGETLIST_UPDATECOLLECTLIST = 103;
    public static final int RADIO_CALLBACK_UPDATEVIEW_AND_LIST = 104;
    public static final int RADIO_CLEAR_LIST = 105;
    public static final int RADIO_CLEAR_LIST_AND_SEARCH = 106;
    public static final int ISUNMUTE = 0;
    public static final int ISMUTE = 1;
    public static final int SEARCHTYPE_CURRENT_POSITION  = 1;
    /**
     * 从最小值开始搜索
     */
    public static final int SEARCHTYPE_MINIMUM  = 2;
    /**
     * 非搜索状态
     */
    public static final int RADIO_CALLBACK_SEARCHSTATUS_0 = 0;
    /**
     * 自动搜台
     */
    public static final int RADIO_CALLBACK_SEARCHSTATUS_1 = 1;
    /**
     * 浏览播放
     */
    public static final int RADIO_CALLBACK_SEARCHSTATUS_2 = 2;
    /**
     * seekUp
     */
    public static final int RADIO_CALLBACK_SEARCHSTATUS_3 = 3;

    /**
     * seekDown
     */
    public static final int RADIO_CALLBACK_SEARCHSTATUS_4 = 4;

    public static final String BAND_FM_TEXT = "FM";
    public static final String BAND_AM_TEXT = "AM";

    /**
     * 系统属性标识用户暂停
     */
    public static String SYSTEM_PROPERTY_SET_MCU_MUTE_KEY = "persist.adayo.AAOP_RadioService_setMcuMute";
    public static String RADIO_APP_ACTION = "com.adayo.app.radio";
    /**
     * 系统属性标识当前Radio焦点
     */
    public static String SYSTEM_PROPERTY_SET_RADIO_FOCUS_KEY = "persist.adayo.AAOP_RadioService_Focus";
}
