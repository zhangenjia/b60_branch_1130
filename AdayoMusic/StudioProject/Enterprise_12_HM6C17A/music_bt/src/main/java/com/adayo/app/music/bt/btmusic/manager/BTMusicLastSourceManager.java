package com.adayo.app.music.bt.btmusic.manager;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.adayo.app.music.bt.btmusic.callback.IBTMusicCallBack;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.nforetek.bt.base.jar.NforeBtBaseJar;
import com.nforetek.bt.base.listener.BluetoothMusicChangeListener;
import com.nforetek.bt.base.listener.BluetoothSettingChangeListener;
import com.nforetek.bt.res.NfDef;

import org.json.JSONException;
import org.json.JSONObject;

public class BTMusicLastSourceManager {

    private static final String TAG = BTMusicLastSourceManager.class.getSimpleName();
    private static volatile BTMusicLastSourceManager mModel;
    private IBTMusicCallBack mBtCallBack;
    private Context mContext;
    private String mCurrentAudioId = AdayoSource.ADAYO_SOURCE_NULL;
    private String mPrevAudioId = AdayoSource.ADAYO_SOURCE_NULL;
    private volatile int mA2dpConnect = 0;
    private volatile int mAvrcpConnect = 0;

    private BTMusicLastSourceManager(Context context) {
        // TODO: 2020/2/29 注册carservice回调
        this.mContext = context;
        initNf();
    }

    private void registerShareData() {
        String jsonStr = ShareDataManager.getShareDataManager().getShareData(14);
        if (!TextUtils.isEmpty(jsonStr)) {
            mCurrentAudioId = getAudioId(jsonStr);
        }
        ShareDataManager.getShareDataManager().registerShareDataListener(14, new IShareDataListener() {
            @Override
            public void notifyShareData(int dataId, String dataValue) {
                if (dataId == 14) {
                    if (!TextUtils.isEmpty(dataValue)) {
                        Log.d(TAG, "notifyShareData: mCurrentAudioId = " + mCurrentAudioId);
                        mCurrentAudioId = getAudioId(dataValue);
                        if (!AdayoSource.ADAYO_SOURCE_NULL.equals(mCurrentAudioId) && !AdayoSource.ADAYO_SOURCE_CLOCK.equals(mCurrentAudioId)
                                && !AdayoSource.ADAYO_SOURCE_BT_AUDIO.equals(mCurrentAudioId)) {
                            mPrevAudioId = mCurrentAudioId;
                        }
                    }
                }
            }
        });
    }

    private String getAudioId(String jsonStr) {
        String audioId = AdayoSource.ADAYO_SOURCE_NULL;
        try {
            JSONObject object = new JSONObject(jsonStr);
            if (object.has("AudioID")) {
                audioId = object.getString("AudioID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return audioId;
    }

    public static BTMusicLastSourceManager getInstance(Context context) {
        if (mModel == null) {
            synchronized (BTMusicLastSourceManager.class) {
                if (mModel == null) {
                    mModel = new BTMusicLastSourceManager(context);
                }
            }
        }
        return mModel;
    }

    private void initNf() {
        NforeBtBaseJar.registerBluetoothServiceConnectedListener(b -> {
            try {
                SrcMngSwitchManager.getInstance().notifyServiceUIChange(AdayoSource.ADAYO_SOURCE_BT_AUDIO, mContext.getPackageName());
                boolean success = NforeBtBaseJar.reqAvrcpPlay();
                registerNfListener();
                registerShareData();
                Log.d(TAG, "reqAvrcpPlay: " + success);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
        NforeBtBaseJar.init(mContext);
    }

    private void registerNfListener() {
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
                    mAvrcpConnect = connectState;
                    Log.d(TAG, "onAvrcpStateChanged: avrcpConnect " + mAvrcpConnect);

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
                    Log.d(TAG, "onAvrcpStateChanged: a2dpConnect " + mA2dpConnect);
                    mA2dpConnect = connectState;
                    onA2dpAvrcpConnectState();
                } else if (connectState == NforeBtBaseJar.CONNECT_DISCONNECT) {
                    //断开
                    Log.d(TAG, "onA2dpStateChanged: a2dpDisConnect");
                    mCurrentAudioId = AdayoSource.ADAYO_SOURCE_NULL;
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
            public void retAvrcp13ElementAttributesPlaying(int[] ints, String[] strings) {

            }

            @Override
            public void retAvrcp13PlayStatus(long totalTime, long currentTime, byte playStatus) {
                if (playStatus == NfDef.AVRCP_PLAYING_STATUS_ID_PLAYING) {
                    mPrevAudioId = AdayoSource.ADAYO_SOURCE_BT_AUDIO;
                }
            }

            @Override
            public void onAvrcp13RegisterEventWatcherSuccess(byte b) {

            }

            @Override
            public void onAvrcp13RegisterEventWatcherFail(byte b) {

            }

            @Override
            public void onAvrcp13EventPlaybackStatusChanged(byte b) {

            }

            @Override
            public void onAvrcp13EventTrackChanged(long l) {

            }

            @Override
            public void onAvrcp13EventTrackReachedEnd() {

            }

            @Override
            public void onAvrcp13EventTrackReachedStart() {

            }

            @Override
            public void onAvrcp13EventPlaybackPosChanged(long l) {

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
            public void retAvrcpUpdateSongStatus(String s, String s1, String s2) {

            }
        });
    }

    private synchronized void onA2dpAvrcpConnectState() {

        //蓝牙连接
        if (mA2dpConnect == NforeBtBaseJar.CONNECT_SUCCESSED
                && mAvrcpConnect == NforeBtBaseJar.CONNECT_SUCCESSED) {
            if (AdayoSource.ADAYO_SOURCE_BT_AUDIO.equals(mPrevAudioId)) {
                try {
                    NforeBtBaseJar.requestA2dpFocus();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
