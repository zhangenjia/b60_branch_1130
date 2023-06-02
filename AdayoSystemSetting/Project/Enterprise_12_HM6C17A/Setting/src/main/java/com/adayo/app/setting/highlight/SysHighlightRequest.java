package com.adayo.app.setting.highlight;

import android.arch.lifecycle.MutableLiveData;

public class SysHighlightRequest {
    private final MutableLiveData<Integer> mSysHighlight = new MutableLiveData<>();

    public void init() {
        mSysHighlight.setValue(0);
    }
    public void requestHighlightModule(Integer i) {
        mSysHighlight.setValue(i);
    }

    public void uninit() {
    }

    public MutableLiveData<Integer> getSysHighlight() {
        return mSysHighlight;
    }
}
