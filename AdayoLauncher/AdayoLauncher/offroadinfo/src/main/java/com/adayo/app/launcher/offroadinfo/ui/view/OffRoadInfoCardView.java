package com.adayo.app.launcher.offroadinfo.ui.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.car.VehiclePropertyIds;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.adayo.app.launcher.communicationbase.WrapperUtil;
import com.adayo.app.launcher.communicationbase.BlurTransitionView;
import com.adayo.app.launcher.communicationbase.ConstantUtil;
import com.adayo.app.launcher.communicationbase.IViewBase;
import com.adayo.app.launcher.offroadinfo.R;
import com.adayo.app.launcher.offroadinfo.model.bean.VehicleDataInFo;
import com.adayo.app.launcher.offroadinfo.presenter.CanDataCallbcak;
import com.adayo.app.launcher.offroadinfo.presenter.ISkinChangeListener;
import com.adayo.app.launcher.offroadinfo.presenter.OffroadAttrHandler;
import com.adayo.app.launcher.offroadinfo.presenter.SkinChangeImpl;
import com.adayo.app.launcher.offroadinfo.presenter.CrossDataManager;
import com.adayo.app.launcher.offroadinfo.presenter.TireColorStrategyManager;
import com.adayo.app.launcher.offroadinfo.util.ConfigFunction;
import com.adayo.app.launcher.offroadinfo.util.Constant;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;
import com.airbnb.lottie.LottieAnimationView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static android.car.VehicleAreaWheel.WHEEL_LEFT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_LEFT_REAR;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_REAR;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DEFAULT_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.DRAG_TO_INITCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_BIGCARD;
import static com.adayo.app.launcher.communicationbase.ConstantUtil.TYPE_SMALLCARD;
import static com.adayo.app.launcher.offroadinfo.presenter.CrossDataManager.CAR_SETTING_DATA;
import static com.adayo.app.launcher.offroadinfo.presenter.TireColorStrategyManager.LEVEL_HIGH;
import static com.adayo.app.launcher.offroadinfo.presenter.TireColorStrategyManager.LEVEL_LOW;
import static com.adayo.app.launcher.offroadinfo.presenter.TireColorStrategyManager.LEVEL_MIDDLE;
import static com.adayo.app.launcher.offroadinfo.presenter.TireColorStrategyManager.TYPE_TIRE_PRESSURE;
import static com.adayo.app.launcher.offroadinfo.presenter.TireColorStrategyManager.TYPE_TIRE_TEMP;
import static com.adayo.app.launcher.offroadinfo.util.Constant.CURRENTTHEME;
import static com.adayo.app.launcher.offroadinfo.util.Constant.CURRENT_TAB;
import static com.adayo.app.launcher.offroadinfo.util.Constant.DRIVINGMODESELECTERROR;
import static com.adayo.app.launcher.offroadinfo.util.Constant.ENVIRONMENTALINFORMATION;
import static com.adayo.app.launcher.offroadinfo.util.Constant.FAULT;
import static com.adayo.app.launcher.offroadinfo.util.Constant.INVALID;
import static com.adayo.app.launcher.offroadinfo.util.Constant.IS_CONFIG;
import static com.adayo.app.launcher.offroadinfo.util.Constant.LOCKED;
import static com.adayo.app.launcher.offroadinfo.util.Constant.LOCKREQUESTDENY;
import static com.adayo.app.launcher.offroadinfo.util.Constant.OVERSPEEDWARNING;
import static com.adayo.app.launcher.offroadinfo.util.Constant.UNLOCKED;
import static com.adayo.app.launcher.offroadinfo.util.Constant.VEHICLEINFORMATION;
import static com.adayo.proxy.media.mediascanner.utils.ContextUtil.getAppContext;

public class OffRoadInfoCardView extends RelativeLayout implements IViewBase, View.OnClickListener, ISkinChangeListener.CallBack {

    private static final String TAG = OffRoadInfoCardView.class.getSimpleName();

    private int lastmLfWheelSlipStatusValue = -1;
    private int lastmRfWheelSlipStatusValue = -1;
    private int lastmLrWheelSlipStatusValue = -1;
    private int lastmRrWheelSlipStatusValue = -1;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == PLAY_ANIMATION) {
                if (CURRENT_THEME == 2) {
                    Animator card_name_animator = AnimatorInflater.loadAnimator(mContext, R.animator.card_name_animator);
                    card_name_animator.setTarget(tv_orimSmall);
                    card_name_animator.start();
                }
                lottie_view.setAnimation("offroadinfo" + ConstantUtil.CURRENT_THEME + ".json");
                lottie_view.playAnimation();
                lottie_view.addAnimatorListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        AAOP_HSkin.with(lottie_view)
                                .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.offroadinfo45_lottie)
                                .applySkin(false);
                    }
                });
                return;
            }

            if (msg.what == 8300) {//  -40 到 +40 算上 0度 一共81度 ，81张图。
                vehicleWheelAngle = (int) msg.obj;
                if (tv_wheelAngle == null) {//控件没初始化完直接return
                    return;
                }
//                Log.d(TAG, "handleMessage:wheelAngle " + vehicleWheelAngle);
                if (lastAngle != vehicleWheelAngle) {
                    lastAngle = vehicleWheelAngle;

                    Log.d(TAG, "handleMessage: resIndex " + resIndex);
                    if (vehicleWheelAngle < -40 || vehicleWheelAngle > 40) {//异常值
                        tv_wheelAngle.setText("--");//
                        resIndex = 40;
                        startWheelAngleAnim(resIndex, false);
                        return;
                    }

                    // 正常更新
                    tv_wheelAngle.setText(String.valueOf(Math.abs(vehicleWheelAngle)));
                    //一共 81张图 正度数往左转
                    resIndex = 40 - vehicleWheelAngle;
                    startWheelAngleAnim(resIndex, true);
                }

            } else if (msg.what == CAR_SETTING_DATA) {
                //todo 更新数据
                VehicleDataInFo mVehicleDataInFo = (VehicleDataInFo) msg.obj;
                if (mVehicleDataInFo == null) {
                    Log.d(TAG, "mVehicleDataInFo==null = ");
                    return;
                }
                Log.d(TAG, "mVehicleDataInFo==null = " + mVehicleDataInFo);
                Bundle bundle = mVehicleDataInFo.getBundle();
                int properId = mVehicleDataInFo.getId();
                switch (properId) {
                    case VehiclePropertyIds.TIRE_PRESSURS_VALUE://胎压
                        int mPressurevalue = bundle.getInt("IntValue");
                        int mPressureAreaID = bundle.getInt("AreaID");
                        Log.d(TAG, "test===> 胎压 "+" mPressureAreaID "+mPressureAreaID+ " mPressurevalue "+mPressurevalue);
                        if (mPressureAreaID == 1) {

                            if (lastPressure1 != mPressurevalue) {
                                lastPressure1 = mPressurevalue;
                            } else {
                                return;
                            }
                        } else if (mPressureAreaID == 2) {
                            if (lastPressure2 != mPressurevalue) {
                                lastPressure2 = mPressurevalue;
                            } else {
                                return;
                            }
                        } else if (mPressureAreaID == 4) {
                            if (lastPressure4 != mPressurevalue) {
                                lastPressure4 = mPressurevalue;
                            } else {
                                return;
                            }
                        } else if (mPressureAreaID == 8) {
                            if (lastPressure8 != mPressurevalue) {
                                lastPressure8 = mPressurevalue;
                            } else {
                                return;
                            }
                        }
                        updateTirePressureValue(mPressureAreaID, mPressurevalue);
                        break;
                    case VehiclePropertyIds.TIRE_TEMPERATURE_VALUE://胎温
                        int mTemperatureValue = bundle.getInt("IntValue");
                        int mTemperatureAreaID = bundle.getInt("AreaID");
                        Log.d(TAG, "test===> 胎温"+" mTemperatureAreaID "+mTemperatureValue);
                        if (mTemperatureAreaID == 1) {

                            if (lastTemp1 != mTemperatureValue) {
                                lastTemp1 = mTemperatureValue;
                            } else {
                                return;
                            }
                        } else if (mTemperatureAreaID == 2) {
                            if (lastTemp2 != mTemperatureValue) {
                                lastTemp2 = mTemperatureValue;
                            } else {
                                return;
                            }
                        } else if (mTemperatureAreaID == 4) {
                            if (lastTemp4 != mTemperatureValue) {
                                lastTemp4 = mTemperatureValue;
                            } else {
                                return;
                            }
                        } else if (mTemperatureAreaID == 8) {
                            if (lastTemp8 != mTemperatureValue) {
                                lastTemp8 = mTemperatureValue;
                            } else {
                                return;
                            }
                        }
                        updateTire_Temperature_Value(mTemperatureAreaID, mTemperatureValue);
                        break;
                    case VehiclePropertyIds.TIRE_SYSTEM_STATE://胎压系统状态
                        Log.d(TAG, "handleMessage:aa TIRE_SYSTEM_STATE  ");
                        int systemWarning = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 胎压系统状态 "+systemWarning);
                        if (lastSystemWarning != systemWarning) {
                            lastSystemWarning = systemWarning;
                        } else {
                            return;
                        }
                        getTirePress();
                        getTireTemp();
                        break;
                    case VehiclePropertyIds.TIRE_PRESSURES_STATUS://胎压报警
                        Log.d(TAG, "test===> 胎压报警");

                        getTirePress();
                        getTireTemp();
                        break;
                    case VehiclePropertyIds.TIRE_TEMPERATURS_STATUS://胎温报警
                        Log.d(TAG, "test===> 胎温报警");
                        getTirePress();
                        getTireTemp();
                        break;
                    case VehiclePropertyIds.TIRE_SYSTEM_ALARM_PROMPT://系统报警
                        Log.d(TAG, "test===> 系统报警");
                        int loss = bundle.getInt("IntValue");
                        if (lastLost != loss) {
                            lastLost = loss;
                        } else {
                            return;
                        }
                        getTirePress();
                        getTireTemp();
                        break;
                    // todo 方向盘转角
                    case VehiclePropertyIds.TCCU1_ST_4H4L://分动箱状态
                        int mTransfer_Case_State_Value = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 分动箱状态");
                        updateTransferCaseStatus(mTransfer_Case_State_Value);
                        break;
                    case VehiclePropertyIds.EMS_EDS1_ST_FRONTEDSLOCK://前差速锁状态
                        int mFontDifferentialLockValue = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 前差速锁状态");
                        updateFontDifferentialLock(mFontDifferentialLockValue);
                        break;
                    case VehiclePropertyIds.EMS_EDS1_ST_REAREDSLOCK://后差速锁状态
                        int mFearDifferentialLockValue = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 后差速锁状态");
                        updateRearDifferentialLock(mFearDifferentialLockValue);
                        break;
                    case VehiclePropertyIds.LF_WHEEL_SLIP_STATUS://左前轮打滑程度
                        int mLfWheelSlipStatusValue = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 左前轮打滑程度");
                        if (lastmLfWheelSlipStatusValue != mLfWheelSlipStatusValue) {
                            lastmLfWheelSlipStatusValue = mLfWheelSlipStatusValue;
                        } else {
                            return;
                        }
                        updateWheelSlipStatus(WHEEL_LEFT_FRONT, mLfWheelSlipStatusValue);
                        break;
                    case VehiclePropertyIds.RF_WHEEL_SLIP_STATUS://右前轮打滑程度
                        int mRfWheelSlipStatusValue = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 右前轮打滑程度");
                        if (lastmRfWheelSlipStatusValue != mRfWheelSlipStatusValue) {
                            lastmRfWheelSlipStatusValue = mRfWheelSlipStatusValue;
                        } else {
                            return;
                        }
                        updateWheelSlipStatus(WHEEL_RIGHT_FRONT, mRfWheelSlipStatusValue);
                        break;
                    case VehiclePropertyIds.LR_WHEEL_SLIP_STATUS://左后轮打滑程度
                        int mLrWheelSlipStatusValue = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 左后轮打滑程度");
                        if (lastmLrWheelSlipStatusValue != mLrWheelSlipStatusValue) {
                            lastmLrWheelSlipStatusValue = mLrWheelSlipStatusValue;
                        } else {
                            return;
                        }
                        updateWheelSlipStatus(WHEEL_LEFT_REAR, mLrWheelSlipStatusValue);
                        break;
                    case VehiclePropertyIds.RR_WHEEL_SLIP_STATUS://右后轮打滑程度
                        int mRrWheelSlipStatusValue = bundle.getInt("IntValue");
                        Log.d(TAG, "test===> 右后轮打滑程度");
                        if (lastmRrWheelSlipStatusValue != mRrWheelSlipStatusValue) {
                            lastmRrWheelSlipStatusValue = mRrWheelSlipStatusValue;
                        } else {
                            return;
                        }
                        updateWheelSlipStatus(WHEEL_RIGHT_REAR, mRrWheelSlipStatusValue);
                        break;

                    case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_AROUND_TITLING_ANGLE://水平角及有效值信息
                        int mHorizontalAngleValue = bundle.getInt("IntValue");
                        updateHorizontalAngleValue(mHorizontalAngleValue);
                        break;
                    case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_AROUND_PITCH_ANGLE://俯仰角及有效值信息
                        int mPitchAngleValue = bundle.getInt("IntValue");
                        updatePitchAngleValue(mPitchAngleValue);
                        break;
                    case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_ALTITUDEHEIGH://相对高度及有效值信息
                        float mAltitudeValue = bundle.getFloat("FloatValue");
                        updateAltitudeValue(mAltitudeValue);
                        break;
                    case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_ATMOSPHERICPRESSURE://大气压及有效值信息
                        float mAtmosphericPressureValue = bundle.getFloat("FloatValue");
                        updateAtmoPressure(mAtmosphericPressureValue);
                        break;
                    case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_COMPASS_DIRECTION://指南针方向及有效值信息
                        int mCompassDirectionValue = bundle.getInt("IntValue");
                        updateCompassDirection(mCompassDirectionValue);
                        break;
                    case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_COMPASS_SIG://指南针角度及有效值信息
                        float mCompassAngleValue = bundle.getFloat("FloatValue");
                        updateCompassAngle(mCompassAngleValue);
                        break;
                }
            }

        }
    };

    private Context mContext;
    private static OffRoadInfoCardView mOffRoadInfoCardView;
    private View mBigCardView;
    private View mSmallCardView;
    private HorizontalCustomView cv_horizontal;
    private PitchCustomView cv_pitch;
    private RelativeLayout rl_compass_pointer;
    //private TabLayout tablayout;
    private RelativeLayout rl_environment;
    private RelativeLayout rl_vehicleinfo;
    private ImageView lfwheelImg;
    private ImageView axleImg;
    private ImageView rfwheeltImg;
    private ImageView pointerImg;
    //private final int mTabLayoutLeftIndex = 0;
