package com.adayo.app.systemui.windows.views;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.car.VehiclePropertyIds;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bean.UpgradeInfo;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.bean.BluetoothInfo;
import com.adayo.app.systemui.bean.DVRInfo;
import com.adayo.app.systemui.bean.HotspotInfo;
import com.adayo.app.systemui.bean.HvacInfo;
import com.adayo.app.systemui.bean.NaviInfo;
import com.adayo.app.systemui.bean.SystemUISourceInfo;
import com.adayo.app.systemui.bean.TboxInfo;
import com.adayo.app.systemui.bean.USBInfo;
import com.adayo.app.systemui.bean.VolumeInfo;
import com.adayo.app.systemui.bean.WiFiInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.SystemStatusManager;
import com.adayo.app.systemui.managers.business.BluetoothControllerImpl;
import com.adayo.app.systemui.managers.business.DvrControllerImpl;
import com.adayo.app.systemui.managers.business.HotspotControllerImpl;
import com.adayo.app.systemui.managers.business.NaviControllerImpl;
import com.adayo.app.systemui.managers.business.SourceControllerImpl;
import com.adayo.app.systemui.managers.business.TboxControllerImpl;
import com.adayo.app.systemui.managers.business.USBControllerImpl;
import com.adayo.app.systemui.managers.business.UpgradeControllerImpl;
import com.adayo.app.systemui.managers.business.VolumeControllerImpl;
import com.adayo.app.systemui.managers.business.WiFiControllerImpl;
import com.adayo.app.systemui.utils.ConfigUtils;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.msg.notify.IMessageNotifyListener;
import com.adayo.proxy.msg.notify.bean.MessageNotification;
import com.adayo.proxy.msg.notify.control.MessageManager;
import com.adayo.proxy.setting.bcm.IClientCallBack;
import com.adayo.proxy.setting.bcm.controller.BcmManager;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_RelyInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.adayo.app.systemui.configs.HvacContent.AIR_QUALITY_SWITCH_OFF;
import static com.adayo.app.systemui.configs.HvacContent.AIR_QUALITY_SWITCH_ON;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_ALL;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_ANION_SWITCH;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_INSIDE_PM25_VALUE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_PM25_DISPLAY;
import static com.adayo.app.systemui.configs.SystemUIConfigs.WIRELESS_CHARGING;
import static com.adayo.app.systemui.configs.SystemUIContent.CHARGING;
import static com.adayo.app.systemui.configs.SystemUIContent.CHARGING_COMPLETED;
import static com.adayo.app.systemui.configs.SystemUIContent.DEVICE_INFO;
import static com.adayo.app.systemui.configs.SystemUIContent.STANDBY;
import static com.adayo.app.systemui.configs.SystemUIContent.TAG;
import static com.adayo.app.systemui.configs.SystemUIContent.WPC_FAULT;
import static com.adayo.app.systemui.configs.SystemUIContent.WPC_FAULT1;
import static com.adayo.app.systemui.configs.SystemUIContent.WPC_FAULT2;
import static com.adayo.app.systemui.configs.SystemUIContent.WPC_FAULT3;
import static com.adayo.app.systemui.configs.SystemUIContent.WPC_FAULT4;
import static com.adayo.app.systemui.configs.SystemUIContent.WPC_FAULT5;
import static com.adayo.app.systemui.configs.SystemUIContent.WPC_FAULT6;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_DATA_FLOW;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_SYS_UPGRADE;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_TSP_NOTIFICATION;

public class SystemIconsView extends LinearLayout implements AAOP_HvacManager.AAOP_HvacBindCallback, AAOP_HvacManager.AAOP_HvacServiceConnectCallback {
    private IconView btIcon;
    private IconView electricQuantityIcon;
    private IconView wifiIcon;
    private IconView hotspotIcon;
    private IconView usbIcon;
    private IconView muteIcon;
    private IconView dvrIcon;
    private IconView naviIcon;
    private IconView tboxIcon;
    private MessageIconView messageIcon;
    private IconView wirelessChargingIcon;
    private IconView upgradeIcon;
    private IconView anionIcon;
    private TextView airQualityIcon;

