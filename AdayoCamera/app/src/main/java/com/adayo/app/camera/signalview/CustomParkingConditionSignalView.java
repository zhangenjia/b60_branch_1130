package com.adayo.app.camera.signalview;

import android.view.View;

import com.adayo.proxy.aaop_camera.signalview.base.BaseConditionSignalView;

/**
 * @author Yiwen.Huan
 * created at 2021/9/13 11:57
 */
public class CustomParkingConditionSignalView extends BaseConditionSignalView {

    @Override
    protected boolean shouldOnEvent(int eventId) {
        return true;
    }

    @Override
    protected boolean shouldNotifySignalDataChanged(String key, int value) {
        if (6 == value) {
            View view = getView();
            return (null != view && View.VISIBLE == view.getVisibility());
        }
        return true;
    }
}
