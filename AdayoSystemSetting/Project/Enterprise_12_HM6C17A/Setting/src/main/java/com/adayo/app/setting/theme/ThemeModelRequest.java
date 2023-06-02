package com.adayo.app.setting.theme;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.utils.FastJsonUtil;
import com.adayo.app.setting.utils.timer.DevTimer;
import com.adayo.proxy.aaop_hskin.listener.ILoadSkinListener;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.setting.system.SettingsSvcIfManager;

import java.util.Map;

public class ThemeModelRequest extends BaseRequest implements ILoadSkinListener {
    private String TAG = ThemeModelRequest.class.getName();
    private final MutableLiveData<Integer> mThemeModeLiveData = new MutableLiveData<>();private final MutableLiveData<Boolean> mLoadThemeModeLiveData = new MutableLiveData<>();private final IShareDataListener mIShareDataInterface = (type, content) -> {
        LogUtil.debugD(TAG, "type = " + type + ", content = " + content);
        if (type == ParamConstant.SHARE_INFO_ID_GENERAL) {parseThemeMode(content);
        }
    };

    public void init() {
        AAOP_HSkinHelper.setLoadSkinListener(this);
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);

        if (b) {
            parseThemeMode(ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_GENERAL));

        } else {
            initRegisterTheme();
        }
    }

    private void initRegisterTheme() {
        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);
                if (b) {
                    devTimer.stop();
                    parseThemeMode(ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_GENERAL));
                }
                if (end) {
                    LogUtil.w(TAG, "register SHARE_INFO_ID_LOUDNESS 10 time fail");
                }
            }
        });
        devTimer.start();
    }

    public void unInit() {
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);

    }


    private void parseThemeMode(String content) {
        LogUtil.d(TAG, "");
        Map<String, Object> map = FastJsonUtil.jsonToMap(content);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        int theme = (int) map.get(ParamConstant.SHARE_INFO_KEY_THEME_MODE);
        LogUtil.i(TAG, "mThemeModeLiveData = " + theme);
        mThemeModeLiveData.setValue(theme);

    }


    public void requestThemeMode(int value) {
        LogUtil.i(TAG, "value = " + value);
        SettingsSvcIfManager.getSettingsManager().setSystemThemeMode(value);

    }

    public void requestLoadThemeMode(boolean load) {
        LogUtil.i(TAG, "load = " + load);
        mLoadThemeModeLiveData.setValue(load);

    }

    public LiveData<Integer> getThemeModeLiveData() {
        if (mThemeModeLiveData.getValue() == null) {
            mThemeModeLiveData.setValue(0);
        }
        return mThemeModeLiveData;
    }

    public MutableLiveData<Boolean> getLoadThemeModeLiveData() {
        if (mLoadThemeModeLiveData.getValue() == null) {
            mLoadThemeModeLiveData.setValue(true);
        }
        return mLoadThemeModeLiveData;
    }

    @Override
    public void onLoadStart(String s) {
        LogUtil.d(TAG, "S =" + s);
    }

    @Override
    public void onLoadSuccess(String s) {
        LogUtil.d(TAG, "S =" + s);
        mLoadThemeModeLiveData.setValue(true);

    }

    @Override
    public void onLoadFail(String s) {
        LogUtil.d(TAG, "S =" + s);
    }
}
