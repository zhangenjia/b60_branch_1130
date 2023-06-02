package com.adayo.app.setting.view.fragment.Sys;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.adayo.app.setting.configuration.IFragmentConfig;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.FragmentSysBinding;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.setting.view.dialog.CfmDialog;
import com.adayo.app.setting.view.dialog.WifiConnDialog;
import com.adayo.app.setting.highlight.SysHighlight;
import com.adayo.app.setting.viewmodel.fragment.Sys.LowSysViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.app.base.LogUtil;
import com.lt.library.util.context.ContextUtil;

import java.util.Objects;

import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_NO;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_SYSTEM_MESSAGE;
import static com.adayo.app.setting.model.constant.ParamConstant.HIGHLIGHT_UPGRADE;

public class LowSysFragment implements IFragmentConfig<SysFragment, FragmentSysBinding> {
    private final static String TAG = LowSysFragment.class.getSimpleName();
    protected SysFragment fragment;
    private FragmentSysBinding mViewBinding;
    private static final int MULTIPLE_CLICKS = 5;private static final long MULTIPLE_CLICKS_DURATION = 2000;private long[] mMultipleHits = new long[MULTIPLE_CLICKS];private LowSysViewModel mViewModel;
    private CfmDialog mCfmDialog;
    private SysHighlight mSysHighlight;

    @Override
    public void registerFragment(SysFragment fragment, FragmentSysBinding viewbinding) {
       LogUtil.debugD(TAG,"");
        this.fragment = fragment;
        this.mViewBinding = viewbinding;
    }

