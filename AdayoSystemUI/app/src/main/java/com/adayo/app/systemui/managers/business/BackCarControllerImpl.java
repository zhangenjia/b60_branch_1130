package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.BackCarInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class BackCarControllerImpl extends BaseControllerImpl<BackCarInfo> implements BackCarController, IShareDataListener {
    private volatile static BackCarControllerImpl backCarController;
    private ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
    private BackCarInfo backCarInfo;

    private BackCarControllerImpl() {
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static BackCarControllerImpl getInstance() {
        if (backCarController == null) {
            synchronized (BackCarControllerImpl.class) {
                if (backCarController == null) {
                    backCarController = new BackCarControllerImpl();
                }
            }
        }
        return backCarController;
    }

    private void checkBackCarShareData(String shareData) {
        LogUtil.debugD(SystemUIContent.TAG, "shareData = " + shareData);
        if (!TextUtils.isEmpty(shareData)) {
            LogUtil.d(SystemUIContent.TAG, shareData);
            try {
                if(null == backCarInfo){
                    backCarInfo = new BackCarInfo();
                }
                JSONObject object = new JSONObject(shareData);
                boolean state = object.getBoolean("backCarState");
                if (state != backCarInfo.isBackCar()) {
                    backCarInfo.setBackCar(state);
                }
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    public boolean getBackCarState() {
        if(null != backCarInfo) {
            return backCarInfo.isBackCar();
        }
        return false;
    }

    @Override
    public void notifyShareData(int i, String s) {
        if (i == SystemUIContent.BACK_CAR_SHARE_ID) {
            checkBackCarShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }

    @Override
    protected boolean registerListener() {
        return shareDataManager.registerShareDataListener(SystemUIContent.BACK_CAR_SHARE_ID, this);
    }

    @Override
    protected BackCarInfo getDataInfo() {
        if(null == backCarInfo){
            checkBackCarShareData(shareDataManager.getShareData(SystemUIContent.BACK_CAR_SHARE_ID));
        }
        return backCarInfo;
    }
}
