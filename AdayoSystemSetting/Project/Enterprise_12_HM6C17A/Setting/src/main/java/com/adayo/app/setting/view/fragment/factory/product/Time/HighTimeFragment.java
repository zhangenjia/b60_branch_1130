package com.adayo.app.setting.view.fragment.factory.product.Time;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.adayo.app.setting.configuration.IFragmentConfig;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.FragmentDisplayTimeBinding;
import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.view.popwindow.DateTimeSettingWindow;
import com.adayo.app.setting.view.popwindow.OnSaveButtonListener;
import com.adayo.app.setting.highlight.DisplaySetHighlight;
import com.adayo.app.setting.viewmodel.fragment.Display.HighDisplayViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.flyco.customtablayout.listener.OnTabSelectListener;

import java.util.Map;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_LIGHTNESS;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_STANDBY;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_THEME;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DISPLAY_SET_TIME;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_NO;

public class HighTimeFragment implements IFragmentConfig<TimeFragment, FragmentDisplayTimeBinding> {
    private final static String TAG = HighTimeFragment.class.getSimpleName();
    private HighDisplayViewModel mViewModel;
    private DateTimeSettingWindow DateTimeSettingWindow;
    private boolean mIs24HourClockMode;
    private DisplaySetHighlight mDisplaySetHighlight;
    private TimeFragment fragment;
    private FragmentDisplayTimeBinding mViewBinding;

    @Override
    public void registerFragment(TimeFragment fragment, FragmentDisplayTimeBinding viewbinding) {
        this.fragment = fragment;
        this.mViewBinding = viewbinding;
    }


    @Override
    public void bindData() {
        mViewModel = ViewModelProviders.of(fragment.requireActivity()).get(HighDisplayViewModel.class);
        mDisplaySetHighlight = ViewModelProviders.of(fragment.requireActivity()).get(DisplaySetHighlight.class);

    }

    @Override
    public void initView() {
        LogUtil.debugD(TAG, "");
        mViewBinding.tgBtnDisplaySetTimeAuto.setFadeBack(false);
        mViewBinding.sbDisplaySetTimeMode.setTabData(new String[]{fragment.getString(R.string.display_time_mode_24_state), fragment.getString(R.string.display_time_mode_12_state)});}

