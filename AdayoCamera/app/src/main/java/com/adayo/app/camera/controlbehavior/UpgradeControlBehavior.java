package com.adayo.app.camera.controlbehavior;

import android.car.VehiclePropertyIds;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.app.camera.signalview.UpgradeConfirmTimerSignalView;
import com.adayo.app.camera.signalview.UpgradeFailedTimerSignalView;
import com.adayo.app.camera.signalview.UpgradeStateSignalView;
import com.adayo.app.camera.signalview.VersionSignalView;
import com.adayo.app.camera.utils.Utils;
import com.adayo.proxy.aaop_camera.controlcenter.behavior.BaseControlBehavior;
import com.adayo.proxy.aaop_camera.signalview.base.ISignalView;
import com.adayo.proxy.aaop_camera.signalview.factory.SignalViewFactory;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/8/13 17:17
 */
public class UpgradeControlBehavior extends BaseControlBehavior {

    public UpgradeControlBehavior(Context cxt) {
        super(cxt);
        Log.i("AdayoCamera", TAG + " - " + "UpgradeControlBehavior() called with: cxt = [" + cxt + "]");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.avm_view_upgrade;
    }

    @Override
    protected ISignalView produceRootSignalView(View rootView) {
        ISignalView windowSignalView = createSignalView(SignalViewFactory.WINDOW, rootView, false);
        windowSignalView.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_HIDE)
                .eventTriggeredWindowBehavior(false)
                .setSignal(VehiclePropertyIds.APA_UPDATA_TYPE, 1)
                .signalTriggeredEvent(EventIds.UPGRADE_DISPLAY)
                .signalTriggeredWindowBehavior(true)
                .build());
//        windowSignalView.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.UPGRADE_HIDE)
//                .eventTriggeredWindowBehavior(false, 0)
//                .setSignal(VehiclePropertyIds.APA_UPDATA_TYPE, 0)
//                .signalTriggeredWindowBehavior(false, 5000)
//                .build());
//        windowSignalView.addState(createSignalViewStateBuilder()
//                .setEvent(EventIds.UPGRADE_HIDE)
//                .eventTriggeredWindowBehavior(false)
//                .setSignal(VehiclePropertyIds.APA_UPDATA_TYPE, 2)
//                .signalTriggeredWindowBehavior(true)
//                .build());
//        windowSignalView.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 25)
//                .signalTriggeredEvent(EventIds.UPGRADE_HIDE)
//                .build());
        return windowSignalView;
    }

    @Override
    public void init() {
        Context mContext = rootView.getContext().getApplicationContext();

        ISignalView failedTimerSignalView = new UpgradeFailedTimerSignalView();
        initCustomSignalView(failedTimerSignalView, null, false);
        failedTimerSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 24)
                .signalTriggeredTimerBehavior(true, 10000, 0)
                .build());
        failedTimerSignalView.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 25)
                .signalTriggeredTimerBehavior(true, 10000, 0)
                .build());


        ISignalView tv_progress = createSignalView(SignalViewFactory.TEXT, R.id.tv_progress, false);
        //mcu升级时复归会默认发0，所以不能对0进行处理
//        tv_progress.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 0)
//                .signalTriggeredText(R.string.str_0)
//                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 1)
                .signalTriggeredText(R.string.str_10)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 2)
                .signalTriggeredText(R.string.str_20)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 3)
                .signalTriggeredText(R.string.str_30)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 4)
                .signalTriggeredText(R.string.str_40)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 5)
                .signalTriggeredText(R.string.str_50)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 6)
                .signalTriggeredText(R.string.str_60)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 7)
                .signalTriggeredText(R.string.str_70)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 8)
                .signalTriggeredText(R.string.str_80)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 9)
                .signalTriggeredText(R.string.str_90)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 10)
                .signalTriggeredText(R.string.str_100)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 21)
                .signalTriggeredText(R.string.str_100)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 22)
                .signalTriggeredText(R.string.str_100)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 23)
                .signalTriggeredText(R.string.str_100)
                .build());
        tv_progress.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 25)
                .signalTriggeredText(R.string.str_100)
                .build());


        ISignalView mProgressBar = createSignalView(SignalViewFactory.PROGRESS, R.id.m_progress_bar, false);
//        mProgressBar.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 0)
//                .signalTriggeredProgress(0)
//                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 1)
                .signalTriggeredProgress(10)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 2)
                .signalTriggeredProgress(20)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 3)
                .signalTriggeredProgress(30)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 4)
                .signalTriggeredProgress(40)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 5)
                .signalTriggeredProgress(50)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 6)
                .signalTriggeredProgress(60)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 7)
                .signalTriggeredProgress(70)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 8)
                .signalTriggeredProgress(80)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 9)
                .signalTriggeredProgress(90)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 10)
                .signalTriggeredProgress(100)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 21)
                .signalTriggeredProgress(100)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 22)
                .signalTriggeredProgress(100)
                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 23)
                .signalTriggeredProgress(100)
                .build());

