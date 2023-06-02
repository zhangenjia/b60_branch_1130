package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.PhoneInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneControllerImpl extends BaseControllerImpl<PhoneInfo> implements PhoneController, IShareDataListener {
    private volatile static PhoneControllerImpl phoneController;
    private ShareDataManager shareDataManager;
    private PhoneInfo phoneInfo;

    private PhoneControllerImpl() {
        shareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static PhoneControllerImpl getInstance() {
        if (phoneController == null) {
            synchronized (PhoneControllerImpl.class) {
                if (phoneController == null) {
                    phoneController = new PhoneControllerImpl();
                }
            }
        }
        return phoneController;
    }

    private void checkSourceShareData(String shareData) {
        LogUtil.debugD(SystemUIContent.TAG, "shareData = " + shareData);
        if (!TextUtils.isEmpty(shareData)) {
            try {
                JSONObject object = new JSONObject(shareData);
                int state = object.getInt("call_state");
                if(null == phoneInfo){
                    phoneInfo = new PhoneInfo();
                }
                phoneInfo.setCallState(state);
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    public PhoneInfo getPhoneInfo() {
        if(null != phoneInfo){
            return phoneInfo;
        }
        return null;
    }

    @Override
    public void notifyShareData(int i, String s) {
        if (i == SystemUIContent.BT_CALL_SHARE_ID) {
            checkSourceShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }

    @Override
    protected boolean registerListener() {
        return shareDataManager.registerShareDataListener(SystemUIContent.BT_CALL_SHARE_ID, this);
    }

    @Override
    protected PhoneInfo getDataInfo() {
        if(null == phoneInfo){
            checkSourceShareData(shareDataManager.getShareData(SystemUIContent.BT_CALL_SHARE_ID));
        }
        return phoneInfo;
    }
}
