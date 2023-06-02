package com.adayo.app.systemui.managers.business;

import android.media.AudioManager;
import android.text.TextUtils;

import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.SystemUISourceInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngAudioSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SourceControllerImpl extends BaseControllerImpl<SystemUISourceInfo> implements SourceController, IShareDataListener {
    private volatile static SourceControllerImpl sourceController;
    private ShareDataManager shareDataManager;
    private SrcMngSwitchManager srcMngSwitchManager;
    private SrcMngAudioSwitchManager srcMngAudioSwitchManager;
    private SystemUISourceInfo sourceInfo = new SystemUISourceInfo();

    private SourceControllerImpl() {
        shareDataManager = ShareDataManager.getShareDataManager();
        srcMngSwitchManager = SrcMngSwitchManager.getInstance();
        srcMngAudioSwitchManager = SrcMngAudioSwitchManager.getInstance();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static SourceControllerImpl getInstance() {
        if (sourceController == null) {
            synchronized (SourceControllerImpl.class) {
                if (sourceController == null) {
                    sourceController = new SourceControllerImpl();
                }
            }
        }
        return sourceController;
    }

    private void checkSourceShareData(String shareData) {
        LogUtil.debugD(SystemUIContent.TAG, "shareData = " + shareData);
        if (!TextUtils.isEmpty(shareData)) {
            try {
                JSONObject object = new JSONObject(shareData);
                String uid = object.getString("UID");
                String audioId = object.getString("AudioID");
                if (null == sourceInfo.getUiSource() || null == sourceInfo.getAudioSource() || !uid.equals(sourceInfo.getUiSource()) || !audioId.equals(sourceInfo.getAudioSource())) {
                    sourceInfo.setUiSource(object.getString("UID"));
                    sourceInfo.setAudioSource(object.getString("AudioID"));
                    LogUtil.debugI(SystemUIContent.TAG, "UID = " + sourceInfo.getUiSource() + ";AudioID = " + sourceInfo.getAudioSource());
                }
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    public String getCurrentUISource() {
        if (null != srcMngSwitchManager) {
            return srcMngSwitchManager.getCurrentUID();
        }
        return AdayoSource.ADAYO_SOURCE_HOME;
    }

    @Override
    public List<String> getCurrentAudioSource() {
        if (null != srcMngSwitchManager) {
            return srcMngSwitchManager.getCurrentAudioFocus();
        }
        return null;
    }

    public Stack<String> getAudioSources() {
        if (null != srcMngSwitchManager) {
            return srcMngSwitchManager.getAudioStackInfo();
        }
        return null;
    }

    @Override
    public void requestSource(int sourceSwitch, String sourceType, Map<String, String> map) {
        String currentUISource = SourceControllerImpl.getInstance().getCurrentUISource();
        if(!"ADAYO_SOURCE_APA".equals(sourceType) && (AdayoSource.ADAYO_SOURCE_AVM.equals(currentUISource)
                || "ADAYO_SOURCE_APA".equals(currentUISource) || AdayoSource.ADAYO_SOURCE_RVC.equals(currentUISource))){
            return;
        }
        try {
            SourceInfo info = new SourceInfo(sourceType, map, sourceSwitch,
                    AppConfigType.SourceType.UI.getValue());
            SrcMngSwitchManager srcMngSwitchManager = SrcMngSwitchManager.getInstance();
            if(null != srcMngSwitchManager) {
                srcMngSwitchManager.requestSwitchApp(info);
            }
        } catch (Exception e) {
            LogUtil.w(SystemUIContent.TAG, e.getMessage());
        }
    }

    @Override
    public void requestAdayoAudioFocus(String audioFocus,IAdayoAudioFocusChange iAdayoAudioFocusChange) {
        if (null != srcMngAudioSwitchManager) {
            synchronized (srcMngAudioSwitchManager) {
                LogUtil.debugD(SystemUIContent.TAG, audioFocus);
                srcMngAudioSwitchManager.requestAdayoAudioFocus(AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT, audioFocus, iAdayoAudioFocusChange,
                        SystemUIApplication.getSystemUIContext());
            }
        }
    }

    @Override
    public void abandonAdayoAudioFocus(IAdayoAudioFocusChange iAdayoAudioFocusChange) {
        if (null != iAdayoAudioFocusChange) {
            if (null != srcMngAudioSwitchManager) {
                synchronized (srcMngAudioSwitchManager) {
                    srcMngAudioSwitchManager.abandonAdayoAudioFocus(iAdayoAudioFocusChange);
                }
            }
        }
    }

    @Override
    public void notifyShareData(int i, String s) {
        if (SystemUIContent.SHARE_DATA_SOURCE_MANAGER_ID == i) {
            checkSourceShareData(s);
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    }

    @Override
    protected boolean registerListener() {
        return shareDataManager.registerShareDataListener(SystemUIContent.SHARE_DATA_SOURCE_MANAGER_ID, this);
    }

    @Override
    protected SystemUISourceInfo getDataInfo() {
        if(null == sourceInfo){
            checkSourceShareData(shareDataManager.getShareData(SystemUIContent.SHARE_DATA_SOURCE_MANAGER_ID));
        }
        return sourceInfo;
    }
}
