package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.USBInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class USBControllerImpl extends BaseControllerImpl<USBInfo> implements USBController, IShareDataListener {
    private volatile static USBControllerImpl mUSBControllerImpl;
    private ShareDataManager mShareDataManager;
    private USBInfo usbInfo;


    private USBControllerImpl() {
        mShareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static USBControllerImpl getInstance() {
        if (mUSBControllerImpl == null) {
            synchronized (USBControllerImpl.class) {
                if (mUSBControllerImpl == null) {
                    mUSBControllerImpl = new USBControllerImpl();
                }
            }
        }
        return mUSBControllerImpl;
    }

    private void checkUsbShareData(String shareData) {
        LogUtil.debugI(SystemUIContent.TAG, "shareData = " + shareData);
        if (!TextUtils.isEmpty(shareData)) {
            try {
                JSONObject object = new JSONObject(shareData);
                if(null == usbInfo){
                    usbInfo = new USBInfo();
                }
                usbInfo.setDeviceNum(object.getInt("isDevice"));
                usbInfo.setMountNum(object.getInt("isMount"));
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    public int getUSBDeviceNum() {
        if(null == usbInfo){
            return 0;
        }
        return usbInfo.getDeviceNum();
    }

    @Override
    public int getUSBMountNum() {
        if(null == usbInfo){
            return 0;
        }
        return usbInfo.getMountNum();
    }

    @Override
    public boolean isUSBDevice() {
        return getUSBDeviceNum() > 0 ? true : false;
    }

    @Override
    public boolean isUSBMount() {
        return getUSBMountNum() > 0 ? true : false;
    }

    @Override
    public void notifyShareData(int type, String shareDate) {
        if (type == SystemUIContent.SHARE_DATA_USB_DEVICE_ID) {
            checkUsbShareData(shareDate);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }

    @Override
    protected boolean registerListener() {
        return mShareDataManager.registerShareDataListener(SystemUIContent.SHARE_DATA_USB_DEVICE_ID, this);
    }

    @Override
    protected USBInfo getDataInfo() {
        if(null == usbInfo){
            checkUsbShareData(mShareDataManager.getShareData(SystemUIContent.SHARE_DATA_USB_DEVICE_ID));
        }
        return usbInfo;
    }
}
