package com.adayo.app.systemui.windows.views;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.NumberPicker;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_TEMPERATURE_HI;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_TEMPERATURE_LO;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_TEMPERATURE_STEP;
import static com.adayo.app.systemui.configs.HvacContent.TEMPERATURES;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/16 14:10
 */
public class HvacTemperatureView extends LinearLayout implements View.OnClickListener, AAOP_HvacManager.AAOP_HvacBindCallback, NumberPicker.OnValueChangeListener, AAOP_HvacManager.AAOP_HvacServiceConnectCallback {

    private NumberPicker numberPicker;
    private ImageView incrementButton;
    private ImageView decrementButton;

    protected int sendVehicleID;
    protected int receiveVehicleID;
    protected int areaID;

    public HvacTemperatureView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public HvacTemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public HvacTemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @android.annotation.Nullable AttributeSet attrs, int defStyleAttr){
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
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.hvac_temperature_picker_view, this, true);
        incrementButton = mRootView.findViewById(R.id.increment_button);
        incrementButton.setOnClickListener(this);
        decrementButton = mRootView.findViewById(R.id.decrement_button);
        decrementButton.setOnClickListener(this);
        numberPicker = mRootView.findViewById(R.id.number_picker);
        numberPicker.setDisplayedValues(TEMPERATURES);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(TEMPERATURES.length - 1);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(TEMPERATURES.length - 1);
        numberPicker.setOnValueChangedListener(this);
        bandVehicleID();
    }

    private void setHvacStatus(float value){
        LogUtil.debugD(SystemUIContent.TAG, value + "");
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setFloatProperty(sendVehicleID, areaID, value);
    }

    private void bandVehicleID(){
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).addHvacServiceConnectCallback(this);
    }

    private void setHvacStatus(float value, float step){
        float sendValue = value + step;
        if(sendValue <= HVAC_TEMPERATURE_HI && sendValue >= HVAC_TEMPERATURE_LO){
            setHvacStatus(sendValue);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.increment_button:
                setHvacStatus(HVAC_TEMPERATURE_HI - numberPicker.getValue()/2.0f, HVAC_TEMPERATURE_STEP);
                break;
            case R.id.decrement_button:
                setHvacStatus(HVAC_TEMPERATURE_HI - numberPicker.getValue()/2.0f, -HVAC_TEMPERATURE_STEP);
                break;
            default:
                break;
        }
    }

    private void setView(float value){
        LogUtil.debugD(SystemUIContent.TAG, "value = " + value + "; cartPropertyId = " + receiveVehicleID + "; receiveAreaID = " + areaID);
        if( HVAC_TEMPERATURE_HI >= value && HVAC_TEMPERATURE_LO <= value) {
            numberPicker.setValue((int) ((HVAC_TEMPERATURE_HI - value) * 2));
        }
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if(null == carPropertyValue){
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        int receiveAreaID = carPropertyValue.getAreaId();
        float value = (float) carPropertyValue.getValue();
        LogUtil.debugD(SystemUIContent.TAG, "value = " + value + "; cartPropertyId = " + cartPropertyId + "; receiveAreaID = " + receiveAreaID);
        if(cartPropertyId == receiveVehicleID && receiveAreaID == areaID){
            setView(value);
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        float tmp;
        if (TEMPERATURES[newVal].equals("LO")) {
            tmp = HVAC_TEMPERATURE_LO;
        } else if (TEMPERATURES[newVal].equals("HI")) {
            tmp = HVAC_TEMPERATURE_HI;
        } else {
            tmp = Float.parseFloat(TEMPERATURES[newVal]);
        }
        setHvacStatus(tmp);
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        if(null != aaop_serviceConnectedInfo && aaop_serviceConnectedInfo.isHasConnected()) {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(receiveVehicleID,this);
            setView(AAOP_HvacManager.getInstance(getContext()).getFloatProperty(receiveVehicleID, areaID));
        }
    }
}
