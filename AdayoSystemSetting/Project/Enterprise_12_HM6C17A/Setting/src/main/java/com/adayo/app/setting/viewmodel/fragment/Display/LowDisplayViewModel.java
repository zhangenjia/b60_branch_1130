package com.adayo.app.setting.viewmodel.fragment.Display;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.model.data.request.sub.DirectMediaRequest;
import com.adayo.app.setting.model.data.request.sub.DisplayRequest;
import com.adayo.app.setting.language.LanguageRequest;
import com.adayo.app.setting.model.data.request.sub.time.LowTimeRequest;

public class LowDisplayViewModel extends ViewModel {
   private final static String TAG = LowDisplayViewModel.class.getSimpleName();
    public final DisplayRequest mDisplayRequest = new DisplayRequest();
    public final LanguageRequest mLanguageRequest = new LanguageRequest();
    public final LowTimeRequest mLowTimeRequest = new LowTimeRequest();
    public final DirectMediaRequest mDirectMediaRequest = new DirectMediaRequest();

    {
        mDisplayRequest.init();
        mLanguageRequest.init();
        mLowTimeRequest.init();
        mDirectMediaRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLowTimeRequest.unInit();
        mLanguageRequest.unInit();
        mDisplayRequest.unInit();
        mDirectMediaRequest.unInit();
    }
}