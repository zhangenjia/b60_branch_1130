package com.adayo.app.crossinf.ui.fragment;

import static android.car.VehiclePropertyIds.AFTER_FILTERING_PITCH_ANGLE;
import static android.car.VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_AROUND_PITCH_ANGLE;
import static android.car.VehiclePropertyIds.MCU_IVI_WATER_DETEC_STATUS;
import static android.car.VehiclePropertyIds.MCU_IVI_WATER_OVER_HEIGHT_WARNING;
import static android.car.VehiclePropertyIds.MCU_IVI_WATER_OVER_SLOPE_WARNING;
import static android.car.VehiclePropertyIds.MCU_IVI_WATER_OVER_SPEED_WARNING;
import static android.car.VehiclePropertyIds.MCU_IVI_WDDC_CURRENT_WADE_DEEPTH;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.car.VehiclePropertyIds;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.crossinf.MainActivity;
import com.adayo.app.crossinf.R;
import com.adayo.app.crossinf.model.VehicleDataInFo;
import com.adayo.app.crossinf.presenter.CarSettingManager;
import com.adayo.app.crossinf.util.LogUtil;

import static com.adayo.app.crossinf.util.Constant.Versiondate;

/**
 * A simple {@link Fragment} subclass.
 */

public class WadingFragment extends BaseFragment {

    private static final String TAG = Versiondate + WadingFragment.class.getSimpleName();
    private ImageView img_warning;
    private TextView text_waring;
    private ImageView img_water;
    private float mBaseLineHeight = 0;
    private ImageView imgCar;
    private TextView tv_max;
    private ImageView imgRedCar;
    private ImageView imgLine;

    private int newWadeDeep;
    private ImageView imgGrayCar;

    private int lastPitchAngle = 0;//上一次俯仰角
    private RelativeLayout mRlCar;
    private RelativeLayout rl_bg1;
    private ValueAnimator mValueAnimator;
    private float mCurrentPitchAngle = 0f;

