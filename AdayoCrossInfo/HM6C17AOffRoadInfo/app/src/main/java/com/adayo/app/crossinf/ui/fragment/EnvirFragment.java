package com.adayo.app.crossinf.ui.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.car.VehiclePropertyIds;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.crossinf.MainActivity;
import com.adayo.app.crossinf.MyApplication;
import com.adayo.app.crossinf.R;
import com.adayo.app.crossinf.model.VehicleDataInFo;
import com.adayo.app.crossinf.presenter.CarSettingManager;
import com.adayo.app.crossinf.ui.customview.HorizontalArcView;
import com.adayo.app.crossinf.ui.customview.PitchArcView;
import com.adayo.app.crossinf.util.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.adayo.app.crossinf.util.Constant.Versiondate;
import static com.adayo.proxy.media.mediascanner.utils.ContextUtil.getAppContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnvirFragment extends BaseFragment {

    private static final String TAG = Versiondate + EnvirFragment.class.getSimpleName();
    private RelativeLayout iv_compass_pointer;
    private ImageView iv_horizontalangle;
    private ImageView iv_pitchangle;
    private HorizontalArcView cv_horizontal;
    private PitchArcView cv_pichangle;
    private TextView tv_horizontalangle;
    private TextView tv_altitude;
    private TextView tv_pitchangle;
    private TextView tv_atmosphericpressure;
    private TextView tv_compassangle;
    private int lastHorizontalangle = -61;//上一次水平角
    private int lastPitchAngle = -61;//上一次俯仰角
    private TextView tv_compassdirection;
    private float mLastCompassAngleValue = -1;
    private Map<Integer, Integer> mCompassDirectionMap = new HashMap<Integer, Integer>() {
        {
            put(7, R.string.northwest);
            put(6, R.string.west);
            put(5, R.string.southwest);
            put(4, R.string.south);
            put(3, R.string.southeast);
            put(2, R.string.east);
            put(1, R.string.northeast);
            put(0, R.string.north);
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //todo 更新数据
            if (!isInflateView) {
                return;
            }
            VehicleDataInFo mVehicleDataInFo = (VehicleDataInFo) msg.obj;
            Bundle bundle = mVehicleDataInFo.getBundle();
            int properId = mVehicleDataInFo.getId();
            switch (properId) {
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
                    updateAtmosphericPressureValue(mAtmosphericPressureValue);
                    break;
                case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_COMPASS_DIRECTION://指南针方向及有效值信息
                    mCompassDirectionValue = bundle.getInt("IntValue");
                    updateCompassDirectionValue(mCompassDirectionValue);
                    break;
                case VehiclePropertyIds.IVI_MCU_MEDIA_NAVI_COMPASS_SIG://指南针角度及有效值信息
                    float mCompassAngleValue = bundle.getFloat("FloatValue");
                    updateCompassAngleValue(mCompassAngleValue);
                    break;
                default:
                    Log.d(TAG, "Invalid parameter!");
                    break;
            }
        }
    };
    private ImageView iv_pitchangle_line;
    private ImageView iv_horizontalangle_line;
    private CarSettingManager carSettingManager;
    private ImageView icon_air_pressure;
    private ImageView icon_altitude;
    private int mCompassDirectionValue;
    private ImageView compass_pointer;
    private ValueAnimator valueAnimator_compass_pointer;
    private ValueAnimator valueAnimator_horizontalangle;
    private ValueAnimator valueAnimator_pitchangle;
    private float mCurrentPitchAngle = 0f;
    private float mCurrentHorizontalangle = 0f;
    private float mCurrentCompassAngleValue = 0f;
    private OnFragmentResumeListener listener;
    private boolean isInflateView;
    public static final String SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT = "km_and_oil_wear_unit";//单位 里程/油耗/高度
    private int currentUnit;

    public EnvirFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity) context;
        activity.setHandler(handler);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LogUtil.d(" onCreate EnvirFragment ====== start");
        View view = inflater.inflate(R.layout.fragment_environmental_information, container, false);
        iv_horizontalangle = (ImageView) view.findViewById(R.id.iv_horizontalangle);
        iv_pitchangle = (ImageView) view.findViewById(R.id.iv_pitchangle);
        iv_compass_pointer = (RelativeLayout) view.findViewById(R.id.iv_compass_pointer);
        tv_horizontalangle = (TextView) view.findViewById(R.id.tv_horizontalangle);
        tv_altitude = (TextView) view.findViewById(R.id.tv_altitude);
        tv_pitchangle = (TextView) view.findViewById(R.id.tv_pitchangle);
        tv_atmosphericpressure = (TextView) view.findViewById(R.id.tv_atmosphericpressure);
        tv_compassangle = (TextView) view.findViewById(R.id.tv_compassangle);
        cv_horizontal = (HorizontalArcView) view.findViewById(R.id.cv_horizontal);
        cv_pichangle = (PitchArcView) view.findViewById(R.id.cv_pichangle);
        AAOP_HSkin.with(cv_horizontal).addViewAttrs(new DynamicAttr("pichangle")).applySkin(false);
        AAOP_HSkin.with(cv_pichangle).addViewAttrs(new DynamicAttr("pichangle")).applySkin(false);
        tv_compassdirection = (TextView) view.findViewById(R.id.tv_compassdirection);
        iv_horizontalangle_line = (ImageView) view.findViewById(R.id.iv_horizontalangle_line);
        iv_pitchangle_line = (ImageView) view.findViewById(R.id.iv_pitchangle_line);
        icon_air_pressure = (ImageView) view.findViewById(R.id.icon_air_pressure);
        icon_altitude = (ImageView) view.findViewById(R.id.icon_altitude);
        compass_pointer = (ImageView) view.findViewById(R.id.compass_pointer);
        isInflateView = true;
        initSkinChange(view);
        requestKmAndOilWearUnit();
        initInfo();

        LogUtil.d(" onCreate EnvirFragment ====== end");
        return view;

    }

    /**
     * 更新水平角数值
     */
    private void updateHorizontalAngleValue(int value) {
        Log.d(TAG, "updateHorizontalAngleValue: " + value);


        if (value >= -60 && value <= 60) {
            if (lastHorizontalangle == value) {
                return;
            }
            if (0 == value) {
                cv_horizontal.setPaint2Color(0);
            } else {
                cv_horizontal.setPaint2Color(1);
            }
            tv_horizontalangle.setText("updateHorizontalAngleValue" + Math.round(value) + "°");
            iv_horizontalangle.setPivotX(232);//设置指定旋转中心点X坐标
            iv_horizontalangle.setPivotY(182);//设置指定旋转中心点Y坐标
            if (valueAnimator_horizontalangle != null) {
                valueAnimator_horizontalangle.cancel();
            }
            valueAnimator_horizontalangle = ValueAnimator.ofFloat(mCurrentHorizontalangle, value);
            valueAnimator_horizontalangle.setDuration(Math.abs(Math.round(value - lastHorizontalangle)) * 30);
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
            if (valueAnimator_horizontalangle != null) {
                valueAnimator_horizontalangle.cancel();
            }
            tv_horizontalangle.setText("-°");
            iv_horizontalangle.setRotation(0);
            cv_horizontal.setRotationAngle(0);
            lastHorizontalangle = 0;
            cv_horizontal.setPaint2Color(0);
        }
    }

    /**
     * 更新俯仰角数值
     */
    private void updatePitchAngleValue(int value) {
        Log.d(TAG, "updatePitchAngleValue111 =: " + value);

        if (value >= -40 && value <= 40) {
            if (lastPitchAngle == value) {
                return;
            }
            if (0 == value) {
                cv_pichangle.setPaint2Color(0);
            } else {
                cv_pichangle.setPaint2Color(1);
            }
            tv_pitchangle.setText("updatePitchAngleValue" + Math.round(value) + "°");//文字
            iv_pitchangle.setPivotX(282);//设置指定旋转中心点X坐标
            iv_pitchangle.setPivotY(182);//设置指定旋转中心点Y坐标
            if (valueAnimator_pitchangle != null) {
                valueAnimator_pitchangle.cancel();
            }
            valueAnimator_pitchangle = ValueAnimator.ofFloat(mCurrentPitchAngle, value);
            Log.d(TAG, "updatePitchAngleValue_: mCurrentPitchAngle = " + mCurrentPitchAngle + " value = " + value);
            valueAnimator_pitchangle.setDuration(Math.abs(Math.round(value - mCurrentPitchAngle)) * 30);
            lastPitchAngle = value;
            mCurrentPitchAngle = value;
            valueAnimator_pitchangle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue_pitchangle = (float) animation.getAnimatedValue();
//                    Log.d(TAG, "updatePitchAngleValue_: animatedValue_pitchangle = " + animatedValue_pitchangle);
                    tv_pitchangle.setText("" + Math.round(animatedValue_pitchangle) + "°");
                    iv_pitchangle.setRotation(animatedValue_pitchangle);
                    cv_pichangle.setRotationAngle(animatedValue_pitchangle);
                    mCurrentPitchAngle = animatedValue_pitchangle;
                }
            });
            valueAnimator_pitchangle.start();
        } else {
            if (valueAnimator_pitchangle != null) {
                valueAnimator_pitchangle.cancel();
            }
            tv_pitchangle.setText("-°");
            iv_pitchangle.setRotation(0);
            cv_pichangle.setRotationAngle(0);
            lastPitchAngle = 0;
            cv_pichangle.setPaint2Color(0);
        }
    }

    /**
     * 更新海拔数值
     */
    private void updateAltitudeValue(float value) {
        Log.d(TAG, "updateAltitudeValue: ====" + value);
        value = value + 500f;
        BigDecimal bd = new BigDecimal(value);
        value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
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
    }

    /**
     * 更新大气压数值
     */
    private void updateAtmosphericPressureValue(float value) {
        Log.d(TAG, "updateAtmosphericPressureValue: " + value);
        value = value - 300f;
        BigDecimal bd = new BigDecimal(value);
        value = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        if (value >= 300f && value <= 1100f) { //range

            if (currentUnit == 0 || currentUnit == -1) {
                tv_atmosphericpressure.setText("" + value + " hPa");
            } else {

                //利用BigDecimal来实现四舍五入.保留一位小数
                double result = new BigDecimal(value * 0.0145).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                //1代表保留1位小数,保留两位小数就是2,依此累推
                //BigDecimal.ROUND_HALF_UP 代表使用四舍五入的方式
                System.out.println(result);//输出3.0

                tv_atmosphericpressure.setText("" + result + " psi");
            }
        } else {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_atmosphericpressure.setText("---.- hPa");//invalid
            } else {
                tv_atmosphericpressure.setText("---.- psi");//invalid
            }
        }


    }

    /**
     * 更新指南针方向
     */
    private void updateCompassDirectionValue(int mapKey) {
        Log.d(TAG, "updateCompassDirectionValue: " + mapKey);
        if (mCompassDirectionMap.containsKey(mapKey)) {
            int value = mCompassDirectionMap.get(mapKey);
            Context context = getContext();
            if (context != null) {
                tv_compassdirection.setText("" + context.getResources().getString(value));
            }

        } else {
            int value = mCompassDirectionMap.get(0);
            Context context = getContext();
            if (context != null) {
                tv_compassdirection.setText("" + context.getResources().getString(value));//invalid
            }

        }

    }

    /**
     * 更新指南针角度
     */
    private synchronized void updateCompassAngleValue(float value) {

        if (value < 0f || value > 360f) {
            tv_compassangle.setText(showInvalidValue());//文字
            iv_compass_pointer.setRotation(0f);
            mLastCompassAngleValue = 0;
        } else {
            if (mLastCompassAngleValue == value) {
                return;
            }
            tv_compassangle.setText(Math.round(value) + "°");//
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
                    iv_compass_pointer.setRotation(animatedValue_pitchangle);
                    mCurrentCompassAngleValue = animatedValue_pitchangle;
                    int compassAngle = Math.round(mCurrentCompassAngleValue);
                    if (compassAngle < 0) {
                        compassAngle += 360;
                    }


                    tv_compassangle.setText(compassAngle + "°");//

                }
            });
            valueAnimator_compass_pointer.start();
        }
    }

    /**
     * 进入页面主动getInfo
     */
    private void initInfo() {

        carSettingManager = CarSettingManager.getInstance();
        initVehicleHorizontalAngle();//初始化水平角
        initVehiclePitchAngle();//初始化俯仰角
        initVehicleAltitudeHeight();//初始化相对高度
        initVehicleAtmoSphericPressure();//初始化大气压
        initVehicleCompassDirection();//初始化指南针方向
        initVehicleCompassSig();//初始化指南针角度

        iv_horizontalangle.setPivotX(232);//设置指定旋转中心点X坐标
        iv_horizontalangle.setPivotY(182);//设置指定旋转中心点Y坐标
        iv_pitchangle.setPivotX(282);//设置指定旋转中心点X坐标
        iv_pitchangle.setPivotY(182);//设置指定旋转中心点Y坐标
//      iv_compass_pointer.setRotation(270);


    }

    /**
     * 初始化水平角
     */
    private void initVehicleHorizontalAngle() {
        int naviAroundTitlingAngle = carSettingManager.getNaviAroundTitlingAngle();//获取水平角
        Log.d(TAG, "initVehicleHorizontalAngle: " + naviAroundTitlingAngle);
        naviAroundTitlingAngle = naviAroundTitlingAngle + 120;



        if (naviAroundTitlingAngle == -999 || naviAroundTitlingAngle < -60 || naviAroundTitlingAngle > 60) {//无效
            tv_horizontalangle.setText("" + "-" + "°");//水平角 文字
            iv_horizontalangle.setRotation(0);//水平角
            cv_horizontal.setRotationAngle(0);//绿色弧线
            cv_horizontal.setPaint2Color(0);

        } else {
            tv_horizontalangle.setText("" + Math.round(naviAroundTitlingAngle) + "°");//水平角 文字
            iv_horizontalangle.setRotation(naviAroundTitlingAngle);//水平角
            cv_horizontal.setRotationAngle(naviAroundTitlingAngle);//绿色弧线
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

        int naviPitchAngle = carSettingManager.getNaviPitchAngle();//获得俯仰角
        naviPitchAngle = naviPitchAngle + 80;
        Log.d(TAG, "initVehiclePitchAngle: " + naviPitchAngle);

        if (naviPitchAngle == -999 || naviPitchAngle < -40 || naviPitchAngle > 40) {//无效值
            tv_pitchangle.setText("" + "-" + "°");
            iv_pitchangle.setRotation(0);
            cv_pichangle.setRotationAngle(0);
            cv_pichangle.setPaint2Color(0);
        } else {
            tv_pitchangle.setText("" + Math.round(naviPitchAngle) + "°");
            iv_pitchangle.setRotation(naviPitchAngle);
            cv_pichangle.setRotationAngle(naviPitchAngle);

            if (0 == naviPitchAngle) {
                cv_pichangle.setPaint2Color(0);
            } else {
                cv_pichangle.setPaint2Color(1);
            }
        }
    }

    /**
     * 单位转换
     *
     * @param
     */
    public void requestKmAndOilWearUnit() {

        currentUnit = Settings.Global.getInt(getAppContext().getContentResolver(),
                SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT, -1);
        Log.d(TAG, "requestKmAndOilWearUnit: " + currentUnit);
        MyApplication.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor(SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT),
                true, mObserver);
    }

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {

            currentUnit = Settings.Global.getInt(getAppContext().getContentResolver(),
                    SETTING_GLOBAL_KEY_KM_AND_OIL_WEAR_UNIT, -1);
            Log.d(TAG, "onChange: ==> currentUnit " + currentUnit);
            initVehicleAltitudeHeight();
            initVehicleAtmoSphericPressure();
        }
    };

    /**
     * 获取相对高度
     */
    private void initVehicleAltitudeHeight() {// todo  公制米(公制)   英制ft  1m = 3.28f      -1640ft  ~29520ft


        float naviAltitudeHeight = carSettingManager.getNaviAltitudeHeight();
        naviAltitudeHeight = naviAltitudeHeight + 2000f;
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
    private void initVehicleAtmoSphericPressure() {//todo 1hpa(公制) = 0.0145psi  4.4psi ~16.0psi


        float naviAtmoSphericPressure = carSettingManager.getNaviAtmoSphericPressure();
        naviAtmoSphericPressure = naviAtmoSphericPressure + 200f;
        BigDecimal bd = new BigDecimal(naviAtmoSphericPressure);
        naviAtmoSphericPressure = bd.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

        Log.d(TAG, "initVehicleAtmoSphericPressure: =" + naviAtmoSphericPressure);
        if (naviAtmoSphericPressure >= 300f && naviAtmoSphericPressure <= 1100f) { //range
            if (currentUnit == 0 || currentUnit == -1) {
                tv_atmosphericpressure.setText("" + naviAtmoSphericPressure + " hPa");
            } else {
                double result = new BigDecimal(naviAtmoSphericPressure * 0.0145).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

                tv_atmosphericpressure.setText("" + result + " psi");
            }

        } else {
            if (currentUnit == 0 || currentUnit == -1) {
                tv_atmosphericpressure.setText("---.- hPa");//invalid
            } else {
                tv_atmosphericpressure.setText("---.- psi");//invalid
            }

        }
    }


    /**
     * 初始化指南针方向
     */
    private void initVehicleCompassDirection() {
        int naviCompassDirection = carSettingManager.getNaviCompassDirection();
        Log.d(TAG, "initVehicleCompassDirection: ");
        updateCompassDirectionValue(naviCompassDirection);
    }

    /**
     * 初始化指南针角度
     */
    private void initVehicleCompassSig() {
        float naviCompassSig = carSettingManager.getNaviCompassSig();
        Log.d(TAG, "initVehicleCompassSig: " + naviCompassSig);

        updateCompassAngleValue(naviCompassSig);
    }

    private void initSkinChange(View view) {

        AAOP_HSkin
                .with(icon_air_pressure)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_air_pressure)
                .applySkin(false);
        AAOP_HSkin
                .with(icon_altitude)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.icon_altitude)
                .applySkin(false);
        AAOP_HSkin
                .with(compass_pointer)
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

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateCompassDirectionValue(mCompassDirectionValue);
    }

    private String showInvalidValue() {

        return "--°";
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
}
