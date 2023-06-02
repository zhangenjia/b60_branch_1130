package com.adayo.app.setting.highlight;

import android.arch.lifecycle.ViewModel;

public class SysHighlight extends ViewModel {
    private final static String TAG = SysHighlight.class.getSimpleName();
    public final SysHighlightRequest mSysHighlightRequest=new SysHighlightRequest();

    {
        mSysHighlightRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mSysHighlightRequest.uninit();
    }
}
