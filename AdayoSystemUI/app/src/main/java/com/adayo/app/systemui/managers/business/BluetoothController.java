package com.adayo.app.systemui.managers.business;

import com.adayo.app.systemui.bean.BluetoothInfo;

public interface BluetoothController {
    boolean isBTEnabled();
    void setBTEnabled(boolean enable);
    boolean isBTConnected();
    String getBTDevicesName();
    BluetoothInfo getBluetoothInfo();
}
