package com.adayo.app.systemui.managers.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.WiFiInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;

public class WiFiControllerImpl extends BaseControllerImpl<WiFiInfo> implements WiFiController {
    private volatile static WiFiControllerImpl mWiFiControllerImpl;
    private HotspotControllerImpl mHotspotControllerImpl;
    private WiFiInfo mWiFiInfo;

    private WifiManager wifiManager;

    private WiFiControllerImpl() {
        mHotspotControllerImpl = HotspotControllerImpl.getInstance();
        wifiManager = (WifiManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WIFI_SERVICE);
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        SystemUIApplication.getSystemUIContext().registerReceiver(wifiStateBroadcastReceiver, filter);
    }

    public static WiFiControllerImpl getInstance() {
        if (mWiFiControllerImpl == null) {
            synchronized (WiFiControllerImpl.class) {
                if (mWiFiControllerImpl == null) {
                    mWiFiControllerImpl = new WiFiControllerImpl();
                }
            }
        }
        return mWiFiControllerImpl;
    }

    @Override
    public boolean isWiFiEnable() {
        if (wifiManager != null) {
            return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED ? true : false;
        }
        return false;
    }

    @Override
    public void setWiFiEnable(boolean enable) {
        if (wifiManager != null) {
            if (enable && (wifiManager.getWifiApState() == wifiManager.WIFI_AP_STATE_ENABLING || wifiManager.getWifiApState() == wifiManager.WIFI_AP_STATE_ENABLED)) {
                mHotspotControllerImpl.setHotspotEnabled(false);
            }
            wifiManager.setWifiEnabled(enable);
        }
    }

    @Override
    public boolean isWiFiConnected() {
        WifiManager mWifiManager = (WifiManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            return true;
        }
        return false;
    }

    @Override
    public int getRssi() {
        if (wifiManager == null) {
            return 0;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        return info != null ? info.getRssi() : 0;
    }

    @Override
    public String getSsid() {
        if (wifiManager == null) {
            return null;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        return info != null ? info.getSSID() : null;
    }

    @Override
    protected boolean registerListener() {
        return true;
    }

    @Override
    protected WiFiInfo getDataInfo() {
        if(null == mWiFiInfo){
            mWiFiInfo = new WiFiInfo();
            mWiFiInfo.setEnable(isWiFiEnable());
            mWiFiInfo.setConnected(isWiFiConnected());
            mWiFiInfo.setDeviceName(getSsid());
            mWiFiInfo.setRssi(WifiManager.calculateSignalLevel(getRssi(), 5));
        }
        return mWiFiInfo;
    }

    private BroadcastReceiver wifiStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d(SystemUIContent.TAG, "getAction" + intent.getAction());
            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case WifiManager.RSSI_CHANGED_ACTION:
                    if (null != mWiFiInfo) {
                        mWiFiInfo.setEnable(isWiFiEnable());
                        mWiFiInfo.setConnected(isWiFiConnected());
                        mWiFiInfo.setDeviceName(getSsid());
                        int rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
                        rssi = WifiManager.calculateSignalLevel(rssi, 5);
                        mWiFiInfo.setRssi(rssi);
                    }
                    break;
                default:
                    break;
            }
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    };
}
