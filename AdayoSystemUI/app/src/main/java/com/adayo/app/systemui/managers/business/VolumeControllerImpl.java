package com.adayo.app.systemui.managers.business;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.VolumeInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.GsonUtils;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

public class VolumeControllerImpl extends BaseControllerImpl<VolumeInfo> implements VolumeController, IShareDataListener {
    private volatile static VolumeControllerImpl mVolumeControllerImpl;
    private AAOP_DeviceServiceManager mSettingsSvcIfManager;
    private ShareDataManager mShareDataManager;

    private VolumeInfo volumeInfo;

    private VolumeControllerImpl() {
        mSettingsSvcIfManager = AAOP_DeviceServiceManager.getInstance();
        mShareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static VolumeControllerImpl getInstance() {
        if (mVolumeControllerImpl == null) {
            synchronized (VolumeControllerImpl.class) {
                if (mVolumeControllerImpl == null) {
                    mVolumeControllerImpl = new VolumeControllerImpl();
                }
            }
        }
        return mVolumeControllerImpl;
    }

    @Override
    protected boolean registerListener() {
        return mShareDataManager.registerShareDataListener(SystemUIContent.SHARE_DATA_VOLUME_ID, this);
    }

    @Override
    protected VolumeInfo getDataInfo() {
        if(null == volumeInfo){
            volumeInfo = GsonUtils.jsonToObject(mShareDataManager.getShareData(SystemUIContent.SHARE_DATA_VOLUME_ID), VolumeInfo.class);
        }
        return volumeInfo;
    }

    @Override
    public int getVolume(int volumeType) {
        if (null != mSettingsSvcIfManager) {
            return mSettingsSvcIfManager.getAudioStreamVolume(volumeType);
        }
        return 0;
    }

    @Override
    public void setVolume(int volumeType, int volume) {
        if (null != mSettingsSvcIfManager) {
            mSettingsSvcIfManager.setAudioStreamVolume(volumeType, volume);
        }
    }

    @Override
    public int getSysMute() {
        if (null != mSettingsSvcIfManager) {
            return mSettingsSvcIfManager.getSysMute();
        }
        return 0;
    }

    @Override
    public void setSysMute(int mute) {
        if (null != mSettingsSvcIfManager) {
            mSettingsSvcIfManager.setSysMute(mute);
        }
    }

    @Override
    public void notifyShareData(int i, String s) {
        LogUtil.d(SystemUIContent.TAG, "s = " + s);
        if (i == SystemUIContent.SHARE_DATA_VOLUME_ID) {
            volumeInfo = GsonUtils.jsonToObject(s, VolumeInfo.class);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }
}
