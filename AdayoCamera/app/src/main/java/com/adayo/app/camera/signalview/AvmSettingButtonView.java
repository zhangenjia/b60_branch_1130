package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;

import com.adayo.proxy.aaop_camera.config.Config;
import com.adayo.proxy.aaop_camera.signalview.TextSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

public class AvmSettingButtonView extends TextSignalView {

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: isSelected" +v.isSelected());
        if(!v.isSelected()){
            if (SignalViewState.INVALID != clickEventId) {
                long now = System.currentTimeMillis();
                long clickTimeDifference = now - lastClickTime;
                Log.i("AdayoCamera", TAG + " - onClick:  clickTimeDifference = " + clickTimeDifference + " | now  = " + now + " | lastClickTIme = " + lastClickTime);
                if (clickTimeDifference > 0 && clickTimeDifference < Config.getInstance().debounceTime) {
                    Log.w("AdayoCamera", TAG + " - onClick: failed because (now - lastClickTime < Config.getInstance().debounceTime) , do nothing ,clickTimeDifference = " + clickTimeDifference + " | now  = " + now + " | lastClickTIme = " + lastClickTime);
                    return;
                }
                lastClickTime = now;
                sendEvent(clickEventId);
            }
        }
    }
}
