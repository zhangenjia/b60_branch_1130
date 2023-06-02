package com.adayo.app.setting.model.data.request.sub;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.utils.FastJsonUtil;
import com.adayo.app.setting.utils.timer.DevTimer;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.setting.system.SettingsSvcIfManager;

import java.util.Map;


public class DisplayRequest extends BaseRequest {
    private final static String TAG = DisplayRequest.class.getSimpleName();
    public static final int LIGHTNESS_MODE_AUTO = 1;
    public static final int LIGHTNESS_MODE_DAY = 2;
    public static final int LIGHTNESS_MODE_NIGHT = 3;
    private final MutableLiveData<Integer> mLightnessModeLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mLightnessLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mStandbyModeLiveData = new MutableLiveData<>();private final IShareDataListener mIShareDataInterface = (type, content) -> {
        LogUtil.debugD(TAG, "type = " + type + ", content = " + content);
        if (type == ParamConstant.SHARE_INFO_ID_DISPLAY) {
            parseLightness(content);
        }
    };


    @SuppressWarnings("ConstantConditions")
    private void parseLightness(String content) {
        LogUtil.d(TAG, "");
        Map<String, Object> map = FastJsonUtil.jsonToMap(content);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        int lightness = (int) map.get(ParamConstant.SHARE_INFO_KEY_LIGHTNESS);
        int lightnessMode = (int) map.get(ParamConstant.SHARE_INFO_KEY_LIGHTNESS_MODE);
        mLightnessLiveData.setValue(lightness);mLightnessModeLiveData.setValue(lightnessMode);LogUtil.i(TAG, "mLightnessLiveData = " + lightness + "mLightnessModeLiveData = " + lightnessMode);
    }



    private void parseStandbyMode() {
        int standbyMode = SettingsSvcIfManager.getSettingsManager().getStandbyDisplayMode();
        LogUtil.i(TAG, "Standby mode = " + standbyMode);
        switch (standbyMode) {
            case 1:
                mStandbyModeLiveData.setValue(1);
                break;
            case 4:
                mStandbyModeLiveData.setValue(4);
                break;
            case 3:
                mStandbyModeLiveData.setValue(3);
                break;
            default:
                break;
        }


    }


    public void requestLightnessModeSwitch(int lightnessMode) {
        LogUtil.i(TAG, "lightnessMode = " + lightnessMode);
        switch (lightnessMode) {
            case LIGHTNESS_MODE_AUTO:
                SettingsSvcIfManager.getSettingsManager().setDayNightMode(LIGHTNESS_MODE_AUTO);
                break;
            case LIGHTNESS_MODE_DAY:
                SettingsSvcIfManager.getSettingsManager().setDayNightMode(LIGHTNESS_MODE_DAY);
                break;
            case LIGHTNESS_MODE_NIGHT:
                SettingsSvcIfManager.getSettingsManager().setDayNightMode(LIGHTNESS_MODE_NIGHT);break;
            default:
                break;
        }
    }


    public void requestLightness(int lightness) {
        LogUtil.i(TAG, "lightness = " + lightness);
        SettingsSvcIfManager.getSettingsManager().setSysBacklight(lightness);
    }


    public void requestStandbyMode(int value) {
        LogUtil.i(TAG, "value = " + value);
        SettingsSvcIfManager.getSettingsManager().setStandbyDisplayMode(value);
        mStandbyModeLiveData.setValue(value);

    }

    public void init() {
        LogUtil.debugD(TAG, "");
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_DISPLAY, mIShareDataInterface);
        if (b) {
            parseLightness(ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_DISPLAY));

        } else {
            initRegisterLOUDNESS();
        }
        parseStandbyMode();
    }

    private void initRegisterLOUDNESS() {
        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_DISPLAY, mIShareDataInterface);
                if (b) {
                    devTimer.stop();
                    parseLightness(ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_DISPLAY));
                }
                if (end) {
                    LogUtil.w(TAG, "register SHARE_INFO_ID_LOUDNESS 10 time fail");
                }
            }
        });
        devTimer.start();
    }

    public void unInit() {
        LogUtil.debugD(TAG, "");
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_DISPLAY, mIShareDataInterface);
    }

    public LiveData<Integer> getLightnessModeLiveData() {
        if (mLightnessModeLiveData.getValue() == null) {
            mLightnessModeLiveData.setValue(0);
        }
        return mLightnessModeLiveData;
    }

    public LiveData<Integer> getLightnessLiveData() {
        if (mLightnessLiveData.getValue() == null) {
            mLightnessLiveData.setValue(0);
            LogUtil.w(TAG, " mLightnessLiveData     is null");
        }
        return mLightnessLiveData;
    }


    public LiveData<Integer> getStandbyModeLiveData() {
        if (mStandbyModeLiveData.getValue() == null) {
            mStandbyModeLiveData.setValue(0);
        }
        return mStandbyModeLiveData;
    }
}
