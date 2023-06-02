package com.adayo.app.launcher.navi.util;


import android.util.SparseIntArray;

import com.adayo.app.launcher.navi.R;

public class ConstantsUtil {

    public static final int SIXTYTIMES = 60;
    public static final int MYRIAMETER = 10000;
    public static final int ONEKILOMETER = 1000;
    public static final int TENTHOUSANDMETERS = 100000;
    public static final double ONEKILOMETER_AS_DOUBLE = 1000.0;
    public static final int RE_AUTONAVIINIT = 0x129;
    public static final String NAVI_TIME_INVALID = "xxxxxxxx";
    public static final String NAVI_ROAD_NAME_INVALID = "xxxxxxxx";
    public static final String NAVI_DISTANCE_INVALID = "xxxxxxxx";
    public static final int NAVI_REMAIND_TIME_INT_INVALID = -1;
    public static final int NAVI_REMAIND_DISTANCE_INT_INVALID = -1;
    public static final int NAVI_CUIDE_STATE_NOT_ACTIVE = 0;
    public static final int NAVI_CUIDE_STATE_ACTIVE = 1;
    public static final int TURN_TYPE_NULL = 0;
    public static final int TURN_TYPE_NONE = -1;

    public static final SparseIntArray TURN_ICO_ARR = new SparseIntArray() {
        {
            put(0, TURN_TYPE_NULL);
            put(1, R.drawable.map_ic_route_guide_turn_1);
            put(2, R.drawable.map_ic_route_guide_turn_2);
            put(3, R.drawable.map_ic_route_guide_turn_3);
            put(4, R.drawable.map_ic_route_guide_turn_4);
            put(5, R.drawable.map_ic_route_guide_turn_5);
            put(6, R.drawable.map_ic_route_guide_turn_6);
            put(7, R.drawable.map_ic_route_guide_turn_7);
            put(8, R.drawable.map_ic_route_guide_turn_8);
            put(9, TURN_TYPE_NONE);
            put(10, R.drawable.map_ic_route_guide_turn_10);
            put(11, R.drawable.map_ic_route_guide_turn_11);
            put(12, R.drawable.map_ic_route_guide_turn_12);
            put(13, R.drawable.map_ic_route_guide_turn_13);
            put(14, R.drawable.map_ic_route_guide_turn_14);
            put(15, R.drawable.map_ic_route_guide_turn_15);
            put(16, R.drawable.map_ic_route_guide_turn_16);
            put(17, R.drawable.map_ic_route_guide_turn_17);
            put(18, R.drawable.map_ic_route_guide_turn_18);
            put(19, TURN_TYPE_NONE);
            put(20, R.drawable.map_ic_route_guide_turn_20);
            put(21, R.drawable.map_ic_route_guide_turn_21);
            put(22, R.drawable.map_ic_route_guide_turn_22);
            put(23, R.drawable.map_ic_route_guide_turn_23);
            put(24, R.drawable.map_ic_route_guide_turn_24);
            put(25, R.drawable.map_ic_route_guide_turn_25);
            put(26, R.drawable.map_ic_route_guide_turn_26);
            put(27, R.drawable.map_ic_route_guide_turn_27);
            put(28, R.drawable.map_ic_route_guide_turn_28);
            put(29, TURN_TYPE_NONE);
            put(30, R.drawable.map_ic_route_guide_turn_30);
            put(31, R.drawable.map_ic_route_guide_turn_31);
            put(32, R.drawable.map_ic_route_guide_turn_32);
            put(33, R.drawable.map_ic_route_guide_turn_33);
            put(34, R.drawable.map_ic_route_guide_turn_34);
            put(35, R.drawable.map_ic_route_guide_turn_35);
            put(36, R.drawable.map_ic_route_guide_turn_36);
            put(37, R.drawable.map_ic_route_guide_turn_37);
            put(38, R.drawable.map_ic_route_guide_turn_38);
            put(39, TURN_TYPE_NONE);
            put(40, R.drawable.map_ic_route_guide_turn_40);
            put(41, R.drawable.map_ic_route_guide_turn_41);
            put(42, R.drawable.map_ic_route_guide_turn_42);
            put(43, R.drawable.map_ic_route_guide_turn_43);
            put(44, R.drawable.map_ic_route_guide_turn_44);
            put(45, R.drawable.map_ic_route_guide_turn_45);
            put(46, R.drawable.map_ic_route_guide_turn_46);
            put(47, R.drawable.map_ic_route_guide_turn_47);
            put(48, R.drawable.map_ic_route_guide_turn_48);
            put(49, TURN_TYPE_NONE);
            put(50, TURN_TYPE_NONE);
            put(51, R.drawable.map_ic_route_guide_turn_51);
            put(52, R.drawable.map_ic_route_guide_turn_52);
            put(53, R.drawable.map_ic_route_guide_turn_53);
            put(54, R.drawable.map_ic_route_guide_turn_54);
            put(55, R.drawable.map_ic_route_guide_turn_55);
            put(56, R.drawable.map_ic_route_guide_turn_56);
            put(57, R.drawable.map_ic_route_guide_turn_57);
            put(58, R.drawable.map_ic_route_guide_turn_58);
            put(59, R.drawable.map_ic_route_guide_turn_59);
            put(60, R.drawable.map_ic_route_guide_turn_60);
            put(61, R.drawable.map_ic_route_guide_turn_61);
            put(62, R.drawable.map_ic_route_guide_turn_62);
            put(63, R.drawable.map_ic_route_guide_turn_63);
            put(64, R.drawable.map_ic_route_guide_turn_64);
            put(65, R.drawable.map_ic_route_guide_turn_65);
            put(66, R.drawable.map_ic_route_guide_turn_66);
            put(67, TURN_TYPE_NONE);
            put(68, TURN_TYPE_NONE);
            put(69, TURN_TYPE_NONE);
            put(70, TURN_TYPE_NONE);
            put(71, TURN_TYPE_NONE);
            put(72, TURN_TYPE_NONE);
            put(73, TURN_TYPE_NONE);
            put(74, TURN_TYPE_NONE);
            put(75, TURN_TYPE_NONE);
            put(76, TURN_TYPE_NONE);
            put(77, TURN_TYPE_NONE);
            put(78, TURN_TYPE_NONE);
            put(79, TURN_TYPE_NONE);
            put(80, TURN_TYPE_NONE);
            put(81, R.drawable.map_ic_route_guide_turn_81);
            put(82, R.drawable.map_ic_route_guide_turn_82);
            put(83, R.drawable.map_ic_route_guide_turn_83);
            put(84, R.drawable.map_ic_route_guide_turn_84);
            put(85, R.drawable.map_ic_route_guide_turn_85);
            put(86, R.drawable.map_ic_route_guide_turn_86);
            put(87, R.drawable.map_ic_route_guide_turn_87);
            put(88, R.drawable.map_ic_route_guide_turn_88);
            put(89, R.drawable.map_ic_route_guide_turn_89);
            put(90, R.drawable.map_ic_route_guide_turn_90);
        }
    };






}
