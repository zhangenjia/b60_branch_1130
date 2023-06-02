package com.adayo.app.launcher.music.btmusic;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.lt.library.util.context.ContextUtil;
import com.nforetek.bt.base.jar.NforeBtBaseJar;
import com.nforetek.bt.base.listener.BluetoothMusicChangeListener;
import com.nforetek.bt.base.listener.BluetoothSettingChangeListener;

public class BTMusicModel {

    private volatile int num = 0;
    private static final int CAR_CONNECT = 1;
    private BTMusicManager mBTMusicManager;
    private static final String TAG = BTMusicModel.class.getSimpleName();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile String mTitle;


    public BTMusicModel() {

    }

    public void setBTMusicPresenter(BTMusicManager manager) {
        this.mBTMusicManager = manager;
    }

    public void init() {
        Log.d(TAG, "init: ");

        if (NforeBtBaseJar.init(ContextUtil.getAppContext())) {
            registerBTService();
        } else {
            Thread thread = new Thread(this::retry);
            thread.start();
        }
    }

    private void registerBTService() {
        Log.d(TAG, "registerBTService: ");
        NforeBtBaseJar.registerBluetoothMusicChangeListener(new BluetoothMusicChangeListener() {

            @Override
            public void retAvrcp13CapabilitiesSupportEvent(byte[] bytes) {

            }

            @Override
            public void retAvrcp13PlayerSettingAttributesList(byte[] bytes) {

            }

            @Override
            public void retAvrcp13PlayerSettingValuesList(byte b, byte[] bytes) {

            }

            @Override
            public void retAvrcp13PlayerSettingCurrentValues(byte[] bytes, byte[] bytes1) {

            }

            @Override
            public void retAvrcp13SetPlayerSettingValueSuccess() {

            }

            @Override
            public void retAvrcp13ElementAttributesPlaying(int[] metadataAtrributeIds, String[] texts) {

            }

            @Override
            public void retAvrcp13PlayStatus(long totalTime, long currentTime, byte playStatus) {
                Log.d(TAG, "retAvrcp13PlayStatus: " + currentTime);
                if (mBTMusicManager != null) {
                    mHandler.post(() -> {
                        mBTMusicManager.notifyPlayStatus(totalTime, currentTime, playStatus);
                    });
                }
            }

            @Override
            public void onAvrcp13RegisterEventWatcherSuccess(byte b) {

            }

            @Override
            public void onAvrcp13RegisterEventWatcherFail(byte b) {

            }

            @Override
            public void onAvrcp13EventPlaybackStatusChanged(byte playStatus) {

            }

            @Override
            public void onAvrcp13EventTrackChanged(long l) {
                try {
                    NforeBtBaseJar.reqAvrcpUpdateSongStatus();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAvrcp13EventTrackReachedEnd() {

            }

            @Override
            public void onAvrcp13EventTrackReachedStart() {

            }

            @Override
            public void onAvrcp13EventPlaybackPosChanged(long l) {
//                notifyAvrcpPosition(l);
            }

            @Override
            public void onAvrcp13EventBatteryStatusChanged(byte b) {

            }

            @Override
            public void onAvrcp13EventSystemStatusChanged(byte b) {

            }

            @Override
            public void onAvrcp13EventPlayerSettingChanged(byte[] bytes, byte[] bytes1) {

            }

            @Override
            public void onAvrcp14EventNowPlayingContentChanged() {

            }

            @Override
            public void onAvrcp14EventAvailablePlayerChanged() {

            }

            @Override
            public void onAvrcp14EventAddressedPlayerChanged(int i, int i1) {

            }

            @Override
            public void onAvrcp14EventUidsChanged(int i) {

            }

            @Override
            public void onAvrcp14EventVolumeChanged(byte b) {

            }

            @Override
            public void retAvrcp14SetAddressedPlayerSuccess() {

            }

            @Override
            public void retAvrcp14SetBrowsedPlayerSuccess(String[] strings, int i, long l) {

            }

            @Override
            public void retAvrcp14FolderItems(int i, long l) {

            }

            @Override
            public void retAvrcp14MediaItems(int i, long l) {

            }

            @Override
            public void retAvrcp14ChangePathSuccess(long l) {

            }

            @Override
            public void retAvrcp14ItemAttributes(int[] ints, String[] strings) {

            }

            @Override
            public void retAvrcp14PlaySelectedItemSuccess() {

            }

            @Override
            public void retAvrcp14SearchResult(int i, long l) {

            }

            @Override
            public void retAvrcp14AddToNowPlayingSuccess() {

            }

            @Override
            public void retAvrcp14SetAbsoluteVolumeSuccess(byte b) {

            }

            @Override
            public void onAvrcpErrorResponse(int i, int i1, byte b) {

            }

            @Override
            public void retAvrcpUpdateSongStatus(String artist, String album, String title) {
                mTitle = title;
                if (mBTMusicManager != null) {
                    mHandler.post(() -> {
                        Log.d(TAG, "retAvrcpUpdateSongStatus: 专辑名==" + album + ",演唱者==" + artist + ",歌曲名==" + title);

//                        if (!TextUtils.isEmpty(mTitle)) {
//                            if ("Not Provided".equals(mTitle)) {
//                                mTitle = "未知";
//                            }
//                        } else {
//                            mTitle = "未知";
//                        }
//
//                        if (TextUtils.isEmpty(mTitle)) {
//                            mTitle = "Not Provided";
//                        }
                        mBTMusicManager.notifyId3Info(artist, album, mTitle);
                    });
                }
            }

        });
        NforeBtBaseJar.registerBluetoothSettingChangeListener(new BluetoothSettingChangeListener() {
            //蓝牙开关状态改变
            @Override
            public void onAdapterStateChanged(final int prevState, final int newState) {

            }

            /** 蓝牙开关状态回调 **/
            @Override
            public void onEnableChanged(final boolean isEnable) {

            }

            //蓝牙连接状态回调，只要有任意一个协议（hfp,a2dp,avrcp）连接，则返回true
            @Override
            public void onConnectedChanged(final String address, final int newState) {

            }

            /** 蓝牙hfp连接状态回调 **/
            @Override
            public void onHfpStateChanged(final String address, final int connectState) {
                if (connectState == NforeBtBaseJar.CONNECT_DISCONNECT) {
                    try {
                        boolean isA2dpConnect = NforeBtBaseJar.isA2dpConnected(address);
                        if (!isA2dpConnect) {//a2dp断开
                            if (mBTMusicManager != null) {
                                mHandler.post(() -> {
                                    mBTMusicManager.notifyBluetoothDisconnect();
                                });
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }

            /** 蓝牙 HFP 连接远程设备的音频状态变化 **/
            @Override
            public void onHfpAudioStateChanged(final String address, final int prevState, final int newState) {

            }

            /** 蓝牙avrcp连接状态回调 **/
            @Override
            public void onAvrcpStateChanged(final String address, final int connectState) {
                if (connectState == NforeBtBaseJar.CONNECT_SUCCESSED) {
                    //连接
                    Log.d(TAG, "onAvrcpStateChanged: avrcpConnect");
                    onA2dpAvrcpConnectState();
                } else if (connectState == NforeBtBaseJar.CONNECT_DISCONNECT) {
                    //断开
                    Log.d(TAG, "onAvrcpStateChanged: avrcpDisConnect");
                }

            }

            @Override
            public void onA2dpStateChanged(final String address, final int connectState) {
                if (connectState == NforeBtBaseJar.CONNECT_SUCCESSED) {
                    //连接
                    Log.d(TAG, "onAvrcpStateChanged: a2dpConnect");
                    onA2dpAvrcpConnectState();
                } else if (connectState == NforeBtBaseJar.CONNECT_DISCONNECT) {
                    //断开
                    Log.d(TAG, "onA2dpStateChanged: a2dpDisConnect");
                    try {
                        boolean isHfpConnect = NforeBtBaseJar.isHfpConnected(address);
                        if (isHfpConnect) {//a2dp断开
                            if (mBTMusicManager != null) {
                                mHandler.post(() -> {
                                    mBTMusicManager.notifyA2dpDisconnect();
                                });
                            }
                        } else {//蓝牙断开
                            if (mBTMusicManager != null) {
                                mHandler.post(() -> {
                                    mBTMusicManager.notifyBluetoothDisconnect();
                                });
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
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
            }

            @Override
            public void onMainDevicesChanged(final String address, final String name) {

            }
        });
        if (mBTMusicManager != null) {
            Log.d(TAG, "BTMusicManager : init");
            mBTMusicManager.init();
        } else {
            Log.d(TAG, "BTMusicManager: null");
        }
    }

    private void retry() {
        if (!NforeBtBaseJar.init(ContextUtil.getAppContext())) {
            try {
                num++;
                Log.d(TAG, "run: 正在尝试第" + num + "次连接BT服务");
                Thread.sleep(500);
                retry();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "BTService启动！");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    num = 0;
                    registerBTService();
                }
            });
        }
    }

    private void onA2dpAvrcpConnectState() {
        boolean a2dpConnected = false;
        boolean avrcpConnected = false;
        try {
            a2dpConnected = NforeBtBaseJar.isA2dpConnected(null);
            avrcpConnected = NforeBtBaseJar.isAvrcpConnected(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onA2dpAvrcpConnectState: a2dp连接状态==" + a2dpConnected);
        Log.d(TAG, "onA2dpAvrcpConnectState: avrcp连接状态==" + avrcpConnected);
        if (a2dpConnected && avrcpConnected) {
            if (mBTMusicManager != null) {
                mHandler.post(() -> {
                    mBTMusicManager.notifyA2dpConnect();
                });
            }
            //连接
            try {
                NforeBtBaseJar.reqAvrcpUpdateSongStatus();//id3信息
                NforeBtBaseJar.reqAvrcp13GetPlayStatus();//进度条
//                NforeBtBaseJar.reqAvrcp13GetElementAttributesPlaying();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onA2dpAvrcpConnectState: a2dp与avrcp都连接上了");
        }
    }
}

