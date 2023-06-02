package com.adayo.app.setting.wifi;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.FragmentConnWifiBinding;
import com.adayo.app.setting.view.dialog.WifiConnDialog;
import com.adayo.app.setting.view.dialog.WifiDisconnDialog;
import com.adayo.app.setting.view.dialog.WifiReconnDialog;
import com.adayo.app.setting.view.popwindow.CommonDialog;
import com.adayo.btsetting.viewmodel.CommonHighlight;
import com.adayo.common.settings.bean.WifiInfoBean;

import java.util.List;
import java.util.Objects;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_WIFI;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_NO_SAVE_ITEM;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_NO_WLAN_ITEM;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_SAVE_ITEM;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_WLAN_ITEM;
import static com.adayo.common.settings.constant.EnumConstant.WIFI_STATE.CONNECT;


public class ConnWifiFragment extends ViewStubBase {
    private final static String TAG = ConnWifiFragment.class.getSimpleName();
    private WifiViewModel mViewModel;
    private CommonDialog mCommonDialog;
    private NetWifiAdapter mNetWifiAdapter;
    private NetWifiSaveAdapter mNetWifiSaveAdapter;
    private WifiConnDialog mWifiConnDialog;private WifiDisconnDialog mWifiDisconnDialog;private WifiReconnDialog mWifiReconnDialog;private Animation mLoadAnimation;
    private WifiManager mWifiManager;
    private CommonHighlight mCommonHighlight;
    private AnimationDrawable mAnimationDrawable;
    private FragmentConnWifiBinding mViewBinding;
    private CommonDialog mFailDialog;


    @Override
    public void initViewModel() {
        super.initViewModel();
        mViewModel = ViewModelProviders.of(mBaseActivity).get(WifiViewModel.class);
        mWifiManager = (WifiManager) mBaseActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mCommonHighlight = ViewModelProviders.of(mBaseActivity).get(CommonHighlight.class);

    }

