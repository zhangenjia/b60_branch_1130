package com.adayo.app.setting.view.fragment.factory.product.Time;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.configuration.IFragmentConfig;
import com.adayo.app.setting.databinding.FragmentDisplayTimeBinding;
import com.adayo.app.setting.view.fragment.DisplaySetFragment;
import com.adayo.app.setting.base.BaseFragment;

public class TimeFragment extends BaseFragment<FragmentDisplayTimeBinding> {
    private final static String TAG = TimeFragment.class.getSimpleName();
    private IFragmentConfig timeFunc;

   private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;




    public static DisplaySetFragment newInstance(String param1, String param2) {
        DisplaySetFragment fragment = new DisplaySetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentDisplayTimeBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentDisplayTimeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        timeFunc = ConfigurationManager.getInstance().getTimeFunc();
        timeFunc.registerFragment(this, mViewBinding);
        timeFunc.bindData();
    }

    @Override
    protected void initView() {
        super.initView();
        timeFunc.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        timeFunc.initData();
    }


    @Override
    protected void initEvent() {
        super.initEvent();
        timeFunc.initEvent();
    }


    @Override
    public void onResume() {
        super.onResume();
        timeFunc.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        timeFunc.onStart();

    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        super.onHiddenChanged(isHidden);
        timeFunc.onHiddenChanged(isHidden);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        timeFunc.onConfigurationChanged(newConfig);
    }
}