package com.adayo.app.systemui.bean;

public class BCMInfo {
    private int carPowerStatus;
    private float batteryVoltage;
    private int newEngineStatus;

    public int getCarPowerStatus() {
        return carPowerStatus;
    }

    public void setCarPowerStatus(int carPowerStatus) {
        this.carPowerStatus = carPowerStatus;
    }

    public float getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(float batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public int getNewEngineStatus() {
        return newEngineStatus;
    }

    public void setNewEngineStatus(int newEngineStatus) {
        this.newEngineStatus = newEngineStatus;
    }
}
