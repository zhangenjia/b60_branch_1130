package com.adayo.app.camera;


import android.car.VehiclePropertyIds;
import android.content.Intent;
import android.hardware.camera_rgb.V1_0.ICamera_rgb;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.app.camera.controlbehavior.AvmApaControlBehavior;
import com.adayo.app.camera.controlbehavior.AvmRoadCalibrationBehavior;
import com.adayo.app.camera.controlbehavior.RvcControlBehavior;
import com.adayo.app.camera.controlbehavior.RvcRadarBehavior;
import com.adayo.app.camera.controlbehavior.UpgradeControlBehavior;
import com.adayo.configurationinfo.ConfigurationWordInfo;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.config.Config;
import com.adayo.proxy.aaop_camera.signalmodel.SignalModel;
import com.adayo.proxy.aaop_camera.utils.SystemUtils;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceBase;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_RelyInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainService extends AAOP_SystemServiceBase {

    private static final String TAG = "MainService";
    public static final int APA = 0;
    public static final int RVC_HIGH = 1;
    public static final int RVC_LOW = 2;
    public static int POWER_TYPE = 0;
    public static final int POWER_TYPE_DIGITAL_CLOCK = 1;
    public static final int POWER_TYPE_DIAL_CLOCK = 2;
    public static final int POWER_TYPE_BLACK = 3;
    public static final int POWER_TYPE_LOGO = 4;

    public static boolean hasFront = true;

    BackCarBinder binder = new BackCarBinder();
    ConfigurationWordInfo info;
    int cameraAppType = -1;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void init() {
        Log.i("AdayoCamera", TAG + " - " + "init() called");
        produceAppType();
        initGetConfig();
        Settings.Global.putInt(getApplicationContext().getContentResolver(),"dvrShow",0);
        Log.i("AdayoCamera", TAG + " - init: cameraAppType = " + cameraAppType);
        initFastMode();
        initFinish();
    }

    @Override
    public IBinder getIBinder() {
        return null;
    }

    @Override
    public AAOP_SystemServiceContantsDef.AAOP_SERVICE_ID getServiceID() {
        return AAOP_SystemServiceContantsDef.AAOP_SERVICE_ID.AAOP_SERVICE_CAMERA;
    }

    @Override
    public ArrayList<AAOP_SystemServiceContantsDef.AAOP_SERVICE_ID> getRelyList() {
        return null;
    }

    @Override
    public void onAAOP_SystemServiceConnect() {

    }

    @Override
    public void onAAOP_SystemServiceDisconnect() {

    }

    @Override
    public void onNotifySystemState(AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS aaop_system_status) {
        Log.d(TAG, "onNotifySystemState: aaop_system_status.getValue"+aaop_system_status.getValue());
        if(aaop_system_status.getValue() == 5 ){
            Log.d(TAG, "onNotifySystemState: setPullup = ture");
            setPullup(true);
        }else if (aaop_system_status.getValue() == 6){
            POWER_TYPE =  AAOP_DeviceServiceManager.getInstance().getStandbyDisplayMode();
            if(POWER_TYPE_BLACK == POWER_TYPE  ){
                setStandby(true);
                Log.d(TAG, "onNotifySystemState: setStandby = ture");
            }
        } else {
            Log.d(TAG, "onNotifySystemState: setPullup = false");
            setPullup(false);
            Log.d(TAG, "onNotifySystemState: setStandby = false");
            setStandby(false);
        }


    }

    @Override
    public void onNotifyRelyServiceStarted(ArrayList<AAOP_RelyInfoEntry> arrayList) {

    }

    @Override
    public void onNotifyPreStart() {

    }

    @Override
    public void onNotifyACC() {

    }

    @Override
    public void onNotifyACCON() {

    }

    @Override
    public void onNotifyFakeShutdown() {

    }

    @Override
    public void onNotifyShutdown() {
    }

    Intent  mIntent;
    int mFlags;
    int mStartId;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AdayoCamera", TAG + " - onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        if (!MainService.isStartFinish()){
            mIntent = intent;
            mFlags = flags;
            mStartId = startId;
            Log.i("AdayoCamera", TAG + " - onStartCommand() called with:   MainService.isStartFinish  = ["+MainService.isStartFinish+"], mIntent = [" + mIntent + "], mflags = [" + mFlags + "], mStartId = [" + mStartId + "]");
            initFinishBehavior();
            return super.onStartCommand(intent, flags, startId);
        }
        if (intent != null && APA == cameraAppType) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Map map = (HashMap) bundle.get("map");
                if (map != null) {
                    String value1 = (String) map.get("SourceType");          //ADAYO_SOURCE_AVM / ADAYO_SOURCE_APA/ADAYO_SOURCE_RVC
                    if (AdayoSource.ADAYO_SOURCE_AVM.equals(value1)) {
                        if (map.containsKey("AVM_ROAD_CALIBRATION")) {
                            String isRoad = (String) map.get("AVM_ROAD_CALIBRATION");
                            if ("AVM_ROAD_CALIBRATION".equals(isRoad)) {
                                Log.i("AdayoCamera", TAG + " - onStartCommand() called with: start ROAD");
                                AAOP_Camera.sendEvent(EventIds.ROAD_CALIBRATION_DISPLAY);
                                return super.onStartCommand(intent, flags, startId);
                            }
                        }
                        if (map.containsKey("VOICE_CONTROL")) {
                            String control = (String) map.get("VOICE_CONTROL");
                            if ("AVM_ON".equals(control)) {
                                Log.i("AdayoCamera", TAG + " - onStartCommand: VOICE_CONTROL AVM_ON");
                                AAOP_Camera.sendEvent(EventIds.AVM_DISPLAY);
                                return super.onStartCommand(intent, flags, startId);
                            }

                            if ("AVM_OFF".equals(control)) {
                                Log.i("AdayoCamera", TAG + " - onStartCommand: VOICE_CONTROL AVM_OFF");
                                AAOP_Camera.sendEvent(EventIds.AVM_HIDE);
                                return super.onStartCommand(intent, flags, startId);
                            }
                        }
                        Log.i("AdayoCamera", TAG + " - onStartCommand() called with: start AVM");
                        AAOP_Camera.sendEvent(EventIds.AVM_DISPLAY);
                        return super.onStartCommand(intent, flags, startId);
                    }

                    if (AdayoSource.ADAYO_SOURCE_APA.equals(value1)) {
                        if (map.containsKey("VOICE_CONTROL")) {
                            String control = (String) map.get("VOICE_CONTROL");
                            if ("APA_ON".equals(control)) {
                                Log.i("AdayoCamera", TAG + " - onStartCommand: VOICE_CONTROL APA_ON");
                                AAOP_Camera.sendEvent(EventIds.APA_DISPLAY);
                                return super.onStartCommand(intent, flags, startId);
                            }
                            if ("APA_OFF".equals(control)) {
                                Log.i("AdayoCamera", TAG + " - onStartCommand: VOICE_CONTROL APA_OFF");
                                AAOP_Camera.sendEvent(EventIds.APA_HIDE);
                                return super.onStartCommand(intent, flags, startId);
                            }
                        }
                        Log.i("AdayoCamera", TAG + " - onStartCommand() called with: start APA");
                        AAOP_Camera.sendEvent(EventIds.APA_DISPLAY);
                        return super.onStartCommand(intent, flags, startId);
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("AdayoCamera", TAG + " - onBind() called with: intent = [" + intent + "]");
        //将sourceMng服务注册到系统服务中
//        addServiceToServiceManager(SERVICE_NAME_AVM);
        return binder;
    }

    /**
     * 将该服务添加到ServiceManager中
     */
    public void addServiceToServiceManager(String serviceName) {
        Log.d("AdayoCamera", TAG + " - addServiceToServiceManager() called with: serviceName = [" + serviceName + "]");
        try {
            Object object = new Object();
            Method addService;
            addService = Class.forName("android.os.ServiceManager").getMethod("addService", String.class, IBinder.class);
            addService.invoke(object, serviceName, binder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("AdayoCamera", TAG + " - addServiceToServiceManager() end");
    }

    protected void produceAppType() {
        info = ConfigurationWordInfo.getInstance();
        info.init(getApplicationContext());
    }

    private int getProduceAppType(){
        String platform = info.getCarConfiguration();
        Log.i("AdayoCamera", TAG + " - produceAppType() called with : platform = " + platform);

        int radar_value  = info.getKey("RadarConfig");
        Log.i("AdayoCamera", TAG + " - produceAppType() called with : RadarConfig = " + radar_value);
        if ("HM6C17A".equals(platform)) {
            int value = info.getKey("CameraConfig");
            Log.i("AdayoCamera", TAG + " - produceAppType() called with : CameraConfig = " + value);
            if (3 == value) {
                Log.i("AdayoCamera", TAG + " - produceAppType() returned APA: " + APA);
                return APA;
            }
        }
        if (1 == radar_value){
            Log.i("AdayoCamera", TAG + " - produceAppType() returned RVC_HIGH: " + RVC_HIGH);
            return RVC_HIGH;
        }else{
            Log.i("AdayoCamera", TAG + " - produceAppType() returned RVC_LOW: " + RVC_LOW);
            hasFront = false;
            return RVC_LOW;
        }
    }


    protected void initAAOPCamera(int appType) {
        String windowTypeValue = AAOP_Camera.getSystemPropertyValue("ro.window.type.rvc");
        Log.i("AdayoCamera", TAG + " - initAAOPCamera() called with: windowTypeValue = [" + windowTypeValue + "]");
        if (TextUtils.isEmpty(windowTypeValue)) {
            windowTypeValue = "2054";
        }
        int windowType = Integer.parseInt(windowTypeValue);
        Log.i("AdayoCamera", TAG + " - initAAOPCamera() called with: windowType = [" + windowType + "]");
        byte cameraParam = 0;
        Config config = AAOP_Camera.init(this.getApplicationContext(), SignalModel.MODE_VEHICLE)
                .setCameraParam(cameraParam)
                .setVehicleAreaGlobalId(0)
                .setWindowParams(1920, 1080, 0, 0, 2054)
                .setSourceManagerParams(AdayoSource.ADAYO_SOURCE_AVM, "com.adayo.app.camera")
                .setDebounceTime(200)
                .setNoResponseTimes(3)
                .setCameraDelayedTimeMillis(300L)
                .setNotifyEvsCameraErrorTimeMillis(3000L)
                .setResetStateTime(260)
                .setNoResponseEventId(EventIds.SIGNAL_ERROR, EventIds.SIGNAL_RECOVER);
        if (APA == appType) {
            AAOP_Camera.setUnfilteredSignal(VehiclePropertyIds.APA_CUT_SOURCE,0);
            config.addControlBehavior(new AvmApaControlBehavior(this.getApplicationContext()), new UpgradeControlBehavior(this.getApplicationContext()),new AvmRoadCalibrationBehavior(this.getApplicationContext()));
        } else if (RVC_HIGH == appType) {
            AAOP_Camera.setUnfilteredSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE,0);
            config.addControlBehavior(new RvcRadarBehavior(this.getApplicationContext()), new RvcControlBehavior(this.getApplicationContext()));
        } else {
            AAOP_Camera.setUnfilteredSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE,0);
            config.addControlBehavior(new RvcRadarBehavior(this.getApplicationContext()), new RvcControlBehavior(this.getApplicationContext()));
        }
        setStartFinish(true);
    }


    private class BackCarBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }


    private void initFastMode() {
        Thread thread = new Thread(new FastModeRunnable());
        thread.start();
    }

    private void initFinishBehavior() {
        Thread thread = new Thread(new FinishBehaviorRunnable());
        thread.start();
    }

    private void initGetConfig(){
        Thread thread = new Thread(new getConfigRunnable());
        thread.start();
    }

    private volatile static boolean isEnable = false;

    public synchronized static void setEnable(boolean enable) {
        isEnable = enable;
        Log.i("AdayoCamera", TAG + " - " + "setEnable() called with: enable = [" + enable + "] , isEnable = " + isEnable);
    }

    public synchronized static boolean isEnable() {
        Log.i("AdayoCamera", TAG + " - isEnable() returned: " + isEnable);
        return isEnable;
    }
    private  volatile static  boolean isPullup = false;
    public synchronized static void setPullup(boolean pullup) {
        isPullup = pullup;
        Log.i("AdayoCamera", TAG + " - " + "setEnable() called with: pullup = [" + pullup + "] , isPullup = " + isPullup);
    }

    public synchronized static boolean isPullup() {
        Log.i("AdayoCamera", TAG + " - isPullup() returned: " + isPullup);
        return isPullup;
    }
    private  volatile static  boolean isStandby = false;
    public synchronized static void setStandby(boolean standby) {
        isStandby = standby;
        Log.i("AdayoCamera", TAG + " - " + "setStandby() called with: standby = [" + standby + "] , isStandby = " + isStandby);
    }

    public synchronized static boolean isStandby() {
        Log.i("AdayoCamera", TAG + " - isStandby() returned: " + isStandby);
        return isStandby;
    }
    //等待 initAAOPCamera创建完成
    private  volatile static  boolean isStartFinish = false;
    public synchronized static void setStartFinish(boolean startFinish) {
        isStartFinish = startFinish;
        Log.i("AdayoCamera", TAG + " - " + "setFinish() called with: startFinish = [" + startFinish + "] , isStartFinish = " + startFinish);
    }

    public synchronized static boolean isStartFinish() {
        Log.i("AdayoCamera", TAG + " - isStartFinish() returned: " + isStartFinish);
        return isStartFinish;
    }
    private volatile  static  boolean isGetConfig = false;

    public static boolean isGetConfig() {
        Log.i("AdayoCamera", TAG + " - isGetConfig() returned: " + isGetConfig);
        return isGetConfig;
    }

    public static void setGetConfig(boolean getConfig) {
        isGetConfig = getConfig;
        Log.i("AdayoCamera", TAG + " - " + "setGetConfig() called with: getConfig = [" + getConfig + "] , isGetConfig = " + isGetConfig);
    }

    @Override
    public void onDestroy() {
        SrcMngSwitchManager.getInstance().notifyAppFinished(AdayoSource.ADAYO_SOURCE_AVM, "com.adayo.app.camera");
        Log.i("AdayoCamera", TAG + " - " + "onDestroy() called");
        super.onDestroy();
    }
    public static int delayCount = 0;
    public static class FastModeRunnable implements Runnable {
        private static final String TAG = "FastModeRunnable";

        @Override
        public void run() {
            while (!MainService.isEnable()) {
                getDpu();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Log.e("AdayoCamera", TAG + " - run: getDpu() Thread error", e);
                }
            }
        }

        private void getDpu() {
            try {
                String bootValue = AAOP_Camera.getSystemPropertyValue("service.bootanim.exit");
                Log.i("AdayoCamera", TAG + " - getDpu()   bootValue = " + bootValue);
                if("1".equals(bootValue)){
                    if ( 3 == delayCount  ){
                        ICamera_rgb backCarService = ICamera_rgb.getService();
                        int value = backCarService.get_dpu_handler();
                        Log.i("AdayoCamera", TAG + " - getDpu() called with : backCarService.get_dpu_handler() = " + value);
                        if (value == 1 ) {
                            MainService.setEnable(true);
                            int gearSignValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE,0);
                            if(gearSignValue == 1){
                                AAOP_Camera.sendSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE,1);
                            }
                            AAOP_Camera.sendSignal(VehiclePropertyIds.APA_ST_BACKCARMODE,1);
                            Log.i("AdayoCamera", TAG + " - getDpu() called with isEnable = true");
                            SystemUtils.setSystemProperty("adayo.normal.backcar", "ON");
                        }
                    }else{
                        delayCount++;
                        Log.i("AdayoCamera", TAG + " - getDpu()  delayCount = " + delayCount);
                    }
                }
            } catch (RemoteException e) {
                MainService.setEnable(false);
                Log.i("AdayoCamera", TAG + " - getDpu() called with isEnable = false");
                Log.e("AdayoCamera", TAG + " - getDpu: " + e.getMessage());
            }
        }
    }

    private class FinishBehaviorRunnable implements Runnable {
        private static final String TAG = "FinishBehaviorRunnable";

        @Override
        public void run() {
            while (!MainService.isStartFinish()) {
                getFinish();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Log.e("AdayoCamera", TAG + " - run: getFinish () Thread error", e);
                }
            }
        }
        private void getFinish() {
             if(MainService.isStartFinish()){
                 onStartCommand(mIntent,mFlags,mStartId);
             }

        }
    }
   private class getConfigRunnable implements Runnable{
       private static final String TAG = "getConfigRunnable";
       @Override
       public void run() {
            while (!MainService.isGetConfig()){
                getConfig();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e("AdayoCamera", TAG + " - run: getConfig () Thread error", e);
                }
            }
       }
   }
        private void getConfig(){
            int value = info.getKey("CameraConfig");
            if (value >= 0 ){
                setGetConfig(true);
                cameraAppType = getProduceAppType();
                if (Looper.myLooper() != Looper.getMainLooper()){
                    Handler mainThread = new Handler(Looper.getMainLooper());
                    mainThread.post(new Runnable() {
                        @Override
                        public void run() {
                            initAAOPCamera(cameraAppType);
                        }
                    });
                    return;
                }

                Log.e("AdayoCamera", TAG + " - run: getConfig () setGetConfig ==   true");
            }else{
                setGetConfig(false);
                Log.e("AdayoCamera", TAG + " - run: getConfig () setGetConfig ==   fail");
            }
    }
}
