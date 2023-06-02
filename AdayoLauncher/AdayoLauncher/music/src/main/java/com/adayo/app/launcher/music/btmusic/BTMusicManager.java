package com.adayo.app.launcher.music.btmusic;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.app.launcher.music.util.SPUtils;
import com.lt.library.util.context.ContextUtil;
import com.nforetek.bt.base.jar.NforeBtBaseJar;
import com.nforetek.bt.res.NfDef;

public class BTMusicManager implements IBTMusicFunction {

    private static volatile BTMusicManager mModel;
    private IBTMusicCallBack mBtCallBack;
    private static final String TAG = "Launcher_"+BTMusicManager.class.getSimpleName();

//    private boolean isBTMusicFoucs = false;

    private BTMusicManager() {
        // TODO: 2020/2/29 注册carservice回调
    }

    public static BTMusicManager getInstance() {
        if (mModel == null) {
            synchronized (BTMusicManager.class) {
                if (mModel == null) {
                    mModel = new BTMusicManager();
                }
            }
        }
        return mModel;
    }

    @Override
    public void play() {
        try {
            boolean success = NforeBtBaseJar.reqAvrcpPlay();
            Log.d(TAG, "reqAvrcpPlay: " + success);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            boolean success = NforeBtBaseJar.reqAvrcpPause();
            Log.d(TAG, "reqAvrcpPause: " + success);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playPrev() {
        NforeBtBaseJar.playPrev();
        Log.d(TAG, "playPrev: ");
    }

    @Override
    public void playNext() {
        NforeBtBaseJar.playNext();
        Log.d(TAG, "playNext: ");
    }

    @Override
    public void playOrPause() {
        if ( NforeBtBaseJar.getPlayStatus()  ==NfDef.AVRCP_PLAYING_STATUS_ID_PLAYING ){
            SPUtils.setNeedPlay(ContextUtil.getAppContext(),false);
            pause();
        }else {
            SPUtils.setNeedPlay(ContextUtil.getAppContext(),true);
            play();
        }
    }

    @Override
    public void init() {
        Log.d(TAG, "init: ");
        //蓝牙连接状态
        try {
            boolean isA2dpConnect = NforeBtBaseJar.isA2dpConnected(null);
            boolean isHfpConnect = NforeBtBaseJar.isHfpConnected(null);
            if (mBtCallBack != null) {
                if (!isA2dpConnect) {
                    if (isHfpConnect) {
                        mBtCallBack.notifyA2dpDisconnect();
                    } else {
                        mBtCallBack.notifyBluetoothDisconnect();
                    }
                } else {
                    mBtCallBack.notifyA2dpConnect();
                }
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notifyPlayStatus(long totalTime, long currentTime, int playStatus) {
        if (mBtCallBack != null) {
            mBtCallBack.onPlayTimeStatus(totalTime, currentTime);
            switch (playStatus) {
                case NfDef.AVRCP_PLAYING_STATUS_ID_PAUSED:
                case NfDef.AVRCP_PLAYING_STATUS_ID_STOPPED:
                    mBtCallBack.onMusicStop();
                    break;
                case NfDef.AVRCP_PLAYING_STATUS_ID_PLAYING:
                    mBtCallBack.onMusicPlay();
                    break;
            }
        }
    }

    @Override
    public void notifyId3Info(String artist, String album, String title) {
        if (mBtCallBack != null) {
            mBtCallBack.onId3Info(artist, album, title);
        }
    }

    @Override
    public void notifyA2dpDisconnect() {
        if (mBtCallBack != null) {
            mBtCallBack.notifyA2dpDisconnect();
        }
    }

    @Override
    public void notifyA2dpConnect() {
        if (mBtCallBack != null) {
            mBtCallBack.notifyA2dpConnect();
        }
    }

    @Override
    public void notifyBluetoothDisconnect() {
        if (mBtCallBack != null) {
            mBtCallBack.notifyBluetoothDisconnect();
        }
    }


    @Override
    public void registerCallBack(IBTMusicCallBack callBack) {
        this.mBtCallBack = callBack;
    }
}
