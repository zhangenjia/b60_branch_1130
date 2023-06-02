package com.adayo.app.music.bt.btmusic.model;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.app.music.bt.btmusic.manager.BTMusicManager;
import com.adayo.app.music.bt.btmusic.util.SPUtils;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.nforetek.bt.base.jar.NforeBtBaseJar;
import com.nforetek.bt.base.listener.BluetoothMusicChangeListener;
import com.nforetek.bt.base.listener.BluetoothServiceConnectedListener;
import com.nforetek.bt.base.listener.BluetoothSettingChangeListener;
import com.nforetek.bt.base.listener.OnFocusChangedListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BTMusicModel {

    private Context mContext;
    private volatile int num = 0;
    private static final int CAR_CONNECT = 1;
    private BTMusicManager mBTMusicManager;
    private static final String TAG = "BTMusicModel_APP";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private volatile String mTitle;
    private volatile boolean isConnectBT = false;
    private boolean isRegister = false;
    private final ExecutorService mThread = Executors.newSingleThreadExecutor();
    private final Object mLock = new Object();
    private volatile int mA2dpConnect = 0;
    private volatile int mAvrcpConnect = 0;

   private volatile static BTMusicModel sInstance;

   private BTMusicModel(Context context) {
       this.mContext = context;
   }

   public static BTMusicModel getInstance(Context context) {
       if (sInstance == null) {
           synchronized (BTMusicModel.class) {
               if (sInstance == null) {
                   sInstance = new BTMusicModel(context);
               }
           }
       }
       return sInstance;
   }

    private void setConnectBT(boolean connectBT) {
        synchronized (mLock){
            isConnectBT = connectBT;
            if (isConnectBT) {
                Log.d(TAG, "connectBTSuccess: playBTMusic1");
                initBTStatus();
            }
        }
    }

    public void setBTMusicPresenter(BTMusicManager manager) {
        this.mBTMusicManager = manager;
    }

    public void init() {
        Log.d(TAG, "init: ");
        if (isConnectBT) {
            if (mBTMusicManager != null) {
                Log.d(TAG, "init: playBTMusic1");
                initBTStatus();
            } else {
                Log.d(TAG, "BTMusicManager: null");
            }
        }

        if (!isRegister) {
            Log.d(TAG, "init: firstBoot registerBT");
            mThread.execute(new Runnable() {
                @Override
                public void run() {
                    NforeBtBaseJar.registerBluetoothServiceConnectedListener(new BluetoothServiceConnectedListener() {
                        @Override
                        public void onServiceConnectedChanged(boolean isConnected) {
                            Log.d(TAG, "onServiceConnectedChanged: isConnected = " + isConnected);
                            if (isConnected) {
                                registerBTService();
                            }
                            setConnectBT(isConnected);
                        }
                    });
                    NforeBtBaseJar.init(mContext);
                    NforeBtBaseJar.setOnFocusChangedListener(focusChange -> {
                        if (focusChange== AudioManager.AUDIOFOCUS_LOSS){
                            Log.d(TAG, "run: AUDIOFOCUS_LOSS");
                            SPUtils.setNeedPlay(mContext,true);
                        }
                    });
                }
            });
            isRegister = true;
        }
    }

    private void initBTStatus() {
        mThread.execute(new Runnable() {
            @Override
            public void run() {
                if (mBTMusicManager != null) {
                    mBTMusicManager.requestAudioFocus();
                    if (SPUtils.isNeedPlay(mContext)) {
                        mBTMusicManager.play();
                    }
                    mHandler.post(() -> {
                        mBTMusicManager.init();
                    });

                }
            }
        });
    }

    public void unInit() {
        if (mBTMusicManager!=null){
            mBTMusicManager.abandonA2dpFocus();
        }
        NforeBtBaseJar.release();
        setConnectBT(false);
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
                Log.d(TAG, "onAvrcpStateChanged: address = "+address);
                if (connectState == NforeBtBaseJar.CONNECT_SUCCESSED) {
                    //连接
                    Log.d(TAG, "onAvrcpStateChanged: avrcpConnect");
                    mAvrcpConnect = connectState;
                    onA2dpAvrcpConnectState();
                } else if (connectState == NforeBtBaseJar.CONNECT_DISCONNECT) {
                    //断开
                    Log.d(TAG, "onAvrcpStateChanged: avrcpDisConnect");
                }

            }

            @Override
            public void onA2dpStateChanged(final String address, final int connectState) {
                Log.d(TAG, "onA2dpStateChanged: address = "+address);
                if (connectState == NforeBtBaseJar.CONNECT_SUCCESSED) {
                    //连接
                    Log.d(TAG, "onA2dpStateChanged: a2dpConnect");
                    mA2dpConnect = connectState;
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
    }

    private synchronized void onA2dpAvrcpConnectState() {
        mHandler.post(() -> {

            //蓝牙连接
            if (mA2dpConnect == NforeBtBaseJar.CONNECT_SUCCESSED
                    && mAvrcpConnect == NforeBtBaseJar.CONNECT_SUCCESSED) {
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
        });
    }
}

