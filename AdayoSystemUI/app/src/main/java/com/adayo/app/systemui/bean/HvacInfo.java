package com.adayo.app.systemui.bean;

public class HvacInfo {
    private float mainTemp;
    private float passageTemp;
    private int windValue;
    private int windMode;
    private int powerStatus;
    private int acStatus;
    private int autoStatus;
    private int dualStatus;
    private int recircStatus;
    private int frontStatus;
    private int rearStatus;

    public float getMainTemp() {
        return mainTemp;
    }

    public void setMainTemp(float mainTemp) {
        this.mainTemp = mainTemp;
    }

    public float getPassageTemp() {
        return passageTemp;
    }

    public void setPassageTemp(float passageTemp) {
        this.passageTemp = passageTemp;
    }

    public int getWindValue() {
        return windValue;
    }

    public void setWindValue(int windValue) {
        this.windValue = windValue;
    }

    public int getWindMode() {
        return windMode;
    }

    public void setWindMode(int windMode) {
        this.windMode = windMode;
    }

    public int getPowerStatus() {
        return powerStatus;
    }

    public void setPowerStatus(int powerStatus) {
        this.powerStatus = powerStatus;
    }

    public int getAcStatus() {
        return acStatus;
    }

    public void setAcStatus(int acStatus) {
        this.acStatus = acStatus;
    }

    public int getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(int autoStatus) {
        this.autoStatus = autoStatus;
    }

    public int getDualStatus() {
        return dualStatus;
    }

    public void setDualStatus(int dualStatus) {
        this.dualStatus = dualStatus;
    }

    public int getRecircStatus() {
        return recircStatus;
    }

    public void setRecircStatus(int recircStatus) {
        this.recircStatus = recircStatus;
    }

    public int getFrontStatus() {
        return frontStatus;
    }

    public void setFrontStatus(int frontStatus) {
        this.frontStatus = frontStatus;
    }

    public int getRearStatus() {
        return rearStatus;
    }

    public void setRearStatus(int rearStatus) {
        this.rearStatus = rearStatus;
    }
}
