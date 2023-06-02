package com.adayo.app.launcher.music.manager.impl;

import android.graphics.Bitmap;
import android.nfc.Tag;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adayo.app.launcher.music.btmusic.BTMusicManager;
import com.adayo.app.launcher.music.btmusic.BTMusicModel;
import com.adayo.app.launcher.music.btmusic.IBTMusicCallBack;
import com.adayo.app.launcher.music.manager.IMusicPlayer;
import com.lt.library.util.LogUtil;

import java.util.Objects;

public class BtMusicPlayerImpl implements IMusicPlayer {
    private static final String TAG = "Launcher_"+BtMusicPlayerImpl.class.getSimpleName();
    private final MutableLiveData<Pair<String, String>> mTitleLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPlayStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mDurationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mProgressLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> mCoverLiveData = new MutableLiveData<>();

    @Override
    public void init() {
        BTMusicManager.getInstance().registerCallBack(new IBTMusicCallBack() {
            @Override
            public void onMusicStop() {
                LogUtil.d();
                if (!Objects.equals(PLAY_STATE_PAUSED, mPlayStateLiveData.getValue())) {
                    mPlayStateLiveData.setValue(PLAY_STATE_PAUSED);
                }
            }

            @Override
            public void onMusicPlay() {
                LogUtil.d();
                if (!Objects.equals(PLAY_STATE_PLAYED, mPlayStateLiveData.getValue())) {
                    mPlayStateLiveData.setValue(PLAY_STATE_PLAYED);
                    mCoverLiveData.setValue(null);
                }
            }

            @Override
            public void onId3Info(String artist, String album, String title) {
                LogUtil.d("artist: " + artist + ", album: " + album + ", title: " + title);
                Pair<String, String> pair = new Pair<>(title, artist);
                if (!Objects.equals(pair, mTitleLiveData.getValue())) {
                    mTitleLiveData.setValue(pair);
                }
            }

            @Override
            public void onPlayTimeStatus(long totalTime, long currentTime) {
                LogUtil.d("totalTime: " + totalTime + ", currentTime: " + currentTime);
                if (!Objects.equals(totalTime, mDurationLiveData.getValue())) {
                    mDurationLiveData.setValue(totalTime);
                }
                if (!Objects.equals(currentTime, mProgressLiveData.getValue())) {
                    mProgressLiveData.setValue(currentTime);
                }
            }

            @Override
            public void notifyA2dpDisconnect() {
                LogUtil.d();
                if (!Objects.equals(PLAY_STATE_STOPPED, mPlayStateLiveData.getValue())) {
                    LogUtil.d(TAG,"notifyA2dpDisconnect");
                    cleanBTMusicInfo();
                }
            }

            @Override
            public void notifyA2dpConnect() {
                LogUtil.d();
            }

            @Override
            public void notifyBluetoothDisconnect() {
                if (!Objects.equals(PLAY_STATE_STOPPED, mPlayStateLiveData.getValue())) {
                    LogUtil.d(TAG,"notifyBluetoothDisconnect");
                    cleanBTMusicInfo();
                }
            }
        });
        BTMusicModel musicModel = new BTMusicModel();
        musicModel.setBTMusicPresenter(BTMusicManager.getInstance());
        musicModel.init();
    }

    private void cleanBTMusicInfo() {
        mPlayStateLiveData.setValue(PLAY_STATE_STOPPED);
        mProgressLiveData.setValue(0L);
        mDurationLiveData.setValue(0L);
        mTitleLiveData.setValue(null);
        mCoverLiveData.setValue(null);

    }

    @Override
    public void toggleResumeOrPause() {
        BTMusicManager.getInstance().playOrPause();
    }

    @Override
    public void prev() {
        BTMusicManager.getInstance().playPrev();
    }

    @Override
    public void next() {
        BTMusicManager.getInstance().playNext();
    }

    @Override
    public LiveData<Pair<String, String>> getTitleLiveData() {
        return mTitleLiveData;
    }

    @Override
    public LiveData<Integer> getPlayStateLiveData() {
        return mPlayStateLiveData;
    }

    @Override
    public LiveData<Long> getDurationLiveData() {
        return mDurationLiveData;
    }

    @Override
    public LiveData<Long> getProgressLiveData() {
        return mProgressLiveData;
    }

    @Override
    public LiveData<Bitmap> getCoverLiveData() {
        return mCoverLiveData;
    }
}