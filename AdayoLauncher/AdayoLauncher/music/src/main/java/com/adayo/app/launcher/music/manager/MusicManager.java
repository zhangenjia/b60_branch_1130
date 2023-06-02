package com.adayo.app.launcher.music.manager;

import android.graphics.Bitmap;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.music.manager.impl.BtMusicPlayerImpl;
import com.adayo.app.launcher.music.manager.impl.LoMusicPlayerImpl;
import com.adayo.app.launcher.music.util.HexStateUtil;
import com.adayo.app.launcher.music.view.MusicLargeCard;
import com.adayo.app.launcher.music.view.MusicSmallCard;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.lt.library.util.LogUtil;
import com.lt.library.util.json.fastjson.FastJsonUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicManager {
    private static final int SHARE_INFO_ID_SOURCE = 14;
    private static final String SHARE_INFO_KEY_AUDIO_ID_FROM_SOURCE = "AudioID";
    private static final int CARD_FLAG_NONE = 0x0;
    private static final int CARD_FLAG_SMALL = 0x1;
    private static final int CARD_FLAG_LARGE = 0x2;
    private final MutableLiveData<String> mAudioIdLiveData = new MutableLiveData<>();
    private final LiveData<Pair<String, String>> mTitleLiveData;
    private final LiveData<Integer> mPlayStateLiveData;
    private final LiveData<Long> mDurationLiveData;
    private final LiveData<Long> mProgressLiveData;
    private final LiveData<Bitmap> mCoverLiveData;
    private final IMusicPlayer mLoMusicPlayer = new LoMusicPlayerImpl();
    private final IMusicPlayer mBtMusicPlayer = new BtMusicPlayerImpl();
    private final AtomicBoolean mInitialized = new AtomicBoolean();
    private int mCardFlag = CARD_FLAG_NONE;
    private IShareDataListener mSourceListener;

    {
        mTitleLiveData = Transformations.switchMap(mAudioIdLiveData, input -> {
            switch (input) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    return mLoMusicPlayer.getTitleLiveData();
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    return mBtMusicPlayer.getTitleLiveData();
                default:
                    throw new IllegalArgumentException("unexpected value: " + input);
            }
        });
        mPlayStateLiveData = Transformations.switchMap(mAudioIdLiveData, input -> {
            switch (input) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    return mLoMusicPlayer.getPlayStateLiveData();
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    return mBtMusicPlayer.getPlayStateLiveData();
                default:
                    throw new IllegalArgumentException("unexpected value: " + input);
            }
        });
        mDurationLiveData = Transformations.switchMap(mAudioIdLiveData, input -> {
            switch (input) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    return mLoMusicPlayer.getDurationLiveData();
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    return mBtMusicPlayer.getDurationLiveData();
                default:
                    throw new IllegalArgumentException("unexpected value: " + input);
            }
        });
        mProgressLiveData = Transformations.switchMap(mAudioIdLiveData, input -> {
            switch (input) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    return mLoMusicPlayer.getProgressLiveData();
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    return mBtMusicPlayer.getProgressLiveData();
                default:
                    throw new IllegalArgumentException("unexpected value: " + input);
            }
        });
        mCoverLiveData = Transformations.switchMap(mAudioIdLiveData, input -> {
            switch (input) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    return mLoMusicPlayer.getCoverLiveData();
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    return mBtMusicPlayer.getCoverLiveData();
                default:
                    throw new IllegalArgumentException("unexpected value: " + input);
            }
        });
    }

    private MusicManager() {
        mLoMusicPlayer.init();
        mBtMusicPlayer.init();
    }

    public static MusicManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void init(IViewBase viewBase) {
        LogUtil.d("old cardFlag: " + mCardFlag);
        if (viewBase instanceof MusicSmallCard) {
            if (HexStateUtil.has(mCardFlag, CARD_FLAG_SMALL)) return;
            mCardFlag = HexStateUtil.add(mCardFlag, CARD_FLAG_SMALL);
        } else if (viewBase instanceof MusicLargeCard) {
            if (HexStateUtil.has(mCardFlag, CARD_FLAG_LARGE)) return;
            mCardFlag = HexStateUtil.add(mCardFlag, CARD_FLAG_LARGE);
        }
        LogUtil.d("new cardFlag: " + mCardFlag);
        if (mCardFlag == CARD_FLAG_NONE) {
            return;
        }
        if (!mInitialized.compareAndSet(false, true)) {
            return;
        }
        ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
        if (mSourceListener == null) {
            mSourceListener = (i, s) -> MusicManager.this.onSourceChanged(true, i, s);
        }
        shareDataManager.registerShareDataListener(SHARE_INFO_ID_SOURCE, mSourceListener);
        onSourceChanged(false, SHARE_INFO_ID_SOURCE, shareDataManager.getShareData(SHARE_INFO_ID_SOURCE));
    }

    public void uninit(IViewBase viewBase) {
        LogUtil.d("old cardFlag: " + mCardFlag);
        if (viewBase instanceof MusicSmallCard) {
            if (!HexStateUtil.has(mCardFlag, CARD_FLAG_SMALL)) return;
            mCardFlag = HexStateUtil.del(mCardFlag, CARD_FLAG_SMALL);
        } else if (viewBase instanceof MusicLargeCard) {
            if (!HexStateUtil.has(mCardFlag, CARD_FLAG_LARGE)) return;
            mCardFlag = HexStateUtil.del(mCardFlag, CARD_FLAG_LARGE);
        }
        LogUtil.d("new cardFlag: " + mCardFlag);
        if (mCardFlag != CARD_FLAG_NONE) {
            return;
        }
        ShareDataManager.getShareDataManager().unregisterShareDataListener(SHARE_INFO_ID_SOURCE, mSourceListener);
        mInitialized.set(false);
    }

    public void requireToggleResumeOrPause() {
        LogUtil.d();
        IMusicPlayer musicPlayer = getMusicPlayer();
        if (musicPlayer == null) {
            return;
        }
        musicPlayer.toggleResumeOrPause();
    }

    public void requirePrev() {
        LogUtil.d();
        IMusicPlayer musicPlayer = getMusicPlayer();
        if (musicPlayer == null) {
            return;
        }
        musicPlayer.prev();
    }

    public void requireNext() {
        LogUtil.d();
        IMusicPlayer musicPlayer = getMusicPlayer();
        if (musicPlayer == null) {
            return;
        }
        musicPlayer.next();
    }

    public LiveData<String> getAudioIdLiveData() {
        return mAudioIdLiveData;
    }

    public LiveData<Pair<String, String>> getTitleLiveData() {
        return mTitleLiveData;
    }

    public LiveData<Integer> getPlayStateLiveData() {
        return mPlayStateLiveData;
    }

    public LiveData<Long> getDurationLiveData() {
        return mDurationLiveData;
    }

    public LiveData<Long> getProgressLiveData() {
        return mProgressLiveData;
    }

    public LiveData<Bitmap> getCoverLiveData() {
        return mCoverLiveData;
    }

    private void onSourceChanged(boolean isAsync, int id, String content) {
        LogUtil.d("isAsync: " + isAsync + ", id: " + id + ", content: " + content);
        if (id != SHARE_INFO_ID_SOURCE) {
            return;
        }
        if (content == null) {
            LogUtil.w("related modules have not shared info yet");
            return;
        }
        Object audioId = FastJsonUtil.jsonToMap(content).get(SHARE_INFO_KEY_AUDIO_ID_FROM_SOURCE);
        if (audioId == null) {
            LogUtil.w("related modules have not shared info yet");
            return;
        }
        if (!Objects.equals(audioId, mAudioIdLiveData.getValue())) {
            switch ((String) audioId) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    mAudioIdLiveData.setValue(AdayoSource.ADAYO_SOURCE_USB);
                    break;
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    mAudioIdLiveData.setValue(AdayoSource.ADAYO_SOURCE_BT_AUDIO);
                    break;
                default:
                    break;
            }
        }
    }

    @Nullable
    private IMusicPlayer getMusicPlayer() {
        String audioId = mAudioIdLiveData.getValue();
        LogUtil.d("current audioId: " + audioId);
        if (audioId == null) {
            return null;
        }
        IMusicPlayer musicPlayer;
        switch (audioId) {
            case AdayoSource.ADAYO_SOURCE_USB:
                musicPlayer = mLoMusicPlayer;
                break;
            case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                musicPlayer = mBtMusicPlayer;
                break;
            default:
                musicPlayer = null;
                break;
        }
        return musicPlayer;
    }

    private static class InstanceHolder {
        private static final MusicManager INSTANCE = new MusicManager();
    }
}
