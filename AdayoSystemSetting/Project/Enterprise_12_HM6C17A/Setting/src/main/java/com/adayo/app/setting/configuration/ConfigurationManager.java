package com.adayo.app.setting.configuration;

import android.content.Context;
import android.view.View;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.view.activity.MainActivity;
import com.adayo.app.setting.view.fragment.Sound.HighSoundFragment;
import com.adayo.app.setting.view.fragment.Sound.ISoundConfig;
import com.adayo.app.setting.view.fragment.Sound.LowSoundFragment;
import com.adayo.app.setting.view.fragment.Sys.HighSysFragment;
import com.adayo.app.setting.view.fragment.Sys.LowSysFragment;
import com.adayo.app.setting.view.fragment.factory.product.Time.HighTimeFragment;
import com.adayo.app.setting.view.fragment.factory.product.Time.LowTimeFragment;
import com.adayo.app.setting.view.popwindow.harman.HighHarmanActivity;
import com.adayo.app.setting.view.popwindow.harman.IHarmanActivity;
import com.adayo.app.setting.view.popwindow.harman.LowHarmanActivity;
import com.adayo.app.setting.view.popwindow.soundEq.HighSoundEqWindow;
import com.adayo.app.setting.view.popwindow.soundEq.ISoundEQ;
import com.adayo.app.setting.view.popwindow.soundEq.LowSoundEqWindow;
import com.adayo.configurationinfo.ConfigurationWordInfo;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.app.setting.model.constant.ParamConstant.CONFIGURATION_HM6C17A;
import static com.adayo.app.setting.model.constant.ParamConstant.CONFIGURATION_HM6C18A;
import static com.adayo.app.setting.model.constant.ParamConstant.HamanPowerIcon;
import static com.adayo.app.setting.model.constant.ParamConstant.Vehicle_UI;
import static com.adayo.app.setting.model.constant.ParamConstant.Wireless_Charging;

public class ConfigurationManager {
    private static final String TAG = ConfigurationManager.class.getSimpleName();
    private ConfigurationWordInfo configurationWordInfo;
    private int config = -1;
    private ISoundConfig mISoundConfig;
    private IHarmanActivity mIHarmanActivity;
    private ISoundEQ mISoundEQ;
    private View mMainActivityView;
    private MainActivity mActivity;

    public void setView(View view) {
        LogUtil.d(TAG, "view =" + view);
        mMainActivityView = view;
        if (mActivity != null) {
            mActivity.initMainInflate(view);
        }
    }

    public MainActivity getActivity() {
        return mActivity;
    }

    public void setActivity(MainActivity activity) {
        LogUtil.d(TAG, "mView =" + mMainActivityView);
        mActivity = activity;
        if (mMainActivityView != null) {
            LogUtil.d(TAG, "NOT RIGHY");
            mActivity.initMainInflate(mMainActivityView);
        }

    }

    private static class ConfigurationManagerHolder {
        private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    }

    public static ConfigurationManager getInstance() {
        return ConfigurationManagerHolder.INSTANCE;
    }

    public void init(Context context) {
        LogUtil.debugD(TAG, "");
        configurationWordInfo = ConfigurationWordInfo.getInstance();
        configurationWordInfo.init(context);
    }

    public void uninit() {
        LogUtil.debugD(TAG, "");
        configurationWordInfo = null;
        mISoundConfig = null;
        mIHarmanActivity = null;
        mISoundEQ = null;

    }


    public void getConfiguration() {
        String s = configurationWordInfo.getCarConfiguration();
        LogUtil.d(TAG, "config = " + s);
        if (s.contains(ParamConstant.HM6C17A)) {
            config = CONFIGURATION_HM6C17A;
        } else if (s.contains(ParamConstant.HM6C18A)) {
            config = CONFIGURATION_HM6C18A;
        } else {
            config = 0;
        }
    }


    public IFragmentConfig getSysFunc() {
        if (config == CONFIGURATION_HM6C17A) {
            IFragmentConfig mIFragmentConfig = new HighSysFragment();
            return mIFragmentConfig;
        } else if (config == CONFIGURATION_HM6C18A) {
            IFragmentConfig mIFragmentConfig = new LowSysFragment();
            return mIFragmentConfig;
        }
        return new HighSysFragment();
    }


    public ISoundConfig getSoundFunc() {
        if (config == CONFIGURATION_HM6C17A) {
            mISoundConfig = new HighSoundFragment();
            return mISoundConfig;
        } else if (config == CONFIGURATION_HM6C18A) {
            mISoundConfig = new LowSoundFragment();
            return mISoundConfig;
        } else {
            return mISoundConfig = new HighSoundFragment();
        }
    }


