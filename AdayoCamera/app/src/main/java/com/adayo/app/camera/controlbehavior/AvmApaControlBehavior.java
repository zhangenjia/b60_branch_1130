package com.adayo.app.camera.controlbehavior;

import android.car.VehiclePropertyIds;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.app.camera.signalview.AnimatorBackTrackSV;
import com.adayo.app.camera.signalview.AnimatorCarColorSV;
import com.adayo.app.camera.signalview.ApaErrorTimerSignalView;
import com.adayo.app.camera.signalview.ApaEscStopDiSignalView;
import com.adayo.app.camera.signalview.ApaSettingButtonView;
import com.adayo.app.camera.signalview.ApaTipsTimerSignalView;
import com.adayo.app.camera.signalview.AvmApaSignalView;
import com.adayo.app.camera.signalview.AvmBtnSignalView;
import com.adayo.app.camera.signalview.AvmErrorTimerSignalView;
import com.adayo.app.camera.signalview.AvmMoreAngleSignalView;
import com.adayo.app.camera.signalview.AvmApaWindowSignalView;
import com.adayo.app.camera.signalview.AvmRadarBtnSignalView;
import com.adayo.app.camera.signalview.AvmSettingButtonView;
import com.adayo.app.camera.signalview.AvmTipsTimerSignalView;
import com.adayo.app.camera.signalview.BrightnessSignalView;
import com.adayo.app.camera.signalview.CancelTimerSignalView;
import com.adayo.app.camera.signalview.GearSignalView;
import com.adayo.app.camera.signalview.LineCalibrationConditionSignalView;
import com.adayo.app.camera.signalview.LineCalibrationFailedTimerSignalView;
import com.adayo.app.camera.signalview.LineModeSignalView;
import com.adayo.app.camera.signalview.RadarBeepSignalView;
import com.adayo.app.camera.signalview.RoadCalibratingTimerSV;
import com.adayo.app.camera.signalview.RoadCalibrationConditionSignalView;
import com.adayo.app.camera.signalview.TrackDistanceSignalView;
import com.adayo.app.camera.signalview.TtsSignalView;
import com.adayo.app.camera.signalview.WindowConditionSignalView;
import com.adayo.app.camera.skin.SkinAttrs;
import com.adayo.app.camera.utils.Utils;
import com.adayo.app.camera.view.ParkingView;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.controlcenter.behavior.BaseControlBehavior;
import com.adayo.proxy.aaop_camera.signalview.CameraSignalView;
import com.adayo.proxy.aaop_camera.signalview.WindowSignalView;
import com.adayo.proxy.aaop_camera.signalview.base.ISignalView;
import com.adayo.proxy.aaop_camera.signalview.factory.SignalViewFactory;
import com.adayo.proxy.aaop_camera.signalview.state.Signal;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.adayo.proxy.aaop_camera.view.AAOP_CameraView;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

/**
 * @author Yiwen.Huan
 * created at 2021/8/11 11:45
 */
public class AvmApaControlBehavior extends BaseControlBehavior implements View.OnClickListener  {
    ParkingView mParkingView;
    AvmApaWindowSignalView windowSignalView;
    AvmApaSignalView bgAvm;
    AvmApaSignalView bgAap;
    int currentSignal;
    public void notifySignalDataChanged(String key, int value) {
        if (currentSignal == 255){
            if (currentSignal == value){
                return;
            }
        }
        currentSignal = value;
        //todo 根据key判断切入、切出信号，
        // 当是切入时，判断rootview是否已经显示（是否显示可以根据windowSignalView的形式），如果已经显示，则return,否则执行super
        // 当是切出时，判断rootview是否已经removed（是否removed可以根据windowSignalView的形式），如果已经removed，则return,否则执行super
        if (null != windowSignalView) {
            if (String.valueOf(VehiclePropertyIds.APA_CUT_SOURCE).equals(key)) {
                if (value == 0) {
                    if (!windowSignalView.isShowed()) {
                        return;
                    }
                }else {
                        if ( windowSignalView.isShowed()) {
                           if (value ==2 && bgAvm.getView().getVisibility() == View.VISIBLE){
                               return;
                           }else if(value == 1 && bgAap.getView().getVisibility() == View.VISIBLE){
                               return;
                           }
                        }else{
                            if (value == 255){
                                return;
                            }
                        }
                    }
            }
        }

        super.notifySignalDataChanged(key, value);

    }
    public AvmApaControlBehavior(Context cxt) {
        super(cxt);
        Log.i("AdayoCamera", TAG + " - " + "AvmApaControlBehavior() called with: cxt = [" + cxt + "]");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.avm_view_avm;
    }

    @Override
    protected ISignalView produceRootSignalView(View rootView) {
        ISignalView root = new WindowConditionSignalView();
        return root;

    }

    @Override
    public void init() {

        windowSignalView = new AvmApaWindowSignalView();
        initCustomSignalView(windowSignalView,rootView,false);
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SOURCE_IN, 1)
                .eventTriggeredWindowBehaviorIf(true, new Signal(VehiclePropertyIds.APA_CUT_SOURCE, 0, 2))
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 2)
                .signalTriggeredWindowBehavior(true)
                .signalTriggeredEvent(EventIds.AVM_DISPLAY_FROM_SIGNAL)
                .setNoResponseEvent(EventIds.AVM_DISPLAY_WITH_ERROR, EventIds.SIGNAL_RECOVER)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY)
                .eventTriggeredSignal(VehiclePropertyIds.APA_VIEW_SOURCE_SWITCH_IN, 1)
                .eventTriggeredWindowBehaviorIf(true, new Signal(VehiclePropertyIds.APA_CUT_SOURCE, 0, 1))
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredEvent(EventIds.APA_DISPLAY_FROM_SIGNAL)
                .signalTriggeredWindowBehavior(true)
                .setNoResponseEvent(EventIds.APA_DISPLAY_WITH_ERROR, EventIds.SIGNAL_RECOVER)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_BTN_HIDE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_VIEW_SOURCE_SWITCH_IN, 1)
                .eventTriggeredWindowBehaviorIf(true, new Signal(VehiclePropertyIds.APA_CUT_SOURCE, 0, 1))
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredEvent(EventIds.APA_DISPLAY_FROM_SIGNAL)
                .signalTriggeredWindowBehavior(true)
                .setNoResponseEvent(EventIds.APA_DISPLAY_WITH_ERROR, EventIds.SIGNAL_RECOVER)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SOURCE_OUT, 1)
                .eventTriggeredWindowBehavior(false)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0)
                .signalTriggeredWindowBehavior(false)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HIDE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_VIEW_SOURCE_SWITCH_OUT, 1)
                .eventTriggeredWindowBehavior(false)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0)
                .signalTriggeredWindowBehavior(false)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY_WITH_ERROR)
                .eventTriggeredWindowBehavior(true)
                .build());

        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_EXIT_POSITIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_VIEW_SOURCE_SWITCH_OUT, 1)
                .eventTriggeredWindowBehavior(false)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_WITH_ERROR)
                .eventTriggeredWindowBehavior(true)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_DISPLAY)
                .eventTriggeredWindowBehavior(true)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_FOR_ROAD_DISPLAY)
                .eventTriggeredWindowBehavior(true)
                .build());
        bgAvm = new AvmApaSignalView();
        initCustomSignalView(bgAvm, R.id.bg_avm, false);
//        bgAvm = createSignalView(SignalViewFactory.NORMAL, R.id.bg_avm, false);
        bgAvm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        bgAvm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());
        bgAvm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_WITH_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        bgAvm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_FOR_ROAD_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        bgAvm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        bgAap = new AvmApaSignalView();
        initCustomSignalView(bgAap, R.id.bg_apa, false);