    @Override
    public void initView() {
        super.initView();
        mViewBinding = (FragmentConnWifiBinding) ViewBinding;
        mNetWifiAdapter = new NetWifiAdapter(mViewModel, mCommonHighlight);mNetWifiSaveAdapter = new NetWifiSaveAdapter(mViewModel, mCommonHighlight);mViewBinding.rcvWifiList.setLayoutManager(new LinearLayoutManager(mBaseActivity.getApplicationContext()));mViewBinding.rcvWifiList.setAdapter(mNetWifiAdapter);mViewBinding.rcvWifiList.setHasFixedSize(true);mLoadAnimation = AnimationUtils.loadAnimation(mBaseActivity.getApplicationContext(), R.anim.rotate_cw);mViewBinding.rcvWifiSave.setLayoutManager(new LinearLayoutManager(mBaseActivity.getApplicationContext()));mViewBinding.rcvWifiSave.setAdapter(mNetWifiSaveAdapter);mViewBinding.rcvWifiSave.setHasFixedSize(true);mViewBinding.rcvWifiSave.setItemAnimator(null);
        mAnimationDrawable = (AnimationDrawable) mViewBinding.ivWifiListAnimLoad.getDrawable();
        mViewBinding.tgBtnWifiSwitch.setFadeBack(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewBinding.tgBtnWifiSwitch.setFadeBack(true);
    }

    @Override
    public void initData() {
        super.initData();
        mCommonHighlight.mCommonHighlightRequest.getCommonHighlight().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "integer = " + integer);
                if (integer.equals(HIGHLIGHT_WIFI)) {
                    mViewBinding.flWifi.setSelected(true);
                    mViewBinding.ivWifiIcon.setSelected(true);
} else {
mViewBinding.flWifi.setSelected(false);
                    mViewBinding.ivWifiIcon.setSelected(false);
                    }
            }
        });
        mViewModel.mWifiRequest.getWifiEnableStateLiveData().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "integer = " + integer);
                switch (integer) {
                    case WifiManager.WIFI_STATE_ENABLED:if (mViewBinding.tgBtnWifiSwitch.isFadeBack()) {
                            mViewBinding.tgBtnWifiSwitch.setCheckedNoEvent(true);
                        } else {
                            mViewBinding.tgBtnWifiSwitch.setCheckedImmediatelyNoEvent(true);
                        }
                        mViewBinding.tvWifiOff.setVisibility(View.GONE);
                        mViewBinding.ivWifiOff.setVisibility(View.GONE);
                        mViewBinding.ivWifiListLoad.setVisibility(View.GONE);
                        mViewBinding.groupWifiList.setVisibility(View.VISIBLE);
                        if (mViewModel.mWifiRequest.getWifiUIWLANState().getValue().equals(WIFI_UI_STATE_NO_WLAN_ITEM)) {
                            mViewBinding.ivNoWlanLine.setVisibility(View.VISIBLE);
                            mViewBinding.tvNoWlan.setVisibility(View.VISIBLE);
                        }
                        if (mViewModel.mWifiRequest.getWifiUISAVEState().getValue().equals(WIFI_UI_STATE_NO_SAVE_ITEM)) {
                            mViewBinding.tvWifiSave.setVisibility(View.GONE);
                            mViewBinding.ivWlanLineFinish.setVisibility(View.GONE);
                        }
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:case WifiManager.WIFI_STATE_DISABLED:mViewBinding.groupWifiList.setVisibility(View.GONE);
                        mViewBinding.tvWifiSave.setVisibility(View.GONE);
                        mViewBinding.ivWlanLineFinish.setVisibility(View.GONE);
                        mViewBinding.ivNoWlanLine.setVisibility(View.GONE);
                        mViewBinding.tvNoWlan.setVisibility(View.GONE);
                        if (mViewBinding.tgBtnWifiSwitch.isFadeBack()) {
                            mViewBinding.tgBtnWifiSwitch.setCheckedNoEvent(false);
                        } else {
                            mViewBinding.tgBtnWifiSwitch.setCheckedImmediatelyNoEvent(false);
                        }
                        mViewBinding.ivWifiOff.setVisibility(View.VISIBLE);
                        mViewBinding.tvWifiOff.setVisibility(View.VISIBLE);
                        mViewBinding.ivWifiListLoad.setVisibility(View.GONE);
                        mViewBinding.ivWifiListAnimLoad.setVisibility(View.GONE);
                        mAnimationDrawable.stop();
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:default:
                        break;
                }
            }
        });

