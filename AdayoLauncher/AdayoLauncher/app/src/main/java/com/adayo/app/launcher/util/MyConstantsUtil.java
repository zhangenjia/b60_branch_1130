package com.adayo.app.launcher.util;

import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_BT_AUDIO;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_USB;
import android.content.Context;
import com.adayo.app.launcher.R;

public class MyConstantsUtil {
    private Context context;
    public MyConstantsUtil(Context context) {
        this.context = context;
    }
    public static final String AppTAG = "2022_9_21";
    public static final String HIGH_CONFIG_VEHICLE = "HM6C17A";//高配车
    public static final String LOW_CONFIG_VEHICLE = "HM6C18A";//低配车
    public static final String LAUNCHER_ALLAPPCARD_KEY = "persist.allappidmap3";//allapp
    public static final String LAUNCHER_BOTTOM_BIGCARD_KEY = "persist.bottombigcardidmap3";//bottombigcard
    public static final String LAUNCHER_BOTTOM_SMALLCARD_KEY = "persist.bottomsmallcardmap3";//bottomsmallcard
    public static final String LAUNCHER_BIGCARD_KEY = "persist.bigcardidmap3";//bigcard
    public static final String LAUNCHER_SMALLCARD_KEY = "persist.smallcardidmap3";//smallcard
    public static final String VEHICLECONFIGUREDKEY = "persist.vehicleconfiguredkey3";
    public static final String ID_VIDEO = "a";
    public static final String ID_TEL = "b";
    public static final String ID_PICTURE = "c";
    public static final String ID_SETTING = "d";
    public static final String ID_MYCAR = "e";
    public static final String ID_DVR = "f";
    public static final String ID_MUSIC = "g";
    public static final String ID_NAVI = "i";
    public static final String ID_AVM = "j";
    public static final String ID_OFFROADINFO = "k";
    public static final String ID_APA = "l";
    public static final String ID_RADIO = "m";
    public static final String ID_YUEYEQUAN = "n";
    public static final String ID_WITHTENCENT = "o";
    public static final String ID_AIQUTING = "p";
    public static final String ID_WECHAT = "q";
    public static final String ID_HAVC = "r";
    public static final String ID_WEATHER = "s";
    public static final String ID_CARBIT = "t";
    public static final String ID_USERGUIDE = "u";
    public static  int WARNING_SHOWING = -1;
    public static  int WARNING_SKIP = 0;
    public static  int WARNING_STATE = -1;
    public static  String CURRENT_AUDIOID = ADAYO_SOURCE_BT_AUDIO;
    public static int SHARE_DATA_SOURCE_MANAGER_ID = 14;
    /**
     * 底部小卡固定切图资源映射
     */




}
