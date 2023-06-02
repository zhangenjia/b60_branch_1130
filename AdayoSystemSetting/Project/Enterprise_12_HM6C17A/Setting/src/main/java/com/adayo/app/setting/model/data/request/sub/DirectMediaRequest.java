package com.adayo.app.setting.model.data.request.sub;

import android.arch.lifecycle.MutableLiveData;
import android.provider.Settings;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.base.LogUtil;

public class DirectMediaRequest extends BaseRequest {
    private final static String TAG = DirectMediaRequest.class.getSimpleName();
    private final MutableLiveData<Integer> mDirectMediaLiveData = new MutableLiveData<>();
    public void init() {
        parseDirectMediaButton();
    }

    public void unInit() {

    }

    private void parseDirectMediaButton() {

        int b = Settings.Global.getInt(getAppContext().getContentResolver(),
                ParamConstant.STEERING_WHEEL_CUSTOM_MEDIA_BUTTONS, 0);
        LogUtil.i(TAG, "b =" + b);
        mDirectMediaLiveData.setValue(b);
    }

    public void requestDirectMediaButton(int x) {
        LogUtil.i(TAG, "x =" + x);
        Settings.Global.putInt(getAppContext().getContentResolver(),
                ParamConstant.STEERING_WHEEL_CUSTOM_MEDIA_BUTTONS,
                x);
        mDirectMediaLiveData.setValue(x);

    }

    public MutableLiveData<Integer> getDirectMediaLiveData() {
        if (mDirectMediaLiveData.getValue() == null) {
            mDirectMediaLiveData.setValue(0);
        }
        return mDirectMediaLiveData;
    }
}
