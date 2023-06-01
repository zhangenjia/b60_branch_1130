package com.adayo.app.camera.signalview;

import android.view.View;
import android.widget.TextView;

import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.signalview.base.BaseTimerSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/8/20 10:18
 */
public class LineCalibrationFailedTimerSignalView extends BaseTimerSignalView {
    private int time = 5;

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        if (state.eventTriggeredText > 0) {
            if (view instanceof TextView) {
                setText((TextView) view, state.eventTriggeredText, 5);
            }
        }
        super.processEventBehavior(view, state);
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (state.signalTriggeredText > 0) {
            if (view instanceof TextView) {
                setText((TextView) view, state.signalTriggeredText, 5);
            }
        }
        super.processSignalBehavior(view, state);
    }

    @Override
    protected void onCustomerTaskRunInThread() {
        if (time <= 0) {
            cancelCustomTask();
            sendEvent(EventIds.LINE_CALIBRATION_FAILED_HIDE);
        }
    }

    @Override
    protected void runCustomTask(long delay, long Period) {
        time = 5;
        super.runCustomTask(delay, Period);
    }

    @Override
    protected void onCustomerTaskRunInMainThread(View view) {
        if (view instanceof TextView) {
            String text = view.getContext().getString(R.string.line_calibration_failed_content, time);
            ((TextView) view).setText(text);
            time--;
        }
    }

}