    private int newDepthWarning;
    private int lastDepthWarning = -1;
    private int newOverSlope;
    private int lastSlope = -1;
    private int newOverSpeed;
    private int lastSpeed = -1;
    private int newWadeSystemStatus;
    private int lastWadeSystemStatus = -1;


    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //todo 更新数据
            if (!isInflateView) {
                return;
            }
            VehicleDataInFo mVehicleDataInFo = (VehicleDataInFo) msg.obj;
            Bundle bundle = mVehicleDataInFo.getBundle();
            int properId = mVehicleDataInFo.getId();
            switch (properId) {
                case MCU_IVI_WDDC_CURRENT_WADE_DEEPTH://当前水深值
                    newWadeDeep = bundle.getInt("IntValue");
                    Log.d(TAG, "newWadeDeep =  " + newWadeDeep);
                    updateImgWater();
                    break;
                case VehiclePropertyIds.MCU_IVI_WDDC_MAX_WADE_DEEPTH://最大水深值
                    break;
                case MCU_IVI_WATER_DETEC_STATUS://系统状态
                    int mWadeSystemStatus = bundle.getInt("IntValue");
                    Log.d(TAG, "MCU_IVI_WATER_DETEC_STATUS: " + mWadeSystemStatus);
                    newWadeSystemStatus = mWadeSystemStatus;
                    updateTextWarning();
                    updateImgWarning();
                    updateImgCar();
                    updateMaxLineAndText();
                    updateImgWater();
                    updateGroundImg();
                    break;
                case IVI_MCU_MEDIA_NAVI_AROUND_PITCH_ANGLE://俯仰角及有效值信息
//                    int mPitchAngleValue = bundle.getInt("IntValue");
//                    updatePitchAngleValue(mPitchAngleValue);
                    break;
                case MCU_IVI_WATER_OVER_SLOPE_WARNING:
                    newOverSlope = bundle.getInt("IntValue");
                    Log.d(TAG, "newOverSlope" + newOverSlope);
                    if (newOverSlope != lastSlope) {
                        updateTextWarning();
                        updateImgWarning();
                        updateGroundImg();
                        lastSlope = newOverSlope;
                    }
                    break;
                case MCU_IVI_WATER_OVER_SPEED_WARNING:
                    newOverSpeed = bundle.getInt("IntValue");
                    if (newOverSpeed != lastSpeed) {
                        updateTextWarning();
                        updateImgWarning();
                        updateGroundImg();
                        lastSpeed = newOverSpeed;
                    }
                    break;
                case MCU_IVI_WATER_OVER_HEIGHT_WARNING:
                    newDepthWarning = bundle.getInt("IntValue");
                    Log.d(TAG, " waterOver warning = " + newDepthWarning);
                    if (newDepthWarning != lastDepthWarning) {
                        updateTextWarning();
                        updateImgWarning();
                        updateImgCar();
                        updateGroundImg();
                        lastDepthWarning = newDepthWarning;
                    }
                    break;
                case VehiclePropertyIds.AFTER_FILTERING_LATERAL_PITCH_ANGLE:
                    break;
                case AFTER_FILTERING_PITCH_ANGLE:
                    int mPitchAngleValue = bundle.getInt("IntValue");
                    updatePitchAngleValue(mPitchAngleValue);
                    break;
                default:
                    Log.d(TAG, "Invalid parameter!");
                    break;
            }
        }
    };
    private int lastWadeSystemImg = -1;
    private int lastWadeDeep = -1;
    private OnFragmentResumeListener listener;
    private boolean isInflateView;
    private ImageView iv_bg1;
    private ImageView iv_bg2;
    private int newPitchAngle;


    public WadingFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        Log.d(TAG, "onAttach: ");
        activity.setHandler(handler);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(" onCreate WadingFragment ====== start");
        View view = inflater.inflate(R.layout.fragment_wading_detection, container, false);
        img_warning = (ImageView) view.findViewById(R.id.iv_triangle_warning);
        text_waring = (TextView) view.findViewById(R.id.tv_waringtext);
        img_water = (ImageView) view.findViewById(R.id.iv_water);
        imgCar = (ImageView) view.findViewById(R.id.iv_car);
        imgRedCar = (ImageView) view.findViewById(R.id.iv_car_red);
        imgGrayCar = (ImageView) view.findViewById(R.id.iv_car_gray);
        mRlCar = view.findViewById(R.id.rl_car);
        tv_max = (TextView) view.findViewById(R.id.tv_max);
        imgLine = (ImageView) view.findViewById(R.id.iv_line);
        rl_bg1 = view.findViewById(R.id.rl_bg1);
        iv_bg1 = view.findViewById(R.id.iv_bg1);
        iv_bg2 = view.findViewById(R.id.iv_bg2);
        isInflateView = true;
