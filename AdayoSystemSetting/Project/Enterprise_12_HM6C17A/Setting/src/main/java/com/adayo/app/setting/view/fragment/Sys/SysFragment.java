package com.adayo.app.setting.view.fragment.Sys;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.configuration.IFragmentConfig;
import com.adayo.app.setting.databinding.FragmentSysBinding;
import com.adayo.app.setting.base.BaseFragment;
import com.adayo.app.base.LogUtil;


public class SysFragment extends BaseFragment<FragmentSysBinding> {
    private final static String TAG = SysFragment.class.getSimpleName();
    private IFragmentConfig sysFunc;


    public static SysFragment newInstance(String param1, String param2) {
        SysFragment fragment = new SysFragment();
        Bundle args = new Bundle();
        args.putString(BDL_KEY_PARAM1, param1);
        args.putString(BDL_KEY_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentSysBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSysBinding.inflate(inflater, container,false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
       LogUtil.debugD(TAG,"");
        sysFunc = ConfigurationManager.getInstance().getSysFunc();
        sysFunc.registerFragment(this, mViewBinding);
        sysFunc.bindData();
    }

    @Override
    protected void initView() {
        super.initView();
       LogUtil.debugD(TAG,"");
        sysFunc.initView();
    }

    @Override
    protected void initData() {
        super.initData();
       LogUtil.debugD(TAG,"");
        sysFunc.initData();

    }

    @Override
    protected void initEvent() {
        super.initEvent();
       LogUtil.debugD(TAG,"");
        sysFunc.initEvent();

    }

    @Override
    protected void hideView() {
        super.hideView();
       LogUtil.debugD(TAG,"");
        sysFunc.hideView();
    }

    @Override
    public void onStart() {
        super.onStart();
        sysFunc.onStart();
    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        super.onHiddenChanged(isHidden);
        sysFunc.onHiddenChanged(isHidden);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sysFunc.onConfigurationChanged(newConfig);
    }
}