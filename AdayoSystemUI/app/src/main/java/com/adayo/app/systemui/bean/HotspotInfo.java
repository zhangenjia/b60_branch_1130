package com.adayo.app.systemui.bean;

public class HotspotInfo {
    private int mHotspotState;
    private int mNumConnectedDevices;
    private boolean mWaitingForCallback;

    public int getmHotspotState() {
        return mHotspotState;
    }

    public void setmHotspotState(int mHotspotState) {
        this.mHotspotState = mHotspotState;
    }

    public int getmNumConnectedDevices() {
        return mNumConnectedDevices;
    }

    public void setmNumConnectedDevices(int mNumConnectedDevices) {
        this.mNumConnectedDevices = mNumConnectedDevices;
    }

    public boolean ismWaitingForCallback() {
        return mWaitingForCallback;
    }

    public void setmWaitingForCallback(boolean mWaitingForCallback) {
        this.mWaitingForCallback = mWaitingForCallback;
    }
}