//        ISignalView bgAap = createSignalView(SignalViewFactory.NORMAL, R.id.bg_apa, false);
        bgAap.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HIDE)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        bgAap.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        bgAap.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY_WITH_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        bgAap.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .eventTriggeredVisibility(View.GONE)
                .build());
        ISignalView btnApa = createSignalView(SignalViewFactory.NORMAL, R.id.btn_apa, true);
        btnApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_BTN_HIDE)
                .setSignal(VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING,1)
                .signalTriggeredViewEnable(true)
                .build());
        btnApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_BTN_HIDE)
                .setSignal(VehiclePropertyIds.EMS_EMS5_ST_ENGINERUNNING,SignalViewState.INVALID)
                .signalTriggeredViewEnable(false)
                .build());
        ISignalView btnHome = createSignalView(SignalViewFactory.NORMAL, R.id.btn_home, true);
        btnHome.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());
        btnHome.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HOME_TIPS_DISPLAY)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ISignalView btnLowerBr = createSignalView(SignalViewFactory.NORMAL,R.id.btn_lower_br,false);
        btnLowerBr.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE,2)
                .signalTriggeredSelected(true)
                .build());
        btnLowerBr.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE,SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());

        AAOP_CameraView mCameraView = findViewById(R.id.m_camera_view);
        ISignalView cameraSignalView = createSignalView(SignalViewFactory.CAMERA, mCameraView, false);
        if (cameraSignalView instanceof CameraSignalView) {
            ((CameraSignalView) cameraSignalView).setCallback(rootView, EventIds.CAMERA_ERROR, EventIds.CAMERA_NORMAL, VehiclePropertyIds.AVM_ST_TOUCHPRESS_AND_POINT, 1, 0, 60, 5);
        }

        RadarBeepSignalView radarBeepSignalView = new RadarBeepSignalView();
        initCustomSignalView(radarBeepSignalView, R.id.iv_exit_normal, false);
        radarBeepSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_BUZZERALARMPATTERN, SignalViewState.INVALID)
                .build());

        View containerDialogTips = findViewById(R.id.dialog_tv_home_tips);
        View tipsView = findViewById(R.id.tv_home_tips);
        ISignalView tipsSignalView = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, R.id.dialog_tv_home_tips, false);
        Utils.bindDialogAnimatorSignalView(tipsSignalView, containerDialogTips, tipsView);
        tipsSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HOME_TIPS_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        tipsSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HOME_TIPS_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .build());

        View dialogTime = findViewById(R.id.dialog_time);
        ISignalView dialogTimeView = new AvmTipsTimerSignalView();
        initCustomSignalView(dialogTimeView,dialogTime,false);
        dialogTimeView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HOME_TIPS_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredTimerBehavior(true, 3000, 0)
                .build());
        dialogTimeView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HOME_TIPS_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());


        View errorView = findViewById(R.id.tv_hint_error_evs);
        ISignalView evsErrorSignalView = new AvmErrorTimerSignalView();
        initCustomSignalView(evsErrorSignalView, errorView, false);
        evsErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredTimerBehavior(true, 5000, 0)
                .build());
        evsErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .eventTriggeredVisibility(View.GONE)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());
        evsErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .build());
        evsErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_FROM_SIGNAL)
                .build());

        View errorView2 = findViewById(R.id.tv_hint_error_avm);
        ISignalView avmErrorSignalView = new AvmErrorTimerSignalView();
        initCustomSignalView(avmErrorSignalView, errorView2, false);
        avmErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .setSignal(VehiclePropertyIds.AVM2_ERROR_VIEWOUTPUT, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredTimerBehavior(true, 5000, 0)
                .signalTriggeredEvent(EventIds.ENABLE_FALSE)
                .build());
        avmErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_FROM_SIGNAL)
                .setSignal(VehiclePropertyIds.AVM2_ERROR_VIEWOUTPUT, 0)
                .signalTriggeredEvent(EventIds.ENABLE_RECOVER)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .build());

        View errorView3 = findViewById(R.id.tv_hint_error_avm_1);
        ISignalView avmErrorSignalView1 = new AvmErrorTimerSignalView();
        initCustomSignalView(avmErrorSignalView1, errorView3, false);
        avmErrorSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .setSignal(VehiclePropertyIds.AVM2_ERROR_CONTROLLER, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredTimerBehavior(true, 5000, 0)
                .signalTriggeredEvent(EventIds.ENABLE_FALSE)
                .build());
        avmErrorSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_FROM_SIGNAL)
                .setSignal(VehiclePropertyIds.AVM2_ERROR_CONTROLLER, 0)
                .signalTriggeredEvent(EventIds.ENABLE_RECOVER)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .build());
        View errorView4 = findViewById(R.id.tv_hint_error_avm_2);
        ISignalView avmErrorSignalView2 = new AvmErrorTimerSignalView();
        initCustomSignalView(avmErrorSignalView2, errorView4, false);
        avmErrorSignalView2.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0xFF)
                .signalTriggeredEvent(EventIds.ENABLE_FALSE)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredTimerBehavior(true, 5000, 0)
                .build());
        avmErrorSignalView2.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_FROM_SIGNAL)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, SignalViewState.INVALID)
                .signalTriggeredEvent(EventIds.ENABLE_RECOVER)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .build());

        ISignalView exitSignalView = createSignalView(SignalViewFactory.IMAGE, R.id.iv_exit_normal, true);
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .setSignal(VehiclePropertyIds.AVM2_ST_QUITAVMENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .setSignal(VehiclePropertyIds.AVM2_ST_QUITAVMENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 3)
                .signalTriggeredVisibility(View.GONE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());

        ISignalView exitTvSignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_exit, false);
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .setSignal(VehiclePropertyIds.AVM2_ST_QUITAVMENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .setSignal(VehiclePropertyIds.AVM2_ST_QUITAVMENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY)
                .eventTriggeredSelected(false)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .eventTriggeredSelected(true)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING)
                .eventTriggeredSelected(true)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        exitTvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        ISignalView exitTrackingSignalView = createSignalView(SignalViewFactory.IMAGE, R.id.iv_exit_tracking, true);
        exitTrackingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        exitTrackingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .eventTriggeredVisibility(View.GONE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .eventTriggeredVisibility(View.GONE)
                .build());
        exitSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .eventTriggeredVisibility(View.GONE)
                .build());

        AvmBtnSignalView modeSignalView = new AvmBtnSignalView();
        modeSignalView.addErrorView(errorView)
                .addErrorView(errorView2)
                .addErrorView(errorView3)
                .addErrorView(errorView4);
        initCustomSignalView(modeSignalView, R.id.iv_2d3d, true);
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_2D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODESWITCHENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_2D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODESWITCHENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_2D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 0)
                .signalTriggeredImageResource(R.drawable.img_2d)
                .signalTriggeredSelected(true)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_3D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 1)
                .signalTriggeredImageResource(R.drawable.img_3d)
                .signalTriggeredSelected(true)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_3D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredSelected(true)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_3D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        modeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());
        AvmBtnSignalView exitModeSignalView = new AvmBtnSignalView();
        exitModeSignalView.addErrorView(errorView)
                .addErrorView(errorView2)
                .addErrorView(errorView3)
                .addErrorView(errorView4);
        initCustomSignalView(exitModeSignalView, R.id.tv_ad3d, false);
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_2D)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODESWITCHENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_2D)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODESWITCHENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_2D)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 0)
                .signalTriggeredSelected(true)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_3D)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 1)
                .signalTriggeredSelected(true)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_3D)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredSelected(true)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_3D)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        exitModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView lineModeSignalView = new LineModeSignalView(findViewById(R.id.iv_2d3d));
        lineModeSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(lineModeSignalView, R.id.line_model, false);
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MODESWITCHENABLE, 1)
                .signalTriggeredSelected(true)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MODESWITCHENABLE, 0)
                .signalTriggeredSelected(false)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 0)
                .signalTriggeredSelected(true)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 1)
                .signalTriggeredSelected(true)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredSelected(true)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        lineModeSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView chassisSignalView = new AvmBtnSignalView();
        chassisSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(chassisSignalView, R.id.iv_chassis, true);
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 2)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVEENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 2)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVEENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 2)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVECHASSIS, 1)
                .signalTriggeredSelected(true)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 2)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVECHASSIS, 0)
                .signalTriggeredSelected(false)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 2)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 2)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        chassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView tvChassisSignalView = new AvmBtnSignalView();
        tvChassisSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(tvChassisSignalView, R.id.tv_chassis, false);
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVEENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVEENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVECHASSIS, 1)
                .signalTriggeredSelected(true)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVECHASSIS, 0)
                .signalTriggeredSelected(false)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_CHASSIS)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        tvChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView lineChassisSignalView = new AvmBtnSignalView();
        lineChassisSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(lineChassisSignalView, R.id.line_chassis, false);
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVECHASSIS, 1)
                .signalTriggeredSelected(true)
                .build());
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_PERSPECTIVECHASSIS, 0)
                .signalTriggeredSelected(false)
                .build());
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        lineChassisSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView moreAngleSignalView = new AvmBtnSignalView();
        moreAngleSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(moreAngleSignalView, R.id.iv_more_angle, true);
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredImageResource(R.drawable.img_more_angle1)
                .signalTriggeredSelected(false)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 1)
                .signalTriggeredImageResource(R.drawable.img_more_angle1)
                .signalTriggeredSelected(true)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 2)
                .signalTriggeredImageResource(R.drawable.img_more_angle2)
                .signalTriggeredSelected(true)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 3)
                .signalTriggeredImageResource(R.drawable.img_more_angle3)
                .signalTriggeredSelected(true)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 4)
                .signalTriggeredImageResource(R.drawable.img_more_angle4)
                .signalTriggeredSelected(true)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 5)
                .signalTriggeredImageResource(R.drawable.img_more_angle5)
                .signalTriggeredSelected(true)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 1)
                .signalTriggeredImageResource(R.drawable.img_more_angle1)
                .signalTriggeredSelected(false)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 0)
                .signalTriggeredImageResource(R.drawable.img_more_angle1)
                .signalTriggeredSelected(false)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        moreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView lineMoreAngleSignalView = new AvmBtnSignalView();
        lineMoreAngleSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(lineMoreAngleSignalView, R.id.line_more, false);
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredSelected(false)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, SignalViewState.INVALID)
                .signalTriggeredSelected(true)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 1)
                .signalTriggeredSelected(false)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 0)
                .signalTriggeredSelected(false)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        lineMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        RadioGroup rgMoreAngle = findViewById(R.id.rg_more_angle);
        ISignalView rgSignalView = createSignalView(SignalViewFactory.RADIO_GROUP, rgMoreAngle, true);
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredSelectRadioBtn(SignalViewState.CLEAR)
                .build());
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 1)
                .signalTriggeredSelectRadioBtn(R.id.rb_1)
                .build());
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 2)
                .signalTriggeredSelectRadioBtn(R.id.rb_2)
                .build());
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 3)
                .signalTriggeredSelectRadioBtn(R.id.rb_3)
                .build());
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 4)
                .signalTriggeredSelectRadioBtn(R.id.rb_4)
                .build());
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 5)
                .signalTriggeredSelectRadioBtn(R.id.rb_5)
                .build());
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        rgSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());

        if (rgMoreAngle != null) {
            for (int i = 0; i < rgMoreAngle.getChildCount(); i++) {
                rgMoreAngle.getChildAt(i).setEnabled(false);
            }
        }

        ISignalView mShadowMoreAngleSignalView = createSignalView(SignalViewFactory.NORMAL, R.id.m_shadow_more_angle, true);
        mShadowMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .eventTriggeredVisibility(SignalViewState.REVERSE)
                .build());
        mShadowMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE_MORE_ANGLE)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredEvent(EventIds.AVM_HIDE_MORE_ANGLE)
                .build());


        ISignalView rbMoreAngleSignalView = new AvmMoreAngleSignalView();
        initCustomSignalView(rbMoreAngleSignalView, R.id.rb_more_angle, false);
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE_MORE_ANGLE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .eventTriggeredVisibility(SignalViewState.REVERSE)
                .build());
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredSelectRadioBtn(SignalViewState.CLEAR)
                .build());
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(R.id.rb_more_angle_1)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 3)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 1)
                .signalTriggeredSelectRadioBtn(R.id.rb_more_angle_1)
                .build());
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(R.id.rb_more_angle_2)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 4)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 2)
                .signalTriggeredSelectRadioBtn(R.id.rb_more_angle_2)
                .build());
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(R.id.rb_more_angle_3)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 5)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 3)
                .signalTriggeredSelectRadioBtn(R.id.rb_more_angle_3)
                .build());
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(R.id.rb_more_angle_4)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 6)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 4)
                .signalTriggeredSelectRadioBtn(R.id.rb_more_angle_4)
                .build());
        rbMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(R.id.rb_more_angle_5)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 7)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 5)
                .signalTriggeredSelectRadioBtn(R.id.rb_more_angle_5)
                .build());


        AvmBtnSignalView tvMoreAngleSignalView = new AvmBtnSignalView();
        tvMoreAngleSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(tvMoreAngleSignalView, R.id.tv_more_angle, false);
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 0)
                .signalTriggeredSelected(false)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 1)
                .signalTriggeredSelected(true)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 2)
                .signalTriggeredSelected(true)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 3)
                .signalTriggeredSelected(true)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 4)
                .signalTriggeredSelected(true)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWS, 5)
                .signalTriggeredSelected(true)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 1)
                .signalTriggeredSelected(false)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MODE, 0)
                .signalTriggeredSelected(false)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_MORE_ANGLE)
                .setSignal(VehiclePropertyIds.AVM2_ST_MOREVIEWENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        tvMoreAngleSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView ivTrackSignalView = new AvmBtnSignalView();
        ivTrackSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(ivTrackSignalView, R.id.iv_track, true);
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 0)
                .signalTriggeredSelected(false)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 1)
                .signalTriggeredSelected(false)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 2)
                .signalTriggeredSelected(false)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, SignalViewState.INVALID)
                .signalTriggeredSelected(true)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.PERF_APA_SYSTEM_STATUS, 0)
                .signalTriggeredViewEnable(false)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_FUNCTION_ACTIVE, 1)
                .setSignal(VehiclePropertyIds.PERF_APA_SYSTEM_STATUS, 1)
                .signalTriggeredViewEnable(true)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        ivTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView tvTrackSignalView = new AvmBtnSignalView();
        tvTrackSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(tvTrackSignalView, R.id.tv_track, true);
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 0)
                .signalTriggeredSelected(false)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 1)
                .signalTriggeredSelected(false)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 2)
                .signalTriggeredSelected(false)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, SignalViewState.INVALID)
                .signalTriggeredSelected(true)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.PERF_APA_SYSTEM_STATUS, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_TRACK)
                .setSignal(VehiclePropertyIds.PERF_APA_SYSTEM_STATUS, 1)
                .signalTriggeredViewEnable(true)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        tvTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView lineTrackSignalView = new AvmBtnSignalView();
        lineTrackSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(lineTrackSignalView, R.id.line_track, false);
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 1)
                .signalTriggeredSelected(false)
                .build());
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        lineTrackSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView ivSettingSignalView = new AvmBtnSignalView();
        ivSettingSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(ivSettingSignalView, R.id.iv_setting, true);
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.AVM2_ST_SETTINGENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_SETTINGENABLE, 0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredEvent(EventIds.AVM_HIDE_SETTING)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0)
                .signalTriggeredEvent(EventIds.AVM_HIDE_SETTING)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE_SETTING)
                .eventTriggeredSelected(false)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredEvent(EventIds.AVM_HIDE_SETTING)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        ivSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView tvSettingSignalView = new AvmBtnSignalView();
        tvSettingSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(tvSettingSignalView, R.id.tv_btn_setting, false);
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.AVM2_ST_SETTINGENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_SETTINGENABLE, 0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredEvent(EventIds.AVM_HIDE_SETTING)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0)
                .signalTriggeredEvent(EventIds.AVM_HIDE_SETTING)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE_SETTING)
                .eventTriggeredSelected(false)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredEvent(EventIds.AVM_HIDE_SETTING)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        tvSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView lineSetting = new AvmBtnSignalView();
        lineSetting.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(lineSetting, R.id.line_setting, false);
        lineSetting.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        lineSetting.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        lineSetting.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        lineSetting.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        lineSetting.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        lineSetting.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView ivRockSignalView = new AvmBtnSignalView();
        ivRockSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(ivRockSignalView, R.id.iv_rock, true);
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 11)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKMODEVIEW, 1)
                .setNoResponseEvent(EventIds.RES_TIMES,EventIds.RES_USE,EventIds.RES_REC_USE)
                .signalTriggeredSelected(true)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 11)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKMODEVIEW, 0)
                .signalTriggeredSelected(false)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 11)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 11)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 11)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 11)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        ivRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView tvRockSignalView = new AvmBtnSignalView();
        tvRockSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(tvRockSignalView, R.id.tv_btn_rock, true);
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKMODEVIEW, 1)
                .signalTriggeredSelected(true)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKMODEVIEW, 0)
                .signalTriggeredSelected(false)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROCK)
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        tvRockSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView lineRock = new AvmBtnSignalView();
        lineRock.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(lineRock, R.id.line_rock, false);
        lineRock.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        lineRock.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        lineRock.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKMODEVIEW, 1)
                .signalTriggeredSelected(true)
                .build());
        lineRock.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_ROCKMODEVIEW, 0)
                .signalTriggeredSelected(false)
                .build());
        lineRock.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        lineRock.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        lineRock.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        lineRock.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView ivWalkSignalView = new AvmBtnSignalView();
        ivWalkSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(ivWalkSignalView, R.id.iv_walk, true);
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 10)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMMODEVIEW, 1)
                .setNoResponseEvent(EventIds.RES_TIMES,EventIds.RES_USE,EventIds.RES_REC_USE)
                .signalTriggeredSelected(true)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 10)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMMODEVIEW, 0)
                .signalTriggeredSelected(false)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 10)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 10)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 10)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_AVMVIEWMODE, 10)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        ivWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView tvWalkSignalView = new AvmBtnSignalView();
        tvWalkSignalView.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(tvWalkSignalView, R.id.tv_btn_walk, true);
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMMODEVIEW, 1)
                .signalTriggeredSelected(true)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMMODEVIEW, 0)
                .signalTriggeredSelected(false)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMENABLE, 1)
                .signalTriggeredViewEnable(true)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_WALK)
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMENABLE, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        tvWalkSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        AvmBtnSignalView lineWalk = new AvmBtnSignalView();
        lineWalk.addErrorView(errorView)
                .addErrorView(errorView2).addErrorView(errorView3).addErrorView(errorView4);
        initCustomSignalView(lineWalk, R.id.line_walk, false);
        lineWalk.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        lineWalk.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        lineWalk.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMMODEVIEW, 1)
                .signalTriggeredSelected(true)
                .build());
        lineWalk.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_WORMMODEVIEW, 0)
                .signalTriggeredSelected(false)
                .build());
        lineWalk.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_FALSE)
                .build());
        lineWalk.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .build());
        lineWalk.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ENABLE_RECOVER)
                .build());
        lineWalk.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .build());

        ISignalView mShadowSettingSignalView = createSignalView(SignalViewFactory.NORMAL, R.id.m_shadow_setting, true);
        mShadowSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE_SETTING)
                .eventTriggeredVisibility(View.GONE)
                .build());
        mShadowSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredVisibility(SignalViewState.REVERSE)
                .build());

        ISignalView containerSettingSignalView = createSignalView(SignalViewFactory.NORMAL, R.id.container_setting, true);
        containerSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE_SETTING)
                .eventTriggeredVisibility(View.GONE)
                .build());
        containerSettingSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_SETTING)
                .eventTriggeredVisibility(SignalViewState.REVERSE)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());

        View animatorCarColor = findViewById(R.id.animator_car_color);
        TextView tvPaleGreen = findViewById(R.id.tv_color_pale_green);
        TextView tvMistyBlue = findViewById(R.id.tv_color_misty_blue);
        TextView tvSnowWhite = findViewById(R.id.tv_color_snow_white);
        TextView tvExtremeBlack = findViewById(R.id.tv_color_extreme_black);
        TextView tvLappBlue = findViewById(R.id.tv_color_lapp_blue);
        TextView tvJungleGreen = findViewById(R.id.tv_color_jungle_green);

        AnimatorCarColorSV carColorSV = new AnimatorCarColorSV(tvPaleGreen, tvMistyBlue, tvSnowWhite,tvExtremeBlack,tvLappBlue,tvJungleGreen);
        initCustomSignalView(carColorSV, animatorCarColor, false);
        carColorSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_SET_CARCOLOR, SignalViewState.INVALID)
                .build());
        ISignalView tvColorPaleGreen = createSignalView(SignalViewFactory.TEXT, tvPaleGreen, true);
        tvColorPaleGreen.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_COLOR_PALE_GREEN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 1)
                .build());

        ISignalView tvColorMistyBlue = createSignalView(SignalViewFactory.TEXT, tvMistyBlue, true);
        tvColorMistyBlue.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_COLOR_MISTY_BLUE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 2)
                .build());

        ISignalView tvColorSnowWhite = createSignalView(SignalViewFactory.TEXT, tvSnowWhite, true);
        tvColorSnowWhite.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_COLOR_SNOW_WHITE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 3)
                .build());

        ISignalView tvColorExtremeBlack = createSignalView(SignalViewFactory.TEXT, tvExtremeBlack, true);
        tvColorExtremeBlack.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_COLOR_EXTREME_BLACK)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 4)
                .build());

        ISignalView tvColorLappBlue = createSignalView(SignalViewFactory.TEXT, tvLappBlue, true);
        tvColorLappBlue.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_COLOR_LAPP_BLUE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 5)
                .build());

        ISignalView tvColorJungleGreen = createSignalView(SignalViewFactory.TEXT, tvJungleGreen, true);
        tvColorJungleGreen.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_COLOR_JUNGLE_GREEN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 6)
                .build());

        ISignalView tvSettingLineBodySignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_setting_line_body, true);
        tvSettingLineBodySignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_LINE_BODY)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 0x0B)
                .setSignal(VehiclePropertyIds.AVM2_SET_CARTRAJECTORY, 1)
                .signalTriggeredSelected(true)
                .build());
        tvSettingLineBodySignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_LINE_BODY)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 0x0B)
                .setSignal(VehiclePropertyIds.AVM2_SET_CARTRAJECTORY, 0)
                .signalTriggeredSelected(false)
                .build());

        ISignalView tvSettingLineTyreSignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_setting_line_tyre, true);
        tvSettingLineTyreSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_LINE_TYRE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 0x0A)
                .setSignal(VehiclePropertyIds.AVM2_SET_TIRETRAJECTORY, 1)
                .signalTriggeredSelected(true)
                .build());
        tvSettingLineTyreSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_LINE_TYRE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 0x0A)
                .setSignal(VehiclePropertyIds.AVM2_SET_TIRETRAJECTORY, 0)
                .signalTriggeredSelected(false)
                .build());

        ISignalView tvSettingRadar3dSignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_setting_radar_3d, true);
        tvSettingRadar3dSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_RADAR_3D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 21)
                .setSignal(VehiclePropertyIds.AVM2_SET_3DRADAR, 1)
                .signalTriggeredSelected(true)
                .build());
        tvSettingRadar3dSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_RADAR_3D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 21)
                .setSignal(VehiclePropertyIds.AVM2_SET_3DRADAR, 0)
                .signalTriggeredSelected(false)
                .build());
        ISignalView tvSettingRadar2dSignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_setting_radar_2d, true);
        tvSettingRadar2dSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_RADAR_2D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 20)
                .setSignal(VehiclePropertyIds.AVM2_SET_2DRADAR, 1)
                .signalTriggeredSelected(true)
                .build());
        tvSettingRadar2dSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_RADAR_2D)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 20)
                .setSignal(VehiclePropertyIds.AVM2_SET_2DRADAR, 0)
                .signalTriggeredSelected(false)
                .build());
        TextView tvBrightness = findViewById(R.id.tv_brightness);
        BrightnessSignalView tvSignalView = new BrightnessSignalView();
        initCustomSignalView(tvSignalView, tvBrightness, false);
