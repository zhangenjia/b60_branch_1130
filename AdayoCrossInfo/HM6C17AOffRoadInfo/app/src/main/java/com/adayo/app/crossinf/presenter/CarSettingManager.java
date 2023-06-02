package com.adayo.app.crossinf.presenter;

import android.car.VehiclePropertyIds;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.adayo.app.crossinf.ActivityImpController;
import com.adayo.app.crossinf.CarConfigManager;
import com.adayo.app.crossinf.MyApplication;
import com.adayo.app.crossinf.model.VehicleDataInFo;
import com.adayo.app.crossinf.util.Constant;
import com.adayo.proxy.setting.bcm.IClientCallBack;
import com.adayo.proxy.setting.bcm.controller.BcmManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import static android.car.VehicleAreaWheel.WHEEL_LEFT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_LEFT_REAR;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_REAR;
import static android.car.VehiclePropertyIds.TIRE_POSITION;
import static android.car.VehiclePropertyIds.TIRE_PRESSURES_STATUS;
import static android.car.VehiclePropertyIds.TIRE_SYSTEM_ALARM_PROMPT;
import static android.car.VehiclePropertyIds.TIRE_SYSTEM_STATE;
import static android.car.VehiclePropertyIds.TIRE_TEMPERATURS_STATUS;
import static com.adayo.app.crossinf.util.Constant.FINISH_WADING_FUNCTION;
import static com.adayo.app.crossinf.util.Constant.START_WADING_FUNCTION;

public class CarSettingManager {

    private static CarSettingManager mCarSettingManager;
    private static final String TAG = Constant.Versiondate + CarSettingManager.class.getSimpleName();
    private static final String PackgeName = "com.adayo.app.crossinf";
    private List<String> list = new ArrayList<>();
    private BcmManager mBcmManager;
    private List<Handler> handlerList = new ArrayList<>();
    private int mLatAtsMode = -1;
    public static final int CAR_SETTING_DATA = 1006;
    private int lastTemp1 = -255;
    private int lastTemp2 = -255;
    private int lastTemp4 = -255;
    private int lastTemp8 = -255;

    private int lastPressure1 = -255;
    private int lastPressure2 = -255;
    private int lastPressure4 = -255;
    private int lastPressure8 = -255;

    private int lastSystemWarning = -255;
    private int lastLost = -255;


    public static CarSettingManager getInstance() {
        if (null == mCarSettingManager) {
            synchronized (CarSettingManager.class) {
                if (null == mCarSettingManager) {
                    mCarSettingManager = new CarSettingManager();
                }
            }
        }
        return mCarSettingManager;
    }

