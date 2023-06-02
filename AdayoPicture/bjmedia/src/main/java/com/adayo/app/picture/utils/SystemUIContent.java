package com.adayo.app.picture.utils;

public class SystemUIContent {
    public static final String TAG = "SYSTEMUI";
    public static final String PACKAGE_NAME = "com.adayo.app.systemui";

    public static final int RETRY_DELAYED = 1000;
    public static final int HVAC_DISMISS_DELAYED = 15000;
    public static final int HVAC_POPUP_DELAYED = 5000;
    public static final int VOLUME_POPUP_DELAYED = 3000;
    //type of ShareInfo
    public static final int SHARE_DATA_VOLUME_ID = 7;
    public static final int SHARE_DATA_USB_DEVICE_ID = 17;
    public static final int SHARE_DATA_TBOX_SHARE_DATA_ID = 73;
    public static final int SHARE_DATA_SOURCE_MANAGER_ID = 14;
    public static final int SHARE_DATA_BRIGHTNESS_ID = 10;
    public static final int BACK_CAR_SHARE_ID = 16;
    public static final int BT_CALL_SHARE_ID = 23;
    public static final int DVR_SHARE_ID = 74;
    public static final int SYSTEMUI_SHARE_ID = 87;
    public static final int BLUETOOTH_SHARE_ID = 27;
    public static final int INSTALL_APP_STATE = 106;

    //    public static final int SETTING_STREAM_TYPE_TBOX = 0;    //tbox音频流
    public static final int SETTING_STREAM_TYPE_MEDIA = 3;    //媒体音频流
    public static final int SETTING_STREAM_TYPE_PHONE = 6;    //电话音频流
    public static final int SETTING_STREAM_TYPE_TTS = 9;    //语音音频流
    public static final int SETTING_STREAM_TYPE_NAVI = 11;    //导航音频流
//    public static final int SETTING_STREAM_TYPE_RING = 2;    //铃声音频流

    public static final int SYSTEM_PHONE_FREE = 7;    //蓝牙通话空闲

    /**
     * 系统电源状态监听SystemUI模块id
     */
    public static final byte FUNCTION_ID = 0x08;

    //系统状态
    /**
     * 初始化状态，收到此状态可不做处理
     */
    public static final byte SYSTEM_STATUS_INITING = 0x00;
    /**
     * 预启动状态，声音停止，屏幕关闭，不响应用户操作，用户Acc on唤醒
     */
    public static final byte SYSTEM_STATUS_PRESTART = 0x01;
    /**
     * 假关机状态，声音停止，屏幕关闭，不响应用户操作，长按power键唤醒或者用户Acc on
     */
    public static final byte SYSTEM_STATUS_FAKE_SHUT_DOWN = 0x02;
    /**
     * 假关机中，收到此状态可不做处理
     */
    public static final byte SYSTEM_STATUS_FAKE_SHUT_DOWNINT = 0x03;
    /**
     * 正常开机，声音、屏幕、动作正常
     */
    public static final byte SYSTEM_STATUS_NORMAL = 0x04;
    /**
     * 待机状态，声音停止、屏幕显示待机时钟或者黑屏。响应倒车、空调、语言唤醒等操作
     */
    public static final byte SYSTEM_STATUS_POWER_OFF = 0x05;
    /**
     * 升级状态，声音停止不响应用户操作
     */
    public static final byte SYSTEM_STATUS_UPDATE = 0x06;
    /**
     * 准备状态，srcMng申请视频控制权，Camera申请倒车权，常时service给systemservice回复start OK
     */
    public static final byte SYSTEM_STATUS_READY = 0x07;

    /**
     * 屏幕状态
     */
    public static final int SCREEN_OFF = 1;
    public static final int POWER_OFF = 2;
    public static final int ACC_OFF = 3;

