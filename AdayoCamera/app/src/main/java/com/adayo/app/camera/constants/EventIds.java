package com.adayo.app.camera.constants;

/**
 * @author Yiwen.Huan
 * created at 2021/8/11 13:51
 */
public class EventIds {
    private static final String TAG = "EventIds";

    public static final int RES_USE = -100;
    public static final int RES_REC_USE = -101;
    public static final int RES_TIMES = 9000;

    public static final int CAMERA_ERROR = 0;

    public static final int CAMERA_NORMAL = 1;

    public static final int AVM_DISPLAY = 11;
    public static final int AVM_HIDE = 12;

    public static final int AVM_CLICK_2D = 13;

    public static final int AVM_CLICK_3D = 14;

    public static final int AVM_CLICK_CHASSIS = 15;


    public static final int AVM_CLICK_MORE_ANGLE = 16;
    public static final int AVM_CLICK_MORE_ANGLE_1 = 17;
    public static final int AVM_CLICK_MORE_ANGLE_2 = 18;
    public static final int AVM_CLICK_MORE_ANGLE_3 = 19;
    public static final int AVM_CLICK_MORE_ANGLE_4 = 20;
    public static final int AVM_CLICK_MORE_ANGLE_5 = 21;

    public static final int AVM_HIDE_MORE_ANGLE = 22;

    public static final int AVM_CLICK_TRACK = 25;

    public static final int AVM_CLICK_SETTING = 26;

    public static final int AVM_HIDE_SETTING = 27;

    public static final int AVM_SET_LINE_BODY = 30;
    public static final int AVM_SET_LINE_TYRE = 31;

    public static final int AVM_SET_RADAR_2D = 32;
    public static final int AVM_SET_RADAR_3D = 33;

    public static final int AVM_SET_CAR_COLOR_WHITE = 34;
    public static final int AVM_SET_CAR_COLOR_GRAY = 35;
    public static final int AVM_SET_CAR_COLOR_BLACK = 36;

    public static final int AVM_SET_CAR_TRANSPARENCY_0 = 37;
    public static final int AVM_SET_CAR_TRANSPARENCY_25 = 38;
    public static final int AVM_SET_CAR_TRANSPARENCY_50 = 39;
    public static final int AVM_SET_CAR_TRANSPARENCY_75 = 40;
    public static final int AVM_SET_CAR_TRANSPARENCY_100 = 43;

    public static final int AVM_SET_TURN = 41;
    public static final int AVM_SET_RADAR = 42;

    public static final int UPGRADE_DISPLAY = 50;

    public static final int UPGRADE_HIDE = 51;

    public static final int UPGRADE_CLICK_UPGRADE = 52;

    public static final int UPGRADE_CLICK_UPGRADE_CONFIRM = 53;

    public static final int UPGRADE_ERROR_SIGNAL_DIED = 54;


    public static final int APA_DISPLAY_FROM_SIGNAL = 59;
    public static final int APA_DISPLAY = 60;
    public static final int APA_HIDE = 61;


    public static final int APA_CLICK_BERTH_IN = 62;
    public static final int APA_CLICK_BERTH_OUT = 63;
    public static final int APA_CLICK_CUSTOM_SPACE = 64;
    public static final int APA_CLICK_SETTING = 65;
    public static final int APA_HIDE_SETTING = 66;
    public static final int APA_CLICK_SETTING_CENTER = 67;
    public static final int APA_CLICK_SETTING_RIGHT = 68;

    public static final int APA_ST_REQUEST_0 = 70;
    public static final int APA_ST_REQUEST_1 = 71;
    public static final int APA_ST_REQUEST_2 = 72;
    public static final int APA_ST_REQUEST_3 = 73;
    public static final int APA_ST_REQUEST_4 = 74;
    public static final int APA_ST_REQUEST_5 = 75;
    public static final int APA_ST_REQUEST_6 = 76;
    public static final int APA_ST_REQUEST_7 = 77;
    public static final int APA_ST_REQUEST_8 = 78;
    public static final int APA_ST_REQUEST_9 = 79;
    public static final int APA_ST_REQUEST_10 = 80;
    public static final int APA_ST_REQUEST_11 = 81;
    public static final int APA_ST_REQUEST_12 = 82;
    public static final int APA_ST_REQUEST_13 = 83;
    public static final int APA_ST_REQUEST_14 = 84;
    public static final int APA_ST_REQUEST_15 = 85;
    public static final int APA_ST_REQUEST_16 = 87;
    public static final int APA_BERTHING_REMOTE = 86;

    public static final int APA_DIALOG_CONTROL_POSITIVE = 88;
    public static final int APA_DIALOG_CONTROL_NEGATIVE = 89;
    public static final int APA_HIDE_DIALOG = 90;
    public static final int APA_EXIT_POSITIVE = 91;
    public static final int APA_EXIT_NEGATIVE = 92;

    public static final int AVM_CHANGE_BRIGHT = 93;

    public static final int APA_CLICK_BERTH_IN_ON_KEY = 94;
    public static final int APA_CLICK_BERTH_IN_ON_REMOTE = 95;

    public static final int APA_CLICK_BERTH_OUT_LEFT = 96;
    public static final int APA_CLICK_BERTH_OUT_RIGHT = 97;

    public static final int APA_CLICK_BERTH_RECOVER_POSITIVE = 98;
    public static final int APA_CLICK_BERTH_RECOVER_NEGATIVE = 99;

    public static final int TRACK_DIALOG_CONTROL_POSITIVE = 100;
    public static final int TRACK_DIALOG_CONTROL_NEGATIVE = 101;
    public static final int TRACK_DIALOG_EXIT_POSITIVE = 102;
    public static final int TRACK_DIALOG_EXIT_NEGATIVE = 103;


