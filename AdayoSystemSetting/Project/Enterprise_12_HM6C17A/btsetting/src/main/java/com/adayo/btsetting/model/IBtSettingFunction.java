package com.adayo.btsetting.model;

/**
 * @author Y4134
 */
public interface IBtSettingFunction {

    /**
     * 初始化
     */
    void init();

    /**
     * 设置蓝牙状态
     * @param address 地址
     * @param action 状态
     */
    void setBluetothPairAction(String address, int action);

    /**
     * 配对
     * @param address 地址
     */
    void reqPair(String address);
}
