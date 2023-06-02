package com.adayo.app.systemui.managers.business;

/**
 * USB状态
 * @author xuyue
 * @created 2020/11/13
 */
public interface USBController {
    int getUSBDeviceNum();
    int getUSBMountNum();
    boolean isUSBDevice();
    boolean isUSBMount();
}
