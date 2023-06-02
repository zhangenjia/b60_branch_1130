package com.adayo.app.setting.language;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.base.ViewStubBase;
import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.FragmentCommonLanguageBinding;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.base.LogUtil;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.flyco.customtablayout.listener.OnAnimationListener;
import com.flyco.customtablayout.listener.OnTabSelectListener;

import java.util.Locale;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_BGLANGUAGE;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_BGLANGUAGE_DESTROY;

public class LanguageFragment extends ViewStubBase {
    private final static String TAG = LanguageFragment.class.getSimpleName();
    private FragmentCommonLanguageBinding mViewBinding;
    private LanguageViewModel mViewModel;
    private CommonHighlight mCommonHighlight;
    private int mposition = -1;


    @Override
    public void initViewModel() {
        super.initViewModel();
        mViewModel = ViewModelProviders.of(mBaseActivity).get(LanguageViewModel.class);
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);
    }

    @Override
    public void initData() {
        LogUtil.debugD(TAG);
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_BGLANGUAGE)) {
                    changeSelected(mViewBinding.clLanguage, true);
                } else {
                    changeSelected(mViewBinding.clLanguage, false);
                }

            }
        });
        mViewModel.mLanguageRequest.getLanguageLiveData().observe(mBaseActivity, new Observer<Locale>() {
            @Override
            public void onChanged(@Nullable Locale locale) {
                if (Locale.SIMPLIFIED_CHINESE.equals(locale)) {if (mViewBinding.sbDisplaySetLang.getCurrentTab() != 0) {
                        mViewBinding.sbDisplaySetLang.setCurrentTabWithNoAnim(0);
                    }
                } else if (Locale.ENGLISH.equals(locale)) {
                    if (mViewBinding.sbDisplaySetLang.getCurrentTab() != 1) {
                        mViewBinding.sbDisplaySetLang.setCurrentTabWithNoAnim(1);
                    }
                }
                LogUtil.debugD(TAG, "locale =" + mViewBinding.sbDisplaySetLang.getCurrentTab());
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
    public void initView() {
        mViewBinding = (FragmentCommonLanguageBinding) ViewBinding;
        mViewBinding.sbDisplaySetLang.setTabData(new String[]{mBaseActivity.getString(R.string.display_lang_ch_state), mBaseActivity.getString(R.string.display_lang_en_state)});
        if (ConfigurationManager.getInstance().getConfig() == 2) {
            SkinUtil.setImageResource(mViewBinding.ivDisplaySetBglanguage, R.drawable.language_low_last);
        } else {
            SkinUtil.setImageResource(mViewBinding.ivDisplaySetBglanguage, R.drawable.drive_last_low);
        }
    }

    @Override
    public void initEvent() {
        LogUtil.debugD(TAG);
        mViewBinding.sbDisplaySetLang.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position, boolean fromUser) {
                if (fromUser) {
                    LogUtil.debugD(TAG, "postion = " + position);
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_BGLANGUAGE);
                    mposition = position;
                }
            }
        });
        mViewBinding.sbDisplaySetLang.setOnAnimationListener(new OnAnimationListener() {
            @Override
            public void onAnimationEnd() {
                LogUtil.debugD(TAG, "onAnimationEnd  ");
                mViewModel.mLanguageRequest.requestLanguageSwitch(mposition == 0 ? Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH);
            }
        });
    }

    @Override
    public void onDestroy() {
        mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_BGLANGUAGE_DESTROY);
    }


}