//  private final int mTabLayoutRightIndex = 1;
    private AnimationDrawable lfWhellAnimDraw;
    private AnimationDrawable rfWhellDraw;
    private AnimationDrawable axleAnimDraw;
    private AnimationDrawable pointerAnimDraw;
    private ValueAnimator vehicleWheelAngleAnim;
    private ImageView iv_f_differential_lock;
    private ImageView iv_r_differential_lock;
    private int iv_wheel_left_color = 0x00FFFFFF;
    private int iv_axle_color = 0x00FFFFFF;
    private int iv_wheel_right_color = 0x00FFFFFF;
    private int mWheelNomalColor = 0x00FFFFFF;
    private int mWheelSlipLevelColor_1 = 0x7A405c79;//
    private int mWheelSlipLevelColor_2 = 0x7A4d4d1e;//
    private int mWheelSlipLevelColor_3 = 0x7A582726;//
    //    private static final int WHEEL_COLOR_RED = 0x7AB23633; //fe2b14
    private static final int WHEEL_COLOR_RED = 0x7Afe2b14;//0x7Afe2b14
    private ImageView lrwheelImg;
    private ImageView rrwheelImg;
    private int lastHorizontalangle = -61;//上一次水平角
    private int lastPitchAngle = -61;//上一次俯仰角
    private float mLastCompassAngleValue = 0;
    private int currentAnimValue;
    private int lfWhellRes;
    private int rfWhellRes;
    private int axleRes;
    private int pointerRes;
    private int PLAY_ANIMATION = 1236;
    private TextView lfTirePressureText;
    private TextView rfTirepressureText;
    private TextView lrTirepressureText;
    private TextView rrTirepressureText;
    private TextView lrTiretempText;
    private TextView rfTiretempText;
    private TextView lfTiretempText;
    private TextView rrTiretempText;
    private ImageView iv_transfer_case_state;
    private TextView tv_horizontalangle;
    private ImageView iv_horizontalangle;
    private TextView tv_pitchangle;
    private ImageView iv_pitchangle;
    private TextView tv_altitude;
    private TextView tv_AtmoPressure;
    private TextView tv_compassdirection;
    private TextView tv_compassangle;
    private ImageView iv_crossin_frame;
    private LottieAnimationView lottie_view;
    private TextView tv_orimBig;
    private TextView tv_orimSmall;
    private BlurTransitionView crossinfo_blur;
    private ImageView iv_crossinfo_one;
    private LinearLayout ll_tab;
    private TextView vehicleInfo;
    private TextView environInfo;
    private TextView tv_altitude_name;
    private TextView tv_pressure_name;
    private int mapKey = 0;

    // 车轮转角初始化的值
    private int vehicleWheelAngle;
    private int lastAngle = -99;
    private int lastAnimAngle = 1;
    private int resIndex = 0;
    private boolean isFirstStartAnim = true;
    private final Map<Integer, Integer> mCompassDirectionMap = new HashMap<Integer, Integer>() {
        {
            put(7, R.string.northwest);
            put(6, R.string.west);
            put(5, R.string.southwest);
            put(4, R.string.south);
            put(3, R.string.southeast);
            put(2, R.string.east);
            put(1, R.string.northeast);
            put(0, R.string.north);
            put(255, R.string.north);
        }
    };
    private ImageView iv_crossinfo_two;
    private ImageView iv_crossinfo_frame;
    private ValueAnimator valueAnimator_horizontalangle;
    private ValueAnimator valueAnimator_pitchangle;
    private float mCurrentHorizontalangle;
    private float mCurrentPitchAngle;
    private float mCurrentCompassAngleValue;
    private ValueAnimator valueAnimator_compass_pointer;
    private TextView tv_wheelAngle;
    private int mLastFontDifferentialValue = -1;
    private int mRearDifferentialValue = -1;
    private ImageView icon_altitude;
    private ImageView icon_pressure;
    private CrossDataManager mCrossDataManager;
    private int lf_tirePressure;
    private int rf_tirePressure;
    private int lr_tirePressure;
    private int rr_tirePressure;
    private int tireTemperute;
    private int frontWheelAngle;
    private int tccuStatus;
    private int frontEdsLock;
    private int rearEdsLock;
    private int lfSlipStatus;
    private int rfSlipStatus;
    private int lrSlipStatus;
    private int rrSlipStatus;
    private TextView mTvUnit1;
    private TextView mTvUnit2;
    private TextView mTvUnit3;
    private TextView mTvUnit4;
    private TextView mTvUnit5;
    private TextView mTvUnit6;
    private TextView mTvUnit7;
    private TextView mTvUnit8;


    private int mLFTemp;
    private int mRFTemp;
    private int mLRTemp;
    private int mRRTemp;
    private int duration;
    private ImageView lfTireMarks;
    private ImageView rfTireMarks;
    private ImageView lrTireMarks;
    private ImageView rrTireMarks;
    private Map<Integer, Integer> mHighLevelTireMap = new HashMap<>();//高优先级的轮胎颜色集合 level = 0的时候
    private Map<Integer, Integer> mMiddleLevelTireMap = new HashMap<>();//中优先级的轮胎颜色集合 level = 1的时候
    private Map<Integer, Integer> mLowLevelTireMap = new HashMap<>();//低优先级的轮胎颜色集合 level = 2的时候
    private Map<Integer, ImageView> mIvMap = new HashMap<>();//位置对应的imageview集合

    private int lastTemp1 = -255;
    private int lastTemp2 = -255;
    private int lastTemp4 = -255;
    private int lastTemp8 = -255;

    private int lastPressure1 = -255;
    private int lastPressure2 = -255;
    private int lastPressure4 = -255;
    private int lastPressure8 = -255;

    private int lastSystemWarning = -255;
    private int lastLost = -255;
    public static final String SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT = "km_and_oil_wear_unit";//单位 里程/油耗/高度
    private int currentUnit;
    private ImageView compass_pointer;

    public OffRoadInfoCardView(final Context context) {
        super(context);
        mContext = context;
        AAOP_HSkin.getInstance().registerSkinAttrHandler("offrodskin", new OffroadAttrHandler());//skin
        initData();
        initView();
        registerTireInfo();
        boolean isShowTireInfo = Settings.Global.getInt(mContext.getContentResolver(), "Tire_Pressure", 1) == 1;
        setTireInfoVisible(isShowTireInfo);
        Log.d(TAG, "test===> INIT");
        mCrossDataManager.setOnBcmConnectListener(new CrossDataManager.OnBcmConnectListener() {
            @Override
            public void bcmConnect() {
                getInItData();
            }
        });
        Log.d(TAG, "OffRoadInfoCardView: ");
        SkinChangeImpl.getInstance().addCallBack(this);
    }

    private void registerTireInfo() {
        mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("Tire_Pressure"),
                true, mObserver);
        initMap();
        TireColorStrategyManager.getInstance().registerColorChangeListener(mListener);
    }

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            boolean isShowTireInfo = Settings.Global.getInt(mContext.getContentResolver(), "Tire_Pressure", 1) == 1;
            currentUnit = Settings.Global.getInt(getAppContext().getContentResolver(),
                    SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT, -1);
            Log.d(TAG, "onChange: =========> currentUnit = " + currentUnit + "  isShowTireInfo" + isShowTireInfo);
            Log.d(TAG, "test===>: setTireNormalColor");
            setTireInfoVisible(isShowTireInfo);
            initVehicleAltitudeHeight();
            initVehicleAtmoSphericPressure();
        }
    };
    private final TireColorStrategyManager.IColorChangeListener mListener = (level, position) -> {
//        Log.d(TAG, "changeColor level aa: " + level + " postion = " + position);
        Log.d(TAG, "test===> (level, position)  "+ level+"  postion "+position);
        if (level == LEVEL_HIGH) {
            if (mHighLevelTireMap.containsKey(position)) {
                if (mIvMap.get(position) != null && mHighLevelTireMap.get(position) != null) {
                    ImageView iv = mIvMap.get(position);
                    int color = mHighLevelTireMap.get(position);
                    setFrontWheelColor(position, color);
                    Log.d(TAG, "test===> 111 setTireBg  "+ level+"  postion "+position);
                    setTireBg(iv, color);
                }
            }
        } else if (level == LEVEL_MIDDLE) {
            if (mMiddleLevelTireMap.containsKey(position)) {
                if (mIvMap.get(position) != null && mMiddleLevelTireMap.get(position) != null) {
                    ImageView iv = mIvMap.get(position);
                    int color = mMiddleLevelTireMap.get(position);
                    setFrontWheelColor(position, color);
                    Log.d(TAG, "test===> 222 setTireBg  "+ level+"  postion "+position);
                    setTireBg(iv, color);
                }
            }
        } else if (level == LEVEL_LOW) {
            if (mLowLevelTireMap.containsKey(position)) {
                if (mIvMap.get(position) != null && mLowLevelTireMap.get(position) != null) {
                    ImageView iv = mIvMap.get(position);
                    int color = mLowLevelTireMap.get(position);
                    setFrontWheelColor(position, color);
                    Log.d(TAG, "test===> 333 setTireBg  "+ level+"  postion "+position);
                    setTireBg(iv, color);
                }
            }
        }
    };

    private void getInItData() {
        Log.d(TAG, "test===> getInItData");
        //  胎压
        getTirePress();
        //  胎温
        getTireTemp();
        //分动箱状态
        tccuStatus = mCrossDataManager.getTCCUStatus();
        updateTransferCaseStatus(tccuStatus);
        Log.d(TAG, "getInItData: tccuStatus = " + tccuStatus);
        //差速锁状态
        frontEdsLock = mCrossDataManager.getFrontEdsLock();
        rearEdsLock = mCrossDataManager.getRearEdsLock();
        updateFontDifferentialLock(frontEdsLock);
        updateRearDifferentialLock(rearEdsLock);
        Log.d(TAG, "getInItData: frontEdsLock = " + frontEdsLock + " rearEdsLock = " + rearEdsLock);
        //轮胎打滑状态
        lfSlipStatus = mCrossDataManager.getLFSlipStatus();
        rfSlipStatus = mCrossDataManager.getRFSlipStatus();
        lrSlipStatus = mCrossDataManager.getLRSlipStatus();
        rrSlipStatus = mCrossDataManager.getRRSlipStatus();
        getWheelSlipStatus();
        initVehicleHorizontalAngle();//初始化水平角
        initVehiclePitchAngle();//初始化俯仰角
        initVehicleAltitudeHeight();//初始化相对高度
        initVehicleAtmoSphericPressure();//初始化大气压
        initVehicleCompassDirection();//初始化指南针方向
        initVehicleCompassSig();//初始化指南针角度

        iv_pitchangle.setPivotX(109);//设置指定旋转中心点X坐标
        iv_pitchangle.setPivotY(68);//设置指定旋转中心点Y坐标
        iv_horizontalangle.setPivotX(95);//设置指定旋转中心点X坐标
        iv_horizontalangle.setPivotY(73);//设置指定旋转中心点Y坐标

    }

    private void initData() {
        CanDataCallbcak dataCallBack = CanDataCallbcak.getControl();
        dataCallBack.init_McuComm();
        dataCallBack.setHandler(handler);
        mCrossDataManager = CrossDataManager.getInstance();
        mCrossDataManager.setHandler(handler);
        requestKmAndOilWearUnit();
    }

    public void requestKmAndOilWearUnit() {
        Log.d(TAG, "requestKmAndOilWearUnit: ");
        currentUnit = Settings.Global.getInt(getAppContext().getContentResolver(),
                SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT, -1);
        getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor(SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT),
                true, mObserver);
        Log.d(TAG, "requestKmAndOilWearUnit currentUnit: " + currentUnit);


    }

    private void setFrontWheelColor(int position, int color) {
        if (position == WHEEL_LEFT_FRONT) {
            iv_wheel_left_color = color;
        } else if (position == WHEEL_RIGHT_FRONT) {
            iv_wheel_right_color = color;
        }
    }

    private void initView() {

        mBigCardView = LayoutInflater.from(mContext).inflate(R.layout.bigcar_offroadinfo, null);
        iv_crossin_frame = (ImageView) mBigCardView.findViewById(R.id.iv_crossin_frame);
        ll_tab = (LinearLayout) mBigCardView.findViewById(R.id.ll_tab);
        rl_environment = (RelativeLayout) mBigCardView.findViewById(R.id.rl_environment);
        rl_vehicleinfo = (RelativeLayout) mBigCardView.findViewById(R.id.rl_vehicleinfo);
        cv_horizontal = (HorizontalCustomView) mBigCardView.findViewById(R.id.cv_horizontal);
        cv_pitch = (PitchCustomView) mBigCardView.findViewById(R.id.cv_pitch);
        rl_compass_pointer = (RelativeLayout) mBigCardView.findViewById(R.id.rl_compass_pointer);
        lfwheelImg = (ImageView) mBigCardView.findViewById(R.id.iv_wheel);
        axleImg = (ImageView) mBigCardView.findViewById(R.id.iv_axle);
        rfwheeltImg = (ImageView) mBigCardView.findViewById(R.id.iv_wheel_right);
        pointerImg = (ImageView) mBigCardView.findViewById(R.id.iv_pointer);
        iv_f_differential_lock = (ImageView) mBigCardView.findViewById(R.id.iv_f_differentiallock);
        iv_r_differential_lock = (ImageView) mBigCardView.findViewById(R.id.iv_r_differentiallock);
        lrwheelImg = (ImageView) mBigCardView.findViewById(R.id.lr_wheel);
        rrwheelImg = (ImageView) mBigCardView.findViewById(R.id.rr_wheel);
        lfTirePressureText = (TextView) mBigCardView.findViewById(R.id.tv_lf_tirepressure);
        rfTirepressureText = (TextView) mBigCardView.findViewById(R.id.tv_rf_tirepressure);
        lrTirepressureText = (TextView) mBigCardView.findViewById(R.id.tv_lr_tirepressure);
        rrTirepressureText = (TextView) mBigCardView.findViewById(R.id.tv_rr_tirepressure);
        lrTiretempText = (TextView) mBigCardView.findViewById(R.id.tv_lr_tiretemperature);
        rfTiretempText = (TextView) mBigCardView.findViewById(R.id.tv_rf_tiretemperature);
        lfTiretempText = (TextView) mBigCardView.findViewById(R.id.tv_lf_tiretemperature);
        rrTiretempText = (TextView) mBigCardView.findViewById(R.id.tv_rr_tiretemperature);
        iv_transfer_case_state = (ImageView) mBigCardView.findViewById(R.id.iv_transfer_case_state);
        tv_horizontalangle = (TextView) mBigCardView.findViewById(R.id.tv_horizontalangle);
        tv_horizontalangle.getPaint().setFakeBoldText(true);
        iv_horizontalangle = (ImageView) mBigCardView.findViewById(R.id.iv_horizontalangle);
        tv_pitchangle = (TextView) mBigCardView.findViewById(R.id.tv_pitchangle);
        tv_pitchangle.getPaint().setFakeBoldText(true);
        iv_pitchangle = (ImageView) mBigCardView.findViewById(R.id.iv_pitchangle);
        tv_altitude = (TextView) mBigCardView.findViewById(R.id.tv_altitude);
        tv_AtmoPressure = (TextView) mBigCardView.findViewById(R.id.tv_atmopressure);
        tv_compassdirection = (TextView) mBigCardView.findViewById(R.id.tv_compassdirection);
        tv_compassangle = (TextView) mBigCardView.findViewById(R.id.tv_compassangle);
        tv_orimBig = (TextView) mBigCardView.findViewById(R.id.tv_orimBig);
        crossinfo_blur = (BlurTransitionView) mBigCardView.findViewById(R.id.crossinfo_blur);
        vehicleInfo = (TextView) mBigCardView.findViewById(R.id.vehicleInfo);
        environInfo = (TextView) mBigCardView.findViewById(R.id.environInfo);
        tv_pressure_name = (TextView) mBigCardView.findViewById(R.id.tv_pressure_name);
        compass_pointer = (ImageView) mBigCardView.findViewById(R.id.compass_pointer);

        tv_pressure_name.setText(mContext.getResources().getString(R.string.atmopressure));
        tv_altitude_name = (TextView) mBigCardView.findViewById(R.id.tv_altitude_name);
        tv_wheelAngle = (TextView) mBigCardView.findViewById(R.id.tv_wheelAngle);
        mSmallCardView = LayoutInflater.from(mContext).inflate(R.layout.layout_smallcard_offroad, null);
        lottie_view = (LottieAnimationView) mSmallCardView.findViewById(R.id.lottie_view);
        tv_orimSmall = (TextView) mSmallCardView.findViewById(R.id.tv_orimSmall);
        iv_crossinfo_one = (ImageView) mSmallCardView.findViewById(R.id.iv_crossinfo_one);
        iv_crossinfo_frame = (ImageView) mSmallCardView.findViewById(R.id.iv_crossinfo_frame);
        iv_crossinfo_two = (ImageView) mSmallCardView.findViewById(R.id.iv_crossinfo_two);
        icon_altitude = (ImageView) mBigCardView.findViewById(R.id.icon_altitude);
        icon_pressure = (ImageView) mBigCardView.findViewById(R.id.icon_pressure);


        mTvUnit1 = (TextView) mBigCardView.findViewById(R.id.mTvUnit1);
        mTvUnit2 = (TextView) mBigCardView.findViewById(R.id.mTvUnit2);
        mTvUnit3 = (TextView) mBigCardView.findViewById(R.id.mTvUnit3);
        mTvUnit4 = (TextView) mBigCardView.findViewById(R.id.mTvUnit4);
        mTvUnit5 = (TextView) mBigCardView.findViewById(R.id.mTvUnit5);
        mTvUnit6 = (TextView) mBigCardView.findViewById(R.id.mTvUnit6);
        mTvUnit7 = (TextView) mBigCardView.findViewById(R.id.mTvUnit7);
        mTvUnit8 = (TextView) mBigCardView.findViewById(R.id.mTvUnit8);
        //单位颜色
        mTvUnit1.setTextColor(0xABffffff);
        mTvUnit2.setTextColor(0xABffffff);
        mTvUnit3.setTextColor(0xABffffff);
        mTvUnit4.setTextColor(0xABffffff);
        mTvUnit5.setTextColor(0xABffffff);
        mTvUnit6.setTextColor(0xABffffff);
        mTvUnit7.setTextColor(0xABffffff);
        mTvUnit8.setTextColor(0xABffffff);

        lfTirePressureText.setTextColor(0xffffffff);
        rfTirepressureText.setTextColor(0xffffffff);
        lrTirepressureText.setTextColor(0xffffffff);
        rrTirepressureText.setTextColor(0xffffffff);
        lrTiretempText.setTextColor(0xffffffff);
        rfTiretempText.setTextColor(0xffffffff);
        lfTiretempText.setTextColor(0xffffffff);
        rrTiretempText.setTextColor(0xffffffff);


        lfTireMarks = (ImageView) mBigCardView.findViewById(R.id.lfTireMarks);
        rfTireMarks = (ImageView) mBigCardView.findViewById(R.id.rfTireMarks);
        lrTireMarks = (ImageView) mBigCardView.findViewById(R.id.lrTireMarks);
        rrTireMarks = (ImageView) mBigCardView.findViewById(R.id.rrTireMarks);


        lfTireMarks.setAlpha(0f);
        rfTireMarks.setAlpha(0f);
        lrTireMarks.setAlpha(0f);
        rrTireMarks.setAlpha(0f);
//        tv_terminateNavigation.getPaint().setFakeBoldText(true);
        initSkinChange();
        Resources resources = mContext.getResources();
        lfwheelImg.setImageDrawable(resources.getDrawable(R.drawable.whell_40));
        axleImg.setImageDrawable(resources.getDrawable(R.drawable.axle_40));
        rfwheeltImg.setImageDrawable(resources.getDrawable(R.drawable.whell_right_40));
        vehicleInfo.setOnClickListener(this);
        environInfo.setOnClickListener(this);
        rl_vehicleinfo.setVisibility(VISIBLE);
        rl_environment.setVisibility(INVISIBLE);
        vehicleInfo.setSelected(true);
        environInfo.setSelected(false);


        int configFront_axle_lock = ConfigFunction.getInstance(getContext()).isConfigFront_Axle_Lock();
        int configRear_axle_lock = ConfigFunction.getInstance(getContext()).isConfigRear_Axle_Lock();
        if (configFront_axle_lock == IS_CONFIG) {
            iv_f_differential_lock.setVisibility(View.VISIBLE);
        } else {
            iv_f_differential_lock.setVisibility(View.GONE);
        }
        if (configRear_axle_lock == IS_CONFIG) {
            iv_r_differential_lock.setVisibility(View.VISIBLE);
        } else {
            iv_r_differential_lock.setVisibility(View.GONE);
        }


    }

    private void initSkinChange() {
        AAOP_HSkin.getWindowViewManager().addWindowView(mBigCardView);
        AAOP_HSkin.getWindowViewManager().addWindowView(mSmallCardView);

        AAOP_HSkin.with(cv_horizontal).addViewAttrs(new DynamicAttr("offrodskin")).applySkin(false);
        AAOP_HSkin.with(cv_pitch).addViewAttrs(new DynamicAttr("offrodskin")).applySkin(false);

        AAOP_HSkin.with(iv_crossinfo_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_smallcard)
                .applySkin(false);

        AAOP_HSkin.with(iv_crossinfo_one)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.mipmap.img_smallcard_crossinfo)
                .applySkin(false);

        AAOP_HSkin.with(iv_crossinfo_two)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.img_smallcard_crossinfo_b)
                .applySkin(false);

        AAOP_HSkin.with(environInfo)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector1)
                .applySkin(false);



        AAOP_HSkin.with(vehicleInfo)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector2)
                .applySkin(false);


        AAOP_HSkin.with(icon_pressure)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_pressure)
                .applySkin(false);

        AAOP_HSkin.with(icon_altitude)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_altitude)
                .applySkin(false);



        AAOP_HSkin.with(compass_pointer)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.compass_pointer)
                .applySkin(false);

        AAOP_HSkin
                .with(tv_horizontalangle)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.skinText)
                .applySkin(false);

        AAOP_HSkin
                .with(tv_pitchangle)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.skinText)
                .applySkin(false);

        lottie_view.setImageAssetsFolder("images");


        AAOP_HSkin.with(iv_crossin_frame)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_bigcard)
                .applySkin(false);

        AAOP_HSkin
                .with(environInfo)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.text_selector)
                .applySkin(false);

        AAOP_HSkin
                .with(vehicleInfo)
                .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.drawable.text_selector)
                .applySkin(false);
    }


    /**
     * 初始化数据
     */
    private void initMap() {
        mHighLevelTireMap.put(WHEEL_LEFT_FRONT, mWheelSlipLevelColor_1);
        mHighLevelTireMap.put(WHEEL_RIGHT_FRONT, mWheelSlipLevelColor_1);
        mHighLevelTireMap.put(WHEEL_LEFT_REAR, mWheelSlipLevelColor_1);
        mHighLevelTireMap.put(WHEEL_RIGHT_REAR, mWheelSlipLevelColor_1);
        mMiddleLevelTireMap.put(WHEEL_LEFT_FRONT, WHEEL_COLOR_RED);
        mMiddleLevelTireMap.put(WHEEL_RIGHT_FRONT, WHEEL_COLOR_RED);
        mMiddleLevelTireMap.put(WHEEL_LEFT_REAR, WHEEL_COLOR_RED);
        mMiddleLevelTireMap.put(WHEEL_RIGHT_REAR, WHEEL_COLOR_RED);
        mLowLevelTireMap.put(WHEEL_LEFT_FRONT, mWheelNomalColor);
        mLowLevelTireMap.put(WHEEL_RIGHT_FRONT, mWheelNomalColor);
        mLowLevelTireMap.put(WHEEL_LEFT_REAR, mWheelNomalColor);
        mLowLevelTireMap.put(WHEEL_RIGHT_REAR, mWheelNomalColor);
        mIvMap.put(WHEEL_LEFT_FRONT, lfwheelImg);
        mIvMap.put(WHEEL_RIGHT_FRONT, rfwheeltImg);
        mIvMap.put(WHEEL_LEFT_REAR, lrwheelImg);
        mIvMap.put(WHEEL_RIGHT_REAR, rrwheelImg);
    }

    /**
     * 轮胎打滑状态
     */

    private void getWheelSlipStatus() {
        lfSlipStatus = mCrossDataManager.getLFSlipStatus();
        rfSlipStatus = mCrossDataManager.getRFSlipStatus();
        lrSlipStatus = mCrossDataManager.getLRSlipStatus();
        rrSlipStatus = mCrossDataManager.getRRSlipStatus();
        updateWheelSlipStatus(WHEEL_LEFT_FRONT, lfSlipStatus);
        updateWheelSlipStatus(WHEEL_RIGHT_FRONT, rfSlipStatus);
        updateWheelSlipStatus(WHEEL_LEFT_REAR, lrSlipStatus);
        updateWheelSlipStatus(WHEEL_RIGHT_REAR, rrSlipStatus);
    }

    private void getTireTemp() {
        mLFTemp = mCrossDataManager.getTireTemperute(WHEEL_LEFT_FRONT);//温度四个区域都用左前
        mRFTemp = mCrossDataManager.getTireTemperute(WHEEL_RIGHT_FRONT);//温度四个区域都用左前
        mLRTemp = mCrossDataManager.getTireTemperute(WHEEL_LEFT_REAR);//温度四个区域都用左前
        mRRTemp = mCrossDataManager.getTireTemperute(WHEEL_RIGHT_REAR);//温度四个区域都用左前
        updateTire_Temperature_Value(WHEEL_LEFT_FRONT, mLFTemp);
        updateTire_Temperature_Value(WHEEL_RIGHT_FRONT, mRFTemp);
        updateTire_Temperature_Value(WHEEL_LEFT_REAR, mLRTemp);
        updateTire_Temperature_Value(WHEEL_RIGHT_REAR, mRRTemp);
    }

    private void getTirePress() {
        lf_tirePressure = mCrossDataManager.getTirePressure(WHEEL_LEFT_FRONT);
        rf_tirePressure = mCrossDataManager.getTirePressure(WHEEL_RIGHT_FRONT);
        lr_tirePressure = mCrossDataManager.getTirePressure(WHEEL_LEFT_REAR);
        rr_tirePressure = mCrossDataManager.getTirePressure(WHEEL_RIGHT_REAR);
        Log.d(TAG, "test===>: getTirePress");

        updateTirePressureValue(WHEEL_LEFT_FRONT, lf_tirePressure);
        updateTirePressureValue(WHEEL_RIGHT_FRONT, rf_tirePressure);
        updateTirePressureValue(WHEEL_LEFT_REAR, lr_tirePressure);
        updateTirePressureValue(WHEEL_RIGHT_REAR, rr_tirePressure);
    }

    /**
     * 更新胎压值
     *
     * @param areaId
     * @param value
     */
    private void updateTirePressureValue(int areaId, int value) {
        int tireLoss = CrossDataManager.getInstance().getTireLoss();
        if (tireLoss == 0x04) {//信号丢失
            Log.d(TAG, "test===>: 0x04");
            setAllTirePressureText(getResources().getColor(R.color.color_white1, null)
                    , getResources().getColor(R.color.color_white, null));
            TireColorStrategyManager.getInstance().setTireNormalColor(TYPE_TIRE_PRESSURE, areaId);
        } else {
            if (CrossDataManager.getInstance().isTireSystemWarning()) {//胎压系统故障
                Log.d(TAG, "test===> 胎压系统故障了");
                setAllTirePressureText(WHEEL_COLOR_RED, WHEEL_COLOR_RED);
                TireColorStrategyManager.getInstance().setTireSpecialColor(TYPE_TIRE_PRESSURE, areaId);
            } else {
                Log.d(TAG, "test===> setTirePressure 左前 左后 右前 右后");
                switch (areaId) {
                    case WHEEL_LEFT_FRONT://左前
                        setTirePressure(lfTirePressureText, mTvUnit1, areaId, value);
                        break;
                    case WHEEL_LEFT_REAR://左后
                        setTirePressure(lrTirepressureText, mTvUnit5, areaId, value);
                        break;
                    case WHEEL_RIGHT_FRONT://右前
                        setTirePressure(rfTirepressureText, mTvUnit3, areaId, value);
                        break;
                    case WHEEL_RIGHT_REAR://右后
                        setTirePressure(rrTirepressureText, mTvUnit7, areaId, value);
                        break;
                }
            }
        }
    }

    private void setAllTirePressureText(@ColorInt int unitColor, @ColorInt int txtColor) {
        Log.d(TAG, "test===>: setAllTirePressureText");
        String tirePressureTxt = "-.-";
        mTvUnit1.setTextColor(unitColor);
        mTvUnit5.setTextColor(unitColor);
        mTvUnit3.setTextColor(unitColor);
        mTvUnit7.setTextColor(unitColor);
        lfTirePressureText.setTextColor(txtColor);
        lrTirepressureText.setTextColor(txtColor);
        rfTirepressureText.setTextColor(txtColor);
        rrTirepressureText.setTextColor(txtColor);
        lfTirePressureText.setText(tirePressureTxt);
        lrTirepressureText.setText(tirePressureTxt);
        rfTirepressureText.setText(tirePressureTxt);
        rrTirepressureText.setText(tirePressureTxt);
    }

    private void setTirePressure(TextView tv, TextView tvUnit, int areaId, int value) {
        float result = value * Constant.TIREPRESSURECOEFFICIENT / 100;//kpa转bar 除以100
        Log.d(TAG, "test===> setTirePressure ");
        Log.d(TAG, "updateTirePressureValue: " + "areaId = " + areaId + " intValue = " + value + " result = " + result);
        //利用BigDecimal来实现四舍五入.保留一位小数
        double finalValue = new BigDecimal(result).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        //1代表保留1位小数,保留两位小数就是2,依此累推
        //BigDecimal.ROUND_HALF_UP 代表使用四舍五入的方式
        String tirePressureTxt = String.valueOf(finalValue);
        if (tirePressureTxt.equals("0.0")) {
            tirePressureTxt = "0";
        }
        if (CrossDataManager.getInstance().isTirePressureWarning(areaId)) {
            tv.setTextColor(WHEEL_COLOR_RED);
            tvUnit.setTextColor(WHEEL_COLOR_RED);
            tv.setText(tirePressureTxt);
            TireColorStrategyManager.getInstance().setTireSpecialColor(TYPE_TIRE_PRESSURE, areaId);
        } else {
            tv.setTextColor(getResources().getColor(R.color.color_white, null));
            tvUnit.setTextColor(getResources().getColor(R.color.color_white1, null));
            tv.setText(tirePressureTxt);
            Log.d(TAG, "test===> setTireNormalColor aa ");
            TireColorStrategyManager.getInstance().setTireNormalColor(TYPE_TIRE_PRESSURE, areaId);
        }
    }

    /**
     * 更新胎温值
     *
     * @param value
     */
    private void updateTire_Temperature_Value(int area, int value) {
        Log.d(TAG, "test===> updateTire_Temperature_Value "+value);

        int tireLoss = CrossDataManager.getInstance().getTireLoss();
        if (tireLoss == 0x04) {//信号丢失
            setAllTireTempText(getResources().getColor(R.color.color_white1, null)
                    , getResources().getColor(R.color.color_white, null));
            TireColorStrategyManager.getInstance().setTireNormalColor(TYPE_TIRE_TEMP, area);
        } else {
            if (CrossDataManager.getInstance().isTireSystemWarning()) {//胎压系统故障
                setAllTireTempText(WHEEL_COLOR_RED, WHEEL_COLOR_RED);
                TireColorStrategyManager.getInstance().setTireSpecialColor(TYPE_TIRE_TEMP, area);
            } else {
                switch (area) {
                    case WHEEL_LEFT_FRONT://左前
                        setTireTemp(lfTiretempText, mTvUnit2, area, value);
                        break;
                    case WHEEL_LEFT_REAR://左后
                        setTireTemp(lrTiretempText, mTvUnit6, area, value);
                        break;
                    case WHEEL_RIGHT_FRONT://右前
                        setTireTemp(rfTiretempText, mTvUnit4, area, value);
                        break;
                    case WHEEL_RIGHT_REAR://右后
                        setTireTemp(rrTiretempText, mTvUnit8, area, value);
                        break;
                }
            }
        }
    }

    private void setAllTireTempText(@ColorInt int unitColor, @ColorInt int txtColor) {
        String txt = "---";
        mTvUnit2.setTextColor(unitColor);
        mTvUnit6.setTextColor(unitColor);
        mTvUnit4.setTextColor(unitColor);
        mTvUnit8.setTextColor(unitColor);
        lfTiretempText.setTextColor(txtColor);
        rfTiretempText.setTextColor(txtColor);
        lrTiretempText.setTextColor(txtColor);
        rrTiretempText.setTextColor(txtColor);
        lfTiretempText.setText(txt);
        rfTiretempText.setText(txt);
        lrTiretempText.setText(txt);
        rrTiretempText.setText(txt);
    }

    private void setTireTemp(TextView tv, TextView tvUnit, int areaId, int value) {
        Log.d(TAG, "setTireTemp: areaId " + areaId + " value " + value + " tv " + tv);
        int temp = value - 40;
        boolean isWarning = CrossDataManager.getInstance().isTireTempWarning(areaId);
        boolean tirePressureWarning = CrossDataManager.getInstance().isTirePressureWarning(areaId);

        if (isWarning || tirePressureWarning) {
            Log.d(TAG, "setTireTemp: red");
            tv.setTextColor(WHEEL_COLOR_RED);
            tvUnit.setTextColor(WHEEL_COLOR_RED);
            tv.setText(String.valueOf(temp));
            TireColorStrategyManager.getInstance().setTireSpecialColor(TYPE_TIRE_TEMP, areaId);
        } else {
            Log.d(TAG, "setTireTemp: normal");
            tv.setTextColor(getResources().getColor(R.color.color_white, null));
            tvUnit.setTextColor(getResources().getColor(R.color.color_white1, null));
            tv.setText(String.valueOf(temp));
            TireColorStrategyManager.getInstance().setTireNormalColor(TYPE_TIRE_TEMP, areaId);
        }
    }

    /**
     * 更新分动箱状态
     */
    //todo 根据不同的值匹配不同的图标
    private void updateTransferCaseStatus(int value) {
        Log.d(TAG, "updateTransferCaseStatus: " + value);

        switch (value) {
            case 0:
                AAOP_HSkin.with(iv_transfer_case_state)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_2h)
                        .applySkin(false);
//                iv_transfer_case_state.setBackgroundResource(R.drawable.icon_2h);
                break;
            case 1:
                AAOP_HSkin.with(iv_transfer_case_state)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_4h)
                        .applySkin(false);
