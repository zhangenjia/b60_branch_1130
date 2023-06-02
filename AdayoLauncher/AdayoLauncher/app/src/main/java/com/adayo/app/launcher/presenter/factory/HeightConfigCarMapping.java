package com.adayo.app.launcher.presenter.factory;


import android.util.Log;

import com.adayo.app.launcher.R;
import com.adayo.app.launcher.model.bean.AllAppBean;
import com.adayo.app.launcher.presenter.function.ConfigFunction;

import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AIQUTING;

import static com.adayo.app.launcher.util.MyConstantsUtil.ID_APA;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AVM;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_CARBIT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_DVR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MUSIC;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MYCAR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_NAVI;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_OFFROADINFO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_PICTURE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_RADIO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_SETTING;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_TEL;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_USERGUIDE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_VIDEO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WEATHER;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WECHAT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_WITHTENCENT;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_YUEYEQUAN;

import java.util.ArrayList;
import java.util.List;

import warning.LauncherApplication;

public class HeightConfigCarMapping implements IConfig {

    private String mDefaultBottomBigCardInfoList = "";
    private String mDefaultBottomSmallCardInfoList = "";
    private String mDefaultSmallCardInfoList = "";
    private String allAppMapping = "";
    char avm = 'j';
    char apa = 'l';
    char dvr = 'f';


    private final List<AllAppBean> allAppCardList = new ArrayList<AllAppBean>() {{
        add(new AllAppBean(ID_VIDEO, R.drawable.allapp_video, String.valueOf(R.string.video),true));
        add(new AllAppBean(ID_TEL, R.drawable.allapp_tel, String.valueOf(R.string.tel),true));
        add(new AllAppBean(ID_PICTURE, R.drawable.allapp_picture, String.valueOf(R.string.picture),true));
        add(new AllAppBean(ID_SETTING, R.drawable.allapp_setting, String.valueOf(R.string.settings),true));
        add(new AllAppBean(ID_MYCAR, R.drawable.allapp_mycar, String.valueOf(R.string.mycar),true));
        if (ConfigFunction.getInstance(LauncherApplication.getContext()).isDvrConfigured()) {
            add(new AllAppBean(ID_DVR, R.drawable.allapp_dvr, String.valueOf(R.string.dvr),true));
        }
        add(new AllAppBean(ID_MUSIC, R.drawable.allapp_music, String.valueOf(R.string.music),true));
        add(new AllAppBean(ID_NAVI, R.drawable.allapp_navi, String.valueOf(R.string.navigation),true));
        if (ConfigFunction.getInstance(LauncherApplication.getContext()).isAvmConfigured()) {
            add(new AllAppBean(ID_AVM, R.drawable.allapp_avm, String.valueOf(R.string.avm),true));
        }
        add(new AllAppBean(ID_OFFROADINFO, R.drawable.allapp_offroadinfo, String.valueOf(R.string.orim),true));
        if (ConfigFunction.getInstance(LauncherApplication.getContext()).isApaConfigured()) {
            add(new AllAppBean(ID_APA, R.drawable.allapp_aps, String.valueOf(R.string.apa),true));
        }
        add(new AllAppBean(ID_RADIO, R.drawable.allapp_fm, String.valueOf(R.string.radio),true));
        add(new AllAppBean(ID_YUEYEQUAN, R.drawable.allapp_yueyequan, String.valueOf(R.string.yueyequan),true));
        add(new AllAppBean(ID_WITHTENCENT, R.drawable.allapp_withtencent, String.valueOf(R.string.tencentway),true));
        add(new AllAppBean(ID_AIQUTING, R.drawable.allapp_aiquting, String.valueOf(R.string.funcaudio),true));
        add(new AllAppBean(ID_WECHAT, R.drawable.allapp_wechat, String.valueOf(R.string.wechat),true));
        add(new AllAppBean(ID_CARBIT, R.drawable.allapp_carbit, String.valueOf(R.string.easyconnect),true));
        add(new AllAppBean(ID_USERGUIDE, R.drawable.allapp_userguide, String.valueOf(R.string.userguide),true));
    }};

    private static HeightConfigCarMapping mHeightConfigCarMapping;
    private boolean avmConfigured;
    private boolean apaConfigured;
    private boolean dvrConfigured;

