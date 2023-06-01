package com.adayo.app.btphone.callstate;


public interface ICallState {

    void updateDisplayStatus();

    void updateCallerName(String name);

    void updateCallingTime(String time);

    void updateMutiCallerName(String name);

    void updateMutiCallingTime(String time);

    void hangUpCall();

}
