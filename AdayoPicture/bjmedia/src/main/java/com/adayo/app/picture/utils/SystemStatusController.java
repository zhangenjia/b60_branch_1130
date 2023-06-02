package com.adayo.app.picture.utils;

public interface SystemStatusController {
    /**
     * 判断systemService是否正常连接
     *
     * @return true:已连接  false:未连接 registered
     */
    boolean isSystemServiceConnected();

    /**
     * 注册回调监听
     */
    void addCallback(SystemStatusChangeCallback callback);

    /**
     * 取消注册监听
     */
    void removeCallback(SystemStatusChangeCallback callback);

    /**
     * 请求关闭屏幕
     */
    void requestPowerAction();

    /**
     * 获取当前电源状态
     */
    byte getSystemStatus();

    public interface SystemStatusChangeCallback {
        void onSystemStatusChanged(byte systemStatus);
    }
}
