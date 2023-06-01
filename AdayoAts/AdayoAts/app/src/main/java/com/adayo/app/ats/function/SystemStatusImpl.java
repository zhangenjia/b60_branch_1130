package com.adayo.app.ats.function;

import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.IAAOP_SystemServiceCallBack;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_RelyInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_ServiceInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.proxy.infrastructure.input.AdayoKeyEvent.KEYEVENT_ACTION_LONGPRESS;

public class SystemStatusImpl {
    private static final String TAG = "ATS" + SystemStatusImpl.class.getSimpleName();

    private volatile static SystemStatusImpl mSystemStatusControllerImpl;

    private AAOP_SystemServiceManager mSystemServiceManager;

    private ArrayList<AAOP_RelyInfoEntry> mRelyInfoList = new ArrayList<>();

    private List<Object> params = new ArrayList<>();

    private volatile int screenStatus = 1;
    private boolean isPowerOff;//待机
    private boolean isScreenOff;//关屏

    private int status = -1;

    private SystemStatusImpl() {
        Log.d(TAG, "SystemStatusImpl()");

        mSystemServiceManager = AAOP_SystemServiceManager.getInstance();
        try {
            status = mSystemServiceManager.getSystemState().getValue();
            updateSystemState(status);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        mSystemServiceManager.registerAAOPSystemServiceAPP(mSystemServiceCallBack, mRelyInfoList);//注册系统状态
    }

    public static SystemStatusImpl getInstance() {
        if (mSystemStatusControllerImpl == null) {
            synchronized (SystemStatusImpl.class) {
                if (mSystemStatusControllerImpl == null) {
                    mSystemStatusControllerImpl = new SystemStatusImpl();
                }
            }
        }
        return mSystemStatusControllerImpl;
    }

    public boolean isPowerOff() {
        return isPowerOff;
    }

    public boolean isScreenOff(){
        return isScreenOff;
    }

    public void setScreenOn() {
        boolean isSuccess = false;
        if (null != mSystemServiceManager) {
            try {
                isSuccess = mSystemServiceManager.setScreenState(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
                Log.d(TAG, "setScreenOn: isSuccess = "+isSuccess);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }



    IAAOP_SystemServiceCallBack.Stub mSystemServiceCallBack = new IAAOP_SystemServiceCallBack.Stub() {
        @Override
        public void onNotifySystemState(int state) throws RemoteException {//系统状态
            Log.d(TAG, "onNotifySystemState: state = " + state);
            updateSystemState(state);
        }

        @Override
        public void onNotifyPowerState(int state) throws RemoteException {//车身电源状态
            Log.d(TAG, "onNotifyPowerState: state = " + state);
        }

        @Override
        public void onNotifyRelyServiceStarted(AAOP_ServiceInfoEntry aaopserviceInfoEntry) throws RemoteException {

        }
    };

    private void updateSystemState(int state) {
        if (state == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_NORMAL.getValue()) {//4
            Log.d(TAG, "NORMAL状态:");
            isPowerOff = false;
            isScreenOff = false;
        } else if (state == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_POWEROFF.getValue()) {//6
            Log.d(TAG, "待机状态：");
            isPowerOff = true;
            isScreenOff = true;
        } else if (state == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_SCREENOFF.getValue()) {//5
            Log.d(TAG, "关屏状态：");
            isPowerOff = false;
            isScreenOff = true;
        } else if (state == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_FACKESHUT.getValue()) {//7
            Log.d(TAG, "假关机状态：");
            isPowerOff = false;
            isScreenOff = true;
        }else {//其他情况暂时按照 不是待机 不是关屏处理  关机状态不需要关注，按这个处理也没有问题
            isPowerOff = false;
            isPowerOff = false;
        }
    }

}
