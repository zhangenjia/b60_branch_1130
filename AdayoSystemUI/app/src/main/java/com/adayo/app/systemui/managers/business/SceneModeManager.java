package com.adayo.app.systemui.managers.business;

import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.ModeInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SceneModeManager extends BaseControllerImpl<ModeInfo> implements IShareDataListener {

    private volatile static SceneModeManager sceneModeManager;
    private ShareDataManager shareDataManager;
    private ModeInfo modeInfo;

    private SceneModeManager() {
        shareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static SceneModeManager getInstance() {
        if (sceneModeManager == null) {
            synchronized (SceneModeManager.class) {
                if (sceneModeManager == null) {
                    sceneModeManager = new SceneModeManager();
                }
            }
        }
        return sceneModeManager;
    }

    private void checkSourceShareData(String shareData) {
        if (!TextUtils.isEmpty(shareData)) {
            try {
                if(null == modeInfo){
                    modeInfo = new ModeInfo();
                }
                JSONObject object = new JSONObject(shareData);
                modeInfo.setDrag_mode(object.getBoolean("drag_mode"));
                LogUtil.debugI(SystemUIContent.TAG, "drag_mode = " + modeInfo.isDrag_mode());
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    protected boolean registerListener() {
        return shareDataManager.registerShareDataListener(SystemUIContent.SHARE_DATA_DRAG_MODE_SHARE_DATA_ID, this);
    }

    @Override
    protected ModeInfo getDataInfo() {
        if(null == modeInfo){
            checkSourceShareData(shareDataManager.getShareData(SystemUIContent.SHARE_DATA_DRAG_MODE_SHARE_DATA_ID));
        }
        return modeInfo;
    }

    @Override
    public void notifyShareData(int i, String s) {
        if(SystemUIContent.SHARE_DATA_DRAG_MODE_SHARE_DATA_ID == i){
            checkSourceShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }
}
