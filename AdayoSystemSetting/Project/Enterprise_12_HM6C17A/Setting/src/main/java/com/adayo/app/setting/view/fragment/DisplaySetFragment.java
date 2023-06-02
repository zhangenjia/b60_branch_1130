package com.adayo.app.setting.view.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.base.BaseFragment;
import com.adayo.app.setting.databinding.FragmentDisplaySetBinding;
import com.adayo.app.setting.highlight.DisplaySetHighlight;
import com.adayo.app.setting.theme.ThemeModeViewModel;
import com.adayo.app.setting.view.popwindow.DateTimeSettingWindow;
import com.adayo.app.setting.viewmodel.fragment.Display.HighDisplayViewModel;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_LIGHTNESS;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_STANDBY;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_THEME;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_TIME;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_NO;



public class DisplaySetFragment extends BaseFragment<FragmentDisplaySetBinding> {
    private final static String TAG = DisplaySetFragment.class.getSimpleName();
    private HighDisplayViewModel mViewModel;
    private ThemeModeViewModel mThemeViewModel;
    private DateTimeSettingWindow DateTimeSettingWindow;
    private boolean mIs24HourClockMode;
    private DisplaySetHighlight mDisplaySetHighlight;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int mScroviewY;

    private String mParam1;
    private String mParam2;

    public DisplaySetFragment() {
        }



    public static DisplaySetFragment newInstance(String param1, String param2) {
        DisplaySetFragment fragment = new DisplaySetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentDisplaySetBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentDisplaySetBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mViewModel = ViewModelProviders.of(requireActivity()).get(HighDisplayViewModel.class);
        mThemeViewModel = ViewModelProviders.of(requireActivity()).get(ThemeModeViewModel.class);
        mDisplaySetHighlight = ViewModelProviders.of(requireActivity()).get(DisplaySetHighlight.class);
        if (savedInstanceState != null) {
            mScroviewY = savedInstanceState.getInt("DisplaySetFragment");
        }
        LogUtil.d(TAG, "ScroviewY =" + mScroviewY);
    }

