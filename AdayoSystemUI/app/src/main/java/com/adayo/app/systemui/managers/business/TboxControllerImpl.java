package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.TboxInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class TboxControllerImpl extends BaseControllerImpl<TboxInfo> implements TboxController, IShareDataListener {
    private volatile static TboxControllerImpl tboxController;
    private ShareDataManager shareDataManager;
    private TboxInfo tboxInfo;

    private TboxControllerImpl() {
        shareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static TboxControllerImpl getInstance() {
        if (tboxController == null) {
            synchronized (TboxControllerImpl.class) {
                if (tboxController == null) {
                    tboxController = new TboxControllerImpl();
                }
            }
        }
        return tboxController;
    }

    private void checkSourceShareData(String shareData) {
        if (!TextUtils.isEmpty(shareData)) {
            try {
                if(null == tboxInfo){
                    tboxInfo = new TboxInfo();
                }
                JSONObject object = new JSONObject(shareData);
                tboxInfo.setSignal(object.getInt("TBOX_SIGNAL"));
                tboxInfo.setType(object.getInt("TBOX_TYPE"));
                LogUtil.debugI(SystemUIContent.TAG, "TBOX_SIGNAL = " + tboxInfo.getSignal() + " ; TBOX_TYPE = " + tboxInfo.getType());
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    public int getTboxSignal() {
        if(null == tboxInfo){
            checkSourceShareData(shareDataManager.getShareData(SystemUIContent.SHARE_DATA_TBOX_SHARE_DATA_ID));
        }
        return tboxInfo.getSignal();
    }

    @Override
    public int getTboxType() {
        if(null == tboxInfo){
            checkSourceShareData(shareDataManager.getShareData(SystemUIContent.SHARE_DATA_TBOX_SHARE_DATA_ID));
        }
        return tboxInfo.getType();
    }

    @Override
    public void notifyShareData(int i, String s) {
        if(SystemUIContent.SHARE_DATA_TBOX_SHARE_DATA_ID == i){
            checkSourceShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }

    @Override
    protected boolean registerListener() {
        return shareDataManager.registerShareDataListener(SystemUIContent.SHARE_DATA_TBOX_SHARE_DATA_ID, this);
    }

    @Override
    protected TboxInfo getDataInfo() {
        if(null == tboxInfo){
            checkSourceShareData(shareDataManager.getShareData(SystemUIContent.SHARE_DATA_TBOX_SHARE_DATA_ID));
        }
        return tboxInfo;
    }
}