//        tvSignalView.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.AVM_CLICK_SETTING)
//                .build());
//        tvSignalView.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.AVM_CHANGE_BRIGHT)
//                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_1)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 49)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 1)
                .signalTriggeredText(R.string.apa_1)
                .setNoResponseEvent(EventIds.RES_TIMES, EventIds.RES_USE, EventIds.RES_REC_USE)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_2)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 50)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 2)
                .signalTriggeredText(R.string.apa_2)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_3)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 51)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 3)
                .signalTriggeredText(R.string.apa_3)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_4)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 52)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 4)
                .signalTriggeredText(R.string.apa_4)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_5)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 53)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 5)
                .signalTriggeredText(R.string.apa_5)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_6)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 54)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 6)
                .signalTriggeredText(R.string.apa_6)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_7)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 55)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 7)
                .signalTriggeredText(R.string.apa_7)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_8)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 56)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 8)
                .signalTriggeredText(R.string.apa_8)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_9)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 57)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 9)
                .signalTriggeredText(R.string.apa_9)
                .build());
        tvSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_10)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 58)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 10)
                .signalTriggeredText(R.string.apa_10)
                .build());

        ISignalView seekBarBrightnessSignalView = createSignalView(SignalViewFactory.SEEKBAR, R.id.seek_bar_brightness, false);
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 1)
                .signalTriggeredProgress(1)
                .setNoResponseEvent(EventIds.RES_TIMES, EventIds.RES_USE, EventIds.RES_REC_USE)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 2)
                .signalTriggeredProgress(2)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 3)
                .signalTriggeredProgress(3)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 4)
                .signalTriggeredProgress(4)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 5)
                .signalTriggeredProgress(5)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 6)
                .signalTriggeredProgress(6)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 7)
                .signalTriggeredProgress(7)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 8)
                .signalTriggeredProgress(8)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 9)
                .signalTriggeredProgress(9)
                .build());
        seekBarBrightnessSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 10)
                .signalTriggeredProgress(10)
                .build());



        ISignalView seekBarBrightnessSignalView1 = createSignalView(SignalViewFactory.SEEKBAR, R.id.seek_bar_brightness_1, false);
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_1)
                .eventTriggeredProgress(1)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 1)
                .signalTriggeredProgress(1)
                .setNoResponseEvent(EventIds.RES_TIMES, EventIds.RES_USE, EventIds.RES_REC_USE)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_2)
                .eventTriggeredProgress(2)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 2)
                .signalTriggeredProgress(2)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_3)
                .eventTriggeredProgress(3)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 3)
                .signalTriggeredProgress(3)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_4)
                .eventTriggeredProgress(4)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 4)
                .signalTriggeredProgress(4)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_5)
                .eventTriggeredProgress(5)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 5)
                .signalTriggeredProgress(5)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_6)
                .eventTriggeredProgress(6)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 6)
                .signalTriggeredProgress(6)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_7)
                .eventTriggeredProgress(7)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 7)
                .signalTriggeredProgress(7)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_8)
                .eventTriggeredProgress(8)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 8)
                .signalTriggeredProgress(8)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_9)
                .eventTriggeredProgress(9)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 9)
                .signalTriggeredProgress(9)
                .build());
        seekBarBrightnessSignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_SET_CAR_BRIGHTNESS_10)
                .eventTriggeredProgress(10)
                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL, 10)
                .signalTriggeredProgress(10)
                .build());