    public IHarmanActivity getHarmanActivity() {
        LogUtil.debugD(TAG, "config = " + config);
        if (config == CONFIGURATION_HM6C17A) {
            if (isHamanPowerIcon()) {
                mIHarmanActivity = new HighHarmanActivity();
                return mIHarmanActivity;
            }else {
                mIHarmanActivity = new LowHarmanActivity();
                return mIHarmanActivity;
            }
        } else if (config == CONFIGURATION_HM6C18A) {
            mIHarmanActivity = new LowHarmanActivity();
            return mIHarmanActivity;
        } else {
            return mIHarmanActivity = new HighHarmanActivity();
        }

    }

    public ISoundEQ getISoundEQFunc() {
        LogUtil.debugD(TAG, "config = " + config);
        if (config == CONFIGURATION_HM6C17A) {
            mISoundEQ = new HighSoundEqWindow();
            return mISoundEQ;

        } else if (config == CONFIGURATION_HM6C18A) {
            mISoundEQ = new LowSoundEqWindow();
            return mISoundEQ;
        } else {
            return mISoundEQ = new HighSoundEqWindow();
        }

    }

    public IFragmentConfig getTimeFunc() {
        if (config == CONFIGURATION_HM6C17A) {
            IFragmentConfig mIFragmentConfig = new HighTimeFragment();
            return mIFragmentConfig;
        } else if (config == CONFIGURATION_HM6C18A) {
            IFragmentConfig mIFragmentConfig = new LowTimeFragment();
            return mIFragmentConfig;
        }
        return new HighTimeFragment();
    }

    public int getConfig() {
        return config;
    }


