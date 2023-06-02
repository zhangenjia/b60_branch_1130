package com.adayo.app.setting.unit;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.local.LocalRequest;

public class UnitViewModel extends ViewModel {
    public final UnitRequest mUnitRequest=new UnitRequest();
    {
        mUnitRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mUnitRequest.unInit();
    }
}
