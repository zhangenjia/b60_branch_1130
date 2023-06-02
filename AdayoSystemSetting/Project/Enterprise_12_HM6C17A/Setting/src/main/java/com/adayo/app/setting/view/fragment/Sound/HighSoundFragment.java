package com.adayo.app.setting.view.fragment.Sound;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.databinding.FragmentSoundBinding;
import com.adayo.app.setting.highlight.SoundHighlight;
import com.adayo.app.setting.skin.LanguageUtil;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.setting.view.popwindow.CommonDialog;
import com.adayo.app.setting.viewmodel.fragment.sound.HighSoundViewModel;
import com.flyco.customtablayout.listener.OnTabSelectListener;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_HARMAN;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_NO;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_SOUND_EQ;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_SOUND_LOUDNESS;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_SOUND_SWITCH;

public class HighSoundFragment implements ISoundConfig<SoundFragment, FragmentSoundBinding> {
    private final static String TAG = HighSoundFragment.class.getSimpleName();
    private SoundFragment mfragment;
    private HighSoundViewModel mViewModel;
    private SoundHighlight mSoundHighlight;
    private FragmentSoundBinding mViewBinding;
    private CommonDialog mPhoneToast;
    private static final int SEEK_FLAG_UNPRESSED = 0;
    private CommonDialog mVoiceToast;
    private static final int AUDIO_STREAM_MEDIA = 3;private static final int AUDIO_STREAM_PHONE = 6;private static final int AUDIO_STREAM_TTS = 9;private static final int AUDIO_STREAM_NAVI = 11;private static final int AUDIO_STREAM_BT_MUSIC = 15;private static final int AUDIO_STREAM_BT_PHONE_RING = 2;private static final int SEEK_FLAG_SLIDE_MINIMUM_RANGE = 1;
    private static final int SEEK_FLAG_SLIDE_AVAILABLE_RANGE = 2;
    private static final int SEEK_FLAG_SLIDE_MINIMUM_RANGE_VOICE = 1;
    private static final int SEEK_FLAG_SLIDE_AVAILABLE_RANGE_VOICE = 2;
    private ExecutorService mThreadPool;

    @Override
    public void registerFragment(SoundFragment fragment, FragmentSoundBinding viewbinding) {
        mfragment = fragment;
        mViewBinding = viewbinding;
        mSoundHighlight = ViewModelProviders.of(mfragment.requireActivity()).get(SoundHighlight.class);
        mViewModel = ViewModelProviders.of(mfragment.requireActivity()).get(HighSoundViewModel.class);
    }

    @Override
    public void operationFragment() {
        if (ConfigurationManager.getInstance().isHamanPowerIcon()) {
            SkinUtil.setImageResource(mViewBinding.ivSoundHarman, R.drawable.logo_haman);
        } else {
            mViewBinding.btnSoundHarman.setText(R.string.sound_field_adjustment);
        }
    }

