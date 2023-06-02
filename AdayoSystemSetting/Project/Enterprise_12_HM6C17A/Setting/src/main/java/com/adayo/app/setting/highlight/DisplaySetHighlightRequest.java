package com.adayo.app.setting.highlight;

import android.arch.lifecycle.MutableLiveData;

public class DisplaySetHighlightRequest {
    private final MutableLiveData<Integer> mDisplaySetHighlight = new MutableLiveData<>();

    public void init() {
        mDisplaySetHighlight.setValue(0);
    }
    public void requestHighlightModule(Integer i) {
        mDisplaySetHighlight.setValue(i);
    }

    public void uninit() {
    }

    public MutableLiveData<Integer> getDisplaySetHighlight() {
        return mDisplaySetHighlight;
    }
}
