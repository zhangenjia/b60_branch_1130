package com.adayo.app.systemui.windows.views;

import android.annotation.Nullable;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.bases.BaseTextView;
import com.adayo.app.systemui.bean.BluetoothInfo;
import com.adayo.app.systemui.bean.HotspotInfo;
import com.adayo.app.systemui.bean.WiFiInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.SystemStatusManager;
import com.adayo.app.systemui.managers.business.BluetoothControllerImpl;
import com.adayo.app.systemui.managers.business.HotspotControllerImpl;
import com.adayo.app.systemui.managers.business.SourceControllerImpl;
import com.adayo.app.systemui.managers.business.WiFiControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.utils.StringUtils;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.util.HashMap;

public class SettingSwitchButton extends BaseTextView {
    private BluetoothControllerImpl bluetoothController;
    private WiFiControllerImpl wiFiController;
    private HotspotControllerImpl hotspotController;

    public SettingSwitchButton(Context context) {
        super(context);
    }

    public SettingSwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingSwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onClick(View v) {
        switch (controlType) {
            case SystemUIContent.BLUETOOTH:
                if (null != bluetoothController) {
                    bluetoothController.setBTEnabled(!bluetoothController.isBTEnabled());
                }
                break;
            case SystemUIContent.WIFI:
                if (null != wiFiController) {
                    wiFiController.setWiFiEnable(!wiFiController.isWiFiEnable());
                }
                break;
            case SystemUIContent.HOTSPOT:
                if (null != hotspotController) {
                    hotspotController.setHotspotEnabled(!hotspotController.isHotspotEnabled());
                }
                break;
            case SystemUIContent.SCREEN:
                if (SystemUIContent.SYSTEM_STATUS_SCREEN == systemStatus) {
                    SystemStatusManager.getInstance().setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_OFF);
                } else if (SystemUIContent.SYSTEM_STATUS_POWER == systemStatus) {
                    SystemStatusManager.getInstance().setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_POWEROFF);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(controlType == SystemUIContent.BLUETOOTH){
            BluetoothInfo bluetoothInfo = bluetoothController.getBluetoothInfo();
            if (null != bluetoothInfo) {
                String deviceName = bluetoothInfo.isMount() && !TextUtils.isEmpty(bluetoothInfo.getDeviceName()) ?
                        bluetoothInfo.getDeviceName() : SystemUIApplication.getSystemUIContext().getString(R.string.bluetooth);
                updateView(bluetoothInfo.isEnable(), bluetoothInfo.isMount(), deviceName);
            }
        }
        if(controlType == SystemUIContent.WIFI){
            String deviceName = StringUtils.stringReplace(WiFiControllerImpl.getInstance().getSsid(), "\"", "");
            deviceName = WiFiControllerImpl.getInstance().isWiFiConnected() && !TextUtils.isEmpty(deviceName) ?
                        deviceName : SystemUIApplication.getSystemUIContext().getString(R.string.wifi);
            updateView(WiFiControllerImpl.getInstance().isWiFiEnable(), WiFiControllerImpl.getInstance().isWiFiConnected(), deviceName);
        }
    }

    @Override
    protected void onLongClick(View v) {
        LogUtil.d(SystemUIContent.TAG, "controlType = " + controlType +
                " ; source = " + source + " ; needShowText = " + needShowText +
                " ; mLoop = " + mLoop + " ; responseLongClick = " + responseLongClick +
                " ; systemStatus = " + systemStatus);
        if (SystemUIContent.SCREEN == controlType) {
            SystemStatusManager.getInstance().setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_OFF);
        } else if (null != source && SystemUIContent.controlTypes.length > controlType && SystemUIContent.controlTypes.length > 0) {
            HashMap<String, String> map = new HashMap<>();
            map.put("setting_page", SystemUIContent.controlTypes[controlType]);
            SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), source, map);
            WindowsManager.setQsPanelVisibility(View.GONE);
        }
    }
    @Override
    protected void analyticConfig() {
        switch (controlType) {
            case SystemUIContent.BLUETOOTH:
                LogUtil.d(SystemUIContent.TAG, "BLUETOOTH");
                bluetoothController = BluetoothControllerImpl.getInstance();
                bluetoothController.addCallback((BaseCallback<BluetoothInfo>) bluetoothInfo -> {
                    if (null != bluetoothInfo) {
                        String deviceName = bluetoothInfo.isMount() && !TextUtils.isEmpty(bluetoothInfo.getDeviceName()) ?
                                bluetoothInfo.getDeviceName() : (bluetoothInfo.isEnable() ?
                                SystemUIApplication.getSystemUIContext().getString(R.string.unconnected) : SystemUIApplication.getSystemUIContext().getString(R.string.bluetooth));
                        updateView(bluetoothInfo.isEnable(), bluetoothInfo.isMount(), deviceName);
                    }
                });
                break;
            case SystemUIContent.WIFI:
                LogUtil.d(SystemUIContent.TAG, "WIFI");
                wiFiController = WiFiControllerImpl.getInstance();
                wiFiController.addCallback((BaseCallback<WiFiInfo>) wiFiInfo -> {
                    if (null != wiFiInfo) {
                        String deviceName = StringUtils.stringReplace(wiFiInfo.getDeviceName(), "\"", "");
                        deviceName = wiFiInfo.isConnected() && !TextUtils.isEmpty(deviceName) ?
                                deviceName : (wiFiInfo.isEnable() ? SystemUIApplication.getSystemUIContext().getString(R.string.unconnected) :
                                SystemUIApplication.getSystemUIContext().getString(R.string.wifi));
                        updateView(wiFiInfo.isEnable(), wiFiInfo.isConnected(), deviceName);
                    }
                });
                break;
            case SystemUIContent.HOTSPOT:
                LogUtil.d(SystemUIContent.TAG, "HOTSPOT");
                hotspotController = HotspotControllerImpl.getInstance();
                hotspotController.addCallback((BaseCallback<HotspotInfo>) hotspotInfo -> {
                    if (null != hotspotInfo) {
                        LogUtil.debugD(SystemUIContent.TAG, "getmHotspotState = " + hotspotInfo.getmHotspotState()
                                + " ; getmNumConnectedDevices = " + hotspotInfo.getmNumConnectedDevices());
                        updateView(hotspotController.isHotspotEnabled(), hotspotInfo.getmNumConnectedDevices() > 0, hotspotInfo.getmNumConnectedDevices() + "");
                    }
                });
                break;
            default:
                break;
        }
    }

    private void updateView(boolean isEnable, boolean isMount, String deviceName){
        LogUtil.debugD(SystemUIContent.TAG, "isEnable = " + isEnable + " ; isMount = " + isMount  + " ; deviceName = " + deviceName);
        setSelected(isMount);
        setActivated(isEnable);
        if(!isEnable){
            setAlpha(0.3f);
        }else {
            setAlpha(1.0f);
        }
        if(needShowText) {
            setText(deviceName);
        }
    }
}
