package com.adayo.app.setting.highlight;

import android.arch.lifecycle.MutableLiveData;

public class VoiceHighlightRequest {
    private final MutableLiveData<Integer> mVoiceHighlight = new MutableLiveData<>();

    public void init() {
        mVoiceHighlight.setValue(0);
    }
    public void requestHighlightModule(Integer i) {
        mVoiceHighlight.setValue(i);
    }

    public void uninit() {
    }

    public MutableLiveData<Integer> getVoiceHighlight() {
        return mVoiceHighlight;
    }
}
