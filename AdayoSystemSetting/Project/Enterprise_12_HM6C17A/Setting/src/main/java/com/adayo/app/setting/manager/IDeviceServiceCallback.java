package com.adayo.app.setting.manager;

import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

public interface IDeviceServiceCallback {

    void notifyInitComplete();

    AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS getPowerState();

    AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS getSystemState();

}
