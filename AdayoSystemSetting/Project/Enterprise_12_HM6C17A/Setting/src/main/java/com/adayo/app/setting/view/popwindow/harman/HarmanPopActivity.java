package com.adayo.app.setting.view.popwindow.harman;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.R;
import com.adayo.app.setting.base.BasePopActivity;
import com.adayo.app.setting.databinding.ActivityHarmanBinding;
import com.adayo.app.setting.skin.SkinUtil;

import com.adayo.app.base.LogUtil;

public class HarmanPopActivity extends BasePopActivity<ActivityHarmanBinding> implements View.OnTouchListener {
    private final static String TAG = HarmanPopActivity.class.getSimpleName();
    private IHarmanActivity harmanFunc;


    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        LogUtil.d(TAG);
        ConfigurationManager.getInstance().init(this);
        ConfigurationManager.getInstance().getConfiguration();
        harmanFunc = ConfigurationManager.getInstance().getHarmanActivity();
        harmanFunc.registerFragment(this, mViewBinding);
    }


    @Override
    protected ActivityHarmanBinding bindView() {
        return ActivityHarmanBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        super.initView();
        onSystemUIVisibility(true);
        harmanFunc.initView();
        int x = ConfigurationManager.getInstance().getVehicleUI();
        if (x == 0) {
            SkinUtil.setImageResource(mViewBinding.ivBackground, R.drawable.offroad_system_settings_sound_reset_car_model_1);
        } else if (x == 1) {
            SkinUtil.setImageResource(mViewBinding.ivBackground, R.drawable.offroad_system_settings_sound_reset_car_model);
        }

    }

    @Override
    protected void initData() {
        super.initData();
        harmanFunc.initData();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        harmanFunc.initEvent();
        ;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        LogUtil.debugD(TAG, "onTouch");
        boolean b = harmanFunc.onTouch(view, motionEvent);
        return b;
    }



    private void onSystemUIVisibility(boolean visibility) {
        LogUtil.debugD(TAG, "visibility =" + visibility);
        View decorView = this.getWindow().getDecorView();
        if (visibility) {this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);} else {decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);}
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG);
    }


}
