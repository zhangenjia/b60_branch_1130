package com.adayo.app.setting.hotspot;

import android.arch.lifecycle.ViewModel;

public class HotspotViewModel extends ViewModel {
    public final HotspotRequest mHotspotRequest=new HotspotRequest();
    {
        mHotspotRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mHotspotRequest.unInit();
    }
}
