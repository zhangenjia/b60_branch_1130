package com.adayo.app.systemui.managers;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;

import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.ScreenCallback;
import com.adayo.app.systemui.bean.SystemInfo;
import com.adayo.app.systemui.bean.WiFiInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.AudioFocusUtils;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.utils.SPHelper;
import com.adayo.app.systemui.windows.panels.ScreenSaver;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.IAAOP_SystemServiceCallBack;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_RelyInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_ServiceInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.util.ArrayList;

import static com.adayo.app.systemui.configs.SystemUIConfigs.IS_NEED_SCREENT_MUTE;
import static com.adayo.app.systemui.configs.SystemUIContent.POWER_TYPE_BLACK;
import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

/**
 * @author XuYue
 * @description:
 * @date :2021/10/20 10:47
 */
public class SystemStatusManager extends IAAOP_SystemServiceCallBack.Stub implements ScreenCallback {
    private volatile static SystemStatusManager systemStatusManager;
    private static final String DISPLAY_DEVICE = "DisplayDevice";
    private static final String SET_BACK_LIGHT_SWITCH = "set_back_light_switch";
    private static final int SET_BACK_LIGHT_SWITCH_ON = 0;
    private static final int SET_BACK_LIGHT_SWITCH_OFF = 1;

    private final ArrayList<ServiceStatusCallBack> mCallbacks = new ArrayList<>();
    private AAOP_ServiceInfoEntry myServiceInfoEntry;

    private AAOP_SystemServiceManager systemServiceManager;
    private SystemInfo systemInfo = new SystemInfo();
    private ScreenSaver screenSaver;