//                iv_transfer_case_state.setBackgroundResource(R.drawable.icon_4h);
                break;
            case 2:
                AAOP_HSkin.with(iv_transfer_case_state)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_4l)
                        .applySkin(false);
//                iv_transfer_case_state.setBackgroundResource(R.drawable.icon_4l);
                break;
            case 3:
                break;
        }
    }

    /**
     * 更新前差速锁状态
     */
    private void updateFontDifferentialLock(final int value) {
        Log.d(TAG, "updateFontDifferentialLock: " + value);
        if (mLastFontDifferentialValue == value) {
            return;
        }

        mLastFontDifferentialValue = value;
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(iv_f_differential_lock, "scaleX", 1f, 0f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(iv_f_differential_lock, "scaleY", 1f, 0f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(iv_f_differential_lock, "alpha", 1f, 0f);
        objectAnimator2.setDuration(666);
        objectAnimator2.setInterpolator(new AccelerateInterpolator());
        objectAnimator3.setDuration(666);
        objectAnimator3.setInterpolator(new AccelerateInterpolator());
        objectAnimator4.setDuration(666);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                switch (value) {
                    case INVALID:
                    case FAULT:
                    case LOCKREQUESTDENY:
                    case DRIVINGMODESELECTERROR:
                    case OVERSPEEDWARNING:
                        break;
                    case LOCKED:
                        iv_f_differential_lock.setBackgroundResource(R.drawable.icon_close_lock);
                        break;
                    case UNLOCKED:
                        iv_f_differential_lock.setBackgroundResource(R.drawable.icon_open_lock);
                        break;
                }
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(iv_f_differential_lock, "scaleX", 0f, 1f);
                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(iv_f_differential_lock, "scaleY", 0f, 1f);
                ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(iv_f_differential_lock, "alpha", 0f, 1f);
                objectAnimator2.setDuration(333);
                objectAnimator2.setInterpolator(new AccelerateInterpolator());
                objectAnimator3.setDuration(333);
                objectAnimator3.setInterpolator(new AccelerateInterpolator());
                objectAnimator4.setDuration(333);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4);
                animatorSet.start();
                Log.d(TAG, "onAnimationEnd: " + value);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**
     * 更新后差速锁状态
     */
    private void updateRearDifferentialLock(final int value) {
        Log.d(TAG, "updateFearDifferentialLock: " + value);
        if (mRearDifferentialValue == value) {
            return;
        }
        mRearDifferentialValue = value;
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(iv_r_differential_lock, "scaleX", 1f, 0f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(iv_r_differential_lock, "scaleY", 1f, 0f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(iv_r_differential_lock, "alpha", 1f, 0f);
        objectAnimator2.setDuration(333);
        objectAnimator2.setInterpolator(new AccelerateInterpolator());
        objectAnimator3.setDuration(333);
        objectAnimator3.setInterpolator(new AccelerateInterpolator());
        objectAnimator4.setDuration(333);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                switch (value) {
                    case INVALID:
                    case FAULT:
                    case LOCKREQUESTDENY:
                    case DRIVINGMODESELECTERROR:
                    case OVERSPEEDWARNING:
                        break;
                    case LOCKED:
                        iv_r_differential_lock.setBackgroundResource(R.drawable.icon_close_lock);
                        break;
                    case UNLOCKED:
                        iv_r_differential_lock.setBackgroundResource(R.drawable.icon_open_lock);
                        break;
                }
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(iv_r_differential_lock, "scaleX", 0f, 1f);
                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(iv_r_differential_lock, "scaleY", 0f, 1f);
                ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(iv_r_differential_lock, "alpha", 0f, 1f);
                objectAnimator2.setDuration(666);
                objectAnimator2.setInterpolator(new AccelerateInterpolator());
                objectAnimator3.setDuration(666);
                objectAnimator3.setInterpolator(new AccelerateInterpolator());
                objectAnimator4.setDuration(666);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4);
                animatorSet.start();
                Log.d(TAG, "onAnimationEnd: " + value);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    /**
     * 更新轮胎打滑状态
     */
    private void updateWheelSlipStatus(int area, int value) {
        updateWheelSlipStatusImg(area, value);
        Log.d(TAG, "updateWheelSlipStatus: area = " + area + " value = " + value);
        Log.d(TAG, "setTireNormalColor: 1168103 dddddd");
        if (value == 0) {
            TireColorStrategyManager.getInstance().setTireNormalColor(TireColorStrategyManager.TYPE_TIRE_SLIP, area);
        } else if (value == 1) {
            mHighLevelTireMap.put(area, mWheelSlipLevelColor_1);
            TireColorStrategyManager.getInstance().setTireSpecialColor(TireColorStrategyManager.TYPE_TIRE_SLIP, area);
        } else if (value == 2) {
            mHighLevelTireMap.put(area, mWheelSlipLevelColor_2);
            TireColorStrategyManager.getInstance().setTireSpecialColor(TireColorStrategyManager.TYPE_TIRE_SLIP, area);
        } else if (value == 3) {
            mHighLevelTireMap.put(area, mWheelSlipLevelColor_3);
            TireColorStrategyManager.getInstance().setTireSpecialColor(TireColorStrategyManager.TYPE_TIRE_SLIP, area);
        }
    }

    /**
     * 设置轮胎背景颜色
     */
    private void setTireBg(ImageView iv, int color) {
        Log.d(TAG, "setTireBg: iv = " + iv + " color = " + color);
        Drawable drawable = iv.getDrawable();

        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        iv.setImageDrawable(drawable);
//        Drawable drawable = lrwheelImg.getDrawable();
//        drawable.setColorFilter(mLRSwitch ? WHEEL_COLOR_RED : color, PorterDuff.Mode.SRC_ATOP);
//        lrwheelImg.setBackground(drawable);

    }

    private void updateWheelSlipStatusImg(int area, int value) {
        switch (area) {
            case 1:
                if (value == 1 || value == 2 || value == 3) {
                    if (lfTireMarks.getAlpha() != 0f) {
                        return;
                    }
                    ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(lfTireMarks, "alpha", 0f, 1f);
                    alphaAnim.setDuration(1000);
                    alphaAnim.start();

                } else {
                    lfTireMarks.setAlpha(0f);
                }

                break;
            case 2:
                if (value == 1 || value == 2 || value == 3) {
                    if (rfTireMarks.getAlpha() != 0f) {
                        return;
                    }
                    ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(rfTireMarks, "alpha", 0f, 1f);
                    alphaAnim.setDuration(1000);
                    alphaAnim.start();

                } else {
                    rfTireMarks.setAlpha(0f);
                }

                break;
            case 4:
                if (value == 1 || value == 2 || value == 3) {
                    if (lrTireMarks.getAlpha() != 0f) {
                        return;
                    }
                    ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(lrTireMarks, "alpha", 0f, 1f);
                    alphaAnim.setDuration(1000);
                    alphaAnim.start();

                } else {
                    lrTireMarks.setAlpha(0f);
                }

                break;
            case 8:
                if (value == 1 || value == 2 || value == 3) {

                    if (rrTireMarks.getAlpha() != 0f) {
                        return;
                    }
                    ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(rrTireMarks, "alpha", 0f, 1f);
                    alphaAnim.setDuration(1000);
                    alphaAnim.start();
                } else {
                    rrTireMarks.setAlpha(0f);
                }
                break;
        }
    }

    /**
     * 方向盘转角信号
     */
    private void startWheelAngleAnim(int resIndex, boolean isNormal) {
        duration = 1000;
        if (!isNormal || isFirstStartAnim) {
            duration = 0;
            isFirstStartAnim = false;
        }

        vehicleWheelAngleAnim = ValueAnimator.ofInt(lastAnimAngle, resIndex);
        lastAnimAngle = resIndex;
        vehicleWheelAngleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAnimValue = (int) animation.getAnimatedValue();
                if (80 < currentAnimValue || currentAnimValue < 0) {
                    return;
                }

                if (mContext == null) {
                    return;
                }

                lfWhellAnimDraw = new AnimationDrawable();
                lfWhellRes = getResources().getIdentifier("whell_" + currentAnimValue, "drawable", mContext.getPackageName());
                Drawable lfWhellDrawable = getResources().getDrawable(lfWhellRes);
                lfWhellDrawable.setColorFilter(iv_wheel_left_color, PorterDuff.Mode.SRC_ATOP);
                lfwheelImg.setImageDrawable(lfWhellAnimDraw);
                lfWhellAnimDraw.addFrame(lfWhellDrawable, 0);
                lfWhellAnimDraw.start();

                rfWhellDraw = new AnimationDrawable();
                rfWhellRes = getResources().getIdentifier("whell_right_" + currentAnimValue, "drawable", mContext.getPackageName());
                Drawable rfWhellDrawable = getResources().getDrawable(rfWhellRes);
                rfWhellDrawable.setColorFilter(iv_wheel_right_color, PorterDuff.Mode.SRC_ATOP);
                rfwheeltImg.setImageDrawable(rfWhellDraw);
                rfWhellDraw.addFrame(rfWhellDrawable, 0);
                rfWhellDraw.start();

                axleAnimDraw = new AnimationDrawable();
                axleRes = getResources().getIdentifier("axle_" + currentAnimValue, "drawable", mContext.getPackageName());
                Drawable axleDrawable = getResources().getDrawable(axleRes);
                axleDrawable.setColorFilter(iv_axle_color, PorterDuff.Mode.SRC_ATOP);
                axleImg.setImageDrawable(axleAnimDraw);
                axleAnimDraw.addFrame(axleDrawable, 0);
                axleAnimDraw.start();

                pointerAnimDraw = new AnimationDrawable();
                if (ConstantUtil.CURRENT_THEME == 1) {
                    pointerRes = getResources().getIdentifier("pointer_1_" + (currentAnimValue), "drawable", mContext.getPackageName());
                } else {
                    pointerRes = getResources().getIdentifier("pointer_2_" + (currentAnimValue), "drawable", mContext.getPackageName());
                }
                Drawable pointerDrawable = getResources().getDrawable(pointerRes);
                pointerImg.setImageDrawable(pointerAnimDraw);
                pointerAnimDraw.addFrame(pointerDrawable, duration);
                pointerAnimDraw.start();

            }
        });
        vehicleWheelAngleAnim.setDuration(duration);
        vehicleWheelAngleAnim.start();
    }

    private void setTireInfoVisible(boolean isVisible) {
        Log.d(TAG, "test===>: setTireInfoVisible");
        lfTirePressureText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit1.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        lfTiretempText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit2.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        rfTirepressureText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit3.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        rfTiretempText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit4.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        lrTirepressureText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit5.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        lrTiretempText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit6.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        rrTirepressureText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit7.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        rrTiretempText.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvUnit8.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新水平角数值
     */
    private void updateHorizontalAngleValue(int value) {
        Log.d(TAG, "updateHorizontalAngleValue: "+"  "+lastHorizontalangle+"    "+value);
        if (value >= -60 && value <= 60) {
            if (lastHorizontalangle == value) {
                return;
            }
            if (0 == value) {
                cv_horizontal.setPaint2Color(0);
            } else {
                cv_horizontal.setPaint2Color(1);
            }

            if (valueAnimator_horizontalangle != null) {
                valueAnimator_horizontalangle.cancel();
            }
            tv_horizontalangle.setText("" + Math.round(value));
            iv_horizontalangle.setPivotX(95);//设置指定旋转中心点X坐标
            iv_horizontalangle.setPivotY(73);//设置指定旋转中心点Y坐标
            if (valueAnimator_horizontalangle != null) {
                valueAnimator_horizontalangle.cancel();
            }
            valueAnimator_horizontalangle = ValueAnimator.ofFloat(mCurrentHorizontalangle, value);
            valueAnimator_horizontalangle.setDuration(Math.abs(Math.round(value - mCurrentHorizontalangle)) * 50);
            lastHorizontalangle = value;
            mCurrentHorizontalangle = value;
            valueAnimator_horizontalangle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue_horizontalangle = (float) animation.getAnimatedValue();
                    tv_horizontalangle.setText("" + Math.round(animatedValue_horizontalangle) + "°");
                    iv_horizontalangle.setRotation(animatedValue_horizontalangle);
                    cv_horizontal.setRotationAngle(animatedValue_horizontalangle);
                    mCurrentHorizontalangle = animatedValue_horizontalangle;
                }
            });
            valueAnimator_horizontalangle.start();
        } else {
            tv_horizontalangle.setText("- °");
            iv_horizontalangle.setRotation(0);
            cv_horizontal.setRotationAngle(0);
            cv_horizontal.setPaint2Color(0);
            lastHorizontalangle = 0;
        }
    }

    /**
     * 更新俯仰角数值
     */
    private void updatePitchAngleValue(int value) {
        Log.d(TAG, "updatePitchAngleValue: "+value);
        if (value >= -40 && value <= 40) {
            if (lastPitchAngle == value) {
                return;
            }
            if (0 == value) {
                cv_pitch.setPaint2Color(0);
            } else {
                cv_pitch.setPaint2Color(1);
            }
            if (valueAnimator_pitchangle != null) {
                valueAnimator_pitchangle.cancel();
            }
            tv_pitchangle.setText("" + Math.round(value));
            iv_pitchangle.setPivotX(109);//设置指定旋转中心点X坐标
            iv_pitchangle.setPivotY(68);//设置指定旋转中心点Y坐标
            if (valueAnimator_pitchangle != null) {
                valueAnimator_pitchangle.cancel();
            }
            valueAnimator_pitchangle = ValueAnimator.ofFloat(mCurrentPitchAngle, value);
            Log.d(TAG, "updatePitchAngleValue: " + Math.abs(Math.round(value - mCurrentPitchAngle)) + "value = " + value + "lastPitchAngle = " + lastPitchAngle);
            valueAnimator_pitchangle.setDuration(Math.abs(Math.round(value - mCurrentPitchAngle)) * 50);
            lastPitchAngle = value;
            mCurrentPitchAngle = value;
            valueAnimator_pitchangle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue_pitchangle = (float) animation.getAnimatedValue();
                    tv_pitchangle.setText("" + Math.round(animatedValue_pitchangle) + "°");
                    iv_pitchangle.setRotation(animatedValue_pitchangle);
                    cv_pitch.setRotationAngle(animatedValue_pitchangle);
                    mCurrentPitchAngle = animatedValue_pitchangle;
                }
            });
            valueAnimator_pitchangle.start();
        } else {
            tv_pitchangle.setText("- °");
            iv_pitchangle.setRotation(0);
            cv_pitch.setRotationAngle(0);
            cv_pitch.setPaint2Color(0);
            lastPitchAngle = 0;
        }
    }

    /**
     * 更新海拔数值
     */
    private void updateAltitudeValue(float value) {
        value = value + 500f;//offset
        if (value >= -500 & value <= 9000) {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_altitude.setText("" + Math.round(value) + " m");
            } else {
                double result = new BigDecimal(value * 3.28).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                tv_altitude.setText("" + Math.round(result) + " ft");
            }
        } else {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_altitude.setText("--- m");//invalid
            } else {
                tv_altitude.setText("--- ft");//invalid
            }
        }
        Log.d(TAG, "updateAltitudeValue: " + value);
    }

    /**
     * 更新大气压数值
     */
    private void updateAtmoPressure(float value) {
        Log.d(TAG, "updateAtmoPressure = " + value);

        value = value - 300f;//offset
        BigDecimal bd = new BigDecimal(value);
        value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

        if (value >= 300f && value <= 1100f) { //range

            if (currentUnit == 0 || currentUnit == -1) {
                tv_AtmoPressure.setText("" + value + " hPa");
            } else {
                double result = new BigDecimal(value * 0.0145).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

                tv_AtmoPressure.setText("" + result + " psi");
            }
        } else {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_AtmoPressure.setText("---.- hPa");//invalid
            } else {
                tv_AtmoPressure.setText("---.- psi");//invalid
            }
        }
    }

    /**
     * 更新指南针方向
     */
    private void updateCompassDirection(int mapKey) {
        Log.d(TAG, "updateCompassDirectionValue: " + mapKey);
        this.mapKey = mapKey;
        if (mCompassDirectionMap.containsKey(mapKey)) {
            int value = mCompassDirectionMap.get(mapKey);
            tv_compassdirection.setText("" + mContext.getResources().getString(value));
        } else {
            int value = mCompassDirectionMap.get(0);
            tv_compassdirection.setText("" + mContext.getResources().getString(value));//invalid
        }
    }

    /**
     * 更新指南针角度
     */
    private void updateCompassAngle(float value) {

        if (value < 0f || value > 360f) {
            tv_compassangle.setText(showInvalidValue());//文字
            rl_compass_pointer.setRotation(0f);
            mLastCompassAngleValue = 0;
        } else {
            if (mLastCompassAngleValue == value) {
                return;
            }
            tv_compassangle.setText(Math.round(value) + "");//
            if (valueAnimator_compass_pointer != null) {
                valueAnimator_compass_pointer.cancel();
            }

            //只换当前的value
            if (Math.abs(value - mCurrentCompassAngleValue) > 180) {
                if (value >= 0 && value < 180) {
                    mCurrentCompassAngleValue = mCurrentCompassAngleValue - 360;
                } else {
                    mCurrentCompassAngleValue = mCurrentCompassAngleValue + 360;
                }
            }
            Log.d(TAG, "updateCompassAngleValue: " + " current = " + mCurrentCompassAngleValue + " value = " + value);
            valueAnimator_compass_pointer = ValueAnimator.ofFloat(mCurrentCompassAngleValue, value);
            int animationTime = Math.abs(Math.round(value - mCurrentCompassAngleValue)) * 10;
            valueAnimator_compass_pointer.setDuration(animationTime);
            mLastCompassAngleValue = value;
            mCurrentCompassAngleValue = value;
            valueAnimator_compass_pointer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue_pitchangle = (float) animation.getAnimatedValue();
                    rl_compass_pointer.setRotation(animatedValue_pitchangle);
                    mCurrentCompassAngleValue = animatedValue_pitchangle;
                    int compassAngle = Math.round(mCurrentCompassAngleValue);
                    if (compassAngle < 0) {
                        compassAngle += 360;
                    }


                    tv_compassangle.setText(compassAngle + "");//

                }
            });
            valueAnimator_compass_pointer.start();
        }
        Log.d(TAG, "updateCompassAngleValue: " + value);


    }

    private String showInvalidValue() {

        return "--";
    }

    @Override
    public View initCardView(String id, String type, String type1) {

        if (type.equals(TYPE_BIGCARD)) {
            WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
                @Override
                public void setWallPaper(Bitmap bitmap) {
                    crossinfo_blur.setBitmap(5, 0.5f);
                    crossinfo_blur.show(0);
                }

                @Override
                public void resumeDefault() {
                    crossinfo_blur.setBitmap(5, 0.5f);
                    crossinfo_blur.show(0);
                }

                @Override
                public void deleteWallPaper() {
                    crossinfo_blur.setBitmap(5, 0.5f);
                    crossinfo_blur.show(0);
                }


            });


            return mBigCardView;
        } else if (type.equals(TYPE_SMALLCARD)) {
            if (type1.equals(DRAG_TO_INITCARD)) {
                AAOP_HSkin.with(lottie_view)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.offroadinfo45_lottie)
                        .applySkin(false);

            }
            if (type1.equals(DEFAULT_INITCARD) && CURRENT_THEME == 2) {
                tv_orimSmall.setAlpha(0);
            }
            return mSmallCardView;
        }
        return null;
    }


    @Override
    public void unInitCardView(String id, String type) {

        Log.d(TAG, "unInitCardView: cross");
    }

    @Override
    public void releaseResource() {

    }

    @Override
    public void playAnimation(String id, int delay) {
        Message message = Message.obtain();
        message.what = PLAY_ANIMATION;
        handler.sendMessageDelayed(message, delay);
    }

    public void onConfigurationChanged() {
        Log.d(TAG, "onConfigurationChanged: ");
        if (tv_orimSmall != null) {
            tv_orimSmall.setText(mContext.getResources().getString(R.string.orim));
        }
        if (tv_orimBig != null) {
            tv_orimBig.setText(mContext.getResources().getString(R.string.orim));
        }
        if (vehicleInfo != null) {
            vehicleInfo.setText(mContext.getResources().getString(R.string.tab2));
        }
        if (environInfo != null) {
            environInfo.setText(mContext.getResources().getString(R.string.tab1));
        }
        if (tv_altitude_name != null) {
            tv_altitude_name.setText(mContext.getResources().getString(R.string.altitude));
        }
        if (tv_pressure_name != null) {
            tv_pressure_name.setText(mContext.getResources().getString(R.string.atmopressure));
        }
        if (tv_compassdirection != null) {
            if (mCompassDirectionMap.containsKey(mapKey)) {
                int value = mCompassDirectionMap.get(mapKey);
                tv_compassdirection.setText("" + mContext.getResources().getString(value));
            }
        }
    }

    @Override
    public void launcherLoadComplete() {
        Log.d(TAG, "launcherLoadComplete: crossinfo");
        crossinfo_blur.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.app_bg_2);
                crossinfo_blur.setBitmap(5, 0.5f);
                crossinfo_blur.show(300);
            }
        });
    }

    @Override
    public void launcherAnimationUpdate(int i) {
        if (i == 1) {
            crossinfo_blur.show(300);
        } else {
            if (crossinfo_blur.getIsShow()) {
                crossinfo_blur.hide(0);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.vehicleInfo) {
            CURRENT_TAB = VEHICLEINFORMATION;
            rl_vehicleinfo.setVisibility(VISIBLE);
            rl_environment.setVisibility(INVISIBLE);
            vehicleInfo.setSelected(true);
            environInfo.setSelected(false);
//            vehicleInfo.setTextColor(Color.WHITE);
//            environInfo.setTextColor(Color.WHITE);
        } else if (v.getId() == R.id.environInfo) {
            CURRENT_TAB = ENVIRONMENTALINFORMATION;
            rl_vehicleinfo.setVisibility(INVISIBLE);
            rl_environment.setVisibility(VISIBLE);
            vehicleInfo.setSelected(false);
            environInfo.setSelected(true);
//            vehicleInfo.setTextColor(Color.WHITE);
//            environInfo.setTextColor(Color.WHITE);
        }
    }


    /**
     * 初始化水平角
     */
    private void initVehicleHorizontalAngle() {
        int naviAroundTitlingAngle = mCrossDataManager.getNaviAroundTitlingAngle();//获取水平角
        Log.d(TAG, "initVehicleHorizontalAngle: " + naviAroundTitlingAngle);
        if (naviAroundTitlingAngle == -999 || naviAroundTitlingAngle < -60 || naviAroundTitlingAngle > 60) {//无效
            tv_horizontalangle.setText("" + "-" + "°");//水平角 文字
            iv_horizontalangle.setRotation(0);//水平角
            cv_horizontal.setRotationAngle(0);//绿色弧线
            cv_horizontal.setPaint2Color(0);

        } else {
            tv_horizontalangle.setText("" + Math.round(naviAroundTitlingAngle) + "°");//水平角 文字
            iv_horizontalangle.setRotation(-naviAroundTitlingAngle);//水平角
            cv_horizontal.setRotationAngle(-naviAroundTitlingAngle);//绿色弧线

            if (0 == naviAroundTitlingAngle) {
                cv_horizontal.setPaint2Color(0);
            } else {
                cv_horizontal.setPaint2Color(1);
            }

        }
    }

    /**
     * 初始化俯仰角
     */
    private void initVehiclePitchAngle() {

        int naviPitchAngle = mCrossDataManager.getNaviPitchAngle();//获得俯仰角
        Log.d(TAG, "initVehiclePitchAngle: " + naviPitchAngle);
        if (naviPitchAngle == -999 || naviPitchAngle < -40 || naviPitchAngle > 40) {//无效值
            tv_pitchangle.setText("" + "-" + "°");
            iv_pitchangle.setRotation(0);
            cv_pitch.setRotationAngle(0);
            cv_pitch.setPaint2Color(0);

        } else {
            tv_pitchangle.setText("" + Math.round(naviPitchAngle) + "°");
            iv_pitchangle.setRotation(-naviPitchAngle);
            cv_pitch.setRotationAngle(-naviPitchAngle);

            if (0 == naviPitchAngle) {
                cv_pitch.setPaint2Color(0);
            } else {
                cv_pitch.setPaint2Color(1);
            }
        }
    }

    /**
     * 获取相对高度
     */
    private void initVehicleAltitudeHeight() {
        float naviAltitudeHeight = mCrossDataManager.getNaviAltitudeHeight();
        naviAltitudeHeight = naviAltitudeHeight + 500;
        BigDecimal bd = new BigDecimal(naviAltitudeHeight);
        naviAltitudeHeight = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        Log.d(TAG, "initVehicleAltitudeHeight: " + naviAltitudeHeight);
        if (naviAltitudeHeight >= -500 & naviAltitudeHeight <= 9000) {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_altitude.setText("" + Math.round(naviAltitudeHeight) + " m");
            } else {
                double result = new BigDecimal(naviAltitudeHeight * 3.28).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                tv_altitude.setText("" + Math.round(result) + " ft");
            }
        } else {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_altitude.setText("--- m");//invalid
            } else {
                tv_altitude.setText("--- ft");//invalid
            }
        }
    }

    /**
     * 初始化大气压
     */
    private void initVehicleAtmoSphericPressure() {

        float naviAtmoSphericPressure = mCrossDataManager.getNaviAtmoSphericPressure();
        naviAtmoSphericPressure = naviAtmoSphericPressure - 300f;
        BigDecimal bd = new BigDecimal(naviAtmoSphericPressure);
        naviAtmoSphericPressure = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        Log.d(TAG, "initVehicleAtmoSphericPressure: =" + naviAtmoSphericPressure);

        if (naviAtmoSphericPressure >= 300f && naviAtmoSphericPressure <= 1100f) { //range

            if (currentUnit == 0 || currentUnit == -1) {
                tv_AtmoPressure.setText("" + naviAtmoSphericPressure + " hPa");
            } else {
                double result = new BigDecimal(naviAtmoSphericPressure * 0.0145).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

                tv_AtmoPressure.setText("" + result + " psi");
            }
        } else {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_AtmoPressure.setText("---.- hPa");//invalid
            } else {
                tv_AtmoPressure.setText("---.- psi");//invalid
            }
        }
    }

    /**
     * 初始化指南针方向
     */
    private void initVehicleCompassDirection() {
        int naviCompassDirection = mCrossDataManager.getNaviCompassDirection();
        Log.d(TAG, "initVehicleCompassDirection: ");
        updateCompassDirection(naviCompassDirection);
    }

    /**
     * 初始化指南针角度
     */
    private void initVehicleCompassSig() {
        float naviCompassSig = mCrossDataManager.getNaviCompassSig();
        Log.d(TAG, "initVehicleCompassSig: " + naviCompassSig);
        if (naviCompassSig >= 0 && naviCompassSig <= 360) {
            rl_compass_pointer.setRotation(naviCompassSig);
            tv_compassangle.setText(Math.round(naviCompassSig) + "");
        } else {
            tv_compassangle.setText("--");
            rl_compass_pointer.setRotation(0);
        }


    }

    @Override
    public void skinChangeCallBack(int skin) {

        pointerAnimDraw = new AnimationDrawable();
        if (CURRENTTHEME == 1) {
            pointerRes = getResources().getIdentifier("pointer_1_" + (resIndex), "drawable", mContext.getPackageName());
        } else if (CURRENTTHEME == 2) {
            pointerRes = getResources().getIdentifier("pointer_2_" + (resIndex), "drawable", mContext.getPackageName());
        }
        Drawable pointer_id_drawable = getResources().getDrawable(pointerRes);
        pointerImg.setImageDrawable(pointerAnimDraw);
        pointerAnimDraw.addFrame(pointer_id_drawable, 0);
    }


}

