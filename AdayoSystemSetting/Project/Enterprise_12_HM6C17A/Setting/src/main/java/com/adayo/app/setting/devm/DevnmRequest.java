package com.adayo.app.setting.devm;

import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothAdapter;
import android.provider.Settings;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.base.LogUtil;

public class DevnmRequest extends BaseRequest {
    private final static String TAG = DevnmRequest.class.getSimpleName();
    private final MutableLiveData<String> mDevnmName = new MutableLiveData<>();

    public void init() {
        parseName();
    }

    private void parseName() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String btName = bluetoothAdapter.getName();
        LogUtil.i(TAG, "btName =" + btName);
        mDevnmName.setValue(btName);
    }

    public void requestName(String btName) {
        LogUtil.i(TAG, "btName =" + btName);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean result = bluetoothAdapter.setName(btName);
        mDevnmName.setValue(btName);
        Settings.Global.putString(getAppContext().getContentResolver(),
                ParamConstant.BT_NAME, btName);}

    public void unInit() {
    }

    public MutableLiveData<String> getDevnmName() {
        if (mDevnmName.getValue() == null) {
            mDevnmName.setValue("0");
        }
        return mDevnmName;
    }
}
