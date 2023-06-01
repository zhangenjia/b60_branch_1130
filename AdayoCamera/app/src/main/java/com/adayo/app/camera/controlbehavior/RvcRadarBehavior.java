package com.adayo.app.camera.controlbehavior;

import android.car.VehiclePropertyIds;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.adayo.app.camera.MainService;
import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.app.camera.signalview.RadarBeepSignalView;
import com.adayo.app.camera.signalview.RvcRadarErrorSV;
import com.adayo.app.camera.signalview.RvcRadarWindowSignalView;
import com.adayo.app.camera.signalview.WindowConditionSignalView;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.controlcenter.behavior.BaseControlBehavior;
import com.adayo.proxy.aaop_camera.signalview.WindowSignalView;
import com.adayo.proxy.aaop_camera.signalview.base.ISignalView;
import com.adayo.proxy.aaop_camera.signalview.factory.SignalViewFactory;
import com.adayo.proxy.aaop_camera.signalview.state.Signal;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/11/10 13:42
 */
public class RvcRadarBehavior extends BaseControlBehavior {

    public RvcRadarBehavior(Context mContext) {
        super(mContext);
        Log.i("AdayoCamera", TAG + " - " + "RvcRadarBehavior() called with: mContext = [" + mContext + "]");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.rvc_radar;
    }

    @Override
    protected ISignalView produceRootSignalView(View rootView) {
        ISignalView root = new WindowConditionSignalView();
        return root;
    }

    @Override
    public void init(){

        WindowSignalView rootSV = new RvcRadarWindowSignalView();
        initCustomSignalView(rootSV,rootView,false);
        rootSV.setShouldNotifySourceManager(false);
        WindowManager.LayoutParams layoutParams = rootSV.getLayoutParams();
        layoutParams.width = 512;

        rootSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 2)
                .signalTriggeredWindowBehavior(true)
                .build());
        rootSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 3)
                .signalTriggeredWindowBehavior(true)
                .build());
        rootSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 4)
                .signalTriggeredWindowBehavior(true)
                .build());
        rootSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE, SignalViewState.INVALID)
                .build());

        if (MainService.hasFront) {
            Log.d("AdayoCamera", TAG + " - produceRootSignalView: MainService.hasFront = true");
            rootSV.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 1)
                    .signalTriggeredWindowBehaviorIf(false, new Signal(VehiclePropertyIds.FRONT_TADAR_SYSTEM_STATUS, 0, 0, 1))
                    .build());
            rootSV.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 0)
                    .signalTriggeredWindowBehaviorIf(false, new Signal(VehiclePropertyIds.FRONT_TADAR_SYSTEM_STATUS, 0, 0, 1))
                    .build());


            rootSV.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.FRONT_TADAR_SYSTEM_STATUS, 1)
                    .signalTriggeredWindowBehaviorIf(false, new Signal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 0, 0, 1))
                    .build());
            rootSV.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.FRONT_TADAR_SYSTEM_STATUS, 0)
                    .signalTriggeredWindowBehaviorIf(false, new Signal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 0, 0, 1))
                    .build());

            rootSV.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.FRONT_TADAR_SYSTEM_STATUS, 2)
                    .signalTriggeredWindowBehavior(true)
                    .build());

        } else {
            rootSV.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 0)
                    .signalTriggeredWindowBehavior(false)
                    .build());
            rootSV.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, 1)
                    .signalTriggeredWindowBehavior(false)
                    .build());
        }

        RadarBeepSignalView radarBeepSignalView = new RadarBeepSignalView();
        initCustomSignalView(radarBeepSignalView, R.id.iv_car, false);
        radarBeepSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.FRONT_RADAR_ALARM_FOR_PARKING, SignalViewState.INVALID)
                .build());

        RvcRadarErrorSV radarErrorSV = new RvcRadarErrorSV();
        initCustomSignalView(radarErrorSV, R.id.iv_car, false);
        radarErrorSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.RADAR_FAULT_INFORMATION, AAOP_Camera.INVALID)
                .build());
        radarErrorSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.REAR_TADAR_SYSTEM_STATUS, AAOP_Camera.INVALID)
                .build());

        ISignalView radarHintSV = createSignalView(SignalViewFactory.TEXT, R.id.tv_radar_error, false);
        if (MainService.hasFront) {
            Log.d("AdayoCamera", TAG + " - init: MainService.hasFront = true");
            radarHintSV.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_FRONT_ERROR)
                    .eventTriggeredText(R.string.rvc_front_radar_error)
                    .eventTriggeredVisibility(View.VISIBLE)
                    .build());
        }
        radarHintSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_BEHIND_ERROR)
                .eventTriggeredText(R.string.rvc_rear_radar_error)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        radarHintSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                .eventTriggeredText(R.string.rvc_radar_limited)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        radarHintSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                .eventTriggeredText(R.string.rvc_radar_error)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        radarHintSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_HINT_DISMISS)
                .eventTriggeredVisibility(View.GONE)
                .build());


        ISignalView behindLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_behind_left, false);
        behindLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 1)
                .signalTriggeredImageResource(R.mipmap.behind_left_4)
                .build());
        behindLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 2)
                .signalTriggeredImageResource(R.mipmap.behind_left_4)
                .build());
        behindLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 3)
                .signalTriggeredImageResource(R.mipmap.behind_left_3)
                .build());
        behindLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 4)
                .signalTriggeredImageResource(R.mipmap.behind_left_3)
                .build());
