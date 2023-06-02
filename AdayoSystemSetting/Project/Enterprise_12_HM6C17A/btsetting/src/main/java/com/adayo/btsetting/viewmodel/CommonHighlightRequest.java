package com.adayo.btsetting.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

/**
 * @author Y4134
 */
public class CommonHighlightRequest extends BaseRequest {

    private final MutableLiveData<Integer> mCommonHighlight = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mWelcome = new MutableLiveData<>();

    public void init() {
        mCommonHighlight.setValue(0);
        mWelcome.setValue(true);
    }

    public void requestHighlightModule(Integer i) {
        mCommonHighlight.setValue(i);
    }
    public void requestWelcome(boolean b) {
        mWelcome.setValue(b);
    }


    public void uninit() {
    }

    public MutableLiveData<Boolean> getWelcome() {
        if (mWelcome.getValue() == null) {
            mWelcome.setValue(true);
            Log.w("mWelcome", " mWelcome     is null");
        }
        return mWelcome;
    }

    public MutableLiveData<Integer> getCommonHighlight() {
        if (mCommonHighlight.getValue() == null) {
            mCommonHighlight.setValue(0);
            Log.w("CommonHighlightRequest", " mCommonHighlight     is null");
        }
        return mCommonHighlight;
    }

}
