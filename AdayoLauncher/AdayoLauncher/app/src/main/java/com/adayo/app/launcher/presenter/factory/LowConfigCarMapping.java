package com.adayo.app.launcher.presenter.factory;

import com.adayo.app.launcher.R;
import com.adayo.app.launcher.model.bean.AllAppBean;
import com.adayo.app.launcher.presenter.function.ConfigFunction;
import com.adayo.app.launcher.util.MyConstantsUtil;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_APA;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_AVM;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_DVR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MUSIC;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_MYCAR;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_OFFROADINFO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_PICTURE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_RADIO;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_SETTING;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_TEL;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_USERGUIDE;
import static com.adayo.app.launcher.util.MyConstantsUtil.ID_VIDEO;

import java.util.ArrayList;
import java.util.List;
import warning.LauncherApplication;

public class LowConfigCarMapping implements IConfig {

    private String mDefaultBottomBigCardInfoList = "";
    private String mDefaultBottomSmallCardInfoList = "";
    private String mDefaultSmallCardInfoList = "";
    private String allAppMapping = "";

    private boolean avmConfigured;
    private boolean apaConfigured;
    private boolean dvrConfigured;
    char avm = 'j';
    char apa = 'l';
    char dvr = 'f';

    private final List<AllAppBean> allAppCardList = new ArrayList<AllAppBean>(){{
        add(new AllAppBean(ID_RADIO,R.drawable.allapp_fm, String.valueOf(R.string.radio),true));
        add(new AllAppBean(ID_TEL,R.drawable.allapp_tel, String.valueOf(R.string.tel),true));
        add(new AllAppBean(ID_MUSIC,R.drawable.allapp_music, String.valueOf(R.string.music),true));
        add(new AllAppBean(ID_PICTURE,R.drawable.allapp_picture, String.valueOf(R.string.picture),true));
        add(new AllAppBean(ID_VIDEO,R.drawable.allapp_video, String.valueOf(R.string.video),true));
        add(new AllAppBean(ID_MYCAR,R.drawable.allapp_mycar,String.valueOf(R.string.mycar),true));
        add(new AllAppBean(ID_SETTING,R.drawable.allapp_setting, String.valueOf(R.string.settings),true));
        if (ConfigFunction.getInstance(LauncherApplication.getContext()).isDvrConfigured()){
            add(new AllAppBean(ID_DVR,R.drawable.allapp_dvr, String.valueOf(R.string.dvr),true));
        }
        add(new AllAppBean(ID_OFFROADINFO,R.drawable.allapp_offroadinfo, String.valueOf(R.string.orim),true));
        if (ConfigFunction.getInstance(LauncherApplication.getContext()).isAvmConfigured()){
            add(new AllAppBean(ID_AVM,R.drawable.allapp_avm, String.valueOf(R.string.avm),true));
        }
        if (ConfigFunction.getInstance(LauncherApplication.getContext()).isApaConfigured()){
            add(new AllAppBean(ID_APA,R.drawable.allapp_aps, String.valueOf(R.string.apa),true));
        }
        add(new AllAppBean(ID_USERGUIDE, R.drawable.allapp_userguide, String.valueOf(R.string.userguide),true));
    }};



    private static LowConfigCarMapping mLowConfigCarMapping;

    public static LowConfigCarMapping getInstance() {
        if (null == mLowConfigCarMapping) {
            synchronized (LowConfigCarMapping.class) {
                if (null == mLowConfigCarMapping) {
                    mLowConfigCarMapping = new LowConfigCarMapping();
                }
            }
        }
        return mLowConfigCarMapping;
    }

    private LowConfigCarMapping() {
    }

    @Override
    public String getDefaultSmallCardMapping() {
        mDefaultSmallCardInfoList = ID_RADIO + ID_MUSIC + ID_TEL + ID_SETTING;
        return mDefaultSmallCardInfoList;
    }

    /**
     * 获取底部默认小卡
     */
    @Override
    public String getBottomDefaultSmallCardMapping() {
        avmConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isAvmConfigured();
        apaConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isApaConfigured();
        dvrConfigured = ConfigFunction.getInstance(LauncherApplication.getContext()).isDvrConfigured();

        mDefaultBottomSmallCardInfoList = ID_MYCAR + ID_OFFROADINFO + ID_VIDEO +
                ID_PICTURE + ID_DVR + ID_AVM + ID_APA;
        if (!avmConfigured) {//如果没有avm去掉avm的卡片映射
            mDefaultBottomSmallCardInfoList = deleteCharString(mDefaultBottomSmallCardInfoList, avm);
        }
        if (!apaConfigured) {//如果没有apa去掉apa的卡片映射
            mDefaultBottomSmallCardInfoList = deleteCharString(mDefaultBottomSmallCardInfoList, apa);
        }
        if (!dvrConfigured) {//如果没有apa去掉apa的卡片映射
            mDefaultBottomSmallCardInfoList = deleteCharString(mDefaultBottomSmallCardInfoList, dvr);
        }
        return mDefaultBottomSmallCardInfoList;
    }

    /**
     * 获取底部默认大卡
     */
    @Override
    public String getBottomDefaultBigCardMapping() {
        mDefaultBottomBigCardInfoList = ID_TEL  + ID_MUSIC + ID_RADIO;
        return mDefaultBottomBigCardInfoList;
    }

    @Override
    public List<AllAppBean> getAllAppCardResource() {

        return allAppCardList;
    }

    @Override
    public String getAllAppCardMapping() {

        allAppMapping = MyConstantsUtil.ID_RADIO + MyConstantsUtil.ID_TEL + MyConstantsUtil.ID_MUSIC
                + MyConstantsUtil.ID_PICTURE + MyConstantsUtil.ID_VIDEO + MyConstantsUtil.ID_MYCAR
                + MyConstantsUtil.ID_SETTING + MyConstantsUtil.ID_DVR + ID_OFFROADINFO + ID_USERGUIDE;
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
        return ID_OFFROADINFO+ID_TEL  + ID_MUSIC + ID_RADIO;
    }
}
