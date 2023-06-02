package com.adayo.app.camera.controlbehavior;

import android.car.VehiclePropertyIds;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.app.camera.signalview.AvmApaWindowSignalView;
import com.adayo.app.camera.signalview.AvmTipsTimerSignalView;
import com.adayo.app.camera.signalview.WindowConditionSignalView;
import com.adayo.app.camera.utils.Utils;
import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.controlcenter.behavior.BaseControlBehavior;
import com.adayo.proxy.aaop_camera.signalview.base.ISignalView;
import com.adayo.proxy.aaop_camera.signalview.factory.SignalViewFactory;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

public class AvmRoadCalibrationBehavior extends BaseControlBehavior{

    public AvmRoadCalibrationBehavior(Context mContext) {
        super(mContext);
        Log.i("AdayoCamera", TAG + " - " + "AvmRoadCalibrationBehavior() called with: mContext = [" + mContext + "]");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.avm_view_road_calibration;
    }

    @Override
    protected ISignalView produceRootSignalView(View rootView) {
        ISignalView root = new WindowConditionSignalView();
        return root;
    }

    @Override
    public void init() {
        ISignalView windowSignalView = new AvmApaWindowSignalView();
        initCustomSignalView(windowSignalView,rootView,false);
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_ENTER_HINT)
                .eventTriggeredWindowBehavior(true)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_ROAD_CALIBRATION_HIDE)
                .eventTriggeredWindowBehavior(false)
                .build());
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_FOR_ROAD_DISPLAY)
                .eventTriggeredWindowBehavior(false)
                .build());


        ISignalView btnApa = createSignalView(SignalViewFactory.NORMAL, R.id.btn_apa, true);
        btnApa.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY)
                .build());
        ISignalView btnHome = createSignalView(SignalViewFactory.NORMAL, R.id.btn_home, true);
        btnHome.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_HOME_TIPS_DISPLAY)
                .build());

        ISignalView btnLowerBr = createSignalView(SignalViewFactory.NORMAL,R.id.btn_lower_br,false);
        btnLowerBr.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE,2)
                .signalTriggeredSelected(true)
                .build());
        btnLowerBr.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CURRVIEWTYPE, SignalViewState.INVALID)
                .signalTriggeredSelected(false)
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

        ISignalView containerEnterRoadHint = createSignalView(SignalViewFactory.NORMAL, R.id.container_road_calibration, false);
        containerEnterRoadHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_ENTER_HINT)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        containerEnterRoadHint.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_ROAD_CALIBRATION_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.APA_CUT_SOURCE, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerEnterRoadHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, 1)
                .signalTriggeredVisibility(View.GONE)
                .build());

        View containerDialogRoadEnterConfirm = findViewById(R.id.container_dialog_road_enter_confirm);
        View dialogRoadEnterConfirm = findViewById(R.id.dialog_road_enter_confirm);
        ISignalView containerRoadConfirm = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogRoadEnterConfirm, false);
        Utils.bindDialogAnimatorSignalView(containerRoadConfirm, containerDialogRoadEnterConfirm, dialogRoadEnterConfirm);
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CLICK_AVM_ROAD_CALIBRATION_ENTER_HINT)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROAD_CALIBRATION_HIDE)
                .eventTriggeredVisibility(View.GONE)
                .build());
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.AVM2_ST_CALIBRATION, AAOP_Camera.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerRoadConfirm.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.ROAD_CALIBRATION_ENTER_HINT)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView dialogRoadConfirmPositive = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_road_enter_confirm_positive, true);
        dialogRoadConfirmPositive.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_CLICK_ROAD_CALIBRATION_HIDE)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_ST_AVMROADCALIBRATION, 1)
                .build());
        ISignalView dialogRoadConfirmExit = createSignalView(SignalViewFactory.NORMAL, R.id.tv_dialog_road_enter_confirm_negative, true);
        dialogRoadConfirmExit.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_ROAD_CALIBRATION_HIDE)
                .build());

        ISignalView enterRoadPositive = createSignalView(SignalViewFactory.NORMAL, R.id.tv_road_calibration_hint_positive, true);
        enterRoadPositive.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.CLICK_AVM_ROAD_CALIBRATION_ENTER_HINT)
                .build());
        ISignalView enterRoadNegative = createSignalView(SignalViewFactory.NORMAL, R.id.tv_road_calibration_hint_negative, true);
        enterRoadNegative.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.AVM_ROAD_CALIBRATION_HIDE)
                .build());
    }

}
