package com.adayo.btsetting.model;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import com.adayo.btsetting.R;
import com.adayo.btsetting.util.BtSettingLog;
import com.adayo.btsetting.view.PhoneRequestPairDialog;
import com.adayo.btsetting.view.ToastDialog;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.nforetek.bt.base.jar.NforeBtBaseJar;
import com.nforetek.bt.base.listener.BluetoothExtendListener;
import com.nforetek.bt.base.listener.BluetoothSettingChangeListener;
import com.nforetek.bt.res.NfDef;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Y4134
 */
public class BtSettingManager implements IBtSettingFunction {

    private static volatile BtSettingManager mModel;
    private static final String TAG = BtSettingManager.class.getSimpleName();
    private Set<String> addressSet = new HashSet<>();
    private Context mContext;
    private ToastDialog mPairFailDialog;
    private PhoneRequestPairDialog mConnectDialog;
    private volatile String deviceAddress;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile boolean isHutClick = false;
    private static final String BT_NAME = "bt_name";
    /**
     *      *               0 请求配对
     *      *               1 取消配对
     *      *               2 拒绝配对
     *      *               3 删除配对
     *      *               4 确定配对
     */
    private static final int REQUEST_PAIR = 0;
    private static final int CANCEL_PAIR = 1;
    private static final int REJECT_PAIR = 2;
    private static final int DELETE_PAIR = 3;
    private static final int CONFIRM_PAIR = 4;

    private BtSettingManager(Context context) {
        // TODO: 2020/2/29 注册carservice回调
        mContext = context;
    }

    public static BtSettingManager getInstance(Context context) {
        if (mModel == null) {
            synchronized (BtSettingManager.class) {
                if (mModel == null) {
                    mModel = new BtSettingManager(context);
                }
            }
        }
        return mModel;
    }

