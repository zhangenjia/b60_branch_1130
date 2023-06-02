package com.adayo.app.setting.hotspot;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.base.LogUtil;

import java.util.Objects;


public class HotspotRequest extends BaseRequest {
    private final static String TAG = HotspotRequest.class.getSimpleName();
    private final ConnectivityManager mConnectManager = (ConnectivityManager) getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);private final WifiManager mWifiManager = (WifiManager) getAppContext().getSystemService(Context.WIFI_SERVICE);private final MutableLiveData<Integer> mHotspotEnableStateLiveData = new MutableLiveData<>();private final MutableLiveData<String> mHotspotSSIDLiveData = new MutableLiveData<>();private final MutableLiveData<String> mHotspotPasswordLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mHotspotClientsNumLiveData = new MutableLiveData<>();
    private final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback = new ConnectivityManager.OnStartTetheringCallback() {@Override
        public void onTetheringStarted() {
            super.onTetheringStarted();
            LogUtil.debugV(TAG, "");
        }

        @Override
        public void onTetheringFailed() {
            super.onTetheringFailed();
            LogUtil.debugV(TAG, "");
        }
    };

    private boolean mRestartWifiApAfterConfigChange;
    private final BroadcastReceiver mTetherChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtil.debugD(TAG, "action = " + action);
            if (action.equals(ConnectivityManager.ACTION_TETHER_STATE_CHANGED)) {int wifiApState = mWifiManager.getWifiApState();LogUtil.i(TAG, "wifiApState = " + wifiApState);
                if (!Objects.equals(wifiApState, getHotspotEnableStateLiveData().getValue())) {mHotspotEnableStateLiveData.setValue(wifiApState);
                }
                if (wifiApState == WifiManager.WIFI_AP_STATE_DISABLED && mRestartWifiApAfterConfigChange) {startTether();
                }
                WifiConfiguration wifiApConfiguration = mWifiManager.getWifiApConfiguration();mHotspotSSIDLiveData.setValue(wifiApConfiguration.SSID);mHotspotPasswordLiveData.setValue(wifiApConfiguration.preSharedKey);} else if (action.equals(WifiManager.WIFI_AP_STATE_CHANGED_ACTION)) {int wifiApState = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, 0);LogUtil.i(TAG, "wifiApState = " + wifiApState + "mHotspotEnableStateLiveData.getValue() =" + getHotspotEnableStateLiveData().getValue());
                mHotspotEnableStateLiveData.setValue(wifiApState);
                if (wifiApState == WifiManager.WIFI_AP_STATE_DISABLED && mRestartWifiApAfterConfigChange) {startTether();}
            }
        }
    };


    private final WifiManager.SoftApCallback mSoftApCallback = new WifiManager.SoftApCallback() {
        @Override
        public void onStateChanged(int i, int i1) {

        }


        @Override
        public void onNumClientsChanged(int i) {
            LogUtil.i(TAG, "i = " + i);
            if (!Objects.equals(i, getHotspotClientsNumLiveData().getValue())) {
                mHotspotClientsNumLiveData.setValue(i);
            }
        }
    };


    private void startTether() {
        LogUtil.debugD(TAG, "");
        mRestartWifiApAfterConfigChange = false;
        mConnectManager.startTethering(ConnectivityManager.TETHERING_WIFI, true ,mOnStartTetheringCallback, new Handler(Looper.getMainLooper()));
    }


    private void stopTether() {
        LogUtil.debugD(TAG, "");
        mRestartWifiApAfterConfigChange = true;
        mConnectManager.stopTethering(ConnectivityManager.TETHERING_WIFI);}



    private void parseHotspotCfg() {
        WifiConfiguration wifiApConfiguration = mWifiManager.getWifiApConfiguration();mHotspotEnableStateLiveData.setValue(mWifiManager.getWifiApState());mHotspotSSIDLiveData.setValue(wifiApConfiguration.SSID);mHotspotPasswordLiveData.setValue(wifiApConfiguration.preSharedKey);LogUtil.i(TAG, "mHotspotEnableStateLiveData ="+mWifiManager.getWifiApState()+" mHotspotSSIDLiveData ="+wifiApConfiguration.SSID+" mHotspotPasswordLiveData ="+wifiApConfiguration.preSharedKey);


    }


    public void requestEnableState(boolean enable) {
        LogUtil.i(TAG, "enable = " + enable);
        if (enable) {
            mConnectManager.startTethering(ConnectivityManager.TETHERING_WIFI, true, mOnStartTetheringCallback);} else {
            mConnectManager.stopTethering(ConnectivityManager.TETHERING_WIFI);}

    }


    public void requestHotspotName(String name) {
        LogUtil.i(TAG, "name = " + name);
        requestHotspotCfg(name, null);
    }


    public void requestHotspotPwd(String password) {
        LogUtil.i(TAG, "password = " + password);
        requestHotspotCfg(null, password);
    }


    public void requestHotspotCfg(String name, String password) {
        LogUtil.i(TAG, "name = " + name + ", password = " + password);
        WifiConfiguration apConfiguration = mWifiManager.getWifiApConfiguration();
        LogUtil.debugD(TAG, "apConfiguration.SSID = " + apConfiguration.SSID
                + ", apConfiguration.preSharedKey = " + apConfiguration.preSharedKey
                + ", apConfiguration.allowedKeyManagement = " + apConfiguration.allowedKeyManagement
                + ", apConfiguration.allowedAuthAlgorithms = " + apConfiguration.allowedAuthAlgorithms
                + ", apConfiguration.apBand = " + apConfiguration.apBand);if (Objects.nonNull(name)) {
            apConfiguration.SSID = name;}
        if (Objects.nonNull(password)) {
            apConfiguration.preSharedKey = password;
        }
        if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {stopTether();}
        boolean isSuccess = mWifiManager.setWifiApConfiguration(apConfiguration);LogUtil.debugD(TAG, "setWifiApConfiguration isSuccess = " + isSuccess);
        if (isSuccess) {if (Objects.nonNull(name)) {
                mHotspotSSIDLiveData.setValue(name);
            }
            if (Objects.nonNull(password)) {
                mHotspotPasswordLiveData.setValue(password);
            }
        }
    }

    public void init() {
        LogUtil.debugD(TAG, "");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);intentFilter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        getAppContext().registerReceiver(mTetherChangeReceiver, intentFilter);
        mWifiManager.registerSoftApCallback(mSoftApCallback, new Handler());
        parseHotspotCfg();
    }

    public void unInit() {
        LogUtil.debugD(TAG, "");
        getAppContext().unregisterReceiver(mTetherChangeReceiver);
        mWifiManager.unregisterSoftApCallback(mSoftApCallback);
    }

    public LiveData<Integer> getHotspotEnableStateLiveData() {
        if (mHotspotEnableStateLiveData.getValue() == null) {
            LogUtil.w(TAG,"mHotspotEnableStateLiveData = null");
            mHotspotEnableStateLiveData.setValue(0);
        }
        return mHotspotEnableStateLiveData;
    }

    public LiveData<String> getHotspotSSIDLiveData() {
        if (mHotspotSSIDLiveData.getValue() == null) {
            mHotspotSSIDLiveData.setValue("0");
        }
        return mHotspotSSIDLiveData;
    }

    public LiveData<String> getHotspotPasswordLiveData() {
        if (mHotspotPasswordLiveData.getValue() == null) {
            mHotspotPasswordLiveData.setValue("0");
        }
        return mHotspotPasswordLiveData;
    }

    public LiveData<Integer> getHotspotClientsNumLiveData() {
        if (mHotspotClientsNumLiveData.getValue() == null) {
            mHotspotClientsNumLiveData.setValue(0);
        }
        return mHotspotClientsNumLiveData;
    }
}
