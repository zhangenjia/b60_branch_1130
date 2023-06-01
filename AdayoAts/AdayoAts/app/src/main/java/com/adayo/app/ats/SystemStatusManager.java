package com.adayo.app.ats;

import android.os.Message;
import android.os.RemoteException;
import android.util.Log;


import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.IAAOP_SystemServiceCallBack;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_RelyInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_ServiceInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.util.ArrayList;


public class SystemStatusManager extends IAAOP_SystemServiceCallBack.Stub {
    private volatile static SystemStatusManager systemStatusManager;
    private static final String TAG = "ATSSystemStatusManager";
    /**
     * 屏幕状态
     */
    public static final String SCREEN_MODE = "screen_mode";
    public static final int SCREEN_NORMAL = 0;//解除关屏和TOD
    public static final int SCREEN_OFF = 1;//关闭屏幕
    public static final int SCREEN_POWER_OFF = 2;//进入TOD
    public static final int SCREEN_FACKSHUT = 3;//假关机
    public static final int SCREEN_ON = 4;//解除关屏
    public static final int SCREEN_POWER_ON = 5;//解除TOD
    public static final int POWER_TYPE_DIGITAL_CLOCK = 1;
    public static final int POWER_TYPE_DIAL_CLOCK = 2;
    public static final int POWER_TYPE_BLACK = 3;
    public static final int POWER_TYPE_LOGO = 4;
    //关屏
    //待机
    private AAOP_ServiceInfoEntry myServiceInfoEntry;

    private AAOP_SystemServiceManager systemServiceManager;


    private boolean isScreenOff;
    private boolean isPowerOff;

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

    public boolean isScreenOff() {
        return isScreenOff;
    }

    public boolean isPowerOff() {
        return isPowerOff;
    }

    public int getSystemStatus() {
        return 1;
    }

    public void setSystemStatusNormal() {
        setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
    }

    private void setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS status) {

        if (null != systemServiceManager) {
            try {
                systemServiceManager.setScreenState(status);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void initSystemStatus() throws RemoteException {
        systemServiceManager = AAOP_SystemServiceManager.getInstance();
        ArrayList<AAOP_RelyInfoEntry> relyInfoEntries = new ArrayList<>();
        AAOP_RelyInfoEntry relyInfoEntry = new AAOP_RelyInfoEntry(AAOP_SystemServiceContantsDef.AAOP_SERVICE_ID.AAOP_SERVICE_BCM.getValue(), false);
        relyInfoEntries.add(relyInfoEntry);
        boolean registerRes = systemServiceManager.registerAAOPSystemServiceAPP(this, relyInfoEntries);
        Log.d(TAG, "initSystemStatus: " + registerRes);
        if (registerRes) {

            onNotifySystemState(systemServiceManager.getSystemState().getValue());
        } else {

        }
    }


    int systemPowerStatus = AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.AAOP_POWER_SHUTDOWN.getValue();


    public int getSystemPowerStatus() {

        return systemPowerStatus;
    }

    @Override
    public void onNotifySystemState(int i) {
        Log.d(TAG, "onNotifySystemState: " + i);
        updateSystemState(i);
    }

    @Override
    public void onNotifyPowerState(int i) {

    }

    @Override
    public void onNotifyRelyServiceStarted(AAOP_ServiceInfoEntry aaopServiceInfoEntry) {

        myServiceInfoEntry = aaopServiceInfoEntry;
        Message serviceInfoEntry = Message.obtain();
        serviceInfoEntry.what = 3;

    }

    private void updateSystemState(int value) {

        if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_SCREENOFF.getValue()) {
            isScreenOff = true;
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_POWEROFF.getValue()) {
            isPowerOff = true;
            isScreenOff = false;
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_FACKESHUT.getValue() ||
                value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_SHUTDOWN.getValue()) {
        } else {
            isScreenOff = false;
            isPowerOff = false;
        }
    }

}