    public static final int AVM_DISPLAY_FROM_SIGNAL = 105;
    public static final int AVM_NO_CONTROL_MORE_ANGLE = 106;

    public static final int SIGNAL_ERROR = 110;
    public static final int SIGNAL_RECOVER = 111;

    public static final int AVM_CLICK_ROCK = 120;
    public static final int AVM_CLICK_WALK = 121;

    public static final int LINE_CALIBRATION_FAILED_HIDE = 130;

    public static final int ROAD_CALIBRATION_ENTER_HINT = 131;
    public static final int ROAD_CALIBRATION_ENTER_HINT_CONFIRM = 132;
    public static final int CLICK_ROAD_CALIBRATION_ENTER_HINT_CONFIRM = 133;
    public static final int CLICK_ROAD_CALIBRATION_ENTER_HINT = 134;
    public static final int ROAD_CALIBRATION_DISPLAY = 135;
    public static final int CALIBRATION_SUCCESS = 136;

    public static final int ENABLE_FALSE = 140;
    public static final int ENABLE_RECOVER = 141;

    public static final int AVM_DISPLAY_WITH_ERROR = 150;
    public static final int APA_DISPLAY_WITH_ERROR = 151;

    public static final int RVC_RADAR_BEHIND_LEFT_ERROR = 160;
    public static final int RVC_RADAR_BEHIND_MIDDLE_LEFT_ERROR = 162;
    public static final int RVC_RADAR_BEHIND_MIDDLE_RIGHT_ERROR = 163;
    public static final int RVC_RADAR_BEHIND_RIGHT_ERROR = 164;
    public static final int RVC_RADAR_FRONT_LEFT_ERROR = 165;
    public static final int RVC_RADAR_FRONT_MIDDLE_LEFT_ERROR = 166;
    public static final int RVC_RADAR_FRONT_MIDDLE_RIGHT_ERROR = 167;
    public static final int RVC_RADAR_FRONT_RIGHT_ERROR = 168;

    public static final int RVC_RADAR_SYSTEM_ERROR = 170;
    public static final int RVC_RADAR_SYSTEM_LIMIT = 171;
    public static final int RVC_RADAR_FRONT_ERROR = 172;
    public static final int RVC_RADAR_BEHIND_ERROR = 173;
    public static final int RVC_RADAR_HINT_DISMISS = 174;

    public static final int AVM_HOME_TIPS_DISPLAY = 175;
    public static final int AVM_HOME_TIPS_HIDE = 176;

    public static final int APA_HOME_TIPS_DISPLAY = 177;
    public static final int APA_HOME_TIPS_HIDE = 178;

    public static final int AVM_RADAR_OPEN  = 179;
    public static final int AVM_RADAR_OFF  = 180;

    public static final int AVM_SET_CAR_COLOR_PALE_GREEN = 181;
    public static final int AVM_SET_CAR_COLOR_MISTY_BLUE = 182;
    public static final int AVM_SET_CAR_COLOR_SNOW_WHITE= 183;
    public static final int AVM_SET_CAR_COLOR_EXTREME_BLACK = 184;
    public static final int AVM_SET_CAR_COLOR_LAPP_BLUE = 185;
    public static final int AVM_SET_CAR_COLOR_JUNGLE_GREEN = 186;

    public static final int APA_CLICK_BERTH_OUT_VERTICAL_LEFT =187;
    public static final int APA_CLICK_BERTH_OUT_VERTICAL_RIGHT =188;
    public static final int APA_CLICK_BERTH_OUT_DOWN_LEFT =189;
    public static final int APA_CLICK_BERTH_OUT_DOWN_RIGHT =190;

    public static final int AVM_RADAR_OPEN_RADAR = 191;
    public static final int AVM_RADAR_OFF_RADAR = 192;

    public static final int APA_CLICK_BERTH_IN_FRONT_PARKING =193;
    public static final int APA_CLICK_BERTH_IN_REAR_PARKING =194;

    public static final int AVM_ROAD_CALIBRATION_HIDE = 195;
    public static final int CLICK_AVM_ROAD_CALIBRATION_ENTER_HINT = 196;
    public static final int AVM_CLICK_ROAD_CALIBRATION_HIDE = 197;
    public static final int AVM_FOR_ROAD_DISPLAY = 198 ;

    public static final int APA_SET_CAR_BRIGHTNESS_1= 201;
    public static final int APA_SET_CAR_BRIGHTNESS_2 = 202;
    public static final int APA_SET_CAR_BRIGHTNESS_3 = 203;
    public static final int APA_SET_CAR_BRIGHTNESS_4 = 204;
    public static final int APA_SET_CAR_BRIGHTNESS_5 = 205;
    public static final int APA_SET_CAR_BRIGHTNESS_6 = 206;
    public static final int APA_SET_CAR_BRIGHTNESS_7 = 207;
    public static final int APA_SET_CAR_BRIGHTNESS_8 = 208;
    public static final int APA_SET_CAR_BRIGHTNESS_9 = 209;
    public static final int APA_SET_CAR_BRIGHTNESS_10 = 210;

    public static final int AVM_EXIT_TRACKING = 211;
    public static final int AVM_EXIT_TRACKING_NEGATIVE = 212;
    public static final int AVM_EXIT_TRACKING_POSITIVE = 213;

    public static final int RVC_LINE_OFF = 214;
    public static final int RVC_LINE_ON = 215;

    public static final int ROAD_QUIT = 216;
    public static final int AVM_HIDE_EXIT_TRACKING = 217;

    public static final int APA_BTN_HIDE = 218;

}
