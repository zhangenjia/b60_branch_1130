package com.adayo.app.setting.drive;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.R;
import com.adayo.app.base.ViewStubBase;
import com.adayo.app.setting.databinding.FragmentCommonDriveBinding;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.base.LogUtil;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_BGDRIVE;


public class DriveFragment extends ViewStubBase {
    private final static String TAG = DriveFragment.class.getSimpleName();
    private FragmentCommonDriveBinding mViewBinding;
    private DriveViewModel mViewModel;
    private CommonHighlight mCommonHighlight;


    @Override
    public void initViewModel() {
        super.initViewModel();
        mViewModel = ViewModelProviders.of(mBaseActivity).get(DriveViewModel.class);
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);
    }

    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);
            LogUtil.debugD(TAG, "true" + childSelected);

        }
    }

    @Override
    public void initData() {
        LogUtil.debugD(TAG);
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_BGDRIVE)) {
                    changeSelected(mViewBinding.flDrive, true);
                } else {
                    changeSelected(mViewBinding.flDrive, false);
                }

            }
        });
        mViewModel.mDriveRequest.getDrivingVideoLiveData().observe(mBaseActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mViewBinding.tgBtnDisplaySetDriving.setCheckedImmediatelyNoEvent(aBoolean);
            }
        });


    }


    @Override
    public void initView() {
        this.mViewBinding = (FragmentCommonDriveBinding) ViewBinding;
        mViewBinding.tgBtnDisplaySetDriving.setFadeBack(false);
        if (ConfigurationManager.getInstance().getConfig() == ParamConstant.CONFIGURATION_HM6C18A) {
            SkinUtil.setBackground(mViewBinding.ivDisplaySetBgdrive, R.drawable.drive_last_low);

        }
    }

    @Override
    public void initEvent() {
        LogUtil.debugD(TAG);
        mViewBinding.tgBtnDisplaySetDriving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_BGDRIVE);
                    mViewModel.mDriveRequest.requestDrivingVideoSwitch(isChecked);
                }
            }
        });

    }


    @Override
    public void onResume() {
        mViewBinding.tgBtnDisplaySetDriving.setFadeBack(true);
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        AAOP_HSkin.getInstance().refreshLanguage();
    }
}
