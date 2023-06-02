package com.adayo.app.systemui.managers.business;

public interface BrightnessController {
    int getBrightness();
    void setBrightness(int brightness);
    void setBrightnessSwitch(boolean open);
}
