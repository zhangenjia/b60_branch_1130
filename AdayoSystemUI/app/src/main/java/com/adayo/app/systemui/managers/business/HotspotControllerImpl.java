package com.adayo.app.systemui.managers.business;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;

import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.HotspotInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;

public class HotspotControllerImpl extends BaseControllerImpl<HotspotInfo> implements HotspotController, WifiManager.SoftApCallback {

    private final WifiStateReceiver mWifiStateReceiver = new WifiStateReceiver();
    private final ConnectivityManager mConnectivityManager;
    private final WifiManager mWifiManager;

    private HotspotInfo hotspotInfo = new HotspotInfo();

    private volatile static HotspotControllerImpl mHotspotControllerImpl;

    private HotspotControllerImpl() {
        mConnectivityManager =
                (ConnectivityManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WIFI_SERVICE);
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static HotspotControllerImpl getInstance() {
        if (mHotspotControllerImpl == null) {
            synchronized (HotspotControllerImpl.class) {
                if (mHotspotControllerImpl == null) {
                    mHotspotControllerImpl = new HotspotControllerImpl();
                }
            }
        }
        return mHotspotControllerImpl;
    }

    @Override
    public boolean isHotspotSupported() {
        return mConnectivityManager.isTetheringSupported()
                && mConnectivityManager.getTetherableWifiRegexs().length != 0
                && UserManager.get(SystemUIApplication.getSystemUIContext()).isUserAdmin(ActivityManager.getCurrentUser());
    }

    /**
     * Updates the wifi state receiver to either start or stop listening to get updates to the
     * hotspot status. Additionally starts listening to wifi manager state to track the number of
     * connected devices.
     *
     * @param shouldListen whether we should start listening to various wifi statuses
     */
    private void updateWifiStateListeners(boolean shouldListen) {
        mWifiStateReceiver.setListening(shouldListen);
        if (shouldListen) {
            mWifiManager.registerSoftApCallback(
                    this,
                    new Handler(Looper.getMainLooper()));
        } else {
            mWifiManager.unregisterSoftApCallback(this);
        }
    }

    @Override
    public boolean isHotspotEnabled() {
        return hotspotInfo.getmHotspotState() == WifiManager.WIFI_AP_STATE_ENABLED;
    }

    @Override
    public boolean isHotspotTransient() {
        return hotspotInfo.ismWaitingForCallback() || (hotspotInfo.getmHotspotState() == WifiManager.WIFI_AP_STATE_ENABLING);
    }

    private OnStartTetheringCallback myOnStartTetheringCallback;

    @Override
    public void setHotspotEnabled(boolean enabled) {
        if (hotspotInfo.ismWaitingForCallback()) {
            LogUtil.d(SystemUIContent.TAG, "Ignoring setHotspotEnabled; waiting for callback.");
            return;
        }
        if (enabled) {
            if(null == myOnStartTetheringCallback) {
                myOnStartTetheringCallback = new OnStartTetheringCallback();
            }
            hotspotInfo.setmWaitingForCallback(true);
            LogUtil.d(SystemUIContent.TAG, "Starting tethering");
            mConnectivityManager.startTethering(
                    ConnectivityManager.TETHERING_WIFI, true, myOnStartTetheringCallback);
        } else {
            mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
        }
    }

    @Override
    public int getNumConnectedDevices() {
        return hotspotInfo.getmNumConnectedDevices();
    }

    /**
     * Sends a hotspot changed callback with the new enabled status. Wraps
     * {@link #fireHotspotChangedCallback(boolean, int)} and assumes that the number of devices has
     * not changed.
     *
     * @param enabled whether the hotspot is enabled
     */
    private void fireHotspotChangedCallback(boolean enabled) {
        LogUtil.d(SystemUIContent.TAG, "mNumConnectedDevices " + hotspotInfo.getmNumConnectedDevices());
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    /**
     * Sends a hotspot changed callback with the new enabled status & the number of devices
     * connected to the hotspot. Be careful when calling over multiple threads, especially if one of
     * them is the main thread (as it can be blocked).
     *
     * @param enabled whether the hotspot is enabled
     * @param numConnectedDevices number of devices connected to the hotspot
     */
    private void fireHotspotChangedCallback(boolean enabled, int numConnectedDevices) {
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    private void fireHotspotChangedCallback() {
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    @Override
    public void onStateChanged(int state, int failureReason) {
        LogUtil.d(SystemUIContent.TAG, "state = " + state + " ; failureReason = " + failureReason);
        // Do nothing - we don't care about changing anything here.
    }

    @Override
    public void onNumClientsChanged(int numConnectedDevices) {
        LogUtil.d(SystemUIContent.TAG, "numConnectedDevices " + numConnectedDevices);
        hotspotInfo.setmNumConnectedDevices(numConnectedDevices);
        fireHotspotChangedCallback();
    }

    @Override
    protected boolean registerListener() {
        updateWifiStateListeners(true);
        return true;
    }

    @Override
    protected HotspotInfo getDataInfo() {
        return hotspotInfo;
    }

    private final class OnStartTetheringCallback extends
            ConnectivityManager.OnStartTetheringCallback {
        @Override
        public void onTetheringStarted() {
            LogUtil.d(SystemUIContent.TAG, "onTetheringStarted");
            // Don't fire a callback here, instead wait for the next update from wifi.
        }



        @Override
        public void onTetheringFailed() {
            LogUtil.d(SystemUIContent.TAG, "onTetheringFailed");
            hotspotInfo.setmWaitingForCallback(false);
            fireHotspotChangedCallback(isHotspotEnabled());
          // TODO: Show error.
        }
    }

    /**
     * Class to listen in on wifi state and update the hotspot state
     */
    private final class WifiStateReceiver extends BroadcastReceiver {
        private boolean mRegistered;

        public void setListening(boolean listening) {
            if (listening && !mRegistered) {
                LogUtil.d(SystemUIContent.TAG, "Registering receiver");
                final IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
                SystemUIApplication.getSystemUIContext().registerReceiver(this, filter);
                mRegistered = true;
            } else if (!listening && mRegistered) {
                LogUtil.d(SystemUIContent.TAG, "Unregistering receiver");
                SystemUIApplication.getSystemUIContext().unregisterReceiver(this);
                mRegistered = false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(
                    WifiManager.EXTRA_WIFI_AP_STATE, WifiManager.WIFI_AP_STATE_FAILED);
            LogUtil.d(SystemUIContent.TAG, "onReceive " + state);
            if(state == WifiManager.WIFI_AP_STATE_ENABLED || state == WifiManager.WIFI_AP_STATE_DISABLED) {
                hotspotInfo.setmHotspotState(state);
                if (!isHotspotEnabled()) {
                    hotspotInfo.setmNumConnectedDevices(0);
                }
                fireHotspotChangedCallback(isHotspotEnabled());
                hotspotInfo.setmWaitingForCallback(false);
            }else if (state == WifiManager.WIFI_AP_STATE_FAILED) {
                hotspotInfo.setmWaitingForCallback(false);
            }
        }
    }
}