    @Override
    public void initData() {

        LogUtil.debugD(TAG, "");
        mViewModel.mHighTimeRequest.getAutoTimeSyncLiveData().observe(fragment.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                LogUtil.debugD(TAG, "shi jian ri qi ,zi dong/shou dong = " + aBoolean);
                if (mViewBinding.tgBtnDisplaySetTimeAuto.isFadeBack()) {
                    mViewBinding.tgBtnDisplaySetTimeAuto.setCheckedNoEvent(aBoolean);
                } else {
                    mViewBinding.tgBtnDisplaySetTimeAuto.setCheckedImmediatelyNoEvent(aBoolean);
                }
                if (aBoolean) {
                    mViewBinding.btnSetTime.setEnabled(false);
                    mViewBinding.tvDisplaySetTimeSet.setAlpha((float) 0.38);
                    mViewBinding.btnSetTime.setAlpha((float) 0.38);
                } else {
                    mViewBinding.btnSetTime.setEnabled(true);
                    mViewBinding.tvDisplaySetTimeSet.setAlpha(1);
                    mViewBinding.btnSetTime.setAlpha(1);
                }
            }
        });
        mViewModel.mHighTimeRequest.get24ClockModeLiveData().observe(fragment.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                LogUtil.debugD(TAG, "shi jian zhi shi UI = " + aBoolean);
                if (mViewBinding.sbDisplaySetTimeMode.getCurrentTab() != (aBoolean ? 0 : 1)) {
                    mViewBinding.sbDisplaySetTimeMode.setSelectTab(aBoolean ? 0 : 1);}
                mIs24HourClockMode = aBoolean;
            }

        });
        mViewModel.mHighTimeRequest.getTimeLiveData().observe(fragment.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                LogUtil.debugD(TAG, "shou dong she zhi de shi jian = " + s);
                mViewBinding.tvDisplaySetTimeSet.setText(s);
            }
        });
        mDisplaySetHighlight.mDisplaySetHighlightRequest.getDisplaySetHighlight().observe(fragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_NO)) {
                    changeSelected(mViewBinding.clTime, false);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_LIGHTNESS)) {
                    changeSelected(mViewBinding.clTime, false);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_TIME)) {
                    changeSelected(mViewBinding.clTime, true);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_THEME)) {
                    changeSelected(mViewBinding.clTime, false);
                } else if (integer.equals(HIGHLIGHT_DISPLAY_SET_STANDBY)) {
                    changeSelected(mViewBinding.clTime, false);
                }
            }
        });
    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);
            LogUtil.debugD(TAG, "true" + childSelected);

        }
    }

    @Override
    public void initEvent() {

        LogUtil.debugD(TAG, "");

        mViewBinding.tgBtnDisplaySetTimeAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                LogUtil.debugD(TAG, "UI shi jian ri qi ");
                if (compoundButton.isPressed()) {
                    mViewModel.mHighTimeRequest.requestAutoTimeSync(b);
                    mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_TIME);
                }
            }

        });
        mViewBinding.sbDisplaySetTimeMode.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int progress, boolean fromUser) {
                LogUtil.debugD(TAG, "UI shi jian zhi shi = " + progress);
                if (fromUser) {
                    mViewModel.mHighTimeRequest.request24ClockMode(progress == 0);
                    mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_TIME);
                }
            }
        });
        mViewBinding.btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "UI btnSetTime");
                if (view.isPressed()) {
                    initDateTimeSettingWindow(mIs24HourClockMode);
                    if (!DateTimeSettingWindow.isShowing()) {
                        DateTimeSettingWindow.showAtLocation(view, Gravity.TOP, 0, (int) (fragment.getResources().getDimension(R.dimen.popup_big_y_totop)));
                    }
                    mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_DISPLAY_SET_TIME);

                }
            }
        });
    }


    private void initDateTimeSettingWindow(boolean is24HourClockMode) {
        LogUtil.debugD(TAG, "");
        DateTimeSettingWindow = new DateTimeSettingWindow(fragment.getActivity(), is24HourClockMode);DateTimeSettingWindow.setOnSaveButtonClickListener(new OnSaveButtonListener() {
            @Override
            public void onSaveButtonClick(View view, Map<String, Integer> data) {
                LogUtil.debugD(TAG, "bao cun");
                int tyear = data.get("year");
                int tmonth = data.get("month");
                int tday = data.get("day");
                int thour = data.get("hour");
                int tminute = data.get("minute");
                int timeMode = data.get("isAm");if (timeMode == 0) {
                    if (thour == 12) {
                        thour = 0;
                    }
                } else if (timeMode == 1) {
                    if (thour < 12) {
                        thour += 12;
                    }
                }
                mViewModel.mHighTimeRequest.requestTime(tyear, tmonth, tday, thour, tminute, 0);
            }
        });

    }

    @Override
    public void onResume() {
        LogUtil.debugD(TAG, "");

        mViewBinding.tgBtnDisplaySetTimeAuto.setFadeBack(true);
    }

    @Override
    public void onStart() {
        LogUtil.debugD(TAG, "");
        mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);

    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        mDisplaySetHighlight.mDisplaySetHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);
    }


    @Override
    public void hideView() {

    }

    @Override
    public void onDestroy() {

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        AAOP_HSkin.getInstance().refreshLanguage();
        mViewBinding.sbDisplaySetTimeMode.setTabData(new String[]{fragment.getString(R.string.display_time_mode_24_state), fragment.getString(R.string.display_time_mode_12_state)});}
}