    public static final String ACTION_KEYEVENT_ACTION_UP = "adayo.keyEvent.onKeyUp";
    public static final String KEYEVENT_ACTION_LONGPRESS = "adayo.keyEvent.onKeyLongPress";
    public static final String ACTION_SCREEN = "com.adayo.tai.speechadapter.action.ACTION_SCREEN";
    public static final String ACTION_SHOW_HVAC_PANEL = "com.adayo.systemui.widget.ACTION_SHOW_HVAC_PANEL";
    public static final String ACTION_UPDATE_QS_PANEL = "com.adayo.systemui.widget.ACTION_SHOW_QS_PANEL";
    public static final String ACTION_SHOW_VOLUME_PANEL = "com.adayo.systemui.action.ACTION_SHOW_VOLUME_PANEL";
    public static final String ACTION_SHOW_ALL_APP_PANEL = "com.adayo.systemui.action.ACTION_SHOW_ALL_APP_PANEL";
    public static final String ACTION_MESSAGE = "com.adayo.systemui.ACTION_MESSAGE";
    public static final String ACTION_SYSTEM_THEME_CHANGE = "android.intent.action.system.THEME_CHANGED";

    public static final String MESSAGE_SP_NAME = "com.adayo.systemui.MESSAGE";
    public static final String MESSAGE_SP_KEY = "message";

    public static final String ACTION_START_WIDGET_SERVICE = "com.adayo.systemui.widget.EXAMPLE_APP_WIDGET_SERVICE";
//    public static final String ACTION_UPDATE_STATUS = "com.adayo.systemui.widget.UPDATE_STATUS";
//    public static final String ACTION_UPDATE_WIND_MODE  = "com.adayo.systemui.widget.UPDATE_WIND_MODE";
//    public static final String ACTION_UPDATE_LEFT_TEMP  = "com.adayo.systemui.widget.UPDATE_LEFT_TEMP";
//    public static final String ACTION_UPDATE_RIGHT_TEMP  = "com.adayo.systemui.widget.UPDATE_RIGHT_TEMP";
//    public static final String ACTION_UPDATE_WIND_VALUE  = "com.adayo.systemui.widget.UPDATE_WIND_VALUE";
//    public static final String ACTION_UPDATE_CYCLE_MODE  = "com.adayo.systemui.widget.UPDATE_CYCLE_MODE";
//    public static final String ACTION_UPDATE_FRONT_DEFROST  = "com.adayo.systemui.widget.UPDATE_FRONT_DEFROST";

    public static final int HVAC_POWER_OFF = 1;
    public static final int HVAC_POWER_ON = 2;
    public static final int HVAC_FACE_MODE = 1;
    public static final int HVAC_FACE_FOOT_MODE = 3;
    public static final int HVAC_FOOT_MODE = 2;
    public static final int HVAC_FOOT_WIND_MODE = 4;
    public static final int HVAC_WIND_MODE = 5;
    public static final float HVAC_MAX_TEMP = 32;
    public static final float HVAC_MIN_TEMP = 18;
    public static final int HVAC_EXTERNAL_TYPE = 1;
    public static final int HVAC_INTERNAL_TYPE = 2;
    public static final int HVAC_FRONT_OFF = 0;
    public static final int HVAC_FRONT_ON = 1;
    public static final int HVAC_WIND_MIN = 0;
    public static final int HVAC_WIND_MAX = 8;

    /**
     * card
     */
    public static final int BUTTON_OPEN_HVAC_PANEL = 1;
    public static final int CLICK_LEFT_TEMP_PLUS = 2;
    public static final int CLICK_LEFT_TEMP_SUB = 3;
    public static final int CLICK_RIGHT_TEMP_PLUS = 4;
    public static final int CLICK_RIGHT_TEMP_SUB = 5;
    public static final int CLICK_WIND_MODE = 6;
    public static final int CLICK_WIND_SUB = 7;
    public static final int CLICK_WIND_PLUS = 8;
    public static final int CLICK_WIND_ONE = 9;
    public static final int CLICK_WIND_TOW = 10;
    public static final int CLICK_WIND_THREE = 11;
    public static final int CLICK_WIND_FOUR = 12;
    public static final int CLICK_WIND_FIVE = 13;
    public static final int CLICK_WIND_SIX = 14;
    public static final int CLICK_WIND_SEVEN = 15;
    public static final int CLICK_WIND_EIGHT = 16;
}
