package com.adayo.app.btphone.callback;

import com.adayo.common.bluetooth.bean.PeopleInfo;

import java.util.List;

public interface IBTLinkManCallback {

    void updateContactsSyncState(String state);

    void updateContactsList(List<PeopleInfo> list, List<String> list1);

    void updateHfpState(boolean state);

    void updateA2DPState(boolean state);

    void updateNoContactsAlertState(boolean state);
}
