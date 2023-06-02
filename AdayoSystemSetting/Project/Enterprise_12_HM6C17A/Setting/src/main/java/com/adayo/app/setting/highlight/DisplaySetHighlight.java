package com.adayo.app.setting.highlight;

import android.arch.lifecycle.ViewModel;

public class DisplaySetHighlight extends ViewModel {
    private final static String TAG = DisplaySetHighlight.class.getSimpleName();
    public final DisplaySetHighlightRequest mDisplaySetHighlightRequest=new DisplaySetHighlightRequest();

    {
        mDisplaySetHighlightRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisplaySetHighlightRequest.uninit();
    }
}
