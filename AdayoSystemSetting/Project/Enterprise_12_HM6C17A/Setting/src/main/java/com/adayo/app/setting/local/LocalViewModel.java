package com.adayo.app.setting.local;

import android.arch.lifecycle.ViewModel;

public class LocalViewModel extends ViewModel {
    public final LocalRequest mLocalRequest=new LocalRequest();
    {
        mLocalRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLocalRequest.unInit();
    }
}
