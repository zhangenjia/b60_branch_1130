package com.adayo.app.systemui.bases;

import android.annotation.CallSuper;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
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
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_IDS;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/17 9:55
 */
public class BaseRelativeLayout extends RelativeLayout implements AAOP_HvacManager.AAOP_HvacBindCallback, AAOP_HvacManager.AAOP_HvacServiceConnectCallback {

    protected int sendVehicleID;
    protected int receiveVehicleID;
    protected int areaID;

    public BaseRelativeLayout(Context context) {
        super(context);
    }
    public BaseRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @CallSuper
    protected void initView(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.hvacIcon);
        int sendVehicleIndex = array.getInt(R.styleable.hvacIcon_sendVehicleIndex, -1);
        int receiveVehicleIndex = array.getInt(R.styleable.hvacIcon_receiveVehicleIndex, -1);
        int areaIndex = array.getInt(R.styleable.hvacIcon_area, 8);
        if(sendVehicleIndex >= 0 && sendVehicleIndex < HVAC_IDS.length) {
            sendVehicleID = HVAC_IDS[sendVehicleIndex];
        }
        if(receiveVehicleIndex >= 0 && receiveVehicleIndex < HVAC_IDS.length) {
            receiveVehicleID = HVAC_IDS[receiveVehicleIndex];
        }
        areaID = HVAC_AIR_IDS[areaIndex];
        bandVehicleID();
    }

    protected void setHvacStatus(int value){
        LogUtil.debugD(SystemUIContent.TAG, value + "");
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(sendVehicleID, areaID, value);
    }

    protected void bandVehicleID(){
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).addHvacServiceConnectCallback(this);
    }

    private int statusValue = -1;
    private int powerValue = -1;
    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if(null == carPropertyValue){
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        int value = (int) carPropertyValue.getValue();
        if(cartPropertyId == receiveVehicleID && carPropertyValue.getAreaId() == areaID){
            statusValue = value;
            updateViews(statusValue, powerValue);
        }

        if(cartPropertyId == HvacContent.HVAC_POWER_ON && carPropertyValue.getAreaId() == getPowerAreaID(areaID)){
            powerValue = value;
            updateViews(statusValue, powerValue);
        }
    }

    protected void updateViews(int value, int powerMode){

    }

    protected int getPowerAreaID(int areaID){
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
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(receiveVehicleID,this);
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(HvacContent.HVAC_POWER_ON, this);
            updateViews(AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(receiveVehicleID, areaID),
                    AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HvacContent.HVAC_POWER_ON, getPowerAreaID(areaID)));
        }
    }
}
