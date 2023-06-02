package com.adayo.app.setting.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.SystemProperties;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.base.LogUtil;

public class LocalRequest extends BaseRequest {
    private final static String TAG = LocalRequest.class.getSimpleName();
    private final MutableLiveData<Integer> mLocateModeLiveData = new MutableLiveData<>();public void init() {
        parseLocateMode();
    }

    public void unInit() {

    }

    private void parseLocateMode() {
        LogUtil.d(TAG, "");
        int anInt = SystemProperties.getInt(ParamConstant.SYS_PROP_KEY_LOCATE_MODE, ParamConstant.SYS_PROP_VALUE_LOCATE_MODE_DUAL);mLocateModeLiveData.setValue(anInt);
        LogUtil.i(TAG, "mLocateModeLiveData = " + anInt);
    }

    public void requestLocateMode(int value) {
        LogUtil.i(TAG, "value = " + value);
        SystemProperties.set(ParamConstant.SYS_PROP_KEY_LOCATE_MODE, String.valueOf(value));mLocateModeLiveData.setValue(value);
    }
    public LiveData<Integer> getLocateModeLiveData() {
        if (mLocateModeLiveData.getValue() == null) {
            mLocateModeLiveData.setValue(0);
        }
        return mLocateModeLiveData;
    }
}
