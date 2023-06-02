package com.adayo.app.systemui.bean;

public class BluetoothInfo {
    private boolean enable;
    private boolean mount;
    private String deviceName;
    private int electricQuantity;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isMount() {
        return mount;
    }

    public void setMount(boolean mount) {
        this.mount = mount;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getElectricQuantity() {
        return electricQuantity;
    }

    public void setElectricQuantity(int electricQuantity) {
        this.electricQuantity = electricQuantity;
    }
}
