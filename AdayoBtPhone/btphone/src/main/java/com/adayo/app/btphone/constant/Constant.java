package com.adayo.app.btphone.constant;

public class Constant {

    public static final String TAG = "Adayo_BT_APP_";
    public static final int LINK_MAN = 0;
    public static final int CALL_LOG = 1;

    public static final int CALL_TYPE_INCOMINT_TELEGRAM = 0;//单方通话-来电中
    public static final int CALL_TYPE_RING_UP = 1;//单方通话 去电
    public static final int CALL_TYPE_ON_THE_LINE = 2;//单方通话中
    public static final int CALL_TYPE_THIRD_INCOMING = 3;//三方来电-来电中
    public static final int CALL_TYPE_THIRD_CALLING = 4;//三方来电-通话中
    public static final int CALL_TYPE_END_CALL = 5;//结束通话
    public static final int CALL_TYPE_NONE = 0xFF;

    public static final int THIRD_STATUS_SPEAK = 0;   //保留当前通话，接听第三方来电后回调

    public static final int THIRD_STATUS_HOLD = 1;    //切换通话后回调

    public static final int THIRD_STATUS_DIAL = 2;
    public static final int THIRD_STATUS_GO = 3;

    public static final int THIRD_STATUS_COME_FOUR = 4;    //三方来电过程中回调
    public static final int THIRD_STATUS_COME = 5;    //三方来电过程中回调

    public static final int THIRD_STATUS_NONE = 0xFF;

    public static final int CALL_DIALOG_SHOW = 0;
    public static final int CALL_DIALOG_DISMISS = 1;
    public static int CALL_DIALOG_SHOW_STATE = CALL_DIALOG_DISMISS;

    public static final int CONN_STATE_BT_CONNECTED = 0;
    public static final int CONN_STATE_BT_NOT_CONNECTED = 1;
    public static final int CONN_STATE_PHONE_NOT_CONNECTED_AUDIO_CONNECTED = 2;
}