//        behindLeft.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 5)
//                .signalTriggeredImageResource(R.mipmap.behind_left_2)
//                .build());
//        behindLeft.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 6)
//                .signalTriggeredImageResource(R.mipmap.behind_left_2)
//                .build());
        behindLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, SignalViewState.INVALID)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_BEHIND_LEFT_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_left_fault)
                .build());
        behindLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_left_fault2)
                .build());
        behindLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                .eventTriggeredImageResource(R.mipmap.behind_left_fault2)
                .build());


        ISignalView behindMiddleLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_behind_middle_left, false);
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 0)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 0)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 1)
                .signalTriggeredImageResource(R.mipmap.behind_middle_left_4)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 2)
                .signalTriggeredImageResource(R.mipmap.behind_middle_left_4)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 3)
                .signalTriggeredImageResource(R.mipmap.behind_middle_left_3)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 4)
                .signalTriggeredImageResource(R.mipmap.behind_middle_left_3)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 5)
                .signalTriggeredImageResource(R.mipmap.behind_middle_left_2)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 6)
                .signalTriggeredImageResource(R.mipmap.behind_middle_left_2)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, 7)
                .signalTriggeredImageResource(R.mipmap.behind_middle_left_1)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_CENTER_TO_OBSTACLE, SignalViewState.INVALID)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_BEHIND_MIDDLE_LEFT_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_middle_left_fault)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_middle_left_fault2)
                .build());
        behindMiddleLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                .eventTriggeredImageResource(R.mipmap.behind_middle_left_fault2)
                .build());

        ISignalView behindMiddleRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_behind_middle_right, false);
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 0)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 1)
                .signalTriggeredImageResource(R.mipmap.behind_middle_right_4)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 2)
                .signalTriggeredImageResource(R.mipmap.behind_middle_right_4)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 3)
                .signalTriggeredImageResource(R.mipmap.behind_middle_right_3)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 4)
                .signalTriggeredImageResource(R.mipmap.behind_middle_right_3)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 5)
                .signalTriggeredImageResource(R.mipmap.behind_middle_right_2)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 6)
                .signalTriggeredImageResource(R.mipmap.behind_middle_right_2)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, 7)
                .signalTriggeredImageResource(R.mipmap.behind_middle_right_1)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_CENTER_TO_OBSTACLE, SignalViewState.INVALID)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_BEHIND_MIDDLE_RIGHT_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_middle_right_fault)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_middle_right_fault2)
                .build());
        behindMiddleRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                .eventTriggeredImageResource(R.mipmap.behind_middle_right_fault2)
                .build());

        ISignalView behindRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_behind_right, false);
        behindRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 0)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 1)
                .signalTriggeredImageResource(R.mipmap.behind_right_4)
                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 2)
                .signalTriggeredImageResource(R.mipmap.behind_right_4)
                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 3)
                .signalTriggeredImageResource(R.mipmap.behind_right_3)
                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 4)
                .signalTriggeredImageResource(R.mipmap.behind_right_3)
                .build());
