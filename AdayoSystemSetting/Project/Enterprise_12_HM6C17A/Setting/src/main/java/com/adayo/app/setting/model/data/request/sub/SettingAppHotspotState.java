package com.adayo.app.setting.model.data.request.sub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.base.LogUtil;


public class SettingAppHotspotState {
    private final String TAG = SettingAppHotspotState.class.getName();
    private Context mContext;
    private BroadcastReceiver mTetherChangeReceiver;
    private final ConnectivityManager mConnectManager;

    private final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback = new ConnectivityManager.OnStartTetheringCallback() {
        @Override
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


    public SettingAppHotspotState(Context context) {
        mContext = context;
        mConnectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int state = Settings.Global.getInt(mContext.getContentResolver(), ParamConstant.HOTSPOT_SWITCH, WifiManager.WIFI_AP_STATE_DISABLED);
        LogUtil.i(TAG, "state =" + state);
        if (state == WifiManager.WIFI_AP_STATE_ENABLED) {
            LogUtil.i(TAG, "start AP");
            mConnectManager.startTethering(ConnectivityManager.TETHERING_WIFI, true, mOnStartTetheringCallback);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.ACTION_TETHER_STATE_CHANGED);
        intentFilter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
        registerAPReceiver();
        context.registerReceiver(mTetherChangeReceiver, intentFilter);
    }

    private void registerAPReceiver() {
        LogUtil.i(TAG);
        mTetherChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                LogUtil.debugD(TAG, "action = " + action);
                if (action.equals(WifiManager.WIFI_AP_STATE_CHANGED_ACTION)) {
                    int wifiApState = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, 0);
                    LogUtil.i(TAG, "wifiApState = " + wifiApState);
                    if (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED || wifiApState == WifiManager.WIFI_AP_STATE_DISABLED) {
                        LogUtil.i(TAG, "put wifiApState = " + wifiApState);
                        Settings.Global.putInt(mContext.getContentResolver(), ParamConstant.HOTSPOT_SWITCH, wifiApState);
                    }
                }
            }
        };
    }
}