    /**
     * @param action pairAction
     *               0 请求配对
     *               1 取消配对
     *               2 拒绝配对
     *               3 删除配对
     *               4 确定配对
     *               {deviceAddress;pairAction}
     */
    @Override
    public void setBluetothPairAction(String address, int action) {
        try {
            BtSettingLog.logD(TAG, "address = " + address + " action = " + action);
            if (action == REQUEST_PAIR) {
                NforeBtBaseJar.setPairingConfirmation();
            } else if (action == CANCEL_PAIR) {
                NforeBtBaseJar.cancelPairingUserInput();
            } else if (action == REJECT_PAIR) {
                NforeBtBaseJar.cancelPairingUserInput();
            } else if (action == DELETE_PAIR) {
                NforeBtBaseJar.reqBtUnpair(address);
            } else if (action == CONFIRM_PAIR) {
                NforeBtBaseJar.reqBtPair(address);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reqPair(String address) {
        try {
            isHutClick = true;
            BtSettingLog.logD(TAG, "isHutClick = " + isHutClick);
            NforeBtBaseJar.reqBtPair(address);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        if (isBtEnable()) {
            setBtName();
        }
        initView();
        registerBtCallBack();
    }


    private void showDialog(String deviceName) {
        StringBuilder sb = new StringBuilder();
        sb.append(mContext.getResources().getString(R.string.conn_bt_pair_compare))
                .append(deviceName)
                .append(mContext.getResources().getString(R.string.conn_bt_pair));
        if (mConnectDialog != null) {
            mConnectDialog.setBtnTxt(mContext.getResources().getString(R.string.conn_bt_pair1)
                    , mContext.getResources().getString(R.string.conn_bt_refuse));
            mConnectDialog.setTitle(mContext.getResources().getString(R.string.conn_bt_pair_request));
            mConnectDialog.setMsg(sb.toString());
            mConnectDialog.setConnectIcon();
        }
        if (mConnectDialog != null) {
            mConnectDialog.show();
        }
    }

    private void dismissDialog() {
        if (mConnectDialog != null && mConnectDialog.isShowing()) {
            mConnectDialog.dismiss();
        }
    }


    private void registerBtCallBack() {
        NforeBtBaseJar.registerBluetoothSettingChangeListener(new BluetoothSettingChangeListener() {
            //蓝牙开关状态改变
            @Override
            public void onAdapterStateChanged(final int prevState, final int newState) {

            }

            /** 蓝牙开关状态回调 **/
            @Override
            public void onEnableChanged(final boolean isEnable) {
                if (isEnable) {
                    setBtName();
                }
            }

            //蓝牙连接状态回调，只要有任意一个协议（hfp,a2dp,avrcp）连接，则返回true
            @Override
            public void onConnectedChanged(final String address, final int newState) {

            }

            /** 蓝牙hfp连接状态回调 **/
            @Override
            public void onHfpStateChanged(final String address, final int connectState) {

            }

            /** 蓝牙 HFP 连接远程设备的音频状态变化 **/
            @Override
            public void onHfpAudioStateChanged(final String address, final int prevState, final int newState) {

            }

            /** 蓝牙avrcp连接状态回调 **/
            @Override
            public void onAvrcpStateChanged(final String address, final int connectState) {

            }

            @Override
            public void onA2dpStateChanged(final String address, final int connectState) {

            }

            @Override
            public void onAdapterDiscoveryStarted() {

            }

            @Override
            public void onAdapterDiscoveryFinished() {

            }

            /** 历史配对设备回调 **/
            @Override
            public void retPairedDevices(final int elements, final String[] address, final String[] name, final int[] supportProfile) {

            }

            /** 扫描到新设备回调 **/
            @Override
            public void onDeviceFound(final String address, final String name) {

            }

            /** 配对状态回调 **/
            @Override
            public void onDeviceBondStateChanged(final String address, final String name, final int newState) {

            }

            /** 车机蓝牙名称变化回调 **/
            @Override
            public void onLocalAdapterNameChanged(final String name) {

            }

            /** 车机蓝牙配对状态变化回调 **/
            @Override
            public void onPairStateChanged(final String name, final String address, final int type, final int pairingValue) {
                BtSettingLog.logD(TAG, "============== isHutClick = " + isHutClick);
                deviceAddress = address;
                mHandler.post(() -> {
                    if (isHutClick) {
                        ShareDataManager.getShareDataManager().sendShareData(714, String.valueOf(pairingValue));
                        setBluetothPairAction(deviceAddress, 0);
                        isHutClick = false;
                    } else {
                        showDialog(name);
                    }

                });
            }

            @Override
            public void onMainDevicesChanged(final String address, final String name) {

            }
        });
        NforeBtBaseJar.setExtendListener(new BluetoothExtendListener() {
            @Override
            public void onBoundStateChanged(String address, String name, int preState, int newState) {
                switch (newState) {
                    case NfDef.BOND_NONE://配对失败
                        if (preState == NfDef.BOND_BONDING) {
                            mHandler.post(() -> {
                                dismissDialog();//手机主动和车机连线，并且失败的时候
                                showPairFailToast();
                            });
                            ShareDataManager.getShareDataManager().sendShareData(715, "false");
                        }
                        break;
                    case NfDef.BOND_BONDING://配对中
                        break;
                    case NfDef.BOND_BONDED://配对完
                        ShareDataManager.getShareDataManager().sendShareData(715, "true");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initView() {
        mConnectDialog = PhoneRequestPairDialog.getInstance(mContext);
        mConnectDialog.setBtnTxt(mContext.getResources().getString(R.string.conn_bt_pair1)
                , mContext.getResources().getString(R.string.conn_bt_refuse));
        mConnectDialog.setOnPosListener(this::onPair);
        mConnectDialog.setOnNegListener(this::onCancel);
        mConnectDialog.setTitle(mContext.getResources().getString(R.string.conn_bt_pair_request));
        mPairFailDialog = ToastDialog.getInstance(mContext);
    }

    private void onPair() {
        setBluetothPairAction(deviceAddress, 0);
    }

    private void onCancel() {
        setBluetothPairAction(deviceAddress, 1);
    }

    private void showPairFailToast() {
        if (mPairFailDialog != null) {
            mPairFailDialog.setText(mContext.getResources().getString(R.string.conn_bt_toast_hint_bt_pair_fail));
            mPairFailDialog.showToast();
        }
    }


    private boolean isBtEnable() {
        boolean isEnable = false;
        try {
            isEnable = NforeBtBaseJar.isBtEnabled();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return isEnable;
    }


    private void setBtName() {
        String btName = Settings.Global.getString(mContext.getContentResolver(), BT_NAME);
        if (TextUtils.isEmpty(btName)) {
            Log.d(TAG, "setBtName: sssssssssss");
        } else {
            try {
                boolean isSuccess = NforeBtBaseJar.setBtLocalName(btName);
                Log.d(TAG, "setBtName: isSuccess = "+isSuccess);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "setBtName: aaaaaaaaaaaaa");
        }
    }

}
