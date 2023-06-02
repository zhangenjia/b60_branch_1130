package com.adayo.app.setting.view.popwindow.soundEq;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.adayo.app.setting.databinding.PopupSoundEqBinding;
import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.viewmodel.fragment.sound.HighSoundViewModel;

import java.util.Arrays;
import java.util.Map;


public class HighSoundEqWindow implements SeekBar.OnSeekBarChangeListener, ISoundEQ<SoundEqWindow, PopupSoundEqBinding> {
    private final static String TAG = HighSoundEqWindow.class.getSimpleName();
    private HighSoundViewModel mViewModel;
    private SoundEqWindow mActivity;
    private PopupSoundEqBinding mViewBinding;
    private boolean isChange = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            isChange=true;
        }
    };
    private final static int FROMUSER_TOUCH = 1;


    @Override
    public void registerFragment(SoundEqWindow activity, PopupSoundEqBinding viewbinding) {
        mActivity = activity;
        mViewBinding = viewbinding;
        mViewModel = ViewModelProviders.of(mActivity).get(HighSoundViewModel.class);
    }


    @Override
    public void initView() {
        LogUtil.debugD(TAG, "");
        onSystemUIVisibility(true);
        Window window = mActivity.getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
    }

    @Override
    public void initData() {
        mViewModel.mHighSoundRequest.getEqLiveData().observe(mActivity, new Observer<Map<Integer, int[]>>() {
            @Override
            public void onChanged(@Nullable Map<Integer, int[]> integerMap) {
                LogUtil.debugD(TAG, "Eq integerMap =" + integerMap);
                if (isChange) {
                    Integer eqMode = integerMap.keySet().iterator().next();
                    int[] ints = integerMap.get(eqMode);
                    LogUtil.debugD("ints = " + Arrays.toString(ints));
                    mViewBinding.sbPopupEqHignSlide.setProgress(ints[2]);mViewBinding.sbPopupEqHignSlide.setMin(0);
                    mViewBinding.sbPopupEqHignSlide.setMax(20);
                    mViewBinding.tvPopupEqHignValue.setText(convertEffectValue(ints[2]));mViewBinding.sbPopupEqMidSlide.setProgress(ints[1]);mViewBinding.sbPopupEqMidSlide.setMin(0);
                    mViewBinding.sbPopupEqMidSlide.setMax(20);
                    mViewBinding.tvPopupEqMidValue.setText(convertEffectValue(ints[1]));


                    mViewBinding.sbPopupEqLowSlide.setProgress(ints[0]);mViewBinding.sbPopupEqLowSlide.setMin(0);
                    mViewBinding.sbPopupEqLowSlide.setMax(20);
                    mViewBinding.tvPopupEqLowValue.setText(convertEffectValue(ints[0]));
                }
            }
        });
    }

    @Override
    public void initEvent() {
        LogUtil.debugD(TAG, "");
        mViewBinding.sbPopupEqHignSlide.setOnSeekBarChangeListener(this);mViewBinding.sbPopupEqMidSlide.setOnSeekBarChangeListener(this);
        mViewBinding.sbPopupEqLowSlide.setOnSeekBarChangeListener(this);

        mViewBinding.btnPopupEqClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "btnPopupEqClose");
                mActivity.finish();
            }
        });
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {mHandler.removeMessages(FROMUSER_TOUCH);
            isChange=false;
            mHandler.sendEmptyMessageDelayed(FROMUSER_TOUCH,5000);if (seekBar == mViewBinding.sbPopupEqHignSlide) {
                mViewModel.mHighSoundRequest.requestEqBand(2, progress);
                mViewBinding.tvPopupEqHignValue.setText(convertEffectValue(progress));} else if (seekBar == mViewBinding.sbPopupEqMidSlide) {
                mViewModel.mHighSoundRequest.requestEqBand(1, progress);
                mViewBinding.tvPopupEqMidValue.setText(convertEffectValue(progress));
            } else if (seekBar == mViewBinding.sbPopupEqLowSlide) {
                mViewModel.mHighSoundRequest.requestEqBand(0, progress);
                mViewBinding.tvPopupEqLowValue.setText(convertEffectValue(progress));
            } else {
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void freeEvent() {
        mViewBinding.sbPopupEqHignSlide.setOnSeekBarChangeListener(null);mViewBinding.sbPopupEqMidSlide.setOnSeekBarChangeListener(null);
        mViewBinding.sbPopupEqLowSlide.setOnSeekBarChangeListener(null);

        mViewBinding.btnPopupEqClose.setOnClickListener(null);
        mHandler = null;

    }


    private String convertEffectValue(int i) {
        String s;
        int value = i - 10;
        if (value < 0) {
            s = value + " ";
        } else if (value > 0) {
            s = "+" + value + " ";
        } else {
            s = String.valueOf(value);
        }
        return s;
    }



    private void onSystemUIVisibility(boolean visibility) {
        LogUtil.debugD(TAG, "visibility =" + visibility);
        View decorView = mActivity.getWindow().getDecorView();
        if (visibility) {mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);} else {decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);}
    }
}
