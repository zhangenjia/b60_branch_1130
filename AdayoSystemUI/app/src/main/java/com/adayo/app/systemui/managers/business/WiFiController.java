package com.adayo.app.systemui.managers.business;

public interface WiFiController {
    boolean isWiFiEnable();
    void setWiFiEnable(boolean enable);
    boolean isWiFiConnected();
    int getRssi();
    String getSsid();
}
