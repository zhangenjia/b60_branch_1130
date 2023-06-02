package com.adayo.app.setting.wifi;

import android.arch.lifecycle.ViewModel;

public class WifiViewModel extends ViewModel {
    public final WifiRequest mWifiRequest=new WifiRequest();
    {
        mWifiRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mWifiRequest.unInit();
    }
}