    @Override
    public void bindData() {
       LogUtil.debugD(TAG,"");
        mViewModel = ViewModelProviders.of(fragment.requireActivity()).get(LowSysViewModel.class);
        mSysHighlight = ViewModelProviders.of(fragment.requireActivity()).get(SysHighlight.class);

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
       LogUtil.debugD(TAG,"");
        mViewModel.mLowVerRequest.getHasUsbFileLiveData().observe(fragment.getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                LogUtil.debugD(TAG,"aBoolean ="+aBoolean);
                mViewBinding.btnUpgradeUsb.setEnabled(aBoolean);
            }
        });
        mViewModel.mLowVerRequest.getVerLiveData().observe(fragment.getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
               LogUtil.debugD(TAG, "ban ben xin xi = " + s);
                if (TextUtils.isEmpty(s)) {
                    mViewBinding.tvSysCurrentVerContent.setText(R.string.sys_current_ver_nothing);
                } else {
                    mViewBinding.tvSysCurrentVerContent.setText(s);
                }
            }
        });
        mSysHighlight.mSysHighlightRequest.getSysHighlight().observe(fragment.getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer.equals(HIGHLIGHT_NO)) {
                    changeSelected(mViewBinding.clSys, false);
                    changeSelected(mViewBinding.clUpgrade, false);
                    } else if (integer.equals(HIGHLIGHT_SYSTEM_MESSAGE)) {
                    changeSelected(mViewBinding.clSys, true);
                    changeSelected(mViewBinding.clUpgrade, false);
                    } else if (integer.equals(HIGHLIGHT_UPGRADE)) {
                    changeSelected(mViewBinding.clSys, false);
                    changeSelected(mViewBinding.clUpgrade, true);
                    }
            }
        });
    }

    @Override
    public void initView() {
        LogUtil.debugD(TAG, "");
        mViewBinding.btnUpgradeOta.setVisibility(View.GONE);
        mViewBinding.tvBadge.setVisibility(View.GONE);
        SkinUtil.setBackground(mViewBinding.ivUpgradeBg,R.drawable.upgrade_last_low);
        }

    @Override
    public void initEvent() {
       LogUtil.debugD(TAG,"");
mViewBinding.tvSysCurrentVerTitle.setOnTouchListener((view, motionEvent) -> multipleClick());
        mViewBinding.tvSysCurrentVerContent.setOnTouchListener((view, motionEvent) -> multipleClick());
        mViewBinding.btnSysRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LogUtil.debugD(TAG, "hui fu chu chang she zhi");
                if(view.isPressed()){
                    mSysHighlight.mSysHighlightRequest.requestHighlightModule(HIGHLIGHT_SYSTEM_MESSAGE);
                }
                mCfmDialog = CfmDialog.newInstance(fragment.getString(R.string.sys_restore_reset_dlg_title),
                        fragment.getString(R.string.sys_restore_reset_dlg_msg),
                        fragment.getString(R.string.sys_restore_reset_dlg_neutral_btn),
                        fragment.getString(R.string.sys_restore_reset_dlg_positive_btn));
                mCfmDialog.setLayout(800, 480)
                        .setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL)
                        .setOutCancel(true)
                        .setOnPositiveButtonClickListener((dialogFragment, view1, object) -> {
                            if(!ActivityManager.isUserAMonkey()){
                            execFactoryReset();}
                        })
                        .show(fragment.getFragmentManager(), getClass().getSimpleName());
            }
        });


        mViewBinding.btnUpgradeUsb.setOnClickListener(v -> {
           LogUtil.debugD(TAG, "btnUpgradeUsb");
            if(v.isPressed()){
                mSysHighlight.mSysHighlightRequest.requestHighlightModule(HIGHLIGHT_UPGRADE);
            }
            mViewModel.mLowVerRequest.requestLoUpgrade();
        });

    }

    @Override
    public void hideView() {
       LogUtil.debugD(TAG,"");
        if (Objects.nonNull(mCfmDialog)) {
            mCfmDialog.dismiss();
        }
    }



    @SuppressLint("ObsoleteSdkInt")
    private void execFactoryReset() {
       LogUtil.debugD(TAG, "");
        Intent intent;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            intent = new Intent(Intent.ACTION_MASTER_CLEAR);
        } else {
            intent = new Intent(Intent.ACTION_FACTORY_RESET);
            intent.setPackage("android");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
            intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, false);
            intent.putExtra(Intent.EXTRA_WIPE_ESIMS, true);
        }
        ContextUtil.getInstance().getApplicationContext().sendBroadcast(intent);
    }


    private boolean multipleClick() {
       LogUtil.debugD(TAG, "");
        System.arraycopy(mMultipleHits, 1, mMultipleHits, 0, mMultipleHits.length - 1);mMultipleHits[mMultipleHits.length - 1] = SystemClock.uptimeMillis();if (mMultipleHits[0] >= (SystemClock.uptimeMillis() - MULTIPLE_CLICKS_DURATION)) {
            String password=SystemProperties.get(ParamConstant.FACTORY_MODE);
            if(!(password == null || password.isEmpty()) ){
                WifiConnDialog mWifiConnDialog = WifiConnDialog.newInstance(fragment.getString(R.string.sys_1), 0);mWifiConnDialog.setWindowType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                        .setLayout((int) (fragment.getResources().getDimension(R.dimen.popup_big_width)), (int) (fragment.getResources().getDimension(R.dimen.popup_big_height)))
                        .setGravity(Gravity.TOP)
                        .setOffsetY((int) (fragment.getResources().getDimension(R.dimen.popup_big_y_totop)))
                        .setOnPositiveButtonClickListener((dialogFragment, view, object) -> {
                            if (object.equals(password)) {
                                Intent intent = ContextUtil.getInstance().getApplicationContext().getPackageManager().getLaunchIntentForPackage(ParamConstant.INTENT_COMP_NAME_FTY_MODE);
                                fragment.startActivity(intent);
                            } else {
                                Toast.makeText(fragment.getActivity(), "密码输入错误，请重新输入",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show(fragment.getFragmentManager(), getClass().getSimpleName());
            }else {
                Intent intent = ContextUtil.getInstance().getApplicationContext().getPackageManager().getLaunchIntentForPackage(ParamConstant.INTENT_COMP_NAME_FTY_MODE);
                fragment.startActivity(intent);
            }
            mMultipleHits = new long[MULTIPLE_CLICKS];}
        return false;
    }

    @Override
    public void onStart() {
        mSysHighlight.mSysHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        mSysHighlight.mSysHighlightRequest.requestHighlightModule(HIGHLIGHT_NO);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        AAOP_HSkin.getInstance().refreshLanguage();
    }

}
