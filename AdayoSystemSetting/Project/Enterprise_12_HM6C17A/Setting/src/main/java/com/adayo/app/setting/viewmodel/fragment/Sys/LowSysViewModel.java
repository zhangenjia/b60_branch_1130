package com.adayo.app.setting.viewmodel.fragment.Sys;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.model.data.request.sub.Ver.LowVerRequest;
import com.adayo.app.base.LogUtil;

public class LowSysViewModel extends ViewModel {
    private final static String TAG = LowSysViewModel.class.getSimpleName();
    public final LowVerRequest mLowVerRequest = new LowVerRequest();

    {
       LogUtil.debugD(TAG,"");
        mLowVerRequest.init();
    }

    @Override
    protected void onCleared() {
       LogUtil.debugD(TAG,"");
        super.onCleared();
        mLowVerRequest.unInit();
    }
}
