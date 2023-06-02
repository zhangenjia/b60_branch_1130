package com.adayo.app.camera.signalview;

import android.view.View;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.signalview.base.BaseTimerSignalView;

/**
 * @author Yiwen.Huan
 * created at 2021/9/28 15:59
 */
public class UpgradeFailedTimerSignalView extends BaseTimerSignalView {

    @Override
    protected void onCustomerTaskRunInThread() {
        sendEvent(EventIds.UPGRADE_HIDE);
    }

    @Override
    protected void onCustomerTaskRunInMainThread(View view) {

    }
}
