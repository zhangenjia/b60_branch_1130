package com.adayo.app.camera.signalview;

import android.view.View;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.base.BaseTimerSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/8/31 14:02
 */
public class ApaErrorTimerSignalView extends BaseTimerSignalView {


    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        super.processEventBehavior(view, state);
        if (state.eventId == EventIds.APA_DISPLAY || state.eventId == EventIds.APA_DISPLAY_FROM_SIGNAL || state.eventId == EventIds.APA_DISPLAY_WITH_ERROR) {
            if (view.getVisibility() == View.VISIBLE) {
                cancelCustomTask();
                runCustomTask(5000, 0);
            }
        }
    }

    @Override
    protected void onCustomerTaskRunInThread() {
        AAOP_Camera.sendEvent(EventIds.APA_HIDE);
    }

    @Override
    protected void onCustomerTaskRunInMainThread(View view) {
    }
}
