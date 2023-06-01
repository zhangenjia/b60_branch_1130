package com.adayo.app.camera.signalview;

import android.car.VehiclePropertyIds;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.MainService;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.WindowSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

public class RvcRadarWindowSignalView extends WindowSignalView {
    private static final int SET_BACK_LIGHT_SWITCH_ON = 0;
    private static final int SET_BACK_LIGHT_SWITCH_OFF = 1;
    private static final String DISPLAY_DEVICE = "DisplayDevice";
    private static final String SET_BACK_LIGHT_SWITCH = "set_back_light_switch";
    public RvcRadarWindowSignalView() {
        super();
    }


    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        super.processSignalBehavior(view, state);
        if (String.valueOf(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE).equals(state.signalKey)) {
            if (state.signalValue == 1){
                if (view.getWindowToken() != null){
                    view.setAlpha(1);
                }else{
                    view.setAlpha(0.0F);
                }
            }else{
                view.setAlpha(1);
            }
        }
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
