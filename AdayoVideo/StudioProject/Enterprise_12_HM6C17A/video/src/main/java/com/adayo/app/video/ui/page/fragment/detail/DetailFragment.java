package com.adayo.app.video.ui.page.fragment.detail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.adayo.app.video.R;
import com.adayo.app.video.data.constant.VideoConst;
import com.adayo.app.video.databinding.FragmentVideoDetailBinding;
import com.adayo.app.video.helper.CtrlBarVisibilityHelper;
import com.adayo.app.video.player.PlayerManager;
import com.adayo.app.video.ui.page.custom.IRenderView;
import com.adayo.app.video.ui.page.custom.RenderViewConst;
import com.adayo.app.video.ui.page.custom.TextureRenderView;
import com.adayo.app.video.ui.page.fragment.AbsFragment;
import com.adayo.app.video.ui.state.MainViewModel;
import com.adayo.proxy.media.constant.MediaConst;
import com.adayo.proxy.media.manager.VideoFunManager;
import com.adayo.proxy.media.util.HexStateUtil;
import com.lt.library.util.LogUtil;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetailFragment extends AbsFragment<FragmentVideoDetailBinding> {
    private static final int COUNT_DOWN_TOTAL_TIME = 15;
    private final ConstraintSet mUnFillConstraintSet = new ConstraintSet();
    private final ConstraintSet mFillConstraintSet = new ConstraintSet();
    private final Handler mHandler = new Handler();
    private final Runnable mDrivingWarningRunnable;
    private Runnable mRunnable;
    private MainViewModel mStateViewModel;
    private int mCurrentCountDownTime;
    private CtrlBarVisibilityHelper mCtrlBarVisibilityHelper;
    private IRenderView mRenderView;
    private long mPrevTouchTime;
    private TextView mNotSupportH1;
    private TextView mDrivingWarningH1;
    private TextView mDrivingWarningH2;
    private Button mDrivingWarningButton;

    {
        mDrivingWarningRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mViewBinding.inDetailControl.imgBtnDetailFill.isActivated()) {
                    mHandler.removeCallbacks(this);
                    return;
                }
                if (mDrivingWarningButton != null) {
                    mDrivingWarningButton.setText(getString(R.string.lo_video_detail_info_exit, mCurrentCountDownTime));
                }
                if (mCurrentCountDownTime-- == 0) {
                    mStateViewModel.setFillLiveData(false);
                    mHandler.removeCallbacks(this);
                } else {
                    mHandler.postDelayed(this, TimeUnit.SECONDS.toMillis(1));
                }
            }
        };
    }

    @Override
    protected FragmentVideoDetailBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentVideoDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initView() {
        super.initView();
        mCtrlBarVisibilityHelper = new CtrlBarVisibilityHelper(true,
                                                               TimeUnit.SECONDS.toMillis(3),
                                                               new ViewGroup[]{
                                                                       mViewBinding.getRoot()
                                                               },
                                                               new View[]{
                                                                       mViewBinding.inDetailControl.getRoot(),
                                                                       mViewBinding.tvDetailFileName
                                                               });
        mRenderView = new TextureRenderView(requireContext());
    }

    @Override
    protected void initData() {
        super.initData();
        mStateViewModel.getFillLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                enterFullScreen(mViewBinding.inDetailControl.imgBtnDetailFill);
            } else {
                exitFullScreen(mViewBinding.inDetailControl.imgBtnDetailFill);
            }
        });
        mStateViewModel.getCtrlBtnEnabledLiveData().observe(getViewLifecycleOwner(), integer -> {
            float dimness = 0.38F;
            mViewBinding.inDetailControl.sbDetailProgress.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT)
                                                                             && !HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING)
                                                                             && !HexStateUtil.has(integer, MainViewModel.STATE_PARSE_ERROR));
            mViewBinding.inDetailControl.sbDetailProgress.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
            mViewBinding.inDetailControl.tvDetailNowProgress.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
            mViewBinding.inDetailControl.tvDetailAllProgress.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
            mViewBinding.inDetailControl.imgBtnDetailFill.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT) && !HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING));
            mViewBinding.inDetailControl.imgBtnDetailFill.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
            mViewBinding.inDetailControl.imgBtnDetailPrev.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT) && !HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING));
            mViewBinding.inDetailControl.imgBtnDetailPrev.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
            mViewBinding.inDetailControl.imgBtnDetailPlay.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT)
                                                                             && !HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING)
                                                                             && !HexStateUtil.has(integer, MainViewModel.STATE_PARSE_ERROR));
            mViewBinding.inDetailControl.imgBtnDetailPlay.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
            mViewBinding.inDetailControl.imgBtnDetailNext.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT) && !HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING));
            mViewBinding.inDetailControl.imgBtnDetailNext.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
            mViewBinding.inDetailControl.imgBtnDetailEffect.setEnabled(!HexStateUtil.has(integer, MainViewModel.STATE_NO_PLAY_CONTENT) && !HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING));
            mViewBinding.inDetailControl.imgBtnDetailEffect.setAlpha(!HexStateUtil.has(integer, MainViewModel.STATE_DRIVING_WARNING) ? 1.0F : dimness);
        });
        PlayerManager.getInstance().getPlayerLiveData().getNodeInfoLiveData().observe(getViewLifecycleOwner(), nodeInfo -> {
            LogUtil.d("nodeInfo: " + nodeInfo);
            mViewBinding.tvDetailFileName.setVisibility(nodeInfo == null ? View.INVISIBLE : View.VISIBLE);
            mViewBinding.tvDetailFileName.setText(nodeInfo == null ? null : nodeInfo.getNodeName());
            mViewBinding.tvDetailFileName.setSelected(true);
            mViewBinding.tvDetailFileName.setFocusable(true);
            mViewBinding.tvDetailFileName.setFocusableInTouchMode(true);
            mViewBinding.tvDetailFileName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            if (nodeInfo == null) {
                Boolean isFill = mStateViewModel.getFillLiveData().getValue();
                if (isFill != null && isFill) {
                    mStateViewModel.setFillLiveData(false);
                }
                delTextureView();
                return;
            }
            addTextureView();
        });
        PlayerManager.getInstance().getPlayerLiveData().getPlayStateLiveData().observe(getViewLifecycleOwner(), integer -> {
            int resId = integer == MediaConst.PlayStateDef.RESUMED ? R.drawable.selector_detail_play_btn : R.drawable.selector_detail_pause_btn;
            mViewBinding.inDetailControl.imgBtnDetailPlay.setImageResource(resId);
        });
        PlayerManager.getInstance().getPlayerLiveData().getPlayErrorCauseLiveData().observe(getViewLifecycleOwner(), integer -> {
            if (integer == MediaConst.PlayErrorCauseDef.NONSUPPORT_FILE_FORMAT || integer == MediaConst.PlayErrorCauseDef.NONSUPPORT_FILE_ENCODE || integer == MediaConst.PlayErrorCauseDef.UNKNOWN) {
                ViewGroup viewGroup = addStateLayout(mViewBinding.flDetailStateHost, R.layout.in_detail_not_support);
                mNotSupportH1 = viewGroup.findViewById(R.id.tv_not_support_h1);
                Boolean isFill = mStateViewModel.getFillLiveData().getValue();
                if (isFill != null && isFill) {
                    mNotSupportH1.setText(mNotSupportH1.getText().toString().replaceAll(System.lineSeparator(), ""));
                } else {
                    mNotSupportH1.setText(R.string.lo_music_detail_not_support);
                }
            } else {
                delStateLayout(mViewBinding.flDetailStateHost, R.layout.in_detail_not_support);
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
        PlayerManager.getInstance().getPlayerLiveData().getVideoSizeLiveData().observe(getViewLifecycleOwner(), integerIntegerPair -> {
            mRenderView.setVideoRatio(RenderViewConst.VIDEO_RATIO_DEFAULT);
            mRenderView.setVideoSize(integerIntegerPair.first, integerIntegerPair.second);
        });
        mStateViewModel.getInfoRequest().getDrivingWarningStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                ViewGroup drivingWarningViewGroup = addStateLayout(mViewBinding.flDetailStateHost, R.layout.in_detail_driving_warning);
                mDrivingWarningH1 = drivingWarningViewGroup.findViewById(R.id.tv_driving_warning_h1);
                mDrivingWarningH2 = drivingWarningViewGroup.findViewById(R.id.tv_driving_warning_h2);
                mDrivingWarningButton = drivingWarningViewGroup.findViewById(R.id.btn_driving_warning_exit);
                Boolean isFill = mStateViewModel.getFillLiveData().getValue();
                if (isFill != null && isFill) {
                    mDrivingWarningH1.setText(mDrivingWarningH1.getText().toString().replaceAll(System.lineSeparator(), ""));
                    mDrivingWarningH2.setText(mDrivingWarningH2.getText().toString().replaceAll(System.lineSeparator(), ""));
                    mDrivingWarningButton.setVisibility(View.VISIBLE);
                } else {
                    mDrivingWarningH1.setText(R.string.lo_video_detail_info_driving_warning);
                    mDrivingWarningH2.setText(R.string.lo_video_detail_info_desc_driving_warning);
                    mDrivingWarningButton.setVisibility(View.GONE);
                }
                mDrivingWarningButton.setOnClickListener(v -> mStateViewModel.setFillLiveData(false));
            } else {
                delStateLayout(mViewBinding.flDetailStateHost, R.layout.in_detail_driving_warning);
                mDrivingWarningButton = null;
            }
            mHandler.removeCallbacks(mDrivingWarningRunnable);
            if (aBoolean) {
                mCurrentCountDownTime = COUNT_DOWN_TOTAL_TIME;
                mHandler.post(mDrivingWarningRunnable);
            }
        });// TODO: 2022/4/10 待恢复按钮
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initEvent() {
        super.initEvent();
        mRenderView.setOnSurfaceChanged(surface -> {
            PlayerManager.getInstance().getPlayerControl().setSurface(VideoConst.AUDIO_ZONE_CODE, surface);
        });
        mViewBinding.inDetailControl.sbDetailProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerManager.getInstance().getPlayerControl().setProgress(VideoConst.AUDIO_ZONE_CODE, seekBar.getProgress());
                PlayerManager.getInstance().getPlayerControl().resume(VideoConst.AUDIO_ZONE_CODE, true);
            }
        });
        mViewBinding.inDetailControl.imgBtnDetailFill.setOnClickListener(v -> {
            boolean activated = v.isActivated();
            if (!activated) {
                mStateViewModel.setFillLiveData(true);
            } else {
                mStateViewModel.setFillLiveData(false);
            }
        });
        mViewBinding.inDetailControl.imgBtnDetailPlay.setOnClickListener(v -> {
            PlayerManager.getInstance().getPlayerControl().toggleResumeOrPause(VideoConst.AUDIO_ZONE_CODE);
        });
        mViewBinding.inDetailControl.imgBtnDetailEffect.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setPackage("com.adayo.app.setting");
            intent.setAction("adayo.setting.intent.action.EQ");
            startActivity(intent);
        });
        mCtrlBarVisibilityHelper.setOnTouchListener((v, event) -> {
            if (mViewBinding.inDetailControl.imgBtnDetailPrev != v && mViewBinding.inDetailControl.imgBtnDetailNext != v) {
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPrevTouchTime = System.currentTimeMillis();
                    mRunnable = new MyRunnable(mViewBinding.inDetailControl.imgBtnDetailPrev == v ? MyRunnable.FAST_REWIND : MyRunnable.FAST_FORWARD);
                    mHandler.postDelayed(mRunnable, TimeUnit.SECONDS.toMillis(1));
                    break;
                case MotionEvent.ACTION_UP:
                    mHandler.removeCallbacks(mRunnable);
                    long currentTouchTime = System.currentTimeMillis();
                    if (currentTouchTime - mPrevTouchTime > TimeUnit.SECONDS.toMillis(1)) {
                        if (mViewBinding.inDetailControl.imgBtnDetailPrev == v) {
                            VideoFunManager.getFunction().fastRewindEnd(VideoConst.AUDIO_ZONE_CODE);
                        } else {
                            VideoFunManager.getFunction().fastForwardEnd(VideoConst.AUDIO_ZONE_CODE);
                        }
                    } else {
                        if (mViewBinding.inDetailControl.imgBtnDetailPrev == v) {
                            PlayerManager.getInstance().getPlayerControl().prev(VideoConst.AUDIO_ZONE_CODE);
                        } else {
                            PlayerManager.getInstance().getPlayerControl().next(VideoConst.AUDIO_ZONE_CODE);
                        }
                    }
                    if (mViewBinding.inDetailControl.imgBtnDetailPrev == v) {
                        mViewBinding.inDetailControl.imgBtnDetailPrev.playSoundEffect(SoundEffectConstants.CLICK);
                    } else {
                        mViewBinding.inDetailControl.imgBtnDetailNext.playSoundEffect(SoundEffectConstants.CLICK);
                    }
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        VideoFunManager.getFunction().reportPlayerVisibility(VideoConst.AUDIO_ZONE_CODE, true);
    }

    @Override
    public void onStop() {
        super.onStop();
        VideoFunManager.getFunction().reportPlayerVisibility(VideoConst.AUDIO_ZONE_CODE, false);
    }

    @Override
    protected void freeView() {
        super.freeView();
        mRenderView.release();
        mCtrlBarVisibilityHelper.release();
        mHandler.removeCallbacksAndMessages(null);
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

    private void enterFullScreen(View v) {
        Boolean value = mStateViewModel.getInfoRequest().getDrivingWarningStateLiveData().getValue();
        if (value != null && value) {
            mHandler.removeCallbacks(mDrivingWarningRunnable);
            mCurrentCountDownTime = COUNT_DOWN_TOTAL_TIME;
            mHandler.post(mDrivingWarningRunnable);
        }
        if (mNotSupportH1 != null) {
            mNotSupportH1.setText(mNotSupportH1.getText().toString().replaceAll(System.lineSeparator(), ""));
        }
        if (mDrivingWarningH1 != null) {
            mDrivingWarningH1.setText(mDrivingWarningH1.getText().toString().replaceAll(System.lineSeparator(), ""));
        }
        if (mDrivingWarningH2 != null) {
            mDrivingWarningH2.setText(mDrivingWarningH2.getText().toString().replaceAll(System.lineSeparator(), ""));
        }
        if (mDrivingWarningButton != null) {
            mDrivingWarningButton.setVisibility(View.VISIBLE);
        }

        mUnFillConstraintSet.clone(mViewBinding.inDetailControl.getRoot());
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mViewBinding.getRoot());
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.applyTo(mViewBinding.getRoot());

        mViewBinding.inDetailControl.ivDetailCtrBtnBg.setBackground(ContextCompat.getDrawable(getAppContext(), R.drawable.frame_button_last));
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mViewBinding.inDetailControl.getRoot().getLayoutParams();
        layoutParams.width = 1449;
        layoutParams.bottomMargin = 34;
        mViewBinding.inDetailControl.getRoot().setLayoutParams(layoutParams);
        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) mViewBinding.inDetailControl.cslDetailCtrBtnHost.getLayoutParams();
        layoutParams1.height = 116;
        layoutParams1.width = 818;
        mViewBinding.inDetailControl.cslDetailCtrBtnHost.setLayoutParams(layoutParams1);

        ConstraintSet constraintSet1 = new ConstraintSet();
        constraintSet1.clone(mViewBinding.inDetailControl.getRoot());
        constraintSet1.connect(mViewBinding.inDetailControl.tvDetailAllProgress.getId(), ConstraintSet.TOP, mViewBinding.inDetailControl.sbDetailProgress.getId(), ConstraintSet.TOP);
        constraintSet1.connect(mViewBinding.inDetailControl.tvDetailAllProgress.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet1.connect(mViewBinding.inDetailControl.tvDetailAllProgress.getId(), ConstraintSet.BOTTOM, mViewBinding.inDetailControl.sbDetailProgress.getId(), ConstraintSet.BOTTOM);
        constraintSet1.connect(mViewBinding.inDetailControl.tvDetailAllProgress.getId(), ConstraintSet.START, mViewBinding.inDetailControl.sbDetailProgress.getId(), ConstraintSet.END);
        constraintSet1.connect(mViewBinding.inDetailControl.tvDetailNowProgress.getId(), ConstraintSet.END, mViewBinding.inDetailControl.sbDetailProgress.getId(), ConstraintSet.START);
        constraintSet1.connect(mViewBinding.inDetailControl.tvDetailNowProgress.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet1.connect(mViewBinding.inDetailControl.sbDetailProgress.getId(), ConstraintSet.END, mViewBinding.inDetailControl.tvDetailAllProgress.getId(), ConstraintSet.START);
        constraintSet1.connect(mViewBinding.inDetailControl.sbDetailProgress.getId(), ConstraintSet.START, mViewBinding.inDetailControl.tvDetailNowProgress.getId(), ConstraintSet.END);
        constraintSet1.applyTo(mViewBinding.inDetailControl.getRoot());

        mViewBinding.inDetailControl.cslDetailCtrBtnHost.setPadding(55, 0, 55, 0);

        mViewBinding.inDetailControl.tvDetailAllProgress.setTextColor(Color.parseColor("#FFFFFFFF"));

        mCtrlBarVisibilityHelper.start();

        v.setActivated(true);
    }

    private void exitFullScreen(View v) {
        mFillConstraintSet.clone(mViewBinding.inDetailControl.getRoot());
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mViewBinding.getRoot());
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.START, mViewBinding.flDetailFrameHostBorder.getId(), ConstraintSet.START);
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.TOP, mViewBinding.flDetailFrameHostBorder.getId(), ConstraintSet.TOP);
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.END, mViewBinding.flDetailFrameHostBorder.getId(), ConstraintSet.END);
        constraintSet.connect(mViewBinding.flDetailFrameHost.getId(), ConstraintSet.BOTTOM, mViewBinding.flDetailFrameHostBorder.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(mViewBinding.getRoot());

        mViewBinding.inDetailControl.ivDetailCtrBtnBg.setBackground(null);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mViewBinding.inDetailControl.getRoot().getLayoutParams();
        layoutParams.width = 707;
        layoutParams.bottomMargin = 0;
        mViewBinding.inDetailControl.getRoot().setLayoutParams(layoutParams);
        ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) mViewBinding.inDetailControl.cslDetailCtrBtnHost.getLayoutParams();
        layoutParams1.height = 84;
        layoutParams1.width = 707;
        mViewBinding.inDetailControl.cslDetailCtrBtnHost.setLayoutParams(layoutParams1);

        mUnFillConstraintSet.applyCustomAttributes(mViewBinding.inDetailControl.getRoot());

        mViewBinding.inDetailControl.cslDetailCtrBtnHost.setPadding(0, 0, 0, 0);

        mViewBinding.inDetailControl.tvDetailAllProgress.setTextColor(Color.parseColor("#99FFFFFF"));

        mCtrlBarVisibilityHelper.stop(true);

        Boolean value = mStateViewModel.getInfoRequest().getDrivingWarningStateLiveData().getValue();
        if (value != null && value) {
            mHandler.removeCallbacks(mDrivingWarningRunnable);
        }
        if (mNotSupportH1 != null) {
            mHandler.post(() -> mNotSupportH1.setText(R.string.lo_music_detail_not_support));
        }
        if (mDrivingWarningH1 != null) {
            mHandler.post(() -> mDrivingWarningH1.setText(R.string.lo_video_detail_info_driving_warning));
        }
        if (mDrivingWarningH2 != null) {
            mHandler.post(() -> mDrivingWarningH2.setText(R.string.lo_video_detail_info_desc_driving_warning));
        }
        if (mDrivingWarningButton != null) {
            mDrivingWarningButton.setVisibility(View.GONE);
        }

        v.setActivated(false);
    }

    private void addTextureView() {
        if (mRenderView.getView().getParent() != null) {
            delTextureView();
        }
        FrameLayout frameHost = mViewBinding.flDetailFrameHost;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        frameHost.addView(mRenderView.getView(), 1, layoutParams);//0: iv_not_playing_ico, 1: TextureView, 2: tv_detail_fileName
        mViewBinding.flDetailFrameHost.setBackgroundColor(Color.BLACK);
    }// TODO: 2022/4/10 待封装VideoView

    private void delTextureView() {
        mViewBinding.flDetailFrameHost.setBackgroundResource(R.drawable.frame_video_last_bg);
        mViewBinding.flDetailFrameHost.removeView(mRenderView.getView());
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
                    VideoFunManager.getFunction().fastRewindStart(VideoConst.AUDIO_ZONE_CODE);
                    break;
                case FAST_FORWARD:
                    VideoFunManager.getFunction().fastForwardStart(VideoConst.AUDIO_ZONE_CODE);
                    break;
                default:
                    break;
            }
        }
    }
}
