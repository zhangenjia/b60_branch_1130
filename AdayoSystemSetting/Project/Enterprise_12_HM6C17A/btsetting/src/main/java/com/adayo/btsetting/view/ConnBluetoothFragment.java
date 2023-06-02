package com.adayo.btsetting.view;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adayo.app.base.BaseActivity;
import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;
import com.adayo.app.base.ViewUtils;
import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.bpresenter.bluetooth.contracts.IBTWndMangerContract;
import com.adayo.bpresenter.bluetooth.contracts.IPairedContract;
import com.adayo.bpresenter.bluetooth.contracts.IScanningContract;
import com.adayo.bpresenter.bluetooth.contracts.ISettingsContract;
import com.adayo.bpresenter.bluetooth.presenters.BTWndManagerPresenter;
import com.adayo.bpresenter.bluetooth.presenters.PairedPresenter;
import com.adayo.bpresenter.bluetooth.presenters.ScanningPresenter;
import com.adayo.bpresenter.bluetooth.presenters.SettingsPresenter;
import com.adayo.btsetting.R;
import com.adayo.btsetting.adapter.HisScanListAdapter;
import com.adayo.btsetting.adapter.PairedAdapter;
import com.adayo.btsetting.adapter.ScanAdapter;
import com.adayo.btsetting.constant.Contants;
import com.adayo.btsetting.model.BtFinishManager;
import com.adayo.btsetting.receiver.BtHutPairReceiver;
import com.adayo.btsetting.util.CommonUtil;
import com.adayo.btsetting.util.SetListviewItemHeigtUtil;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.common.bluetooth.bean.RingtoneInfo;
import com.adayo.common.bluetooth.constant.BtDef;
import com.adayo.component.log.Trace;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.kyleduo.switchbutton.SwitchButton;
import com.lt.library.util.context.ContextUtil;
import com.nforetek.bt.base.jar.NforeBtBaseJar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType.SourceType.UI_AUDIO;

/**
 * 蓝牙
 * @author Y4134
 */
public class ConnBluetoothFragment extends ViewStubBase implements View.OnClickListener, PairedAdapter.AcitonListener, IBTWndMangerContract.IBTWndMangerView {

    private static final String TAG = "ConnBluetoothFragment_0426";
    protected ISettingsContract.ISettingsPresenter mSettingsPresenter;
    private IPairedContract.IPairedPresenter pairedPresenter;
    private IScanningContract.IScanningPresenter scanningPresenter;

    private View mContentView;
    private LinearLayout mLyBluetoothList;
    private SwitchButton mTgBtnBtSwitch;
    private ImageView mIbCanPairRefresh;
    private LinearLayout mLlBtNotConnected;
    private LinearLayout mLlHasPair;
    private ImageView mIvRefreshNormal;
    private RelativeLayout mRlBluetoothSwitch;
    private FrameLayout mFl;
    public HisScanListAdapter hisScanListAdapter;

    private ListView mlvHasList;
    private ListView mlvCanPair;
    private TextView mTvHasPair;
    private ImageView mIvHasPairTop;
    private ScanAdapter scanAdapter;
    private BTWndManagerPresenter mManagerPresenter;
    private List<BluetoothDevice> blueList = new ArrayList();
    private List<BluetoothDevice> mHasBlueList = new ArrayList();
    private DisConnectDialog mDisConnectDialog;
    private DisConnectAndConnectOtherDialog mDisConnectAndConnectOtherDialog;
    private DeleteDialog mDeleteDialog;
    private PairDialog mPairDialog;
    private ToastDialog mToastDialog;
    private volatile boolean isCanSetBt = true;
    private NestedScrollView mNsvBt;
    /**
     * 刷新的帧动画
     */
    private AnimationDrawable mAnimationDrawable;
    private int mBtState;
    private Toast mConnectFailToast;
    private Handler mHandler = new Handler();
    private Context mContext;
    private static final String BT_CONNECT = "com.adayo.bt.connect";
    private CommonHighlight mCommonHighlight;
    private static final int LIMIT_TIME = 300;
    private long mLastClickTime = 0;
    private ImageView mIvBtIcon;
    private static final int SHARE_DATA_PAIRING = 714;
    private static final int SHARE_DATA_PAIR_SUCCESS = 715;

