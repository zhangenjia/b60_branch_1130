package com.adayo.app.camera.signalview;

import android.view.View;
import android.widget.TextView;

import com.adayo.app.camera.R;
import com.adayo.proxy.aaop_camera.signalview.base.BaseTimerSignalView;

/**
 * @author Yiwen.Huan
 * created at 2021/9/26 17:01
 */
public class CancelTimerSignalView extends BaseTimerSignalView {

    int time = 30;

    @Override
    protected void runCustomTask(long delay, long Period) {
        time = 30;
        super.runCustomTask(delay, Period);
    }

    @Override
    protected void onCustomerTaskRunInThread() {
        if (time == 0) {
            sendEvent(currState.eventId);
        } else if (time < 0) {
            time = 0;
        }
    }

    @Override
    protected void onCustomerTaskRunInMainThread(View view) {
        if (view instanceof TextView) {
            ((TextView) view).setText(view.getContext().getString(R.string.apa_dialog_exit_negative, time));
        }
        time--;
    }

}