    @Override
    protected void initView() {
        super.initView();
        LogUtil.debugD(TAG, "");
        mViewBinding.tgBtnDisplaySetLightness.setFadeBack(false);
        mViewBinding.nsvDisplay.scrollTo(0, mScroviewY);
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.debugD(TAG, "");
        mViewModel.mDisplayRequest.getLightnessModeLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "tgBtnDisplaySetLightness UI = " + integer + "dongxiao =" + mViewBinding.tgBtnDisplaySetLightness.isFadeBack());
                boolean isAutoLightness = integer == 1;if (mViewBinding.tgBtnDisplaySetLightness.isFadeBack()) {
                    mViewBinding.tgBtnDisplaySetLightness.setCheckedNoEvent(isAutoLightness);
                } else {
                    mViewBinding.tgBtnDisplaySetLightness.setCheckedImmediatelyNoEvent(isAutoLightness);

                }
            }
        });
        mViewModel.mDisplayRequest.getLightnessLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "sbDisplaySetLightness UI = " + integer);
                if (!integer.equals(mViewBinding.sbDisplaySetLightness.getProgress())) {
                    mViewBinding.sbDisplaySetLightness.setProgress(integer);
                }
            }
        });



        mThemeViewModel.mThemeModeRequest.getThemeModeLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                LogUtil.d(TAG, "ThemeMode UI = " + s);
                switch (s) {
                    case 1:
                        mViewBinding.rgDisplaySetThemeModeHost.setCheckWithoutNotif(mViewBinding.rbDisplaySetThemeMode1.getId());
break;
                    case 2:
                        mViewBinding.rgDisplaySetThemeModeHost.setCheckWithoutNotif(mViewBinding.rbDisplaySetThemeMode2.getId());
                        break;
                    default:
                        break;
                }
            }
        });
        mThemeViewModel.mThemeModeRequest.getLoadThemeModeLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mViewBinding.rbDisplaySetThemeMode1.setEnabled(aBoolean);
                mViewBinding.rbDisplaySetThemeMode2.setEnabled(aBoolean);
            }
        });

        mViewModel.mDisplayRequest.getStandbyModeLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "dai ji jie mian UI = " + integer);
                switch (integer) {
                    case 1:
                        mViewBinding.rbDisplaySetStandbyMode1.setChecked(true);
                        break;
                    case 4:
                        mViewBinding.rbDisplaySetStandbyMode2.setChecked(true);
                        break;
                    case 3:
                        mViewBinding.rbDisplaySetStandbyMode3.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        });
        mDisplaySetHighlight.mDisplaySetHighlightRequest.getDisplaySetHighlight().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_NO)) {
                    changeSelected(mViewBinding.clLightness, false);
                    changeSelected(mViewBinding.clTheme, false);
                    changeSelected(mViewBinding.clStandby, false);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_LIGHTNESS)) {
                    changeSelected(mViewBinding.clLightness, true);
                    changeSelected(mViewBinding.clTheme, false);
                    changeSelected(mViewBinding.clStandby, false);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_TIME)) {
                    changeSelected(mViewBinding.clLightness, false);
                    changeSelected(mViewBinding.clTheme, false);
                    changeSelected(mViewBinding.clStandby, false);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_THEME)) {
                    changeSelected(mViewBinding.clLightness, false);
                    changeSelected(mViewBinding.clTheme, true);
                    changeSelected(mViewBinding.clStandby, false);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_STANDBY)) {
                    changeSelected(mViewBinding.clLightness, false);
                    changeSelected(mViewBinding.clTheme, false);
                    changeSelected(mViewBinding.clStandby, true);
                }
            }
        });
    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);

        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        LogUtil.debugD(TAG, "");
        mViewBinding.tgBtnDisplaySetLightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.debugD(TAG, "UI tgBtnDisplaySetLightness =" + isChecked);
                if (buttonView.isPressed()) {
                    mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_LIGHTNESS);
                    mViewModel.mDisplayRequest.requestLightnessModeSwitch(isChecked ? 1 : 2);}
            }
        });

        mViewBinding.ivDisplaySetLightnessIcoL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewBinding.sbDisplaySetLightness.getProgress() > 1) {
                    mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_LIGHTNESS);
                    mViewModel.mDisplayRequest.requestLightness(mViewBinding.sbDisplaySetLightness.getProgress() - 1);
                }
            }
        });
        mViewBinding.ivDisplaySetLightnessIcoR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewBinding.sbDisplaySetLightness.getProgress() < 10) {
                    mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_LIGHTNESS);
                    mViewModel.mDisplayRequest.requestLightness(mViewBinding.sbDisplaySetLightness.getProgress() + 1);
                }
            }
        });
        mViewBinding.sbDisplaySetLightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_LIGHTNESS);
                mViewModel.mDisplayRequest.requestLightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        mViewBinding.rgDisplaySetThemeModeHost.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = group.findViewById(checkedId);
            LogUtil.d(TAG, "UI ThemeMode rb =" + rb + " isPressed =" + rb.isPressed());
            if (rb.isPressed()) {
                mViewBinding.rbDisplaySetThemeMode1.setEnabled(false);
                mViewBinding.rbDisplaySetThemeMode2.setEnabled(false);
                mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_THEME);
                if (rb == mViewBinding.rbDisplaySetThemeMode1) {
                    mThemeViewModel.mThemeModeRequest.requestThemeMode(1);
                } else if (rb == mViewBinding.rbDisplaySetThemeMode2) {
                    mThemeViewModel.mThemeModeRequest.requestThemeMode(2);
                }
            } else {
                switch (mThemeViewModel.mThemeModeRequest.getThemeModeLiveData().getValue()) {
                    case 1:
                        mViewBinding.rgDisplaySetThemeModeHost.setCheckWithoutNotif(mViewBinding.rbDisplaySetThemeMode1.getId());
break;
                    case 2:
                        mViewBinding.rgDisplaySetThemeModeHost.setCheckWithoutNotif(mViewBinding.rbDisplaySetThemeMode2.getId());
                        break;
                    default:
                        break;
                }
                LogUtil.d(TAG, "getThemeModeLiveData " + mThemeViewModel.mThemeModeRequest.getThemeModeLiveData().getValue());
            }

        });



        mViewBinding.rgDisplaySetStandbyModeHost.setOnCheckedChangeListener((group, checkedId) -> {
            LogUtil.debugD(TAG, "UI StandbyMode");
            RadioButton rb = group.findViewById(checkedId);
            if (rb.isPressed()) {
                mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_STANDBY);
                if (rb == mViewBinding.rbDisplaySetStandbyMode1) {
                    mViewModel.mDisplayRequest.requestStandbyMode(1);
                } else if (rb == mViewBinding.rbDisplaySetStandbyMode2) {
                    mViewModel.mDisplayRequest.requestStandbyMode(4);
                } else if (rb == mViewBinding.rbDisplaySetStandbyMode3) {
                    mViewModel.mDisplayRequest.requestStandbyMode(3);
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtil.debugD(TAG, "");
        mViewBinding.tgBtnDisplaySetLightness.setFadeBack(true);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("DisplaySetFragment", mViewBinding.nsvDisplay.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.debugD(TAG, "");
        mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);

    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        super.onHiddenChanged(isHidden);
        mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);
    }


}