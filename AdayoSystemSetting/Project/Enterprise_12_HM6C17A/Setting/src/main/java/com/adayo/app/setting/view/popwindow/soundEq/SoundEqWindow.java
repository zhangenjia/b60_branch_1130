package com.adayo.app.setting.view.popwindow.soundEq;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.base.BasePopActivity;
import com.adayo.app.setting.databinding.PopupSoundEqBinding;
import com.adayo.app.setting.viewmodel.fragment.sound.HighSoundViewModel;
import com.adayo.app.base.LogUtil;


public class SoundEqWindow extends BasePopActivity<PopupSoundEqBinding> {
    private final static String TAG = SoundEqWindow.class.getSimpleName();
    private HighSoundViewModel mViewModel;
    private ISoundEQ soundEQFunc;

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        ConfigurationManager.getInstance().init(this);
        ConfigurationManager.getInstance().getConfiguration();
        soundEQFunc = ConfigurationManager.getInstance().getISoundEQFunc();
        soundEQFunc.registerFragment(this, mViewBinding);
    }

    @Override
    protected PopupSoundEqBinding bindView() {
        return PopupSoundEqBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        soundEQFunc.initView();
    }

    @Override
    protected void initData() {
        soundEQFunc.initData();
    }

    @Override
    protected void initEvent() {
       LogUtil.debugD(TAG, "");
        soundEQFunc.initEvent();
    }

    @Override
    protected void freeEvent() {
        super.freeEvent();
        soundEQFunc.freeEvent();
    }

}