//      mRlCar.setPivotX(232);//todo /设置指定旋转中心点X坐标
//      mRlCar.setPivotY(182);//todo /设置指定旋转中心点y坐标
        initData();
        initVehiclePitchAngle();
        LogUtil.d(" onCreate WadingFragment ====== end");
        return view;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        lastWadeSystemStatus = -1;
        Log.d(TAG, "onConfigurationChanged: ===>");
        updateTextWarning();

        if (tv_max != null) {
            tv_max.setText(getResources().getString(R.string.max));
        }
    }


    /**
     * 更新水位切图
     * 变化量
     * 1 系统状态
     * 2 水深
     */
    public void updateImgWater() {
        if (img_water == null) {
            return;
        }
        Log.d(TAG, " updateImgWater = " + newWadeSystemStatus);
        if (newWadeSystemStatus != 0) {//非notActive 都要显示水面
            img_water.setVisibility(View.VISIBLE);
        } else {
            img_water.setVisibility(View.INVISIBLE);
        }
        //高度
        if (newWadeDeep > 75) {
            newWadeDeep = 75;
        }

        if (newWadeDeep != lastWadeDeep) {
            playDeepChangeAnim(newWadeDeep);
        }

        lastWadeDeep = newWadeDeep;
    }

    /**
     * 更新警告文字
     */
    public void updateTextWarning() {
        if (text_waring == null) {
            return;
        }
        if (newWadeSystemStatus != 0) {  //判断优先级
            switch (newWadeSystemStatus) {
                case 1:
                    if (newDepthWarning == 3) {
                        if (text_waring != null) {
                            text_waring.setVisibility(View.VISIBLE);
                            text_waring.setText(getContext().getResources().getString(R.string.tipsText7));
                            playWarningTextAnim();
                        }
                    } else {
                        if (newOverSpeed == 1) {
                            if (text_waring != null) {
                                text_waring.setVisibility(View.VISIBLE);
                                text_waring.setText(getContext().getResources().getString(R.string.tipsText6));
                                playWarningTextAnim();
                            }
                        } else {
                            if (newOverSlope == 1) {
                                if (text_waring != null) {
                                    text_waring.setVisibility(View.VISIBLE);
                                    text_waring.setText(getContext().getResources().getString(R.string.tipsText5));
                                    playWarningTextAnim();
                                }

                            } else {
                                if (text_waring != null) {
                                    text_waring.setVisibility(View.INVISIBLE);
                                }
                                text_waring.setText("");
                            }
                        }
                    }


                    break;
                case 2:
                    if (lastWadeSystemStatus != newWadeSystemStatus) {
                        text_waring.setVisibility(View.VISIBLE);
                        text_waring.setText(getContext().getResources().getString(R.string.tipsText1));
                        playWarningTextAnim();
                    }
                    break;
                case 3:
                    if (lastWadeSystemStatus != newWadeSystemStatus) {
                        text_waring.setVisibility(View.VISIBLE);
                        text_waring.setText(getContext().getResources().getString(R.string.tipsText2));
                        playWarningTextAnim();
                    }
                    break;
                case 4:
                    if (lastWadeSystemStatus != newWadeSystemStatus) {
                        text_waring.setVisibility(View.VISIBLE);
                        text_waring.setText(getContext().getResources().getString(R.string.tipsText3));
                        playWarningTextAnim();
                    }
                    break;
                case 5:
                    if (lastWadeSystemStatus != newWadeSystemStatus) {
                        text_waring.setVisibility(View.VISIBLE);
                        text_waring.setText(getContext().getResources().getString(R.string.tipsText4));
                        playWarningTextAnim();
                    }
                    break;
            }

        } else {
            text_waring.setVisibility(View.INVISIBLE);
            text_waring.setText("");
        }
        lastWadeSystemStatus = newWadeSystemStatus;
    }

    /**
     * 更新警告图标
     */
    public void updateImgWarning() {
        if (img_warning == null) {
            return;
        }
        Log.d(TAG, "updateWarnIcon: mLastWadeSystem " + newWadeSystemStatus
                + " mLastWadeMode " + " currentWadeDeep " + newWadeDeep);
        if (newWadeSystemStatus != 0) {
            switch (newWadeSystemStatus) {
                case 1:
                    if (newDepthWarning == 2 || newDepthWarning == 3) {
                        if (img_warning != null) {
                            img_warning.setVisibility(View.VISIBLE);
                            playWarningImgAnim();
                        }
                    } else {
                        if (newOverSpeed == 1) {
                            if (img_warning != null) {
                                img_warning.setVisibility(View.VISIBLE);
                                playWarningImgAnim();
                            }
                        } else {
                            if (newOverSlope == 1) {
                                if (img_warning != null) {
                                    img_warning.setVisibility(View.VISIBLE);
                                    playWarningImgAnim();
                                }
                            } else {
                                img_warning.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                    if (lastWadeSystemImg != newWadeSystemStatus) {
                        img_warning.setVisibility(View.VISIBLE);
                        playWarningImgAnim();
                    }
                    break;
            }
        } else {
            img_warning.setVisibility(View.INVISIBLE);
        }

        lastWadeSystemImg = newWadeSystemStatus;
    }

    /**
     * 更新车模颜色
     * 变化条件 涉水系统状态 预警状态
     */
    private void updateImgCar() {
        if (imgCar == null || imgRedCar == null || imgGrayCar == null) {
            return;
        }
        Log.d(TAG, "updateImgCar: " + newWadeDeep + " currentWadeSystem = " + newWadeSystemStatus + " newDepthWarning " + newDepthWarning);
        switch (newWadeSystemStatus) {
            case 1:
                if (newDepthWarning == 3) {
                    imgGrayCar.setVisibility(View.INVISIBLE);
                    imgRedCar.setVisibility(View.VISIBLE);
                    imgCar.setVisibility(View.VISIBLE);
                } else {
                    imgGrayCar.setVisibility(View.INVISIBLE);
                    imgRedCar.setVisibility(View.INVISIBLE);
                    imgCar.setVisibility(View.VISIBLE);
                }
                break;
            case 0://涉水监测不可用
            case 2:
            case 3:
            case 4:
                imgGrayCar.setVisibility(View.VISIBLE);
                imgRedCar.setVisibility(View.INVISIBLE);
                imgCar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void updateMaxLineAndText() {
        if (imgLine == null || tv_max == null) {
            return;
        }

        switch (newWadeSystemStatus) {
            case 1:
                imgLine.setBackgroundResource(R.drawable.line_max);
                tv_max.setAlpha(1f);
                break;
            case 0://涉水监测不可用
            case 2:
            case 3:
            case 4:
                imgLine.setBackgroundResource(R.drawable.line_max_dis);
                tv_max.setAlpha(0.38f);
                break;
        }
    }


    public void playWarningTextAnim() {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(true);
        alphaAnimation.setStartOffset(300);

        TranslateAnimation translateanimation = new TranslateAnimation(0, 0, 50, 0);
        translateanimation.setInterpolator(new OvershootInterpolator());
        translateanimation.setDuration(400);
        translateanimation.setStartOffset(300);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateanimation);
        text_waring.startAnimation(animationSet);
    }


    public void playWarningImgAnim() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(img_warning, "translationX", 0, 30, 0, 20, 30);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(400);
        animator.start();

    }

    /**
     * 水深动画
     */
    private void playDeepChangeAnim(int value) {

        if (value > 75) {
            value = 75;
        }
        float finalValue = ((float) value) * (163f / 75f);
        Log.d(TAG, "playAnimation3: mBaseLineHeight " + mBaseLineHeight + " finalValue " + finalValue);
        ObjectAnimator animator_iv_water_surface_down = ObjectAnimator.ofFloat(img_water, "translationY", -mBaseLineHeight, -finalValue);//动画值取反
        animator_iv_water_surface_down.setInterpolator(new LinearInterpolator());
        animator_iv_water_surface_down.setDuration((long) (Math.abs((finalValue - mBaseLineHeight) * 3f)));
        animator_iv_water_surface_down.start();
        mBaseLineHeight = finalValue;

        Log.d(TAG, "updateCurrentWadeDeepth: " + value);
    }

    /**
     * 初始化俯仰角
     */
    private void initVehiclePitchAngle() {

        int naviPitchAngle = CarSettingManager.getInstance().getNaviPitchAngle();//获得俯仰角
        naviPitchAngle = naviPitchAngle + 80;
        Log.d(TAG, "initVehiclePitchAngle: " + naviPitchAngle);
        if (naviPitchAngle == -999 || naviPitchAngle < -40 || naviPitchAngle > 40) {//无效值
            mRlCar.setRotation(0);
            rl_bg1.setRotation(0);
        } else {


            if (naviPitchAngle > 0) {
                if (naviPitchAngle < 6) {
                    naviPitchAngle = 0;
                } else if (naviPitchAngle > 10) {
                    naviPitchAngle = 10;
                }


            }

            if (naviPitchAngle < 0) {
                if (naviPitchAngle > -6) {
                    naviPitchAngle = 0;
                } else if (naviPitchAngle < -10) {
                    naviPitchAngle = -10;
                }
            }

        }
    }

    /**
     * 更新俯仰角数值
     */
    private void updatePitchAngleValue(int value) {
        Log.d(TAG, "updatePitchAngleValue111 =: " + value);
        value = value-40;
        if (value >= -40 && value <= 40) {
            if (lastPitchAngle == value) {
                return;
            }
            if (value > 0) {
                if (value < 6) {
                    value = 0;
                } else if (value > 10) {
                    value = 10;
                }


            }

            if (value < 0) {
                if (value > -6) {
                    value = 0;
                } else if (value < -10) {
                    value = -10;
                }
            }

            if (mValueAnimator != null) {
                mValueAnimator.cancel();
            }
            mValueAnimator = ValueAnimator.ofFloat(mCurrentPitchAngle, value);
            Log.d(TAG, "updatePitchAngleValue_: mCurrentPitchAngle = " + mCurrentPitchAngle + " value = " + value);
            mValueAnimator.setDuration(Math.abs(Math.round(value - mCurrentPitchAngle)) * 30);
            lastPitchAngle = value;
            mCurrentPitchAngle = value;
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue_pitchangle = (float) animation.getAnimatedValue();
//                    Log.d(TAG, "updatePitchAngleValue_: animatedValue_pitchangle = " + animatedValue_pitchangle);
                    mRlCar.setRotation(animatedValue_pitchangle);
                    rl_bg1.setRotation(animatedValue_pitchangle);
                    mCurrentPitchAngle = animatedValue_pitchangle;
                }
            });
            mValueAnimator.start();
        } else {
            if (mValueAnimator != null) {
                mValueAnimator.cancel();
            }
            mRlCar.setRotation(0);
            rl_bg1.setRotation(0);
            lastPitchAngle = 0;
        }
    }

    private void updateGroundImg() {

        if (newWadeSystemStatus == 1) {
            iv_bg1.setVisibility(View.VISIBLE);
            iv_bg2.setVisibility(View.INVISIBLE);
        } else {
            iv_bg1.setVisibility(View.INVISIBLE);
            iv_bg2.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "updateGroundImg: " + newWadeSystemStatus);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            listener.onFragmentResume();
        }
    }

    @Override
    public void registerOnResumeListener(OnFragmentResumeListener listener) {
        this.listener = listener;
    }

    public void initData() {
        newWadeDeep = CarSettingManager.getInstance().getIntTransDataMessage(MCU_IVI_WDDC_CURRENT_WADE_DEEPTH,0);//当前水深
        newWadeSystemStatus = CarSettingManager.getInstance().getIntTransDataMessage(MCU_IVI_WATER_DETEC_STATUS,0);//系统状态
        newOverSlope = CarSettingManager.getInstance().getIntTransDataMessage(MCU_IVI_WATER_OVER_SLOPE_WARNING,0);//坡度警告
        newOverSpeed = CarSettingManager.getInstance().getIntTransDataMessage(MCU_IVI_WATER_OVER_SPEED_WARNING,0);//超速警告
        newDepthWarning = CarSettingManager.getInstance().getIntTransDataMessage(MCU_IVI_WATER_OVER_HEIGHT_WARNING,0);//水深警告
        //滤波过后俯仰角
        newPitchAngle = CarSettingManager.getInstance().getIntTransDataMessage(AFTER_FILTERING_PITCH_ANGLE, 0);
        Log.d(TAG, "initWadingData: newWadeDeep " + newWadeDeep + " newWadeSystemStatus " + newWadeSystemStatus + " newOverSlope " + newOverSlope + " newOverSpeed " + newOverSpeed + " newDepthWarning " + newDepthWarning);
        updateTextWarning();
        updateImgWarning();
        updateImgCar();
        updateMaxLineAndText();
        updateImgWater();
        updateGroundImg();
        updatePitchAngleValue(newPitchAngle);
    }

}

