package com.adayo.app.launcher.wecarflow.util;

import android.content.Context;

import com.adayo.app.launcher.wecarflow.bean.MusicInfo;
import com.tencent.wecarflow.controlsdk.BindListener;
import com.tencent.wecarflow.controlsdk.FlowPlayControl;
import com.tencent.wecarflow.controlsdk.MediaChangeListener;
import com.tencent.wecarflow.controlsdk.MediaInfo;
import com.tencent.wecarflow.controlsdk.PlayStateListener;
import com.tencent.wecarflow.controlsdk.QueryCallback;
import com.tencent.wecarflow.controlsdk.data.NavigationInfo;

public class initSDKUtil {
    private static final String TAG = "My" + initSDKUtil.class.getSimpleName();

    private static initSDKUtil mUtil;
    private Context mContext;
    private final MusicInfo mMusicInfo;
    private final QueryCallback<MediaInfo> mQueryCallback = new QueryCallback() {
        @Override
        public void onError(int code) {
            LogUtil.d(TAG, "onError():code = " + code);
        }

        @Override
        public void onSuccess(Object o) {
            LogUtil.d(TAG, "onSuccess():mediaInfo = " + o.toString());
            MediaInfo mediaInfo = (MediaInfo) o;
            mMusicInfo.setContentState(mediaInfo.getItemUUID() == null ? MusicInfo.CONTENT_STATUS_NOT_EXISTED : MusicInfo.CONTENT_STATUS_EXISTED);
            mMusicInfo.setMediaInfo(mediaInfo);
            FlowPlayControl.getInstance().queryPlaying(mPlayingQueryCallback);
        }
    };

    private final QueryCallback<Boolean> mPlayingQueryCallback = new QueryCallback() {
        @Override
        public void onError(int code) {
            LogUtil.d(TAG, "onError():code = " + code);
        }

        @Override
        public void onSuccess(Object o) {
            boolean playing = (Boolean) o;
            LogUtil.d(TAG, "onSuccess():playing = " + playing);

            if (playing) {
                mMusicInfo.setPlayState(MusicInfo.PLAY_STATE_PLAY);
            } else {
                mMusicInfo.setPlayState(MusicInfo.PLAY_STATE_PAUSE);
            }
        }
    };

    private initSDKUtil(Context context) {
        mContext = context;
        mMusicInfo = MusicInfo.getInstance(mContext);
    }

    public static initSDKUtil getInstance(Context context) {
        if (null == mUtil) {
            synchronized (initSDKUtil.class) {
                if (null == mUtil) {
                    mUtil = new initSDKUtil(context);
                }
            }
        }
        return mUtil;
    }

    public void initSDK() {
        LogUtil.d(TAG, "initSDK():");

        //绑定爱趣听Service
        initBindService();

        //监听播放状态变化
        initQueryPlayState();

        //监听媒体信息变化
        initMediaStatusChange();

        //查询当前播放的媒体资源
        FlowPlayControl.getInstance().queryCurrent(mQueryCallback);

    }


    private final BindListener mBindListener = new BindListener() {
        @Override
        public void onServiceConnected() {
            LogUtil.d(TAG, "onServiceConnected():");

            FlowPlayControl.getInstance().queryCurrent(mQueryCallback);

        }

        @Override
        public void onBindDied() {
            LogUtil.d(TAG, "onBindDied():");
            mMusicInfo.setContentState(MusicInfo.CONTENT_STATUS_NOT_EXISTED);
        }

        @Override
        public void onServiceDisconnected() {
            LogUtil.d(TAG, "onServiceDisconnected():");
            mMusicInfo.setContentState(MusicInfo.CONTENT_STATUS_NOT_EXISTED);
        }

        @Override
        public void onError(int errorCode) {
            LogUtil.d(TAG, "onError():errorCode = " + errorCode);
        }
    };

    private void initBindService() {
        LogUtil.d(TAG, "initBindService()");

        FlowPlayControl.InitParams initParams = new FlowPlayControl.InitParams();
        initParams.setAutoRebind(true);
        FlowPlayControl.getInstance().init(initParams);
        FlowPlayControl.getInstance().addBindListener(mBindListener);
        FlowPlayControl.getInstance().bindPlayService(mContext);
    }

    private final PlayStateListener mPlayStateListener = new PlayStateListener() {
        @Override
        public void onStart() {
            LogUtil.d(TAG, "onStart():");
            mMusicInfo.setPlayState(MusicInfo.PLAY_STATE_PLAY);
        }

        @Override
        public void onPause() {
            LogUtil.d(TAG, "onPause():");
            mMusicInfo.setPlayState(MusicInfo.PLAY_STATE_PAUSE);
        }

        @Override
        public void onStop() {
            LogUtil.d(TAG, "onStop():");
            mMusicInfo.setPlayState(MusicInfo.PLAY_STATE_STOP);
        }

        @Override
        public void onProgress(String type, long current, long total) {
            LogUtil.d(TAG, "onProgress():type = " + type + " current = " + current + " total = " + total);

            mMusicInfo.setProcess(type, current, total);
        }

        @Override
        public void onBufferingStart() {
            LogUtil.d(TAG, "onBufferingStart():");
        }

        @Override
        public void onBufferingEnd() {
            LogUtil.d(TAG, "onBufferingEnd():");
        }

        @Override
        public void onAudioSessionId(int id) {
            LogUtil.d(TAG, "onAudioSessionId():id = " + id);
        }
    };

    private void initQueryPlayState() {
        LogUtil.d(TAG, "initQueryPlayState():");

        FlowPlayControl.getInstance().addPlayStateListener(mPlayStateListener);
    }

    private final MediaChangeListener mMediaChangeListener = new MediaChangeListener() {
        @Override
        public void onMediaChange(MediaInfo mediaInfo) {
            LogUtil.d(TAG, "onMediaChange():");

            mMusicInfo.setContentState(mediaInfo.getItemUUID() == null ? MusicInfo.CONTENT_STATUS_NOT_EXISTED : MusicInfo.CONTENT_STATUS_EXISTED);
            mMusicInfo.setMediaInfo(mediaInfo);
        }

        @Override
        public void onMediaChange(MediaInfo mediaInfo, NavigationInfo navigationInfo) {
            LogUtil.d(TAG, "onMediaChange():导航状态");
        }

        @Override
        public void onFavorChange(boolean isFavored, String ids) {
            LogUtil.d(TAG, "onFavorChange():isFavored = " + isFavored + " ids = " + ids);
        }

        @Override
        public void onModeChange(int mode) {
            LogUtil.d(TAG, "onModeChange():mode = " + mode);
        }

        @Override
        public void onPlayListChange() {
            LogUtil.d(TAG, "onPlayListChange():");
        }
    };

    private void initMediaStatusChange() {
        LogUtil.d(TAG, "initMediaStatusChange():");

        FlowPlayControl.getInstance().addMediaChangeListener(mMediaChangeListener);
    }

    public void destroyListener() {
        LogUtil.d(TAG, "destroyListener():");

        if (mMediaChangeListener != null) {
            FlowPlayControl.getInstance().removeMediaChangeListener(mMediaChangeListener);
        }

        if (mPlayStateListener != null) {
            FlowPlayControl.getInstance().removePlayStateListener(mPlayStateListener);
        }

        FlowPlayControl.getInstance().unbindPlayService(mContext);

        if (mBindListener != null) {
            FlowPlayControl.getInstance().removeBindListener(mBindListener);
        }
    }
}
