package com.adayo.app.systemui.windows.views;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.BaseHvacSwitch;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.HvacContent.HVAC_CYCLE_AUTO;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_CYCLE_EXTERNAL;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_CYCLE_INTERNAL;
import static com.adayo.app.systemui.configs.HvacContent.SWITCH_ON_REFERENCE_VALUE;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/10 20:03
 */
public class HvacCycleModeView extends BaseHvacSwitch {
//    private int currentState = 1;

    public HvacCycleModeView(Context context) {
        super(context);
        setViewChange(HVAC_CYCLE_EXTERNAL);
    }

    public HvacCycleModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setViewChange(HVAC_CYCLE_EXTERNAL);
    }

    public HvacCycleModeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setViewChange(HVAC_CYCLE_EXTERNAL);
    }

    @Override
    protected void onViewClick(View v) {
        LogUtil.d(SystemUIContent.TAG, "sendVehicleID = " + sendVehicleID +
                "receiveVehicleID = " + receiveVehicleID + " ; areaID = " + areaID);
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(sendVehicleID, areaID, SWITCH_ON_REFERENCE_VALUE);

//        switch (currentState){
//            case HVAC_CYCLE_EXTERNAL:
//                AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(sendVehicleID, areaID, SWITCH_ON_REFERENCE_VALUE + sendOffset);
//                break;
//            case HVAC_CYCLE_INTERNAL:
//            case HVAC_CYCLE_AUTO:
//                AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(sendVehicleID, areaID, SWITCH_OFF_REFERENCE_VALUE + sendOffset);
//                break;
//            default:
//                break;
//        }
    }

    private void setViewChange(int value){
            switch (value) {
                case HVAC_CYCLE_EXTERNAL:
//                    currentState = value;
                    if(isOnlyShow) {
                        setBackgroundResource(R.drawable.com_icon_outside_loop);
                    }else{
                        setBackgroundResource(R.drawable.selector_hvac_circulation);
                    }
                    break;
                case HVAC_CYCLE_INTERNAL:
//                    currentState = value;
                    if(isOnlyShow) {
                        setBackgroundResource(R.drawable.com_icon_inside_loop);
                    }else{
                        setBackgroundResource(R.drawable.selector_hvac_inside);
                    }
                    break;
                case HVAC_CYCLE_AUTO:
//                    currentState = value;
                    if(isOnlyShow) {
                        setBackgroundResource(R.drawable.com_icon_automatic_loop);
                    }else{
                        setBackgroundResource(R.drawable.selector_hvac_automatic);
                    }
                    break;
                default:
                    break;
            }
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if (null == carPropertyValue) {
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        int value = (int) carPropertyValue.getValue();
        LogUtil.d(SystemUIContent.TAG, "cartPropertyId = " + cartPropertyId +
                " ; value = " + value + " ; areaID = " + carPropertyValue.getAreaId());
        if (cartPropertyId == receiveVehicleID && carPropertyValue.getAreaId() == areaID) {
            setViewChange(value);
        }
    }
}
