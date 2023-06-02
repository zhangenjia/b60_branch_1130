package com.adayo.app.launcher.radio.constant;

public class Constants {
    public static final int FM_MIN_FREQ = 8750;
    public static final int FM_MAX_FREQ = 10800;
    public static final int AM_MIN_FREQ = 531;
    public static final int AM_MAX_FREQ = 1602;
    public static final int BAND_AM = 1;
    public static final int BAND_FM = 0;
    public static final int RADIO_CALLBACK_UPDATEVIEW = 101;
    public static final int RADIO_UPDATEVIEW = 103;
    public static final int PLAY_ANIMATION = 102;
    public static final int isUnMute = 0;
    public static final int isMute = 1;

    public static final String BAND_FM_TEXT = "FM";
    public static final String BAND_AM_TEXT = "AM";

    //系统属性标识是否抢过焦点
    public static String SYSTEM_PROPERTY_REQUEST_AUDIO_FOCUS_KEY = "AAOP_RadioService_requestAudioFocus_static";
}
