package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adayo.app.camera.R;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

import java.util.Locale;

/**
 * @author Yiwen.Huan
 * created at 2021/8/30 17:17
 */
public class TrackDistanceSignalView extends BaseSignalView {

    private String distanceStr = "0";

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {

    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (state.signalTriggeredText <= 0) {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior: failed because state.signalTriggeredText = " + state.signalTriggeredText);
            return;
        }

        int distance = state.signalValue;

        if (distance <= 0) {
            distanceStr = "0";
        } else {
            float distanceFloat = (float) distance / 100;
            distanceStr = String.format(Locale.US, "%.2f", distanceFloat);
            if (distanceStr.contains(".00")) {
                distanceStr = distanceStr.replace(".00", "");
            }
        }

        if (view instanceof TextView) {
            ((TextView) view).setText(view.getContext().getString(R.string.track_distance, distanceStr));
        } else {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior: failed because view is not TextView , view = " + view);
        }
    }

}
