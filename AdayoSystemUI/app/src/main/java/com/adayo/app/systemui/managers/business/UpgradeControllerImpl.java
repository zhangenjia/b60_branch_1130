package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.UpgradeInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author XuYue
 * @description:
 * @date :2022/2/8 13:52
 */
public class UpgradeControllerImpl extends BaseControllerImpl<UpgradeInfo> implements UpgradeController, IShareDataListener {
    private volatile static UpgradeControllerImpl mUpgradeController;
    private ShareDataManager mShareDataManager;
    private UpgradeInfo upgradeInfo;

    private UpgradeControllerImpl() {
        mShareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static UpgradeControllerImpl getInstance() {
        if (mUpgradeController == null) {
            synchronized (UpgradeControllerImpl.class) {
                if (mUpgradeController == null) {
                    mUpgradeController = new UpgradeControllerImpl();
                }
            }
        }
        return mUpgradeController;
    }

    @Override
    protected boolean registerListener() {
        return mShareDataManager.registerShareDataListener(SystemUIContent.SHARE_DATA_UPGRADE_ID, this);
    }

    @Override
    protected UpgradeInfo getDataInfo() {
        return upgradeInfo;
    }

    @Override
    public UpgradeInfo getUpgradeData() {
        return upgradeInfo;
    }

    @Override
    public void notifyShareData(int i, String s) {
        if (SystemUIContent.SHARE_DATA_UPGRADE_ID == i) {
            checkShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }

    private void checkShareData(String shareData) {
        LogUtil.debugD(SystemUIContent.TAG, "shareData =" + shareData);
        if (!TextUtils.isEmpty(shareData)) {
            if(null == upgradeInfo){
                upgradeInfo = new UpgradeInfo();
            }
            try {
                JSONObject object = new JSONObject(shareData);
                upgradeInfo.setUpgradeState(object.getInt("upgrade_state"));
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }
}
