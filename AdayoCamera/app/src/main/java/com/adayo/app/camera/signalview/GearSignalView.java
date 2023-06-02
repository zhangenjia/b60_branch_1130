package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;

import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/9/15 15:43
 */
public class GearSignalView extends BaseSignalView {

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {

    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        AAOP_Camera.sendShareInfoData(16, "backCarState", String.valueOf(state.signalValue == 1));
        Log.d("AdayoCamera", TAG + " - processSignalBehavior() called with: view = [" + view + "], state.signalValue = [" + state.signalValue + "]");
    }
}
