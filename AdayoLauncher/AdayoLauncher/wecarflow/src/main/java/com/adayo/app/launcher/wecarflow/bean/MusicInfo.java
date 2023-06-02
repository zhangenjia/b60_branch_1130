package com.adayo.app.launcher.wecarflow.bean;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.adayo.app.launcher.wecarflow.util.LogUtil;
import com.tencent.wecarflow.controlsdk.MediaInfo;

public class MusicInfo {
    private static final String TAG = MusicInfo.class.getSimpleName();
    private static MusicInfo mInfo;
    private Context mContext;
    public static final int CONTENT_STATUS_NOT_EXISTED = 0;
    public static final int CONTENT_STATUS_EXISTED = 1;
    public static final int PLAY_STATE_STOP = 0;
    public static final int PLAY_STATE_PLAY = 1;
    public static final int PLAY_STATE_PAUSE = 2;
    private boolean networkState;
    private int contentState;
    private MediaInfo mediaInfo;
    private int playState;
    private String mediaType;
    private long process_current;
    private long process_total;
    private MusicInfoChangeListener mMusicInfoChangeListener;
    private  ConnectivityManager mConnectManager;
    private int TYPE_RNDIS = 18;

    private MusicInfo(Context context) {
        mContext = context;
        mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        if (mConnectManager != null) {
            mConnectManager.registerNetworkCallback(networkRequest, mNetworkCallback);
        } else {
            LogUtil.d(TAG, "ConnectManager is Null!");
        }

        getNetWorkState();
    }

    private void getNetWorkState(){
         mConnectManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(mConnectManager!=null){
            NetworkInfo info = mConnectManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                Log.d(TAG, "onAvailable: info.isConnected() " + info.isConnected());
                if ((info.getType() == ConnectivityManager.TYPE_WIFI) || (info.getType() == TYPE_RNDIS)) {
                    setNetworkState(true);
                } else {
                    Log.d(TAG, "getNetWorkState: 1");
                    setNetworkState(false);
                }
            } else {
                Log.d(TAG, "getNetWorkState: 2");
                setNetworkState(false);
            }
        }else {
            Log.d(TAG, "getNetWorkState: 3");
            setNetworkState(false);
        }

    }
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            getNetWorkState();
        }

        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
    
            Log.d(TAG, "getNetWorkState: 4");
            setNetworkState(false);
        }

        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }

        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
        }

        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }
    };

    public static MusicInfo getInstance(Context context) {
        if (null == mInfo) {
            synchronized (MusicInfo.class) {
                if (null == mInfo) {
                    mInfo = new MusicInfo(context);
                }
            }
        }
        return mInfo;
    }

