package com.adayo.app.setting.devm;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.FragmentCommonBinding;
import com.adayo.app.setting.hotspot.HotspotViewModel;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;
import com.adayo.btsetting.viewmodel.CommonHighlight;

import static com.adayo.app.setting.model.constant.ParamConstant.CONFIGURATION_HM6C17A;
import static com.adayo.app.setting.model.constant.ParamConstant.CONFIGURATION_HM6C18A;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_DEVNM;

public class ConnDevnmFragment extends ViewStubBase {
    private final static String TAG = ConnDevnmFragment.class.getSimpleName();
    private DevnmNameDialog mDevnmNameDialog;
    private DevmViewModel mViewModel;
    private CommonHighlight mCommonHighlight;
    private FragmentCommonBinding mViewBinding;
    private HotspotViewModel mHotspotViewModel;


    @Override
    public void initViewModel() {
        super.initViewModel();
        if(ConfigurationManager.getInstance().getConfig()==CONFIGURATION_HM6C17A){
            mHotspotViewModel  = ViewModelProviders.of(mBaseActivity).get(HotspotViewModel.class);
        }
        mViewModel = ViewModelProviders.of(mBaseActivity).get(DevmViewModel.class);
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);

    }

    @Override
    public void initData() {
        LogUtil.debugD(TAG);
        mViewModel.mDevnmRequest.getDevnmName().observe(mBaseActivity, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                mViewBinding.fragConnDevnm.btnConnDevnm.setText(s);
            }
        });
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_DEVNM)) {
                    changeSelected(mViewBinding.fragConnDevnm.llDevnm, true);
                } else {
                    changeSelected(mViewBinding.fragConnDevnm.llDevnm, false);
                    }

            }
        });
    }


    @Override
    public void initView() {
        LogUtil.debugD(TAG);
        this.mViewBinding = (FragmentCommonBinding) ViewBinding;
        if (ConfigurationManager.getInstance().getConfig() == CONFIGURATION_HM6C18A) {
            SkinUtil.setBackground(mViewBinding.fragConnDevnm.ivLast, R.drawable.conn_devnm_last_low);
            SkinUtil.setBackground(mViewBinding.fragConnDevnm.btnConnDevnm, R.drawable.offroad_system_settings_low_match_input_box_benjimingcheng_n);
            SkinUtil.setBackground(mViewBinding.fragConnDevnm.ivLine, R.drawable.line_860);
        } else {
            SkinUtil.setBackground(mViewBinding.fragConnDevnm.ivLast, R.drawable.conn_devnm_last_high);
            SkinUtil.setBackground(mViewBinding.fragConnDevnm.btnConnDevnm, R.drawable.offroad_system_settings_high_match_input_box_benjimingcheng_n);
            SkinUtil.setBackground(mViewBinding.fragConnDevnm.ivLine, R.drawable.line_400);
        }

    }

    @Override
    public void initEvent() {
        LogUtil.debugD(TAG);
mViewBinding.fragConnDevnm.btnConnDevnm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_DEVNM);
                mDevnmNameDialog = DevnmNameDialog.newInstance(mViewModel.mDevnmRequest.getDevnmName().getValue());mDevnmNameDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY).setLayout((int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_width)), (int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_height)))
                        .setOffsetY((int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_y_totop)))
                        .setGravity(Gravity.TOP)
                        .setOnPositiveButtonClickListener((dialogFragment, view1, object) -> {
                            String devnm = (String) object;
                            if(mHotspotViewModel!=null){
                                mHotspotViewModel.mHotspotRequest.requestHotspotName(devnm);}
mViewModel.mDevnmRequest.requestName(devnm);
                        })
                        .show(mBaseActivity.getSupportFragmentManager(), getClass().getSimpleName());
            }
        });

    }



    private void changeSelected(ViewGroup viewGroup, boolean isSelect) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childSelected = viewGroup.getChildAt(i);
            childSelected.setSelected(isSelect);

        }
    }
}
