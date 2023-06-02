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
public class UpgradeFailedButtonSignalView extends BaseTimerSignalView {
    private int time = 10;

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        if (state.eventTriggeredText > 0) {
            if (view instanceof TextView) {
                setText((TextView) view, state.eventTriggeredText);
            }
        }
        super.processEventBehavior(view, state);
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (state.signalTriggeredText > 0) {
            if (view instanceof TextView) {
                setText((TextView) view, state.signalTriggeredText);
            }
        }
        super.processSignalBehavior(view, state);
    }

    @Override
    protected void onCustomerTaskRunInThread() {
        if (time <= 0) {
            cancelCustomTask();
            sendEvent(EventIds.UPGRADE_HIDE);
        }
    }


    @Override
    protected void runCustomTask(long delay, long Period) {
        time = 10;
        super.runCustomTask(delay, Period);
    }

    @Override
    protected void onCustomerTaskRunInMainThread(View view) {
        if (view instanceof TextView) {
            String text = view.getContext().getString(R.string.avm_dialog_update_failed_button, time);
            ((TextView) view).setText(text);
            time--;
        }
    }
}
