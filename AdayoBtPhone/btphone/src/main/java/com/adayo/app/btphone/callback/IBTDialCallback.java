package com.adayo.app.btphone.callback;

import com.adayo.common.bluetooth.bean.PeopleInfo;

import java.util.List;

public interface IBTDialCallback {

    void updateContacts(List<PeopleInfo> list);

    void updateActiveDeviceChanged(String address, String name, boolean isMutiDev);

}
