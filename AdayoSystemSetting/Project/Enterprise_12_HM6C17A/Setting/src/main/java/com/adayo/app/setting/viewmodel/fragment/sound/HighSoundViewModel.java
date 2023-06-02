package com.adayo.app.setting.viewmodel.fragment.sound;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.model.data.request.sub.sound.HighSoundRequest;

public class HighSoundViewModel extends ViewModel {
    private final static String TAG = HighSoundViewModel.class.getSimpleName();
    public final HighSoundRequest mHighSoundRequest = new HighSoundRequest();

    {
        mHighSoundRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mHighSoundRequest.unInit();
    }
}
