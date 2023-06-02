package com.adayo.app.setting.highlight;

import android.arch.lifecycle.ViewModel;

public class CommonHighlight extends ViewModel {
    private final static String TAG = CommonHighlight.class.getSimpleName();
    public final CommonHighlightRequest mCommonHighlightRequest=new CommonHighlightRequest();

    {
        mCommonHighlightRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCommonHighlightRequest.uninit();
    }


}
