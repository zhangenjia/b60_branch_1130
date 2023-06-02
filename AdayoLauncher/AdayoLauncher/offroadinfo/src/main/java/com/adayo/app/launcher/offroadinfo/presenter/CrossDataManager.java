package com.adayo.app.launcher.offroadinfo.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.app.launcher.offroadinfo.model.bean.VehicleDataInFo;
import com.adayo.app.launcher.offroadinfo.util.Constant;
import com.adayo.proxy.setting.bcm.IClientCallBack;
import com.adayo.proxy.setting.bcm.controller.BcmManager;
import java.util.ArrayList;
import java.util.List;

import static android.car.VehicleAreaWheel.WHEEL_LEFT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_LEFT_REAR;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_REAR;
//import static android.car.VehiclePropertyIds.TIRE_POSITION;
import static android.car.VehiclePropertyIds.TIRE_PRESSURES_STATUS;
import static android.car.VehiclePropertyIds.TIRE_SYSTEM_ALARM_PROMPT;
import static android.car.VehiclePropertyIds.TIRE_SYSTEM_STATE;
import static android.car.VehiclePropertyIds.TIRE_TEMPERATURS_STATUS;

public class CrossDataManager {
    public static final int CAR_SETTING_DATA = 1006;
    private static CrossDataManager mCrossDataManager;
    private static final String TAG = Constant.Versiondate + CrossDataManager.class.getSimpleName();
    private static final String PackgeName = "com.adayo.app.launcher.offroadinfo";

    private List<String> list = new ArrayList<>();
    private BcmManager mBcmManager;
    private Handler handler = null;
    private final int retry = 1001;
    private final int BCM_ENABLE = 1002;
    private final int INIT_BCMDATA = 1003;
    private boolean bcmDateEnable;
    private Handler retryHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case retry:
                    boolean success = mBcmManager.registerListener(mCallBack, PackgeName, list);
                    Log.d(TAG, "init==============>222 : " + success);
                    if (success) {
                        if (runnable != null) {
                            retryHandler.removeCallbacks(runnable);
                            Log.d(TAG, "success   removeCallbacks");
                        }
                        Message message = Message.obtain();
                        message.what = BCM_ENABLE;
                        retryHandler.sendMessageDelayed(message, 6000);
                    } else {
                        retryHandler.postDelayed(runnable, 500);
                        Log.d(TAG, "failed  removeCallbacks");
                    }
                    break;
                case BCM_ENABLE:
                    bcmDateEnable = true;
                    break;
                case INIT_BCMDATA:
                    Log.d(TAG, "handleMessage: INIT_BCMDATA"+bcmDateEnable);
                    if (bcmDateEnable){
                        if (onBcmConnectListener!=null){
                            onBcmConnectListener.bcmConnect();
                        }
                    }else {
                        Message message = Message.obtain();
                        message.what = INIT_BCMDATA;
                        retryHandler.sendMessageDelayed(message, 500);
                    }

                    break;
            }

        }
    };
    private OnBcmConnectListener onBcmConnectListener;


    public static CrossDataManager getInstance() {
        if (null == mCrossDataManager) {
            synchronized (CrossDataManager.class) {
                if (null == mCrossDataManager) {
                    mCrossDataManager = new CrossDataManager();
                }
            }
        }
        return mCrossDataManager;
    }

    private CrossDataManager( ) {

    }

    private IClientCallBack mCallBack = new IClientCallBack.Stub() {

        @Override
        public boolean onChangeListener(int id, Bundle bundle) throws RemoteException {
            Log.d(TAG, "onChangeListener = " + id + "   bcmDateEnable = " +bcmDateEnable );
            if (!bcmDateEnable){

                return true;
            }
            if (handler != null) {
                VehicleDataInFo vehicleDataInFo = new VehicleDataInFo(id, bundle);
                Message msg = Message.obtain();
                msg.what = CAR_SETTING_DATA;
                msg.obj = vehicleDataInFo;
                handler.sendMessage(msg);
            }
            return false;
        }

        @Override
        public boolean onErrorEvent(int i, int i1) throws RemoteException {
            Log.d(TAG, "onErrorEvent(): i = " + i + " i1 = " + i1);
            return false;
        }

        @Override
        public boolean isConnectCarProperty(boolean b) throws RemoteException {
            Log.d(TAG, "isConnectCarProperty():b = " + b);
            return false;
        }

    };


    public void init() {
        mBcmManager = BcmManager.getInstance();
        list.add("CarInfo");
        boolean success = mBcmManager.registerListener(mCallBack, PackgeName, list);
        Log.d(TAG, "VehicleDataPresenter: " + mBcmManager+" "+success+" "+onBcmConnectListener);
        if (success) {
                Message message = Message.obtain();
                message.what = BCM_ENABLE;
                retryHandler.sendMessageDelayed(message, 0);
                Log.d(TAG, "init: init success");

        } else {
            retryHandler.postDelayed(runnable, 200);
        }

    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            retryHandler.sendEmptyMessage(retry);
        }
    };


    /**
     * 胎压
     *
     * @return
     */
    public int getTirePressure(int area) {
//        Log.d(TAG, "getTirePressure: ");
        int tirePressure = mBcmManager.getTirePressure(area);
        Log.d(TAG, "getTirePressure: " + tirePressure + " area = " + area);
        return tirePressure;
    }

    /**
     * 胎温
     *
     * @return
     */
    public int getTireTemperute(int area) {
        int tireTemperute = mBcmManager.getTireTemperute(area);
        Log.d(TAG, "getTireTemperute: " + tireTemperute);
        return tireTemperute;
    }

