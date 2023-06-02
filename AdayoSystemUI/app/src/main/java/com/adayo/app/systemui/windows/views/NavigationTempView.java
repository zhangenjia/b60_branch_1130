package com.adayo.app.systemui.windows.views;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import static com.adayo.app.systemui.configs.HvacContent.HVAC_TEMPERATURE_HI;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_TEMPERATURE_LO;

/**
 * @author XuYue
 * @description:
 * @date :2022/3/21 16:42
 */
public class NavigationTempView extends LinearLayout implements AAOP_HvacManager.AAOP_HvacServiceConnectCallback, AAOP_HvacManager.AAOP_HvacBindCallback {

    protected int sendVehicleID;
    protected int receiveVehicleID;
    protected int areaID;

    private TextView temp;
    private View unit;

    private float temperature;
    private int powerStatus;

    public NavigationTempView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public NavigationTempView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public NavigationTempView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void setMyTypeFace(){
        Typeface mFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/font_beijingauto_b60v_normal_v1.1.3.otf");
        temp.setTypeface(mFace);
        setViewValue();
    }

    private void init(Context context, @android.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.hvacIcon);
        int sendVehicleIndex = array.getInt(R.styleable.hvacIcon_sendVehicleIndex, -1);
        int receiveVehicleIndex = array.getInt(R.styleable.hvacIcon_receiveVehicleIndex, -1);
        int areaIndex = array.getInt(R.styleable.hvacIcon_area, 8);
        if (sendVehicleIndex >= 0 && sendVehicleIndex < HVAC_IDS.length) {
            sendVehicleID = HVAC_IDS[sendVehicleIndex];
        }
        if (receiveVehicleIndex >= 0 && receiveVehicleIndex < HVAC_IDS.length) {
            receiveVehicleID = HVAC_IDS[receiveVehicleIndex];
        }
        areaID = HVAC_AIR_IDS[areaIndex];
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.navigation_temp_layout, this, true);
        temp = mRootView.findViewById(R.id.temperature);
        unit = mRootView.findViewById(R.id.unit);
        setMyTypeFace();
        bandVehicleID();
    }

    private void bandVehicleID() {
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).addHvacServiceConnectCallback(this);
    }

    private void setViewValue() {
        if(null == temp || null == unit){
            return;
        }
        LogUtil.debugD(SystemUIContent.TAG, "temperature = " + temperature + " ; powerStatus = " + powerStatus);
        int powerOnStatus = HvacContent.HVAC_POWER_SWITCH_ON;
        String tempString = temperature + "";
        if (AREA_SEAT_HVAC_REAR == getPowerAreaID(areaID)) {
            powerOnStatus = HvacContent.HVAC_POWER_SWITCH_ON - 1;
        }
        if (powerOnStatus == powerStatus) {
            if (HVAC_TEMPERATURE_LO > temperature && HVAC_TEMPERATURE_HI < temperature) {
                return;
            }
            if(temperature == HVAC_TEMPERATURE_LO){
                tempString = "LO";
                temp.setTextColor(getResources().getColor(R.color.hvac_lo));
                unit.setVisibility(View.GONE);
            }else if(temperature == HVAC_TEMPERATURE_HI){
                tempString = "HI";
                temp.setTextColor(getResources().getColor(R.color.hvac_hi));
                unit.setVisibility(View.GONE);
            }else {
                temp.setTextColor(getResources().getColor(R.color.white));
                unit.setVisibility(View.VISIBLE);
            }
        } else {
            tempString = "--";
            temp.setTextColor(getResources().getColor(R.color.white));
            unit.setVisibility(View.VISIBLE);
        }
        temp.setText(tempString);
    }

    private int getPowerAreaID(int areaID) {
        switch (areaID) {
            case AREA_SEAT_DRIVER:
            case AREA_SEAT_PASSENGER:
            case AREA_SEAT_HVAC_FRONT:
                return AREA_SEAT_HVAC_ALL;
            case AREA_SEAT_HVAC_REAR:
            case AREA_SEAT_REAR_LEFT:
            case AREA_SEAT_REAR_RIGHT:
                return AREA_SEAT_HVAC_REAR;
        }
        return AREA_SEAT_HVAC_ALL;
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        if (null != aaop_serviceConnectedInfo && aaop_serviceConnectedInfo.isHasConnected()) {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(receiveVehicleID, this);
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(HvacContent.HVAC_POWER_ON, this);
            temperature = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getFloatProperty(receiveVehicleID, areaID);
            powerStatus = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HvacContent.HVAC_POWER_ON, getPowerAreaID(areaID));
            setViewValue();
        }
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if (null == carPropertyValue) {
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        if (cartPropertyId == receiveVehicleID && carPropertyValue.getAreaId() == areaID) {
            temperature = (float) carPropertyValue.getValue();
            setViewValue();
        }
        if (cartPropertyId == HvacContent.HVAC_POWER_ON && carPropertyValue.getAreaId() == getPowerAreaID(areaID)) {
            powerStatus = (int) carPropertyValue.getValue();
            setViewValue();
        }
    }
}
