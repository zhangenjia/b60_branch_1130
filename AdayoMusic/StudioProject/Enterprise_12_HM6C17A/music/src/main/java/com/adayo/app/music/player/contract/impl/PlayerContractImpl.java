package com.adayo.app.music.player.contract.impl;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adayo.app.music.data.repository.DataRepo;
import com.adayo.app.music.player.contract.IPlayerContract;
import com.adayo.proxy.media.bean.FileInfo;
import com.adayo.proxy.media.bean.MetadataInfo;
import com.adayo.proxy.media.bean.NodeInfo;
import com.adayo.proxy.media.bean.PlayerConfig;
import com.adayo.proxy.media.manager.AudioFunManager;
import com.adayo.proxy.media.util.ParseUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lt.library.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PlayerContractImpl implements
        IPlayerContract.IPlayerController,
        IPlayerContract.IPlayerInfoHolder {
    private final ExecutorService mThreadPool = new ThreadPoolExecutor(1,
                                                                       1,
                                                                       0L,
                                                                       TimeUnit.MILLISECONDS,
                                                                       new LinkedBlockingQueue<>(1024),
                                                                       new ThreadFactoryBuilder().setNameFormat("async-play-pool-%d").build(),
                                                                       new ThreadPoolExecutor.AbortPolicy());
    private final MutableLiveData<Integer> mRepeatModeLiveData = new MutableLiveData<>();
    private final MutableLiveData<NodeInfo> mNodeInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<MetadataInfo> mMetadataLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPlayStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPlayErrorCauseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mDurationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mProgressLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> mCoverLiveData = new MutableLiveData<>();

    @Override
    public void init(int audioZoneCode, PlayerConfig playerConfig, boolean isForceUpdate) {
        AudioFunManager.getFunction().deploy(audioZoneCode, playerConfig, isForceUpdate);
        bindListener();
    }

    @Override
    public void start(int audioZoneCode, List<FileInfo> playlist, FileInfo fileInfo) {
        List<FileInfo> fileInfos = new ArrayList<>(playlist);
        mThreadPool.execute(() -> {
            AudioFunManager.getFunction().setPlaylist(audioZoneCode, fileInfos);
            AudioFunManager.getFunction().start(audioZoneCode, fileInfo);
        });
    }

    @Override
    public void resume(int audioZoneCode, boolean fromUser) {
        AudioFunManager.getFunction().resume(audioZoneCode, fromUser);
    }

    @Override
    public void pause(int audioZoneCode, boolean fromUser) {
        AudioFunManager.getFunction().pause(audioZoneCode, fromUser);
    }

    @Override
    public void stop(int audioZoneCode, boolean fromUser) {
        AudioFunManager.getFunction().stop(audioZoneCode, fromUser);
    }

    @Override
    public void toggleResumeOrPause(int audioZoneCode) {
        AudioFunManager.getFunction().togglePlayState(audioZoneCode);
    }

    @Override
    public void prev(int audioZoneCode) {
        AudioFunManager.getFunction().prev(audioZoneCode);
    }

    @Override
    public void next(int audioZoneCode) {
        AudioFunManager.getFunction().next(audioZoneCode);
    }

    @Override
    public void fastRewindStart(int audioZoneCode) {
        AudioFunManager.getFunction().fastRewindStart(audioZoneCode);
    }

    @Override
    public void fastRewindEnd(int audioZoneCode) {
        AudioFunManager.getFunction().fastRewindEnd(audioZoneCode);
    }

    @Override
    public void fastForwardStart(int audioZoneCode) {
        AudioFunManager.getFunction().fastForwardStart(audioZoneCode);
    }

    @Override
    public void fastForwardEnd(int audioZoneCode) {
        AudioFunManager.getFunction().fastForwardEnd(audioZoneCode);
    }

    @Override
    public void setProgress(int audioZoneCode, long progress) {
        AudioFunManager.getFunction().setProgress(audioZoneCode, progress);
    }

    @Override
    public void setRepeatMode(int audioZoneCode, int repeatMode) {
        AudioFunManager.getFunction().setRepeatMode(audioZoneCode, repeatMode);
    }

    @Override
    public void toggleRepeatMode(int audioZoneCode) {
        AudioFunManager.getFunction().toggleRepeatMode(audioZoneCode);
    }

    @Override
    public void requestAudioFocus(int audioZoneCode) {
        AudioFunManager.getFunction().requestAudioFocus(audioZoneCode);
    }

    @Override
    public LiveData<Integer> getRepeatModeLiveData() {
        return mRepeatModeLiveData;
    }

    @Override
    public LiveData<NodeInfo> getNodeInfoLiveData() {
        return mNodeInfoLiveData;
    }

    @Override
    public LiveData<MetadataInfo> getMetadataLiveData() {
        return mMetadataLiveData;
    }

    @Override
    public LiveData<Integer> getPlayStateLiveData() {
        return mPlayStateLiveData;
    }

    @Override
    public LiveData<Integer> getPlayErrorCauseLiveData() {
        return mPlayErrorCauseLiveData;
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

    private void bindListener() {
        DataRepo.getInstance().needBreakState(dataResult -> {
            LogUtil.d("breakStateResult: " + dataResult);
        });
        DataRepo.getInstance().needRepeatMode(dataResult -> {
            LogUtil.d("repeatModeResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            mRepeatModeLiveData.postValue(dataResult.getResult());
        });
        DataRepo.getInstance().needNodeInfo(dataResult -> {
            LogUtil.d("nodeInfoResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            NodeInfo nodeInfo = dataResult.getResult();
            mNodeInfoLiveData.postValue(nodeInfo);
            Bitmap bitmap;
            if (nodeInfo == null) {
                bitmap = null;
            } else {
                ParseUtil parseUtil = new ParseUtil.Builder(nodeInfo.getNodePath()).addEmbeddedPicture()
                                                                                   .build();
                bitmap = parseUtil.getCover(ParseUtil.METADATA_KEY_EMBEDDED_PICTURE);
            }
            mCoverLiveData.postValue(bitmap);
        });
        DataRepo.getInstance().needMetadataInfo(dataResult -> {
            LogUtil.d("metadataInfoResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            MetadataInfo metadataInfo = dataResult.getResult();
            mMetadataLiveData.postValue(metadataInfo);
        });
        DataRepo.getInstance().needPlayState(dataResult -> {
            LogUtil.d("playStateResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            mPlayStateLiveData.postValue(dataResult.getResult());
        });
        DataRepo.getInstance().needPlayErrorCause(dataResult -> {
            LogUtil.d("playErrorCauseResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            mPlayErrorCauseLiveData.postValue(dataResult.getResult());
        });
        DataRepo.getInstance().needDuration(dataResult -> {
            LogUtil.d("durationResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            mDurationLiveData.postValue(dataResult.getResult());
        });
        DataRepo.getInstance().needProgress(dataResult -> {
            LogUtil.d("progressResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            mProgressLiveData.postValue(dataResult.getResult());
        });
    }
}
