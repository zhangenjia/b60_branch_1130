package com.adayo.app.systemui.managers.business;

public interface VolumeController {
    int getVolume(int volumeType);
    void setVolume(int volumeType, int volume);
    int getSysMute();
    void setSysMute(int mute);
}