//        behindRight.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 5)
//                .signalTriggeredImageResource(R.mipmap.behind_right_2)
//                .build());
//        behindRight.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, 6)
//                .signalTriggeredImageResource(R.mipmap.behind_right_2)
//                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_RIGHT_TO_OBSTACLE, SignalViewState.INVALID)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_BEHIND_RIGHT_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_right_fault)
                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                .eventTriggeredImageResource(R.mipmap.behind_right_fault2)
                .build());
        behindRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                .eventTriggeredImageResource(R.mipmap.behind_right_fault2)
                .build());

        if (MainService.hasFront) {
            Log.d("AdayoCamera", TAG + " - init: MainService.hasFront = true");
            ISignalView frontLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_front_left, false);
            frontLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, 0)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, 1)
                    .signalTriggeredImageResource(R.mipmap.front_left_4)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, 2)
                    .signalTriggeredImageResource(R.mipmap.front_left_4)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, 3)
                    .signalTriggeredImageResource(R.mipmap.front_left_3)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, 4)
                    .signalTriggeredImageResource(R.mipmap.front_left_3)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_TO_THE_OBSTACLE, SignalViewState.INVALID)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_FRONT_LEFT_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_left_fault)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_left_fault2)
                    .build());
            frontLeft.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                    .eventTriggeredImageResource(R.mipmap.front_left_fault2)
                    .build());

            ISignalView frontMiddleLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_front_middle_left, false);
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 0)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 1)
                    .signalTriggeredImageResource(R.mipmap.front_middle_left_4)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 2)
                    .signalTriggeredImageResource(R.mipmap.front_middle_left_4)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 3)
                    .signalTriggeredImageResource(R.mipmap.front_middle_left_3)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 4)
                    .signalTriggeredImageResource(R.mipmap.front_middle_left_3)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 5)
                    .signalTriggeredImageResource(R.mipmap.front_middle_left_2)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, 6)
                    .signalTriggeredImageResource(R.mipmap.front_middle_left_2)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_LEFT_CENTER_TO_THE_OBSTACLE, SignalViewState.INVALID)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_FRONT_MIDDLE_LEFT_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_middle_left_fault)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_middle_left_fault2)
                    .build());
            frontMiddleLeft.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                    .eventTriggeredImageResource(R.mipmap.front_middle_left_fault2)
                    .build());

            ISignalView frontMiddleRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_front_middle_right, false);
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 0)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 1)
                    .signalTriggeredImageResource(R.mipmap.front_middle_right_4)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 2)
                    .signalTriggeredImageResource(R.mipmap.front_middle_right_4)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 3)
                    .signalTriggeredImageResource(R.mipmap.front_middle_right_3)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 4)
                    .signalTriggeredImageResource(R.mipmap.front_middle_right_3)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 5)
                    .signalTriggeredImageResource(R.mipmap.front_middle_right_2)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, 6)
                    .signalTriggeredImageResource(R.mipmap.front_middle_right_2)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_CENTER_TO_THE_OBSTACLE, SignalViewState.INVALID)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_FRONT_MIDDLE_RIGHT_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_middle_right_fault)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_middle_right_fault2)
                    .build());
            frontMiddleRight.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                    .eventTriggeredImageResource(R.mipmap.front_middle_right_fault2)
                    .build());

            ISignalView frontRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_front_right, false);
            frontRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, 0)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, 1)
                    .signalTriggeredImageResource(R.mipmap.front_right_4)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, 2)
                    .signalTriggeredImageResource(R.mipmap.front_right_4)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, 3)
                    .signalTriggeredImageResource(R.mipmap.front_right_3)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, 4)
                    .signalTriggeredImageResource(R.mipmap.front_right_3)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setSignal(VehiclePropertyIds.DISTANCE_FROM_THE_FRONT_RIGHT_TO_THE_OBSTACLE, SignalViewState.INVALID)
                    .signalTriggeredImageResource(R.drawable.frame_n)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_FRONT_RIGHT_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_right_fault)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_ERROR)
                    .eventTriggeredImageResource(R.mipmap.front_right_fault2)
                    .build());
            frontRight.addState(createSignalViewStateBuilder()
                    .setEvent(EventIds.RVC_RADAR_SYSTEM_LIMIT)
                    .eventTriggeredImageResource(R.mipmap.front_right_fault2)
                    .build());
        }
    }
}
