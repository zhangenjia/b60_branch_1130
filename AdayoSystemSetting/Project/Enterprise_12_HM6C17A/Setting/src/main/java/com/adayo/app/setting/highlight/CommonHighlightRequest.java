package com.adayo.app.setting.highlight;

import android.arch.lifecycle.MutableLiveData;

import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.base.LogUtil;

public class CommonHighlightRequest extends BaseRequest {

    private final MutableLiveData<Integer> mCommonHighlight = new MutableLiveData<>();

    public void init() {
        mCommonHighlight.setValue(0);
    }

    public void requestHighlightModule(Integer i) {
        mCommonHighlight.setValue(i);
    }

    public void uninit() {
    }

    public MutableLiveData<Integer> getCommonHighlight() {
        if (mCommonHighlight.getValue() == null) {
            mCommonHighlight.setValue(0);
            LogUtil.w("CommonHighlightRequest", " mCommonHighlight     is null");
        }
        return mCommonHighlight;
    }
}
