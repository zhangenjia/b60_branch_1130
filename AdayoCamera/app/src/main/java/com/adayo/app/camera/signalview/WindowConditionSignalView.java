package com.adayo.app.camera.signalview;

import android.util.Log;

import com.adayo.app.camera.MainService;
import com.adayo.proxy.aaop_camera.signalview.base.BaseConditionSignalView;

/**
 * @author Yiwen.Huan
 * created at 2021/9/6 16:36
 */
public class WindowConditionSignalView extends BaseConditionSignalView {

    @Override
    protected boolean shouldOnEvent(int eventId) {
        return true;
    }

    @Override
    protected boolean shouldNotifySignalDataChanged(String key, int value) {
        boolean shouldNotify = MainService.isEnable();
        Log.d("AdayoCamera", TAG + " - shouldNotifySignalDataChanged() returned: " + shouldNotify);
        return shouldNotify;
    }

}
