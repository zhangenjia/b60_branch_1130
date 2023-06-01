package com.adayo.app.camera.signalview;

import android.car.VehiclePropertyIds;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.MainService;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.WindowSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.adayo.proxy.adas.evs.EvsCameraMng;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.util.TimerTask;

public class AvmApaWindowSignalView extends WindowSignalView {

    public static final int SETTING_SET_LOCK = 1;
    public static final int SETTING_SET_UNLOCK = 0;
    public byte dvrOpenCameraParam = 1;
    public static final int SETTING_LOCK_ID_APA = 2;
    private static final int SET_BACK_LIGHT_SWITCH_ON = 0;
    private static final int SET_BACK_LIGHT_SWITCH_OFF = 1;
    private static final String DISPLAY_DEVICE = "DisplayDevice";
    private static final String SET_BACK_LIGHT_SWITCH = "set_back_light_switch";
    private  int  getDvrStatus; // 从dvr进入rvc
    public AvmApaWindowSignalView() {
        super();
    }
    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        if (state.eventId == EventIds.AVM_DISPLAY){
            noResponseEventId = EventIds.AVM_DISPLAY_WITH_ERROR;
        }else if(state.eventId == EventIds.APA_DISPLAY){
            noResponseEventId = EventIds.APA_DISPLAY_WITH_ERROR;
        }
        noResponseRecoveryEventId = EventIds.SIGNAL_RECOVER;
        super.processEventBehavior(view, state);
    }

    @Override
    public void displayView(View view) {
        try {
            getDvrStatus = Settings.Global.getInt(view.getContext().getContentResolver(), "dvrShow");
            Log.d("AdayoCamera", TAG + "  getDvrStatus: getDvrStatus === " +getDvrStatus);
        } catch (Settings.SettingNotFoundException e) {
            Log.d("AdayoCamera", TAG + "  getDvrStatus: fail  " +e);
        }

        switchDisplayView(view);
    }

    private void switchDisplayView(View view) {
        this.withCamera = true;
        if(getDvrStatus == 1){
            this.cameraMostDelayTime = 1500L;
        }else{
            this.cameraMostDelayTime = 600L;
        }
        processDisplayVViewDefault(view);
    }

    private void processDisplayVViewDefault(View view) {
        super.displayView(view);
    }

    @Override
    protected void beforeShowToWindow(View view) {
        int mBrightLevel =  AAOP_Camera.getSignalValue(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL,0);
        AAOP_DeviceServiceManager.getInstance().setSysBacklightLock(SETTING_SET_LOCK, SETTING_LOCK_ID_APA, mBrightLevel);
        AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(true));
        if(MainService.isPullup()){
            Log.d("AdayoCamera", TAG + " - displayView: MainService.isPullup() true" );
            try {
                AAOP_SystemServiceManager.getInstance().setScreenState(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
            } catch (RemoteException e) {
                Log.e("AdayoCamera", TAG + " - displayView: error", e);
            }
        }
        setBackLightSwitch(SET_BACK_LIGHT_SWITCH_ON);
    }

    @Override
    protected void afterShowToWindow(View view, boolean isShowed) {
        super.afterShowToWindow(view, isShowed);
        Log.d("AdayoCamera", TAG + " - afterShowToWindow   111111: isShowed =  " + isShowed);
        if (!isShowed){
            AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(false));
            AAOP_DeviceServiceManager.getInstance().setSysBacklightLock(SETTING_SET_UNLOCK, SETTING_LOCK_ID_APA, 0);
            setBackLightSwitch(SET_BACK_LIGHT_SWITCH_OFF);
        }
    }

    @Override
    protected void afterRemoveFromWindow(View view,boolean isShowed) {
        Log.d("AdayoCamera", TAG + " - afterRemoveFromWindow    222222 : isShowed =  " + isShowed);
        super.afterRemoveFromWindow(view,isShowed);
        if (!isShowed){
            AAOP_DeviceServiceManager.getInstance().setSysBacklightLock(SETTING_SET_UNLOCK, SETTING_LOCK_ID_APA, 0);
            AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(false));
            setBackLightSwitch(SET_BACK_LIGHT_SWITCH_OFF);
        }else {
            AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(true));
            setBackLightSwitch(SET_BACK_LIGHT_SWITCH_ON);
        }

    }
    private void setBackLightSwitch(int status){
        if (MainService.isStandby()) {
            Bundle param = new Bundle();
            param.putInt(SET_BACK_LIGHT_SWITCH, status);
            int requestReturn = AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface(DISPLAY_DEVICE, SET_BACK_LIGHT_SWITCH, param);
            Log.d("AdayoCamera", TAG + " - setBackLightSwitch: requestReturn = " + requestReturn);
        }
    }
}
