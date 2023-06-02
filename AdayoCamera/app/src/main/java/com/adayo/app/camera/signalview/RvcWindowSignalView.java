package com.adayo.app.camera.signalview;

import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.MainService;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.WindowSignalView;
import com.adayo.proxy.adas.evs.EvsCameraMng;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.util.TimerTask;

public class RvcWindowSignalView extends WindowSignalView{
    public byte dvrOpenCameraParam = 1;
    private static final int SET_BACK_LIGHT_SWITCH_ON = 0;
    private static final int SET_BACK_LIGHT_SWITCH_OFF = 1;
    private static final String DISPLAY_DEVICE = "DisplayDevice";
    private static final String SET_BACK_LIGHT_SWITCH = "set_back_light_switch";
    private  int  getDvrStatus; // 从dvr进入rvc
    public RvcWindowSignalView() {
        super();
        this.withCamera = true;
        this.cameraMostDelayTime = 800L;
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
        if (1 == getDvrStatus) {
            processDisplayViewFromDvr(view);
        } else {
            processDisplayVViewDefault(view);
        }
    }

    TimerTask task;
    private void processDisplayViewFromDvr(View view) {
        if (null != task) {
            Log.w("AdayoCamera", this.TAG + " - processDisplayViewFromDvr: failed because null != task , do nothing");
            return;
        }

        task = new TimerTask() {
            @Override
            public void run() {
                    EvsCameraMng.closeCamera(dvrOpenCameraParam);
                    mainHandler.postDelayed(() -> showToWindow(view),600);
                    task.cancel();
                    task = null;
            }
        };
        timer.schedule(task,600);

    }

    private void processDisplayVViewDefault(View view) {
        super.displayView(view);
    }

    @Override
    protected void beforeShowToWindow(View view) {
        AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(true));
        if(MainService.isPullup()){
            Log.d("AdayoCamera", TAG + " - displayView: MainService.isPullup() true" );
            try {
                AAOP_SystemServiceManager
                        .getInstance()
                        .setScreenState(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
            } catch (RemoteException e) {
                Log.e("AdayoCamera", TAG + " - displayView: error", e);
            }
        }

        setBackLightSwitch(SET_BACK_LIGHT_SWITCH_ON);

    }

    @Override
    protected void afterShowToWindow(View view, boolean isShowed) {
        super.afterShowToWindow(view, isShowed);
        if (!isShowed){
            AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(false));
            setBackLightSwitch(SET_BACK_LIGHT_SWITCH_OFF);
        }
    }

    @Override
    protected void afterRemoveFromWindow(View view,boolean isShowed) {
        super.afterRemoveFromWindow(view,isShowed);
        if (!isShowed){
            if (null != task){
                task.cancel();
                task = null;
            }
            AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(false));
            setBackLightSwitch(SET_BACK_LIGHT_SWITCH_OFF);
        }else{
            AAOP_Camera.sendShareInfoData(16, "enterAvm", String.valueOf(true));
            setBackLightSwitch(SET_BACK_LIGHT_SWITCH_ON);
        }

    }

    private void setBackLightSwitch(int status){
        if (MainService.isStandby()){
            Bundle param = new Bundle();
            param.putInt(SET_BACK_LIGHT_SWITCH, status);
            int requestReturn = AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface(DISPLAY_DEVICE, SET_BACK_LIGHT_SWITCH, param);
            Log.d(TAG, "setBackLightSwitch: requestReturn = " +requestReturn);
            Log.d("AdayoCamera", TAG + " -  setBackLightSwitch: requestReturn = " +requestReturn);
        }else {
            Log.d("AdayoCamera", TAG + " -  setBackLightSwitch: fail ");
        }
    }
}
