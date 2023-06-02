package com.adayo.app.camera.signalview;

import android.car.VehiclePropertyIds;
import android.view.View;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.ImageSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yiwen.Huan
 * created at 2021/9/23 14:17
 */
public class AvmBtnSignalView extends ImageSignalView {
    private List<WeakReference<View>> errorViewList = new ArrayList<>();

    public AvmBtnSignalView addErrorView(View view) {
        WeakReference<View> wr = new WeakReference<>(view);
        errorViewList.add(wr);
        return this;
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        super.processSignalBehavior(view, state);
    }

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        super.processEventBehavior(view, state);
        if (state.eventId == EventIds.AVM_CLICK_SETTING){
            int sendLevel = 0;
            int mBrightLevel =  AAOP_Camera.getSignalValue(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL,0);
            switch (mBrightLevel){
                case 1:
                    sendLevel = 49;
                    break;
                case 2:
                    sendLevel = 50;
                    break;
                case 3:
                    sendLevel = 51;
                    break;
                case 4:
                    sendLevel = 52;
                    break;
                case 5:
                    sendLevel = 53;
                    break;
                case 6:
                    sendLevel = 54;
                    break;
                case 7:
                    sendLevel = 55;
                    break;
                case 8:
                    sendLevel = 56;
                    break;
                case 9:
                    sendLevel = 57;
                    break;
                case 10:
                    sendLevel = 58;
                    break;
                default:
                    break;
            }
            AAOP_Camera.sendSignal(VehiclePropertyIds.AVM_SET_SETTING,sendLevel);
        }
        if (state.eventId == EventIds.ENABLE_RECOVER || state.eventId == EventIds.CAMERA_NORMAL) {
            int size = errorViewList.size();
            if (0 == size) {
                resetStateFromSignal();
                return;
            }
            for (int i = 0; i < size; i++) {
                View item = errorViewList.get(i).get();
                if (item == null) {
                    resetStateFromSignal();
                    return;
                }
                if (View.VISIBLE == item.getVisibility()) {
                    return;
                }
            }
            resetStateFromSignal();
            return;
        }
        if (state.eventId == EventIds.ENABLE_FALSE || state.eventId == EventIds.CAMERA_ERROR) {
            view.setEnabled(false);
            view.setSelected(false);
        }
    }

    @Override
    protected void onSignalDataChanged(SignalViewState state) {
        int size = errorViewList.size();
        if (0 != size) {
            for (int i = 0; i < size; i++) {
                View item = errorViewList.get(i).get();
                if (item != null) {
                    if (View.VISIBLE == item.getVisibility()) {
                        return;
                    }
                }
            }
        }
        super.onSignalDataChanged(state);
    }
}
