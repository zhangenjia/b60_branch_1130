package com.adayo.app.systemui.windows.views;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.BaseHvacSwitch;
import com.adayo.app.systemui.configs.HvacContent;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_DRIVER;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_ALL;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_REAR;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_PASSENGER;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_REAR_LEFT;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_REAR_RIGHT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_TEMPERATURE_HI;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_TEMPERATURE_LO;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/10 20:20
 */
public class HvacTempView extends BaseHvacSwitch {
    public HvacTempView(Context context) {
        super(context);
        setMyTypeFace();
    }

    public HvacTempView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyTypeFace();
    }

    public HvacTempView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMyTypeFace();
    }

    private void setMyTypeFace(){
        Typeface mFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/font_beijingauto_b60v_normal_v1.1.3.otf");
        setTypeface(mFace);
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if(null == carPropertyValue){
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        if(cartPropertyId == receiveVehicleID && carPropertyValue.getAreaId() == areaID){
            if(needSetValue){
                temperature = (float) carPropertyValue.getValue();
                setViewValue();
            }
        }
        if(cartPropertyId == HvacContent.HVAC_POWER_ON && carPropertyValue.getAreaId() == getPowerAreaID(areaID)){
            if(needSetValue){
                powerStatus = (int) carPropertyValue.getValue();
                setViewValue();
            }
        }
    }
    private float temperature;
    private int powerStatus;
    private void setViewValue(){
        LogUtil.debugD(SystemUIContent.TAG, "temperature = " + temperature + " ; powerStatus = " + powerStatus);
        int powerOnStatus = HvacContent.HVAC_POWER_SWITCH_ON;
        if(AREA_SEAT_HVAC_REAR == getPowerAreaID(areaID)){
            powerOnStatus = HvacContent.HVAC_POWER_SWITCH_ON - 1;
        }
        if(powerOnStatus == powerStatus){
            if(HVAC_TEMPERATURE_LO > temperature && HVAC_TEMPERATURE_HI < temperature){
                return;
            }
            setText(temperature + "");
        }else {
            setText("--");
        }
    }

    private int getPowerAreaID(int areaID){
        switch (areaID){
            case AREA_SEAT_DRIVER:
            case AREA_SEAT_PASSENGER:
            case AREA_SEAT_HVAC_FRONT:
                return AREA_SEAT_HVAC_ALL;
            case AREA_SEAT_HVAC_REAR:
            case AREA_SEAT_REAR_LEFT:
            case AREA_SEAT_REAR_RIGHT:
                return  AREA_SEAT_HVAC_REAR;
        }
        return AREA_SEAT_HVAC_ALL;
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        if(null != aaop_serviceConnectedInfo && aaop_serviceConnectedInfo.isHasConnected()) {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(receiveVehicleID, this);
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(HvacContent.HVAC_POWER_ON, this);
            temperature = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getFloatProperty(receiveVehicleID, areaID);
            powerStatus = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HvacContent.HVAC_POWER_ON, getPowerAreaID(areaID));
            setViewValue();
        }
    }
}
