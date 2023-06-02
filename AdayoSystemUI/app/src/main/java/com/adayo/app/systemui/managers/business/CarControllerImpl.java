package com.adayo.app.systemui.managers.business;

import static android.car.VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.BCMInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.setting.bcm.IClientCallBack;
import com.adayo.proxy.setting.bcm.controller.BcmManager;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.app.systemui.configs.HvacContent.VEHICLE_CAN_TO_IVI_BAT_VOLTAGE;
import static com.adayo.app.systemui.configs.SystemUIContent.CAR_INFO;
import static com.adayo.app.systemui.configs.SystemUIContent.DEVICE_INFO;
import static com.adayo.app.systemui.configs.SystemUIContent.PEPS_POWER_MODE;
import static com.adayo.app.systemui.configs.SystemUIContent.TAG;


public class CarControllerImpl extends BaseControllerImpl<BCMInfo> implements CarController {

    private volatile static CarControllerImpl carController;
    private BcmManager mBcmManager;
    private BCMInfo bcmInfo = new BCMInfo();
    private boolean hasInitData = false;

    private List<String> bcmDeviceList = new ArrayList<>();


    private CarControllerImpl() {
        mBcmManager = BcmManager.getInstance();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static CarControllerImpl getInstance() {
        if (carController == null) {
            synchronized (CarControllerImpl.class) {
                if (carController == null) {
                    carController = new CarControllerImpl();
                }
            }
        }
        return carController;
    }

    private IClientCallBack callBack = new IClientCallBack.Stub() {
        @Override
        public boolean onChangeListener(final int vehiclePropertyId, final Bundle bundle) throws RemoteException {
            switch (vehiclePropertyId){
                case PEPS_POWER_MODE:
                    LogUtil.debugI(TAG, "vehiclePropertyId = " + vehiclePropertyId + " ; value = " + bundle.getInt("IntValue", 0));
                    bcmInfo.setCarPowerStatus(bundle.getInt("IntValue", 0));
                    mHandler.removeMessages(NOTIFY_CALLBACKS);
                    mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    break;
                case VEHICLE_CAN_TO_IVI_BAT_VOLTAGE:
                    LogUtil.debugI(TAG, "vehiclePropertyId = " + vehiclePropertyId + " ; value = " + bundle.getFloat("FloatValue", 0.0f));
                    bcmInfo.setBatteryVoltage(bundle.getFloat("FloatValue", 0.0f));
                    mHandler.removeMessages(NOTIFY_CALLBACKS);
                    mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    break;
                case EMS_EMS5_ST_ENGINERUNNING:
                    LogUtil.debugI(TAG, "vehiclePropertyId = " + vehiclePropertyId + " ; value = " + bundle.getInt("IntValue", 0));
                    int value = bundle.getInt("IntValue", 0);
                    bcmInfo.setNewEngineStatus(value == 255 ? 1 : value);
                    mHandler.removeMessages(NOTIFY_CALLBACKS);
                    mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onErrorEvent(int i, int i1) throws RemoteException {
            return false;
        }

        @Override
        public boolean isConnectCarProperty(boolean isConnect) throws RemoteException {
            return false;
        }
    };

    public void setData(int vehicleId, int areaId, int value){
        mBcmManager.setIntTransDataMessage("DeviceInfo", vehicleId, areaId, value);
    }

    public int getPowerData() {
        int ret = mBcmManager.getIntTransDataMessage("DeviceInfo", PEPS_POWER_MODE, 0);
        LogUtil.debugD(SystemUIContent.TAG, "ret = " + ret);
        bcmInfo.setCarPowerStatus(ret);
        return ret;
    }

    public float getBatteryVoltage(){
        float batteryVoltage = mBcmManager.getFloatTransDataMessage("DeviceInfo", VEHICLE_CAN_TO_IVI_BAT_VOLTAGE, 0);
        LogUtil.debugD(SystemUIContent.TAG, "batteryVoltage = " + batteryVoltage);
        bcmInfo.setBatteryVoltage(batteryVoltage);
        return batteryVoltage;
    }

    public int getNewEngineStatus() {
        int ret = BcmManager.getInstance().getIntTransDataMessage("CarInfo", EMS_EMS5_ST_ENGINERUNNING, 0);
        LogUtil.debugD(TAG, "getNewEngineStatus: "+ret);
        bcmInfo.setNewEngineStatus(ret);
        if (ret != 0 && ret != 1){
            mRetryHandler.removeMessages(RETRY_GET);
            mRetryHandler.sendEmptyMessageDelayed(RETRY_GET, 100);
        }
        return ret;
    }

    private final int RETRY_GET = 20001;
    protected Handler mRetryHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case RETRY_GET:
                    int status = getNewEngineStatus();
                    if (status == 0 || status == 1){
                        mHandler.removeMessages(NOTIFY_CALLBACKS);
                        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected boolean registerListener() {
        bcmDeviceList.add(DEVICE_INFO);
        bcmDeviceList.add(CAR_INFO);
        return mBcmManager.registerListener(callBack, SystemUIContent.PACKAGE_NAME + 1, bcmDeviceList);
    }

    @Override
    protected BCMInfo getDataInfo() {
        if(!hasInitData) {
            getBatteryVoltage();
            getPowerData();
            hasInitData = true;
        }
        return bcmInfo;
    }
}
