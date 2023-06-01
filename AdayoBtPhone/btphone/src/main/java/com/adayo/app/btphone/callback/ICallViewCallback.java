package com.adayo.app.btphone.callback;

public interface ICallViewCallback {

    void showCallView(int type);

    void updateMicMuteOrUnMuteState(int state);

    void updateAudioOnCarOrPhoneState(int state);

    void updateCallerName(String name);

    void updateCallerNumber(String number);

    void updateCallingTime(String time);

    void updateInputNumber(String number);

    void update3WayCallActiveChanged(int state);

    void updateMutiCallerName(String name);

    void updateMutiCallerNumber(String number);

    void updateMutiCallingTime(String time);
}
