package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.base.BaseTimerSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/9/22 15:50
 */
public class RoadCalibratingTimerSV extends BaseTimerSignalView {

    private int time = 100;

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        if (state.eventTriggeredText > 0) {
            if (view instanceof TextView) {
                setText((TextView) view, state.eventTriggeredText);
            }
        } else {
            Log.d("AdayoCamera", TAG + " - processEventBehavior: failed because state.eventTriggeredText = " + state.eventTriggeredText);
        }
        super.processEventBehavior(view, state);
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (state.signalTriggeredText > 0) {
            if (view instanceof TextView) {
                setText((TextView) view, state.signalTriggeredText);
            }
        } else {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior: failed because state.signalTriggeredText = " + state.signalTriggeredText);
        }
        super.processSignalBehavior(view, state);
    }

    @Override
    protected void onCustomerTaskRunInThread() {
        if (time < -300) {
            cancelCustomTask();
            AAOP_Camera.sendEvent(EventIds.SIGNAL_ERROR);
        }
    }

    @Override
    protected void runCustomTask(long delay, long Period) {
        time = 100;
        super.runCustomTask(delay, Period);
    }

    @Override
    protected void onCustomerTaskRunInMainThread(View view) {
        if (view instanceof TextView) {
            if (time >= 0) {
                ((TextView) view).setText(view.getContext().getString(R.string.road_calibration_running_hint_title, time));
            }
            time--;
        }
    }
}