//        SeekBar seekBarBrightness = findViewById(R.id.seek_bar_brightness);
//        BrightnessSignalView seekSignalView = new BrightnessSignalView();
//        initCustomSignalView(seekSignalView, seekBarBrightness, false);
//        seekSignalView.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.AVM_CLICK_SETTING)
//                .setSignal(VehiclePropertyIds.AVM1_ST_BRIGHTLEVEL,SignalViewState.INVALID)
//                .build());
//        seekSignalView.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.AVM_CHANGE_BRIGHT)
//                .build());



        ISignalView tvTransparencySignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_transparency, false);
        tvTransparencySignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_0)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 40)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 0)
                .signalTriggeredText(R.string.str_0)
                .setNoResponseEvent(EventIds.RES_TIMES, EventIds.RES_USE, EventIds.RES_REC_USE)
                .build());
        tvTransparencySignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_25)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 41)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 1)
                .signalTriggeredText(R.string.str_25)
                .build());
        tvTransparencySignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_50)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 42)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 2)
                .signalTriggeredText(R.string.str_50)
                .build());
        tvTransparencySignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_75)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 43)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 3)
                .signalTriggeredText(R.string.str_75)
                .build());
        tvTransparencySignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_100)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 44)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 4)
                .signalTriggeredText(R.string.str_100)
                .build());


        ISignalView seekBarTransparencySignalView = createSignalView(SignalViewFactory.SEEKBAR, R.id.seek_bar_transparency, false);
        seekBarTransparencySignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 0)
                .signalTriggeredProgress(0)
                .setNoResponseEvent(EventIds.RES_TIMES, EventIds.RES_USE, EventIds.RES_REC_USE)
                .build());
        seekBarTransparencySignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 1)
                .signalTriggeredProgress(1)
                .build());
        seekBarTransparencySignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 2)
                .signalTriggeredProgress(2)
                .build());
        seekBarTransparencySignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 3)
                .signalTriggeredProgress(3)
                .build());
        seekBarTransparencySignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 4)
                .signalTriggeredProgress(4)
                .build());


        ISignalView seekBarTransparencySignalView1 = createSignalView(SignalViewFactory.SEEKBAR, R.id.seek_bar_transparency_1, false);
        seekBarTransparencySignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_0)
                .eventTriggeredProgress(0)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 0)
                .signalTriggeredProgress(0)
                .setNoResponseEvent(EventIds.RES_TIMES, EventIds.RES_USE, EventIds.RES_REC_USE)
                .build());
        seekBarTransparencySignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_25)
                .eventTriggeredProgress(1)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 1)
                .signalTriggeredProgress(1)
                .build());
        seekBarTransparencySignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_50)
                .eventTriggeredProgress(2)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 2)
                .signalTriggeredProgress(2)
                .build());
        seekBarTransparencySignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_75)
                .eventTriggeredProgress(3)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 3)
                .signalTriggeredProgress(3)
                .build());
        seekBarTransparencySignalView1.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_CAR_TRANSPARENCY_100)
                .eventTriggeredProgress(4)
                .setSignal(VehiclePropertyIds.AVM2_ST_TRANSPARENCY, 4)
                .signalTriggeredProgress(4)
                .build());
        ISignalView tvSettingSystemOpenSignalView = new AvmSettingButtonView();
        View setOpenView = findViewById(R.id.tv_setting_radar_system_open);
        initCustomSignalView(tvSettingSystemOpenSignalView,setOpenView, true);
        tvSettingSystemOpenSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OPEN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 22)
                .setSignal(VehiclePropertyIds.AVM2_ST_RADARONOFF, 1)
                .signalTriggeredEvent(EventIds.AVM_RADAR_OPEN_RADAR)
                .signalTriggeredSelected(true)
                .build());
        tvSettingSystemOpenSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OPEN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 22)
                .setSignal(VehiclePropertyIds.AVM2_ST_RADARONOFF, 0)
                .signalTriggeredSelected(false)
                .build());

        ISignalView tvSettingSystemOffSignalView = new AvmSettingButtonView();
        View setOffView = findViewById(R.id.tv_setting_radar_system_off);
        initCustomSignalView(tvSettingSystemOffSignalView,setOffView, true);
        tvSettingSystemOffSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OFF)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 23)
                .setSignal(VehiclePropertyIds.AVM2_ST_RADARONOFF, 0)
                .signalTriggeredEvent(EventIds.AVM_RADAR_OFF_RADAR)
                .signalTriggeredSelected(true)
                .build());
        tvSettingSystemOffSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OFF)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 23)
                .setSignal(VehiclePropertyIds.AVM2_ST_RADARONOFF, 1)
                .signalTriggeredSelected(false)
                .build());

        ISignalView tvSettingTriggerLampSignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_setting_trigger_lamp, true);
        tvSettingTriggerLampSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_TURN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 30)
                .setSignal(VehiclePropertyIds.AVM2_SET_TURNTRIGGERVIEW, 1)
                .signalTriggeredSelected(true)
                .build());
        tvSettingTriggerLampSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_TURN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 30)
                .setSignal(VehiclePropertyIds.AVM2_SET_TURNTRIGGERVIEW, 0)
                .signalTriggeredSelected(false)
                .build());

        ISignalView tvSettingTriggerRadarSignalView = createSignalView(SignalViewFactory.TEXT, R.id.tv_setting_trigger_radar, true);
        tvSettingTriggerRadarSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_RADAR)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 31)
                .setSignal(VehiclePropertyIds.AVM2_SET_RADARTRIGGERVIEW, 1)
                .signalTriggeredSelected(true)
                .build());
        tvSettingTriggerRadarSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_SET_RADAR)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SETTING, 31)
                .setSignal(VehiclePropertyIds.AVM2_SET_RADARTRIGGERVIEW, 0)
                .signalTriggeredSelected(false)
                .build());
        tvSettingTriggerRadarSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OFF_RADAR)
                .eventTriggeredViewEnableState(false)
                .build());
        tvSettingTriggerRadarSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OPEN_RADAR)
                .eventTriggeredViewEnableState(true)
                .build());
        AvmRadarBtnSignalView tvAvmRadarbtn = new AvmRadarBtnSignalView();
        initCustomSignalView(tvAvmRadarbtn, R.id.tv_setting_trigger_radar_no, false);
        tvAvmRadarbtn.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OFF_RADAR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        tvAvmRadarbtn.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_RADAR_OPEN_RADAR)
                .eventTriggeredVisibility(View.GONE)
                .build());

        ISignalView ivTrackDistanceSignalView = new TrackDistanceSignalView();
        initCustomSignalView(ivTrackDistanceSignalView, R.id.tv_track_distance, true);
        ivTrackDistanceSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ivTrackDistanceSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivTrackDistanceSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivTrackDistanceSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 6)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivTrackDistanceSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 7)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivTrackDistanceSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_REMAINING_DISTANCE, SignalViewState.INVALID)
                .signalTriggeredText(R.string.str_0)
                .build());
        ImageView backView = findViewById(R.id.tv_back_arrows);
        AnimatorBackTrackSV ivBackGifSignalView =   new AnimatorBackTrackSV(backView);
        initCustomSignalView(ivBackGifSignalView, backView, false);
        ivBackGifSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ivBackGifSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivBackGifSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivBackGifSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 6)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivBackGifSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKING_STATE, 7)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());

        ISignalView ivTrackControlHint = createSignalView(SignalViewFactory.IMAGE, R.id.iv_track_control_hint, false);
        ivTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ivTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 3)
                .signalTriggeredImageResource(R.mipmap.img_step_on_the_brake)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 1)
                .signalTriggeredImageResource(R.mipmap.img_back_tracking)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ISignalView tvTrackControlHint = createSignalView(SignalViewFactory.TEXT, R.id.tv_track_control_hint, false);
        tvTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        tvTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 3)
                .signalTriggeredText(R.string.apa_view_brake_tips)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 1)
                .signalTriggeredText(R.string.track_control_hint)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvTrackControlHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView llTrackSuccess = createSignalView(SignalViewFactory.NORMAL, R.id.ll_track_success, false);
        llTrackSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        llTrackSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());
        llTrackSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        llTrackSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_TRACKING_REQUEST, 3)
                .signalTriggeredVisibility(View.GONE)
                .build());

        View dialogHintContainer = findViewById(R.id.container_dialog_hint);
        View dialogHintView = findViewById(R.id.dialog_hint);
        ISignalView dialogHint = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogHintContainer, false);
        Utils.bindDialogAnimatorSignalView(dialogHint, dialogHintContainer, dialogHintView);
        dialogHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        dialogHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 4)
                .signalTriggeredVisibility(View.GONE)
                .build());
        dialogHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 5)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView tvDialogHintContent = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_hint_content, false);
        tvDialogHintContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 1)
                .signalTriggeredText(R.string.track_dialog_content_pause_1)
                .build());
        tvDialogHintContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 2)
                .signalTriggeredText(R.string.track_dialog_content_pause_2)
                .build());
        tvDialogHintContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 3)
                .signalTriggeredText(R.string.track_dialog_content_pause_3)
                .build());

        View dialogControlContainer = findViewById(R.id.container_dialog_control);
        View dialogControlView = findViewById(R.id.dialog_control);
        ISignalView dialogControl = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogControlContainer, false);
        Utils.bindDialogAnimatorSignalView(dialogControl, dialogControlContainer, dialogControlView);
        dialogControl.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogControl.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogControl.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvDialogControlTitle = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_control_title, true);
        tvDialogControlTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 4)
                .signalTriggeredText(R.string.track_dialog_title_recover)
                .build());
        tvDialogControlTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 5)
                .signalTriggeredText(R.string.track_dialog_title_exit)
                .build());
        ISignalView tvDialogControlContent = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_control_content, true);
        tvDialogControlContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 4)
                .signalTriggeredText(R.string.track_dialog_content_recover)
                .build());
        tvDialogControlContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 5)
                .signalTriggeredText(R.string.track_dialog_content_exit)
                .build());

        ISignalView tvDialogPositive = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_positive, true);
        tvDialogPositive.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.TRACK_DIALOG_CONTROL_POSITIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_RECOVER, 1)
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 4)
                .build());
        tvDialogPositive.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.TRACK_DIALOG_EXIT_POSITIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_TRACKING_QUIT_CONFIRM, 1)
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 5)
                .build());

        ISignalView tvDialogNegative = new CancelTimerSignalView();
        initCustomSignalView(tvDialogNegative, R.id.tv_dialog_negative, true);
        tvDialogNegative.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.TRACK_DIALOG_CONTROL_NEGATIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_REVERSE_TRACKING_RECOVER, 2)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 4)
                .signalTriggeredTimerBehavior(true, 0, 1000)
                .build());
        tvDialogNegative.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.TRACK_DIALOG_EXIT_NEGATIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_TRACKING_QUIT_CONFIRM, 2)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, 5)
                .signalTriggeredTimerBehavior(true, 0, 1000)
                .build());
        tvDialogNegative.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_RECOVER_INDICATION, SignalViewState.INVALID)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .build());

        View dialogControlExitContainer = findViewById(R.id.container_dialog_control_exit);
        View dialogControlExit = findViewById(R.id.dialog_control_exit);
        ISignalView containerDialogControlExit = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogControlExitContainer, false);
        Utils.bindDialogAnimatorSignalView(containerDialogControlExit, dialogControlExitContainer, dialogControlExit);
        containerDialogControlExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        containerDialogControlExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING_NEGATIVE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        containerDialogControlExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING_POSITIVE)
                .eventTriggeredVisibility(View.GONE)
                .build());


        ISignalView tvDialogPositiveExit = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_positive_exit, true);
        tvDialogPositiveExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING_POSITIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_TRACKING_QUIT_CONFIRM, 1)
                .build());

        ISignalView tvDialogNegativeExit = new CancelTimerSignalView();
        initCustomSignalView(tvDialogNegativeExit, R.id.tv_dialog_negative_exit, true);
        tvDialogNegativeExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING_NEGATIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_TRACKING_QUIT_CONFIRM, 2)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());
        tvDialogNegativeExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_EXIT_TRACKING)
                .eventTriggeredSignal(VehiclePropertyIds.APA_TRACKING_QUIT_CONFIRM, 2)
                .eventTriggeredTimerBehavior(true, 0, 1000)
                .build());

        View dialogStopContainer = findViewById(R.id.container_dialog_stop);
        View dialogStopView = findViewById(R.id.dialog_stop);
        ISignalView dialogStop = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogStopContainer, false);
        Utils.bindDialogAnimatorSignalView(dialogStop, dialogStopContainer, dialogStopView);
        dialogStop.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        dialogStop.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());


        ISignalView tvDialogStopContent = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_stop_content, false);
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 1)
                .signalTriggeredText(R.string.track_dialog_content_stop_1)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 2)
                .signalTriggeredText(R.string.track_dialog_content_stop_2)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 3)
                .signalTriggeredText(R.string.track_dialog_content_stop_3)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 4)
                .signalTriggeredText(R.string.track_dialog_content_stop_4)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 5)
                .signalTriggeredText(R.string.track_dialog_content_stop_5)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 6)
                .signalTriggeredText(R.string.track_dialog_content_stop_6)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 7)
                .signalTriggeredText(R.string.track_dialog_content_stop_7)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 8)
                .signalTriggeredText(R.string.track_dialog_content_stop_8)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 9)
                .signalTriggeredText(R.string.track_dialog_content_stop_9)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 10)
                .signalTriggeredText(R.string.track_dialog_content_stop_10)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 11)
                .signalTriggeredText(R.string.track_dialog_content_stop_11)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 12)
                .signalTriggeredText(R.string.track_dialog_content_stop_12)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 13)
                .signalTriggeredText(R.string.track_dialog_content_stop_13)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 14)
                .signalTriggeredText(R.string.track_dialog_content_stop_14)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 15)
                .signalTriggeredText(R.string.track_dialog_content_stop_15)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 16)
                .signalTriggeredText(R.string.track_dialog_content_stop_16)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 17)
                .signalTriggeredText(R.string.track_dialog_content_stop_17)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 18)
                .signalTriggeredText(R.string.track_dialog_content_stop_18)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 19)
                .signalTriggeredText(R.string.track_dialog_content_stop_19)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 20)
                .signalTriggeredText(R.string.track_dialog_content_stop_20)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 21)
                .signalTriggeredText(R.string.track_dialog_content_stop_21)
                .build());
        tvDialogStopContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TRACKONG_QUIT_INDICATION, 22)
                .signalTriggeredText(R.string.track_dialog_content_stop_22)
                .build());


        View speedContainer = findViewById(R.id.fl_hint_speed);
        TextView speed = findViewById(R.id.tv_hint_speed);
        ISignalView hintSpeedSignalView = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, speedContainer, false);
        Utils.bindDialogAnimatorSignalView(hintSpeedSignalView, speedContainer, speed);
        hintSpeedSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_PROMPTMESSAGE, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        hintSpeedSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_PROMPTMESSAGE, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());

        View dialogSignalDiedContainer = findViewById(R.id.container_dialog_signal_error);
        View dialogSignalDiedView = findViewById(R.id.dialog_signal_error);
        ISignalView dialogSignalDied = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogSignalDiedContainer, false);
        Utils.bindDialogAnimatorSignalView(dialogSignalDied, dialogSignalDiedContainer, dialogSignalDiedView);
        dialogSignalDied.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        dialogSignalDied.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        dialogSignalDied.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_WITH_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        dialogSignalDied.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_RECOVER)
                .eventTriggeredVisibility(View.GONE)
                .build());


        ISignalView tvDialogSignalDiedPositive = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_signal_error_positive, true);
        tvDialogSignalDiedPositive.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .build());

        ISignalView tvDialogSignalDiedNegative = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_signal_error_negative, true);
        tvDialogSignalDiedNegative.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_RECOVER)
                .build());
        tvDialogSignalDiedNegative.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_DISPLAY_WITH_ERROR)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvDialogSignalDiedNegative.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());


        ISignalView gearSignalView = new GearSignalView();
        initCustomSignalView(gearSignalView, null, false);
        gearSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.DRIVER_DISPLAY_DRIVE_MODE_VALUE, SignalViewState.INVALID)
                .build());

        View containerDialogLineCalibrationFailed = findViewById(R.id.container_dialog_line_calibration_failed);
        View dialogLineCalibrationFailed = findViewById(R.id.dialog_line_calibration_failed);
        ISignalView lineCalibrationConditionSV = new LineCalibrationConditionSignalView(this);
        initCustomSignalView(lineCalibrationConditionSV, containerDialogLineCalibrationFailed, false);

        ISignalView containerDialogLineCalibrationFailedSV = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogLineCalibrationFailed, false);
        Utils.bindDialogAnimatorSignalView(containerDialogLineCalibrationFailedSV, containerDialogLineCalibrationFailed, dialogLineCalibrationFailed);
        containerDialogLineCalibrationFailedSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.LINE_CALIBRATION_FAILED_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerDialogLineCalibrationFailedSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView lineCalibrationConditionSV2 = new LineCalibrationConditionSignalView(this);
        initCustomSignalView(lineCalibrationConditionSV2, R.id.tv_dialog_line_calibration_failed_content, false);

        ISignalView lineCalibrationFailedTimerSV = new LineCalibrationFailedTimerSignalView();
        initCustomSignalView(lineCalibrationFailedTimerSV, R.id.tv_dialog_line_calibration_failed_content, false);
        lineCalibrationFailedTimerSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 3)
                .signalTriggeredTimerBehavior(true, 0, 1000)
                .signalTriggeredText(R.string.line_calibration_failed_content)
                .build());
        lineCalibrationFailedTimerSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .signalTriggeredText(R.string.line_calibration_failed_content)
                .build());


        View containerDialogLIneCalibrating = findViewById(R.id.container_dialog_line_calibrating);
        View dialogLineCalibration = findViewById(R.id.dialog_line_calibrating);
        ISignalView lineCalibrationConditionSV3 = new LineCalibrationConditionSignalView(this);
        initCustomSignalView(lineCalibrationConditionSV3, containerDialogLIneCalibrating, false);

        ISignalView containerDialogLineCalibration = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogLIneCalibrating, false);
        Utils.bindDialogAnimatorSignalView(containerDialogLineCalibration, containerDialogLIneCalibrating, dialogLineCalibration);
        containerDialogLineCalibration.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerDialogLineCalibration.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());


        View containerDialogRoadCalibrationFailed = findViewById(R.id.container_dialog_road_calibration_failed);
        View dialogRoadCalibrationFailed = findViewById(R.id.dialog_road_calibration_failed);
        ISignalView roadCondition1 = new RoadCalibrationConditionSignalView(this);
        initCustomSignalView(roadCondition1, containerDialogRoadCalibrationFailed, false);
        ISignalView containerRoadFailed = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogRoadCalibrationFailed, false);
        Utils.bindDialogAnimatorSignalView(containerRoadFailed, containerDialogRoadCalibrationFailed, dialogRoadCalibrationFailed);
        containerRoadFailed.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_ENTER_HINT)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerRoadFailed.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView reRoadCalibration = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_road_calibration_failed_positive, true);
        reRoadCalibration.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_ENTER_HINT)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_SET_SOURCE_OUT, 1)
                .build());
        ISignalView exitRoadCalibration = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_road_calibration_failed_negative, true);
        exitRoadCalibration.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .build());

        View containerDialogRoadEnterConfirm = findViewById(R.id.container_dialog_road_enter_confirm);
        View dialogRoadEnterConfirm = findViewById(R.id.dialog_road_enter_confirm);
        ISignalView containerRoadConfirm = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogRoadEnterConfirm, false);
        Utils.bindDialogAnimatorSignalView(containerRoadConfirm, containerDialogRoadEnterConfirm, dialogRoadEnterConfirm);
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CLICK_ROAD_CALIBRATION_ENTER_HINT)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_FOR_ROAD_DISPLAY)
                .eventTriggeredVisibility(View.GONE)
                .build());
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, AAOP_Camera.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView dialogRoadConfirmPositive = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_road_enter_confirm_positive, true);
        dialogRoadConfirmPositive.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CLICK_ROAD_CALIBRATION_ENTER_HINT_CONFIRM)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_ST_AVMROADCALIBRATION, 1)
                .build());
        ISignalView dialogRoadConfirmExit = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_road_enter_confirm_negative, true);
        dialogRoadConfirmExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .build());

        View containerDialogRoadCalibrationSpeed = findViewById(R.id.container_dialog_road_calibration_speed);
        View dialogRoadCalibrationSpeed = findViewById(R.id.dialog_road_calibration_speed);
        ISignalView containerRoadSpeed = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogRoadCalibrationSpeed, false);
        Utils.bindDialogAnimatorSignalView(containerRoadSpeed, containerDialogRoadCalibrationSpeed, dialogRoadCalibrationSpeed);
        containerRoadSpeed.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 6)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerRoadSpeed.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView roadSpeedExit = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_calibration_speed_button, true);
        roadSpeedExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_QUIT)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_ST_AVMROADCALIBRATION,2)
                .build());

        View containerDialogRoadCalibrationSuccess = findViewById(R.id.container_dialog_road_calibration_success);
        View dialogRoadCalibrationSuccess = findViewById(R.id.dialog_road_calibration_success);
        ISignalView containerCalibrationSuccess = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogRoadCalibrationSuccess, false);
        Utils.bindDialogAnimatorSignalView(containerCalibrationSuccess, containerDialogRoadCalibrationSuccess, dialogRoadCalibrationSuccess);
        containerCalibrationSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredEvent(EventIds.CALIBRATION_SUCCESS)
                .build());
        containerCalibrationSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());


        View containerDialogRoadCalibrating = findViewById(R.id.container_dialog_road_calibrating);
        View dialogRoadCalibrating = findViewById(R.id.dialog_road_calibrating);
        ISignalView roadCalibratingCondition = new RoadCalibrationConditionSignalView(this);
        initCustomSignalView(roadCalibratingCondition, containerDialogRoadCalibrating, false);
        ISignalView containerRoadCalibrating = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogRoadCalibrating, false);
        Utils.bindDialogAnimatorSignalView(containerRoadCalibrating, containerDialogRoadCalibrating, dialogRoadCalibrating);
        containerRoadCalibrating.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 1)
                .signalTriggeredEvent(EventIds.AVM_FOR_ROAD_DISPLAY)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerRoadCalibrating.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView roadCalibratingCondition2 = new RoadCalibrationConditionSignalView(this);
        initCustomSignalView(roadCalibratingCondition2, R.id.tv_dialog_road_calibrating_title, false);
        ISignalView roadCalibratingTimerSV = new RoadCalibratingTimerSV();
        initCustomSignalView(roadCalibratingTimerSV, R.id.tv_dialog_road_calibrating_title, false);
        roadCalibratingTimerSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 1)
                .signalTriggeredTimerBehavior(true, 0, 1000)
                .build());
        roadCalibratingTimerSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .build());

        View containerDialogCalibrationEnterFailed = findViewById(R.id.container_dialog_calibration_enter_failed);
        View dialogCalibrationEnterFailed = findViewById(R.id.dialog_calibration_enter_failed);
        ISignalView containerEnterCalibrationFailed = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogCalibrationEnterFailed, false);
        Utils.bindDialogAnimatorSignalView(containerEnterCalibrationFailed, containerDialogCalibrationEnterFailed, dialogCalibrationEnterFailed);
        containerEnterCalibrationFailed.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerEnterCalibrationFailed.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerEnterCalibrationFailed.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerEnterCalibrationFailed.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerEnterCalibrationFailed.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView enterCalibrationContent = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_calibration_enter_failed_content, false);
        enterCalibrationContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 4)
                .signalTriggeredText(R.string.road_calibration_start_failed_content_camera)
                .build());
        enterCalibrationContent.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 5)
                .signalTriggeredText(R.string.road_calibration_start_failed_content_door)
                .build());

        ISignalView containerEnterRoadHint = createSignalView(SignalViewFactory.NORMAL, R.id.container_road_calibration, false);
        containerEnterRoadHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerEnterRoadHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerEnterRoadHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView enterRoadPositive = createSignalView(SignalViewFactory.NORMAL, R.id.tv_road_calibration_hint_positive, true);
        enterRoadPositive.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CLICK_ROAD_CALIBRATION_ENTER_HINT)
                .build());
        ISignalView enterRoadNegative = createSignalView(SignalViewFactory.NORMAL, R.id.tv_road_calibration_hint_negative, true);
        enterRoadNegative.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HIDE)
                .build());

        View dialogTimeApa = findViewById(R.id.dialog_time);
        ISignalView dialogTimeViewApa = new ApaTipsTimerSignalView();
        initCustomSignalView(dialogTimeViewApa,dialogTimeApa,false);
        dialogTimeViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HOME_TIPS_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredTimerBehavior(true, 3000, 0)
                .build());
        dialogTimeViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HOME_TIPS_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());

        ISignalView btnHomeApa = createSignalView(SignalViewFactory.NORMAL, R.id.apa_btn_home, true);
        btnHomeApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());
        btnHomeApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HOME_TIPS_DISPLAY)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        View containerDialogTipsApa = findViewById(R.id.apa_dialog_tv_home_tips);
        View tipsViewApa = findViewById(R.id.apa_tv_home_tips);
        ISignalView tipsSignalViewApa = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, R.id.apa_dialog_tv_home_tips, false);
        Utils.bindDialogAnimatorSignalView(tipsSignalViewApa, containerDialogTipsApa, tipsViewApa);
        tipsSignalViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HOME_TIPS_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        tipsSignalViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HOME_TIPS_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .build());

        ISignalView tipsStopSignalView = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, R.id.dialog_tv_stop_di, false);
        tipsStopSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKED_STATE,3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tipsStopSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKED_STATE,SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ISignalView escStopDiSignalView = new ApaEscStopDiSignalView();
        initCustomSignalView(escStopDiSignalView, R.id.tv_stop_di, true);
        escStopDiSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_N_ESCSTOPDI,SignalViewState.INVALID)
                .signalTriggeredText(R.string.str_0)
                .build());

        ISignalView ivBerthIn = createSignalView(SignalViewFactory.IMAGE, R.id.iv_berth_in, true);
        ivBerthIn.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 1)
                .signalTriggeredSelected(true)
                .build());
        ivBerthIn.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 1)
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        ivBerthIn.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 1)
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_IN, 0)
                .signalTriggeredViewEnable(false)
                .build());
        ivBerthIn.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 1)
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_IN, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView tvBerthIn = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_in, false);
        tvBerthIn.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 1)
                .signalTriggeredSelected(true)
                .build());
        tvBerthIn.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        tvBerthIn.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_IN, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvBerthIn.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_IN, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView lineBerthIn = createSignalView(SignalViewFactory.NORMAL, R.id.line_berth_in, false);
        lineBerthIn.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 1)
                .signalTriggeredSelected(true)
                .build());
        lineBerthIn.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());


        ISignalView ivBerthOut = createSignalView(SignalViewFactory.IMAGE, R.id.iv_berth_out, true);
        ivBerthOut.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 2)
                .signalTriggeredSelected(true)
                .build());
        ivBerthOut.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 2)
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        ivBerthOut.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 2)
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_OUT, 0)
                .signalTriggeredViewEnable(false)
                .build());
        ivBerthOut.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 2)
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_OUT, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView tvBerthOut = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_out, false);
        tvBerthOut.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 2)
                .signalTriggeredSelected(true)
                .build());
        tvBerthOut.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        tvBerthOut.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_OUT, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvBerthOut.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_AUTO_PARKING_OUT, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView lineBerthOut = createSignalView(SignalViewFactory.NORMAL, R.id.line_berth_out, false);
        lineBerthOut.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 2)
                .signalTriggeredSelected(true)
                .build());
        lineBerthOut.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());

        ISignalView ivSelectSpace = createSignalView(SignalViewFactory.IMAGE, R.id.iv_select_space, true);
        ivSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 3)
                .signalTriggeredSelected(true)
                .build());
        ivSelectSpace.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_CUSTOM_SPACE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 3)
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        ivSelectSpace.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_CUSTOM_SPACE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 3)
                .setSignal(VehiclePropertyIds.APA_ST_USER_SLOT, 0)
                .signalTriggeredViewEnable(false)
                .build());
        ivSelectSpace.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_CUSTOM_SPACE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_MODE_SELECTION, 3)
                .setSignal(VehiclePropertyIds.APA_ST_USER_SLOT, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView tvSelectSpace = createSignalView(SignalViewFactory.TEXT, R.id.tv_select_space, false);
        tvSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        tvSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 3)
                .signalTriggeredSelected(true)
                .build());
        tvSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_USER_SLOT, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_USER_SLOT, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView lineSelectSpace = createSignalView(SignalViewFactory.NORMAL, R.id.line_select_space, false);
        lineSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 1)
                .signalTriggeredSelected(false)
                .build());
        lineSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 0)
                .signalTriggeredSelected(false)
                .build());
        lineSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 2)
                .signalTriggeredSelected(false)
                .build());
        lineSelectSpace.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_TYPE, 3)
                .signalTriggeredSelected(true)
                .build());

        ISignalView ivSettingApa = createSignalView(SignalViewFactory.IMAGE, R.id.apa_iv_setting, true);
        ivSettingApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.APA_ST_SET, 0)
                .signalTriggeredViewEnable(false)
                .build());
        ivSettingApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_SETTING)
                .eventTriggeredSelectedReverse()
                .setSignal(VehiclePropertyIds.APA_ST_SET, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView tvSetting = createSignalView(SignalViewFactory.TEXT, R.id.tv_setting, false);
        tvSetting.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_SET, 0)
                .signalTriggeredViewEnable(false)
                .build());
        tvSetting.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_ST_SET, 1)
                .signalTriggeredViewEnable(true)
                .build());

        ISignalView dialogSetting = createSignalView(SignalViewFactory.NORMAL, R.id.dialog_setting, true);
        dialogSetting.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_SETTING)
                .eventTriggeredVisibility(SignalViewState.REVERSE)
                .build());

        ISignalView setCenter = new  ApaSettingButtonView();
        View setCenterView = findViewById(R.id.tv_dialog_setting_center);
        initCustomSignalView(setCenter,setCenterView, true);
        setCenter.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_SETTING_CENTER)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_PLACE_PREFERENCE, 1)
                .setSignal(VehiclePropertyIds.APA_PARKING_PREFERENCE, 1)
                .signalTriggeredSelected(true)
                .build());
        setCenter.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_SETTING_CENTER)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_PLACE_PREFERENCE, 1)
                .setSignal(VehiclePropertyIds.APA_PARKING_PREFERENCE, 2)
                .signalTriggeredSelected(false)
                .build());
        ISignalView setRight = new  ApaSettingButtonView();
        View setRightView = findViewById(R.id.tv_dialog_setting_right);
        initCustomSignalView(setRight,setRightView, true);
        setRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_SETTING_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_PLACE_PREFERENCE, 2)
                .setSignal(VehiclePropertyIds.APA_PARKING_PREFERENCE, 1)
                .signalTriggeredSelected(false)
                .build());
        setRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_SETTING_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_PLACE_PREFERENCE, 2)
                .setSignal(VehiclePropertyIds.APA_PARKING_PREFERENCE, 2)
                .signalTriggeredSelected(true)
                .build());


        ISignalView ivBerthHint = createSignalView(SignalViewFactory.IMAGE, R.id.iv_berth_hint, false);
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 0)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_0)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_search_for_parking_spaces)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_1)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_gear_d)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_2)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_find_parking_space)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_3)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_select_docking_mode)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_4)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 5)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_5)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 6)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_release_the_brake)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_6)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 7)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_7)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 8)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_no_space)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_8)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_9)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 10)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_10)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 11)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_too_fast)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_11)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 12)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_vehicle_speed_too_high)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_12)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 13)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_gear_p)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_13)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.drawable.frame_n)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_14)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 15)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_please_choose_parking)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_15)
                .build());
        ivBerthHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 16)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResource(R.mipmap.img_step_on_the_brake)
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_16)
                .build());


