package com.adayo.app.launcher.music.manager.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adayo.app.launcher.music.manager.IMusicPlayer;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.lt.library.util.LogUtil;
import com.lt.library.util.context.ContextUtil;
import com.lt.library.util.json.fastjson.FastJsonUtil;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class LoMusicPlayerImpl implements IMusicPlayer {
    private static final int SHARE_INFO_ID_LO_MUSIC_INFO = 0;
    private static final String SHARE_INFO_KEY_PATH_FROM_MUSIC_INFO = "path";
    private static final String SHARE_INFO_KEY_DURATION_FROM_MUSIC_INFO = "durationTime";
    private static final String SHARE_INFO_KEY_ARTIST_FROM_MUSIC_INFO = "artist";
    private static final String SHARE_INFO_KEY_FILENAME_FROM_MUSIC_INFO = "name";
    private static final String SHARE_INFO_KEY_PLAY_STATE_FROM_MUSIC_INFO = "status";
    private static final int SHARE_INFO_ID_LO_MUSIC_NOW_TIME = 19;
    private static final String PERMISSION_BIND_LO_MUSIC_SERVICE = "adayo.permission.BIND_MEDIA_SERVICE";
    private static final String INTENT_ACTION_LO_AUDIO_PLAYER_CTRL = "adayo.intent.action.AUDIO_PLAYER_CTRL";
    private static final String INTENT_EXTRA_KEY_LO_CTRL_TYPE = "play_type";
    private static final String INTENT_EXTRA_VALUE_LO_RESUME = "resume";
    private static final String INTENT_EXTRA_VALUE_LO_PAUSE = "pause";
    private static final String INTENT_EXTRA_VALUE_LO_RESUME_OR_PAUSE = "resume_or_pause";
    private static final String INTENT_EXTRA_VALUE_LO_PREV = "prev";
    private static final String INTENT_EXTRA_VALUE_LO_NEXT = "next";
    private final MutableLiveData<Pair<String, String>> mTitleLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mPlayStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mDurationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Long> mProgressLiveData = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> mCoverLiveData = new MutableLiveData<>();
    private final ExecutorService mMetadataParseThreadPool = new ThreadPoolExecutor(1,
                                                                                    1,
                                                                                    0L,
                                                                                    TimeUnit.MILLISECONDS,
                                                                                    new LinkedBlockingQueue<>(1),
                                                                                    new ThreadPoolExecutor.DiscardOldestPolicy());
    private String mMusicPath;

    @Override
    public void init() {
        ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
        shareDataManager.registerShareDataListener(SHARE_INFO_ID_LO_MUSIC_INFO, (i, s) -> onMusicInfoChanged(true, i, s));
        onMusicInfoChanged(false, SHARE_INFO_ID_LO_MUSIC_INFO, shareDataManager.getShareData(SHARE_INFO_ID_LO_MUSIC_INFO));
        shareDataManager.registerShareDataListener(SHARE_INFO_ID_LO_MUSIC_NOW_TIME, (i, s) -> onMusicNowTimeChanged(true, i, s));
        onMusicNowTimeChanged(false, SHARE_INFO_ID_LO_MUSIC_NOW_TIME, shareDataManager.getShareData(SHARE_INFO_ID_LO_MUSIC_NOW_TIME));
    }

    @Override
    public void toggleResumeOrPause() {
        sendLoMusicCtrl(INTENT_EXTRA_VALUE_LO_RESUME_OR_PAUSE);
    }

    @Override
    public void prev() {
        sendLoMusicCtrl(INTENT_EXTRA_VALUE_LO_PREV);
    }

    @Override
    public void next() {
        sendLoMusicCtrl(INTENT_EXTRA_VALUE_LO_NEXT);
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

    private void onMusicInfoChanged(boolean isAsync, int id, String content) {
        LogUtil.d("isAsync: " + isAsync + ", id: " + id + ", content: " + content);
        if (id != SHARE_INFO_ID_LO_MUSIC_INFO) {
            return;
        }
        if (content == null) {
            LogUtil.w("related modules have not shared info yet");
            return;
        }
        Map<String, Object> map = FastJsonUtil.jsonToMap(content);
        String path;
        if (map.containsKey(SHARE_INFO_KEY_PATH_FROM_MUSIC_INFO)) {
            Object o = map.get(SHARE_INFO_KEY_PATH_FROM_MUSIC_INFO);
            path = o == null ? null : (String) o;
            if (path != null) {
                if (!TextUtils.equals(path, mMusicPath)) {
                    if (path.isEmpty()) {
                        mTitleLiveData.setValue(null);
                        mCoverLiveData.setValue(null);
                    } else {
                        Pair<String, String> pair = new Pair<>((String) map.get(SHARE_INFO_KEY_FILENAME_FROM_MUSIC_INFO), (String) map.get(SHARE_INFO_KEY_ARTIST_FROM_MUSIC_INFO));
                        if (!Objects.equals(pair, mTitleLiveData.getValue())) {
                            mTitleLiveData.setValue(pair);
                        }
                        mMetadataParseThreadPool.execute(() -> {
                            LogUtil.d("async parse metadataInfo start");
                            Bitmap bitmap = parseAlbumCover(path);
                            LogUtil.d("async parse metadataInfo end");
                            synchronized (LoMusicPlayerImpl.this) {
                                boolean checkExpired = !Objects.equals(path, mMusicPath);
                                if (!checkExpired) {
                                    LogUtil.d("async parse metadataInfo finish, will be update playContent");
                                    mCoverLiveData.postValue(bitmap);
                                } else {
                                    LogUtil.d("async parse metadataInfo expired");
                                }
                            }
                        });
                    }
                    synchronized (this) {
                        mMusicPath = path;
                    }
                }
            }
        }
        String durationTime;
        if (map.containsKey(SHARE_INFO_KEY_DURATION_FROM_MUSIC_INFO)) {
            Object o = map.get(SHARE_INFO_KEY_DURATION_FROM_MUSIC_INFO);
            durationTime = o == null ? null : (String) o;
            long duration = stringToMillis(durationTime);
            if (!Objects.equals(duration, mDurationLiveData.getValue())) {
                mDurationLiveData.setValue(duration);
            }
        }
        String status;
        if (map.containsKey(SHARE_INFO_KEY_PLAY_STATE_FROM_MUSIC_INFO)) {
            Object o = map.get(SHARE_INFO_KEY_PLAY_STATE_FROM_MUSIC_INFO);
            status = o == null ? null : (String) o;
            Map<String, Integer> playStateMap = new ArrayMap<>();
            playStateMap.put("stop", PLAY_STATE_STOPPED);
            playStateMap.put("playing", PLAY_STATE_PLAYED);
            playStateMap.put("pause", PLAY_STATE_PAUSED);
            Integer playState = playStateMap.get(status);
            if (!Objects.equals(playState, mPlayStateLiveData.getValue())) {
                mPlayStateLiveData.setValue(playState);
            }
        }
    }

    private void onMusicNowTimeChanged(boolean isAsync, int id, String content) {
        LogUtil.d("isAsync: " + isAsync + ", id: " + id + ", content: " + content);
        if (id != SHARE_INFO_ID_LO_MUSIC_NOW_TIME) {
            return;
        }
        if (content == null) {
            LogUtil.w("related modules have not shared info yet");
            return;
        }
        Map<String, Object> map = FastJsonUtil.jsonToMap(content);
        Object currentTime = map.get("currentTime");
        if (currentTime != null) {
            long progress = stringToMillis((String) currentTime);
            if (!Objects.equals(progress, mProgressLiveData.getValue())) {
                mProgressLiveData.setValue(progress);
            }
        }
    }

    private long stringToMillis(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }
        String[] arr = string.split(":");
        Duration duration = Duration.ZERO;
        if (arr.length == 2) {
            string = "PT" + arr[0] + "M" + arr[1] + "S";
            duration = Duration.parse(string);
        } else if (arr.length == 3) {
            string = "PT" + arr[0] + "H" + arr[1] + "M" + arr[2] + "S";
            duration = Duration.parse(string);
        }
        return duration.toMillis();
    }

    private Bitmap parseAlbumCover(String path) {
        FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
        try {
            fmmr.setDataSource(path);// TODO: 2021/10/4 待验证file为空文件的情况, 是否如P层所言闪退
            byte[] embeddedPicture = fmmr.getEmbeddedPicture();
            if (embeddedPicture == null || embeddedPicture.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.length);
        } catch (RuntimeException e) {
            LogUtil.w(e + ", path = " + path);
            return null;
        } finally {
            fmmr.release();
        }
    }

    private void sendLoMusicCtrl(String ctrlType) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.setAction(INTENT_ACTION_LO_AUDIO_PLAYER_CTRL);
        intent.putExtra(INTENT_EXTRA_KEY_LO_CTRL_TYPE, ctrlType);
        ContextUtil.getAppContext().sendBroadcast(intent, PERMISSION_BIND_LO_MUSIC_SERVICE);
    }
}