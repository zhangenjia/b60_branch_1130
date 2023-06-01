package com.adayo.app.camera.signalview;

import android.util.Log;

import com.adayo.app.camera.controlbehavior.AvmApaControlBehavior;
import com.adayo.proxy.aaop_camera.signalview.base.BaseConditionSignalView;

import java.lang.ref.WeakReference;

/**
 * @author Yiwen.Huan
 * created at 2021/9/22 11:01
 */
public class RoadCalibrationConditionSignalView extends BaseConditionSignalView {

    private WeakReference<AvmApaControlBehavior> wr;

    private RoadCalibrationConditionSignalView() {
        super();
    }

    public RoadCalibrationConditionSignalView(AvmApaControlBehavior behavior) {
        super();
        wr = new WeakReference<>(behavior);
    }

    @Override
    protected boolean shouldOnEvent(int eventId) {
        return true;
    }

    @Override
    protected boolean shouldNotifySignalDataChanged(String key, int value) {
        if (null == wr) {
            Log.e("AdayoCamera", TAG + " - shouldNotifySignalDataChanged: failed because WeakReference<AvmControlBehavior> is null");
            return false;
        }
        if (null == wr.get()) {
            Log.e("AdayoCamera", TAG + " - shouldNotifySignalDataChanged: failed because wr.get() is null");
            return false;
        }
        int calibrationType = wr.get().getCalibrationType();
        Log.d("AdayoCamera", TAG + " - shouldNotifySignalDataChanged: calibrationType = " + calibrationType);
        return AvmApaControlBehavior.CALIBRATION_ROAD == calibrationType;
    }
}
