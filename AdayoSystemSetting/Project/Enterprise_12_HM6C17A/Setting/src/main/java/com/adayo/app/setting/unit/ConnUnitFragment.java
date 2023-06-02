package com.adayo.app.setting.unit;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.FragmentConnUnitBinding;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.flyco.customtablayout.listener.OnTabSelectListener;

import static com.adayo.app.setting.model.constant.ParamConstant.CONFIGURATION_HM6C18A;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_UNIT;

public class ConnUnitFragment extends ViewStubBase {
    private final static String TAG = ConnUnitFragment.class.getSimpleName();

    private CommonHighlight mCommonHighlight;
    private UnitViewModel mViewModel;
    private FragmentConnUnitBinding mViewBinding;


    @Override
    public void initViewModel() {
        super.initViewModel();
        mViewModel = ViewModelProviders.of(mBaseActivity).get(UnitViewModel.class);
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);

    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);

        }
    }

    @Override
    public void initData() {
        LogUtil.debugD(TAG);
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_UNIT)) {
                    changeSelected(mViewBinding.clUnit, true);
} else {
                    changeSelected(mViewBinding.clUnit, false);
}

            }
        });
        mViewModel.mUnitRequest.getTemperatureUnitLiveData().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (mViewBinding.sbDisplaySetTemperature.getCurrentTab() != integer){
                    mViewBinding.sbDisplaySetTemperature.setSelectTab(integer);}
            }
        });

        mViewModel.mUnitRequest.getKmAndOilWearUnitLiveData().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (mViewBinding.sbDisplaySetMileage.getCurrentTab() != integer){
                    mViewBinding.sbDisplaySetMileage.setSelectTab(integer);}
            }
        });


    }


    @Override
    public void initView() {
        this.mViewBinding = (FragmentConnUnitBinding) ViewBinding;
        mViewBinding.sbDisplaySetTemperature.setTabData(new String[]{mBaseActivity.getString(R.string.display_unit_temperature_celsius_state), mBaseActivity.getString(R.string.display_unit_temperature_fahrenheit_state)});mViewBinding.sbDisplaySetMileage.setTabData(new String[]{mBaseActivity.getString(R.string.display_unit_mileage_metric_state), mBaseActivity.getString(R.string.display_unit_mileage_imperial_state)});if (ConfigurationManager.getInstance().getConfig() == CONFIGURATION_HM6C18A) {
            SkinUtil.setBackground(mViewBinding.line, R.drawable.line_860);
            SkinUtil.setImageResource(mViewBinding.ivDisplaySetBgunit, R.drawable.unit_last_low);
        }
    }

    @Override
    public void initEvent() {
        mViewBinding.sbDisplaySetTemperature.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int progress, boolean fromUser) {
                if (fromUser) {
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_UNIT);
                    mViewModel.mUnitRequest.requestTemperatureUnit(progress);
                }
            }
        });

        mViewBinding.sbDisplaySetMileage.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int progress, boolean fromUser) {
                if (fromUser) {
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_UNIT);
                    mViewModel.mUnitRequest.requestKmAndOilWearUnit(progress);
                }
            }
        });

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mViewBinding.sbDisplaySetTemperature.setTabData(new String[]{mBaseActivity.getString(R.string.display_unit_temperature_celsius_state), mBaseActivity.getString(R.string.display_unit_temperature_fahrenheit_state)});mViewBinding.sbDisplaySetMileage.setTabData(new String[]{mBaseActivity.getString(R.string.display_unit_mileage_metric_state), mBaseActivity.getString(R.string.display_unit_mileage_imperial_state)});}

}