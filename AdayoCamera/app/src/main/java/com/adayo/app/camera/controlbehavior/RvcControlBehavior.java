package com.adayo.app.camera.controlbehavior;

import android.car.VehiclePropertyIds;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.MainService;
import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.app.camera.signalview.CalibrationTrackLineSignalView;
import com.adayo.app.camera.signalview.GearSignalView;
import com.adayo.app.camera.signalview.RvcSpeedSV;
import com.adayo.app.camera.signalview.RvcTrackLineSV;
import com.adayo.app.camera.signalview.RvcWindowSignalView;
import com.adayo.app.camera.signalview.WindowConditionSignalView;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.controlcenter.behavior.BaseControlBehavior;
import com.adayo.proxy.aaop_camera.signalview.CameraSignalView;
import com.adayo.proxy.aaop_camera.signalview.base.ISignalView;
import com.adayo.proxy.aaop_camera.signalview.factory.SignalViewFactory;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.adayo.proxy.aaop_camera.view.AAOP_CameraView;

/**
 * @author Yiwen.Huan
 * created at 2021/11/10 13:42
 */
public class RvcControlBehavior extends BaseControlBehavior {

    long lastClickTime = 0;
    long lastClickTime1 = 0;
    RvcWindowSignalView rootSV;
    public void notifySignalDataChanged(String key, int value) {
        //todo 根据key判断切入、切出信号，
        // 当是切入时，判断rootview是否已经显示（是否显示可以根据windowSignalView的形式），如果已经显示，则return,否则执行super
        // 当是切出时，判断rootview是否已经removed（是否removed可以根据windowSignalView的形式），如果已经removed，则return,否则执行super
        if (null != rootSV) {
            if (String.valueOf(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE).equals(key)) {
                if (value != 1){
                    if (!rootSV.isShowed()) {
                        return;
                    }
                }else{
                    if (rootSV.isShowed()) {
                        return;
                    }
                }

            }
        }
        super.notifySignalDataChanged(key, value);
    }
    public RvcControlBehavior(Context mContext) {
        super(mContext);
        ModelObserver modelObserver = new ModelObserver(new Handler());
        mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("RVC_Guide"),false,modelObserver);
        Log.i("AdayoCamera", TAG + " - " + "RvcControlBehavior() called with: mContext = [" + mContext + "]");
    }

    private void initCalibration() {
        initCalibrationTrackLineSignalView();
        initRvcTrackLineSignalView();
        findViewById(R.id.iv_car).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastClickTime = System.currentTimeMillis();
                Log.d("AdayoCamera", TAG +"onLongClick:  iv_car");
                return false;
            }
        });
        findViewById(R.id.iv_behind_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastClickTime < 2000) {
                    Log.d("AdayoCamera", TAG +"onLongClick: iv_behind_right");
                    lastClickTime1 = System.currentTimeMillis();
                }
            }
        });
        findViewById(R.id.tv_caution).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - lastClickTime1 < 2000) {
                    Log.d("AdayoCamera", TAG +"onLongClick: tv_caution");
                    findViewById(R.id.container_calibration).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initCalibrationTrackLineSignalView() {
        CalibrationTrackLineSignalView calibrationTrackLineSignalView = new CalibrationTrackLineSignalView();
        initCustomSignalView(calibrationTrackLineSignalView, R.id.container_calibration, false);
        calibrationTrackLineSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.MCU_REPLY_STEERING_WHEEL_ANGLE, AAOP_Camera.INVALID)
                .build());
        calibrationTrackLineSignalView.setup("/system/etc/adayo/crtrack/CarTrackConfig.json", findViewById(R.id.m_line_view));
    }

    private void initRvcTrackLineSignalView() {
        getGlobal();
        RvcTrackLineSV rvcTrackLineSV = new RvcTrackLineSV();
        initCustomSignalView(rvcTrackLineSV, R.id.m_line_view, false);
        rvcTrackLineSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_LINE_OFF)
                .eventTriggeredVisibility(View.GONE)
                .build());
        rvcTrackLineSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.RVC_LINE_ON)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        rvcTrackLineSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.MCU_REPLY_STEERING_WHEEL_ANGLE, AAOP_Camera.INVALID)
                .build());
        rvcTrackLineSV.setUp("/system/etc/adayo/crtrack/CarTrackConfigDemo.json");
    }

    private  void  getGlobal(){
        try {
            int  getlineStatus = Settings.Global.getInt(rootView.getContext().getApplicationContext().getContentResolver(),"RVC_Guide");
            Log.d(TAG, "getGlobal: getlineStatus === " +getlineStatus);
            if (getlineStatus ==0){
                AAOP_Camera.sendEvent(EventIds.RVC_LINE_OFF);
            }else{
                AAOP_Camera.sendEvent(EventIds.RVC_LINE_ON);
            }
        } catch (Settings.SettingNotFoundException e) {
            Log.d(TAG, "getGlobal: fail ");
        }

    }
    private class ModelObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public ModelObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            int lineStatus =Settings.Global.getInt(rootView.getContext().getApplicationContext().getContentResolver(),"RVC_Guide", 0);
            if (lineStatus == 0){
                AAOP_Camera.sendEvent(EventIds.RVC_LINE_OFF);
            }else{
                AAOP_Camera.sendEvent(EventIds.RVC_LINE_ON);
            }
            super.onChange(selfChange);
        }
    }

    /**
     * ********************分割线******************************************
     */

    @Override
    protected int getLayoutResource() {
        return R.layout.rvc_camera;
    }

    @Override
    protected ISignalView produceRootSignalView(View rootView) {
        ISignalView root = new WindowConditionSignalView();
        return root;

    }

    @Override
    public void init() {

         rootSV = new RvcWindowSignalView();
        initCustomSignalView(rootSV,rootView,false);
        rootSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE, 1)
                .signalTriggeredWindowBehavior(true)
                .build());
        rootSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE, AAOP_Camera.INVALID)
                .signalTriggeredWindowBehavior(false)
                .build());
        initCalibration();

        AAOP_CameraView mCameraView = findViewById(R.id.m_camera);
        ISignalView cameraSignalView = createSignalView(SignalViewFactory.CAMERA, mCameraView, false);
        if (cameraSignalView instanceof CameraSignalView) {
            ((CameraSignalView) cameraSignalView).setCallback(rootView, EventIds.CAMERA_ERROR, EventIds.CAMERA_NORMAL);
        }

        ISignalView bgCameraErrorSV = createSignalView(SignalViewFactory.NORMAL, R.id.bg_camera_error, false);
        bgCameraErrorSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .eventTriggeredVisibility(View.GONE)
                .build());
        bgCameraErrorSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());

        ISignalView tvCameraErrorSV = createSignalView(SignalViewFactory.NORMAL, R.id.tv_camera_error, false);
        tvCameraErrorSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvCameraErrorSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());

        ISignalView bgSpeedErrorSV = new RvcSpeedSV();
        initCustomSignalView(bgSpeedErrorSV, R.id.bg_speed_error, false);
        bgSpeedErrorSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.VEHICLE_SPEED, AAOP_Camera.INVALID)
                .build());

        ISignalView tvSpeedSV = new RvcSpeedSV();
        initCustomSignalView(tvSpeedSV, R.id.tv_speed_error, false);
        tvSpeedSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.VEHICLE_SPEED, AAOP_Camera.INVALID)
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
                .setSignal(VehiclePropertyIds.DISTANCE_FROM_REAR_LEFT_TO_OBSTACLE, 0)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .build());
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

        ISignalView gearSignalView = new GearSignalView();
        initCustomSignalView(gearSignalView, null, false);
        gearSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE, SignalViewState.INVALID)
                .build());


    }

    @Override
    public void notifyEventComing(int eventId) {
        if (eventId == EventIds.CAMERA_ERROR){
            if (!rootSV.isShowed()){
                return;
            }
        }
        super.notifyEventComing(eventId);
    }
}
