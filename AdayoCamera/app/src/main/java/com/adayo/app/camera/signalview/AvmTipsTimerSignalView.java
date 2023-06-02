package com.adayo.app.camera.signalview;

import android.view.View;
import android.widget.TextView;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.signalview.base.BaseTimerSignalView;

public class AvmTipsTimerSignalView extends BaseTimerSignalView {
    @Override
    protected void onCustomerTaskRunInThread() {

    }

    @Override
    protected void onCustomerTaskRunInMainThread(View view) {
               sendEvent(EventIds.AVM_HOME_TIPS_HIDE);
    }
}
