package com.adayo.app.setting.highlight;

import android.arch.lifecycle.ViewModel;

public class VoiceHighlight extends ViewModel {
    private final static String TAG = VoiceHighlight.class.getSimpleName();
    public final VoiceHighlightRequest mVoiceHighlightRequest=new VoiceHighlightRequest();

    {
        mVoiceHighlightRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mVoiceHighlightRequest.uninit();
    }
}
