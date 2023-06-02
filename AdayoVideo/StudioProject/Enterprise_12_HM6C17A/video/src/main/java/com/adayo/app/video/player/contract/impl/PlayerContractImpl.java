package com.adayo.app.video.player.contract.impl;

import android.util.Pair;
import android.view.Surface;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adayo.app.video.data.repository.DataRepo;
import com.adayo.app.video.player.contract.IPlayerContract;
import com.adayo.proxy.media.bean.FileInfo;
import com.adayo.proxy.media.bean.NodeInfo;
import com.adayo.proxy.media.bean.PlayerConfig;
import com.adayo.proxy.media.manager.VideoFunManager;
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
    private final MutableLiveData<NodeInfo> mNodeInfoLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPlayStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPlayErrorCauseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mDurationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mProgressLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<Integer, Integer>> mVideoSizeLiveData = new MutableLiveData<>();

    @Override
    public void init(int audioZoneCode, PlayerConfig playerConfig, boolean isForceUpdate) {
        VideoFunManager.getFunction().deploy(audioZoneCode, playerConfig, isForceUpdate);
        bindListener();
    }

    @Override
    public void start(int audioZoneCode, List<FileInfo> playlist, FileInfo fileInfo) {
        List<FileInfo> fileInfos = new ArrayList<>(playlist);
        mThreadPool.execute(() -> {
            VideoFunManager.getFunction().setPlaylist(audioZoneCode, fileInfos);
            VideoFunManager.getFunction().start(audioZoneCode, fileInfo);
        });
    }

    @Override
    public void resume(int audioZoneCode, boolean fromUser) {
        VideoFunManager.getFunction().resume(audioZoneCode, fromUser);
    }

    @Override
    public void pause(int audioZoneCode, boolean fromUser) {
        VideoFunManager.getFunction().pause(audioZoneCode, fromUser);
    }

    @Override
    public void stop(int audioZoneCode, boolean fromUser) {
        VideoFunManager.getFunction().stop(audioZoneCode, fromUser);
    }

    @Override
    public void toggleResumeOrPause(int audioZoneCode) {
        VideoFunManager.getFunction().togglePlayState(audioZoneCode);
    }

    @Override
    public void prev(int audioZoneCode) {
        VideoFunManager.getFunction().prev(audioZoneCode);
    }

    @Override
    public void next(int audioZoneCode) {
        VideoFunManager.getFunction().next(audioZoneCode);
    }

    @Override
    public void fastRewindStart(int audioZoneCode) {
        VideoFunManager.getFunction().fastRewindStart(audioZoneCode);
    }

    @Override
    public void fastRewindEnd(int audioZoneCode) {
        VideoFunManager.getFunction().fastRewindEnd(audioZoneCode);
    }

    @Override
    public void fastForwardStart(int audioZoneCode) {
        VideoFunManager.getFunction().fastForwardStart(audioZoneCode);
    }

    @Override
    public void fastForwardEnd(int audioZoneCode) {
        VideoFunManager.getFunction().fastForwardEnd(audioZoneCode);
    }

    @Override
    public void setProgress(int audioZoneCode, long progress) {
        VideoFunManager.getFunction().setProgress(audioZoneCode, progress);
    }

    @Override
    public void setRepeatMode(int audioZoneCode, int repeatMode) {
        VideoFunManager.getFunction().setRepeatMode(audioZoneCode, repeatMode);
    }

    @Override
    public void toggleRepeatMode(int audioZoneCode) {
        VideoFunManager.getFunction().toggleRepeatMode(audioZoneCode);
    }

    @Override
    public void setSurface(int audioZoneCode, Surface surface) {
        VideoFunManager.getFunction().setSurface(audioZoneCode, surface);
    }

    @Override
    public void requestAudioFocus(int audioZoneCode) {
        VideoFunManager.getFunction().requestAudioFocus(audioZoneCode);
    }

    @Override
    public LiveData<NodeInfo> getNodeInfoLiveData() {
        return mNodeInfoLiveData;
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
    public LiveData<Pair<Integer, Integer>> getVideoSizeLiveData() {
        return mVideoSizeLiveData;
    }

    private void bindListener() {
        DataRepo.getInstance().needBreakState(dataResult -> {
            LogUtil.d("breakStateResult: " + dataResult);
        });
        DataRepo.getInstance().needNodeInfo(dataResult -> {
            LogUtil.d("nodeInfoResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            NodeInfo nodeInfo = dataResult.getResult();
            mNodeInfoLiveData.postValue(nodeInfo);
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
        DataRepo.getInstance().needVideoSize(dataResult -> {
            LogUtil.d("videoSizeResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            mVideoSizeLiveData.postValue(dataResult.getResult());
        });
    }
}
