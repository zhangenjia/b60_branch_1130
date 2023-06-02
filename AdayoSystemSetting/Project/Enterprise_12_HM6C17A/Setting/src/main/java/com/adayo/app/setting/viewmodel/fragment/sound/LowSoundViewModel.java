package com.adayo.app.setting.viewmodel.fragment.sound;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.model.data.request.sub.sound.HighSoundRequest;
import com.adayo.app.setting.model.data.request.sub.sound.LowSoundRequest;


public class LowSoundViewModel extends ViewModel {
    private final static String TAG = LowSoundViewModel.class.getSimpleName();
    public final LowSoundRequest mLowSoundRequest = new LowSoundRequest();

    {
        mLowSoundRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLowSoundRequest.unInit();
    }
}