//        ISignalView ivBerthHint2 = createSignalView(SignalViewFactory.IMAGE, R.id.iv_berth_hint2, false);
//        ivBerthHint2.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 2)
//                .signalTriggeredVisibility(View.VISIBLE)
//                .signalTriggeredImageResource(R.mipmap.img_gear_p)
//                .signalTriggeredEvent(EventIds.APA_BERTHING_REMOTE)
//                .build());
//        ivBerthHint2.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 0)
//                .signalTriggeredVisibility(View.GONE)
//                .build());

        ISignalView tvBerthHint = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_hint, false);
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_0)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_1)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_search_space)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_2)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_search_space_with_D)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_3)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_find_space)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_4)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_5)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_6)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_berth_in_control_hint)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_7)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_8)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_berth_out_no_space)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_9)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_berth_out_orientation)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_10)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_11)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_seed_25)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_12)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_seed_30)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_13)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_remote_control_hint)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_BERTHING_REMOTE)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_remote_control_hint)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_14)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_15)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_smart_parking_control_hint)
                .build());
        tvBerthHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_16)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredText(R.string.apa_view_brake_tips)
                .build());

        ISignalView tts = new TtsSignalView(rootView.getContext().getApplicationContext());
        initCustomSignalView(tts, null, false);
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_1)
                .eventTriggeredText(R.string.apa_search_space)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_2)
                .eventTriggeredText(R.string.apa_search_space_with_D)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_3)
                .eventTriggeredText(R.string.apa_find_space)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .signalTriggeredEvent(EventIds.APA_ST_REQUEST_4)
                .eventTriggeredText(R.string.apa_select_berth_in_type)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_6)
                .eventTriggeredText(R.string.tts_berth_in_control_hint)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_7)
                .eventTriggeredText(R.string.apa_berth_in_success)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_8)
                .eventTriggeredText(R.string.tts_berth_out_no_space)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_9)
                .eventTriggeredText(R.string.tts_berth_out_orientation)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_10)
                .eventTriggeredText(R.string.apa_berth_out_success)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_11)
                .eventTriggeredText(R.string.tts_speed_high)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_12)
                .eventTriggeredText(R.string.tts_speed_too_high)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_BERTHING_REMOTE)
                .eventTriggeredText(R.string.apa_remote_control_hint)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 1)
                .signalTriggeredText(R.string.apa_dialog_content_pause_1)
                .build());
        tts.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 2)
                .signalTriggeredText(R.string.tts_dialog_content_pause_2)
                .build());

        ISignalView tvBerthInKey = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_in_key, true);
        tvBerthInKey.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_ON_KEY)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_IN_MODE, 1)
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthInKey.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInKey.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 2)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInKey.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 1)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());


        ISignalView tvBerthInRemote = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_in_remote, true);
        tvBerthInRemote.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_ON_REMOTE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_IN_MODE, 2)
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthInRemote.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInRemote.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 2)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInRemote.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 1)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        ISignalView ivBerthCar = createSignalView(SignalViewFactory.IMAGE, R.id.iv_berth_car, false);

        ivBerthCar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS,9)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        ivBerthCar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView containerBerthVerticalOutLeft = createSignalView(SignalViewFactory.NORMAL, R.id.container_button_out_vertical_left, false);
        containerBerthVerticalOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerBerthVerticalOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView containerBerthVerticalOutRight = createSignalView(SignalViewFactory.NORMAL, R.id.container_button_out_vertical_right, false);
        containerBerthVerticalOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerBerthVerticalOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView containerBerthOutLeft = createSignalView(SignalViewFactory.NORMAL, R.id.container_button_out_middle_left, false);
        containerBerthOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerBerthOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ISignalView containerBerthOutRight = createSignalView(SignalViewFactory.NORMAL, R.id.container_button_out_middle_right, false);
        containerBerthOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerBerthOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView containerBerthDownOutLeft = createSignalView(SignalViewFactory.NORMAL, R.id.container_button_out_down_left, false);
        containerBerthDownOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerBerthDownOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        ISignalView containerBerthDownOutRight = createSignalView(SignalViewFactory.NORMAL, R.id.container_button_out_down_right, false);
        containerBerthDownOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerBerthDownOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView ivBerthVerticalOutLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_title_out_vertical_left, true);
        ivBerthVerticalOutLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_VERTICAL_LEFT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,7)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,5)
                .signalTriggeredSelected(true)
                .build());
        ivBerthVerticalOutLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_VERTICAL_LEFT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,7)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,0)
                .signalTriggeredSelected(false)
                .build());
        ivBerthVerticalOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTHDLEAVL,0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());
        ivBerthVerticalOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTHDLEAVL,1)
                .signalTriggeredViewEnable(true)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());

        ISignalView ivBerthVerticalOutRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_title_out_vertical_right, true);
        ivBerthVerticalOutRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_VERTICAL_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,8)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,6)
                .signalTriggeredSelected(true)
                .build());
        ivBerthVerticalOutRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_VERTICAL_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,8)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,0)
                .signalTriggeredSelected(false)
                .build());
        ivBerthVerticalOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTHDRIAVL,0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());
        ivBerthVerticalOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTHDRIAVL,1)
                .signalTriggeredViewEnable(true)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());


        ISignalView ivBerthMiddleOutLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_title_out_middle_left, true);
        ivBerthMiddleOutLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_LEFT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,5)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,1)
                .signalTriggeredSelected(true)
                .build());
        ivBerthMiddleOutLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_LEFT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,5)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,0)
                .signalTriggeredSelected(false)
                .build());
        ivBerthMiddleOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRHALFLEAVL,0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());
        ivBerthMiddleOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRHALFLEAVL,1)
                .signalTriggeredViewEnable(true)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());


        ISignalView ivBerthMiddleOutRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_title_out_middle_right, true);
        ivBerthMiddleOutRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,6)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,2)
                .signalTriggeredSelected(true)
                .build());
        ivBerthMiddleOutRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,6)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,0)
                .signalTriggeredSelected(false)
                .build());
        ivBerthMiddleOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRHALFRIAVL,0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());
        ivBerthMiddleOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRHALFRIAVL,1)
                .signalTriggeredViewEnable(true)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());

        ISignalView ivBerthDownOutLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_title_out_down_left, true);
        ivBerthDownOutLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_DOWN_LEFT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,9)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,7)
                .signalTriggeredSelected(true)
                .build());
        ivBerthDownOutLeft.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_DOWN_LEFT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,9)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,0)
                .signalTriggeredSelected(false)
                .build());
        ivBerthDownOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTTRLEAVL,0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());
        ivBerthDownOutLeft.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTTRLEAVL,1)
                .signalTriggeredViewEnable(true)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());


        ISignalView ivBerthDownOutRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_title_out_down_right, true);
        ivBerthDownOutRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_DOWN_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,10)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,8)
                .signalTriggeredSelected(true)
                .build());
        ivBerthDownOutRight.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_OUT_DOWN_RIGHT)
                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT,10)
                .setSignal(VehiclePropertyIds.APA2_ST_PARKOUTDIRSET,0)
                .signalTriggeredSelected(false)
                .build());
        ivBerthDownOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTTRRIAVL,0)
                .signalTriggeredViewEnable(false)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());
        ivBerthDownOutRight.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA2_ST_DIRVERTTRRIAVL,1)
                .signalTriggeredViewEnable(true)
                .signalTriggeredSelected(false)
                .keepLastEvent()
                .build());

        ISignalView tvBerthTypeTitle = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_type_title, false);
        tvBerthTypeTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthTypeTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvBerthTypeVerticalTitle = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_type_vertical_title, false);
        tvBerthTypeVerticalTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthTypeVerticalTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvBerthTypeVerticalFrontParking = createSignalView(SignalViewFactory.IMAGE, R.id.tv_berth_type_vertical_front_parking, true);
        tvBerthTypeVerticalFrontParking.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_FRONT_PARKING)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_TYPE_SELECTION, 1)
                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 1)
                .setNoResponseEvent(EventIds.RES_TIMES,EventIds.RES_USE,EventIds.RES_REC_USE)
                .signalTriggeredSelected(true)
                .build());
        tvBerthTypeVerticalFrontParking.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_FRONT_PARKING)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_TYPE_SELECTION, 1)
                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        tvBerthTypeVerticalFrontParking.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .keepLastEvent()
                .build());
        tvBerthTypeVerticalFrontParking.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());


        ISignalView tvBerthTypeVerticalRearParking = createSignalView(SignalViewFactory.IMAGE, R.id.tv_berth_type_vertical_rear_parking, true);
        tvBerthTypeVerticalRearParking.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_REAR_PARKING)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_TYPE_SELECTION, 2)
                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 2)
                .setNoResponseEvent(EventIds.RES_TIMES,EventIds.RES_USE,EventIds.RES_REC_USE)
                .signalTriggeredSelected(true)
                .build());
        tvBerthTypeVerticalRearParking.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_REAR_PARKING)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_AUTOMATIC_PARKING_SYSTEM_PARKING_TYPE_SELECTION, 2)
                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
                .build());
        tvBerthTypeVerticalRearParking.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .keepLastEvent()
                .build());
        tvBerthTypeVerticalRearParking.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());

        ISignalView tvBerthTypeVerticalFrontParkingTitle = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_type_vertical_front_parking_tilte, false);
        tvBerthTypeVerticalFrontParkingTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthTypeVerticalFrontParkingTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvBerthTypeVerticalRearParkingTitle = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_type_vertical_rear_parking_tilte, false);
        tvBerthTypeVerticalRearParkingTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthTypeVerticalRearParkingTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvBerthinVerticalTitle = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_in_vertical_title, false);
        tvBerthinVerticalTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthinVerticalTitle.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvBerthInVerticalKey = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_in_vertical_key, true);
        tvBerthInVerticalKey.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_ON_KEY)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_IN_MODE, 1)
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthInVerticalKey.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInVerticalKey.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 2)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInVerticalKey.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 1)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());

        ISignalView tvBerthInVerticalRemote = createSignalView(SignalViewFactory.TEXT, R.id.tv_berth_in_vertical_remote, true);
        tvBerthInVerticalRemote.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_IN_ON_REMOTE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_PARKING_IN_MODE, 2)
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        tvBerthInVerticalRemote.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInVerticalRemote.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 2)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());
        tvBerthInVerticalRemote.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PARKING_METHOD, 1)
                .signalTriggeredVisibility(View.GONE)
                .keepLastEvent()
                .build());