//    public int getFrontLightEffect (int area) {
//        Log.d(TAG, "()");
//        int ret = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, TIRE_POSITION, area);
//        Log.d(TAG, "getFrontLightEffect = " + ret);
//        return ret;
//    }

    public int getTireLoss() {
        int tireLoss = mBcmManager.getIntTransDataMessage("CarInfo", TIRE_SYSTEM_STATE, 15);
        Log.d(TAG, " getTireLoss=" + tireLoss);
        return tireLoss;
    }

    /**
     * 胎压报警
     * TPMS_St_LRPWarning
     * 0 ：无警告
     * 1 ：无传感器
     * 2 ：快漏气
     * 3 ：高压报警
     * 4 ：低压报警
     * 5 ：传感器电池低
     * 6 ：传感器异常
     * 7 ：无法使用
     *
     * @param position
     * @return true 警告 false 正常
     */
    public boolean isTirePressureWarning(@TireColorStrategyManager.TirePosition int position) {

        int frontValue = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, TIRE_PRESSURES_STATUS, 3);
        int lfValue = frontValue & 0b111;
        int rfValue = frontValue >> 3 & 0b111;
        int rearValue = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, TIRE_PRESSURES_STATUS, 10);
        int lrValue = rearValue & 0b111;
        int rrValue = rearValue >> 3 & 0b111;

        if (position == WHEEL_LEFT_FRONT) {
            Log.d(TAG, "getTirePressureWarning: lfValue " +position+"  "+ lfValue);
            return lfValue != 0;
        } else if (position == WHEEL_RIGHT_FRONT) {
            Log.d(TAG, "getTirePressureWarning: rfValue " +position+"  "+ rfValue);
            return rfValue != 0;
        } else if (position == WHEEL_LEFT_REAR) {
            Log.d(TAG, "getTirePressureWarning: lrValue " +position+"  "+ lrValue);
            return lrValue != 0;
        } else if (position == WHEEL_RIGHT_REAR) {
            Log.d(TAG, "getTirePressureWarning: rrValue " +position+"  "+ rrValue);
            return rrValue != 0;
        }
        return false;
    }


    /**
     * 胎温预警
     * @param position
     * @return
     */
    public boolean isTireTempWarning(@TireColorStrategyManager.TirePosition int position) {
        int value = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, TIRE_TEMPERATURS_STATUS, 15);
        int lfValue = value & 0b11;
        int rfValue = value >> 2 & 0b11;
        int lrValue = value >> 4 & 0b11;
        int rrValue = value >> 6 & 0b11;

        if (position == WHEEL_LEFT_FRONT) {
            Log.d(TAG, "getTireTempWarning: lfValue " + lfValue);
            return lfValue == 1;
        } else if (position == WHEEL_RIGHT_FRONT) {
            Log.d(TAG, "getTireTempWarning: rfValue " + rfValue);
            return rfValue == 1;
        } else if (position == WHEEL_LEFT_REAR) {
            Log.d(TAG, "getTireTempWarning: lrValue " + lrValue);
            return lrValue == 1;
        } else if (position == WHEEL_RIGHT_REAR) {
            Log.d(TAG, "getTireTempWarning: rrValue " + rrValue);
            return rrValue == 1;
        }
        return false;
    }

    /**
     * 胎压系统警告
     * @return
     */
    public boolean isTireSystemWarning(){
        int value = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, TIRE_SYSTEM_ALARM_PROMPT, 15);
        Log.d(TAG, "getTireSystemWarning: value " + value);
        return value == 1;
    }

    /**
     * 前轮转向 //todo
     */
    public int getFrontWheelAngle() {

        return -1;
    }

    /**
     * 分动箱状态
     *
     * @return
     */
    public int getTCCUStatus() {
        int tccuStatus = mBcmManager.getTCCUStatus();
        Log.d(TAG, "getTCCUStatus: " + tccuStatus);
        return tccuStatus;
    }

    /**
     * 前差速锁状态
     *
     * @return
     */
    public int getFrontEdsLock() {
        int frontEdsLock = mBcmManager.getFrontEdsLock();
        Log.d(TAG, "getFrontEdsLock: " + frontEdsLock);
        return frontEdsLock;
    }

    /**
     * 后差速锁状态
     *
     * @return
     */
    public int getRearEdsLock() {
        int rearEdsLock = mBcmManager.getRearEdsLock();
        Log.d(TAG, "getRearEdsLock: " + rearEdsLock);
        return rearEdsLock;
    }

    /**
     * 左前轮打滑程度
     *
     * @return
     */
    public int getLFSlipStatus() {
        int lfSlipStatus = mBcmManager.getLFSlipStatus();
        Log.d(TAG, "getLFSlipStatus: " + lfSlipStatus);
        return lfSlipStatus;
    }

    /**
     * 右前轮打滑程度
     *
     * @return
     */
    public int getRFSlipStatus() {
        int rfSlipStatus = mBcmManager.getRFSlipStatus();
        Log.d(TAG, "getRFSlipStatus: " + rfSlipStatus);
        return rfSlipStatus;
    }

    /**
     * 左后轮打滑程度
     *
     * @return
     */
    public int getLRSlipStatus() {
        int lrSlipStatus = mBcmManager.getLRSlipStatus();
        Log.d(TAG, "getLRSlipStatus: " + lrSlipStatus);
        return lrSlipStatus;
    }

    /**
     * 右后轮打滑程度
     *
     * @return
     */
    public int getRRSlipStatus() {
        int rrSlipStatus = mBcmManager.getRRSlipStatus();
        Log.d(TAG, "getRRSlipStatus: " + rrSlipStatus);
        return rrSlipStatus;
    }

    /**
     * 水平角及有效值信息
     *
     * @return
     */
    public int getNaviAroundTitlingAngle() {
        int naviAroundTitlingAngle = mBcmManager.getNaviAroundTitlingAngle();
        Log.d(TAG, "getNaviAroundTitlingAngle: " + naviAroundTitlingAngle);
        return naviAroundTitlingAngle;
    }

    /**
     * 俯仰角及有效值信息
     *
     * @return
     */
    public int getNaviPitchAngle() {
        int naviPitchAngle = mBcmManager.getNaviPitchAngle();
        return naviPitchAngle;
    }

    /**
     * 相对高度及有效值信息
     *
     * @return
     */
    public float getNaviAltitudeHeight() {
        float naviAltitudeHeith = mBcmManager.getNaviAltitudeHeith();
        Log.d(TAG, "getNaviAltitudeHeith: " + naviAltitudeHeith);
        return naviAltitudeHeith;
    }

    /**
     * 大气压及有效值信息
     *
     * @return
     */
    public float getNaviAtmoSphericPressure() {
        float naviAtmoSphericPressure = mBcmManager.getNaviAtmoSphericPressure();
        Log.d(TAG, "getNaviAtmoSphericPressure: " + naviAtmoSphericPressure);
        return naviAtmoSphericPressure;
    }

    /**
     * 指南针方向及有效值信息
     *
     * @return
     */
    public int getNaviCompassDirection() {
        int naviCompassDirection = mBcmManager.getNaviCompassDirection();
        Log.d(TAG, "getNaviCompassDirection: " + naviCompassDirection);
        return naviCompassDirection;
    }

    /**
     * 指南针角度及有效值信息
     *
     * @return
     */
    public float getNaviCompassSig() {
        float naviCompassSig = mBcmManager.getNaviCompassSig();
        Log.d(TAG, "getNaviCompassSig: " + naviCompassSig);
        return naviCompassSig;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setOnBcmConnectListener(OnBcmConnectListener onBcmConnectListener) {
        Log.d(TAG, "setOnBcmConnectListener: "+onBcmConnectListener);
        this.onBcmConnectListener = onBcmConnectListener;
        Message message = Message.obtain();
        message.what = INIT_BCMDATA;
        retryHandler.sendMessageDelayed(message, 500);
    }

    public interface OnBcmConnectListener {
        void bcmConnect();
    }

}
