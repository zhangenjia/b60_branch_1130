package com.adayo.app.crossinf.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.car.VehiclePropertyIds;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;

import com.adayo.app.crossinf.CarConfigManager;
import com.adayo.app.crossinf.MyApplication;
import com.adayo.app.crossinf.presenter.CanDataCallbcak;
import com.adayo.app.crossinf.MainActivity;
import com.adayo.app.crossinf.R;
import com.adayo.app.crossinf.model.VehicleDataInFo;
import com.adayo.app.crossinf.presenter.ISkinChangeListener;
import com.adayo.app.crossinf.presenter.SkinChangeImpl;
import com.adayo.app.crossinf.presenter.CarSettingManager;
import com.adayo.app.crossinf.presenter.TireColorStrategyManager;
import com.adayo.app.crossinf.util.Constant;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static android.car.VehicleAreaWheel.WHEEL_LEFT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_LEFT_REAR;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_FRONT;
import static android.car.VehicleAreaWheel.WHEEL_RIGHT_REAR;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.adayo.app.crossinf.CarConfigManager.CONSTANT_CONFIG;
import static com.adayo.app.crossinf.presenter.CarSettingManager.CAR_SETTING_DATA;
import static com.adayo.app.crossinf.presenter.TireColorStrategyManager.LEVEL_HIGH;
import static com.adayo.app.crossinf.presenter.TireColorStrategyManager.LEVEL_LOW;
import static com.adayo.app.crossinf.presenter.TireColorStrategyManager.LEVEL_MIDDLE;
import static com.adayo.app.crossinf.presenter.TireColorStrategyManager.TYPE_TIRE_PRESSURE;
import static com.adayo.app.crossinf.presenter.TireColorStrategyManager.TYPE_TIRE_TEMP;
import static com.adayo.app.crossinf.util.Constant.CURRENTTHEME;
import static com.adayo.app.crossinf.util.Constant.DRIVINGMODESELECTERROR;
import static com.adayo.app.crossinf.util.Constant.FAULT;
import static com.adayo.app.crossinf.util.Constant.INVALID;
import static com.adayo.app.crossinf.util.Constant.LOCKED;
import static com.adayo.app.crossinf.util.Constant.LOCKREQUESTDENY;
import static com.adayo.app.crossinf.util.Constant.OVERSPEEDWARNING;
import static com.adayo.app.crossinf.util.Constant.UNLOCKED;
import static com.adayo.proxy.media.mediascanner.utils.ContextUtil.getAppContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleInFragment extends BaseFragment implements ISkinChangeListener.CallBack {


    private static final String TAG = Constant.Versiondate + VehicleInFragment.class.getSimpleName();
    private AnimationDrawable mWhellAnimationDrawable;
    private AnimationDrawable mRightWhellAnimationDrawable;
    private AnimationDrawable axleAnimationDrawable;
    private AnimationDrawable pointerAnimationDrawable;

    private int iv_wheel_left_color = 0x00FFFFFF;
    private int iv_axle_color = 0x00FFFFFF;
    private int iv_wheel_right_color = 0x00FFFFFF;
    private int mWheelNomalColor = 0x00FFFFFF;
    private int mWheelSlipLevelColor_1 = 0x7A405c79;//
    private int mWheelSlipLevelColor_2 = 0x7A4d4d1e;//
    private int mWheelSlipLevelColor_3 = 0x7A582726;//
//    private static final int WHEEL_COLOR_RED = 0x7AB23633;
    private static final int WHEEL_COLOR_RED = 0x7Afe2b14;//0x7Afe2b14
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_WHITE1 = 0xABffffff;


    private TextView lfTirePressureText;
    private TextView lfTiretempText;
    private TextView rfTirepressureText;
    private TextView rfTiretempText;
    private TextView lrTirepressureText;
    private TextView lrTiretempText;
    private TextView rrTirepressureText;
    private TextView rrTiretempText;
    private TextView wheelAngleText;
    private ImageView transferImg;
    private ImageView fLockImg;
    private ImageView rLockImg;

    private ImageView lrwheelImg;
    private ImageView rrwheelImg;
    //********************20220822**********************************//
    private ImageView mIvLineUpperLeft;
    private ImageView mIvLineUpperRight;
    private ImageView mIvLineLowerLeft;
    private ImageView mIvLineLowerRight;
    private TextView mTvUnit1;
    private TextView mTvUnit2;
    private TextView mTvUnit3;
    private TextView mTvUnit4;
    private TextView mTvUnit5;
    private TextView mTvUnit6;
    private TextView mTvUnit7;
    private TextView mTvUnit8;
    //********************20220822**********************************//
    private MainActivity activity;
    private CarSettingManager mCarSettingManager;
    private int mLFTemp;
    private int mRFTemp;
    private int mLRTemp;
    private int mRRTemp;
    private int tccuStatus;
    private int frontEdsLock;
    private int rearEdsLock;
    private int lfSlipStatus;
    private int rfSlipStatus;
    private int lrSlipStatus;
    private int rrSlipStatus;

    private static final float coefficient = 0.0551f;

    // 车轮转角初始化的值
    private int vehicleWheelAngle;
    private int lastAngle = 255;
    private int lastAnimAngle;
    private int resIndex = 0;
    private boolean isFirstTime = true;
    private int duration;
    private AnimationDrawable lfWhellAnimDraw;
    private AnimationDrawable rfWhellDraw;
    private AnimationDrawable axleAnimDraw;
    private AnimationDrawable pointerAnimDraw;
    private ValueAnimator vehicleWheelAngleAnim;
    private int currentAnimValue;
    private int lfWhellRes;
    private int rfWhellRes;
    private int axleRes;
    private int pointerRes;
    private ImageView lfwheelImg;
    private ImageView axleImg;
    private ImageView rfwheeltImg;
    private ImageView pointerImg;
    private int lf_tirePressure;
    private int rf_tirePressure;
    private int lr_tirePressure;
    private int rr_tirePressure;
    private int mLastFontDifferentialLockValue = -1;
    private int mLastFearDifferentialLockValue = -1;
    private View view;
    private OnFragmentResumeListener listener;
    private boolean isInflateView;
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



    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isInflateView) {
                return;
            }
            if (msg.what == 8300) {
//
                vehicleWheelAngle = (int) msg.obj;
                if (wheelAngleText == null) {//控件没初始化完直接return
                    return;
                }
//                Log.d(TAG, "handleMessage:wheelAngle " + vehicleWheelAngle);
                if (lastAngle != vehicleWheelAngle) {
                    lastAngle = vehicleWheelAngle;
                    Log.d(TAG, "handleMessage: resIndex " + resIndex);
                    if (vehicleWheelAngle < -40 || vehicleWheelAngle > 40) {//异常值
                        wheelAngleText.setText("--");//
                        resIndex = 40;
                        startWheelAngleAnim(resIndex, false);
                        return;
                    }
                    // 正常更新
                    wheelAngleText.setText(String.valueOf(Math.abs(vehicleWheelAngle)));
                    //一共 81张图 正度数往左转
                    resIndex = 40 - vehicleWheelAngle;
                    startWheelAngleAnim(resIndex, true);
                }
            } else if (msg.what == CAR_SETTING_DATA) {
                //todo 更新数据
                VehicleDataInFo mVehicleDataInFo = (VehicleDataInFo) msg.obj;
                Bundle bundle = mVehicleDataInFo.getBundle();
                int properId = mVehicleDataInFo.getId();
                switch (properId) {
                    case VehiclePropertyIds.TIRE_PRESSURS_VALUE://胎压
                        int mPressurevalue = bundle.getInt("IntValue");
                        int mPressureAreaID = bundle.getInt("AreaID");
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
                        Log.d(TAG, "handleMessage:aa TIRE_SYSTEM_STATE  " );
                        int systemWarning = bundle.getInt("IntValue");
                        if (lastSystemWarning!=systemWarning){
                            lastSystemWarning = systemWarning;
                        }else {
                            return;
                        }
                        getTirePress();
                        getTireTemp();
                        break;
                    case VehiclePropertyIds.TIRE_PRESSURES_STATUS://胎压报警
                        Log.d(TAG, "handleMessage:aa TIRE_PRESSURES_STATUS  " );
                        getTirePress();
                        getTireTemp();
                        break;
                    case VehiclePropertyIds.TIRE_TEMPERATURS_STATUS://胎温报警
                        Log.d(TAG, "handleMessage:aa TIRE_TEMPERATURS_STATUS  " );
                        getTirePress();
                        getTireTemp();
                        break;
                    case VehiclePropertyIds.TIRE_SYSTEM_ALARM_PROMPT://系统报警
                        Log.d(TAG, "handleMessage:aa TIRE_SYSTEM_ALARM_PROMPT  " );
                        int loss = bundle.getInt("IntValue");
                        if (lastLost!=loss){
                            lastLost = loss;
                        }else {
                            return;
                        }
                        getTirePress();
                        getTireTemp();
                        break;


                    // todo 方向盘转角
                    case VehiclePropertyIds.TCCU1_ST_4H4L://分动箱状态
                        int mTransfer_Case_State_Value = bundle.getInt("IntValue");
                        updateTransferCaseStatus(mTransfer_Case_State_Value);
                        break;
                    case VehiclePropertyIds.EMS_EDS1_ST_FRONTEDSLOCK://前差速锁状态
                        int mFontDifferentialLockValue = bundle.getInt("IntValue");
                        updateFontDifferentialLock(mFontDifferentialLockValue);
                        break;
                    case VehiclePropertyIds.EMS_EDS1_ST_REAREDSLOCK://后差速锁状态
                        int mFearDifferentialLockValue = bundle.getInt("IntValue");
                        updateFearDifferentialLock(mFearDifferentialLockValue);
                        break;
                    case VehiclePropertyIds.LF_WHEEL_SLIP_STATUS://左前轮打滑程度
                        int mLfWheelSlipStatusValue = bundle.getInt("IntValue");

                        updateWheelSlipStatus(WHEEL_LEFT_FRONT, mLfWheelSlipStatusValue);
                        break;
                    case VehiclePropertyIds.RF_WHEEL_SLIP_STATUS://右前轮打滑程度
                        int mRfWheelSlipStatusValue = bundle.getInt("IntValue");

                        updateWheelSlipStatus(WHEEL_RIGHT_FRONT, mRfWheelSlipStatusValue);
                        break;
                    case VehiclePropertyIds.LR_WHEEL_SLIP_STATUS://左后轮打滑程度
                        int mLrWheelSlipStatusValue = bundle.getInt("IntValue");

                        updateWheelSlipStatus(WHEEL_LEFT_REAR, mLrWheelSlipStatusValue);
                        break;
                    case VehiclePropertyIds.RR_WHEEL_SLIP_STATUS://右后轮打滑程度
                        int mRrWheelSlipStatusValue = bundle.getInt("IntValue");

                        updateWheelSlipStatus(WHEEL_RIGHT_REAR, mRrWheelSlipStatusValue);
                        break;
                    default:
                        Log.d(TAG, "Invalid parameter!");
                        break;
                }
            }

        }
    };
    private ImageView lfTireMarks;
    private ImageView rfTireMarks;
    private ImageView lrTireMarks;
    private ImageView rrTireMarks;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        activity.setHandler(handler);
        CanDataCallbcak.getControl().init_McuComm();
        Log.d(TAG, "onAttach: ");
        CanDataCallbcak.getControl().setHandler(handler);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vehicle_information, container, false);
        initView();
        // TODO: 2022/8/22 胎压监测信号只有下发没有上报需要和车辆设置联调
        registerTireInfo();
        getInItData();
        initSkinChange();
        SkinChangeImpl.getInstance().addCallBack(this);
        boolean isShowTireInfo = Settings.Global.getInt(MyApplication.getContext().getContentResolver(), "Tire_Pressure", 1) == 1;
        setTireInfoVisible(isShowTireInfo);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            listener.onFragmentResume();
        }
    }


    private void registerTireInfo() {
        MyApplication.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("Tire_Pressure"),
                true, mObserver);
        initMap();
        TireColorStrategyManager.getInstance().registerColorChangeListener(mListener);
    }

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            boolean isShowTireInfo = Settings.Global.getInt(MyApplication.getContext().getContentResolver(), "Tire_Pressure", 1) == 1;
            setTireInfoVisible(isShowTireInfo);


        }
    };

    private final TireColorStrategyManager.IColorChangeListener mListener = (level, position) -> {
        Log.d(TAG, "changeColor level bb: " + level + " postion = " + position);
        if (level == LEVEL_HIGH) {
            if (mHighLevelTireMap.containsKey(position)) {
                if (mIvMap.get(position) != null && mHighLevelTireMap.get(position) != null) {
                    ImageView iv = mIvMap.get(position);
                    int color = mHighLevelTireMap.get(position);
                    setFrontWheelColor(position, color);
                    setTireBg(iv, color);
                }
            }
        } else if (level == LEVEL_MIDDLE) {
            if (mMiddleLevelTireMap.containsKey(position)) {
                if (mIvMap.get(position) != null && mMiddleLevelTireMap.get(position) != null) {
                    ImageView iv = mIvMap.get(position);
                    int color = mMiddleLevelTireMap.get(position);
                    setFrontWheelColor(position, color);
                    setTireBg(iv, color);
                }
            }
        } else if (level == LEVEL_LOW) {
            if (mLowLevelTireMap.containsKey(position)) {
                if (mIvMap.get(position) != null && mLowLevelTireMap.get(position) != null) {
                    ImageView iv = mIvMap.get(position);
                    int color = mLowLevelTireMap.get(position);
                    setFrontWheelColor(position, color);
                    setTireBg(iv, color);
                }
            }
        }
    };


    /**
     * 前轮转向 //todo 获取
     * 胎温 // todo area
     * 胎压 //todo area
     * 打滑程度
     * 前差速锁状态
     * 后差速锁状态
     * 分动箱状态
     */

    private void getInItData() {
        mCarSettingManager = CarSettingManager.getInstance();
        //胎压
        getTirePress();
        //胎温
        getTireTemp();
        //分动箱状态
        tccuStatus = mCarSettingManager.getTCCUStatus();
        updateTransferCaseStatus(tccuStatus);
        Log.d(TAG, "getInItData: tccuStatus = " + tccuStatus);
        //差速锁状态
        frontEdsLock = mCarSettingManager.getFrontEdsLock();
        rearEdsLock = mCarSettingManager.getRearEdsLock();
        updateFontDifferentialLock(frontEdsLock);
        updateFearDifferentialLock(rearEdsLock);
        Log.d(TAG, "getInItData: frontEdsLock = " + frontEdsLock + " rearEdsLock = " + rearEdsLock);
        getWheelSlipStatus();
        //车轮转向
        int steelAngle = CanDataCallbcak.getControl().getAngle();
        float i = ((float) (steelAngle - 7800) / 10) * coefficient;
        Message msg = Message.obtain();
        msg.what = 8300;
        msg.obj = Math.round(i);
        if (handler != null) {
            handler.sendMessage(msg);
        }
    }

    private void setFrontWheelColor(int position, int color) {
        if (position == WHEEL_LEFT_FRONT) {
            iv_wheel_left_color = color;
        } else if (position == WHEEL_RIGHT_FRONT) {
            iv_wheel_right_color = color;
        }
    }

    private void initView() {
        int configFront_axle_lock = CarConfigManager.getInstance(getContext()).isConfigFront_Axle_Lock();
        int configRear_axle_lock = CarConfigManager.getInstance(getContext()).isConfigRear_Axle_Lock();

        lfwheelImg = (ImageView) view.findViewById(R.id.iv_wheel);//左前
        axleImg = (ImageView) view.findViewById(R.id.iv_axle);//前横梁
        rfwheeltImg = (ImageView) view.findViewById(R.id.iv_wheel_right);//右前
        pointerImg = (ImageView) view.findViewById(R.id.iv_pointer);//指针
        transferImg = (ImageView) view.findViewById(R.id.iv_transfer_case_state);
        fLockImg = (ImageView) view.findViewById(R.id.iv_f_differential_lock);
        rLockImg = (ImageView) view.findViewById(R.id.iv_r_differential_lock);

        lfTirePressureText = (TextView) view.findViewById(R.id.tv_lf_tirepressure);
        lfTiretempText = (TextView) view.findViewById(R.id.tv_lf_tiretemperature);
        rfTirepressureText = (TextView) view.findViewById(R.id.tv_rf_tirepressure);
        rfTiretempText = (TextView) view.findViewById(R.id.tv_rf_tiretemperature);
        lrTirepressureText = (TextView) view.findViewById(R.id.tv_lr_tirepressure);
        lrTiretempText = (TextView) view.findViewById(R.id.tv_lr_tiretemperature);
        rrTirepressureText = (TextView) view.findViewById(R.id.tv_rr_tirepressure);
        rrTiretempText = (TextView) view.findViewById(R.id.tv_rr_tiretemperature);
        wheelAngleText = (TextView) view.findViewById(R.id.tv_wheelangle);

        lrwheelImg = (ImageView) view.findViewById(R.id.iv_wheel_rleft);
        rrwheelImg = (ImageView) view.findViewById(R.id.iv_wheel_rright);

        mIvLineUpperLeft = view.findViewById(R.id.iv_line_upper_left);
        mIvLineUpperRight = view.findViewById(R.id.iv_line_upper_right);
        mIvLineLowerLeft = view.findViewById(R.id.iv_line_lower_left);
        mIvLineLowerRight = view.findViewById(R.id.iv_line_lower_right);

        mTvUnit1 = view.findViewById(R.id.tv_unit1);
        mTvUnit2 = view.findViewById(R.id.tv_unit2);
        mTvUnit3 = view.findViewById(R.id.tv_unit3);
        mTvUnit4 = view.findViewById(R.id.tv_unit4);
        mTvUnit5 = view.findViewById(R.id.tv_unit5);
        mTvUnit6 = view.findViewById(R.id.tv_unit6);
        mTvUnit7 = view.findViewById(R.id.tv_unit7);
        mTvUnit8 = view.findViewById(R.id.tv_unit8);

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



        lfTireMarks = (ImageView) view.findViewById(R.id.lfTireMarks);
        rfTireMarks = (ImageView) view.findViewById(R.id.rfTireMarks);
        lrTireMarks = (ImageView) view.findViewById(R.id.lrTireMarks);
        rrTireMarks = (ImageView) view.findViewById(R.id.rrTireMarks);



        lfTireMarks.setAlpha(0f);
        rfTireMarks.setAlpha(0f);
        lrTireMarks.setAlpha(0f);
        rrTireMarks.setAlpha(0f);


        isInflateView = true;

        Resources resources = getActivity().getResources();
        lfwheelImg.setImageDrawable(resources.getDrawable(R.drawable.whell_40));
        axleImg.setImageDrawable(resources.getDrawable(R.drawable.axle_40));
        rfwheeltImg.setImageDrawable(resources.getDrawable(R.drawable.whell_right_40));
        pointerImg.setImageDrawable(resources.getDrawable(R.drawable.pointer_1_40));

        lrwheelImg.setImageDrawable(resources.getDrawable(R.drawable.rr_wheel));

        rrwheelImg.setImageDrawable(resources.getDrawable(R.drawable.lr_wheel));
        if (configFront_axle_lock == CONSTANT_CONFIG) {
            fLockImg.setVisibility(VISIBLE);
        } else {
            fLockImg.setVisibility(View.GONE);
        }
        if (configRear_axle_lock == CONSTANT_CONFIG) {
            rLockImg.setVisibility(VISIBLE);
        } else {
            rLockImg.setVisibility(View.GONE);
        }
        AAOP_HSkin.with(pointerImg).addViewAttrs(new DynamicAttr("pichangle")).applySkin(false);
    }


    private void initSkinChange() {

//        AAOP_HSkin
//                .with(iv_transfer_case_state)
//                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_2h)
//                .applySkin(false);
    }


    /**
     * 初始化数据
     */
    private void initMap() {
        mHighLevelTireMap.put(WHEEL_LEFT_FRONT, mWheelSlipLevelColor_1);//打滑 （）
        mHighLevelTireMap.put(WHEEL_RIGHT_FRONT, mWheelSlipLevelColor_1);
        mHighLevelTireMap.put(WHEEL_LEFT_REAR, mWheelSlipLevelColor_1);
        mHighLevelTireMap.put(WHEEL_RIGHT_REAR, mWheelSlipLevelColor_1);

        mMiddleLevelTireMap.put(WHEEL_LEFT_FRONT, WHEEL_COLOR_RED);//胎压胎温各种异常
        mMiddleLevelTireMap.put(WHEEL_RIGHT_FRONT, WHEEL_COLOR_RED);
        mMiddleLevelTireMap.put(WHEEL_LEFT_REAR, WHEEL_COLOR_RED);
        mMiddleLevelTireMap.put(WHEEL_RIGHT_REAR, WHEEL_COLOR_RED);

        mLowLevelTireMap.put(WHEEL_LEFT_FRONT, mWheelNomalColor);//正常
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
        lfSlipStatus = mCarSettingManager.getLFSlipStatus();
        rfSlipStatus = mCarSettingManager.getRFSlipStatus();
        lrSlipStatus = mCarSettingManager.getLRSlipStatus();
        rrSlipStatus = mCarSettingManager.getRRSlipStatus();
        updateWheelSlipStatus(WHEEL_LEFT_FRONT, lfSlipStatus);
        updateWheelSlipStatus(WHEEL_RIGHT_FRONT, rfSlipStatus);
        updateWheelSlipStatus(WHEEL_LEFT_REAR, lrSlipStatus);
        updateWheelSlipStatus(WHEEL_RIGHT_REAR, rrSlipStatus);
    }

    private void getTireTemp() {
        mLFTemp = mCarSettingManager.getTireTemperute(WHEEL_LEFT_FRONT);//温度四个区域都用左前
        mRFTemp = mCarSettingManager.getTireTemperute(WHEEL_RIGHT_FRONT);//温度四个区域都用左前
        mLRTemp = mCarSettingManager.getTireTemperute(WHEEL_LEFT_REAR);//温度四个区域都用左前
        mRRTemp = mCarSettingManager.getTireTemperute(WHEEL_RIGHT_REAR);//温度四个区域都用左前
        updateTire_Temperature_Value(WHEEL_LEFT_FRONT, mLFTemp);
        updateTire_Temperature_Value(WHEEL_RIGHT_FRONT, mRFTemp);
        updateTire_Temperature_Value(WHEEL_LEFT_REAR, mLRTemp);
        updateTire_Temperature_Value(WHEEL_RIGHT_REAR, mRRTemp);
    }

    private void getTirePress() {
        lf_tirePressure = mCarSettingManager.getTirePressure(WHEEL_LEFT_FRONT);
        rf_tirePressure = mCarSettingManager.getTirePressure(WHEEL_RIGHT_FRONT);
        lr_tirePressure = mCarSettingManager.getTirePressure(WHEEL_LEFT_REAR);
        rr_tirePressure = mCarSettingManager.getTirePressure(WHEEL_RIGHT_REAR);
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
        int tireLoss = CarSettingManager.getInstance().getTireLoss();
        if (tireLoss == 0x04) {//信号丢失
            setAllTirePressureText(COLOR_WHITE1
                    , COLOR_WHITE);
            TireColorStrategyManager.getInstance().setTireNormalColor(TYPE_TIRE_PRESSURE, areaId);
        } else {
            if (CarSettingManager.getInstance().isTireSystemWarning() == 1) {//胎压系统故障
                setAllTirePressureText(WHEEL_COLOR_RED, WHEEL_COLOR_RED);
                TireColorStrategyManager.getInstance().setTireSpecialColor(TYPE_TIRE_PRESSURE, areaId);
            } else {

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
        Log.d(TAG, "1027 ___: ");
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

        Log.d(TAG, "updateTirePressureValue: " + "areaId = " + areaId + " intValue = " + value + " result = " + result);
        //利用BigDecimal来实现四舍五入.保留一位小数
        double finalValue = new BigDecimal(result).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        //1代表保留1位小数,保留两位小数就是2,依此累推
        //BigDecimal.ROUND_HALF_UP 代表使用四舍五入的方式
        String tirePressureTxt = String.valueOf(finalValue);
        if (tirePressureTxt.equals("0.0")) {
            tirePressureTxt = "0";
        }
        if (CarSettingManager.getInstance().isTirePressureWarning(areaId)) {
            Log.d(TAG, "1027 ___: ");
            tv.setTextColor(WHEEL_COLOR_RED);
            tvUnit.setTextColor(WHEEL_COLOR_RED);
            tv.setText(tirePressureTxt);
            TireColorStrategyManager.getInstance().setTireSpecialColor(TYPE_TIRE_PRESSURE, areaId);
        } else {
            Log.d(TAG, "1027 ___: ");
            tv.setTextColor(COLOR_WHITE);
            tvUnit.setTextColor(COLOR_WHITE1);
            tv.setText(tirePressureTxt);
            TireColorStrategyManager.getInstance().setTireNormalColor(TYPE_TIRE_PRESSURE, areaId);
        }
    }

    /**
     * 更新胎温值
     * TPMS_N_TireTemperature
     *
     * @param value
     */
    private void updateTire_Temperature_Value(int area, int value) {
        Log.d(TAG, "updateTire_Temperature_Value: area = " + area + " value = " + value);
        int tireLoss = CarSettingManager.getInstance().getTireLoss();


        if (tireLoss == 0x04) {//信号丢失
            setAllTireTempText(COLOR_WHITE1
                    , COLOR_WHITE);
            TireColorStrategyManager.getInstance().setTireNormalColor(TYPE_TIRE_TEMP, area);
        } else {
            if (CarSettingManager.getInstance().isTireSystemWarning() == 1) {//胎压系统故障
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
        Log.d(TAG, "1027 ___: ");
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
        boolean isWarning = CarSettingManager.getInstance().isTireTempWarning(areaId);
        boolean tirePressureWarning = CarSettingManager.getInstance().isTirePressureWarning(areaId);
        if (isWarning || tirePressureWarning) {
            Log.d(TAG, "setTireTemp: red");
            Log.d(TAG, "1027 ___: ");
            tv.setTextColor(WHEEL_COLOR_RED);
            tvUnit.setTextColor(WHEEL_COLOR_RED);

            tv.setText(String.valueOf(temp));
            TireColorStrategyManager.getInstance().setTireSpecialColor(TYPE_TIRE_TEMP, areaId);
        } else {
            Log.d(TAG, "setTireTemp: normal");
            Log.d(TAG, "1027 ___: ");
            tv.setTextColor(COLOR_WHITE);
            tvUnit.setTextColor(COLOR_WHITE1);

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
                AAOP_HSkin.with(transferImg)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_2h)
                        .applySkin(false);
//                iv_transfer_case_state.setBackgroundResource(R.drawable.icon_2h);
                break;
            case 1:
                AAOP_HSkin.with(transferImg)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_4h)
                        .applySkin(false);
//                iv_transfer_case_state.setBackgroundResource(R.drawable.icon_4h);
                break;
            case 2:
                AAOP_HSkin.with(transferImg)
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
        if (mLastFontDifferentialLockValue == value) {
            return;
        }

        mLastFontDifferentialLockValue = value;
        //消失
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(fLockImg, "scaleX", 1f, 1.5f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(fLockImg, "scaleY", 1f, 1.5f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(fLockImg, "alpha", 1f, 0f);
        ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(fLockImg, "scaleX", 1.5f, 0.55f);
        ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(fLockImg, "scaleY", 1.5f, 0.55f);
        objectAnimator2.setDuration(100);
        objectAnimator2.setInterpolator(new DecelerateInterpolator());
        objectAnimator3.setDuration(100);
        objectAnimator3.setInterpolator(new DecelerateInterpolator());
        objectAnimator4.setDuration(333);
        objectAnimator5.setDuration(233);
        objectAnimator5.setInterpolator(new AccelerateInterpolator());
        objectAnimator6.setDuration(233);
        objectAnimator6.setInterpolator(new AccelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4, objectAnimator5, objectAnimator6);
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
                        fLockImg.setBackgroundResource(R.drawable.icon_close_lock);
                        break;
                    case UNLOCKED:
                        fLockImg.setBackgroundResource(R.drawable.icon_open_lock);
                        break;
                }
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(fLockImg, "scaleX", 0.5f, 1.05f);
                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(fLockImg, "scaleY", 0.5f, 1.05f);
                ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(fLockImg, "alpha", 0f, 1f);
                ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(fLockImg, "scaleX", 0f, 1f);
                ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(fLockImg, "scaleY", 0f, 1f);
                objectAnimator2.setDuration(233);
                objectAnimator2.setInterpolator(new AccelerateInterpolator());
                objectAnimator3.setDuration(233);
                objectAnimator3.setInterpolator(new AccelerateInterpolator());
                objectAnimator4.setDuration(333);
                objectAnimator5.setDuration(100);
                objectAnimator6.setInterpolator(new DecelerateInterpolator());
                objectAnimator6.setDuration(100);
                objectAnimator6.setInterpolator(new DecelerateInterpolator());
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4, objectAnimator5, objectAnimator6);
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
    private void updateFearDifferentialLock(final int value) {
        if (mLastFearDifferentialLockValue == value) {
            return;
        }

        mLastFearDifferentialLockValue = value;
        Log.d(TAG, "updateFearDifferentialLock: " + value);
        //消失
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(rLockImg, "scaleX", 1f, 1.5f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(rLockImg, "scaleY", 1f, 1.5f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(rLockImg, "alpha", 1f, 0f);
        ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(rLockImg, "scaleX", 1.5f, 0.55f);
        ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(rLockImg, "scaleY", 1.5f, 0.55f);
        objectAnimator2.setDuration(100);
        objectAnimator2.setInterpolator(new DecelerateInterpolator());
        objectAnimator3.setDuration(100);
        objectAnimator3.setInterpolator(new DecelerateInterpolator());
        objectAnimator4.setDuration(333);
        objectAnimator5.setDuration(233);
        objectAnimator5.setInterpolator(new AccelerateInterpolator());
        objectAnimator6.setDuration(233);
        objectAnimator6.setInterpolator(new AccelerateInterpolator());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4, objectAnimator5, objectAnimator6);
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
                        rLockImg.setBackgroundResource(R.drawable.icon_close_lock);
                        break;
                    case UNLOCKED:
                        rLockImg.setBackgroundResource(R.drawable.icon_open_lock);
                        break;
                }
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(rLockImg, "scaleX", 0.5f, 1.05f);
                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(rLockImg, "scaleY", 0.5f, 1.05f);
                ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(rLockImg, "alpha", 0f, 1f);
                ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(rLockImg, "scaleX", 0f, 1f);
                ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(rLockImg, "scaleY", 0f, 1f);
                objectAnimator2.setDuration(233);
                objectAnimator2.setInterpolator(new AccelerateInterpolator());
                objectAnimator3.setDuration(233);
                objectAnimator3.setInterpolator(new AccelerateInterpolator());
                objectAnimator4.setDuration(333);
                objectAnimator5.setDuration(100);
                objectAnimator6.setInterpolator(new DecelerateInterpolator());
                objectAnimator6.setDuration(100);
                objectAnimator6.setInterpolator(new DecelerateInterpolator());
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimator2, objectAnimator3, objectAnimator4, objectAnimator5, objectAnimator6);
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
     *
     * @param area
     * @param value
     */
    private void updateWheelSlipStatus(int area, int value) {

        Log.d(TAG, "updateWheelSlipStatus: area = " + area + " value = " + value);
        updateWheelSlipStatusImg(area, value);
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
        if (!isNormal || isFirstTime) {
            duration = 0;
            isFirstTime = false;
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


                lfWhellAnimDraw = new AnimationDrawable();
                lfWhellRes = getResources().getIdentifier("whell_" + currentAnimValue, "drawable", MyApplication.getContext().getPackageName());
                Drawable lfWhellDrawable = getResources().getDrawable(lfWhellRes);
                lfWhellDrawable.setColorFilter(iv_wheel_left_color, PorterDuff.Mode.SRC_ATOP);
                lfwheelImg.setImageDrawable(lfWhellAnimDraw);
                lfWhellAnimDraw.addFrame(lfWhellDrawable, 0);
                lfWhellAnimDraw.start();

                rfWhellDraw = new AnimationDrawable();
                rfWhellRes = getResources().getIdentifier("whell_right_" + currentAnimValue, "drawable", MyApplication.getContext().getPackageName());
                Drawable rfWhellDrawable = getResources().getDrawable(rfWhellRes);
                rfWhellDrawable.setColorFilter(iv_wheel_right_color, PorterDuff.Mode.SRC_ATOP);
                rfwheeltImg.setImageDrawable(rfWhellDraw);
                rfWhellDraw.addFrame(rfWhellDrawable, 0);
                rfWhellDraw.start();

                axleAnimDraw = new AnimationDrawable();
                axleRes = getResources().getIdentifier("axle_" + currentAnimValue, "drawable", MyApplication.getContext().getPackageName());
                Drawable axleDrawable = getResources().getDrawable(axleRes);
                axleDrawable.setColorFilter(iv_axle_color, PorterDuff.Mode.SRC_ATOP);
                axleImg.setImageDrawable(axleAnimDraw);
                axleAnimDraw.addFrame(axleDrawable, 0);
                axleAnimDraw.start();

                pointerAnimDraw = new AnimationDrawable();
                if (CURRENTTHEME == 1) {

                    pointerRes = getResources().getIdentifier("pointer_1_" + (currentAnimValue), "drawable", MyApplication.getContext().getPackageName());
                } else {
                    pointerRes = getResources().getIdentifier("pointer_2_" + (currentAnimValue), "drawable", MyApplication.getContext().getPackageName());
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
        mIvLineUpperLeft.setVisibility(isVisible ? VISIBLE : View.GONE);
        mIvLineUpperRight.setVisibility(isVisible ? VISIBLE : View.GONE);
        mIvLineLowerLeft.setVisibility(isVisible ? VISIBLE : View.GONE);
        mIvLineLowerRight.setVisibility(isVisible ? VISIBLE : View.GONE);
        lfTirePressureText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit1.setVisibility(isVisible ? VISIBLE : View.GONE);
        lfTiretempText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit2.setVisibility(isVisible ? VISIBLE : View.GONE);
        rfTirepressureText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit3.setVisibility(isVisible ? VISIBLE : View.GONE);
        rfTiretempText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit4.setVisibility(isVisible ? VISIBLE : View.GONE);
        lrTirepressureText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit5.setVisibility(isVisible ? VISIBLE : View.GONE);
        lrTiretempText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit6.setVisibility(isVisible ? VISIBLE : View.GONE);
        rrTirepressureText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit7.setVisibility(isVisible ? VISIBLE : View.GONE);
        rrTiretempText.setVisibility(isVisible ? VISIBLE : View.GONE);
        mTvUnit8.setVisibility(isVisible ? VISIBLE : View.GONE);
    }


    @Override
    public void registerOnResumeListener(OnFragmentResumeListener listener) {
        this.listener = listener;
    }


    @Override
    public void callBack(int skin) {// 手动换肤更新指针颜色

        pointerAnimDraw = new AnimationDrawable();
        if (CURRENTTHEME == 1) {
            pointerRes = getResources().getIdentifier("pointer_1_" + (resIndex), "drawable", MyApplication.getContext().getPackageName());
        } else if (CURRENTTHEME == 2) {
            pointerRes = getResources().getIdentifier("pointer_2_" + (resIndex), "drawable", MyApplication.getContext().getPackageName());
        }
        Drawable pointer_id_drawable = getResources().getDrawable(pointerRes);
        pointerImg.setImageDrawable(pointerAnimDraw);
        pointerAnimDraw.addFrame(pointer_id_drawable, 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TireColorStrategyManager.getInstance().unRegisterColorChangeListener();
    }


}
