package com.adayo.app.systemui.managers.business;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Message;
import android.text.TextUtils;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.BluetoothInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothControllerImpl extends BaseControllerImpl<BluetoothInfo> implements BluetoothController, IShareDataListener {
    private volatile static BluetoothControllerImpl mBluetoothControllerImpl;
    private BluetoothAdapter blueAdapter;
    private ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
    private BluetoothInfo bluetoothInfo;

    private BluetoothControllerImpl() {
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
        blueAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothControllerImpl getInstance() {
        if (mBluetoothControllerImpl == null) {
            synchronized (BluetoothControllerImpl.class) {
                if (mBluetoothControllerImpl == null) {
                    mBluetoothControllerImpl = new BluetoothControllerImpl();
                }
            }
        }
        return mBluetoothControllerImpl;
    }

    private void checkBluetoothShareData(String shareData) {
        if (!TextUtils.isEmpty(shareData)) {
            if(null == bluetoothInfo){
                bluetoothInfo = new BluetoothInfo();
            }
            try {
                JSONObject object = new JSONObject(shareData);
                if(object.has("is_bt_enabled")) {
                    bluetoothInfo.setEnable(object.getBoolean("is_bt_enabled"));
                }
                if(object.has("is_a2dp_connected") || object.has("is_hfp_connected")){
                    boolean isA2dpConnected = false;
                    boolean isHfpConnected = false;
                    if(object.has("is_a2dp_connected")) {
                        isA2dpConnected = object.getBoolean("is_a2dp_connected");
                    }
                    if(object.has("is_hfp_connected")) {
                        isHfpConnected = object.getBoolean("is_hfp_connected");
                    }
                    bluetoothInfo.setMount(isA2dpConnected || isHfpConnected) ;
                }
                if(object.has("phoneName")) {
                    bluetoothInfo.setDeviceName(object.getString("phoneName"));
                }
                if(object.has("electric_quantity")) {
                    bluetoothInfo.setElectricQuantity(object.getInt("electric_quantity"));
                }
            } catch (JSONException e) {
                LogUtil.w(SystemUIContent.TAG, e.getMessage());
            }
        }
    }

    @Override
    public boolean isBTEnabled() {
        if (null != blueAdapter) {
            return blueAdapter.isEnabled();
        }
        return false;
    }

    private boolean canSetBt = true;
    @Override
    public void setBTEnabled(boolean enable) {
        LogUtil.debugD(SystemUIContent.TAG, "enable = " + enable);
        if (null != blueAdapter && canSetBt) {
            canSetBt = false;
            if (enable) {
                blueAdapter.enable();
            } else {
                blueAdapter.disable();
            }
        }
    }

    @Override
    public boolean isBTConnected() {
        if (null != blueAdapter) {
            Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
            if (bondedDevices != null && bondedDevices.size() > 0) {
                for (BluetoothDevice bondedDevice : bondedDevices) {
                    try {
                        //使用反射调用被隐藏的方法
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        return (boolean) isConnectedMethod.invoke(bondedDevice, (Object[]) null);
                    } catch (NoSuchMethodException e) {
                        LogUtil.w(SystemUIContent.TAG, e.getMessage());
                    } catch (IllegalAccessException e) {
                        LogUtil.w(SystemUIContent.TAG, e.getMessage());
                    } catch (InvocationTargetException e) {
                        LogUtil.w(SystemUIContent.TAG, e.getMessage());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getBTDevicesName() {
        if(null != blueAdapter) {
            //得到已匹配的蓝牙设备列表
            Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
            if (bondedDevices != null && bondedDevices.size() > 0) {
                for (BluetoothDevice bondedDevice : bondedDevices) {
                    try {
                        //使用反射调用被隐藏的方法
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        boolean isConnected = (boolean) isConnectedMethod.invoke(bondedDevice, (Object[]) null);
                        if (isConnected) {
                            return bondedDevice.getName();
                        }
                    } catch (NoSuchMethodException e) {
                        LogUtil.w(SystemUIContent.TAG, e.getMessage());
                    } catch (IllegalAccessException e) {
                        LogUtil.w(SystemUIContent.TAG, e.getMessage());
                    } catch (InvocationTargetException e) {
                        LogUtil.w(SystemUIContent.TAG, e.getMessage());
                    }
                }
            }
        }
        return "";
    }

    @Override
    public BluetoothInfo getBluetoothInfo() {
        return bluetoothInfo;
    }

    @Override
    public void notifyShareData(int i, String s) {
        LogUtil.d(SystemUIContent.TAG, "i = " + i + ";s = " + s);
        if(TextUtils.isEmpty(s)){
            return;
        }
        switch (i){
            case SystemUIContent.BLUETOOTH_SHARE_ID:
            case SystemUIContent.BLUETOOTH_DEVICE_SHARE_ID:
                canSetBt = true;
                checkBluetoothShareData(s);
                mHandler.removeMessages(NOTIFY_CALLBACKS);
                mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                break;
        }
    }

    @Override
    protected boolean registerListener() {
        return shareDataManager.registerShareDataListener(SystemUIContent.BLUETOOTH_SHARE_ID, this) &&
                shareDataManager.registerShareDataListener(SystemUIContent.BLUETOOTH_DEVICE_SHARE_ID, this);
    }

    @Override
    protected BluetoothInfo getDataInfo() {
        if(null == bluetoothInfo){
            checkBluetoothShareData(shareDataManager.getShareData(SystemUIContent.BLUETOOTH_SHARE_ID));
            checkBluetoothShareData(shareDataManager.getShareData(SystemUIContent.BLUETOOTH_DEVICE_SHARE_ID));
        }
        return bluetoothInfo;
    }
}
