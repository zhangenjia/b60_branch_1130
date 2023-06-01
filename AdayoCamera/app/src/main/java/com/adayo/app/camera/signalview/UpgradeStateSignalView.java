package com.adayo.app.camera.signalview;

import android.car.VehiclePropertyIds;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.TextSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

import java.lang.ref.WeakReference;
import java.util.TimerTask;

/**
 * @author Yiwen.Huan
 * created at 2021/9/16 15:00
 */
public class UpgradeStateSignalView extends TextSignalView {
    private TimerTask upgradeStateTask;

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        super.processSignalBehavior(view, state);
        if (null != upgradeStateTask) {
            upgradeStateTask.cancel();
            upgradeStateTask = null;
        }
        if (state.signalValue == 0 || state.signalValue == 24 || state.signalValue == 25) {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior() called with: state.signalValue == 0 || state.signalValue == 24 || state.signalValue == 25 ; | state.signalValue = " + state.signalValue);
            return;
        }
        if (null != view && View.VISIBLE == view.getVisibility()) {
            upgradeStateTask = new UpgradeStateTimerTask(state.signalValue, this);
            timer.schedule(upgradeStateTask, 180000);
            Log.d("AdayoCamera", TAG + " - processSignalBehavior() called with:  timer.schedule(upgradeStateTask, 180000);");
        }
    }

    protected void checkUpgradeState(int lastStateValue) {
        Log.d("AdayoCamera", TAG + " - checkUpgradeState() called");
        View view = getView();
        if (null != view && View.VISIBLE == view.getVisibility()) {
            int curr = AAOP_Camera.getSignalValue(VehiclePropertyIds.APA_UPGRADE_STATUS, 0);
            if (curr == lastStateValue) {
                AAOP_Camera.sendEvent(EventIds.UPGRADE_ERROR_SIGNAL_DIED);
            }
            return;
        }
        Log.d("AdayoCamera", TAG + " - checkUpgradeState: failed because null == view && View.VISIBLE ！= view.getVisibility() ； | view = " + view);
    }

    private static class UpgradeStateTimerTask extends TimerTask {
        private static final String TAG = "UpgradeStateTimerTask";
        private int stateValue = -1;
        private WeakReference<UpgradeStateSignalView> wr;
        Handler mainHandler;

        public UpgradeStateTimerTask(int stateValue, UpgradeStateSignalView signalView) {
            this.stateValue = stateValue;
            this.wr = new WeakReference<>(signalView);
            mainHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void run() {
            UpgradeStateSignalView signalView = wr.get();
            if (null == signalView) {
                Log.d("AdayoCamera", TAG + " - run: failed because signalView is null , do nothing");
                return;
            }
            mainHandler.post(new MainRunnable(signalView, stateValue));
        }
    }

    protected static class MainRunnable implements Runnable {
        protected static final String TAG = "MainRunnable";
        WeakReference<UpgradeStateSignalView> wrSignalView;
        private int stateValue = -1;

        protected MainRunnable(UpgradeStateSignalView signalView, int stateValue) {
            this.wrSignalView = new WeakReference<>(signalView);
            this.stateValue = stateValue;
        }

        @Override
        public void run() {
            UpgradeStateSignalView signalView = wrSignalView.get();
            if (null == signalView) {
                Log.d("AdayoCamera", TAG + " - run: failed because signalView is null , do nothing");
                return;
            }
            signalView.checkUpgradeState(stateValue);
        }
    }


}