    private BluetoothControllerImpl mBluetoothControllerImpl;
    private USBControllerImpl mUSBControllerImpl;
    private WiFiControllerImpl mWiFiControllerImpl;
    private HotspotControllerImpl mHotspotControllerImpl;
    private VolumeControllerImpl volumeController;
    private SourceControllerImpl sourceController;
    private DvrControllerImpl dvrController;
    private TboxControllerImpl tboxController;
    private NaviControllerImpl naviController;
    private UpgradeControllerImpl upgradeController;
    private BcmManager bcmManager;
    private List<String> bcmDeviceList = new ArrayList<>();
    private AAOP_HvacManager hvacManager;
    private SystemStatusManager systemServiceManager;

    private boolean hasRegisterBCM = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.d(TAG, "onServiceConnected() begin");
            MessageManager.getInstance().init(SystemUIApplication.getSystemUIContext());
            MessageManager.getInstance().requestMessageListener(mListener, MSG_TYPE_DATA_FLOW | MSG_TYPE_TSP_NOTIFICATION | MSG_TYPE_SYS_UPGRADE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.d(TAG, "onServiceDisconnected() begin");
        }
    };

    private IMessageNotifyListener mListener = new IMessageNotifyListener() {
        @Override
        public void notifyMsgChange(List<MessageNotification> list) {
        }

        @Override
        public void notifyUnReaderMsg(int num, List<MessageNotification> list) {
            if (list == null) {
                return;
            }
            Message msg = Message.obtain();
            msg.what = 2;
            msg.arg1 = num;
            msg.obj = list;
            mHandler.sendMessage(msg);
        }

        @Override
        public void notifyNewMsg(List<MessageNotification> list) {
            LogUtil.d(TAG, "notifyNewMsg() begin");
            if (list == null) {
                return;
            }

            Message msg = Message.obtain();
            msg.what = 3;
            msg.obj = list;
            mHandler.sendMessage(msg);
        }

        @Override
        public void notifyServiceDead() {
            LogUtil.d(TAG, "notifyServiceDead()");
            initConnection();
        }
    };

    private static final int REGISTER_BCM_LISTENER = 10001;
    private static final int UPDATE_WIRELESS_CHARGING_ICON = 10002;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 3:
                    LogUtil.d(TAG, "handleMessage() refresh" + ((List<MessageNotification>) msg.obj).get(0).getmContext());
                    Toast.makeText(SystemUIApplication.getSystemUIContext(), "消息内容 ： " + ((List<MessageNotification>) msg.obj).get(0).getmContext(), Toast.LENGTH_LONG);
                    break;

                case 2:
                    LogUtil.d(TAG, "handleMessage() refresh");
                    int unreadNumber = msg.arg1;
                    updateMessageView(unreadNumber);
                    break;
                case REGISTER_BCM_LISTENER:
                    LogUtil.d(TAG, "REGISTER_BCM_LISTENER registerBCMListener");
                    registerBCMListener();
                    break;
                case UPDATE_WIRELESS_CHARGING_ICON:
                    LogUtil.d(TAG, "UPDATE_WIRELESS_CHARGING_ICON updateWirelessChargingIcon");
                    updateWirelessChargingIcon(bcmManager.getWirelessChargingSwitch(), bcmManager.getWirelessChargingCharging());
                    break;
                default:
                    break;
            }
        }
    };

    public SystemIconsView(Context context) {
        super(context);
    }

    public SystemIconsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SystemIconsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initController() {
        mBluetoothControllerImpl = BluetoothControllerImpl.getInstance();
        mBluetoothControllerImpl.addCallback((BaseCallback<BluetoothInfo>) data -> mHandlerThread.post(() -> {
            updateBtView(data);
            updateBtBatteryView(data);
        }));
        mUSBControllerImpl = USBControllerImpl.getInstance();
        mUSBControllerImpl.addCallback((BaseCallback<USBInfo>) data -> mHandlerThread.post(() -> updateUSBView(data)));
        mWiFiControllerImpl = WiFiControllerImpl.getInstance();
        mWiFiControllerImpl.addCallback((BaseCallback<WiFiInfo>) data -> mHandlerThread.post(() -> updateWiFiView(data)));
        mHotspotControllerImpl = HotspotControllerImpl.getInstance();
        mHotspotControllerImpl.addCallback((BaseCallback<HotspotInfo>) data -> mHandlerThread.post(() -> updateHotspotView(data)));
        volumeController = VolumeControllerImpl.getInstance();
        volumeController.addCallback((BaseCallback<VolumeInfo>) data -> mHandlerThread.post(() -> updateMuteView(data)));
        sourceController = SourceControllerImpl.getInstance();
        sourceController.addCallback((BaseCallback<SystemUISourceInfo>) data -> {
            //todo 暂时无用
        });
        dvrController = DvrControllerImpl.getInstance();
        dvrController.addCallback((BaseCallback<DVRInfo>) data -> mHandlerThread.post(() -> updateDvrView(data)));
        upgradeController = UpgradeControllerImpl.getInstance();
        upgradeController.addCallback((BaseCallback<UpgradeInfo>) data -> mHandlerThread.post(() -> updateUpgradeView(data)));
        tboxController = TboxControllerImpl.getInstance();
        tboxController.addCallback((BaseCallback<TboxInfo>) data -> mHandlerThread.post(() -> updateTboxView(data)));
        naviController = NaviControllerImpl.getInstance();
        naviController.addCallback((BaseCallback<NaviInfo>) data -> mHandlerThread.post(() -> updateNaviView(data)));
        if(ConfigUtils.getKey(WIRELESS_CHARGING) == 1) {
            systemServiceManager = SystemStatusManager.getInstance();
            systemServiceManager.addCallback(aaop_serviceInfoEntry -> {
                mHandlerThread.post(() -> {
                    if (null == aaop_serviceInfoEntry) {
                        return;
                    }
                    ArrayList<AAOP_RelyInfoEntry> relyInfoEntryList = aaop_serviceInfoEntry.getRelyList();
                    for (AAOP_RelyInfoEntry entry : relyInfoEntryList) {
                        LogUtil.debugD(TAG, "entry.getServiceId() = " + entry.getServiceId() +
                                " ; hasRegisterBCM = " + hasRegisterBCM);
                        if (entry.getServiceId() == AAOP_SystemServiceContantsDef.AAOP_SERVICE_ID.AAOP_SERVICE_BCM && !hasRegisterBCM) {
                            mHandler.sendEmptyMessage(REGISTER_BCM_LISTENER);
                        }
                    }
                });
            });
        }
        mHandlerThread.post(() -> {
            bandVehicleID();
        });
        initConnection();
    }

    private void registerBCMListener() {
        bcmManager = BcmManager.getInstance();
        bcmDeviceList.add(DEVICE_INFO);
        boolean registerStatus = bcmManager.registerListener(new IClientCallBack.Stub() {
            @Override
            public boolean onChangeListener(int i, Bundle bundle) throws RemoteException {
                LogUtil.debugD(TAG, "289409616 = " + i + " ; 289409619 = " + i);
                switch (i) {
                    case VehiclePropertyIds.VEHICLE_CAN_TO_IVI_WPCSS:
                    case VehiclePropertyIds.VEHICLE_CAN_TO_IVI_SOCOTW:
                        mHandler.sendEmptyMessage(UPDATE_WIRELESS_CHARGING_ICON);
                        break;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public boolean onErrorEvent(int i, int i1) throws RemoteException {
                return false;
            }

            @Override
            public boolean isConnectCarProperty(boolean b) throws RemoteException {
                return false;
            }
        }, getContext().getPackageName(), bcmDeviceList);
        LogUtil.debugD(TAG, "registerStatus = " + registerStatus);
        hasRegisterBCM = true;
        mHandler.sendEmptyMessage(UPDATE_WIRELESS_CHARGING_ICON);
    }

    protected void bandVehicleID() {
        hvacManager = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext());
        hvacManager.addHvacServiceConnectCallback(this);
    }

    private void initConnection() {
        Intent intent1 = new Intent();
        intent1.setPackage("com.adayo.service.msg.nofity");
        intent1.setAction("com.adayo.service.msg.nofity.control.MessageServic");
        SystemUIApplication.getSystemUIContext().bindService(intent1, connection, BIND_AUTO_CREATE);
    }

    private void updateAirView(HvacInfo data) {
        if (null == data) {
            return;
        }
    }

    private void updateBtView(BluetoothInfo mBluetoothInfo) {
        if (null == mBluetoothInfo) {
            return;
        }
        if (!mBluetoothInfo.isEnable()) {
            if (null != btIcon && btIcon.getAddTag()) {
                removeView(btIcon);
                btIcon.setAddTag(false);
            }
        } else {
            if (null == btIcon) {
                btIcon = new IconView(getContext());
            }
            if (mBluetoothInfo.isMount()) {
                btIcon.setBackgroundIcon(R.drawable.com_icon_bluetooth_on);
            } else {
                btIcon.setBackgroundIcon(R.drawable.com_icon_bluetooth_off);
            }
            if (!btIcon.getAddTag()) {
                addView(btIcon, 0);
                btIcon.setAddTag(true);
            }
        }
    }

    private void updateBtBatteryView(BluetoothInfo mBluetoothInfo) {
        if (null == mBluetoothInfo) {
            return;
        }
        if (!mBluetoothInfo.isEnable() || !mBluetoothInfo.isMount()) {
            if (null != electricQuantityIcon && electricQuantityIcon.getAddTag()) {
                removeView(electricQuantityIcon);
                electricQuantityIcon.setAddTag(false);
            }
        } else {
            if (null == electricQuantityIcon) {
                electricQuantityIcon = new IconView(getContext());
            }

            switch (mBluetoothInfo.getElectricQuantity()){
                case 0:
                    electricQuantityIcon.setBackgroundIcon(R.drawable.com_icon_electricity_white_0);
                    break;
                case 1:
                    electricQuantityIcon.setBackgroundIcon(R.drawable.com_icon_electricity_white_1);
                    break;
                case 2:
                    electricQuantityIcon.setBackgroundIcon(R.drawable.com_icon_electricity_white_2);
                    break;
                case 3:
                    electricQuantityIcon.setBackgroundIcon(R.drawable.com_icon_electricity_white_3);
                    break;
                case 4:
                    electricQuantityIcon.setBackgroundIcon(R.drawable.com_icon_electricity_white_4);
                    break;
                case 5:
                    electricQuantityIcon.setBackgroundIcon(R.drawable.com_icon_electricity_white_5);
                    break;
                case -1:
                    if (null != electricQuantityIcon && electricQuantityIcon.getAddTag()) {
                        removeView(electricQuantityIcon);
                        electricQuantityIcon.setAddTag(false);
                    }
                    return;
            }

            if (!electricQuantityIcon.getAddTag()) {
                addView(electricQuantityIcon, 0);
                electricQuantityIcon.setAddTag(true);

            }
        }
    }

    private void updateUSBView(USBInfo usbInfo) {
        if (null == usbInfo) {
            return;
        }
        if (SystemUIContent.SYSTEM_STATUS_SCREEN == SystemStatusManager.getInstance().getSystemStatus()) {
            SystemStatusManager.getInstance().setScreenOn();
        }
        if (usbInfo.getDeviceNum() == 0) {
            if (null != usbIcon && usbIcon.getAddTag()) {
                removeView(usbIcon);
                usbIcon.setAddTag(false);
            }
        } else {
            if (null == usbIcon) {
                usbIcon = new IconView(getContext());
                usbIcon.setBackgroundIcon(R.drawable.com_icon_usb);
            }
            if (!usbIcon.getAddTag()) {
                addView(usbIcon, 0);
                usbIcon.setAddTag(true);
            }
        }
    }

    private void updateWiFiView(WiFiInfo wiFiInfo) {
        if (null != wiFiInfo) {
            LogUtil.debugD(TAG, "isWiFiEnable = " + wiFiInfo.isEnable()
                    + " ; isWiFiConnected = " + wiFiInfo.isConnected()
                    + " ; rssi = " + wiFiInfo.getRssi());
            if (null != tboxIcon) {
                tboxIcon.setForegroundIconVisibility(wiFiInfo.isConnected() ? View.GONE : View.VISIBLE);
            }
            if (!wiFiInfo.isEnable()) {
                if (null != wifiIcon && wifiIcon.getAddTag()) {
                    removeView(wifiIcon);
                    wifiIcon.setAddTag(false);
                }
            } else {
                if (null == wifiIcon) {
                    wifiIcon = new IconView(getContext());
                }
                int res = R.drawable.com_icon_wifi0;
                if (wiFiInfo.isConnected()) {
                    switch (wiFiInfo.getRssi()) {
                        case 0:
                            res = R.drawable.com_icon_wifi0;
                            break;
                        case 1:
                            res = R.drawable.com_icon_wifi1;
                            break;
                        case 2:
                            res = R.drawable.com_icon_wifi2;
                            break;
                        case 3:
                            res = R.drawable.com_icon_wifi3;
                            break;
                        case 4:
                            res = R.drawable.com_icon_wifi4;
                            break;
                        default:
                            break;
                    }
                }
                wifiIcon.setBackgroundIcon(res);
                if (!wifiIcon.getAddTag()) {
                    addView(wifiIcon, 0);
                    wifiIcon.setAddTag(true);
                }
            }
        }
    }

    private void updateHotspotView(HotspotInfo hotspotInfo) {
        if (null == hotspotInfo || hotspotInfo.getmNumConnectedDevices() < 0 || hotspotInfo.getmNumConnectedDevices() > 5) {
            return;
        }
        LogUtil.debugD(TAG, "getmHotspotState = " + hotspotInfo.getmHotspotState()
                + " ; getmNumConnectedDevices = " + hotspotInfo.getmNumConnectedDevices());
        if (!(hotspotInfo.getmHotspotState() == SystemUIContent.WIFI_AP_STATE_ENABLED)) {
            if (null != hotspotIcon && hotspotIcon.getAddTag()) {
                removeView(hotspotIcon);
                hotspotIcon.setAddTag(false);
            }
        } else {
            if (null == hotspotIcon) {
                hotspotIcon = new IconView(getContext());
            }
            int res = R.drawable.com_icon_hotspot_off;
            switch (hotspotInfo.getmNumConnectedDevices()) {
                case 0:
                    res = R.drawable.com_icon_hotspot_off;
                    break;
                case 1:
                    res = R.drawable.com_icon_hotspot_1;
                    break;
                case 2:
                    res = R.drawable.com_icon_hotspot_2;
                    break;
                case 3:
                    res = R.drawable.com_icon_hotspot_3;
                    break;
                case 4:
                    res = R.drawable.com_icon_hotspot_4;
                    break;
                case 5:
                    res = R.drawable.com_icon_hotspot_5;
                    break;
                default:
                    break;
            }
            hotspotIcon.setBackgroundIcon(res);
            if (!hotspotIcon.getAddTag()) {
                addView(hotspotIcon, 0);
                hotspotIcon.setAddTag(true);
            }
        }
    }

    private void updateMuteView(VolumeInfo volumeInfo) {
        if (null != volumeInfo) {
            boolean isMute = volumeInfo.isMute_switch();
            LogUtil.debugD(TAG, "VolumeInfo : getMuteState = " + isMute);
            if (!isMute) {
                if (null != muteIcon && muteIcon.getAddTag()) {
                    removeView(muteIcon);
                    muteIcon.setAddTag(false);
                }
            } else {
                if (null == muteIcon) {
                    muteIcon = new IconView(getContext());
                    muteIcon.setBackgroundIcon(R.drawable.com_icon_volume_mute);
                }
                if (!muteIcon.getAddTag()) {
                    addView(muteIcon, 0);
                    muteIcon.setAddTag(true);
                }
            }
        }
    }

    private void updateNaviView(NaviInfo naviInfo) {
        if (null == naviInfo) {
            return;
        }
        if (naviInfo.getNaviStatus() == SystemUIContent.NAVI_EXIT) {
            if (null != naviIcon && naviIcon.getAddTag()) {
                removeView(naviIcon);
                naviIcon.setAddTag(false);
            }
        } else if (SystemUIContent.NAVI_START == naviInfo.getNaviStatus()) {
            if (null == naviIcon) {
                naviIcon = new IconView(getContext());
                naviIcon.setBackgroundIcon(R.drawable.com_icon_navigation);
            }
            if (!naviIcon.getAddTag()) {
                addView(naviIcon, 0);
                naviIcon.setAddTag(true);
            }
        }
    }

    private void updateDvrView(DVRInfo dvrInfo) {
        if (null == dvrInfo) {
            return;
        }
        int dvrState = dvrInfo.getDvrState();
        LogUtil.debugI(TAG, "dvr_state = " + dvrState);
        if (dvrState == 0) {
            if (null != dvrIcon && dvrIcon.getAddTag()) {
                removeView(dvrIcon);
                dvrIcon.setAddTag(false);
            }
        } else if (dvrState == 1) {
            if (null == dvrIcon) {
                dvrIcon = new IconView(getContext());
                dvrIcon.setBackgroundIcon(R.drawable.com_icon_logger);
            }
            if (!dvrIcon.getAddTag()) {
                addView(dvrIcon, 0);
                dvrIcon.setAddTag(true);
            }
        }
    }

    private void updateUpgradeView(UpgradeInfo data) {
        if (null == data) {
            return;
        }
        int upgradeState = data.getUpgradeState();
        LogUtil.debugI(TAG, "upgrade_state = " + upgradeState);
        if (upgradeState == 0) {
            if (null != upgradeIcon && upgradeIcon.getAddTag()) {
                removeView(upgradeIcon);
                upgradeIcon.setAddTag(false);
            }
        } else {
            if (null == upgradeIcon) {
                upgradeIcon = new IconView(getContext());
                upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_during_o);
            }
            switch (upgradeState) {
                case 1:
                    upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_during_o);
                    break;
                case 2:
                    upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_inspection);
                    break;
                case 3:
                    upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_downloading);
                    break;
                case 4:
                    upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_during_during_upgrade);
                    break;
                case 5:
                    upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_during_upgrade);
                    break;
                case 6:
                    upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_during_abnormal);
                    break;
                case 7:
                    upgradeIcon.setBackgroundIcon(R.drawable.com_icon_ota_upgrade);
                    break;
                default:
                    break;
            }
            if (!upgradeIcon.getAddTag()) {
                addView(upgradeIcon, 0);
                upgradeIcon.setAddTag(true);
            }
        }
    }

    private void updateTboxView(TboxInfo tboxInfo) {
        if (null != tboxInfo) {
            if (0 == tboxInfo.getType()) {
                if (null != tboxIcon && tboxIcon.getAddTag()) {
                    removeView(tboxIcon);
                    tboxIcon.setAddTag(false);
                }
            } else {
                if (null == tboxIcon) {
                    tboxIcon = new IconView(getContext());
                }
                if (null == mWiFiControllerImpl) {
                    mWiFiControllerImpl = WiFiControllerImpl.getInstance();
                }
                switch (tboxInfo.getType()) {
                    case 2:
                        tboxIcon.setForegroundIcon(R.drawable.com_icon_2g, mWiFiControllerImpl.isWiFiConnected() ? View.GONE : View.VISIBLE);
                        break;
                    case 3:
                        tboxIcon.setForegroundIcon(R.drawable.com_icon_3g, mWiFiControllerImpl.isWiFiConnected() ? View.GONE : View.VISIBLE);
                        break;
                    case 4:
                        tboxIcon.setForegroundIcon(R.drawable.com_icon_4g, mWiFiControllerImpl.isWiFiConnected() ? View.GONE : View.VISIBLE);
                        break;
                    case 5:
                        tboxIcon.setForegroundIcon(R.drawable.com_icon_5g, mWiFiControllerImpl.isWiFiConnected() ? View.GONE : View.VISIBLE);
                        break;
                    default:
                        break;
                }
                switch (tboxInfo.getSignal()) {
                    case 0:
                        tboxIcon.setBackgroundIcon(R.drawable.com_icon_signal0);
                        break;
                    case 1:
                        tboxIcon.setBackgroundIcon(R.drawable.com_icon_signal1);
                        break;
                    case 2:
                        tboxIcon.setBackgroundIcon(R.drawable.com_icon_signal2);
                        break;
                    case 3:
                        tboxIcon.setBackgroundIcon(R.drawable.com_icon_signal3);
                        break;
                    case 4:
                        tboxIcon.setBackgroundIcon(R.drawable.com_icon_signal4);
                        break;
                    case 5:
                        tboxIcon.setBackgroundIcon(R.drawable.com_icon_signal5);
                        break;
                    default:
                        break;
                }
                if (!tboxIcon.getAddTag()) {
                    addView(tboxIcon, 0);
                    tboxIcon.setAddTag(true);
                }
            }
        }
    }

    private void updateMessageView(int number) {
        if (null == messageIcon) {
            messageIcon = new MessageIconView(getContext());
        }
        if (number > 0) {
            if (!messageIcon.getAddTag()) {
                addView(messageIcon, 0);
                messageIcon.setAddTag(true);
            }
            messageIcon.setForegroundText(number);
        } else {
            if (null != messageIcon && messageIcon.getAddTag()) {
                removeView(messageIcon);
                messageIcon.setAddTag(false);
            }
        }
    }

    private void updateWirelessChargingIcon(int wirelessChargingSwitch, int status) {
        LogUtil.debugD(TAG, "wirelessChargingSwitch = " + wirelessChargingSwitch +
                " ; status = " + status);
        if (wirelessChargingSwitch == 1) {
            if (null == wirelessChargingIcon) {
                wirelessChargingIcon = new IconView(getContext());
            }
            switch (status) {
                case STANDBY:
                case CHARGING_COMPLETED:
                case WPC_FAULT4:
                case WPC_FAULT5:
                    wirelessChargingIcon.setBackgroundIcon(R.drawable.com_icon_electricity_gray);
                    break;
                case CHARGING:
                    wirelessChargingIcon.setBackgroundIcon(R.drawable.com_icon_electricity_green);
                    break;
                case WPC_FAULT:
                case WPC_FAULT1:
                case WPC_FAULT2:
                case WPC_FAULT3:
                case WPC_FAULT6:
                    wirelessChargingIcon.setBackgroundIcon(R.drawable.com_icon_electricity_red);
                    break;
                default:
                    break;
            }
            if (!wirelessChargingIcon.getAddTag()) {
                addView(wirelessChargingIcon, 0);
                wirelessChargingIcon.setAddTag(true);
            }
        } else {
            if (null != wirelessChargingIcon && wirelessChargingIcon.getAddTag()) {
                removeView(wirelessChargingIcon);
                wirelessChargingIcon.setAddTag(false);
            }
        }
    }

    private void updateAnionIcon(int value) {
        if (value == 1) {
            if (null == anionIcon) {
                anionIcon = new IconView(getContext());
                anionIcon.setBackgroundIcon(R.drawable.com_icon_air_purification);
            }
            if (!anionIcon.getAddTag()) {
                addView(anionIcon, 0);
                anionIcon.setAddTag(true);
            }
        } else {
            if (null != anionIcon && anionIcon.getAddTag()) {
                removeView(anionIcon);
                anionIcon.setAddTag(false);
            }
        }
    }

    private int airQualitySwitch;
    private int airQualityValue;
    private void updateAirQualityIcon(int airQualitySwitch, int airQualityValue) {
        LogUtil.debugD(TAG, "airQualitySwitch = " + airQualitySwitch + " ; airQualityValue = " + airQualityValue);
        if (null == airQualityIcon) {
            airQualityIcon = new TextView(getContext());
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 56);
            layoutParams.leftMargin = 18;
            airQualityIcon.setLayoutParams(layoutParams);
            airQualityIcon.setTextColor(getContext().getColor(R.color.white));
            airQualityIcon.setGravity(Gravity.CENTER);
            airQualityIcon.setTextSize(28.0f);
            addView(airQualityIcon, 0);
        }
        if(airQualityValue >= 0 && airQualityValue <=1000){
            this.airQualityValue = airQualityValue;
            airQualityIcon.setText(String.format(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.air_quality_unit), airQualityValue));
        }
        if(airQualitySwitch <= AIR_QUALITY_SWITCH_ON && airQualitySwitch >= AIR_QUALITY_SWITCH_OFF){
            this.airQualitySwitch = airQualitySwitch;
            airQualityIcon.setVisibility(airQualitySwitch == AIR_QUALITY_SWITCH_ON ? View.VISIBLE : View.GONE);
        }
    }

    protected final int INIT_CONTROLLER = 10003;

    protected Handler mHandlerThread = new Handler(Looper.getMainLooper());

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initController();
//            mHandlerThread.post(() -> {
//            });
        }).start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if (null == carPropertyValue) {
            return;
        }
        mHandlerThread.post(() -> {
            switch (carPropertyValue.getPropertyId()) {
                case HVAC_ANION_SWITCH:
                    updateAnionIcon((int) carPropertyValue.getValue());
                    break;
                case HVAC_PM25_DISPLAY:
                    updateAirQualityIcon((int) carPropertyValue.getValue(), airQualityValue);
                    break;
                case HVAC_INSIDE_PM25_VALUE:
                    updateAirQualityIcon(airQualitySwitch, (int) carPropertyValue.getValue());
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        hvacManager.registerHvacBindCallback(HVAC_ANION_SWITCH, this);
        hvacManager.registerHvacBindCallback(HVAC_PM25_DISPLAY, this);
        hvacManager.registerHvacBindCallback(HVAC_INSIDE_PM25_VALUE, this);
        mHandlerThread.post(() -> {
            updateAnionIcon(hvacManager.getAnionSwitchStatus());
            updateAirQualityIcon(hvacManager.getIntProperty(HVAC_PM25_DISPLAY, AREA_SEAT_HVAC_ALL), hvacManager.getIntProperty(HVAC_INSIDE_PM25_VALUE, AREA_SEAT_HVAC_ALL));
        });
    }
}