//    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
//        @Override
//        public void onAvailable(Network network) {
//            super.onAvailable(network);
//            ConnectivityManager connectivity = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if(connectivity != null){
//                NetworkInfo info = connectivity.getActiveNetworkInfo();
//                if (info != null && info.isConnected()){
//                    if ((info.getType() == ConnectivityManager.TYPE_WIFI)||(info.getType() == ConnectivityManager.TYPE_MOBILE)){
//                        setNetworkState(true);
//                    }else {
//                        setNetworkState(false);
//                    }
//                }else {
//                    setNetworkState(false);
//                }
//            }else {
//                setNetworkState(false);
//            }
//        }
//
//        @Override
//        public void onLosing(Network network, int maxMsToLive) {
//            super.onLosing(network, maxMsToLive);
//            Log.d("bug1013178_", "onLosing 3: ");
//        }
//
//        @Override
//        public void onLost(Network network) {
//            super.onLost(network);
//            Log.d("bug1013178_", "onLost: ");
//            setNetworkState(false);
//        }
//
//        @Override
//        public void onUnavailable() {
//            super.onUnavailable();
//        }
//
//        @Override
//        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
//            super.onCapabilitiesChanged(network, networkCapabilities);
//        }
//
//        @Override
//        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
//            super.onLinkPropertiesChanged(network, linkProperties);
//        }
//    };

    public boolean getNetworkState() {
        LogUtil.d(TAG, "getNetworkState():networkState = " + networkState);

        return networkState;
    }

    public void setNetworkState(Boolean state) {
        LogUtil.d(TAG, "setNetworkState():state = " + state);
        Log.d("bug1013178", "setNetworkState: "+state);
        this.networkState = state;

        if (mMusicInfoChangeListener != null) {
            mMusicInfoChangeListener.onNetWorkStateChanged(networkState);
        } else {
            LogUtil.e(TAG, "mMusicInfoChangeListener is Null!");
        }
    }

    public boolean getContentState() {
        LogUtil.d(TAG, "getContentState(): contentState = " + contentState);

        if (contentState == CONTENT_STATUS_EXISTED) {
            return true;
        } else {
            return false;
        }
    }

    public void setContentState(Integer contentState) {
        LogUtil.d(TAG, "setContentState():contentState = " + contentState);

        this.contentState = contentState;

        if (mMusicInfoChangeListener != null) {
            if (contentState == CONTENT_STATUS_EXISTED) {
                mMusicInfoChangeListener.onConnectStateChanged(true);
            } else {
                mMusicInfoChangeListener.onConnectStateChanged(false);
            }
        } else {
            LogUtil.e(TAG, "mMusicInfoChangeListener is Null!");
        }

    }

    public MediaInfo getMediaInfo() {
        LogUtil.d(TAG, "getMediaInfo():");

        if (mediaInfo != null) {
            LogUtil.d(TAG, "mediaInfo = " + mediaInfo.toString());
            return mediaInfo;
        } else {
            LogUtil.e(TAG, "mediaInfo is null!!");
            return null;
        }

    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        LogUtil.d(TAG, "setMediaInfo():mediaInfo = " + mediaInfo.toString());

        this.mediaInfo = mediaInfo;

        if (mMusicInfoChangeListener != null) {
            mMusicInfoChangeListener.onMediaInfoChanged(mediaInfo);
        } else {
            LogUtil.e(TAG, "mMusicInfoChangeListener is Null!");
        }

    }

    public int getPlayState() {
        LogUtil.d(TAG, "getPlayState():playState = " + playState);

        return playState;
    }

    public void setPlayState(Integer playState) {
        LogUtil.d(TAG, "setPlayState():playState = " + playState);
        LogUtil.d("GG", "this = " + this.hashCode());
        this.playState = playState;

        if (mMusicInfoChangeListener != null) {
            mMusicInfoChangeListener.onPlayStateChanged(playState);
        } else {
            LogUtil.e(TAG, "mMusicInfoChangeListener is Null!");
        }
    }

    public String getMediaType() {
        LogUtil.d(TAG, "getMediaType():mediaType = " + mediaType);

        return mediaType;
    }

    public long getCurrentProcess() {
        LogUtil.d(TAG, "getCurrentProcess():process_current = " + process_current);

        return process_current;
    }

    public long getTotalProcess() {
        LogUtil.d(TAG, "getTotalProcess():process_total = " + process_total);

        return process_total;
    }

    public void setProcess(String type, long current, long total) {
        LogUtil.d(TAG, "setProcess():type = " + type + " current = " + current + " total = " + total);
        if (mMusicInfoChangeListener != null) {
            this.mediaType = type;

            this.process_current = current;
            mMusicInfoChangeListener.onProcessCurrentChanged(process_current);

            if (process_total != total) {
                this.process_total = total;
                mMusicInfoChangeListener.onProcessTotalChanged(process_total);
            }
        } else {
            LogUtil.e(TAG, "mMusicInfoChangeListener is Null!");
        }
    }

    public interface MusicInfoChangeListener {
        void onPlayStateChanged(int playState);

        void onMediaInfoChanged(MediaInfo mediaInfo);

        void onConnectStateChanged(boolean connectState);

        void onNetWorkStateChanged(boolean netWorkState);

        void onProcessTotalChanged(long progress);

        void onProcessCurrentChanged(long progress);
    }

    public void setMusicInfoChangeListener(MusicInfoChangeListener musicInfoChangeListener) {
        this.mMusicInfoChangeListener = musicInfoChangeListener;
    }

}
