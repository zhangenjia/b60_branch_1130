package com.adayo.app.camera.signalview;

import android.car.VehiclePropertyIds;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.SeekbarSignalView;
import com.adayo.proxy.aaop_camera.signalview.TextSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;

/**
 * @author Yiwen.Huan
 * created at 2021/8/13 16:51
 */
public class BrightnessSignalView extends TextSignalView {

    public static final int SETTING_SET_LOCK = 1;
    public static final int SETTING_LOCK_ID_APA = 2;


    public BrightnessSignalView() {
        super();
    }

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        if (SignalViewState.INVALID != state.signalValue){
            AAOP_DeviceServiceManager.getInstance().setSysBacklightLock(SETTING_SET_LOCK, SETTING_LOCK_ID_APA, state.signalValue);
        }
        super.processEventBehavior(view, state);
    }
}