//
//        ISignalView containerBerthOutLeft = createSignalView(SignalViewFactory.NORMAL, R.id.container_berth_out_left, false);
//        containerBerthOutLeft.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
//                .signalTriggeredVisibility(View.VISIBLE)
//                .build());
//        containerBerthOutLeft.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
//                .signalTriggeredVisibility(View.GONE)
//                .build());
//        ISignalView containerBerthOutRight = createSignalView(SignalViewFactory.NORMAL, R.id.container_berth_out_right, false);
//        containerBerthOutRight.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 9)
//                .signalTriggeredVisibility(View.VISIBLE)
//                .build());
//        containerBerthOutRight.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
//                .signalTriggeredVisibility(View.GONE)
//                .build());

//        ISignalView ivBerthOutLeft = createSignalView(SignalViewFactory.IMAGE, R.id.iv_berth_out_left, true);
//        ivBerthOutLeft.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_LEFT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 1)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 0)
//                .signalTriggeredVisibility(View.GONE)
//                .build());
//        ivBerthOutLeft.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_LEFT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 1)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 1)
//                .signalTriggeredVisibility(View.VISIBLE)
//                .build());
//        ivBerthOutLeft.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_LEFT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 1)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 2)
//                .signalTriggeredVisibility(View.GONE)
//                .build());
//        ivBerthOutLeft.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_LEFT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 1)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 3)
//                .signalTriggeredVisibility(View.VISIBLE)
//                .build());
//
//
//        ISignalView ivBerthOutRight = createSignalView(SignalViewFactory.IMAGE, R.id.iv_berth_out_right, true);
//        ivBerthOutRight.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_RIGHT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 2)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 0)
//                .signalTriggeredVisibility(View.GONE)
//                .build());
//        ivBerthOutRight.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_RIGHT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 2)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 1)
//                .signalTriggeredVisibility(View.GONE)
//                .build());
//        ivBerthOutRight.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_RIGHT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 2)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 2)
//                .signalTriggeredVisibility(View.VISIBLE)
//                .build());
//        ivBerthOutRight.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.APA_CLICK_BERTH_OUT_RIGHT)
//                .eventTriggeredSignal(VehiclePropertyIds.APA_DIRECTION_OF_PARKING_OUT, 2)
//                .setSignal(VehiclePropertyIds.APA_OUT_DIRECTION, 3)
//                .signalTriggeredVisibility(View.VISIBLE)
//                .build());


        ISignalView llBerthSuccess = createSignalView(SignalViewFactory.NORMAL, R.id.ll_berth_success, false);
        llBerthSuccess.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY)
                .eventTriggeredVisibility(View.GONE)
                .build());
        llBerthSuccess.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        llBerthSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 7)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        llBerthSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 10)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        llBerthSuccess.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvBerthSuccessApa = createSignalView(SignalViewFactory.TEXT, R.id.apa_tv_berth_success, false);
        tvBerthSuccessApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_7)
                .eventTriggeredText(R.string.apa_berth_in_success)
                .build());
        tvBerthSuccessApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_ST_REQUEST_10)
                .eventTriggeredText(R.string.apa_berth_out_success)
                .build());

        ISignalView containerCustomParking = createSignalView(SignalViewFactory.NORMAL, R.id.container_custom_hint, false);
        containerCustomParking.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerCustomParking.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());


        mParkingView = findViewById(R.id.m_parking_view);
        AAOP_HSkin
                .with(mParkingView)
                .addViewAttrs(SkinAttrs.IMAGE_N, R.mipmap.img_parking_space_default_s)
                .applySkin(false);
        ISignalView parkingSignalView = createSignalView(SignalViewFactory.NORMAL, mParkingView, false);
        parkingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredViewEnable(true)
                .build());
        parkingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 6)
                .signalTriggeredViewEnable(false)
                .build());
        parkingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 4)
                .signalTriggeredViewEnable(false)
                .build());
        parkingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, 14)
                .signalTriggeredViewEnable(false)
                .build());
        parkingSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_FIND_PARKING_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredViewEnable(false)
                .build());

        TextView textView = findViewById(R.id.tv_custom_btn);
        textView.setOnClickListener(this);

        View containerDialogHintApa = findViewById(R.id.apa_container_dialog_hint);
        View dialogHintApa = findViewById(R.id.apa_dialog_hint);
        ISignalView dialogHintContainerApa = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, R.id.apa_container_dialog_hint, false);
        Utils.bindDialogAnimatorSignalView(dialogHintContainerApa, containerDialogHintApa, dialogHintApa);
        dialogHintContainerApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        dialogHintContainerApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogHintContainerApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 2)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogHintContainerApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogHintContainerApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogHintContainerApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 5)
                .signalTriggeredVisibility(View.GONE)
                .build());
        dialogHintContainerApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 6)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView tvDialogHintContentApa = createSignalView(SignalViewFactory.TEXT, R.id.apa_tv_dialog_hint_content, false);
        tvDialogHintContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 1)
                .signalTriggeredText(R.string.apa_dialog_content_pause_1)
                .build());
        tvDialogHintContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 2)
                .signalTriggeredText(R.string.apa_dialog_content_pause_2)
                .build());
        tvDialogHintContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 3)
                .signalTriggeredText(R.string.apa_dialog_content_pause_3)
                .build());
        tvDialogHintContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 4)
                .signalTriggeredText(R.string.apa_dialog_content_pause_4)
                .build());


        View dialogHintDContainer = findViewById(R.id.container_dialog_hint_door);
        View dialogHintD = findViewById(R.id.dialog_hint_door);
        ISignalView dialogHintDoor = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogHintDContainer, false);
        Utils.bindDialogAnimatorSignalView(dialogHintDoor, dialogHintDContainer, dialogHintD);
        dialogHintDoor.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PRECONDITION_CONFIRMATION, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        dialogHintDoor.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PRECONDITION_CONFIRMATION, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());

        View dialogStopContainerApa = findViewById(R.id.apa_container_dialog_stop);
        View dialogStopApa = findViewById(R.id.apa_dialog_stop);
        ISignalView containerDialogStopApa = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogStopContainerApa, false);
        Utils.bindDialogAnimatorSignalView(containerDialogStopApa, dialogStopContainerApa, dialogStopApa);
        containerDialogStopApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerDialogStopApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());

        ISignalView tvDialogStopContentApa = createSignalView(SignalViewFactory.TEXT, R.id.apa_tv_dialog_stop_content, false);
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 1)
                .signalTriggeredText(R.string.apa_dialog_content_stop_1)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 2)
                .signalTriggeredText(R.string.apa_dialog_content_stop_2)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 3)
                .signalTriggeredText(R.string.apa_dialog_content_stop_3)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 4)
                .signalTriggeredText(R.string.apa_dialog_content_stop_4)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 5)
                .signalTriggeredText(R.string.apa_dialog_content_stop_5)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 6)
                .signalTriggeredText(R.string.apa_dialog_content_stop_6)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 7)
                .signalTriggeredText(R.string.apa_dialog_content_stop_7)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 8)
                .signalTriggeredText(R.string.apa_dialog_content_stop_8)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 9)
                .signalTriggeredText(R.string.apa_dialog_content_stop_9)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 10)
                .signalTriggeredText(R.string.apa_dialog_content_stop_10)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 11)
                .signalTriggeredText(R.string.apa_dialog_content_stop_11)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 12)
                .signalTriggeredText(R.string.apa_dialog_content_stop_12)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 13)
                .signalTriggeredText(R.string.apa_dialog_content_stop_13)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 14)
                .signalTriggeredText(R.string.apa_dialog_content_stop_14)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 15)
                .signalTriggeredText(R.string.apa_dialog_content_stop_15)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 16)
                .signalTriggeredText(R.string.apa_dialog_content_stop_16)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 17)
                .signalTriggeredText(R.string.apa_dialog_content_stop_17)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 18)
                .signalTriggeredText(R.string.apa_dialog_content_stop_18)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 19)
                .signalTriggeredText(R.string.apa_dialog_content_stop_19)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 20)
                .signalTriggeredText(R.string.apa_dialog_content_stop_20)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 21)
                .signalTriggeredText(R.string.apa_dialog_content_stop_21)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 22)
                .signalTriggeredText(R.string.apa_dialog_content_stop_22)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 23)
                .signalTriggeredText(R.string.apa_dialog_content_stop_23)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 24)
                .signalTriggeredText(R.string.apa_dialog_content_stop_24)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 25)
                .signalTriggeredText(R.string.apa_dialog_content_stop_25)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 26)
                .signalTriggeredText(R.string.apa_dialog_content_stop_26)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 27)
                .signalTriggeredText(R.string.apa_dialog_content_stop_27)
                .build());
        tvDialogStopContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_TERMINATION_EVENT, 28)
                .signalTriggeredText(R.string.apa_dialog_content_stop_28)
                .build());



        View dialogControlContainerApa = findViewById(R.id.apa_container_dialog_control);
        View dialogControlApa = findViewById(R.id.apa_dialog_control);
        ISignalView containerDialogControlApa = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogControlContainerApa, false);
        Utils.bindDialogAnimatorSignalView(containerDialogControlApa, dialogControlContainerApa, dialogControlApa);
        containerDialogControlApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerDialogControlApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 6)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        containerDialogControlApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView tvDialogControlTitleApa = createSignalView(SignalViewFactory.TEXT, R.id.apa_tv_dialog_control_title, false);
        tvDialogControlTitleApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 5)
                .signalTriggeredText(R.string.apa_dialog_title_recover)
                .build());
        tvDialogControlTitleApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 6)
                .signalTriggeredText(R.string.apa_dialog_exit_title)
                .build());
        ISignalView tvDialogControlImg = createSignalView(SignalViewFactory.IMAGE, R.id.tv_dialog_control_img, false);
        tvDialogControlImg.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 5)
                .signalTriggeredImageResource(R.mipmap.icon_singlerow_recovery)
                .build());
        tvDialogControlImg.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 6)
                .signalTriggeredText(R.string.apa_dialog_exit_title)
                .signalTriggeredImageResource(R.mipmap.icon_singlerow_exit)
                .build());


        ISignalView tvDialogControlContentApa = createSignalView(SignalViewFactory.TEXT, R.id.apa_tv_dialog_control_content, false);
        tvDialogControlContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 5)
                .signalTriggeredText(R.string.apa_dialog_content_recover)
                .build());
        tvDialogControlContentApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 6)
                .signalTriggeredText(R.string.apa_dialog_exit_content)
                .build());

        ISignalView tvDialogPositiveApa = createSignalView(SignalViewFactory.TEXT, R.id.apa_tv_dialog_positive, true);
        tvDialogPositiveApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DIALOG_CONTROL_POSITIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_QUIT_CONFIRM, 1)
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 6)
                .build());
        tvDialogPositiveApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_RECOVER_POSITIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_RESTORE_PARKING, 1)
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 5)
                .build());

        ISignalView tvDialogNegativeApa = new CancelTimerSignalView();
        initCustomSignalView(tvDialogNegativeApa, R.id.apa_tv_dialog_negative, true);
        tvDialogNegativeApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, SignalViewState.INVALID)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .build());
        tvDialogNegativeApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DIALOG_CONTROL_NEGATIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_QUIT_CONFIRM, 2)
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 6)
                .signalTriggeredTimerBehavior(true, 0, 1000)
                .build());
        tvDialogNegativeApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_CLICK_BERTH_RECOVER_NEGATIVE)
                .eventTriggeredSignal(VehiclePropertyIds.APA_RESTORE_PARKING, 2)
                .setSignal(VehiclePropertyIds.APA_PAUSE_EVENT, 5)
                .signalTriggeredTimerBehavior(true, 0, 1000)
                .build());


        View dialogControlExitContainerApa = findViewById(R.id.apa_container_dialog_control_exit);
        View dialogControlExitApa = findViewById(R.id.apa_dialog_control_exit);
        ISignalView containerDialogControlExitApa = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogControlExitContainerApa, false);
        Utils.bindDialogAnimatorSignalView(containerDialogControlExitApa, dialogControlExitContainerApa, dialogControlExitApa);
        containerDialogControlExitApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HIDE_DIALOG)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        containerDialogControlExitApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_EXIT_NEGATIVE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        containerDialogControlExitApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_EXIT_POSITIVE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        containerDialogControlExitApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerDialogControlExitApa.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 2)
                .signalTriggeredVisibility(View.GONE)
                .build());


        ISignalView tvDialogPositiveExitApa = createSignalView(SignalViewFactory.TEXT, R.id.apa_tv_dialog_positive_exit, true);
        tvDialogPositiveExitApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_EXIT_POSITIVE)
                .build());

        ISignalView tvDialogNegativeExitApa = new CancelTimerSignalView();
        initCustomSignalView(tvDialogNegativeExitApa, R.id.apa_tv_dialog_negative_exit, true);
        tvDialogNegativeExitApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_EXIT_NEGATIVE)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());
        tvDialogNegativeExitApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HIDE_DIALOG)
                .eventTriggeredTimerBehavior(true, 0, 1000)
                .build());


        View dialogSignalDiedContainerApa = findViewById(R.id.apa_container_dialog_signal_error);
        View dialogSignalDiedViewApa = findViewById(R.id.apa_dialog_signal_error);
        ISignalView dialogSignalDiedApa = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, dialogSignalDiedContainerApa, false);
        Utils.bindDialogAnimatorSignalView(dialogSignalDiedApa, dialogSignalDiedContainerApa, dialogSignalDiedViewApa);
        dialogSignalDiedApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        dialogSignalDiedApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        dialogSignalDiedApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY_WITH_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        dialogSignalDiedApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_RECOVER)
                .eventTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvDialogSignalDiedPositiveApa = createSignalView(SignalViewFactory.NORMAL, R.id.apa_tv_dialog_signal_error_positive, true);
        tvDialogSignalDiedPositiveApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_HIDE)
                .build());
        ISignalView tvDialogSignalDiedNegativeApa = createSignalView(SignalViewFactory.NORMAL, R.id.apa_tv_dialog_signal_error_negative, true);
        tvDialogSignalDiedNegativeApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_RECOVER)
                .build());
        tvDialogSignalDiedNegativeApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY_WITH_ERROR)
                .eventTriggeredVisibility(View.GONE)
                .build());
        tvDialogSignalDiedNegativeApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.SIGNAL_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());


        ISignalView evsErrorSignalViewApa = new ApaErrorTimerSignalView();
        initCustomSignalView(evsErrorSignalViewApa, R.id.apa_tv_hint_error_evs, false);
        evsErrorSignalViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_ERROR)
                .eventTriggeredVisibility(View.VISIBLE)
                .eventTriggeredTimerBehavior(true, 5000, 0)
                .build());
        evsErrorSignalViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CAMERA_NORMAL)
                .eventTriggeredVisibility(View.GONE)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());
        evsErrorSignalViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY)
                .build());
        evsErrorSignalViewApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY_FROM_SIGNAL)
                .build());

        ISignalView signalErrorSignalView = new ApaErrorTimerSignalView();
        initCustomSignalView(signalErrorSignalView, R.id.tv_hint_error_signal, false);
        signalErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 0xFF)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredTimerBehavior(true, 5000, 0)
                .build());
        signalErrorSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY_FROM_SIGNAL)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .signalTriggeredTimerBehavior(false, 0, 0)
                .build());

    }

    private volatile static int calibrationType = AvmApaControlBehavior.CALIBRATION_LINE;
    //    public static final int CALIBRATION_INVALID = 0;
    public static final int CALIBRATION_ROAD = 1;
    public static final int CALIBRATION_LINE = 2;

    public synchronized int getCalibrationType() {
        Log.i("AdayoCamera", TAG + " - getCalibrationType() returned: " + calibrationType);
        return calibrationType;
    }

    public synchronized void setCalibrationType(int type) {
        Log.i("AdayoCamera", TAG + " - " + "setCalibrationType() called with: type = [" + type + "]");
        calibrationType = type;
    }

    @Override
    public void notifyEventComing(int eventId) {
        if (EventIds.CAMERA_ERROR == eventId){
            if (!windowSignalView.isShowed()){
                return;
            }
        }
        if (EventIds.ROAD_CALIBRATION_DISPLAY == eventId) {
            setCalibrationType(CALIBRATION_ROAD);
        } else if (EventIds.AVM_HIDE == eventId || EventIds.CALIBRATION_SUCCESS == eventId) {
            setCalibrationType(CALIBRATION_LINE);
        }
        if (eventId == EventIds.APA_BTN_HIDE) {
            boolean isShowing = false;
            ISignalView windowSignalView = rootSignalView.getWrappedSignalView();
            if (windowSignalView instanceof WindowSignalView) {
                isShowing = ((WindowSignalView) windowSignalView).isShowed();
            }
            int sourceApa = AAOP_Camera.getSignalValue(VehiclePropertyIds.APA_CUT_SOURCE,0);
            if (isShowing && sourceApa == 1) {
                eventId = EventIds.APA_HIDE_DIALOG;
            }
        }
        if (eventId == EventIds.APA_DISPLAY) {
            int sourceValue = AAOP_Camera.getSignalValue(VehiclePropertyIds.APA_CUT_SOURCE,0);
            Log.i("AdayoCamera", TAG + " - notifyEventComing: with sourceValue : " + sourceValue);
            if(2 == sourceValue){
                int quitValue =  AAOP_Camera.getSignalValue(VehiclePropertyIds.AVM2_ST_QUITAVMENABLE,0);
                Log.i("AdayoCamera", TAG + " - notifyEventComing: with quitValue : " + quitValue);
                if (0 == quitValue){
                    Log.i("AdayoCamera", TAG + " - notifyEventComing: with return : " );
                    return;
                }
            }
        }

        Log.i("AdayoCamera", TAG + " - notifyEventComing: with eventId : " + eventId);
        super.notifyEventComing(eventId);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_custom_btn) {
            float degree = mParkingView.getDegree();
            int degreeData = (int) ((degree + 180) * 10);
            int x = (int) mParkingView.getLeftTopX();
            int y = (int) mParkingView.getLeftTopY();
            Log.i("AdayoCamera", TAG + " - send custom parking signal : degree = [" + degree + "] | degreeData = [" + degreeData + "] | x = [" + x + "] | y = [" + y + "]");
            AAOP_Camera.sendSignal(VehiclePropertyIds.APA_SLOT_ANGLE_AND_POINT, degreeData, x, y);
            AAOP_Camera.sendSignal(VehiclePropertyIds.APA_PARKINGPLACECONFIRM,1);
        }
    }
}
