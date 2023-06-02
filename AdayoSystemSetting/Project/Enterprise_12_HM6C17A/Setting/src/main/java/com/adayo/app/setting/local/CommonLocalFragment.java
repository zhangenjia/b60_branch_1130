package com.adayo.app.setting.local;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.FragmentCommonLocalBinding;
import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.flyco.customtablayout.listener.OnTabSelectListener;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_BGLOCAL;



public class CommonLocalFragment extends ViewStubBase {
    private final static String TAG = CommonLocalFragment.class.getSimpleName();
    private LocalViewModel mViewModel;
    private CommonHighlight mCommonHighlight;
    private FragmentCommonLocalBinding mViewBinding;


    @Override
    public void initViewModel() {
        super.initViewModel();
        mViewModel = ViewModelProviders.of(mBaseActivity).get(LocalViewModel.class);
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);

    }

    @Override
    public void initView() {
        super.initView();
        mViewBinding = (FragmentCommonLocalBinding) ViewBinding;
        mViewBinding.sbDisplaySetLocate.setTabData(new String[]{mBaseActivity.getString(R.string.display_local_mode_dual_state), mBaseActivity.getString(R.string.display_local_mode_gps_state), mBaseActivity.getString(R.string.display_local_mode_beidou_state)});
    }

    @Override
    public void initData() {
        super.initData();

        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_BGLOCAL)) {
                    changeSelected(mViewBinding.clDisplaySetBglocal, true);
                } else {
                    changeSelected(mViewBinding.clDisplaySetBglocal, false);

                }

            }
        });

        mViewModel.mLocalRequest.getLocateModeLiveData().observe(mBaseActivity, new Observer<Integer>() {

            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "integer =" + integer);
                if (mViewBinding.sbDisplaySetLocate.getCurrentTab() != integer) {
                    mViewBinding.sbDisplaySetLocate.setCurrentTabWithNoAnim(integer);}
            }
        });


    }

    @Override
    public void initEvent() {
        super.initEvent();
        mViewBinding.sbDisplaySetLocate.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position, boolean fromUser) {
                if (fromUser) {
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_BGLOCAL);
                    mViewModel.mLocalRequest.requestLocateMode(position);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mViewBinding.sbDisplaySetLocate.setTabData(new String[]{mBaseActivity.getString(R.string.display_local_mode_dual_state), mBaseActivity.getString(R.string.display_local_mode_gps_state), mBaseActivity.getString(R.string.display_local_mode_beidou_state)});
    }
}