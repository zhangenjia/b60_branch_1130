package com.adayo.app.setting.language;

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

import java.util.Locale;
import java.util.Map;


public class LanguageRequest extends BaseRequest {
    private final static String TAG = LanguageRequest.class.getSimpleName();
    private final MutableLiveData<Locale> mLanguageLiveData = new MutableLiveData<>();
    private final IShareDataListener mIShareDataInterface = (type, content) -> {
        LogUtil.debugD(TAG, "type = " + type + ", content = " + content);
        if (type == ParamConstant.SHARE_INFO_ID_GENERAL) {parseLang(content);}
    };



    private void parseLang(String json) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        int i = (int) map.get(ParamConstant.SHARE_INFO_KEY_LANGUAGE);LogUtil.i(TAG, "i = " + i);
        switch (i) {
            case 1:
                mLanguageLiveData.setValue(Locale.SIMPLIFIED_CHINESE);break;
            case 2:
                mLanguageLiveData.setValue(Locale.ENGLISH);break;
            default:
                break;
        }
    }



    public void requestLanguageSwitch(Locale locale) {
        LogUtil.i(TAG, "locale = " + locale);
        if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {
            mLanguageLiveData.setValue(Locale.SIMPLIFIED_CHINESE);
            SettingsSvcIfManager.getSettingsManager().setSysLanguage(1);
            LogUtil.debugD(TAG, "CHINESE");
        } else if (Locale.ENGLISH.equals(locale)) {
            mLanguageLiveData.setValue(Locale.ENGLISH);
            SettingsSvcIfManager.getSettingsManager().setSysLanguage(2);
            LogUtil.debugD(TAG, "ENGLISH");
        }
    }

    public void init() {
        LogUtil.debugD(TAG, "");
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);
        if (b) {
            String shareData = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_GENERAL);
            parseLang(shareData);
        }else {
        initRegisterLOUDNESS();}

    }

    private void initRegisterLOUDNESS() {
        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);

                if (b) {
                    devTimer.stop();
                    String shareData = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_GENERAL);
                    parseLang(shareData);
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
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);
    }


    public LiveData<Locale> getLanguageLiveData() {
        if (mLanguageLiveData.getValue() == null) {
            mLanguageLiveData.setValue(Locale.SIMPLIFIED_CHINESE);
        }
        return mLanguageLiveData;
    }
}