    public Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1001){
                int configWading_induction_sys = CarConfigManager.getInstance(MyApplication.getContext()).isConfigWading_Induction_Sys();
                if (configWading_induction_sys != 1) {
                    return;
                }
                VehicleDataInFo mVehicleDataInFo = (VehicleDataInFo) msg.obj;
                Bundle bundle = mVehicleDataInFo.getBundle();

                int properId = mVehicleDataInFo.getId();
                switch (properId) {
                    case VehiclePropertyIds.MCU_IVI_WATER_DETEC_STATUS://
                        int atsMode = bundle.getInt("IntValue");
                        Log.d(TAG, "handleMessage: atsMode " + atsMode);
                        if (mLatAtsMode == atsMode) {
                            return;
                        }
                        mLatAtsMode = atsMode;
                        if (atsMode == 0) {//not Active
                            ActivityImpController.getInstance().updateActivityStatus(FINISH_WADING_FUNCTION);
                        } else if (atsMode == 1) {// Active
                            ActivityImpController.getInstance().updateActivityStatus(START_WADING_FUNCTION);
                        }
                        break;
                }
            }else if (msg.what==1002){
                boolean ret = mBcmManager.registerListener(mCallBack, PackgeName, list);
                Log.d(TAG, "handleMessage: init ret " + ret );
                if (!ret){
                    handler.sendEmptyMessageDelayed(1002,500);
                }
            }

        }
    };


    private int lastmLfWheelSlipStatusValue = -1;
    private int lastmRfWheelSlipStatusValue = -1;
    private int lastmLrWheelSlipStatusValue = -1;
    private int lastmRrWheelSlipStatusValue = -1;
    private IClientCallBack mCallBack = new IClientCallBack.Stub() {
        @Override
        public boolean onChangeListener(int id, Bundle bundle) throws RemoteException {
            Log.d(TAG, "onChangeListener: id = " + id);

            VehicleDataInFo vehicleDataInFo = new VehicleDataInFo(id, bundle);

            int properId = vehicleDataInFo.getId();
            switch (properId) {
                case VehiclePropertyIds.TIRE_PRESSURS_VALUE://胎压
                    int mPressurevalue = bundle.getInt("IntValue");
                    int mPressureAreaID = bundle.getInt("AreaID");
                    if (mPressureAreaID == 1) {

                        if (lastPressure1 != mPressurevalue) {
                            lastPressure1 = mPressurevalue;
                        } else {
                            return true;
                        }
                    } else if (mPressureAreaID == 2) {
                        if (lastPressure2 != mPressurevalue) {
                            lastPressure2 = mPressurevalue;
                        } else {
                            return true;
                        }
                    } else if (mPressureAreaID == 4) {
                        if (lastPressure4 != mPressurevalue) {
                            lastPressure4 = mPressurevalue;
                        } else {
                            return true;
                        }
                    } else if (mPressureAreaID == 8) {
                        if (lastPressure8 != mPressurevalue) {
                            lastPressure8 = mPressurevalue;
                        } else {
                            return true;
                        }
                    }

                    break;

                case VehiclePropertyIds.TIRE_TEMPERATURE_VALUE://胎温
                    int mTemperatureValue = bundle.getInt("IntValue");
                    int mTemperatureAreaID = bundle.getInt("AreaID");
                    if (mTemperatureAreaID == 1) {

                        if (lastTemp1 != mTemperatureValue) {
                            lastTemp1 = mTemperatureValue;
                        } else {
                            return true;
                        }
                    } else if (mTemperatureAreaID == 2) {
                        if (lastTemp2 != mTemperatureValue) {
                            lastTemp2 = mTemperatureValue;
                        } else {
                            return true;
                        }
                    } else if (mTemperatureAreaID == 4) {
                        if (lastTemp4 != mTemperatureValue) {
                            lastTemp4 = mTemperatureValue;
                        } else {
                            return true;
                        }
                    } else if (mTemperatureAreaID == 8) {
                        if (lastTemp8 != mTemperatureValue) {
                            lastTemp8 = mTemperatureValue;
                        } else {
                            return true;
                        }
                    }

                    break;
                case VehiclePropertyIds.TIRE_SYSTEM_STATE://胎压系统状态
                    Log.d(TAG, "handleMessage:aa TIRE_SYSTEM_STATE  ");
                    int systemWarning = bundle.getInt("IntValue");
                    if (lastSystemWarning != systemWarning) {
                        lastSystemWarning = systemWarning;
                    } else {
                        return true;
                    }
                    break;
                case VehiclePropertyIds.TIRE_PRESSURES_STATUS://胎压报警
                    Log.d(TAG, "handleMessage:aa TIRE_PRESSURES_STATUS  ");
                    break;
                case VehiclePropertyIds.TIRE_TEMPERATURS_STATUS://胎温报警
                    Log.d(TAG, "handleMessage:aa TIRE_TEMPERATURS_STATUS  ");
                    break;
                case VehiclePropertyIds.TIRE_SYSTEM_ALARM_PROMPT://系统报警
                    Log.d(TAG, "handleMessage:aa TIRE_SYSTEM_ALARM_PROMPT  ");
                    int loss = bundle.getInt("IntValue");
                    if (lastLost != loss) {
                        lastLost = loss;
                    } else {
                        return true;
                    }
                    break;
                case VehiclePropertyIds.LF_WHEEL_SLIP_STATUS://左前轮打滑程度
                    int mLfWheelSlipStatusValue = bundle.getInt("IntValue");

                    if (lastmLfWheelSlipStatusValue!=mLfWheelSlipStatusValue){
                        lastmLfWheelSlipStatusValue = mLfWheelSlipStatusValue;
                    }else {
                        return true;
                    }

                    break;
                case VehiclePropertyIds.RF_WHEEL_SLIP_STATUS://右前轮打滑程度
                    int mRfWheelSlipStatusValue = bundle.getInt("IntValue");
                    if (lastmRfWheelSlipStatusValue!=mRfWheelSlipStatusValue){
                        lastmRfWheelSlipStatusValue = mRfWheelSlipStatusValue;
                    }else {
                        return true;
                    }

                    break;
                case VehiclePropertyIds.LR_WHEEL_SLIP_STATUS://左后轮打滑程度
                    int mLrWheelSlipStatusValue = bundle.getInt("IntValue");
                    if (lastmLrWheelSlipStatusValue!=mLrWheelSlipStatusValue){
                        lastmLrWheelSlipStatusValue = mLrWheelSlipStatusValue;
                    }else {
                        return true;
                    }

                    break;
                case VehiclePropertyIds.RR_WHEEL_SLIP_STATUS://右后轮打滑程度
                    int mRrWheelSlipStatusValue = bundle.getInt("IntValue");
                    if (lastmRrWheelSlipStatusValue!=mRrWheelSlipStatusValue){
                        lastmRrWheelSlipStatusValue = mRrWheelSlipStatusValue;
                    }else {
                        return true;
                    }
                    break;
            }

            Message message = Message.obtain();
            message.obj = vehicleDataInFo;
            message.what = 1001;
            handler.sendMessage(message);

            for (Handler handler : handlerList) {//除以上两种指定打开涉水检测页面的普通信号
                if (handler != null) {
                    Message msg = Message.obtain();
                    msg.what = CAR_SETTING_DATA;
                    msg.obj = vehicleDataInFo;
                    if (handler != null) {
                        handler.sendMessage(msg);
                    }
                }
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

    private CarSettingManager() {
        mBcmManager = BcmManager.getInstance();
        Log.d(TAG, "VehicleDataCommunication: " + mBcmManager);
        init();
    }

    private void init() {
        list.add("CarInfo");
        boolean ret = mBcmManager.registerListener(mCallBack, PackgeName, list);
        if (!ret){
            handler.sendEmptyMessageDelayed(1002,500);
        }

    }


    /**
     * 胎压
     *
     * @return
     */
    public int getTirePressure(int area) {

        int tirePressure = mBcmManager.getTirePressure(area);
        Log.d(TAG, "getTirePressure: " + tirePressure + " area = " + area);

        float finalValue1 = (float) (tirePressure * Constant.TIREPRESSURECOEFFICIENT / 100);//kpa转bar 除以100
        DecimalFormat df1 = new DecimalFormat("##.#");//输出"0.12"
        return tirePressure;
    }

    /**
     * 胎温
     *
     * @return
     */
    public int getTireTemperute(int area) {
        int tireTemperute = mBcmManager.getTireTemperute(area);
        Log.d(TAG, " area = " + area + " getTireTemperute=" + tireTemperute);
        getFrontLightEffect(area);
        return tireTemperute;
    }

    public int getFrontLightEffect(int area) {
        Log.d(TAG, "()");
        int ret = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, TIRE_POSITION, area);
        Log.d(TAG, "getFrontLightEffect = " + ret);
        return ret;
    }

    /**
     * 6 胎温胎压系统信号丢失
     *
     * @return
     */
    public int getTireLoss() {
        int tireLoss = mBcmManager.getIntTransDataMessage("CarInfo", TIRE_SYSTEM_STATE, 15);
        Log.d(TAG, " getTireLoss=" + tireLoss);
        return tireLoss;
    }

    /**
     * 胎压报警 2
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
            Log.d(TAG, "getTirePressureWarning: lfValue " + lfValue);
            return lfValue != 0;
        } else if (position == WHEEL_RIGHT_FRONT) {
            Log.d(TAG, "getTirePressureWarning: rfValue " + rfValue);
            return rfValue != 0;
        } else if (position == WHEEL_LEFT_REAR) {
            Log.d(TAG, "getTirePressureWarning: lrValue " + lrValue);
            return lrValue != 0;
        } else if (position == WHEEL_RIGHT_REAR) {
            Log.d(TAG, "getTirePressureWarning: rrValue " + rrValue);
            return rrValue != 0;
        }
        return false;
    }


    /**
     * 胎温预警 2
     *
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
     * 系统故障 5
     * <p>
     * return 1 故障
     * return 0 正常
     *
     * @return
     */
    public int isTireSystemWarning() {
        int value = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, TIRE_SYSTEM_ALARM_PROMPT, 15);
        Log.d(TAG, "getTireSystemWarning: value " + value);
        if (value == 1) {
            return 1;
        } else {
            return 0;
        }
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
        naviAroundTitlingAngle = naviAroundTitlingAngle - 120;
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
        naviPitchAngle = naviPitchAngle - 80;
        Log.d(TAG, "naviPitchAngle: " + naviPitchAngle);
        return naviPitchAngle;
    }

    /**
     * 相对高度及有效值信息
     *
     * @return
     */
    public float getNaviAltitudeHeight() {
        float naviAltitudeHeith = mBcmManager.getNaviAltitudeHeith();
        naviAltitudeHeith = Math.round(naviAltitudeHeith - 1500f);
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
        Log.d(TAG, "getNaviAtmoSphericPressure: naviAtmoSphericPressure " + naviAtmoSphericPressure);
        naviAtmoSphericPressure = Math.round(naviAtmoSphericPressure - 500f);
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

    /**
     * 当前水深
     *
     * @return
     */
    public int getWddcWadeDeepth() {
        int wddcWadeDeepth = mBcmManager.getWddcWadeDeepth();
        Log.d(TAG, "getWddcWadeDeepth: " + wddcWadeDeepth);
        return wddcWadeDeepth;
    }

    /**
     * 最大水深
     *
     * @return
     */
    public int getWddcMaxWadeDeepth() {
        int wddcMaxWadeDeepth = mBcmManager.getWddcMaxWadeDeepth();
        Log.d(TAG, "getWddcMaxWadeDeepth: " + wddcMaxWadeDeepth);
        return wddcMaxWadeDeepth;
    }

    /**
     * 水深检测系统开关
     *
     * @return
     */
    public int getWddcWadeModeStatus() {
        int wddcWadeModeStatus = mBcmManager.getWddcWadeModeStatus();
        Log.d(TAG, "getWddcWadeModeStatus: " + wddcWadeModeStatus);
        return wddcWadeModeStatus;
    }

    public void setHandler(Handler handler) {
        if (!handlerList.contains(handler)) {
            handlerList.add(handler);
        }
    }

    /**
     * 通用接口
     */
    public int getIntTransDataMessage(int VehiclePropertyIds,int i){
        int ret = mBcmManager.getIntTransDataMessage(mBcmManager.BcmDeviceCarInfo, VehiclePropertyIds, i);
        return ret;
    }

}
