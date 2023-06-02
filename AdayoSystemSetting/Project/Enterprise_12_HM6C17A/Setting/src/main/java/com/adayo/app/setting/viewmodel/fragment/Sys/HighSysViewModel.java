package com.adayo.app.setting.viewmodel.fragment.Sys;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.model.data.request.sub.Ver.HighVerRequest;
import com.adayo.app.setting.model.data.request.sub.Ver.HighVerRequest;


public class HighSysViewModel extends ViewModel {
    private final static String TAG = HighSysViewModel.class.getSimpleName();
    public final HighVerRequest mHighVerRequest = new HighVerRequest();

    {
        mHighVerRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mHighVerRequest.unInit();
    }
}
