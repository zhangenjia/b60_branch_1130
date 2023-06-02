package com.adayo.app.camera.signalview;

import android.car.VehiclePropertyIds;
import android.view.View;

import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.ImageSignalView;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

public class AvmRadarBtnSignalView extends BaseSignalView {
    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
//            if (EventIds.AVM_RADAR_OFF_RADAR == state.eventId){
//                int getRadarSignal = AAOP_Camera.getSignalValue(VehiclePropertyIds.AVM2_SET_RADARTRIGGERVIEW,0);
//                if (getRadarSignal == 1){
//                    view.setBackgroundResource(R.mipmap.frame_set_sel_front_radar_dis);
//                }else if (getRadarSignal == 0){
//                    view.setBackgroundResource(R.mipmap.frame_set_last_front_radar_dis);
//                }
//            }
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {

    }
}
