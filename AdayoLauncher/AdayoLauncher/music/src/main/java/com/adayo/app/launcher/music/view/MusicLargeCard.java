package com.adayo.app.launcher.music.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.music.R;
import com.adayo.app.launcher.music.databinding.CardMusicLargeBinding;
import com.adayo.app.launcher.music.manager.IMusicPlayer;
import com.adayo.app.launcher.music.manager.MusicManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lt.library.util.LogUtil;

import java.util.Objects;

public class MusicLargeCard extends ConstraintLayout implements IViewBase {
    private CardMusicLargeBinding mViewBinding;
    private Observer<String> mAudioIdObserver;
    private Observer<Pair<String, String>> mTitleObserver;
    private Observer<Integer> mPlayStateObserver;
    private Observer<Long> mDurationObserver;
    private Observer<Long> mProgressObserver;
    private Observer<Bitmap> mCoverObserver;

    public MusicLargeCard(Context context) {
        this(context, null);
    }

    public MusicLargeCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicLargeCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View root = View.inflate(getContext(), R.layout.card_music_large, this);
        mViewBinding = CardMusicLargeBinding.bind(root);
    }

    @Override
    public View initCardView(String id, String type, String type1) {
        LogUtil.d("id: " + id + ", type: " + type + ", type1: " + type1);
        WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
            @Override
            public void setWallPaper(Bitmap bitmap) {

            }

            @Override
            public void resumeDefault() {

            }

            @Override
            public void deleteWallPaper() {

            }


        });


        MusicManager.getInstance().init(this);
        initView();
        initData();
        initEvent();
        return mViewBinding.getRoot();
    }

    @Override
    public void unInitCardView(String id, String type) {
        LogUtil.d("id: " + id + ", type: " + type);
        MusicManager.getInstance().uninit(this);
        freeEvent();
        freeData();
        freeView();
    }

    @Override
    public void releaseResource() {
        LogUtil.d();
    }

    @Override
    public void playAnimation(String id, int delay) {
        LogUtil.d("id: " + id + ", delay: " + delay);
    }

    @Override
    public void onConfigurationChanged() {
        String audioId = MusicManager.getInstance().getAudioIdLiveData().getValue();
        LogUtil.d("audioId: " + audioId);
        if (audioId == null) {
            mViewBinding.tvMusicSourceName.setText(R.string.source_bt);
            return;
        }
        switch (Objects.requireNonNull(audioId)) {
            case AdayoSource.ADAYO_SOURCE_USB:
                mViewBinding.tvMusicSourceName.setText(R.string.source_lo);
                break;
            case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                mViewBinding.tvMusicSourceName.setText(R.string.source_bt);
                break;
            default:
                break;
        }
        Pair<String, String> title = MusicManager.getInstance().getTitleLiveData().getValue();
        LogUtil.d("title: " + title);
        if (title == null) {
            mViewBinding.tvMusicTitle.setText(R.string.no_play);
        } else {
            String fileName = title.first;
            String artist = title.second;
            mViewBinding.tvMusicTitle.setText((TextUtils.isEmpty(fileName) ? getContext().getString(R.string.unknown) : fileName) + "-" + (TextUtils.isEmpty(artist) ? getContext().getString(R.string.unknown) : artist));
        }
    }

    @Override
    public void launcherLoadComplete() {
        LogUtil.d();
    }

    @Override
    public void launcherAnimationUpdate(int i) {
        LogUtil.d("i: " + i);
    }

    private void initView() {
        IWindowViewManager windowViewManager = AAOP_HSkin.getWindowViewManager();
        windowViewManager.addWindowView(mViewBinding.getRoot());
        ISkinManager skinManager = AAOP_HSkin.getInstance();
        skinManager.applySkin(mViewBinding.getRoot(), true);

        AAOP_HSkin.with(mViewBinding.getRoot())
                  .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_bigcard)
                  .applySkin(false);

        AAOP_HSkin.with(mViewBinding.sbMusicProgress)
                .addViewAttrs(AAOP_HSkin.ATTR_PROGRESS_DRAWABLE, R.drawable.music_progress)
                .applySkin(false);

        mViewBinding.tvMusicSourceName.setText(R.string.source_bt);
        mViewBinding.ivMusicCover.setImageResource(R.drawable.module_music_frame_yinyue);
        mViewBinding.tvMusicTitle.setText(R.string.no_play);
        mViewBinding.sbMusicProgress.setEnabled(false);
        mViewBinding.imgBtnMusicPlay.setEnabled(false);
        mViewBinding.imgBtnMusicPrev.setEnabled(false);
        mViewBinding.imgBtnMusicNext.setEnabled(false);
    }

    private void initData() {
        mAudioIdObserver = audioId -> {
            LogUtil.d("audioId: " + audioId);
            switch (audioId) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    mViewBinding.tvMusicSourceName.setText(R.string.source_lo);
                    break;
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    mViewBinding.tvMusicSourceName.setText(R.string.source_bt);
                    break;
                default:
                    break;
            }
        };
        MusicManager.getInstance().getAudioIdLiveData().observeForever(mAudioIdObserver);
        mTitleObserver = title -> {
            LogUtil.d("title: " + title);
            if (title == null) {
                mViewBinding.tvMusicTitle.setText(R.string.no_play);
            } else {
                String fileName = title.first;
                String artist = title.second;
                mViewBinding.tvMusicTitle.setText((TextUtils.isEmpty(fileName) ? getContext().getString(R.string.unknown) : fileName) + "-" + (TextUtils.isEmpty(artist) ? getContext().getString(R.string.unknown) : artist));
            }
            mViewBinding.tvMusicTitle.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        };
        MusicManager.getInstance().getTitleLiveData().observeForever(mTitleObserver);
        mPlayStateObserver = playState -> {
            LogUtil.d("playState: " + playState);
            if (IMusicPlayer.PLAY_STATE_STOPPED == playState) {
                Glide.with(this).clear(mViewBinding.ivMusicCover);
                mViewBinding.imgBtnMusicPlay.setEnabled(false);
                mViewBinding.imgBtnMusicPrev.setEnabled(false);
                mViewBinding.imgBtnMusicNext.setEnabled(false);
            } else {
                mViewBinding.imgBtnMusicPlay.setEnabled(true);
                mViewBinding.imgBtnMusicPrev.setEnabled(true);
                mViewBinding.imgBtnMusicNext.setEnabled(true);
            }
            mViewBinding.imgBtnMusicPlay.setActivated(IMusicPlayer.PLAY_STATE_PLAYED == playState);
        };
        MusicManager.getInstance().getPlayStateLiveData().observeForever(mPlayStateObserver);
        mDurationObserver = duration -> {
            LogUtil.d("duration: " + duration);
            mViewBinding.sbMusicProgress.setMax(duration.intValue());
        };
        MusicManager.getInstance().getDurationLiveData().observeForever(mDurationObserver);
        mProgressObserver = progress -> {
            LogUtil.d("progress:" + progress);
            mViewBinding.sbMusicProgress.setProgress(progress.intValue());
        };
        MusicManager.getInstance().getProgressLiveData().observeForever(mProgressObserver);
        mCoverObserver = cover -> {
            LogUtil.d();
            Glide.with(this)
                 .asDrawable()
                 .load(cover)
                 .transition(DrawableTransitionOptions.withCrossFade(getResources().getInteger(android.R.integer.config_longAnimTime)))
                 .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                 .apply(RequestOptions.placeholderOf(R.drawable.module_music_frame_yinyue))
                 .into(mViewBinding.ivMusicCover);
        };
        MusicManager.getInstance().getCoverLiveData().observeForever(mCoverObserver);
    }

    private void initEvent() {
        mViewBinding.imgBtnMusicPrev.setOnClickListener(v -> {
            MusicManager.getInstance().requirePrev();
        });
        mViewBinding.imgBtnMusicNext.setOnClickListener(v -> {
            MusicManager.getInstance().requireNext();
        });
        mViewBinding.imgBtnMusicPlay.setOnClickListener(v -> {
            MusicManager.getInstance().requireToggleResumeOrPause();
        });
    }

    private void freeEvent() {
        mViewBinding.imgBtnMusicPrev.setOnClickListener(null);
        mViewBinding.imgBtnMusicNext.setOnClickListener(null);
        mViewBinding.imgBtnMusicPlay.setOnClickListener(null);
    }

    private void freeData() {
        MusicManager.getInstance().getAudioIdLiveData().removeObserver(mAudioIdObserver);
        MusicManager.getInstance().getTitleLiveData().removeObserver(mTitleObserver);
        MusicManager.getInstance().getDurationLiveData().removeObserver(mDurationObserver);
        MusicManager.getInstance().getCoverLiveData().removeObserver(mCoverObserver);
        MusicManager.getInstance().getPlayStateLiveData().removeObserver(mPlayStateObserver);
        MusicManager.getInstance().getProgressLiveData().removeObserver(mProgressObserver);
    }

    private void freeView() {
    }
}
