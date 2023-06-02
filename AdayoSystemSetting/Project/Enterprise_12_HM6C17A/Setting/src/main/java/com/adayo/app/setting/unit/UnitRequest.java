package com.adayo.app.setting.unit;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.provider.Settings;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.base.LogUtil;

public class UnitRequest extends BaseRequest {
    private final static String TAG = UnitRequest.class.getSimpleName();
    private final MutableLiveData<Integer> mTemperatureUnitLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mKmAndOilWearUnitLiveData = new MutableLiveData<>();public void init() {
        parseTemperatureUnit();
        parseKmAndOilWearUnit();
    }

    public void unInit() {

    }


    private void parseTemperatureUnit() {
        int anInt = Settings.Global.getInt(getAppContext().getContentResolver(),ParamConstant.SETTING_GLOBAL_KEY_TEMPERATURE_UNIT,ParamConstant.SETTING_GLOBAL_VALUE_TEMPERATURE_UNIT_CELSIUS);mTemperatureUnitLiveData.setValue(anInt);
        LogUtil.i(TAG, "mTemperatureUnitLiveData = " + anInt);
    }


    private void parseKmAndOilWearUnit() {
        LogUtil.d(TAG, "");
        int anInt = Settings.Global.getInt(getAppContext().getContentResolver(),
                ParamConstant.SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT,ParamConstant.SETTING_GLOBAL_VALUE_KM_AND_OIL_WEAR_UNIT_METRIC);mKmAndOilWearUnitLiveData.setValue(anInt);
        LogUtil.i(TAG, "mKmAndOilWearUnitLiveData = " + anInt);
    }


    public void requestTemperatureUnit(int value) {
        LogUtil.i(TAG, "value = " + value);
        boolean b = Settings.Global.putInt(getAppContext().getContentResolver(),ParamConstant.SETTING_GLOBAL_KEY_TEMPERATURE_UNIT,value);
        mTemperatureUnitLiveData.setValue(b ? value : getTemperatureUnitLiveData().getValue());}


    public void requestKmAndOilWearUnit(int value) {
        LogUtil.i(TAG, "value = " + value);
        boolean b = Settings.Global.putInt(getAppContext().getContentResolver(),
                ParamConstant.SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT,
                value);
        mKmAndOilWearUnitLiveData.setValue(b ? value : getKmAndOilWearUnitLiveData().getValue());
    }

    public LiveData<Integer> getTemperatureUnitLiveData() {
        if (mTemperatureUnitLiveData.getValue() == null) {
            mTemperatureUnitLiveData.setValue(0);
        }
        return mTemperatureUnitLiveData;
    }

    public LiveData<Integer> getKmAndOilWearUnitLiveData() {
        if (mKmAndOilWearUnitLiveData.getValue() == null) {
            mKmAndOilWearUnitLiveData.setValue(0);
        }
        return mKmAndOilWearUnitLiveData;
    }

}
