package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.RadioGroupSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

/**
 * @author Yiwen.Huan
 * created at 2021/9/1 13:53
 */
public class AvmMoreAngleSignalView extends RadioGroupSignalView {
    protected TimerTask customTask;

    public AvmMoreAngleSignalView() {
        super();
    }

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        super.processEventBehavior(view, state);
        if (state.eventId == EventIds.AVM_CLICK_MORE_ANGLE) {
            cancelCustomTask();
            if (View.GONE == view.getVisibility()) {
                return;
            }
            runCustomTask();
            return;
        }
        if (state.eventId == EventIds.AVM_HIDE_MORE_ANGLE) {
            cancelCustomTask();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        super.onCheckedChanged(group, checkedId);
        cancelCustomTask();
        runCustomTask();
    }

    protected void cancelCustomTask() {
        if (null != customTask) {
            customTask.cancel();
            customTask = null;
        }
    }

    protected void runCustomTask() {
        if (null == customTask) {
            customTask = new CustomRunnable(this);
        }
        timer.schedule(customTask, 5000);
    }

    protected void onCustomerTaskRunInThread() {
        AAOP_Camera.sendEvent(EventIds.AVM_HIDE_MORE_ANGLE);
    }

    protected static class CustomRunnable extends TimerTask {
        protected static final String TAG = "CustomerRunnable";
        WeakReference<AvmMoreAngleSignalView> wrSignalView;

        protected CustomRunnable(AvmMoreAngleSignalView signalView) {
            this.wrSignalView = new WeakReference<>(signalView);
        }

        @Override
        public void run() {
            AvmMoreAngleSignalView signalView = wrSignalView.get();
            if (null == signalView) {
                Log.d("AdayoCamera", TAG + " - run: signalView is null");
                return;
            }
            signalView.onCustomerTaskRunInThread();
        }
    }
}