    @Override
    public void onStart() {
        mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);

    }


    @Override
    public void initView() {
        LogUtil.debugD(TAG, "");
        mViewBinding.inFss.tgBtnSoundTouchSwitch.setFadeBack(false);
        mViewBinding.inFss.sbSoundPhoneSwitch.setTabData(new String[]{mfragment.getString(R.string.sound_phone_ring_mountain_stream_state), mfragment.getString(R.string.sound_phone_ring_running_water_state), mfragment.getString(R.string.sound_phone_ring_starry_sky_state)});

        mViewBinding.inFss.sbSoundSpeedSwitch.setTabData(new String[]{mfragment.getString(R.string.sound_with_speed_close_state), mfragment.getString(R.string.sound_with_speed_low_state), mfragment.getString(R.string.sound_with_speed_mid_state), mfragment.getString(R.string.sound_with_speed_high_state)});
        SkinUtil.setImageResource(mViewBinding.inFsl.sbPopupLoundnessSystem.ivItemSlidebarIco, R.drawable.offroad_system_settings_sound_icon_volume_setting_ring_tone);
        LanguageUtil.setText(mViewBinding.inFsl.sbPopupLoundnessSystem.tvItemSlidevarTitle, R.string.sound_loudness_system);
        mViewBinding.inFsl.sbPopupLoundnessSystem.sbItemSlidebarSlide.setMin(0);
        mViewBinding.inFsl.sbPopupLoundnessSystem.sbItemSlidebarSlide.setMax(39);
        mViewBinding.inFsl.sbPopupLoundnessSystem.sbItemSlidebarSlide.setTag(SEEK_FLAG_UNPRESSED);
        SkinUtil.setImageResource(mViewBinding.inFsl.sbPopupLoundnessBluetooth.ivItemSlidebarIco, R.drawable.offroad_system_settings_sound_icon_volume_setting_bt);

        LanguageUtil.setText(mViewBinding.inFsl.sbPopupLoundnessBluetooth.tvItemSlidevarTitle, R.string.sound_loudness_bluetooth);
        mViewBinding.inFsl.sbPopupLoundnessBluetooth.sbItemSlidebarSlide.setMin(0);
        mViewBinding.inFsl.sbPopupLoundnessBluetooth.sbItemSlidebarSlide.setMax(39);
        mViewBinding.inFsl.sbPopupLoundnessBluetooth.sbItemSlidebarSlide.setTag(SEEK_FLAG_UNPRESSED);
        SkinUtil.setImageResource(mViewBinding.inFsl.sbPopupLoundnessMedia.ivItemSlidebarIco, R.drawable.offroad_system_settings_sound_icon_volume_setting_multimedia);

        LanguageUtil.setText(mViewBinding.inFsl.sbPopupLoundnessMedia.tvItemSlidevarTitle, R.string.sound_loudness_media);
        mViewBinding.inFsl.sbPopupLoundnessMedia.sbItemSlidebarSlide.setMin(0);
        mViewBinding.inFsl.sbPopupLoundnessMedia.sbItemSlidebarSlide.setMax(39);
        mViewBinding.inFsl.sbPopupLoundnessMedia.sbItemSlidebarSlide.setTag(SEEK_FLAG_UNPRESSED);
        SkinUtil.setImageResource(mViewBinding.inFsl.sbPopupLoundnessPhone.ivItemSlidebarIco, R.drawable.offroad_system_settings_sound_icon_volume_setting_phone);

        LanguageUtil.setText(mViewBinding.inFsl.sbPopupLoundnessPhone.tvItemSlidevarTitle, R.string.sound_loudness_phone);
        mViewBinding.inFsl.sbPopupLoundnessPhone.sbItemSlidebarSlide.setMin(0);
        mViewBinding.inFsl.sbPopupLoundnessPhone.sbItemSlidebarSlide.setMax(39);
        mViewBinding.inFsl.sbPopupLoundnessPhone.sbItemSlidebarSlide.setTag(SEEK_FLAG_UNPRESSED);
        SkinUtil.setImageResource(mViewBinding.inFsl.sbPopupLoundnessNavi.ivItemSlidebarIco, R.drawable.offroad_system_settings_sound_icon_volume_setting_navigation);

        LanguageUtil.setText(mViewBinding.inFsl.sbPopupLoundnessNavi.tvItemSlidevarTitle, R.string.sound_loudness_navigation);
        mViewBinding.inFsl.sbPopupLoundnessNavi.sbItemSlidebarSlide.setMin(0);
        mViewBinding.inFsl.sbPopupLoundnessNavi.sbItemSlidebarSlide.setMax(39);
        mViewBinding.inFsl.sbPopupLoundnessNavi.sbItemSlidebarSlide.setTag(SEEK_FLAG_UNPRESSED);
        SkinUtil.setImageResource(mViewBinding.inFsl.sbPopupLoundnessVoice.ivItemSlidebarIco, R.drawable.offroad_system_settings_sound_icon_volume_setting_voice);

        LanguageUtil.setText(mViewBinding.inFsl.sbPopupLoundnessVoice.tvItemSlidevarTitle, R.string.sound_loudness_voice);
        mViewBinding.inFsl.sbPopupLoundnessVoice.sbItemSlidebarSlide.setMin(0);
        mViewBinding.inFsl.sbPopupLoundnessVoice.sbItemSlidebarSlide.setMax(39);
        mViewBinding.inFsl.sbPopupLoundnessVoice.sbItemSlidebarSlide.setTag(SEEK_FLAG_UNPRESSED);
        mViewBinding.inFsl.sbPopupLoundnessVoice.sbItemSlidebarSlide.setVisibility(View.VISIBLE);

    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);

        }
    }

    @Override
    public void initData() {
        initThreadPool();
        LogUtil.debugD(TAG, "");
        mViewModel.mHighSoundRequest.getLoudnessBtPhoneRingLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "dian hua ling sheng = " + integer);
                if (!integer.equals(mViewBinding.inFsl.sbPopupLoundnessSystem.sbItemSlidebarSlide.getProgress())) {mViewBinding.inFsl.sbPopupLoundnessSystem.sbItemSlidebarSlide.setProgress(integer);
                }
                mViewBinding.inFsl.sbPopupLoundnessSystem.tvItemSlidebarValue.setText(String.valueOf(integer));
            }
        });

        mViewModel.mHighSoundRequest.getLoudnessBtMusicLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "lan ya yin yue = " + integer);
                if (!integer.equals(mViewBinding.inFsl.sbPopupLoundnessBluetooth.sbItemSlidebarSlide.getProgress())) {
                    mViewBinding.inFsl.sbPopupLoundnessBluetooth.sbItemSlidebarSlide.setProgress(integer);
                }
                mViewBinding.inFsl.sbPopupLoundnessBluetooth.tvItemSlidebarValue.setText(String.valueOf(integer));
            }
        });

        mViewModel.mHighSoundRequest.getLoudnessMediaLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "Media yin liang = " + integer);
                if (!integer.equals(mViewBinding.inFsl.sbPopupLoundnessMedia.sbItemSlidebarSlide.getProgress())) {
                    mViewBinding.inFsl.sbPopupLoundnessMedia.sbItemSlidebarSlide.setProgress(integer);
                }
                mViewBinding.inFsl.sbPopupLoundnessMedia.tvItemSlidebarValue.setText(String.valueOf(integer));
            }
        });

        mViewModel.mHighSoundRequest.getLoudnessPhoneLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "Phone yin liang = " + integer + mViewBinding.inFsl.sbPopupLoundnessPhone.sbItemSlidebarSlide.getProgress());
                if (!integer.equals(mViewBinding.inFsl.sbPopupLoundnessPhone.sbItemSlidebarSlide.getProgress())) {
                    mViewBinding.inFsl.sbPopupLoundnessPhone.sbItemSlidebarSlide.setProgress(integer);
                }
                mViewBinding.inFsl.sbPopupLoundnessPhone.tvItemSlidebarValue.setText(String.valueOf(integer));
            }
        });

        mViewModel.mHighSoundRequest.getLoudnessNaviLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "Navi = " + integer);
                if (!integer.equals(mViewBinding.inFsl.sbPopupLoundnessNavi.sbItemSlidebarSlide.getProgress())) {
                    mViewBinding.inFsl.sbPopupLoundnessNavi.sbItemSlidebarSlide.setProgress(integer);
                }
                mViewBinding.inFsl.sbPopupLoundnessNavi.tvItemSlidebarValue.setText(String.valueOf(integer));
            }
        });

        mViewModel.mHighSoundRequest.getLoudnessTtsLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "Voice = " + integer);
                if (!integer.equals(mViewBinding.inFsl.sbPopupLoundnessVoice.sbItemSlidebarSlide.getProgress())) {
                    mViewBinding.inFsl.sbPopupLoundnessVoice.sbItemSlidebarSlide.setProgress(integer);
                }
                mViewBinding.inFsl.sbPopupLoundnessVoice.tvItemSlidebarValue.setText(String.valueOf(integer));
            }
        });


        mViewModel.mHighSoundRequest.getSpeedLevelLiveData().observe(mfragment.getViewLifecycleOwner(), integer -> {
            LogUtil.debugD(TAG, "che su tiao jie yin liang = " + integer);
            if (mViewBinding.inFss.sbSoundSpeedSwitch.getCurrentTab() != integer) {
                mViewBinding.inFss.sbSoundSpeedSwitch.setSelectTab(integer);
            }
        });

        mViewModel.mHighSoundRequest.getBeepEnableLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                LogUtil.debugD(TAG, "tgBtnSoundTouchSwitch UI =" + aBoolean);
                if (mViewBinding.inFss.tgBtnSoundTouchSwitch.isFadeBack()) {
                    mViewBinding.inFss.tgBtnSoundTouchSwitch.setCheckedNoEvent(aBoolean);
                } else {
                    mViewBinding.inFss.tgBtnSoundTouchSwitch.setCheckedImmediatelyNoEvent(aBoolean);
                }
            }
        });

        mViewModel.mHighSoundRequest.getPhoneRingLiveData().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "PhoneRing UI = " + integer);
                if (mViewBinding.inFss.sbSoundPhoneSwitch.getCurrentTab() != integer) {
                    mViewBinding.inFss.sbSoundPhoneSwitch.setSelectTab(integer);
                }
            }
        });
        mSoundHighlight.mSoundHighlightRequest.getSoundHighlight().observe(mfragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_NO)) {
                    changeSelected(mViewBinding.inFsl.clSoundLoudness, false);
SkinUtil.setBackground(mViewBinding.inFss.clSoundSwitch, R.drawable.offroad_system_settings_sound_frame_last_right_three_n);
                    changeSelected(mViewBinding.clEq, false);
                    changeSelected(mViewBinding.flHarman, false);
                } else if (integer.equals(HIGHLIGHT_SOUND_LOUDNESS)) {
                    changeSelected(mViewBinding.inFsl.clSoundLoudness, true);
SkinUtil.setBackground(mViewBinding.inFss.clSoundSwitch, R.drawable.offroad_system_settings_sound_frame_last_right_three_n);
                    changeSelected(mViewBinding.clEq, false);
                    changeSelected(mViewBinding.flHarman, false);
                } else if (integer.equals(HIGHLIGHT_SOUND_SWITCH)) {
                    changeSelected(mViewBinding.inFsl.clSoundLoudness, false);

                    SkinUtil.setBackground(mViewBinding.inFss.clSoundSwitch, R.drawable.offroad_system_settings_sound_frame_last_right_three_p);
                    changeSelected(mViewBinding.clEq, false);
                    changeSelected(mViewBinding.flHarman, false);
                } else if (integer.equals(HIGHLIGHT_SOUND_EQ)) {
                    changeSelected(mViewBinding.inFsl.clSoundLoudness, false);

                    SkinUtil.setBackground(mViewBinding.inFss.clSoundSwitch, R.drawable.offroad_system_settings_sound_frame_last_right_three_n);
                    changeSelected(mViewBinding.clEq, true);
                    changeSelected(mViewBinding.flHarman, false);
                } else if (integer.equals(HIGHLIGHT_HARMAN)) {
                    changeSelected(mViewBinding.inFsl.clSoundLoudness, false);

                    SkinUtil.setBackground(mViewBinding.inFss.clSoundSwitch, R.drawable.offroad_system_settings_sound_frame_last_right_three_n);
                    changeSelected(mViewBinding.clEq, false);
                    changeSelected(mViewBinding.flHarman, true);
                }
            }
        });
    }

    @Override
    public void initEvent() {
        LogUtil.debugD(TAG, "");
        mViewBinding.inFsl.sbPopupLoundnessSystem.sbItemSlidebarSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {return;
                }
                mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_LOUDNESS);
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_BT_PHONE_RING, progress);}
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mViewBinding.inFsl.sbPopupLoundnessBluetooth.sbItemSlidebarSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_LOUDNESS);
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_BT_MUSIC, progress);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mViewBinding.inFsl.sbPopupLoundnessMedia.sbItemSlidebarSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_LOUDNESS);
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_MEDIA, progress);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mViewBinding.inFsl.sbPopupLoundnessPhone.sbItemSlidebarSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_LOUDNESS);
                LogUtil.debugD(TAG, "progress = " + progress);
                if (progress < 5) {
                    if (!seekBar.getTag().equals(SEEK_FLAG_SLIDE_MINIMUM_RANGE)) {
                        mThreadPool.execute(() -> {
                            mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_PHONE, 5);
                        });
                        if (mPhoneToast == null) {
                            mPhoneToast = CommonDialog.getCommonDialog(mfragment.getActivity());
                        }
                        mPhoneToast.initIntView(R.string.toast_hint_sound_loudness_min, R.drawable.icon_singlerow_voice);
                        if (!mPhoneToast.isShowing()) {
                            mPhoneToast.show();
                        }
                        seekBar.setTag(SEEK_FLAG_SLIDE_MINIMUM_RANGE);
                    }
                    seekBar.setProgress(5);
                } else {
                    mThreadPool.execute(() -> {
                        mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_PHONE, progress);
                    });
                    seekBar.setTag(SEEK_FLAG_SLIDE_AVAILABLE_RANGE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setTag(SEEK_FLAG_UNPRESSED);
            }
        });

        mViewBinding.inFsl.sbPopupLoundnessNavi.sbItemSlidebarSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_LOUDNESS);
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_NAVI, progress);
                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mViewBinding.inFsl.sbPopupLoundnessVoice.sbItemSlidebarSlide.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                LogUtil.debugD(TAG, "progress = " + progress);
                mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_LOUDNESS);
                if (progress < 5) {
                    if (!seekBar.getTag().equals(SEEK_FLAG_SLIDE_MINIMUM_RANGE_VOICE)) {
                        mThreadPool.execute(() -> {
                            mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_TTS, 5);
                        });
                        if (mVoiceToast == null) {
                            mVoiceToast = CommonDialog.getCommonDialog(mfragment.getActivity());
                        }
                        mVoiceToast.initIntView(R.string.toast_hint_sound_loudness_min, R.drawable.icon_singlerow_voice);

                        if (!mVoiceToast.isShowing()) {
                            mVoiceToast.show();
                        }
                        seekBar.setTag(SEEK_FLAG_SLIDE_MINIMUM_RANGE_VOICE);
                    }
                    seekBar.setProgress(5);
                } else {
                    mThreadPool.execute(() -> {
                        mViewModel.mHighSoundRequest.requestLoudness(AUDIO_STREAM_TTS, progress);
                    });

                    seekBar.setTag(SEEK_FLAG_SLIDE_AVAILABLE_RANGE_VOICE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setTag(SEEK_FLAG_UNPRESSED);
            }
        });


        mViewBinding.btnSoundEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.isPressed()) {
                    mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_EQ);
                }
                LogUtil.debugD(TAG, "UI btnSoundEq");
                Intent intent = new Intent("adayo.setting.intent.action.EQ");
                mfragment.startActivity(intent);
            }
        });

        mViewBinding.btnSoundHarman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "");
                if (view.isPressed()) {
                    mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_HARMAN);
                    Intent intent = new Intent("adayo.setting.intent.action.Harman");
                    mfragment.startActivity(intent);
                }
            }
        });

        mViewBinding.inFss.sbSoundSpeedSwitch.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int progress, boolean fromUser) {
                if (fromUser) {
                    mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_SWITCH);
                }
                LogUtil.debugD(TAG, "UI che su tiao jie yin liang = " + progress);
                mViewModel.mHighSoundRequest.requestSpeedLevel(progress);
            }
        });

        mViewBinding.inFss.tgBtnSoundTouchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtil.debugD(TAG, "UI tgBtnSoundTouchSwitch = " + b);
                if (compoundButton.isPressed()) {
                    mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_SWITCH);
                    mViewModel.mHighSoundRequest.requestBeepSwitch(b);
                }

            }
        });

        mViewBinding.inFss.sbSoundPhoneSwitch.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int progress, boolean fromUser) {
                LogUtil.debugD(TAG, "PhoneRing = " + progress);
                if (fromUser) {
                    mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_SOUND_SWITCH);
                }
                mViewModel.mHighSoundRequest.requestPhoneRing(progress);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        mSoundHighlight.mSoundHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (!ConfigurationManager.getInstance().isHamanPowerIcon()) {
            mViewBinding.btnSoundHarman.setText(R.string.sound_field_adjustment);
        }
        mViewBinding.inFss.sbSoundSpeedSwitch.setTabData(new String[]{mfragment.getString(R.string.sound_with_speed_close_state), mfragment.getString(R.string.sound_with_speed_low_state), mfragment.getString(R.string.sound_with_speed_mid_state), mfragment.getString(R.string.sound_with_speed_high_state)});
        mViewBinding.inFss.sbSoundPhoneSwitch.setTabData(new String[]{mfragment.getString(R.string.sound_phone_ring_mountain_stream_state), mfragment.getString(R.string.sound_phone_ring_running_water_state), mfragment.getString(R.string.sound_phone_ring_starry_sky_state)});
    }



    private void initThreadPool() {
        if (Objects.isNull(mThreadPool) || mThreadPool.isShutdown()) {
            mThreadPool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 5);
        }
    }


    private void freeThreadPool() {
        try {
            mThreadPool.shutdown();
            if (mThreadPool.awaitTermination(1, TimeUnit.SECONDS)) {
                mThreadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            mThreadPool.shutdownNow();
        } finally {
            mThreadPool = null;
        }
    }

    @Override
    public void onResume() {
        mViewBinding.inFss.tgBtnSoundTouchSwitch.setFadeBack(true);
    }
}
