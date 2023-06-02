
package com.adayo.app.setting.view.fragment.Sound;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.databinding.FragmentSoundBinding;
import com.adayo.app.setting.base.BaseFragment;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.app.base.LogUtil;


public class SoundFragment extends BaseFragment<FragmentSoundBinding> {

    private final static String TAG = SoundFragment.class.getSimpleName();
    private ISoundConfig soundFunc;


    public static SoundFragment newInstance(String param1, String param2) {
        SoundFragment fragment = new SoundFragment();
        Bundle args = new Bundle();
        args.putString(BDL_KEY_PARAM1, param1);
        args.putString(BDL_KEY_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected FragmentSoundBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSoundBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        soundFunc = ConfigurationManager.getInstance().getSoundFunc();
        soundFunc.registerFragment(this, mViewBinding);
        soundFunc.operationFragment();

    }

    @Override
    protected void initView() {
        super.initView();
        soundFunc.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.debugD(TAG, "");
        soundFunc.initData();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        soundFunc.initEvent();

    }

    @Override
    protected void freeEvent() {
        super.freeEvent();
    }

    @Override
    protected void freeData() {
        super.freeData();
    }

    @Override
    protected void freeView() {
        super.freeView();
    }


    @Override
    public void onStart() {
        super.onStart();
        soundFunc.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        soundFunc.onResume();
    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        super.onHiddenChanged(isHidden);
        soundFunc.onHiddenChanged(isHidden);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AAOP_HSkin.getInstance().refreshLanguage();
        soundFunc.onConfigurationChanged(newConfig);
    }
}