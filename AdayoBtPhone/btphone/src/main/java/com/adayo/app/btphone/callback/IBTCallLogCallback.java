package com.adayo.app.btphone.callback;

import android.util.Log;

import com.adayo.common.bluetooth.bean.PeopleInfo;

import java.util.List;

public interface IBTCallLogCallback {

    void updateCallLogSyncState(String state);

    void updateAllCallLog(List<PeopleInfo> peopleList);

    void showNoCallLogDataAlert();

    void hideNoCallLogDataAlert();

    void showNotFoundCallLogAlert();

    void hideNotFoundCallLogAlert();
}
