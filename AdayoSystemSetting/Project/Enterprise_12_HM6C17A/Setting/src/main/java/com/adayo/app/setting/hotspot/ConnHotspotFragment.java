package com.adayo.app.setting.hotspot;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.adayo.app.setting.R;
import com.adayo.app.setting.SettingAppService;
import com.adayo.app.setting.databinding.FragmentConnHotspotBinding;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;
import com.adayo.app.setting.view.dialog.HotspotPwdDialog;
import com.adayo.btsetting.viewmodel.CommonHighlight;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_HOTSPOT;


public class ConnHotspotFragment extends ViewStubBase{
    private final static String TAG = ConnHotspotFragment.class.getSimpleName();
    private HotspotViewModel mViewModel;
    private HotspotPwdDialog mHotspotPwdDialog;
    private CommonHighlight mCommonHighlight;
    private FragmentConnHotspotBinding mViewBinding;


    @Override
    public void initViewModel() {
        super.initViewModel();
        mViewModel = ViewModelProviders.of(mBaseActivity).get(HotspotViewModel.class);
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);
    }

    @Override
    public void initView() {
        super.initView();
        mViewBinding=(FragmentConnHotspotBinding)ViewBinding;

    }

    @Override
    public void initData() {
        super.initData();
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_HOTSPOT)) {
                    changeSelected(mViewBinding.clHospot, true);
                } else {
                    changeSelected(mViewBinding.clHospot, false);
                }
            }
        });
        mViewModel.mHotspotRequest.getHotspotEnableStateLiveData().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "HotspotEnableState =" + integer);
                if (integer == WifiManager.WIFI_AP_STATE_ENABLED) {mViewBinding.tgBtnHospotSwitch.setChecked(true);
                    mViewBinding.tvHospotOff.setVisibility(View.GONE);
                    mViewBinding.groupHospotOn.setVisibility(View.VISIBLE);
                } else if (integer == WifiManager.WIFI_AP_STATE_ENABLING) {
                } else {
                    mViewBinding.tgBtnHospotSwitch.setChecked(false);
                    mViewBinding.tvHospotOff.setVisibility(View.VISIBLE);
                    mViewBinding.groupHospotOn.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.mHotspotRequest.getHotspotPasswordLiveData().

                observe(mBaseActivity, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        mViewBinding.tvHospotPwd.setText(s);
                    }
                });
        mViewModel.mHotspotRequest.getHotspotClientsNumLiveData().

                observe(mBaseActivity, new Observer<Integer>() {
                    @Override
                    public void onChanged(@Nullable Integer num) {
                        if (num == 0) {mViewBinding.tvHospotTitle2.setTextColor(mBaseActivity.getApplicationContext().getColor(R.color.gray));mViewBinding.tvHospotNumber.setTextColor(mBaseActivity.getApplicationContext().getColor(R.color.gray));
                            mViewBinding.tvHospotNumber.setText(String.valueOf(num));
                        } else {mViewBinding.tvHospotTitle2.setTextColor(mBaseActivity.getApplicationContext().getColor(R.color.white));mViewBinding.tvHospotNumber.setTextColor(mBaseActivity.getApplicationContext().getColor(R.color.white));
                            mViewBinding.tvHospotNumber.setText(String.valueOf(num));}
                    }
                });
    }

    @Override
    public void initEvent() {
        super.initEvent();

        mViewBinding.tgBtnHospotSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    mViewModel.mHotspotRequest.requestEnableState(b);
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_HOTSPOT);
                }
            }
        });

        mViewBinding.ivHospotArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(ParamConstant.HIGHLIGHT_HOTSPOT);
                mHotspotPwdDialog = HotspotPwdDialog.newInstance(mViewModel.mHotspotRequest.getHotspotPasswordLiveData().getValue());mHotspotPwdDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                        .setLayout((int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_width)), (int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_height)))
                        .setGravity(Gravity.TOP)
                        .setOffsetY((int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_y_totop)))
                        .setOnPositiveButtonClickListener((dialogFragment, view1, object) -> {
                            mViewModel.mHotspotRequest.requestHotspotPwd((String) object);})
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