mViewModel.mWifiRequest.getWifiConnectFail().observe(mBaseActivity, new Observer<Boolean>() {
    @Override
    public void onChanged(@Nullable Boolean aBoolean) {
        if(aBoolean){
            if (mFailDialog == null) {
                mFailDialog = CommonDialog.getCommonDialog(mBaseActivity);
            }
            mFailDialog.initIntView(R.string.toast_hint_wifi_connect_err, R.drawable.icon_failure);
            if (!mFailDialog.isShowing()) {
                mFailDialog.show();
            }
        }
    }
});

        mViewModel.mWifiRequest.getWifiInfoListSearchLiveData().observe(mBaseActivity, aBoolean -> {
            LogUtil.debugD(TAG, "aBoolean = " + aBoolean);
            if (mViewBinding.tgBtnWifiSwitch.isChecked()) {
                if (aBoolean) {if (mViewBinding.tgBtnWifiSwitch.isChecked()) {
                        mViewBinding.ivWifiListLoad.setVisibility(View.GONE);
                        mViewBinding.ivWifiListAnimLoad.setVisibility(View.VISIBLE);
                        if (!mAnimationDrawable.isRunning()) {
                            mAnimationDrawable.start();
                        }
                    }
                } else {
                    mAnimationDrawable.stop();
                    mViewBinding.ivWifiListAnimLoad.setVisibility(View.GONE);
                    mViewBinding.ivWifiListLoad.setVisibility(View.VISIBLE);
                }
            }
        });

        mViewModel.mWifiRequest.getWifiInfoListLiveData().observe(mBaseActivity, new Observer<List<WifiInfoBean>>() {
            @Override
            public void onChanged(@Nullable List<WifiInfoBean> wifiInfoBeans) {
                if (wifiInfoBeans == null) {
                    return;
                }
                mNetWifiAdapter.notifyEntitySetChanged(wifiInfoBeans);}
        });
        mViewModel.mWifiRequest.getWifiInfoSaveListLiveData().observe(mBaseActivity, new Observer<List<WifiInfoBean>>() {
            @Override
            public void onChanged(@Nullable List<WifiInfoBean> wifiInfoBeans) {
                if (wifiInfoBeans == null) {
                    LogUtil.w(TAG, "wifiInfoBeans = null ");
                    return;
                }
                mNetWifiSaveAdapter.notifyEntitySetChanged(wifiInfoBeans);
            }
        });


        mViewModel.mWifiRequest.getWifiConnectingLiveData().observe(mBaseActivity, new Observer<WifiInfoBean>() {
            @Override
            public void onChanged(@Nullable WifiInfoBean wifiInfoBean) {
                if (wifiInfoBean == null) {
                    return;
                }
                mNetWifiSaveAdapter.notifyWifiConnecting(wifiInfoBean);}
        });


        mViewModel.mWifiRequest.getWifiConnCfmLiveData().observe(mBaseActivity, wifiInfoBean -> {
            if (wifiInfoBean == null) {
                return;
            }
            mWifiConnDialog = WifiConnDialog.newInstance(wifiInfoBean.getScanResult().SSID, 8);mWifiConnDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    .setLayout((int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_width)), (int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_height)))
                    .setGravity(Gravity.TOP)
                    .setOffsetY((int) (mBaseActivity.getResources().getDimension(R.dimen.popup_big_y_totop)))
                    .setOnPositiveButtonClickListener((dialogFragment, view, object) -> {
                        mViewModel.mWifiRequest.requestConnect((String) object, wifiInfoBean);})
                    .show(mBaseActivity.getSupportFragmentManager(), getClass().getSimpleName());
        });

        mViewModel.mWifiRequest.getWifiDisconnCfmLiveData().observe(mBaseActivity, wifiInfoBean -> {
            if (wifiInfoBean == null) {
                return;
            }
            mWifiDisconnDialog = WifiDisconnDialog.newInstance();mWifiDisconnDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    .setLayout(800, 480)
                    .setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL)
                    .setOutCancel(true)
                    .setOnPositiveButtonClickListener((dialogFragment, view, object) -> {
                        mViewModel.mWifiRequest.requestDisconnect();})
                    .setOnNegativeButtonClickListener((dialogFragment, view, object) -> {
                        mViewModel.mWifiRequest.requestForget(wifiInfoBean);})
                    .show(mBaseActivity.getSupportFragmentManager(), getClass().getSimpleName());
        });


        mViewModel.mWifiRequest.getWifiReconnCfmLiveData().observe(mBaseActivity, wifiInfoBean -> {
            if (wifiInfoBean == null){
                return;}
            switch (wifiInfoBean.getState()) {case SAVED:case PASSWORD_ERROR:if (mNetWifiSaveAdapter.getBean().getState() == CONNECT) {
                        mWifiReconnDialog = WifiReconnDialog.newInstance();
                        mWifiReconnDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                                .setLayout(800, 480)
                                .setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL)
                                .setOutCancel(true)
                                .setOnPositiveButtonClickListener((dialogFragment, view, object) -> {
                                    mViewModel.mWifiRequest.requestForget(wifiInfoBean);})
                                .setOnNegativeButtonClickListener((dialogFragment, view, object) -> {

                                    mViewModel.mWifiRequest.requestConnect(wifiInfoBean);})
                                .show(mBaseActivity.getSupportFragmentManager(), getClass().getSimpleName());
                    } else {
                        mViewModel.mWifiRequest.requestConnect(wifiInfoBean);
                    }
                    break;
                case CONNECT:
                case CONNECTING:
                case NOT_CONNECT_WEP:
                case NOT_CONNECT_WPA:
                case NOT_CONNECT_WPS:
                case NOT_CONNECT_OPEN:
                default:
                    break;
            }
        });
        mViewModel.mWifiRequest.getMpasswordError().observe(mBaseActivity, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    if (mCommonDialog == null) {
                        mCommonDialog = CommonDialog.getCommonDialog(mBaseActivity);
                    }
                    mCommonDialog.initStringView(s, R.drawable.icon_failure);
                    if (!mCommonDialog.isShowing()) {
                        mCommonDialog.show();
                    }
                }
            }
        });
        mViewModel.mWifiRequest.getWifiUISAVEState().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "integer = " + integer);
                if (mViewBinding.tgBtnWifiSwitch.isChecked()) {
                    if (integer.equals(WIFI_UI_STATE_NO_SAVE_ITEM)) {
                        mViewBinding.tvWifiSave.setVisibility(View.GONE);
                        mViewBinding.ivWlanLineFinish.setVisibility(View.GONE);
                    } else if (integer.equals(WIFI_UI_STATE_SAVE_ITEM)) {
                        mViewBinding.tvWifiSave.setVisibility(View.VISIBLE);
                        mViewBinding.ivWlanLineFinish.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mViewModel.mWifiRequest.getWifiUIWLANState().observe(mBaseActivity, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                LogUtil.debugD(TAG, "integer = " + integer);
                if (mViewBinding.tgBtnWifiSwitch.isChecked()) {
                    mViewBinding.ivWifiOff.setVisibility(View.GONE);
                    mViewBinding.tvWifiOff.setVisibility(View.GONE);
                    if (integer.equals(WIFI_UI_STATE_NO_WLAN_ITEM)) {
                        mViewBinding.ivNoWlanLine.setVisibility(View.VISIBLE);
                        mViewBinding.tvNoWlan.setVisibility(View.VISIBLE);
                    } else if (integer.equals(WIFI_UI_STATE_WLAN_ITEM)) {
                        mViewBinding.ivNoWlanLine.setVisibility(View.GONE);
                        mViewBinding.tvNoWlan.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    @Override
    public void initEvent() {
        super.initEvent();


        mViewBinding.tgBtnWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    mViewModel.mWifiRequest.requestEnableState(b);
                    if (b) {mViewModel.mWifiRequest.requestSearch();}
                    mCommonHighlight.mCommonHighlightRequest.requestHighlightModule(HIGHLIGHT_WIFI);
                }
            }
        });

    }

    @Override
    public void showOrHide(boolean b) {
        super.showOrHide(b);
        if (!b) {
            if (mViewBinding.tgBtnWifiSwitch.isChecked()) {
                mAnimationDrawable.stop();
                mViewBinding.ivWifiListAnimLoad.setVisibility(View.GONE);
                mViewBinding.ivWifiListLoad.setVisibility(View.VISIBLE);
            }

            if (Objects.nonNull(mWifiConnDialog)) {
                mWifiConnDialog.dismiss();}
            if (Objects.nonNull(mWifiDisconnDialog)) {
                mWifiDisconnDialog.dismiss();
            }
            if (Objects.nonNull(mWifiReconnDialog)) {
                mWifiReconnDialog.dismiss();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.d(TAG);
        if (mViewBinding.tgBtnWifiSwitch.isChecked()) {
            mAnimationDrawable.stop();
            mViewBinding.ivWifiListAnimLoad.setVisibility(View.GONE);
            mViewBinding.ivWifiListLoad.setVisibility(View.VISIBLE);
        }
       if(mViewModel.mWifiRequest.getHandler()!=null){
           mViewModel.mWifiRequest.getHandler().removeMessages(1);
       }
    }

    @Override
    public void onDestroy() {
        LogUtil.debugD(TAG, "");
        mViewModel.mWifiRequest.getMpasswordError().setValue(null);
        mNetWifiAdapter.setOnEntityItemClickListener(null);
        mNetWifiSaveAdapter.setOnEntityItemClickListener(null);
        mCommonDialog = null;
    }
}

