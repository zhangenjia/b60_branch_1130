package com.adayo.app.systemui.managers.business;

public interface HotspotController {
    boolean isHotspotEnabled();
    boolean isHotspotTransient();
    void setHotspotEnabled(boolean enabled);
    boolean isHotspotSupported();
    int getNumConnectedDevices();
}