    private Activity mActivity;

    private IShareDataListener mShareDataListener = new IShareDataListener() {
        @Override
        public void notifyShareData(int dataId, String dataValue) {
            if (dataId == SHARE_DATA_PAIRING) {
                if (!TextUtils.isEmpty(dataValue)) {
                    mHandler.post(() -> {
                        mPairDialog.setText1(mContext.getResources().getString(R.string.conn_bt_toast_hint_bt_pairing));
                        StringBuilder sb = new StringBuilder();
                        sb.append(mContext.getResources().getString(R.string.conn_bt_toast_hint_bt_pair_code));
                        sb.append(dataValue);
                        mPairDialog.setText(sb.toString());//与手机连接过程中
                        mPairDialog.show();
                    });
                }
            } else if (dataId == SHARE_DATA_PAIR_SUCCESS) {
                if (!TextUtils.isEmpty(dataValue)) {
                    if (ViewBinding==null) {
                        Log.d(TAG, "initShareData2: fragment not attach Activity");
                        return;
                    }
                    mHandler.post(() -> mPairDialog.dismiss());
                }
            }
        }
    };

    @Override
    public void setBaseActivity(BaseActivity baseActivity) {
        super.setBaseActivity(baseActivity);
    }

    @Override
    public void initViewModel() {
        super.initViewModel();
        mActivity=mBaseActivity;
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);
    }

    @Override
    public void setView(View view) {
        super.setView(view);
        mContentView=view;
        LogUtil.d(TAG,"mContentView ="+mContentView);
        mFl=(FrameLayout) mContentView;
    }

    @Override
    public void initEvent() {
        super.initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void showOrHide(boolean isShow) {
        super.showOrHide(isShow);
        if (isShow) {
            if (null != scanningPresenter) {
                scanningPresenter.setView(mScanView);
                scanningPresenter.init();
            }
            if (null != mSettingsPresenter) {
                //如果蓝牙服务连接成功了，启动搜索。否则再服务的onServiceConnected回调中启动搜索
                if (mSettingsPresenter.isServiceConnected()) {
                    Trace.i(TAG, "--------scanningPresenter.startScan()----");
                    scanningPresenter.startScan();
                }
            }

            if (null != pairedPresenter) {
                pairedPresenter.setView(mPairView);
                pairedPresenter.init();
            }
        } else {
            scanningPresenter.cancelScan();
            scanningPresenter.removeView(mScanView);
            pairedPresenter.removeView(mPairView);
            dismissDialog();

        }

    }

    @Override
    public void initData() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BT_CONNECT);
        mSettingsPresenter = new SettingsPresenter(mContext);
        mSettingsPresenter.setView(mSettingsView);
        mSettingsView.setPresenter(mSettingsPresenter);
        mSettingsPresenter.init();

        scanningPresenter = new ScanningPresenter(mContext);
        scanningPresenter.setDismissDelayTime(500);
        scanningPresenter.setView(mScanView);
        mScanView.setPresenter(scanningPresenter);
        scanningPresenter.init();

        pairedPresenter = new PairedPresenter(mContext);
        pairedPresenter.setView(mPairView);
        mPairView.setPresenter(pairedPresenter);
        pairedPresenter.init();

        if (null == mManagerPresenter) {
            //把当前的act需要赋值给总的P层
            mManagerPresenter = new BTWndManagerPresenter(mContext);
        }
        mManagerPresenter.setView(this);
        initShareData();
    }

    @Override
    public void initView() {
        mContext = ContextUtil.getInstance().getApplicationContext();

        mDisConnectDialog = DisConnectDialog.getInstance(mContext);
        mDisConnectAndConnectOtherDialog = DisConnectAndConnectOtherDialog.getInstance(mContext);
        mDeleteDialog = DeleteDialog.getInstance(mContext);
        mPairDialog = PairDialog.getInstance(mContext);
        mToastDialog = ToastDialog.getInstance(mContext);
        mIvBtIcon = mContentView.findViewById(R.id.iv_bluetooth_icon);
        mRlBluetoothSwitch = mContentView.findViewById(R.id.rl_bluetooth_switch);
        mTgBtnBtSwitch = mContentView.findViewById(R.id.tgBtn_bluetooth_switch);
        mLyBluetoothList = mContentView.findViewById(R.id.ly_bluetooth_list);
        mLlBtNotConnected = mContentView.findViewById(R.id.ll_bt_not_connected);
        mLlHasPair = mContentView.findViewById(R.id.ll_his_pair);
        mlvHasList = mContentView.findViewById(R.id.lv_hispair);
        mlvCanPair = mContentView.findViewById(R.id.lv_can_pair);
        mNsvBt = mContentView.findViewById(R.id.nsv_bt);
        mIvRefreshNormal = mContentView.findViewById(R.id.iv_refresh_normal);
        mIbCanPairRefresh = mContentView.findViewById(R.id.ib_can_pair_refresh);

        mTvHasPair = mContentView.findViewById(R.id.tv_his_pair);
        mIvHasPairTop = mContentView.findViewById(R.id.iv_his_pair_top);
        mLlHasPair.setVisibility(View.GONE);

        if(!CommonUtil.isHighConfig()){
           ViewUtils.setHeight(mNsvBt,814);
       }
        mAnimationDrawable = (AnimationDrawable) mIbCanPairRefresh.getDrawable();
        mIvRefreshNormal.setOnClickListener(view -> {
            setBackLight();
            if (null != scanningPresenter) {
                try {
                    if (NforeBtBaseJar.isBtDiscovering()) {
                        Log.d(TAG, "initView: isBtDiscovering");
                        NforeBtBaseJar.cancelBtDiscovery();
                    }
                    Log.i(TAG, "-------");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                scanningPresenter.startScan();
            }
        });
        mTgBtnBtSwitch.setEnabled(false);
        mTgBtnBtSwitch.setChecked(false);
        mRlBluetoothSwitch.setOnClickListener(v -> {
            boolean isChecked = mTgBtnBtSwitch.isChecked();
            if (isCanSetBt) {
                isCanSetBt = false;
                mSettingsPresenter.setBluetoothEnable(!isChecked);
            }
            setBackLight();
        });
        if (null == hisScanListAdapter) {
            Trace.i(TAG, "hisScanListAdapter() Start");
            this.hisScanListAdapter = new HisScanListAdapter(mContext, new ArrayList<BluetoothDevice>());
            mlvHasList.setAdapter(hisScanListAdapter);
            this.hisScanListAdapter.setActionListener(this);
        }
        if (null == scanAdapter) {
            scanAdapter = new ScanAdapter(mContext, new ArrayList<BluetoothDevice>());
            mlvCanPair.setAdapter(scanAdapter);
        }
        mlvHasList.setOnItemClickListener((adapterView, view, i, l) -> {
            setBackLight();
            Log.i(TAG, "setOnItemClickListener");
            if (mHasBlueList != null) {
                if (mSettingsPresenter.isHfpConnected()) {
                    if (i == 0) {
                        Log.i(TAG, "disconnectDevice");
                        mDisConnectDialog.setOnPosListener(() -> {
                            pairedPresenter.disconnectDevice(mHasBlueList.get(i).getAddress());
                        });
                        mDisConnectDialog.show();
                    } else {
                        Log.i(TAG, "disconnectAndConnectOtherDevice");
                        //蓝牙已连接，选择未连接设备
                        //显示断开当前连接，连接新设备对话框
                        mDisConnectAndConnectOtherDialog.setOnPosListener(() -> {
                            pairedPresenter.disconnectAndConnectOtherDevice(mHasBlueList.get(i).getAddress());
                        });
                        mDisConnectAndConnectOtherDialog.show();
                    }
                } else {
                    //TODO:临时策
                    boolean isConnecting = mHasBlueList.get(i).getState() == BtDef.STATE_CONNECTING;
                    Log.d(TAG, "initView: connectDevice isConnecting " + isConnecting);
                    if (!isConnecting) {
                        hisScanListAdapter.updateConnectState(BusinessConstants.ConnectionStatus.CONNECTING, mHasBlueList.get(i).getAddress());
                        // 蓝牙未连接时，选择任意设备
                        pairedPresenter.connectDevice(mHasBlueList.get(i).getAddress());
                    }
                }
            }
        });
        mlvCanPair.setOnItemClickListener((adapterView, view, i, l) -> {
            setBackLight();
            String address = blueList.get(i).getAddress();
            Log.i(TAG, "----onConnectClick() address : " + address);
//                scanAdapter.updateConnectState(BusinessConstants.ConnectionStatus.CONNECTING, blueList.get(i).getAddress());
            if (blueList != null) {
                if (mSettingsPresenter.isHfpConnected()) {
                    Log.i(TAG, "disconnectAndConnectOtherDevice");
                    //蓝牙已连接，选择未连接设备
                    //显示断开当前连接，连接新设备对话框
                    mDisConnectAndConnectOtherDialog.setOnPosListener(() -> {
                        try {
                            if (!mManagerPresenter.isHfpConnected()) {
                                NforeBtBaseJar.reqBtDisconnectAll(NforeBtBaseJar.getA2dpConnectedAddress());
                            } else {
                                NforeBtBaseJar.reqBtDisconnectAll(NforeBtBaseJar.getHfpConnectedAddress());
                            }
                            mHandler.postDelayed(() -> {
                                Intent intent = new Intent();
                                intent.setAction(BtHutPairReceiver.BT_HUT_PAIR);
                                intent.putExtra("address", address);
                                mContext.sendBroadcast(intent);
                            }, 2000);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                    mDisConnectAndConnectOtherDialog.show();
                } else {
                    Log.i(TAG, "connectDevice");
                    //蓝牙未连接时，选择任意设备
                    Intent intent = new Intent();
                    intent.setAction(BtHutPairReceiver.BT_HUT_PAIR);
                    intent.putExtra("address", address);
                    mContext.sendBroadcast(intent);
                }
            }
        });
        mBtState = Contants.BT_STATE_INIT;

        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (CommonUtil.isHighConfig()) {
                    LogUtil.d(TAG,"mFl ="+mFl);
                    mFl.setBackgroundResource(integer == 4 ? R.drawable.offroad_system_settings_high_match_frame_wifi_last_sel
                            : R.drawable.offroad_system_settings_high_match_frame_wifi_last_n);
                } else {
                    mFl.setBackgroundResource(integer == 4 ? R.drawable.offroad_system_settings_low_match_frame_bt_last_sel
                            : R.drawable.offroad_system_settings_low_match_frame_bt_last_n);
                }
                AAOP_HSkin.with(mIvBtIcon)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, integer == 4 ? R.drawable.offroad_system_settings_icon_bt_big_1
                                : R.drawable.icon_big_bt)
                        .applySkin(false);
            }
        });
        showOrHideBtView(false);
    }


    private void initShareData() {
        ShareDataManager.getShareDataManager().registerShareDataListener(714, mShareDataListener);
        ShareDataManager.getShareDataManager().registerShareDataListener(715, mShareDataListener);
    }

    private void unRegisterShareData() {
        ShareDataManager.getShareDataManager().unregisterShareDataListener(714, mShareDataListener);
        ShareDataManager.getShareDataManager().unregisterShareDataListener(715, mShareDataListener);
    }

    private synchronized void finishActivity() {
        Log.d(TAG, " finishActivity finishVal = " + BtFinishManager.getInstance().isFinish());
        if (BtFinishManager.getInstance().isFinish()) {
            Log.d(TAG, "finishActivity: "+ BtFinishManager.getInstance().getSourceType());
            BtFinishManager.getInstance().setFinish(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(BtFinishManager.getInstance().getSourceType())){
                        if ( AdayoSource.ADAYO_SOURCE_BT_AUDIO.equals(BtFinishManager.getInstance().getSourceType())){
                            openBtMusic();
                        }else if (AdayoSource.ADAYO_SOURCE_BT_PHONE.equals( BtFinishManager.getInstance().getSourceType())){
                            openBtPhone();
                        }
                    }
                }
            }, 100);
        }
    }


    /**
     * 打开蓝牙音乐
     */
    private void openBtMusic() {
        LogUtil.d(TAG, "openBtMusic():");
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_BT_AUDIO, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
    }

    /**
     * 打开蓝牙音乐
     */
    private void openBtPhone() {
        LogUtil.d(TAG, "openBtMusic():");
        SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_BT_PHONE, AppConfigType.SourceSwitch.APP_ON.getValue(), UI_AUDIO.getValue());
        SrcMngSwitchManager.getInstance().requestSwitchApp(info);
    }


    @Override
    public void onResume() {
        Trace.i(TAG, "--------onResume---- finishValue = " + BtFinishManager.getInstance().isFinish());
        super.onResume();
        if (null != pairedPresenter) {
            pairedPresenter.setView(mPairView);
            pairedPresenter.init();
        }

        if (null != scanningPresenter) {
            scanningPresenter.setView(mScanView);
            scanningPresenter.init();
        }

        if (null != mSettingsPresenter) {
            //如果蓝牙服务连接成功了，启动搜索。否则再服务的onServiceConnected回调中启动搜索
            if (mSettingsPresenter.isServiceConnected()) {
                Trace.i(TAG, "--------scanningPresenter.startScan()----");
                scanningPresenter.startScan();
            }
        }

        if (null != mManagerPresenter) {
            mManagerPresenter.setView(this);
            if (mActivity != null) {
                mManagerPresenter.init(mActivity.getIntent());
            } else {
                mActivity = mBaseActivity;
                mHandler.postDelayed(() -> mManagerPresenter.init(mActivity.getIntent()), 200);
            }
        }
        if(mCommonHighlight.mCommonHighlightRequest.getWelcome().getValue()){
            mCommonHighlight.mCommonHighlightRequest.requestWelcome(false);
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        BtFinishManager.getInstance().setFinish(false);
        super.onPause();
        scanningPresenter.cancelScan();
        dismissDialog();
        scanningPresenter.removeView(mScanView);
        mManagerPresenter.removeView(this);
        pairedPresenter.removeView(mPairView);
    }

    ISettingsContract.ISettingsView mSettingsView = new ISettingsContract.ISettingsView() {
        @Override
        public void setPresenter(ISettingsContract.ISettingsPresenter iSettingsPresenter) {
        }
        @Override
        public void removePresenter(ISettingsContract.ISettingsPresenter iSettingsPresenter) {
        }
        @Override
        public void updateAutoAnswer(boolean b) {
        }
        @Override
        public void updateBluetoothStateChanged(BusinessConstants.BTStateType btStateType) {
            Trace.i(TAG, "callback---- btState_notify=" + btStateType + " btState_now=" + mBtState);
            isCanSetBt = true;
            switch (btStateType) {
                case TURNING_OFF:
                    dismissDialog();
                    mTgBtnBtSwitch.setChecked(false);
                    mTgBtnBtSwitch.setEnabled(false);
                    break;
                case TURN_OFF:
                    dismissDialog();
                    mTgBtnBtSwitch.setChecked(false);
                    mTgBtnBtSwitch.setEnabled(false);
                    showOrHideBtView(false);
                    mBtState = Contants.BT_STATE_OFF;
                    break;
                case TURNING_ON:
//                    executeBtSwitchAnim(true, true);
                    mTgBtnBtSwitch.setChecked(true);
                    mTgBtnBtSwitch.setEnabled(false);
                    break;
                case TURN_ON:
                    mTgBtnBtSwitch.setChecked(true);
                    mTgBtnBtSwitch.setEnabled(false);
                    showOrHideBtView(true);
                    mBtState = Contants.BT_STATE_ON;
                    if (null != mSettingsPresenter && mSettingsPresenter.isServiceConnected()) {
                        scanningPresenter.startScan();
                        mSettingsPresenter.setBluetoothAutoReconnectEnable(true);
                    }
                    break;
                default:
                    break;
            }

        }
        @Override
        public void updateAutoReconnect(boolean b) {
            Trace.i(TAG, "----updateAutoReconnect()----enable=" + b);
//            finishActivity();
        }
        @Override
        public void updateLocalBluetoothName(String s) {
        }
        @Override
        public void updatePinCode(String s) {
        }
        @Override
        public void updateLocalBluetoothDiscoverable(boolean b) {
        }
        @Override
        public void updateAutoDownloadContact(boolean b) {
        }
        @Override
        public void updatePrivateAnswerCall(boolean b) {
        }
        @Override
        public void updateContactsSyncState(BusinessConstants.SyncState syncState) {
        }
        @Override
        public void updateServiceConnectState(boolean b) {
            Trace.i(TAG, "--------updateServiceConnectState---- state=" + b);
            if (b) {
                if (null != scanningPresenter) {
                    scanningPresenter.startScan();
                }
            }
        }
        @Override
        public void updateRingtoneInfos(List<RingtoneInfo> list) {
        }
        @Override
        public void updataCurRingtonePosition(int i) {
        }
        @Override
        public void updateThreePartyCallEnable(boolean b) {
        }
        @Override
        public void updateTwinConnectEnable(boolean b) {
        }
        @Override
        public void notifyRingStop() {
        }
        @Override
        public void updateActiveDeviceChanged(String s, String s1, boolean b) {
        }
        @Override
        public void updateHfpState(boolean b) {
            Contants.isConnectBluetooth = b;
            Trace.i(TAG, "isConnectBluetooth=" + Contants.isConnectBluetooth);
        }
        @Override
        public void updateA2DPState(boolean b) {
        }
    };
    IPairedContract.IPairedView mPairView = new IPairedContract.IPairedView() {
        @Override
        public void setPresenter(IPairedContract.IPairedPresenter iPairedPresenter) {
        }
        @Override
        public void removePresenter(IPairedContract.IPairedPresenter iPairedPresenter) {
        }
        @Override
        public void updateHfpState(boolean b) {
        }
        @Override
        public void updateA2DPState(boolean b) {
        }
        @Override
        public void updateConnectionStatus(BusinessConstants.ConnectionStatus connectionStatus, String s) {
            Log.i(TAG, " status=" + connectionStatus + " param=" + s);
            if (BusinessConstants.ConnectionStatus.FAILED == connectionStatus) {
                //连接失败时，取消连接中动画
                Log.i(TAG, " status  =" + connectionStatus + " param=" + s);
                mToastDialog.setText(mContext.getResources().getString(R.string.conn_bt_toast_hint_bt_connected_fail));
                mToastDialog.showToast();
            } else if (BusinessConstants.ConnectionStatus.SUCCESSED == connectionStatus) {

            }
        }

        @Override
        public void updatePairedDevices(List<BluetoothDevice> list) {
            int pos = 0;
            for (BluetoothDevice data : list) {
                if (data.isHfpConnected() || data.isA2dpConnected()) {
                    Collections.swap(list, 0, pos);
                }
                pos++;
            }
            hisScanListAdapter.updateData(list);
            SetListviewItemHeigtUtil.setListViewHeightBasedOnChildren(mlvHasList);
            mHasBlueList = list;
            if (list.size() <= 0) {
                Log.d(TAG, " list.size() <= 0" + list.size());
                mlvHasList.setVisibility(View.GONE);
                //ll_his_pair.setVisibility(View.VISIBLE);
                mTvHasPair.setVisibility(View.GONE);
                mIvHasPairTop.setVisibility(View.GONE);
            } else {
                BluetoothDevice bluetoothDevice = list.get(0);
                if (bluetoothDevice.isHfpConnected() || bluetoothDevice.isA2dpConnected()) {
                    Log.d(TAG, " updatePairedDevices bt connected and finishVal = " + BtFinishManager.getInstance().isFinish());
                    finishActivity();
                }
                Log.i(TAG, " list.size() > 0" + list.size());
                mlvHasList.setVisibility(View.VISIBLE);
                //ll_his_pair.setVisibility(View.GONE);
                mTvHasPair.setVisibility(View.VISIBLE);
                mIvHasPairTop.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void showBTName(String s) {
        }

        @Override
        public void showPairFailedMsg(String s) {
            Log.i(TAG, " showPairFailedMsg callback from pair presenter");
        }

        @Override
        public void showNoPairedDeviceMsg() {
        }
        @Override
        public void hideNoPairedDeviceMsg() {
            Log.i(TAG, " hideNoPairedDeviceMsg callback from pair presenter");
            dismissProgressDialog();
        }
        @Override
        public void showConnectingView() {
            Log.i(TAG, " showConnectingView callback from pair presenter");
            //TODO:
//            showLoadingDialog();
        }
        @Override
        public void hideConnectingView() {
            Log.i(TAG, " hideConnectingView callback from pair presenter");
            dismissProgressDialog();
        }
    };
    IScanningContract.IScanningView mScanView = new IScanningContract.IScanningView() {
        @Override
        public void showScanning() {
            Trace.i(TAG, "----------showScanning--------");
            showLoading(true);
        }
        @Override
        public void showScanFinished() {
            Trace.i(TAG, "----------showScanFinished--------");
            showLoading(false);
        }
        @Override
        public void updateFoundedDevices(List<BluetoothDevice> list) {
            Trace.i(TAG, "updateFoundedDevices(), size : " + list.size()
                    + ", scanAdapter : " + scanAdapter + ", lv_can_pair : " + mlvCanPair);
            scanAdapter.updateData(list);
            SetListviewItemHeigtUtil.setListViewHeightBasedOnChildren(mlvCanPair);
            blueList = list;
            for (BluetoothDevice device : list) {
                Trace.d(TAG, "device : " + device.getAddress());
            }

            if (list.size() <= 0) {
            }

        }
        @Override
        public void showDevicesNotFindMsg() {
        }
        @Override
        public void hideDevicesNotFindMsg() {
        }
        @Override
        public void showPairFailedMsg(String s) {
            Log.i(TAG, " showPairFailedMsg callback from scan presenter");
        }
        @Override
        public void showConnectingView() {
            Log.i(TAG, " showConnectingView callback from scan presenter");
        }
        @Override
        public void hideConnectingView() {
            Log.i(TAG, " hideConnectingView callback from scan presenter");
        }
        @Override
        public void updateConnectionStatus(BusinessConstants.ConnectionStatus connectionStatus, String s) {
            Log.i(TAG, "----showConnecting()----" + connectionStatus + " param" + s);

        }
        @Override
        public void updateHfpState(boolean b) {
        }
        @Override
        public void updateA2DPState(boolean b) {
        }
        @Override
        public void setPresenter(IScanningContract.IScanningPresenter iScanningPresenter) {
        }
        @Override
        public void removePresenter(IScanningContract.IScanningPresenter iScanningPresenter) {
        }
    };

    private static final int MSG_BT = 2001;

    private CompoundButton.OnCheckedChangeListener mSwCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        }
    };

    private Runnable mDelayedRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run: mDelayedRunnable1  ");
            mSettingsPresenter.setBluetoothEnable(mTgBtnBtSwitch.isChecked());
        }
    };

    private void showLoading(boolean showLoading) {
        if (showLoading) {
            mIvRefreshNormal.setVisibility(View.GONE);
            mIbCanPairRefresh.setVisibility(View.VISIBLE);
            if (mAnimationDrawable != null) {
                if (!mAnimationDrawable.isRunning()){
                    mAnimationDrawable.start();}
            }
        } else {
            if (mAnimationDrawable != null) {
                mAnimationDrawable.selectDrawable(0);
                mAnimationDrawable.stop();
            }
            mIvRefreshNormal.setVisibility(View.VISIBLE);
            mIbCanPairRefresh.setVisibility(View.GONE);
        }

    }

    private void dismissProgressDialog() {


    }

    private void dismissDialog() {
        if (mDisConnectDialog != null) {
            if (mDisConnectDialog.isShowing()) {
                mDisConnectDialog.dismiss();
            }
        }

        if (mDisConnectAndConnectOtherDialog != null) {
            if (mDisConnectAndConnectOtherDialog.isShowing()) {
                mDisConnectAndConnectOtherDialog.dismiss();
            }
        }

        if (mDeleteDialog != null) {
            if (mDeleteDialog.isShowing()) {
                mDeleteDialog.dismiss();
            }
        }
        if (mPairDialog != null) {
            if (mPairDialog.isShowing()) {
                mPairDialog.dismiss();
            }
        }
        if (mToastDialog != null) {
            if (mToastDialog.isShowing()) {
                mToastDialog.dismiss();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    public void showOrHideBtView(boolean isShow) {
        Trace.i(TAG, "showOrHideBTView" + isShow + "");
        if (isShow) {
            mLlBtNotConnected.setVisibility(View.GONE);
            mLyBluetoothList.setVisibility(View.VISIBLE);
        } else {
            mLlBtNotConnected.setVisibility(View.VISIBLE);
            mLyBluetoothList.setVisibility(View.GONE);
        }
    }
    @Override
    public void showOrEnableBottomBar() {
    }
    @Override
    public void hideOrDisableBottomBar() {
    }
    @Override
    public void onTabSelectedChanged(BusinessConstants.FRAGMENT_INDEX fragmentIndex) {
    }
    @Override
    public void showWelcomeLoadingView() {
    }
    @Override
    public void hideWelcomeLoadingView() {
    }
    @Override
    public void finishUI() {
    }
    @Override
    public void updateHfpState(boolean b) {
        Trace.i(TAG, "updateHfpState---- b=" + b);
        if (!mSettingsPresenter.isHfpConnected() && !mSettingsPresenter.isA2dpConnected()) {
//            dismissDisConnectDialog();
        }

    }
    @Override
    public void updateA2DPState(boolean b) {
        Trace.i(TAG, "updateA2DPState---- b=" + b);
        if (!mSettingsPresenter.isHfpConnected() && !mSettingsPresenter.isA2dpConnected()) {
            //           dismissDisConnectDialog();
        }

    }
    @Override
    public boolean showPage(BusinessConstants.FRAGMENT_INDEX pageType, Bundle bundle) {
        Trace.i(TAG, "----showPage() pageType=" + pageType);
        switch (pageType) {
            case BT_POWEROFF:
                Trace.i(TAG, "BT_POWEROFF ");
                Contants.isConnectBluetooth = false;
                break;
            case HFP_DISCONNECTED:
                Trace.i(TAG, "HFP_DISCONNECTED ");
                Contants.isConnectBluetooth = false;
                break;
            case HFP_CONNECTED:
                Contants.isConnectBluetooth = true;
                break;
            default:
                break;
        }

        return true;
    }
    @Override
    public boolean showPages(Set<BusinessConstants.FRAGMENT_INDEX> set, Bundle bundle) {
        return false;
    }

    @Override
    public void onConnectClick(String address) {

    }

    @Override
    public void onDisconnectClick(String address) {
        Contants.isConnectBluetooth = false;
    }

    @Override
    public void onDeleteClick(String address) {
        //显示删除设备对话框
        setBackLight();
        mDeleteDialog.setOnPosListener(() -> pairedPresenter.deleteDevice(address));
        mDeleteDialog.show();
    }

    @Override
    public void onConnected(boolean var1) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        Log.d(TAG, "onPause: ");
        BtFinishManager.getInstance().setFinish(false);
        if (mManagerPresenter != null) {
            mManagerPresenter.removeView(this);
        }

        if (mSettingsPresenter != null) {
            mSettingsPresenter.removeView(mSettingsView);
        }

        if (scanningPresenter != null) {
            scanningPresenter.removeView(mScanView);
        }

        if (pairedPresenter != null) {
            pairedPresenter.removeView(mPairView);
        }

        mManagerPresenter = null;
        mSettingsPresenter = null;
        scanningPresenter = null;
        pairedPresenter = null;
        unRegisterShareData();
    }

    /**
     * 设置背景高亮
     */
    private void setBackLight() {
        mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(4);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }
}