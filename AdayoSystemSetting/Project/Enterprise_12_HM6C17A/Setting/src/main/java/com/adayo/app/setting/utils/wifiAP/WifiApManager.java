

package com.adayo.app.setting.utils.wifiAP;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;


import com.adayo.app.base.LogUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

public class WifiApManager {
    private final WifiManager mWifiManager;
    private final Context context;

    public WifiApManager(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }


    public void showWritePermissionSettings(boolean force) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (force || !Settings.System.canWrite(this.context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.context.startActivity(intent);
            }
        }
    }


    public boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        try {
            if (enabled) {mWifiManager.setWifiEnabled(false);
            }
            Method method= mWifiManager.getClass().getDeclaredMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.setAccessible(true);
return (Boolean) method.invoke(mWifiManager, wifiConfig, enabled);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }


    public WIFI_AP_STATE getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(mWifiManager));

           if (tmp >= 10) {
                tmp = tmp - 10;
            }

            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }


    public boolean isWifiApEnabled() {
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }


    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return null;
        }
    }


    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            return (Boolean) method.invoke(mWifiManager, wifiConfig);
        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
            return false;
        }
    }


    public void getClientList(boolean onlyReachables, FinishScanListener finishListener) {
        getClientList(onlyReachables, 2000, finishListener);
    }


    public void getClientList(final boolean onlyReachables, final int reachableTimeout, final FinishScanListener finishListener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                BufferedReader br = null;
                final ArrayList<ClientScanResult> result = new ArrayList<ClientScanResult>();

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    LogUtil.d("WifiApManager", "br = " + br);
                    String line;
                   Thread.sleep(500);
                    while ((line = br.readLine()) != null) {
                        LogUtil.d("WifiApManager", "line = " + line);
                        String[] tokens = line.split("[ ]+");
                        if (tokens.length < 6) {
                            continue;
                        }
                        String mac = tokens[3];
                        String device = tokens[5];
                        if (mac.matches("..:..:..:..:..:..")) {
                            boolean isReachable = InetAddress.getByName(tokens[0]).isReachable(reachableTimeout);
                           int count = 1;
                            while (!isReachable && count < 10) {
                                isReachable = InetAddress.getByName(tokens[0]).isReachable(reachableTimeout);
                                count++;
                            }
                            LogUtil.d("WifiApManager", "isReachable = " + isReachable);
                            LogUtil.d("WifiApManager", "HWAddr = " + tokens[3]);
                            if (!onlyReachables || (isReachable && ("wlan0".equals(device) || "wlan1".equals(device)))) {
                                LogUtil.d("WifiApManager", "HWAddr = " + tokens[3]);
                                result.add(new ClientScanResult(tokens[0], tokens[3], tokens[5], isReachable));
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().toString(), e.toString());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(this.getClass().toString(), e.getMessage());
                    }
                }

               Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d("WifiApManager", "result = " + result.size());
                        finishListener.onFinishScan(result);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }
}