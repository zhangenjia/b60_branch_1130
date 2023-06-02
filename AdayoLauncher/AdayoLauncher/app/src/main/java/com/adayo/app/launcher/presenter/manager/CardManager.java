package com.adayo.app.launcher.presenter.manager;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_AIQUTING;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_APS;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_AVM;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_CARBIT;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_DVR;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_MUSIC;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_MYCAR;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_NAVI;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_OFFROADINFO;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_PICTURE;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_RADIO;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_SETTING;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_TEL;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_VIDEO;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WEATHER;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WECHAT;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_WITHTENCENT;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.ID_YUEYEQUAN;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.adayo.app.launcher.R;
import com.adayo.app.launcher.btphone.ui.view.BTPhoneBigCardView;
import com.adayo.app.launcher.btphone.ui.view.BTPhoneSmallCardView;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.music.view.MusicLargeCard;
import com.adayo.app.launcher.music.view.MusicSmallCard;
import com.adayo.app.launcher.navi.view.NavigationCardView;
import com.adayo.app.launcher.offroadinfo.presenter.CrossDataManager;
import com.adayo.app.launcher.offroadinfo.ui.view.OffRoadInfoCardView;
import com.adayo.app.launcher.presenter.listener.ConfigurationChangeImpl;
import com.adayo.app.launcher.radio.RadioCarView;
import com.adayo.app.launcher.weather.WeatherCardView;
import com.adayo.app.launcher.wecarflow.WeCarFlowCardView;
import com.adayo.jar.weather.client.WeatherManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.systemservice.SystemServiceManager;
import com.example.BcmImpl;
import com.example.externalcardview.NoShortcutCardView;

import java.util.ArrayList;
import java.util.List;

public class CardManager implements ConfigurationChangeImpl.ConfigurationChangeCallBack {

    //todo 目前是上屏才初始化大卡小卡布局 注册监听，判断一下是否换成别的方案，如果改成启动就注册，启动就加载布局会不会影响性能。

    private String TAG = CardManager.class.getSimpleName();
    private NoShortcutCardView mCardView_video;
    private NoShortcutCardView mCardView_picture;
    private NoShortcutCardView mCardView_setting;
    private NoShortcutCardView mCardView_mycar;
    private NoShortcutCardView mCardView_dvr;
    private NoShortcutCardView mCardView_applets;
    private NoShortcutCardView mCardView_avm;
    private NoShortcutCardView mCardView_aps;
    private NoShortcutCardView mCardView_yueyequan;
    private NoShortcutCardView mCardView_withtencent;
    private NoShortcutCardView mCardView_wechat;
    private NoShortcutCardView mCardView_carbit;
    private OffRoadInfoCardView mCardView_offroadinfo;
    private WeCarFlowCardView mWeCarFlow;
    private BTPhoneBigCardView mBTPhoneBigCardView;
    private BTPhoneSmallCardView mBTPhoneSmallCardView;
    private NavigationCardView mNaviCardView;
    private WeatherCardView mWeatherCardView;
    private List<IViewBase> list = new ArrayList<>();
    private MusicLargeCard mMusicLargeCardView;
    private MusicSmallCard mMusicSmallCardView;
    private RadioCarView mRadioCarView;

    private boolean launchercompelete = false;
    private Context context;
    private boolean isHvacVisible;


    public CardManager(Context context) {
        Log.d(TAG, "CardManager: ");
        CrossDataManager.getInstance().init();
        ConfigurationChangeImpl.getInstance().addConfigurationChangeCallBack(this);
        final String sn = SystemServiceManager.getInstance().getSystemConfigInfo((byte) 0x06);
        WeatherManager.getInstance().init(context, sn, "HM6C17A", "2");
        BcmImpl.getInstance().init();
    }

