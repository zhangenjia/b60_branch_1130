package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.BrightnessInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.system.misc.control.MiscManager;
import com.adayo.proxy.system.misc.util.MiscConst;

import org.json.JSONException;
import org.json.JSONObject;

public class BrightnessControllerImpl extends BaseControllerImpl<BrightnessInfo> implements BrightnessController, IShareDataListener {
    private volatile static BrightnessControllerImpl mBrightnessControllerImpl;
    private BrightnessInfo brightnessInfo;

    private AAOP_DeviceServiceManager mSettingsSvcIfManager;
    private MiscManager mMiscManager;
    private ShareDataManager mShareDataManager;

    private BrightnessControllerImpl() {
        mSettingsSvcIfManager = AAOP_DeviceServiceManager.getInstance();
        mMiscManager = MiscManager.getInstance();
        mShareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static BrightnessControllerImpl getInstance() {
        if (mBrightnessControllerImpl == null) {
            synchronized (BrightnessControllerImpl.class) {
                if (mBrightnessControllerImpl == null) {
                    mBrightnessControllerImpl = new BrightnessControllerImpl();
                }
            }
        }
        return mBrightnessControllerImpl;
    }

    private void checkDisplayShareData(String shareData) {
        if (!TextUtils.isEmpty(shareData)) {
            if(null == brightnessInfo){
                brightnessInfo = new BrightnessInfo();
            }
            try {
                JSONObject object = new JSONObject(shareData);
                brightnessInfo.setBrightness(object.getInt("brightness"));
                brightnessInfo.setDayNightMode(object.getInt("day_night_mode"));
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    protected boolean registerListener() {
        return mShareDataManager.registerShareDataListener(SystemUIContent.SHARE_DATA_BRIGHTNESS_ID, this);
    }

    @Override
    protected BrightnessInfo getDataInfo() {
        if(null == brightnessInfo){
            checkDisplayShareData(mShareDataManager.getShareData(SystemUIContent.SHARE_DATA_BRIGHTNESS_ID));
        }
        return brightnessInfo;
    }

    @Override
    public int getBrightness() {
        if (null != mSettingsSvcIfManager) {
            return mSettingsSvcIfManager.getSysBacklight();
        }
        return 0;
    }

    @Override
    public void setBrightness(int brightness) {
        if (null != mSettingsSvcIfManager) {
            mSettingsSvcIfManager.setSysBacklight(brightness);
        }
    }

    @Override
    public void setBrightnessSwitch(boolean open) {
        LogUtil.debugD(SystemUIContent.TAG, "open=====" + open + " ; mMiscManager =" + mMiscManager);
        if (null != mMiscManager) {
            mMiscManager.setBackLightSwitch(!open ? MiscConst.SCREEN_BACKGROUND.SCREEN_ON : MiscConst.SCREEN_BACKGROUND.SCREEN_OFF);
        }
    }

    @Override
    public void notifyShareData(int i, String s) {
        LogUtil.debugD(SystemUIContent.TAG, "id=====" + i + " ; shareData =" + s);
        if (i == SystemUIContent.SHARE_DATA_BRIGHTNESS_ID) {
            checkDisplayShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }
}
