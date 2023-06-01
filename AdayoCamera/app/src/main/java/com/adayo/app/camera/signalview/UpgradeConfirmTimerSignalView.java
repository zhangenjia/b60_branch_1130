package com.adayo.app.camera.signalview;

import android.view.View;
import android.widget.TextView;

import com.adayo.app.camera.R;
import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.signalview.base.BaseTimerSignalView;

/**
 * @author Yiwen.Huan
 * created at 2021/9/26 17:01
 */
public class UpgradeConfirmTimerSignalView extends BaseTimerSignalView {

    int time = 60;


    @Override
    protected void runCustomTask(long delay, long Period) {
        time = 60;
        super.runCustomTask(delay, Period);
    }


    @Override
    protected void onCustomerTaskRunInMainThread(View view) {
        if (view instanceof TextView) {
            ((TextView) view).setText(view.getContext().getString(R.string.avm_dialog_update_confirm_title) + " ( " + time + "s )");
        }
        time--;
    }

    @Override
    protected void onCustomerTaskRunInThread() {
        if (time <= 0) {
            cancelCustomTask();
            sendEvent(EventIds.UPGRADE_CLICK_UPGRADE_CONFIRM);
        }
    }

}