    private SystemStatusManager() {
        try {
            initSystemStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static SystemStatusManager getInstance() {
        if (systemStatusManager == null) {
            synchronized (SystemStatusManager.class) {
                if (systemStatusManager == null) {
                    systemStatusManager = new SystemStatusManager();
                }
            }
        }
        return systemStatusManager;
    }

    public int getSystemStatus() {
        return systemInfo.getSystemStatus();
    }

    public void setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS status) {
        LogUtil.debugD(SystemUIContent.TAG, status.getValue() + "");
        if (null != systemServiceManager) {
            try {
                systemServiceManager.setScreenState(status);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * AAOP_POWER_STATUS
     *
     * @return
     * AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_INVALID.getValue();
     * AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_SHUTDOWN.getValue();
     * AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_FATESHUT.getValue();
     * AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_PERSTART.getValue();
     * AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_ACC.getValue();
     * AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_ACCON.getValue();
     */
    int systemPowerStatus = AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_SHUTDOWN.getValue();
    public int getSystemPowerStatus() {
        LogUtil.debugI(SystemUIContent.TAG, "systemPowerStatus = " + systemPowerStatus);
        return systemPowerStatus;
    }

    private void initScreenSaver() {
        if (null == screenSaver) {
            screenSaver = ScreenSaver.getInstance();
            screenSaver.setScreenCallback(this);
        }
    }

    private void initSystemStatus() throws RemoteException {
        systemServiceManager = AAOP_SystemServiceManager.getInstance();
        ArrayList<AAOP_RelyInfoEntry> relyInfoEntries = new ArrayList<>();
        AAOP_RelyInfoEntry relyInfoEntry = new AAOP_RelyInfoEntry(AAOP_SystemServiceContantsDef.AAOP_SERVICE_ID.AAOP_SERVICE_BCM.getValue(), false);
        relyInfoEntries.add(relyInfoEntry);
        boolean registerRes = systemServiceManager.registerAAOPSystemServiceAPP(this, relyInfoEntries);
        if(registerRes) {
            onNotifySystemState(systemServiceManager.getSystemState().getValue());
        }else {
            mHandler.sendEmptyMessageDelayed(RETRY_REGISTER, 1000);
        }
        LogUtil.debugD(SystemUIContent.TAG, "System Status = " + systemInfo.getSystemStatus() + " ; registerRes = " + registerRes);
    }

    private final int UPDATE_UI = 10001;
    private final int NOTIFY_SERVICE_STATUS = 10002;
    private final int RETRY_REGISTER = 10003;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    updateViews(msg.arg1);
                    break;
                case NOTIFY_SERVICE_STATUS:
                    notifyServiceStatus();
                    break;
                case RETRY_REGISTER:
                    try {
                        initSystemStatus();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void notifyServiceStatus(){
        synchronized (lock) {
            for (ServiceStatusCallBack mCallBack : mCallbacks) {
                notifyCallback(mCallBack);
            }
        }
    }

    private void notifyCallback(ServiceStatusCallBack callBack){
        callBack.notifyServiceStatus(myServiceInfoEntry);
    }

    private static final  Object lock = new Object();
    public void addCallback(ServiceStatusCallBack callBack) {
        synchronized (lock) {
            if (null != callBack && !mCallbacks.contains(callBack)) {
                mCallbacks.add(callBack);
                notifyCallback(callBack);
            }
        }
    }

    private void updateViews(int value){
        int systemStatus;
        if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_SCREENOFF.getValue()) {
            systemStatus = SystemUIContent.SCREEN_OFF;
            WindowsManager.setQsPanelVisibility(View.GONE);
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_POWEROFF.getValue()) {
            systemStatus = SystemUIContent.SCREEN_POWER_OFF;
            WindowsManager.setQsPanelVisibility(View.GONE);
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_FACKESHUT.getValue() ||
                value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_SHUTDOWN.getValue()) {
            systemStatus = SystemUIContent.SCREEN_FACKSHUT;
            WindowsManager.setQsPanelVisibility(View.GONE);
        } else {
            systemStatus = SystemUIContent.SCREEN_NORMAL;
        }
        systemInfo.setSystemStatus(systemStatus);
        if (SystemUIContent.SCREEN_NORMAL != systemStatus) {
            initScreenSaver();
            screenSaver.show(systemStatus);
        } else {
            if (null != screenSaver) {
                screenSaver.dismiss();
            }
        }
        changeStatus();
    }

    @Override
    public void onNotifySystemState(int i) {
        LogUtil.debugI(SystemUIContent.TAG, "System Status = " + i);
        Message msgVolume = Message.obtain();
        msgVolume.what = UPDATE_UI;
        msgVolume.arg1 = i;
        mHandler.sendMessage(msgVolume);
    }

    @Override
    public void onNotifyPowerState(int i) {
        LogUtil.debugI(SystemUIContent.TAG, "Power Status = " + i);
        systemPowerStatus = i;
    }

    @Override
    public void onNotifyRelyServiceStarted(AAOP_ServiceInfoEntry aaop_serviceInfoEntry) {
        LogUtil.debugI(SystemUIContent.TAG, "aaop_serviceInfoEntry = " + aaop_serviceInfoEntry);
        myServiceInfoEntry = aaop_serviceInfoEntry;
        Message serviceInfoEntry = Message.obtain();
        serviceInfoEntry.what = NOTIFY_SERVICE_STATUS;
        mHandler.sendMessage(serviceInfoEntry);
    }

    @Override
    public void setScreenOn() {
        setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
    }

    public void changeStatus() {
        LogUtil.debugI(SystemUIContent.TAG, "getSystemStatus = " + getSystemStatus());
        switch (getSystemStatus()) {
            case SystemUIContent.SCREEN_OFF:
                if(IS_NEED_SCREENT_MUTE) {
                    AAOP_DeviceServiceManager.getInstance().setSysMuteLock(SystemUIContent.SETTING_LOCK, SystemUIContent.SETTING_LOCK_ID_CLOCK, SystemUIContent.SETTING_MUTE);
                }
                setBackLightSwitch(SET_BACK_LIGHT_SWITCH_OFF);
                break;
            case SystemUIContent.SCREEN_POWER_OFF:
                AAOP_DeviceServiceManager.getInstance().setSysMuteLock(SystemUIContent.SETTING_LOCK, SystemUIContent.SETTING_LOCK_ID_CLOCK, SystemUIContent.SETTING_MUTE);
                AudioFocusUtils.abandonAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_FAKESHUT);
                AudioFocusUtils.requestAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_CLOCK);
                if(AAOP_DeviceServiceManager.getInstance().getStandbyDisplayMode() == POWER_TYPE_BLACK){
                    setBackLightSwitch(SET_BACK_LIGHT_SWITCH_OFF);
                }else{
                    setBackLightSwitch(SET_BACK_LIGHT_SWITCH_ON);
                }
                break;
            case SystemUIContent.SCREEN_FACKSHUT:
                AAOP_DeviceServiceManager.getInstance().setSysMuteLock(SystemUIContent.SETTING_LOCK, SystemUIContent.SETTING_LOCK_ID_CLOCK, SystemUIContent.SETTING_MUTE);
                AudioFocusUtils.abandonAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_CLOCK);
                AudioFocusUtils.requestAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_FAKESHUT);
                break;
            default:
                AAOP_DeviceServiceManager.getInstance().setSysMuteLock(SystemUIContent.SETTING_UNLOCK, SystemUIContent.SETTING_LOCK_ID_CLOCK, SystemUIContent.SETTING_UNMUTE);
                AudioFocusUtils.abandonAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_FAKESHUT);
                AudioFocusUtils.abandonAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_CLOCK);
                setBackLightSwitch(SET_BACK_LIGHT_SWITCH_ON);
                break;
        }
    }

    private void setBackLightSwitch(int status){
        Bundle param = new Bundle();
        param.putInt(SET_BACK_LIGHT_SWITCH, status);
        int requestReturn = AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface(DISPLAY_DEVICE, SET_BACK_LIGHT_SWITCH, param);
        LogUtil.debugI(TAG, "requestReturn = " + requestReturn);
    }

    public interface ServiceStatusCallBack{
        void notifyServiceStatus(AAOP_ServiceInfoEntry aaop_serviceInfoEntry);
    }
}
