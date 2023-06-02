package com.adayo.app.setting.view.popwindow.harman;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.databinding.ActivityHarmanBinding;
import com.adayo.app.setting.viewmodel.fragment.sound.HighSoundViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HighHarmanActivity implements IHarmanViewOutData, IHarmanActivity<HarmanPopActivity, ActivityHarmanBinding> {
    private final static String TAG = HighHarmanActivity.class.getSimpleName();
    private HighSoundViewModel mViewModel;
    private IHarmanViewInData mIHarmanViewInData;
    private List<FastBitBean> mFastBitBeanList = new ArrayList<>();
    private HarmanPopActivity mActivity;
    private ActivityHarmanBinding mViewBinding;
    private List<ImageView> mFastBitImage = new ArrayList<>();
    private boolean isChange = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            LogUtil.d(TAG,"isChange handleMessage");
            isChange = true;
        }
    };
    private final static int FROMUSER_TOUCH = 1;

    @Override
    public void registerFragment(HarmanPopActivity activity, ActivityHarmanBinding viewbinding) {
        mActivity = activity;
        mViewBinding = viewbinding;
        mViewModel = ViewModelProviders.of(mActivity).get(HighSoundViewModel.class);
        mIHarmanViewInData = mViewBinding.viewHarman;
        mIHarmanViewInData = mIHarmanViewInData.init(this);
        mIHarmanViewInData.initPostion(mViewModel.mHighSoundRequest.getSoundFiledLiveData().getValue()[0], mViewModel.mHighSoundRequest.getSoundFiledLiveData().getValue()[1]);
        newFastBit(534, 656, 534, 780, 751, 626, 751, 780, 644, 694, five());newFastBit(672, 392, 672, 546, 889, 392, 889, 546, 780, 459, five());newFastBit(776, 193, 776, 347, 993, 193, 993, 347, 885, 261, four());newFastBit(1049, 193, 1049, 347, 1266, 193, 1266, 347, 1157, 261, three());newFastBit(1321, 193, 1321, 347, 1538, 193, 1538, 347, 1429, 261, two());newFastBit(1422, 392, 1422, 546, 1639, 392, 1639, 546, 1530, 459, one());newFastBit(1561, 626, 1561, 780, 1778, 626, 1778, 780, 1670, 694, one());mIHarmanViewInData.initFastBit(mFastBitBeanList);
        mFastBitImage.add(mViewBinding.ivLeftBottom);
        mFastBitImage.add(mViewBinding.ivLeftMid);
        mFastBitImage.add(mViewBinding.ivLeftTop);
        mFastBitImage.add(mViewBinding.ivMidTop);
        mFastBitImage.add(mViewBinding.ivRightTop);
        mFastBitImage.add(mViewBinding.ivRightMid);
        mFastBitImage.add(mViewBinding.ivRightBottom);
        mViewBinding.tvTitle.setVisibility(View.GONE);
        mViewBinding.ivActivityHarmanLogo.setVisibility(View.VISIBLE);

    }


    private void newFastBit(int leftTopX, int leftTopY, int leftBottomX, int leftBottomY, int rightTopX, int rightTopY, int rightBottomX, int rightBottomY, int centerX, int centerY, List<Bitmap> list) {
        LogUtil.debugD(TAG, "");
        FastBitBean fastBitBean = new FastBitBean(leftTopX, leftTopY, leftBottomX, leftBottomY, rightTopX, rightBottomX, rightBottomY, centerX, centerY, rightTopY, list);
        mFastBitBeanList.add(fastBitBean);
    }


    @Override
    public void initView() {
        LogUtil.debugD(TAG, "");
}

    @Override
    public void initData() {
        LogUtil.debugD(TAG, "");
        mViewModel.mHighSoundRequest.getSoundFiledLiveData().observe(mActivity, new Observer<int[]>() {
            @Override
            public void onChanged(@Nullable int[] ints) {
                LogUtil.d(TAG, "updataView x =" + ints[0] + "y = " + ints[1]);
                if (isChange) {
                    mIHarmanViewInData.updataPostion(ints[0], ints[1]);
                }
            }
        });
        mViewModel.mHighSoundRequest.getLoudnessSwitch().observe(mActivity, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                LogUtil.debugD(TAG, "aBoolean " + aBoolean);
                if (aBoolean) {
                    mViewBinding.cbQls.setChecked(true);
                } else {
                    mViewBinding.cbQls.setChecked(false);
                }
            }
        });
    }

    @Override
    public void initEvent() {
        mViewBinding.btnHarmanClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "");
                mActivity.finish();
            }
        });
        mViewBinding.cbQls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "QLS");
                if (view.isPressed()) {
                    if (mViewBinding.cbQls.isChecked()) {
                        mViewModel.mHighSoundRequest.requestLoudnessSwitch(true);
                    } else {
                        mViewModel.mHighSoundRequest.requestLoudnessSwitch(false);
                    }
                }
            }
        });
        mViewBinding.flHarmanBackground.setOnTouchListener(mActivity);
        mViewBinding.btHarmanReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "");
                if (view.isPressed()) {
                    mViewModel.mHighSoundRequest.requestSoundFiled(0, 0);
                    mIHarmanViewInData.resetView();
                }
            }
        });
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mIHarmanViewInData.onTouchHarman(motionEvent);
        return true;
    }

    @Override
    public void setSoundFiled(int x, int y) {
        LogUtil.debugD(TAG, "x = " + x + "y = " + y);
        mHandler.removeMessages(FROMUSER_TOUCH);
        isChange = false;
        mHandler.sendEmptyMessageDelayed(FROMUSER_TOUCH, 5000);mViewModel.mHighSoundRequest.requestSoundFiled(x, y);
    }

    @Override
    public void hideFastBit(int x) {
        for (int i = 0; i < mFastBitImage.size(); i++) {
            if (i == x) {
                mFastBitImage.get(x).setVisibility(View.GONE);
            } else {
                mFastBitImage.get(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void showFastBit() {
        for (int i = 0; i < mFastBitImage.size(); i++) {
            mFastBitImage.get(i).setVisibility(View.VISIBLE);
        }
    }

    private List<Bitmap> one() {
        List<Bitmap> mrightAnimList = Arrays.asList(
                getImage(mActivity, R.drawable.right00),
                getImage(mActivity, R.drawable.right01),
                getImage(mActivity, R.drawable.right02),
                getImage(mActivity, R.drawable.right03),
                getImage(mActivity, R.drawable.right04),
                getImage(mActivity, R.drawable.right05),
                getImage(mActivity, R.drawable.right06),
                getImage(mActivity, R.drawable.right07),
                getImage(mActivity, R.drawable.right08),
                getImage(mActivity, R.drawable.right09),
                getImage(mActivity, R.drawable.right10),
                getImage(mActivity, R.drawable.right11),
                getImage(mActivity, R.drawable.right12),
                getImage(mActivity, R.drawable.right13),
                getImage(mActivity, R.drawable.right14),
                getImage(mActivity, R.drawable.right15),
                getImage(mActivity, R.drawable.right16),
                getImage(mActivity, R.drawable.right17),
                getImage(mActivity, R.drawable.right18),
                getImage(mActivity, R.drawable.right19),
                getImage(mActivity, R.drawable.right20),
                getImage(mActivity, R.drawable.right21),
                getImage(mActivity, R.drawable.right22),
                getImage(mActivity, R.drawable.right23),
                getImage(mActivity, R.drawable.right24),
                getImage(mActivity, R.drawable.right25),
                getImage(mActivity, R.drawable.right26),
                getImage(mActivity, R.drawable.right27),
                getImage(mActivity, R.drawable.right28),
                getImage(mActivity, R.drawable.right29),
                getImage(mActivity, R.drawable.right30),
                getImage(mActivity, R.drawable.right31),
                getImage(mActivity, R.drawable.right32),
                getImage(mActivity, R.drawable.right33),
                getImage(mActivity, R.drawable.right34),
                getImage(mActivity, R.drawable.right35),
                getImage(mActivity, R.drawable.right36),
                getImage(mActivity, R.drawable.right37),
                getImage(mActivity, R.drawable.right38),
                getImage(mActivity, R.drawable.right39),
                getImage(mActivity, R.drawable.right40),
                getImage(mActivity, R.drawable.right41),
                getImage(mActivity, R.drawable.right42),
                getImage(mActivity, R.drawable.right43),
                getImage(mActivity, R.drawable.right44),
                getImage(mActivity, R.drawable.right45),
                getImage(mActivity, R.drawable.right46),
                getImage(mActivity, R.drawable.right47),
                getImage(mActivity, R.drawable.right48),
                getImage(mActivity, R.drawable.right49),
                getImage(mActivity, R.drawable.right50),
                getImage(mActivity, R.drawable.right51),
                getImage(mActivity, R.drawable.right52),
                getImage(mActivity, R.drawable.right53),
                getImage(mActivity, R.drawable.right54),
                getImage(mActivity, R.drawable.right55),
                getImage(mActivity, R.drawable.right56),
                getImage(mActivity, R.drawable.right57),
                getImage(mActivity, R.drawable.right58),
                getImage(mActivity, R.drawable.right59),
                getImage(mActivity, R.drawable.right60),
                getImage(mActivity, R.drawable.right61),
                getImage(mActivity, R.drawable.right62),
                getImage(mActivity, R.drawable.right63),
                getImage(mActivity, R.drawable.right64),
                getImage(mActivity, R.drawable.right65),
                getImage(mActivity, R.drawable.right66),
                getImage(mActivity, R.drawable.right67),
                getImage(mActivity, R.drawable.right68),
                getImage(mActivity, R.drawable.right69),
                getImage(mActivity, R.drawable.right70),
                getImage(mActivity, R.drawable.right71),
                getImage(mActivity, R.drawable.right72),
                getImage(mActivity, R.drawable.right73),
                getImage(mActivity, R.drawable.right74),
                getImage(mActivity, R.drawable.right75),
                getImage(mActivity, R.drawable.right76),
                getImage(mActivity, R.drawable.right77),
                getImage(mActivity, R.drawable.right78),
                getImage(mActivity, R.drawable.right79),
                getImage(mActivity, R.drawable.right80),
                getImage(mActivity, R.drawable.right81),
                getImage(mActivity, R.drawable.right82),
                getImage(mActivity, R.drawable.right83),
                getImage(mActivity, R.drawable.right84),
                getImage(mActivity, R.drawable.right85),
                getImage(mActivity, R.drawable.right86),
                getImage(mActivity, R.drawable.right87),
                getImage(mActivity, R.drawable.right88),
                getImage(mActivity, R.drawable.right89)
        );
        return mrightAnimList;
    }

    private List<Bitmap> two() {
        List<Bitmap> mrightHighAnimList = Arrays.asList(
                getImage(mActivity, R.drawable.right_high00),
                getImage(mActivity, R.drawable.right_high01),
                getImage(mActivity, R.drawable.right_high02),
                getImage(mActivity, R.drawable.right_high03),
                getImage(mActivity, R.drawable.right_high04),
                getImage(mActivity, R.drawable.right_high05),
                getImage(mActivity, R.drawable.right_high06),
                getImage(mActivity, R.drawable.right_high07),
                getImage(mActivity, R.drawable.right_high08),
                getImage(mActivity, R.drawable.right_high09),
                getImage(mActivity, R.drawable.right_high10),
                getImage(mActivity, R.drawable.right_high11),
                getImage(mActivity, R.drawable.right_high12),
                getImage(mActivity, R.drawable.right_high13),
                getImage(mActivity, R.drawable.right_high14),
                getImage(mActivity, R.drawable.right_high15),
                getImage(mActivity, R.drawable.right_high16),
                getImage(mActivity, R.drawable.right_high17),
                getImage(mActivity, R.drawable.right_high18),
                getImage(mActivity, R.drawable.right_high19),
                getImage(mActivity, R.drawable.right_high20),
                getImage(mActivity, R.drawable.right_high21),
                getImage(mActivity, R.drawable.right_high22),
                getImage(mActivity, R.drawable.right_high23),
                getImage(mActivity, R.drawable.right_high24),
                getImage(mActivity, R.drawable.right_high25),
                getImage(mActivity, R.drawable.right_high26),
                getImage(mActivity, R.drawable.right_high27),
                getImage(mActivity, R.drawable.right_high28),
                getImage(mActivity, R.drawable.right_high29),
                getImage(mActivity, R.drawable.right_high30),
                getImage(mActivity, R.drawable.right_high31),
                getImage(mActivity, R.drawable.right_high32),
                getImage(mActivity, R.drawable.right_high33),
                getImage(mActivity, R.drawable.right_high34),
                getImage(mActivity, R.drawable.right_high35),
                getImage(mActivity, R.drawable.right_high36),
                getImage(mActivity, R.drawable.right_high37),
                getImage(mActivity, R.drawable.right_high38),
                getImage(mActivity, R.drawable.right_high39),
                getImage(mActivity, R.drawable.right_high40),
                getImage(mActivity, R.drawable.right_high41),
                getImage(mActivity, R.drawable.right_high42),
                getImage(mActivity, R.drawable.right_high43),
                getImage(mActivity, R.drawable.right_high44),
                getImage(mActivity, R.drawable.right_high45),
                getImage(mActivity, R.drawable.right_high46),
                getImage(mActivity, R.drawable.right_high47),
                getImage(mActivity, R.drawable.right_high48),
                getImage(mActivity, R.drawable.right_high49),
                getImage(mActivity, R.drawable.right_high50),
                getImage(mActivity, R.drawable.right_high51),
                getImage(mActivity, R.drawable.right_high52),
                getImage(mActivity, R.drawable.right_high53),
                getImage(mActivity, R.drawable.right_high54),
                getImage(mActivity, R.drawable.right_high55),
                getImage(mActivity, R.drawable.right_high56),
                getImage(mActivity, R.drawable.right_high57),
                getImage(mActivity, R.drawable.right_high58),
                getImage(mActivity, R.drawable.right_high59),
                getImage(mActivity, R.drawable.right_high60),
                getImage(mActivity, R.drawable.right_high61),
                getImage(mActivity, R.drawable.right_high62),
                getImage(mActivity, R.drawable.right_high63),
                getImage(mActivity, R.drawable.right_high64),
                getImage(mActivity, R.drawable.right_high65),
                getImage(mActivity, R.drawable.right_high66),
                getImage(mActivity, R.drawable.right_high67),
                getImage(mActivity, R.drawable.right_high68),
                getImage(mActivity, R.drawable.right_high69),
                getImage(mActivity, R.drawable.right_high70),
                getImage(mActivity, R.drawable.right_high71),
                getImage(mActivity, R.drawable.right_high72),
                getImage(mActivity, R.drawable.right_high73),
                getImage(mActivity, R.drawable.right_high74),
                getImage(mActivity, R.drawable.right_high75),
                getImage(mActivity, R.drawable.right_high76),
                getImage(mActivity, R.drawable.right_high77),
                getImage(mActivity, R.drawable.right_high78),
                getImage(mActivity, R.drawable.right_high79),
                getImage(mActivity, R.drawable.right_high80),
                getImage(mActivity, R.drawable.right_high81),
                getImage(mActivity, R.drawable.right_high82),
                getImage(mActivity, R.drawable.right_high83),
                getImage(mActivity, R.drawable.right_high84),
                getImage(mActivity, R.drawable.right_high85),
                getImage(mActivity, R.drawable.right_high86),
                getImage(mActivity, R.drawable.right_high87),
                getImage(mActivity, R.drawable.right_high88),
                getImage(mActivity, R.drawable.right_high89)
        );
        return mrightHighAnimList;
    }

    private List<Bitmap> three() {
        List<Bitmap> mmidHighAnimList = Arrays.asList(
                getImage(mActivity, R.drawable.mid_high00),
                getImage(mActivity, R.drawable.mid_high01),
                getImage(mActivity, R.drawable.mid_high02),
                getImage(mActivity, R.drawable.mid_high03),
                getImage(mActivity, R.drawable.mid_high04),
                getImage(mActivity, R.drawable.mid_high05),
                getImage(mActivity, R.drawable.mid_high06),
                getImage(mActivity, R.drawable.mid_high07),
                getImage(mActivity, R.drawable.mid_high08),
                getImage(mActivity, R.drawable.mid_high09),
                getImage(mActivity, R.drawable.mid_high10),
                getImage(mActivity, R.drawable.mid_high11),
                getImage(mActivity, R.drawable.mid_high12),
                getImage(mActivity, R.drawable.mid_high13),
                getImage(mActivity, R.drawable.mid_high14),
                getImage(mActivity, R.drawable.mid_high15),
                getImage(mActivity, R.drawable.mid_high16),
                getImage(mActivity, R.drawable.mid_high17),
                getImage(mActivity, R.drawable.mid_high18),
                getImage(mActivity, R.drawable.mid_high19),
                getImage(mActivity, R.drawable.mid_high20),
                getImage(mActivity, R.drawable.mid_high21),
                getImage(mActivity, R.drawable.mid_high22),
                getImage(mActivity, R.drawable.mid_high23),
                getImage(mActivity, R.drawable.mid_high24),
                getImage(mActivity, R.drawable.mid_high25),
                getImage(mActivity, R.drawable.mid_high26),
                getImage(mActivity, R.drawable.mid_high27),
                getImage(mActivity, R.drawable.mid_high28),
                getImage(mActivity, R.drawable.mid_high29),
                getImage(mActivity, R.drawable.mid_high30),
                getImage(mActivity, R.drawable.mid_high31),
                getImage(mActivity, R.drawable.mid_high32),
                getImage(mActivity, R.drawable.mid_high33),
                getImage(mActivity, R.drawable.mid_high34),
                getImage(mActivity, R.drawable.mid_high35),
                getImage(mActivity, R.drawable.mid_high36),
                getImage(mActivity, R.drawable.mid_high37),
                getImage(mActivity, R.drawable.mid_high38),
                getImage(mActivity, R.drawable.mid_high39),
                getImage(mActivity, R.drawable.mid_high40),
                getImage(mActivity, R.drawable.mid_high41),
                getImage(mActivity, R.drawable.mid_high42),
                getImage(mActivity, R.drawable.mid_high43),
                getImage(mActivity, R.drawable.mid_high44),
                getImage(mActivity, R.drawable.mid_high45),
                getImage(mActivity, R.drawable.mid_high46),
                getImage(mActivity, R.drawable.mid_high47),
                getImage(mActivity, R.drawable.mid_high48),
                getImage(mActivity, R.drawable.mid_high49),
                getImage(mActivity, R.drawable.mid_high50),
                getImage(mActivity, R.drawable.mid_high51),
                getImage(mActivity, R.drawable.mid_high52),
                getImage(mActivity, R.drawable.mid_high53),
                getImage(mActivity, R.drawable.mid_high54),
                getImage(mActivity, R.drawable.mid_high55),
                getImage(mActivity, R.drawable.mid_high56),
                getImage(mActivity, R.drawable.mid_high57),
                getImage(mActivity, R.drawable.mid_high58),
                getImage(mActivity, R.drawable.mid_high59),
                getImage(mActivity, R.drawable.mid_high60),
                getImage(mActivity, R.drawable.mid_high61),
                getImage(mActivity, R.drawable.mid_high62),
                getImage(mActivity, R.drawable.mid_high63),
                getImage(mActivity, R.drawable.mid_high64),
                getImage(mActivity, R.drawable.mid_high65),
                getImage(mActivity, R.drawable.mid_high66),
                getImage(mActivity, R.drawable.mid_high67),
                getImage(mActivity, R.drawable.mid_high68),
                getImage(mActivity, R.drawable.mid_high69),
                getImage(mActivity, R.drawable.mid_high70),
                getImage(mActivity, R.drawable.mid_high71),
                getImage(mActivity, R.drawable.mid_high72),
                getImage(mActivity, R.drawable.mid_high73),
                getImage(mActivity, R.drawable.mid_high74),
                getImage(mActivity, R.drawable.mid_high75),
                getImage(mActivity, R.drawable.mid_high76),
                getImage(mActivity, R.drawable.mid_high77),
                getImage(mActivity, R.drawable.mid_high78),
                getImage(mActivity, R.drawable.mid_high79),
                getImage(mActivity, R.drawable.mid_high80),
                getImage(mActivity, R.drawable.mid_high81),
                getImage(mActivity, R.drawable.mid_high82),
                getImage(mActivity, R.drawable.mid_high83),
                getImage(mActivity, R.drawable.mid_high84),
                getImage(mActivity, R.drawable.mid_high85),
                getImage(mActivity, R.drawable.mid_high86),
                getImage(mActivity, R.drawable.mid_high87),
                getImage(mActivity, R.drawable.mid_high88),
                getImage(mActivity, R.drawable.mid_high89)
        );
        return mmidHighAnimList;
    }

    private List<Bitmap> four() {
        List<Bitmap> mleftHighAnimList = Arrays.asList(
                getImage(mActivity, R.drawable.left_high00),
                getImage(mActivity, R.drawable.left_high01),
                getImage(mActivity, R.drawable.left_high02),
                getImage(mActivity, R.drawable.left_high03),
                getImage(mActivity, R.drawable.left_high04),
                getImage(mActivity, R.drawable.left_high05),
                getImage(mActivity, R.drawable.left_high06),
                getImage(mActivity, R.drawable.left_high07),
                getImage(mActivity, R.drawable.left_high08),
                getImage(mActivity, R.drawable.left_high09),
                getImage(mActivity, R.drawable.left_high10),
                getImage(mActivity, R.drawable.left_high11),
                getImage(mActivity, R.drawable.left_high12),
                getImage(mActivity, R.drawable.left_high13),
                getImage(mActivity, R.drawable.left_high14),
                getImage(mActivity, R.drawable.left_high15),
                getImage(mActivity, R.drawable.left_high16),
                getImage(mActivity, R.drawable.left_high17),
                getImage(mActivity, R.drawable.left_high18),
                getImage(mActivity, R.drawable.left_high19),
                getImage(mActivity, R.drawable.left_high20),
                getImage(mActivity, R.drawable.left_high21),
                getImage(mActivity, R.drawable.left_high22),
                getImage(mActivity, R.drawable.left_high23),
                getImage(mActivity, R.drawable.left_high24),
                getImage(mActivity, R.drawable.left_high25),
                getImage(mActivity, R.drawable.left_high26),
                getImage(mActivity, R.drawable.left_high27),
                getImage(mActivity, R.drawable.left_high28),
                getImage(mActivity, R.drawable.left_high29),
                getImage(mActivity, R.drawable.left_high30),
                getImage(mActivity, R.drawable.left_high31),
                getImage(mActivity, R.drawable.left_high32),
                getImage(mActivity, R.drawable.left_high33),
                getImage(mActivity, R.drawable.left_high34),
                getImage(mActivity, R.drawable.left_high35),
                getImage(mActivity, R.drawable.left_high36),
                getImage(mActivity, R.drawable.left_high37),
                getImage(mActivity, R.drawable.left_high38),
                getImage(mActivity, R.drawable.left_high39),
                getImage(mActivity, R.drawable.left_high40),
                getImage(mActivity, R.drawable.left_high41),
                getImage(mActivity, R.drawable.left_high42),
                getImage(mActivity, R.drawable.left_high43),
                getImage(mActivity, R.drawable.left_high44),
                getImage(mActivity, R.drawable.left_high45),
                getImage(mActivity, R.drawable.left_high46),
                getImage(mActivity, R.drawable.left_high47),
                getImage(mActivity, R.drawable.left_high48),
                getImage(mActivity, R.drawable.left_high49),
                getImage(mActivity, R.drawable.left_high50),
                getImage(mActivity, R.drawable.left_high51),
                getImage(mActivity, R.drawable.left_high52),
                getImage(mActivity, R.drawable.left_high53),
                getImage(mActivity, R.drawable.left_high54),
                getImage(mActivity, R.drawable.left_high55),
                getImage(mActivity, R.drawable.left_high56),
                getImage(mActivity, R.drawable.left_high57),
                getImage(mActivity, R.drawable.left_high58),
                getImage(mActivity, R.drawable.left_high59),
                getImage(mActivity, R.drawable.left_high60),
                getImage(mActivity, R.drawable.left_high61),
                getImage(mActivity, R.drawable.left_high62),
                getImage(mActivity, R.drawable.left_high63),
                getImage(mActivity, R.drawable.left_high64),
                getImage(mActivity, R.drawable.left_high65),
                getImage(mActivity, R.drawable.left_high66),
                getImage(mActivity, R.drawable.left_high67),
                getImage(mActivity, R.drawable.left_high68),
                getImage(mActivity, R.drawable.left_high69),
                getImage(mActivity, R.drawable.left_high70),
                getImage(mActivity, R.drawable.left_high71),
                getImage(mActivity, R.drawable.left_high72),
                getImage(mActivity, R.drawable.left_high73),
                getImage(mActivity, R.drawable.left_high74),
                getImage(mActivity, R.drawable.left_high75),
                getImage(mActivity, R.drawable.left_high76),
                getImage(mActivity, R.drawable.left_high77),
                getImage(mActivity, R.drawable.left_high78),
                getImage(mActivity, R.drawable.left_high79),
                getImage(mActivity, R.drawable.left_high80),
                getImage(mActivity, R.drawable.left_high81),
                getImage(mActivity, R.drawable.left_high82),
                getImage(mActivity, R.drawable.left_high83),
                getImage(mActivity, R.drawable.left_high84),
                getImage(mActivity, R.drawable.left_high85),
                getImage(mActivity, R.drawable.left_high86),
                getImage(mActivity, R.drawable.left_high87),
                getImage(mActivity, R.drawable.left_high88),
                getImage(mActivity, R.drawable.left_high89)
        );
        return mleftHighAnimList;
    }

    private List<Bitmap> five() {
        List<Bitmap> mleftAnimList = Arrays.asList(
                getImage(mActivity, R.drawable.left00),
                getImage(mActivity, R.drawable.left01),
                getImage(mActivity, R.drawable.left02),
                getImage(mActivity, R.drawable.left03),
                getImage(mActivity, R.drawable.left04),
                getImage(mActivity, R.drawable.left05),
                getImage(mActivity, R.drawable.left06),
                getImage(mActivity, R.drawable.left07),
                getImage(mActivity, R.drawable.left08),
                getImage(mActivity, R.drawable.left09),
                getImage(mActivity, R.drawable.left10),
                getImage(mActivity, R.drawable.left11),
                getImage(mActivity, R.drawable.left12),
                getImage(mActivity, R.drawable.left13),
                getImage(mActivity, R.drawable.left14),
                getImage(mActivity, R.drawable.left15),
                getImage(mActivity, R.drawable.left16),
                getImage(mActivity, R.drawable.left17),
                getImage(mActivity, R.drawable.left18),
                getImage(mActivity, R.drawable.left19),
                getImage(mActivity, R.drawable.left20),
                getImage(mActivity, R.drawable.left21),
                getImage(mActivity, R.drawable.left22),
                getImage(mActivity, R.drawable.left23),
                getImage(mActivity, R.drawable.left24),
                getImage(mActivity, R.drawable.left25),
                getImage(mActivity, R.drawable.left26),
                getImage(mActivity, R.drawable.left27),
                getImage(mActivity, R.drawable.left28),
                getImage(mActivity, R.drawable.left29),
                getImage(mActivity, R.drawable.left30),
                getImage(mActivity, R.drawable.left31),
                getImage(mActivity, R.drawable.left32),
                getImage(mActivity, R.drawable.left33),
                getImage(mActivity, R.drawable.left34),
                getImage(mActivity, R.drawable.left35),
                getImage(mActivity, R.drawable.left36),
                getImage(mActivity, R.drawable.left37),
                getImage(mActivity, R.drawable.left38),
                getImage(mActivity, R.drawable.left39),
                getImage(mActivity, R.drawable.left40),
                getImage(mActivity, R.drawable.left41),
                getImage(mActivity, R.drawable.left42),
                getImage(mActivity, R.drawable.left43),
                getImage(mActivity, R.drawable.left44),
                getImage(mActivity, R.drawable.left45),
                getImage(mActivity, R.drawable.left46),
                getImage(mActivity, R.drawable.left47),
                getImage(mActivity, R.drawable.left48),
                getImage(mActivity, R.drawable.left49),
                getImage(mActivity, R.drawable.left50),
                getImage(mActivity, R.drawable.left51),
                getImage(mActivity, R.drawable.left52),
                getImage(mActivity, R.drawable.left53),
                getImage(mActivity, R.drawable.left54),
                getImage(mActivity, R.drawable.left55),
                getImage(mActivity, R.drawable.left56),
                getImage(mActivity, R.drawable.left57),
                getImage(mActivity, R.drawable.left58),
                getImage(mActivity, R.drawable.left59),
                getImage(mActivity, R.drawable.left60),
                getImage(mActivity, R.drawable.left61),
                getImage(mActivity, R.drawable.left62),
                getImage(mActivity, R.drawable.left63),
                getImage(mActivity, R.drawable.left64),
                getImage(mActivity, R.drawable.left65),
                getImage(mActivity, R.drawable.left66),
                getImage(mActivity, R.drawable.left67),
                getImage(mActivity, R.drawable.left68),
                getImage(mActivity, R.drawable.left69),
                getImage(mActivity, R.drawable.left70),
                getImage(mActivity, R.drawable.left71),
                getImage(mActivity, R.drawable.left72),
                getImage(mActivity, R.drawable.left73),
                getImage(mActivity, R.drawable.left74),
                getImage(mActivity, R.drawable.left75),
                getImage(mActivity, R.drawable.left76),
                getImage(mActivity, R.drawable.left77),
                getImage(mActivity, R.drawable.left78),
                getImage(mActivity, R.drawable.left79),
                getImage(mActivity, R.drawable.left80),
                getImage(mActivity, R.drawable.left81),
                getImage(mActivity, R.drawable.left82),
                getImage(mActivity, R.drawable.left83),
                getImage(mActivity, R.drawable.left84),
                getImage(mActivity, R.drawable.left85),
                getImage(mActivity, R.drawable.left86),
                getImage(mActivity, R.drawable.left87),
                getImage(mActivity, R.drawable.left88),
                getImage(mActivity, R.drawable.left89)
        );
        return mleftAnimList;
    }

    public static final Bitmap getImage(Context context, int imageId) {
        return ((BitmapDrawable) AAOP_HSkin.getInstance().getResourceManager().getDrawable(imageId)).getBitmap();

    }

}
