package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.DVRInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class DvrControllerImpl extends BaseControllerImpl<DVRInfo> implements DvrController, IShareDataListener {

    private volatile static DvrControllerImpl dvrController;
    private ShareDataManager shareDataManager;
    private DVRInfo dvrInfo;

    private DvrControllerImpl() {
        shareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static DvrControllerImpl getInstance() {
        if (dvrController == null) {
            synchronized (DvrControllerImpl.class) {
                if (dvrController == null) {
                    dvrController = new DvrControllerImpl();
                }
            }
        }
        return dvrController;
    }

    private void checkShareData(String shareData) {
        LogUtil.debugD(SystemUIContent.TAG, "shareData =" + shareData);
        if (!TextUtils.isEmpty(shareData)) {
            if(null == dvrInfo){
                dvrInfo = new DVRInfo();
            }
            try {
                JSONObject object = new JSONObject(shareData);
                dvrInfo.setDvrState(object.getInt("dvr_state"));
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    public int getDvrState() {
        return dvrInfo.getDvrState();
    }

    private String shareDate = "";
    @Override
    public void notifyShareData(int i, String s) {
        if (SystemUIContent.DVR_SHARE_ID == i && !shareDate.equals(s)) {
            shareDate = s;
            checkShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }

    @Override
    protected boolean registerListener() {
        return shareDataManager.registerShareDataListener(SystemUIContent.DVR_SHARE_ID, this);
    }

    @Override
    protected DVRInfo getDataInfo() {
        if(null == dvrInfo){
            checkShareData(shareDataManager.getShareData(SystemUIContent.DVR_SHARE_ID));
        }
        return dvrInfo;
    }
}