    private boolean isWirelessCharging() {
        int wirelessCharging = configurationWordInfo.getKey(Wireless_Charging);
        LogUtil.d(TAG, "mWirelessCharging = " + wirelessCharging);
        if (wirelessCharging == 1) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isHamanPowerIcon() {
        int isHamanPowerIcon = configurationWordInfo.getKey(HamanPowerIcon);
        LogUtil.d(TAG, "HamanPowerIcon = " + isHamanPowerIcon);
        if (isHamanPowerIcon == 1) {
            return true;
        } else {
            return false;
        }
    }


    public int getVehicleUI() {
        int vehicleUI = configurationWordInfo.getKey(Vehicle_UI);
        LogUtil.d(TAG, "VehicleUI =" + vehicleUI);
        return vehicleUI;
    }

    public List<DirectMediaBean> getDirectMediaConfig() {
        List<DirectMediaBean> listBean = new ArrayList<>();
        if (config == CONFIGURATION_HM6C18A) {
            listBean.add(new DirectMediaBean(R.string.direct_media_media_change_title, R.string.direct_media_media_change_info, 0));
            listBean.add(new DirectMediaBean(R.string.direct_media_media_play_title, R.string.direct_media_media_play_info, 1));
            listBean.add(new DirectMediaBean(R.string.direct_media_yueye_info_title, R.string.direct_media_yueye_info_info, 2));
            listBean.add(new DirectMediaBean(R.string.direct_media_dvr_switch_title, R.string.direct_media_dvr_switch_info, 10));
            listBean.add(new DirectMediaBean(R.string.direct_media_air_conditioner_title, R.string.direct_media_air_conditioner_info, 11));
            listBean.add(new DirectMediaBean(R.string.direct_media_screen_off_title, R.string.direct_media_screen_off_info, 12));
            listBean.add(new DirectMediaBean(R.string.direct_media_system_set_title, R.string.direct_media_system_set_info, 14));
        } else if (config == 1) {
            if (isWirelessCharging()) {
                listBean.add(new DirectMediaBean(R.string.direct_media_media_change_title, R.string.direct_media_media_change_info, 0));
                listBean.add(new DirectMediaBean(R.string.direct_media_media_play_title, R.string.direct_media_media_play_info, 1));
                listBean.add(new DirectMediaBean(R.string.direct_media_yueye_info_title, R.string.direct_media_yueye_info_info, 2));
                listBean.add(new DirectMediaBean(R.string.direct_media_music_app_title, R.string.direct_media_music_app_info, 3));
                listBean.add(new DirectMediaBean(R.string.direct_media_navi_switch_title, R.string.direct_media_navi_switch_info, 4));
                listBean.add(new DirectMediaBean(R.string.direct_media_navi_soundoff_title, R.string.direct_media_navi_soundoff_info, 6));
                listBean.add(new DirectMediaBean(R.string.direct_media_go_home_title, R.string.direct_media_go_home_info, 7));
                listBean.add(new DirectMediaBean(R.string.direct_media_go_company_title, R.string.direct_media_go_company_info, 8));
                listBean.add(new DirectMediaBean(R.string.direct_media_mobile_internet_title, R.string.direct_media_mobile_internet_info, 9));
                listBean.add(new DirectMediaBean(R.string.direct_media_dvr_switch_title, R.string.direct_media_dvr_switch_info, 10));
                listBean.add(new DirectMediaBean(R.string.direct_media_air_conditioner_title, R.string.direct_media_air_conditioner_info, 11));
                listBean.add(new DirectMediaBean(R.string.direct_media_screen_off_title, R.string.direct_media_screen_off_info, 12));
                listBean.add(new DirectMediaBean(R.string.direct_media_wireless_charging_title, R.string.direct_media_wireless_charging_info, 13));
                listBean.add(new DirectMediaBean(R.string.direct_media_system_yueyequan_title, R.string.direct_media_system_yueyequan_info, 15));
                listBean.add(new DirectMediaBean(R.string.direct_media_system_set_title, R.string.direct_media_system_set_info, 14));


            } else {
                listBean.add(new DirectMediaBean(R.string.direct_media_media_change_title, R.string.direct_media_media_change_info, 0));
                listBean.add(new DirectMediaBean(R.string.direct_media_media_play_title, R.string.direct_media_media_play_info, 1));
                listBean.add(new DirectMediaBean(R.string.direct_media_yueye_info_title, R.string.direct_media_yueye_info_info, 2));
                listBean.add(new DirectMediaBean(R.string.direct_media_music_app_title, R.string.direct_media_music_app_info, 3));
                listBean.add(new DirectMediaBean(R.string.direct_media_navi_switch_title, R.string.direct_media_navi_switch_info, 4));
                listBean.add(new DirectMediaBean(R.string.direct_media_navi_soundoff_title, R.string.direct_media_navi_soundoff_info, 6));
                listBean.add(new DirectMediaBean(R.string.direct_media_go_home_title, R.string.direct_media_go_home_info, 7));
                listBean.add(new DirectMediaBean(R.string.direct_media_go_company_title, R.string.direct_media_go_company_info, 8));
                listBean.add(new DirectMediaBean(R.string.direct_media_mobile_internet_title, R.string.direct_media_mobile_internet_info, 9));
                listBean.add(new DirectMediaBean(R.string.direct_media_dvr_switch_title, R.string.direct_media_dvr_switch_info, 10));
                listBean.add(new DirectMediaBean(R.string.direct_media_air_conditioner_title, R.string.direct_media_air_conditioner_info, 11));
                listBean.add(new DirectMediaBean(R.string.direct_media_screen_off_title, R.string.direct_media_screen_off_info, 12));
                listBean.add(new DirectMediaBean(R.string.direct_media_system_yueyequan_title, R.string.direct_media_system_yueyequan_info, 15));
                listBean.add(new DirectMediaBean(R.string.direct_media_system_set_title, R.string.direct_media_system_set_info, 14));

            }
        } else {
            listBean.add(new DirectMediaBean(R.string.direct_media_media_change_title, R.string.direct_media_media_change_info, 0));
            listBean.add(new DirectMediaBean(R.string.direct_media_media_play_title, R.string.direct_media_media_play_info, 1));
            listBean.add(new DirectMediaBean(R.string.direct_media_yueye_info_title, R.string.direct_media_yueye_info_info, 2));
            listBean.add(new DirectMediaBean(R.string.direct_media_music_app_title, R.string.direct_media_music_app_info, 3));
            listBean.add(new DirectMediaBean(R.string.direct_media_navi_switch_title, R.string.direct_media_navi_switch_info, 4));
            listBean.add(new DirectMediaBean(R.string.direct_media_navi_soundoff_title, R.string.direct_media_navi_soundoff_info, 6));
            listBean.add(new DirectMediaBean(R.string.direct_media_go_home_title, R.string.direct_media_go_home_info, 7));
            listBean.add(new DirectMediaBean(R.string.direct_media_go_company_title, R.string.direct_media_go_company_info, 8));
            listBean.add(new DirectMediaBean(R.string.direct_media_mobile_internet_title, R.string.direct_media_mobile_internet_info, 9));
            listBean.add(new DirectMediaBean(R.string.direct_media_dvr_switch_title, R.string.direct_media_dvr_switch_info, 10));
            listBean.add(new DirectMediaBean(R.string.direct_media_air_conditioner_title, R.string.direct_media_air_conditioner_info, 11));
            listBean.add(new DirectMediaBean(R.string.direct_media_screen_off_title, R.string.direct_media_screen_off_info, 12));
            listBean.add(new DirectMediaBean(R.string.direct_media_wireless_charging_title, R.string.direct_media_wireless_charging_info, 13));
            listBean.add(new DirectMediaBean(R.string.direct_media_system_yueyequan_title, R.string.direct_media_system_yueyequan_info, 15));
            listBean.add(new DirectMediaBean(R.string.direct_media_system_set_title, R.string.direct_media_system_set_info, 14));

        }
        return listBean;
    }

}
