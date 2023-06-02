package com.adayo.app.setting.theme;

import android.arch.lifecycle.ViewModel;

public class ThemeModeViewModel extends ViewModel {
    public final ThemeModelRequest mThemeModeRequest=new ThemeModelRequest();
    {
        mThemeModeRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mThemeModeRequest.unInit();
    }
}
