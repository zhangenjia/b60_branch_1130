package com.adayo.app.camera.signalview;

import android.view.View;

import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/11/10 14:46
 */
public class RvcSpeedSV extends BaseSignalView {

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (state.signalValue * 0.05625 > 15) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
