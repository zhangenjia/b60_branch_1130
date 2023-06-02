package com.adayo.app.systemui.windows.views;

import android.car.hardware.hvac.AAOP_HvacManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.BaseRelativeLayout;
import com.adayo.app.systemui.configs.HvacContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.airbnb.lottie.LottieAnimationView;

import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FACE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FACE_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FOOT_WIND;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_REAR_FACE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_REAR_FACE_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_REAR_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_WIND;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FOUR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT_STATUS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_THREE;
import static com.adayo.app.systemui.configs.HvacContent.SWITCH_ON_REFERENCE_VALUE;
import static com.adayo.proxy.infrastructure.share.constants.ShareDataConstantsDef.TAG;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/17 9:32
 */
public class HvacBlowingModeView extends BaseRelativeLayout implements View.OnClickListener {

    private ImageView face;
    private ImageView faceAndFoot;
    private ImageView foot;
    private ImageView windowAndFoot;
    private ImageView window;
    private LottieAnimationView animationView;

    public HvacBlowingModeView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public HvacBlowingModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public HvacBlowingModeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private int currentMode = -1;
    private void setFrameSurfaceViewBitmaps(int windMode) {
        if(currentMode == windMode){
            return;
        }
        currentMode = windMode;
        LogUtil.debugD(TAG, "windMode = " + windMode);
        switch (windMode) {
            case HVAC_FAN_DIRECTION_FACE:
                animationView.setAnimation(AREA_SEAT_HVAC_REAR == areaID ? "animation_rear_body.json" : "animation_body.json");
                break;
            case HVAC_FAN_DIRECTION_FOOT:
                animationView.setAnimation(AREA_SEAT_HVAC_REAR == areaID ? "animation_rear_feet_body.json" : "animation_feet.json");
                break;
            case HVAC_FAN_DIRECTION_FACE_FOOT:
                animationView.setAnimation(AREA_SEAT_HVAC_REAR == areaID ? "animation_rear_feet.json" : "animation_feet_body.json");
                break;
            case HVAC_FAN_DIRECTION_FOOT_WIND:
                if(AREA_SEAT_HVAC_FRONT == areaID ){
                    animationView.setAnimation("animation_feet_window.json");
                }
                break;
            case HVAC_FAN_DIRECTION_WIND:
                if(AREA_SEAT_HVAC_FRONT == areaID ) {
                    animationView.setAnimation("animation_window.json");
                }
                break;
            default:
                break;
        }
        setAnimationStatus(currentPage);
    }

    private int currentPage;
    public void setAnimationStatus(int page){
        switch (page){
            case HVAC_FRONT:
                currentPage = page;
                if(AREA_SEAT_HVAC_REAR == areaID){
                    animationView.pauseAnimation();
                }
                if(AREA_SEAT_HVAC_FRONT == areaID && !animationView.isAnimating()){
                    animationView.playAnimation();
                }
                break;
            case HVAC_REAR:
                currentPage = page;
                if(AREA_SEAT_HVAC_REAR == areaID && !animationView.isAnimating()){
                    animationView.playAnimation();
                }
                if(AREA_SEAT_HVAC_FRONT == areaID){
                    animationView.pauseAnimation();
                }
                break;
            case HVAC_THREE:
            case HVAC_FOUR:
                currentPage = page;
                animationView.pauseAnimation();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        super.initView(context, attrs, defStyleAttr);
        switch (areaID){
            case AREA_SEAT_HVAC_FRONT:
                LayoutInflater.from(getContext()).inflate(R.layout.hvac_front_blowing_mode_view, this, true);
                windowAndFoot = findViewById(R.id.foot_wind_mode);
                windowAndFoot.setOnClickListener(this);
                window = findViewById(R.id.wind_mode);
                window.setOnClickListener(this);
                break;
            case AREA_SEAT_HVAC_REAR:
                LayoutInflater.from(getContext()).inflate(R.layout.hvac_rear_blowing_mode_view, this, true);
                break;
            default:
                break;
        }
        animationView = findViewById(R.id.animation_view);
        animationView.loop(true);
        face = findViewById(R.id.face_mode);
        face.setOnClickListener(this);
        faceAndFoot = findViewById(R.id.face_foot_mode);
        faceAndFoot.setOnClickListener(this);
        foot = findViewById(R.id.foot_mode);
        foot.setOnClickListener(this);
        bandVehicleID();
    }

    @Override
    protected void updateViews(int mode, int powerStatus){
        if(null != face){
            face.setSelected((AREA_SEAT_HVAC_REAR == areaID ? HVAC_FAN_DIRECTION_REAR_FACE : HVAC_FAN_DIRECTION_FACE) == mode ? true : false);
        }
        if(null != faceAndFoot){
            faceAndFoot.setSelected((AREA_SEAT_HVAC_REAR == areaID ? HVAC_FAN_DIRECTION_REAR_FACE_FOOT : HVAC_FAN_DIRECTION_FACE_FOOT) == mode ? true : false);
        }
        if(null != foot){
            foot.setSelected((AREA_SEAT_HVAC_REAR == areaID ? HVAC_FAN_DIRECTION_REAR_FOOT : HVAC_FAN_DIRECTION_FOOT) == mode ? true : false);
        }
        if(null != windowAndFoot){
            windowAndFoot.setSelected(HVAC_FAN_DIRECTION_FOOT_WIND == mode ? true : false);
        }
        if(null != window){
            int frontDefrosting = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_FRONT_STATUS, 0);
            window.setSelected(HVAC_FAN_DIRECTION_WIND == mode && SWITCH_ON_REFERENCE_VALUE != frontDefrosting ? true : false);
        }
        if(null != animationView){
            setFrameSurfaceViewBitmaps(mode);
            int powerOnStatus = HvacContent.HVAC_POWER_SWITCH_ON;
            if (AREA_SEAT_HVAC_REAR == getPowerAreaID(areaID)) {
                powerOnStatus = HvacContent.HVAC_POWER_SWITCH_ON - 1;
            }
            animationView.setVisibility(powerOnStatus == powerStatus ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.face_mode:
                setHvacStatus(HVAC_FAN_DIRECTION_FACE);
                break;
            case R.id.face_foot_mode:
                setHvacStatus(HVAC_FAN_DIRECTION_FACE_FOOT);
                break;
            case R.id.foot_mode:
                setHvacStatus(HVAC_FAN_DIRECTION_FOOT);
                break;
            case R.id.foot_wind_mode:
                setHvacStatus(HVAC_FAN_DIRECTION_FOOT_WIND);
                break;
            case R.id.wind_mode:
                setHvacStatus(HVAC_FAN_DIRECTION_WIND);
                break;
            default:
                break;
        }
    }
}
