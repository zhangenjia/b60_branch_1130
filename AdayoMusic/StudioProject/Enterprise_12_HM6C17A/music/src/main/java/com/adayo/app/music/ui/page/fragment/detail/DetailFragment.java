package com.adayo.app.music.ui.page.fragment.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.adayo.app.music.R;
import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.databinding.FragmentMusicDetailBinding;
import com.adayo.app.music.player.PlayerManager;
import com.adayo.app.music.ui.page.fragment.AbsFragment;
import com.adayo.app.music.ui.state.MainViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;
import com.adayo.proxy.media.constant.MediaConst;
import com.adayo.proxy.media.manager.AudioFunManager;
import com.adayo.proxy.media.util.HexStateUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.lt.library.util.LogUtil;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailFragment extends AbsFragment<FragmentMusicDetailBinding> {
    private final Handler mHandler = new Handler();
    private Runnable mRunnable;
    private MainViewModel mStateViewModel;
    private long mPrevTouchTime;
    private IAdayoAudioFocusChange mFocusChange;

    @Override
    protected FragmentMusicDetailBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMusicDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initData() {
        super.initData();
        mStateViewModel.getCtrlBtnEnabledLiveData().observe(getViewLifecycleOwner(), integer -> {
            mViewBinding.inDetailControl.sbDetailProgress.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT)
                                                                             && !HexStateUtil.has(integer, MainViewModel.STATE_PARSE_ERROR));
            mViewBinding.inDetailControl.imgBtnDetailRepeat.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT));
            mViewBinding.inDetailControl.imgBtnDetailPrev.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT));
            mViewBinding.inDetailControl.imgBtnDetailPlay.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT)
                                                                             && !HexStateUtil.has(integer, MainViewModel.STATE_PARSE_ERROR));
            mViewBinding.inDetailControl.imgBtnDetailNext.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT));
            mViewBinding.inDetailControl.imgBtnDetailEffect.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT));
        });
        PlayerManager.getInstance().getPlayerLiveData().getRepeatModeLiveData().observe(getViewLifecycleOwner(), integer -> {
            switch (integer) {
                case MediaConst.RepeatModeDef.AN:
                    mViewBinding.inDetailControl.imgBtnDetailRepeat.setImageDrawable(ContextCompat.getDrawable(getAppContext(), R.drawable.selector_detail_repeat_an_btn));
                    break;
                case MediaConst.RepeatModeDef.LIST:
                    mViewBinding.inDetailControl.imgBtnDetailRepeat.setImageDrawable(ContextCompat.getDrawable(getAppContext(), R.drawable.selector_detail_repeat_list_btn));
                    break;
                case MediaConst.RepeatModeDef.RANDOM:
                    mViewBinding.inDetailControl.imgBtnDetailRepeat.setImageDrawable(ContextCompat.getDrawable(getAppContext(), R.drawable.selector_detail_repeat_random_btn));
                    break;
                default:
                    break;
            }
        });
        PlayerManager.getInstance().getPlayerLiveData().getNodeInfoLiveData().observe(getViewLifecycleOwner(), nodeInfo -> {
            String title;
            if (nodeInfo == null) {
                title = "--";
            } else if (nodeInfo.getNodeName().isEmpty()) {
                title = getString(R.string.lo_music_detail_title_unk);
            } else {
                title = nodeInfo.getNodeName();
            }
            mViewBinding.tvDetailTitle.setText(title);
            mViewBinding.tvDetailTitle.setSelected(true);
            mViewBinding.tvDetailTitle.setFocusable(true);
            mViewBinding.tvDetailTitle.setFocusableInTouchMode(true);
            mViewBinding.tvDetailTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        });
        PlayerManager.getInstance().getPlayerLiveData().getMetadataLiveData().observe(getViewLifecycleOwner(), metadataInfo -> {
            String artist;
            if (metadataInfo == null) {
                artist = "--";
            } else if (metadataInfo.getId3Info().getArtistName().isEmpty()) {
                artist = getString(R.string.lo_music_detail_artist_unk);
            } else {
                artist = metadataInfo.getId3Info().getArtistName();
            }
            String album;
            if (metadataInfo == null) {
                album = "--";
            } else if (metadataInfo.getId3Info().getAlbumName().isEmpty()) {
                album = getString(R.string.lo_music_detail_album_unk);
            } else {
                album = metadataInfo.getId3Info().getAlbumName();
            }
            mViewBinding.tvDetailArtist.setText(artist + "-" + album);
            mViewBinding.tvDetailArtist.setSelected(true);
            mViewBinding.tvDetailArtist.setFocusable(true);
            mViewBinding.tvDetailArtist.setFocusableInTouchMode(true);
            mViewBinding.tvDetailArtist.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        });
        PlayerManager.getInstance().getPlayerLiveData().getPlayStateLiveData().observe(getViewLifecycleOwner(), integer -> {
            int resId = integer == MediaConst.PlayStateDef.RESUMED ? R.drawable.selector_detail_play_btn : R.drawable.selector_detail_pause_btn;
            mViewBinding.inDetailControl.imgBtnDetailPlay.setImageResource(resId);
            if (MediaConst.PlayStateDef.STOPPED == integer) {
                Glide.with(this).clear(mViewBinding.ivDetailCover);
            }
        });
        PlayerManager.getInstance().getPlayerLiveData().getPlayErrorCauseLiveData().observe(getViewLifecycleOwner(), integer -> {
            if (integer == MediaConst.PlayErrorCauseDef.NONSUPPORT_FILE_FORMAT || integer == MediaConst.PlayErrorCauseDef.NONSUPPORT_FILE_ENCODE || integer == MediaConst.PlayErrorCauseDef.UNKNOWN) {
                mViewBinding.tvDetailTitle.setText(R.string.lo_music_detail_not_support);
                mViewBinding.tvDetailTitle.setSelected(true);
                mViewBinding.tvDetailTitle.setFocusable(true);
                mViewBinding.tvDetailTitle.setFocusableInTouchMode(true);
                mViewBinding.tvDetailTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            }
        });
        PlayerManager.getInstance().getPlayerLiveData().getDurationLiveData().observe(getViewLifecycleOwner(), aLong -> {
            int duration = Math.toIntExact(aLong);
            if (duration == 0) {
                mViewBinding.inDetailControl.tvDetailAllProgress.setText("--:--");
            } else {
                mViewBinding.inDetailControl.tvDetailAllProgress.setText(formatPlayTime(duration));
            }
            if (duration != mViewBinding.inDetailControl.sbDetailProgress.getMax()) {
                mViewBinding.inDetailControl.sbDetailProgress.setMax(duration);
            }
        });
        PlayerManager.getInstance().getPlayerLiveData().getProgressLiveData().observe(getViewLifecycleOwner(), aLong -> {
            int progress = Math.toIntExact(aLong);
            mViewBinding.inDetailControl.tvDetailNowProgress.setText(formatPlayTime(progress));
            if (!mViewBinding.inDetailControl.sbDetailProgress.isPressed()) {
                mViewBinding.inDetailControl.sbDetailProgress.setProgress(progress);
            }
        });
        PlayerManager.getInstance().getPlayerLiveData().getCoverLiveData().observe(getViewLifecycleOwner(), bitmap -> {
            LogUtil.d("bitmap: " + bitmap);
            Glide.with(this)
                 .asDrawable()
                 .load(bitmap)
                 .transition(DrawableTransitionOptions.withCrossFade(getResources().getInteger(android.R.integer.config_longAnimTime)))
                 .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                 .apply(RequestOptions.placeholderOf(R.drawable.frame_music_album_default_icon))
                 .into(mViewBinding.ivDetailCover);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initEvent() {
        super.initEvent();
        mViewBinding.inDetailControl.sbDetailProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerManager.getInstance().getPlayerControl().setProgress(MusicConst.AUDIO_ZONE_CODE, seekBar.getProgress());
                PlayerManager.getInstance().getPlayerControl().resume(MusicConst.AUDIO_ZONE_CODE, true);
            }
        });
        mViewBinding.inDetailControl.imgBtnDetailRepeat.setOnClickListener(v -> {
            PlayerManager.getInstance().getPlayerControl().toggleRepeatMode(MusicConst.AUDIO_ZONE_CODE);
        });
        mViewBinding.inDetailControl.imgBtnDetailPlay.setOnClickListener(v -> {
            PlayerManager.getInstance().getPlayerControl().toggleResumeOrPause(MusicConst.AUDIO_ZONE_CODE);
        });
        mViewBinding.inDetailControl.imgBtnDetailEffect.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setPackage("com.adayo.app.setting");
            intent.setAction("adayo.setting.intent.action.EQ");
            startActivity(intent);
        });
        mViewBinding.inDetailControl.imgBtnDetailPrev.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPrevTouchTime = System.currentTimeMillis();
                    mRunnable = new MyRunnable(MyRunnable.FAST_REWIND);
                    mHandler.postDelayed(mRunnable, TimeUnit.SECONDS.toMillis(1));
                    break;
                case MotionEvent.ACTION_UP:
                    mHandler.removeCallbacks(mRunnable);
                    long currentTouchTime = System.currentTimeMillis();
                    if (currentTouchTime - mPrevTouchTime > TimeUnit.SECONDS.toMillis(1)) {
                        AudioFunManager.getFunction().fastRewindEnd(MusicConst.AUDIO_ZONE_CODE);
                    } else {
                        PlayerManager.getInstance().getPlayerControl().prev(MusicConst.AUDIO_ZONE_CODE);
                    }
                    mViewBinding.inDetailControl.imgBtnDetailPrev.playSoundEffect(SoundEffectConstants.CLICK);
                    break;
                default:
                    break;
            }
            return false;
        });
        mViewBinding.inDetailControl.imgBtnDetailNext.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPrevTouchTime = System.currentTimeMillis();
                    mRunnable = new MyRunnable(MyRunnable.FAST_FORWARD);
                    mHandler.postDelayed(mRunnable, TimeUnit.SECONDS.toMillis(1));
                    break;
                case MotionEvent.ACTION_UP:
                    mHandler.removeCallbacks(mRunnable);
                    long currentTouchTime = System.currentTimeMillis();
                    if (currentTouchTime - mPrevTouchTime > TimeUnit.SECONDS.toMillis(1)) {
                        AudioFunManager.getFunction().fastForwardEnd(MusicConst.AUDIO_ZONE_CODE);
                    } else {
                        PlayerManager.getInstance().getPlayerControl().next(MusicConst.AUDIO_ZONE_CODE);
                    }
                    mViewBinding.inDetailControl.imgBtnDetailNext.playSoundEffect(SoundEffectConstants.CLICK);
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        PlayerManager.getInstance().getPlayerControl().requestAudioFocus(MusicConst.AUDIO_ZONE_CODE);
        AAOP_HSkin.with(mViewBinding.inDetailControl.sbDetailProgress)
                  .addViewAttrs(AAOP_HSkin.ATTR_PROGRESS_DRAWABLE, R.drawable.layer_detail_progress_sb)
                  .addViewAttrs(AAOP_HSkin.ATTR_THUMB, R.drawable.icon_schedule)
                  .applySkin(false);
    }

    private String formatPlayTime(long millisecond) {
        if (millisecond <= -1) {
            return "--:--";
        }
        if (millisecond >= 100 * 60 * 60 * 1000) {
            return "99:59:59";
        }
        int tmp = (int) (millisecond / 1000);
        int h = tmp / 3600;
        int m = tmp / 60 % 60;
        int s = tmp % 60;
        String result;
        if (millisecond < 60 * 60 * 1000) {
            result = String.format(Locale.getDefault(), "%02d:%02d", m, s);
        } else {
            result = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);
        }
        return result;
    }

    private static class MyRunnable implements Runnable {
        private static final int FAST_REWIND = 1;
        private static final int FAST_FORWARD = 2;
        private final int type;

        private MyRunnable(int type) {
            this.type = type;
        }

        @Override
        public void run() {
            switch (type) {
                case FAST_REWIND:
                    AudioFunManager.getFunction().fastRewindStart(MusicConst.AUDIO_ZONE_CODE);
                    break;
                case FAST_FORWARD:
                    AudioFunManager.getFunction().fastForwardStart(MusicConst.AUDIO_ZONE_CODE);
                    break;
                default:
                    break;
            }
        }
    }
}