    /**
     * 卡片上屏
     * 调用场景
     * 1 launcher启动显示在屏幕上的卡片
     * 2 拖拽上屏
     *
     * @param context
     * @param id
     * @param type
     * @return
     */
    public View initCardView(Context context, String id, String type,String type1) {
        this.context = context;
//        if (true){
//            return new View(context);
//        }
        Log.d(TAG, "initCardView: start" + "id = " + id);
        switch (id) {
            case ID_VIDEO:
                View videoView;
                if (mCardView_video == null) {
//                    mCardView_video = new SimpleCardView(context);
                    mCardView_video = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.video,R.drawable.bg_smallcard,R.mipmap.img_smallcard_video_top,R.drawable.img_smallcard_video_b,"video",id);
                }
                if (!list.contains(mCardView_video)) {
                    list.add(mCardView_video);
                }
                videoView = mCardView_video.initCardView(id, type,type1);
                return videoView;
            case ID_TEL:
                View btPhoneBigCard;
                View btPhoneSmallCard;
                if (type.equals(TYPE_BIGCARD)) {
                    if (mBTPhoneBigCardView == null) {
                        mBTPhoneBigCardView = new BTPhoneBigCardView(context);
                    }
                    if (!list.contains(mBTPhoneBigCardView)) {
                        list.add(mBTPhoneBigCardView);
                    }
                    if (launchercompelete){
                        mBTPhoneBigCardView.launcherLoadComplete();
                    }
                    btPhoneBigCard = mBTPhoneBigCardView.initCardView(id, type,type1);
                    return btPhoneBigCard;
                } else if (type.equals(TYPE_SMALLCARD)) {
                    if (mBTPhoneSmallCardView == null) {
                        mBTPhoneSmallCardView = new BTPhoneSmallCardView(context);
                    }
                    if (!list.contains(mBTPhoneSmallCardView)) {
                        list.add(mBTPhoneSmallCardView);
                    }
                    if (launchercompelete) {
                        mBTPhoneSmallCardView.launcherLoadComplete();
                    }
                    btPhoneSmallCard = mBTPhoneSmallCardView.initCardView(id, type,type1);
                    return btPhoneSmallCard;
                }
                break;
            case ID_PICTURE:
                View pictureView;
                if (mCardView_picture == null) {
                    mCardView_picture = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.picture,R.drawable.bg_smallcard,R.mipmap.img_smallcard_picture_top,R.drawable.img_smallcard_picture_b,"picture",id);
                }
                if (!list.contains(mCardView_picture)) {
                    list.add(mCardView_picture);
                }
                pictureView = mCardView_picture.initCardView(id, type,type1);
                return pictureView;
            case ID_SETTING:
                View settingView;
                if (mCardView_setting == null) {
                    mCardView_setting = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.settings,R.drawable.bg_smallcard,R.mipmap.img_smallcard_setting_top,R.drawable.img_smallcard_setting_b,"setting",id);
                }
                if (!list.contains(mCardView_setting)) {
                    list.add(mCardView_setting);
                }
                settingView = mCardView_setting.initCardView(id, type,type1);
                return settingView;
            case ID_MYCAR:
                View mycarView;
                if (mCardView_mycar == null) {
                    mCardView_mycar = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.mycar,R.drawable.bg_smallcard,R.mipmap.img_smallcard_mycar_top,R.drawable.img_smallcard_mycar_b,"mycar",id);
                }
                if (!list.contains(mCardView_mycar)) {
                    list.add(mCardView_mycar);
                }
                mycarView = mCardView_mycar.initCardView(id, type,type1);
                return mycarView;
            case ID_DVR:
                View dvrView;
                if (mCardView_dvr == null) {
                    mCardView_dvr = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.dvr,R.drawable.bg_smallcard,R.mipmap.img_smallcard_dvr_top,R.drawable.img_smallcard_dvr_b,"dvr",id);
                }
                if (!list.contains(mCardView_dvr)) {
                    list.add(mCardView_dvr);
                }
                dvrView = mCardView_dvr.initCardView(id, type,type1);
                return dvrView;
            case ID_MUSIC:
                View mMusicLargeCard;
                View mMusicSmallCard;
                if (type.equals(TYPE_BIGCARD)) {
                    if (mMusicLargeCardView == null) {
                        mMusicLargeCardView = new MusicLargeCard(context);


                    }
                    if (!list.contains(mMusicLargeCardView)) {
                        list.add(mMusicLargeCardView);
                    }
                    if (launchercompelete){
                        mMusicLargeCardView.launcherLoadComplete();
                    }
                    mMusicLargeCard = mMusicLargeCardView.initCardView(id, type,type1);
                    return mMusicLargeCard;
                } else if (type.equals(TYPE_SMALLCARD)) {
                    if (mMusicSmallCardView == null) {
                        mMusicSmallCardView = new MusicSmallCard(context);
                    }
                    if (!list.contains(mMusicSmallCardView)) {
                        list.add(mMusicSmallCardView);
                    }
                    Log.d(TAG, "initCardView=====: " + mMusicSmallCardView);
                    mMusicSmallCard = mMusicSmallCardView.initCardView(id, type,type1);
                    return mMusicSmallCard;
                }
                break;



            case ID_NAVI:
                View naviCardView = null;
                if (mNaviCardView == null) {
                    mNaviCardView = new NavigationCardView(context);
                    Log.d(TAG, "initCardView: " + mNaviCardView);
                }
                if (!list.contains(mNaviCardView)) {
                    list.add(mNaviCardView);
                }
                if (launchercompelete){
                    mNaviCardView.launcherLoadComplete();
                }
                naviCardView = mNaviCardView.initCardView(id, type,type1);
                return naviCardView;


            case ID_AVM:
                View avmView;
                if (mCardView_avm == null) {
//                    mCardView_avm = new NoShortcutCardView(context,R.layout.layout_smallcard_avm,R.string.avm);
                    mCardView_avm = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.avm,R.drawable.bg_smallcard,R.mipmap.img_smallcard_avm_top,R.drawable.img_smallcard_avm_b,"avm",id);
                }
                if (!list.contains(mCardView_avm)) {
                    list.add(mCardView_avm);
                }
                avmView = mCardView_avm.initCardView(id, type,type1);
                return avmView;
            case ID_OFFROADINFO:

                View offroadinfoView;
                if (mCardView_offroadinfo == null) {
                    mCardView_offroadinfo = new OffRoadInfoCardView(context);
                }
                if (!list.contains(mCardView_offroadinfo)) {
                    list.add(mCardView_offroadinfo);
                }
                if (launchercompelete){
                    mCardView_offroadinfo.launcherLoadComplete();
                }
                offroadinfoView = mCardView_offroadinfo.initCardView(id, type,type1);
                return offroadinfoView;
            case ID_APS:
                View apsView;
                if (mCardView_aps == null) {

                    mCardView_aps = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.apa,R.drawable.bg_smallcard,R.mipmap.img_smallcard_aps_top,R.drawable.img_smallcard_aps_b,"aps",id);
                }
                if (!list.contains(mCardView_aps)) {
                    list.add(mCardView_aps);
                }
                apsView = mCardView_aps.initCardView(id, type,type1);
                return apsView;

            case ID_RADIO:
                View radioView;
                if (mRadioCarView == null) {
                    mRadioCarView = new RadioCarView(context);
                }
                radioView = mRadioCarView.initCardView(id, type,type1);
                if (!list.contains(mRadioCarView)) {
                    list.add(mRadioCarView);
                }
                if (launchercompelete){
                    mRadioCarView.launcherLoadComplete();
                }
                return radioView;
            case ID_YUEYEQUAN:
                View yueyequanView;
                if (mCardView_yueyequan == null) {
                    mCardView_yueyequan = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.yueyequan,R.drawable.bg_smallcard,R.drawable.img_smallcard_yueyequan,R.drawable.img_smallcard_yueyequan_b,null,id);
                }
                if (!list.contains(mCardView_yueyequan)) {
                    list.add(mCardView_yueyequan);
                }
                yueyequanView = mCardView_yueyequan.initCardView(id, type,type1);
                return yueyequanView;
            case ID_WITHTENCENT:
                View withtencentView;
                if (mCardView_withtencent == null) {
                    mCardView_withtencent = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.tencentway,R.drawable.bg_smallcard,R.drawable.img_smallcard_withtencent,R.drawable.img_smallcard_withtencent_b,null,id);
                }
                if (!list.contains(mCardView_withtencent)) {
                    list.add(mCardView_withtencent);
                }
                withtencentView = mCardView_withtencent.initCardView(id, type,type1);
                return withtencentView;
            case ID_AIQUTING:
                View weCarFlowCardView;
                if (mWeCarFlow == null) {
                    mWeCarFlow = new WeCarFlowCardView(context);
                }
                if (!list.contains(mWeCarFlow)) {
                    list.add(mWeCarFlow);
                }
                if (launchercompelete){
                    mWeCarFlow.launcherLoadComplete();
                }
                weCarFlowCardView = mWeCarFlow.initCardView(id, type,type1);

                return weCarFlowCardView;
            case ID_WECHAT:
                View wechatView;
                if (mCardView_wechat == null) {
                    mCardView_wechat = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.wechat,R.drawable.bg_smallcard,R.drawable.img_smallcard_wechat,R.drawable.img_smallcard_wechat_b,null,id);
                }
                if (!list.contains(mCardView_wechat)) {
                    list.add(mCardView_wechat);
                }
                wechatView = mCardView_wechat.initCardView(id, type,type1);
                return wechatView;

            case ID_WEATHER:
                View weatherView;
                if (mWeatherCardView == null) {
                    mWeatherCardView = new WeatherCardView(context);
                }
                if (!list.contains(mWeatherCardView)) {
                    list.add(mWeatherCardView);
                }
                if (launchercompelete){
                    mWeatherCardView.launcherLoadComplete();
                }
                weatherView = mWeatherCardView.initCardView(id, type,type1);
                return weatherView;

            case ID_CARBIT:
                View carbitView;
                if (mCardView_carbit == null) {

                    mCardView_carbit = new NoShortcutCardView(context, R.layout.layout_smallcard_aps
                            ,R.string.easyconnect,R.drawable.bg_smallcard,R.drawable.img_smallcard_carbit,R.drawable.img_smallcard_carbit_b,null,id);
                }
                if (!list.contains(mCardView_carbit)) {
                    list.add(mCardView_carbit);
                }
                carbitView = mCardView_carbit.initCardView(id, type,type1);
                return carbitView;
        }
        Log.d(TAG, "initCardView: end = null");
        return null;
    }

    /**
     * 小卡动画
     */
    public void palyLottileAnimation(String id, int delay) {
        Log.d(TAG, "palyLottileAnimation: " + id + "   " + delay);
        switch (id) {
            case ID_VIDEO:
                if (mCardView_video != null) {
                    mCardView_video.playAnimation(id, delay);
                }
                break;
            case ID_TEL:
                if (mBTPhoneSmallCardView != null) {
                    mBTPhoneSmallCardView.playAnimation(id, delay);
                }
                break;
            case ID_PICTURE:
                if (mCardView_picture != null) {
                    mCardView_picture.playAnimation(id, delay);
                }
                break;
            case ID_SETTING:
                if (mCardView_setting != null) {
                    mCardView_setting.playAnimation(id, delay);
                }
                break;
            case ID_MYCAR:
                if (mCardView_mycar != null) {
                    mCardView_mycar.playAnimation(id, delay);
                }
                break;
            case ID_DVR:
                if (mCardView_dvr != null) {
                    mCardView_dvr.playAnimation(id, delay);
                }
                break;
            case ID_MUSIC:
                if (mMusicSmallCardView != null) {
                    mMusicSmallCardView.playAnimation(id, delay);
                }
                break;

            case ID_NAVI:
                if (mNaviCardView != null) {
                    mNaviCardView.playAnimation(id, delay);
                }
                break;
            case ID_AVM:
                if (mCardView_avm != null) {
                    mCardView_avm.playAnimation(id, delay);
                }
                break;
            case ID_OFFROADINFO:
                if (mCardView_offroadinfo != null) {
                    mCardView_offroadinfo.playAnimation(id, delay);
                }
                break;
            case ID_APS:
                if (mCardView_aps != null) {
                    mCardView_aps.playAnimation(id, delay);
                }
                break;
            case ID_RADIO:
                if (mRadioCarView != null) {
                    mRadioCarView.playAnimation(id, delay);
                }
                break;
            case ID_YUEYEQUAN:
                if (mCardView_yueyequan != null) {
                    mCardView_yueyequan.playAnimation(id, delay);
                }
                break;
            case ID_WITHTENCENT:
                if (mCardView_withtencent != null) {
                    mCardView_withtencent.playAnimation(id, delay);
                }
                break;
            case ID_AIQUTING:
                Log.d(TAG, "unInitCard: " + " aiquting");
                if (mWeCarFlow != null) {
                    mWeCarFlow.playAnimation(id, delay);
                }
                break;
            case ID_WECHAT:
                if (mCardView_wechat != null) {
                    mCardView_wechat.playAnimation(id, delay);
                }
                break;

            case ID_CARBIT:
                if (mCardView_carbit != null) {
                    mCardView_carbit.playAnimation(id, delay);
                }
                break;
        }
    }

    /**
     * 卡片下屏
     *
     * @param
     * @param id
     * @param type
     */
    public void unInitCard(String id, String type) {

        switch (id) {
            case ID_PICTURE:
            case ID_SETTING:
            case ID_MYCAR:
            case ID_DVR:
            case ID_AVM:
            case ID_OFFROADINFO:
                if (mCardView_offroadinfo != null) {
                    mCardView_offroadinfo.unInitCardView(id, type);

                }
                break;
            case ID_APS:
            case ID_YUEYEQUAN:
            case ID_WITHTENCENT:
            case ID_WECHAT://卡片无其他功能无需处理下屏操作
                break;
            case ID_CARBIT://卡片无其他功能无需处理下屏操作
                break;
            case ID_VIDEO:
                if (mCardView_video != null) {
                    mCardView_video.unInitCardView(id, type);
                }
                break;
            case ID_TEL:
                if (mBTPhoneBigCardView != null) {
                    mBTPhoneBigCardView.unInitCardView(id, type);
                } else if (mBTPhoneSmallCardView != null) {
                    mBTPhoneSmallCardView.unInitCardView(id,type);//todo !！
                }
                break;
            case ID_MUSIC:
                if (mMusicLargeCardView != null) {
                    mMusicLargeCardView.unInitCardView(id, type);
                } else if (mMusicSmallCardView != null) {
                    mMusicSmallCardView.unInitCardView(id, type);
                }
                break;
            case ID_NAVI:
                if (mNaviCardView != null) {
                    mNaviCardView.unInitCardView(id, type);
                }
                break;
            case ID_RADIO:
                if (mRadioCarView != null) {
                    mRadioCarView.unInitCardView(id, type);
                }
                break;
            case ID_WEATHER:
                if (mWeatherCardView != null) {
                    mWeatherCardView.unInitCardView(id, type);
                }
                break;
            case ID_AIQUTING:
                Log.d(TAG, "unInitCard: " + " aiquting");
                if (mWeCarFlow != null) {
                    mWeCarFlow.unInitCardView(id, type);
                }
                break;


        }
    }

    /**
     * 切语言
     */
    @Override
    public void configurationChange() {
        for (IViewBase view : list) {
            view.onConfigurationChanged();
        }
    }

    /**
     * Launcher启动完成
     */
    public void launcherBootComplete() {
        launchercompelete = true;
        if (mNaviCardView != null) {
            mNaviCardView.launcherLoadComplete();
        }
        if (mBTPhoneBigCardView != null) {
            mBTPhoneBigCardView.launcherLoadComplete();
        }
        if (mCardView_offroadinfo != null) {
            mCardView_offroadinfo.launcherLoadComplete();
        }
        if (mWeatherCardView != null) {
            mWeatherCardView.launcherLoadComplete();
        }
        if (mMusicSmallCardView != null) {
            mMusicSmallCardView.launcherLoadComplete();
        }
        if (mMusicLargeCardView != null) {
            mMusicLargeCardView.launcherLoadComplete();
        }
        if (mRadioCarView != null) {
            mRadioCarView.launcherLoadComplete();
        }
        if (mNaviCardView != null) {
            mNaviCardView.launcherLoadComplete();
        }

        if (mWeCarFlow != null) {
            mWeCarFlow.launcherLoadComplete();
        }
    }

    public void launcherAnimationUpdate(int i) {
        Log.d(TAG, "launcherAnimationUpdate: "+i);
        if (mNaviCardView != null) {
            mNaviCardView.launcherAnimationUpdate(i);
        }
        if (mBTPhoneBigCardView != null) {
            mBTPhoneBigCardView.launcherAnimationUpdate(i);
        }
        if (mCardView_offroadinfo != null) {
            mCardView_offroadinfo.launcherAnimationUpdate(i);
        }
        if (mWeatherCardView != null) {
            mWeatherCardView.launcherAnimationUpdate(i);
        }
        if (mMusicLargeCardView != null) {
            mMusicLargeCardView.launcherAnimationUpdate(i);
        }
        if (mRadioCarView != null) {
            mRadioCarView.launcherAnimationUpdate(i);
        }
        if (mNaviCardView != null) {
            mNaviCardView.launcherAnimationUpdate(i);
        }

        if (mWeCarFlow != null) {
            mWeCarFlow.launcherAnimationUpdate(i);
        }
    }



}
