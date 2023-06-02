package com.adayo.app.camera.signalview;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.MainService;
import com.adayo.proxy.aaop_camera.config.Config;
import com.adayo.proxy.aaop_camera.signalview.WindowSignalView;
import com.adayo.proxy.adas.evs.EvsCameraMng;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

public class AvmRvcWindowSignalView extends WindowSignalView {


    @Override
    public void displayView(View view) {
        super.displayView(view);
        if(MainService.isPullup()){
            Log.d("AdayoCamera", TAG + " - displayView: MainService.isPullup() true" );
            try {
                AAOP_SystemServiceManager.getInstance().setScreenState(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
            } catch (RemoteException e) {
                Log.e("AdayoCamera", TAG + " - displayView: error", e);
            }
        }
    }
}