//        mProgressBar.addState(createSignalViewStateBuilder()
//                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 24)
//                .signalTriggeredProgress(0)
//                .build());
        mProgressBar.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 25)
                .signalTriggeredProgress(100)
                .build());
        ISignalView tvUpdateImage =createSignalView(SignalViewFactory.IMAGE,R.id.tv_update_image,false);
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 3)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 4)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 5)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 6)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 7)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 8)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 9)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 10)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_44_upgrading)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 24)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_abnormal_detection)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 25)
                .signalTriggeredVisibility(View.VISIBLE)
                .signalTriggeredImageResourceIf(R.mipmap.icon_test_passed)
                .build());
        tvUpdateImage.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());

        ISignalView tvUpdateHint = new UpgradeStateSignalView();
        initCustomSignalView(tvUpdateHint, R.id.tv_update_hint, false);
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 1)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_10)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 2)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_20)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 3)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_30)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 4)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_40)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 5)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_50)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 6)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_60)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 7)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_70)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 8)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_80)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 9)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_90)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 10)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_100)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 21)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_mpu_over)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 22)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_mcu_updating)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 23)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_progress_mcu_over)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 24)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.red))
                .signalTriggeredText(R.string.avm_view_update_title_failed)
                .build());
        tvUpdateHint.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, 25)
                .signalTriggeredTextColor(ContextCompat.getColor(mContext, R.color.white))
                .signalTriggeredText(R.string.avm_view_update_title_success)
                .build());


        View containerDialogUpdateConfirm = findViewById(R.id.container_dialog_update_confirm);
        View dialogUpdateConfirm = findViewById(R.id.dialog_update_confirm);
        ISignalView containerDialogUpdateConfirmSV = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogUpdateConfirm, true);
        Utils.bindUpgradeDialogAnimatorSignalView(containerDialogUpdateConfirmSV, containerDialogUpdateConfirm, dialogUpdateConfirm);
        containerDialogUpdateConfirmSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, SignalViewState.INVALID)
                .signalTriggeredVisibility(View.GONE)
                .build());
        containerDialogUpdateConfirmSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_CLICK_UPGRADE)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());

        ISignalView upgradeConfirmTimerSV = new UpgradeConfirmTimerSignalView();
        initCustomSignalView(upgradeConfirmTimerSV, R.id.tv_dialog_update_confirm_title, false);
        upgradeConfirmTimerSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_CLICK_UPGRADE)
                .eventTriggeredTimerBehavior(true, 0, 1000)
                .build());
        upgradeConfirmTimerSV.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPGRADE_STATUS, SignalViewState.INVALID)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());
        upgradeConfirmTimerSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_HIDE)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());
        upgradeConfirmTimerSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_CLICK_UPGRADE_CONFIRM)
                .eventTriggeredTimerBehavior(false, 0, 0)
                .build());


        ISignalView tvDialogConfirmButton = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_confirm_button, true);
        tvDialogConfirmButton.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_CLICK_UPGRADE_CONFIRM)
                .eventTriggeredSignal(VehiclePropertyIds.AVM_ST_SOFTWAREUPDATE, 1)
                .build());
        ISignalView tvDialogConfirmCancel = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_confirm_cancel, true);
        tvDialogConfirmCancel.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_HIDE)
                .build());


        View containerDialogUpgrade = findViewById(R.id.container_dialog_update);
        View dialogUpgrade = findViewById(R.id.dialog_update);
        ISignalView dialogUpdate = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, containerDialogUpgrade, false);
        Utils.bindUpgradeDialogAnimatorSignalView(dialogUpdate, containerDialogUpgrade, dialogUpgrade);
        dialogUpdate.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_CLICK_UPGRADE)
                .eventTriggeredVisibility(View.GONE)
                .setSignal(VehiclePropertyIds.APA_UPDATA_TYPE, 1)
                .signalTriggeredVisibility(View.VISIBLE)
                .build());
        dialogUpdate.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.APA_DISPLAY)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());


        ISignalView tvDialogUpdateVersion = new VersionSignalView();
        initCustomSignalView(tvDialogUpdateVersion, R.id.tv_dialog_update_version, false);
        tvDialogUpdateVersion.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPDATA_TOTAL_VERSION, -1)
                .build());

        ISignalView tvUpdateVersion = new VersionSignalView();
        initCustomSignalView(tvUpdateVersion, R.id.tv_update_version, false);
        tvUpdateVersion.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_UPDATA_TOTAL_VERSION, -1)
                .build());


        ISignalView tvDialogUpdateVersionCurr = new VersionSignalView();
        initCustomSignalView(tvDialogUpdateVersionCurr, R.id.tv_dialog_update_version_curr, false);
        tvDialogUpdateVersionCurr.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CURRENT_VERSION, -1)
                .build());

        ISignalView tvUpdateVersionCurr = new VersionSignalView();
        initCustomSignalView(tvUpdateVersionCurr, R.id.tv_update_version_curr, false);
        tvUpdateVersionCurr.addState(createSignalViewStateBuilder()
                .setSignal(VehiclePropertyIds.APA_CURRENT_VERSION, -1)
                .build());


        ISignalView tvDialogButton = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_button, true);
        tvDialogButton.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_CLICK_UPGRADE)
                .build());
        ISignalView tvDialogCancel = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_cancel, true);
        tvDialogCancel.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_HIDE)
                .build());


        View containerDialogUpdateFailed = findViewById(R.id.container_dialog_update_failed);
        View dialogUpdateFailed = findViewById(R.id.dialog_update_failed);
        ISignalView dialogUpdateFailedSV = createSignalView(SignalViewFactory.ANIMATOR_VISIBILITY, R.id.container_dialog_update_failed, false);
        Utils.bindDialogAnimatorSignalView(dialogUpdateFailedSV, containerDialogUpdateFailed, dialogUpdateFailed);
        dialogUpdateFailedSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_DISPLAY)
                .eventTriggeredVisibility(View.GONE)
                .build());
        dialogUpdateFailedSV.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_ERROR_SIGNAL_DIED)
                .eventTriggeredVisibility(View.VISIBLE)
                .build());

        ISignalView tvDialogFailedButton = createSignalView(SignalViewFactory.TEXT, R.id.tv_dialog_failed_button, true);
        tvDialogFailedButton.addState(createSignalViewStateBuilder()
                .setEvent(EventIds.UPGRADE_HIDE)
                .build());
    }
}
