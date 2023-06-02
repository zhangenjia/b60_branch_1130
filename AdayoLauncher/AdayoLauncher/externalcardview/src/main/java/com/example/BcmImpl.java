package com.example;

import android.car.VehiclePropertyIds;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.setting.bcm.IClientCallBack;
import com.adayo.proxy.setting.bcm.controller.BcmManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BcmImpl {

    private static final String TAG = "BcmImpl";
    private static BcmImpl mBcmImpl;
    private BcmManager mBcmManager;
    private List<String> list = new ArrayList<>();
    private static final String PackgeName = "com.adayo.app.launcher";
    private int newPowerMode;
    private int newEngineStatus;
    private OnBcmDataChangeListener onBcmDataChangeListener;
    private final int MSG_POWER_MODE = 1001;
    private final int MSG_ENGINERUNNING = 1002;
    private final int MSG_RETRY = 1003;
    private final int MSG_POWER_MODE_GETVALID = 1004;
    private final int MSG_ENGINERUNNING_GETVALID = 1005;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_POWER_MODE:
                    notifyDataChange(VehiclePropertyIds.PEPS_POWER_MODE);
                    break;
                case MSG_ENGINERUNNING:
                    notifyDataChange(VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING);
                    break;
                case MSG_RETRY:
                    boolean success = mBcmManager.registerListener(mCallBack, PackgeName, list);
                    Log.d(TAG, "handleMessage: " + MSG_RETRY);
                    if (!success) {
                        mHandler.sendEmptyMessageDelayed(MSG_RETRY, 1000);
                    }
                    break;
                case MSG_POWER_MODE_GETVALID:
                    int powerStatus = getPowerStatus();
                    if (powerStatus == 0 || powerStatus == 1 || powerStatus == 2 || powerStatus == 3 || powerStatus == 4 || powerStatus == 5) {
                        notifyDataChange(VehiclePropertyIds.PEPS_POWER_MODE);
                    }

                    break;

                case MSG_ENGINERUNNING_GETVALID:
                    int newEngineStatus = getNewEngineStatus();
                    if (newEngineStatus == 0 || newEngineStatus == 1) {
                        notifyDataChange(VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING);
                    }

                    break;
            }

        }
    };
    private List<OnBcmDataChangeListener> listenerList = new ArrayList<>();
    private String drag_mode ;

    public static BcmImpl getInstance() {
        if (null == mBcmImpl) {
            synchronized (BcmImpl.class) {
                if (null == mBcmImpl) {
                    mBcmImpl = new BcmImpl();
                }
            }
        }
        return mBcmImpl;
    }

    private BcmImpl() {
        ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
        String shareData = shareDataManager.getShareData(157);
        drag_mode = checkSourceShareData(shareData);
        if (drag_mode==null){
            drag_mode="false";
        }
        Log.d(TAG, "BcmImpl: drag_mode = " + drag_mode);
        shareDataManager.registerShareDataListener(157, new IShareDataListener() {
            @Override
            public void notifyShareData(int z, String s) {
                drag_mode = checkSourceShareData(s);
                if (drag_mode==null){
                    drag_mode="false";
                }
                Log.d(TAG, "BcmImpl: notifyShareData drag_mode = " + drag_mode);
                for (int i = 0; i < listenerList.size(); i++) {
                    OnBcmDataChangeListener onBcmDataChangeListener = listenerList.get(i);
                    if (onBcmDataChangeListener!=null){
                        onBcmDataChangeListener.onTrailerModeStatusChange();
                    }


                }
            }
        });
    }

    public int getPowerStatus() {

        int ret = BcmManager.getInstance().getIntTransDataMessage("DeviceInfo", VehiclePropertyIds.PEPS_POWER_MODE, 0);
        Log.d(TAG, "getPowerStatus: " + ret);

        if (ret != 0 && ret != 1 && ret != 2 && ret != 3 && ret != 4 && ret != 5) {
            mHandler.removeMessages(MSG_POWER_MODE_GETVALID);
            mHandler.sendEmptyMessageDelayed(MSG_POWER_MODE_GETVALID, 100);
        } else {
            mHandler.removeMessages(MSG_POWER_MODE_GETVALID);
            newPowerMode = ret;
        }


        return ret;
    }

    public int getNewEngineStatus() {

        int ret = BcmManager.getInstance().getIntTransDataMessage("CarInfo", VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING, 0);
        Log.d(TAG, "getNewEngineStatus: " + ret);

        if (ret != 0 && ret != 1) {
            mHandler.removeMessages(MSG_ENGINERUNNING_GETVALID);
            mHandler.sendEmptyMessageDelayed(MSG_ENGINERUNNING_GETVALID, 100);
        } else {
            mHandler.removeMessages(MSG_ENGINERUNNING_GETVALID);
            newEngineStatus = ret;
        }
        return ret;
    }

    public void init() {
        list.add("DeviceInfo");
        list.add("CarInfo");

        mBcmManager = BcmManager.getInstance();
        boolean success = mBcmManager.registerListener(mCallBack, PackgeName, list);
        if (!success) {
            mHandler.sendEmptyMessageDelayed(MSG_RETRY, 1000);
        }
        Log.d(TAG, "init: isSuccess  " + success);
    }

    private int lastPowerMode = -1;
    private int lastEngineStatus = -1;
    private IClientCallBack mCallBack = new IClientCallBack.Stub() {

        @Override
        public boolean onChangeListener(int id, Bundle bundle) throws RemoteException {

            switch (id) {
                case VehiclePropertyIds.PEPS_POWER_MODE:
                    newPowerMode = bundle.getInt("IntValue");
                    if (lastPowerMode != newPowerMode) {
                        lastPowerMode = newPowerMode;
                        mHandler.sendEmptyMessage(MSG_POWER_MODE);
                    }
                    Log.d(TAG, "BcmImpl onChangeListener  powerMode  = " + newPowerMode);
                    break;
                case VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING:
                    newEngineStatus = bundle.getInt("IntValue");
                    if (lastEngineStatus != newEngineStatus) {
                        lastEngineStatus = newEngineStatus;
                        mHandler.sendEmptyMessage(MSG_ENGINERUNNING);
                    }

                    Log.d(TAG, " BcmImpl onChangeListener  engineStatus  = " + newEngineStatus);
                    break;

            }
            return false;
        }

        @Override
        public boolean onErrorEvent(int i, int i1) throws RemoteException {
            return false;
        }

        @Override
        public boolean isConnectCarProperty(boolean b) throws RemoteException {
            return false;
        }
    };

    public interface OnBcmDataChangeListener {

        void powerStatusChange(int value);

        void engineStatusChange(int value);

        void onTrailerModeStatusChange();
    }

    public void setOnBcmDataChangeListener(OnBcmDataChangeListener onBcmDataChangeListener, int i) {
        Log.d(TAG, "setOnBcmDataChangeListener: " + i);
        this.onBcmDataChangeListener = onBcmDataChangeListener;
        listenerList.add(onBcmDataChangeListener);
    }

    public void notifyDataChange(int id) {
        Log.d(TAG, "notifyDataChange===>: " + listenerList.size());
        for (int i = 0; i < listenerList.size(); i++) {
            OnBcmDataChangeListener onBcmDataChangeListener = listenerList.get(i);
            if (VehiclePropertyIds.PEPS_POWER_MODE == id) {
                Log.d(TAG, "notifyDataChange===> newPowerMode: " + newPowerMode);
                onBcmDataChangeListener.powerStatusChange(newPowerMode);

            } else if (VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING == id) {
                Log.d(TAG, "notifyDataChange===> newEngineStatus: " + newEngineStatus);
                onBcmDataChangeListener.engineStatusChange(newEngineStatus);
            }
        }
    }

    private String checkSourceShareData(String shareData) {

        if (!TextUtils.isEmpty(shareData)) {
            try {
                JSONObject object = new JSONObject(shareData);
                drag_mode = object.getString("drag_mode");

            } catch (JSONException e) {

            }
        }
        return drag_mode;
    }

    public String getTrailerMode() {

        Log.d(TAG, "getTrailerMode: "+drag_mode);
        return drag_mode;
    }
}
