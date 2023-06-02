package com.adayo.app.setting.language;

import android.arch.lifecycle.ViewModel;

public class LanguageViewModel extends ViewModel {
    public final LanguageRequest mLanguageRequest = new LanguageRequest();

    {
        mLanguageRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLanguageRequest.unInit();
    }
}
