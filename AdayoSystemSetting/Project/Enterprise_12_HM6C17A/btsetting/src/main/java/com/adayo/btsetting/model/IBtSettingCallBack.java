package com.adayo.btsetting.model;

/**
 * @author Y4134
 */
public interface IBtSettingCallBack {

    /**
     * 蓝牙开关状态改变回调
     * @param prevState 以前状态
     * @param newState 新状态
     */
    void onAdapterStateChanged(int prevState, int newState);
    /**
     * 蓝牙开关状态回调
     * @param isEnable 开关
     */
    void onEnableChanged(boolean isEnable);
    /**
     * 蓝牙连接状态回调，只要有任意一个协议（hfp,a2dp,avrcp）连接，则返回true
     * @param address mac地址
     * @param connectState 连接状态
     */
    void onConnectedChanged(String address, int connectState);
    /**
     * 蓝牙hfp连接状态回调
     * @param address mac地址
     * @param connectState 连接状态
     */
    void onHfpStateChanged(String address, int connectState);
    /**
     * 蓝牙 HFP 连接远程设备的音频状态变化
     * @param address  mac地址
     * @param prevState 以前状态
     * @param newState 新状态
     */
    void onHfpAudioStateChanged(String address, int prevState, int newState);
    /**
     * 蓝牙avrcp连接状态回调
     * @param address mac地址
     * @param connectState 连接状态
     */
    void onAvrcpStateChanged(String address, int connectState);
    /**
     * 蓝牙a2dp连接状态回调
     * @param address mac地址
     * @param connectState 连接状态
     */
    void onA2dpStateChanged(String address, int connectState);
    /**
     * 开始扫描新设备回调
     */
    void onAdapterDiscoveryStarted();
    /**
     * 结束扫描新设备回调
     */
    void onAdapterDiscoveryFinished();
    /**
     * 历史配对设备回调
     * @param elements elements
     * @param address address
     * @param name name
     * @param supportProfile supportProfile
     */
    void retPairedDevices(int elements, String[] address, String[] name, int[] supportProfile);
    /**
     * 扫描到新设备回调
     * @param address mac地址
     * @param name 设备名称
     */
    void onDeviceFound(String address, String name);
    /**
     * 配对状态回调
     * @param address mac地址
     * @param name 名称
     * @param newState 新状态
     */
    void onDeviceBondStateChanged(String address, String name, int newState);
    /**
     * 车机蓝牙名称变化回调
     * @param name 名称
     */
    void onLocalAdapterNameChanged(String name);
    /**
     * 车机蓝牙配对状态变化回调
     * @param name 名称
     * @param address 地址
     * @param type 类型
     * @param pairingValue  pairingValue
     */
    void onPairStateChanged(String name, String address, int type, int pairingValue);
    /**
     * 蓝牙主设备变化回调
     * @param address mac地址
     * @param name 名称
     */
    void onMainDevicesChanged(String address, String name);
}
