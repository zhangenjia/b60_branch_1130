package com.adayo.app.setting.viewmodel.fragment;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.model.data.request.sub.VoiceRequest;


public class VoiceViewModel extends ViewModel {
    private final static String TAG = VoiceViewModel.class.getSimpleName();
    public final VoiceRequest mVoiceRequest = new VoiceRequest();

    {
        mVoiceRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mVoiceRequest.unInit();
    }
}
