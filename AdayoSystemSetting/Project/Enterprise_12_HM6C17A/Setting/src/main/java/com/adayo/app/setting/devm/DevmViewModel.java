package com.adayo.app.setting.devm;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.hotspot.HotspotRequest;

public class DevmViewModel  extends ViewModel {
    public final DevnmRequest mDevnmRequest=new DevnmRequest();
    {
        mDevnmRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDevnmRequest.unInit();
    }
}
