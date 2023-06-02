package com.adayo.app.setting.viewmodel.fragment.Display;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.model.data.request.sub.DirectMediaRequest;
import com.adayo.app.setting.model.data.request.sub.DisplayRequest;
import com.adayo.app.setting.model.data.request.sub.time.HighTimeRequest;


public class HighDisplayViewModel extends ViewModel {
    private final static String TAG = HighDisplayViewModel.class.getSimpleName();
    public final DisplayRequest mDisplayRequest = new DisplayRequest();

    public final HighTimeRequest mHighTimeRequest = new HighTimeRequest();
    public final DirectMediaRequest mDirectMediaRequest = new DirectMediaRequest();

    {
        mDisplayRequest.init();

        mHighTimeRequest.init();
        mDirectMediaRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mHighTimeRequest.unInit();

        mDisplayRequest.unInit();
        mDirectMediaRequest.unInit();
    }
}
