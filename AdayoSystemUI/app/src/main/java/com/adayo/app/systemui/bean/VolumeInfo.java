package com.adayo.app.systemui.bean;

import java.io.Serializable;

public class VolumeInfo implements Serializable {
    private boolean sys_on_volume_switch;//开机音量开关
    private int sys_on_volume;//开机音量
    private transient int sys_on_temp_volume;//":16,
    private int main_volume;//":16,
    private int navi_volume;//":16,
    private int media_volume;//":16
    private int bluetooth_volume;//":16
    private int notity_volume;//":16
    private int tts_volume;//":16
    private boolean sys_beep_switch;//":true,
    private int navi_mix_mode;//":"MIX",
    private int speed_compensate;//速度补偿音
    private boolean mute_switch ;//mute 状态
    private int [] audio_stream_arr   =  new int[30];
    private int navi_repression_media;
    private int avm_repression_media;
    private int current_stream_volume;
    private int media_symbol;
    private int mic_gain;
    private int radio_front_end_gain;
    private int mute_switch_symbol;
    private int setting_max_volume;
    private int media_unmute_symbol;
    private int sys_on_volume_min;
    private int sys_on_volume_min_symbol;

    public boolean isSys_on_volume_switch() {
        return sys_on_volume_switch;
    }

    public void setSys_on_volume_switch(boolean sys_on_volume_switch) {
        this.sys_on_volume_switch = sys_on_volume_switch;
    }

    public int getSys_on_volume() {
        return sys_on_volume;
    }

    public void setSys_on_volume(int sys_on_volume) {
        this.sys_on_volume = sys_on_volume;
    }

    public int getSys_on_temp_volume() {
        return sys_on_temp_volume;
    }

    public void setSys_on_temp_volume(int sys_on_temp_volume) {
        this.sys_on_temp_volume = sys_on_temp_volume;
    }

    public int getMain_volume() {
        return main_volume;
    }

    public void setMain_volume(int main_volume) {
        this.main_volume = main_volume;
    }

    public int getNavi_volume() {
        return navi_volume;
    }

    public void setNavi_volume(int navi_volume) {
        this.navi_volume = navi_volume;
    }

    public int getMedia_volume() {
        return media_volume;
    }

    public void setMedia_volume(int media_volume) {
        this.media_volume = media_volume;
    }

    public int getBluetooth_volume() {
        return bluetooth_volume;
    }

    public void setBluetooth_volume(int bluetooth_volume) {
        this.bluetooth_volume = bluetooth_volume;
    }

    public int getNotity_volume() {
        return notity_volume;
    }

    public void setNotity_volume(int notity_volume) {
        this.notity_volume = notity_volume;
    }

    public int getTts_volume() {
        return tts_volume;
    }

    public void setTts_volume(int tts_volume) {
        this.tts_volume = tts_volume;
    }

    public boolean isSys_beep_switch() {
        return sys_beep_switch;
    }

    public void setSys_beep_switch(boolean sys_beep_switch) {
        this.sys_beep_switch = sys_beep_switch;
    }

    public int getNavi_mix_mode() {
        return navi_mix_mode;
    }

    public void setNavi_mix_mode(int navi_mix_mode) {
        this.navi_mix_mode = navi_mix_mode;
    }

    public int getSpeed_compensate() {
        return speed_compensate;
    }

    public void setSpeed_compensate(int speed_compensate) {
        this.speed_compensate = speed_compensate;
    }

    public boolean isMute_switch() {
        return mute_switch;
    }

    public void setMute_switch(boolean mute_switch) {
        this.mute_switch = mute_switch;
    }

    public int[] getAudio_stream_arr() {
        return audio_stream_arr;
    }

    public void setAudio_stream_arr(int[] audio_stream_arr) {
        this.audio_stream_arr = audio_stream_arr;
    }

    public int getNavi_repression_media() {
        return navi_repression_media;
    }

    public void setNavi_repression_media(int navi_repression_media) {
        this.navi_repression_media = navi_repression_media;
    }

    public int getAvm_repression_media() {
        return avm_repression_media;
    }

    public void setAvm_repression_media(int avm_repression_media) {
        this.avm_repression_media = avm_repression_media;
    }

    public int getCurrent_stream_volume() {
        return current_stream_volume;
    }

    public void setCurrent_stream_volume(int current_stream_volume) {
        this.current_stream_volume = current_stream_volume;
    }

    public int getMedia_symbol() {
        return media_symbol;
    }

    public void setMedia_symbol(int media_symbol) {
        this.media_symbol = media_symbol;
    }

    public int getMic_gain() {
        return mic_gain;
    }

    public void setMic_gain(int mic_gain) {
        this.mic_gain = mic_gain;
    }

    public int getRadio_front_end_gain() {
        return radio_front_end_gain;
    }

    public void setRadio_front_end_gain(int radio_front_end_gain) {
        this.radio_front_end_gain = radio_front_end_gain;
    }

    public int getMute_switch_symbol() {
        return mute_switch_symbol;
    }

    public void setMute_switch_symbol(int mute_switch_symbol) {
        this.mute_switch_symbol = mute_switch_symbol;
    }

    public int getSetting_max_volume() {
        return setting_max_volume;
    }

    public void setSetting_max_volume(int setting_max_volume) {
        this.setting_max_volume = setting_max_volume;
    }

    public int getMedia_unmute_symbol() {
        return media_unmute_symbol;
    }

    public void setMedia_unmute_symbol(int media_unmute_symbol) {
        this.media_unmute_symbol = media_unmute_symbol;
    }

    public int getSys_on_volume_min() {
        return sys_on_volume_min;
    }

    public void setSys_on_volume_min(int sys_on_volume_min) {
        this.sys_on_volume_min = sys_on_volume_min;
    }

    public int getSys_on_volume_min_symbol() {
        return sys_on_volume_min_symbol;
    }

    public void setSys_on_volume_min_symbol(int sys_on_volume_min_symbol) {
        this.sys_on_volume_min_symbol = sys_on_volume_min_symbol;
    }
}
