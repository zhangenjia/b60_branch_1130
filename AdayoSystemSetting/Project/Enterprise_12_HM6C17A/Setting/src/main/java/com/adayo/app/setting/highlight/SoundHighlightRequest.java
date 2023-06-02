package com.adayo.app.setting.highlight;

import android.arch.lifecycle.MutableLiveData;

public class SoundHighlightRequest {
    private final MutableLiveData<Integer> mSoundHighlight = new MutableLiveData<>();

    public void init() {
        mSoundHighlight.setValue(0);
    }
    public void requestHighlightModule(Integer i) {
        mSoundHighlight.setValue(i);
    }

    public void uninit() {
    }

    public MutableLiveData<Integer> getSoundHighlight() {
        return mSoundHighlight;
    }
}
