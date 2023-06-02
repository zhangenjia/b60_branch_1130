package com.adayo.app.launcher.music.view;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.music.R;
import com.adayo.app.launcher.music.databinding.CardMusicSmallBinding;
import com.adayo.app.launcher.music.manager.IMusicPlayer;
import com.adayo.app.launcher.music.manager.MusicManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.manager.ISkinManager;
import com.adayo.proxy.aaop_hskin.view.IWindowViewManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.lt.library.util.LogUtil;

import java.util.Objects;

public class MusicSmallCard extends ConstraintLayout implements IViewBase {
    private static final int PLAY_ANIMATION = 1;
    private final Handler mHandler = new Handler(new MyHandlerCallback());
    private CardMusicSmallBinding mViewBinding;
    private Observer<String> mAudioIdObserver;
    private Observer<Pair<String, String>> mTitleObserver;
    private Observer<Integer> mPlayStateObserver;
    private String mType1;

    public MusicSmallCard(Context context) {
        this(context, null);
    }

    public MusicSmallCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicSmallCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View root = View.inflate(getContext(), R.layout.card_music_small, this);
        mViewBinding = CardMusicSmallBinding.bind(root);
    }

    @Override
    public View initCardView(String id, String type, String type1) {
        LogUtil.d("id: " + id + ", type: " + type + ", type1: " + type1);
        mType1 = type1;
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
        mHandler.sendEmptyMessageDelayed(PLAY_ANIMATION, delay);
    }

    @Override
    public void onConfigurationChanged() {
        String audioId = MusicManager.getInstance().getAudioIdLiveData().getValue();
        LogUtil.d("audioId: " + audioId);
        if (audioId == null) {
            mViewBinding.tvMusicSourceName.setText(R.string.source_bt);
            return;
        }
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
        if (Objects.equals(mType1, DRAG_TO_INITCARD)) {//换肤 1
            AAOP_HSkin.with(mViewBinding.lavMusicBg)
                      .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.music45)
                      .applySkin(false);
        }
        AAOP_HSkin.with(mViewBinding.ivMusicFrame)
                  .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
                  .applySkin(false);
        AAOP_HSkin.with(mViewBinding.imgBtnMusicPlay)
                  .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.selector_music_play_btn_by_small)
                  .applySkin(false);
        mViewBinding.tvMusicSourceName.setText(R.string.source_bt);
        mViewBinding.imgBtnMusicPlay.setActivated(false);
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
                mViewBinding.tvMusicTitle.setText(null);
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
            mViewBinding.imgBtnMusicPlay.setActivated(IMusicPlayer.PLAY_STATE_PLAYED == playState);
        };
        MusicManager.getInstance().getPlayStateLiveData().observeForever(mPlayStateObserver);
    }

    private void initEvent() {
        mViewBinding.imgBtnMusicPlay.setOnClickListener(v -> {
            MusicManager.getInstance().requireToggleResumeOrPause();
        });
    }

    private void freeEvent() {
        mViewBinding.imgBtnMusicPlay.setOnClickListener(null);
    }

    private void freeData() {
        MusicManager.getInstance().getAudioIdLiveData().removeObserver(mAudioIdObserver);
        MusicManager.getInstance().getTitleLiveData().removeObserver(mTitleObserver);
        MusicManager.getInstance().getPlayStateLiveData().removeObserver(mPlayStateObserver);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void freeView() {
        mViewBinding.lavMusicBg.removeAllAnimatorListeners();
        mViewBinding.lavMusicBg.cancelAnimation();
    }

    private class MyHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what != PLAY_ANIMATION) {
                return true;
            }
            if (CURRENT_THEME == -1) {
                return true;
            }
            if (CURRENT_THEME == 2) {
                Animator card_name_animator = AnimatorInflater.loadAnimator(MusicSmallCard.this.getContext(), R.animator.card_name_animator);
                card_name_animator.setTarget(mViewBinding.tvMusicSourceName);
                card_name_animator.start();
            }
            mViewBinding.lavMusicBg.setAnimation("music" + CURRENT_THEME + ".json");
            mViewBinding.lavMusicBg.playAnimation();
            mViewBinding.lavMusicBg.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    AAOP_HSkin.with(mViewBinding.lavMusicBg)
                              .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.music45)
                              .applySkin(false);
                    mViewBinding.lavMusicBg.removeAnimatorListener(this);
                }
            });
            return true;
        }
    }
}
