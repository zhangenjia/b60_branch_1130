package com.adayo.app.setting.highlight;

import android.arch.lifecycle.ViewModel;

public class SoundHighlight extends ViewModel {
    private final static String TAG = SoundHighlight.class.getSimpleName();
    public final SoundHighlightRequest mSoundHighlightRequest=new SoundHighlightRequest();

    {
        mSoundHighlightRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mSoundHighlightRequest.uninit();
    }
}
