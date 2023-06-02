package com.adayo.app.setting.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;
import com.adayo.app.setting.databinding.ActivityMainBinding;
import com.adayo.app.setting.databinding.FragmentCommonBinding;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_BGLANGUAGE;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_BGLANGUAGE_DESTROY;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_NO;

public class CommonFragment extends ViewStubBase {
    private final static String TAG = CommonFragment.class.getSimpleName();
    private CommonHighlight mCommonHighlight;
    private FragmentCommonBinding mViewBinding;
    private int ScrollY;

    @Override
    public void initViewModel() {
        super.initViewModel();
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);
    }

    @Override
    public void initView() {
        super.initView();
        ViewBinding = (ActivityMainBinding) ViewBinding;
        mViewBinding = ((ActivityMainBinding) ViewBinding).vsCommon;
        LogUtil.d(TAG,"mViewBinding ="+mViewBinding);
        if (ScrollY != 0) {
            ((ActivityMainBinding) ViewBinding).llMain.setVisibility(View.INVISIBLE);
            mViewBinding.nsvFragmentCommon.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d(TAG, "height" + mViewBinding.nsvFragmentCommon.getHeight());
                    mViewBinding.nsvFragmentCommon.scrollTo(0, ScrollY);
                    ((ActivityMainBinding) ViewBinding).llMain.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        outState.putInt("CommonFragment", mViewBinding.nsvFragmentCommon.getScrollY());
        LogUtil.d(TAG, "ScrollY =" + mViewBinding.nsvFragmentCommon.getScrollY());
        super.saveState(outState);

    }

    @Override
    public void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        if (savedInstanceState != null) {
            ScrollY = savedInstanceState.getInt("CommonFragment");
            LogUtil.d(TAG, "ScrollY =" + ScrollY);
        }

    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void initEvent() {
        super.initEvent();
        LogUtil.d(TAG, "ScrollY =" + ScrollY);

    }


    @Override
    public void onStart() {
        if (mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().getValue().equals(HIGHLIGHT_BGLANGUAGE_DESTROY)) {
            mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_BGLANGUAGE);

        } else {
            mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);
        }
    }

    @Override
    public void showOrHide(boolean b) {
        LogUtil.d(TAG, "b=" + b);
        if (!b) {
            mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);
        } else {
            if (ScrollY != 0) {
                mViewBinding.llCommon.setVisibility(View.INVISIBLE);
                mViewBinding.nsvFragmentCommon.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.d(TAG, "height =" + mViewBinding.nsvFragmentCommon.getHeight() + "ScrollY =" + ScrollY);
                        mViewBinding.nsvFragmentCommon.scrollTo(0, ScrollY);
                        mViewBinding.llCommon.setVisibility(View.VISIBLE);
                        ((ActivityMainBinding) ViewBinding).flMainInfo.setVisibility(View.GONE);
                    }
                });
            } else {

                mViewBinding.llCommon.setVisibility(View.VISIBLE);
                ((ActivityMainBinding) ViewBinding).flMainInfo.setVisibility(View.GONE);
            }
            LogUtil.d(TAG,"Common VISIBLE");
        }
        super.showOrHide(b);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        AAOP_HSkin.getInstance().refreshLanguage();


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