    public static HeightConfigCarMapping getInstance() {
        if (null == mHeightConfigCarMapping) {
            synchronized (HeightConfigCarMapping.class) {
                if (null == mHeightConfigCarMapping) {
                    mHeightConfigCarMapping = new HeightConfigCarMapping();
                }
            }
        }
        return mHeightConfigCarMapping;
    }

    private HeightConfigCarMapping() {
    }

    @Override
    public String getDefaultSmallCardMapping() {
//        mDefaultSmallCardInfoList = ID_WECHAT + ID_RADIO + ID_AIQUTING + ID_NAVI;
        mDefaultSmallCardInfoList = ID_YUEYEQUAN+ID_AIQUTING+ID_NAVI+ID_WECHAT;

        return mDefaultSmallCardInfoList;
    }

    /**
     * 获取底部默认小卡
     */
    @Override
    public String getBottomDefaultSmallCardMapping() {
        mDefaultBottomSmallCardInfoList = ID_RADIO+ID_TEL+ID_VIDEO + ID_PICTURE + ID_SETTING + ID_MYCAR + ID_OFFROADINFO + ID_APA +
                ID_AVM + ID_MUSIC  + ID_WITHTENCENT + ID_DVR + ID_CARBIT;

        avmConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isAvmConfigured();
        apaConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isApaConfigured();
        dvrConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isDvrConfigured();
        if (!avmConfigured) {//如果没有avm去掉avm的卡片映射
            mDefaultBottomSmallCardInfoList = deleteCharString(mDefaultBottomSmallCardInfoList, avm);
        }
        if (!apaConfigured) {//如果没有apa去掉apa的卡片映射
            mDefaultBottomSmallCardInfoList = deleteCharString(mDefaultBottomSmallCardInfoList, apa);
        }
        if (!dvrConfigured) {//如果没有dvr去掉dvr的卡片映射
            mDefaultBottomSmallCardInfoList = deleteCharString(mDefaultBottomSmallCardInfoList, dvr);
        }
        Log.d("aaa", "getBottomDefaultSmallCardMapping: " + mDefaultBottomSmallCardInfoList);
        return mDefaultBottomSmallCardInfoList;
    }

    /**
     * 获取底部默认大卡
     */
    @Override
    public String getBottomDefaultBigCardMapping() {
        mDefaultBottomBigCardInfoList = ID_AIQUTING + ID_TEL  + ID_MUSIC +
                ID_NAVI + ID_RADIO + ID_WEATHER;
        return mDefaultBottomBigCardInfoList;
    }

    @Override
    public List<AllAppBean> getAllAppCardResource() {

        return allAppCardList;
    }


    @Override
    public String getAllAppCardMapping() {
        avmConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isAvmConfigured();
        apaConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isApaConfigured();
        dvrConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isDvrConfigured();
        allAppMapping = ID_VIDEO + ID_TEL + ID_PICTURE + ID_SETTING + ID_MYCAR +
                ID_DVR + ID_MUSIC + ID_NAVI + ID_AVM + ID_OFFROADINFO +
                ID_APA + ID_RADIO + ID_YUEYEQUAN + ID_WITHTENCENT + ID_AIQUTING
                + ID_WECHAT + ID_CARBIT + ID_USERGUIDE;
        if (!avmConfigured) {//如果没有avm去掉avm的卡片映射
            allAppMapping = deleteCharString(allAppMapping, avm);
        }
        if (!apaConfigured) {//如果没有apa去掉apa的卡片映射
            allAppMapping = deleteCharString(allAppMapping, apa);
        }
        if (!dvrConfigured) {//如果没有dvr去掉dvr的卡片映射
            allAppMapping = deleteCharString(allAppMapping, dvr);
        }
        return allAppMapping;
    }


    /**
     * 去除字符串中指定 char
     *
     * @param sourceString
     * @param chElemData
     * @return
     */
    public String deleteCharString(String sourceString, char chElemData) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < sourceString.length(); i++) {
            if (sourceString.charAt(i) != chElemData) {
                stringBuffer.append(sourceString.charAt(i));
            }
        }
        return stringBuffer.toString();
    }

    @Override
    public String getAllBigCardMapping() {
        return ID_OFFROADINFO + ID_AIQUTING + ID_TEL  + ID_MUSIC +
                ID_NAVI + ID_RADIO + ID_WEATHER;
    }
}
