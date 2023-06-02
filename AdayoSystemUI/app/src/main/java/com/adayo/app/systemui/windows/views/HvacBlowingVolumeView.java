package com.adayo.app.systemui.windows.views;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.HvacSeekBar;
import com.adayo.app.systemui.bean.BCMInfo;
import com.adayo.app.systemui.configs.HvacContent;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.managers.business.CarControllerImpl;
import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_MAX_BLOWING_VOLUME;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_MAX_REAR_BLOWING_VOLUME;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_MIN_BLOWING_VOLUME;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_POWER_SWITCH_OFF;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/15 10:37
 */
public class HvacBlowingVolumeView extends LinearLayout implements SeekBar.OnSeekBarChangeListener, AAOP_HvacManager.AAOP_HvacBindCallback, View.OnClickListener {

    private ImageView subtract;
    private ImageView plus;
    private HvacSeekBar hvacSeekBar;

    protected int areaID;
    protected boolean isOnlyShow;

    public HvacBlowingVolumeView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public HvacBlowingVolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public HvacBlowingVolumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);

    }

    private void initView(Context context, @android.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.hvacIcon);
        int areaIndex = array.getInt(R.styleable.hvacIcon_area, 8);
        areaID = HVAC_AIR_IDS[areaIndex];
        isOnlyShow = array.getBoolean(R.styleable.hvacIcon_onlyShow, false);
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.blowing_volume_view, this, true);
        subtract = mRootView.findViewById(R.id.subtract_icon);
        plus = mRootView.findViewById(R.id.plus_icon);
        if(!isOnlyShow) {
            subtract.setOnClickListener(this);
            plus.setOnClickListener(this);
        }
        hvacSeekBar = mRootView.findViewById(R.id.hvac_seek_bar);
        hvacSeekBar.setMax(AREA_SEAT_HVAC_REAR == areaID ? HVAC_MAX_REAR_BLOWING_VOLUME : HVAC_MAX_BLOWING_VOLUME);
        hvacSeekBar.setOnSeekBarChangeListener(this);
        CarControllerImpl.getInstance().addCallback((BaseCallback<BCMInfo>) data -> {
            if(null == hvacSeekBar){
                return;
            }
            hvacSeekBar.setEnabled(data.getNewEngineStatus() != 0);
        });
        bandVehicleID();
    }

    private void setHvacStatus(int value){
        if(value == 0){
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(HvacContent.HVAC_POWER_ON, areaID, HVAC_POWER_SWITCH_OFF);
        }else {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(HvacContent.HVAC_FAN_SPEED, areaID, value);
        }
    }

    private void bandVehicleID(){
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(HvacContent.HVAC_FAN_SPEED,this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if(fromUser){
//            setHvacStatus(progress);
//        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setHvacStatus(seekBar.getProgress());
    }

    private int currentValue = -1;
    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        int value = (int)carPropertyValue.getValue();
        int area = carPropertyValue.getAreaId();
        int cartPropertyId = carPropertyValue.getPropertyId();
        LogUtil.d(SystemUIContent.TAG, "cartPropertyId = " + cartPropertyId +
                " ; value = " + value + " ; areaID = " + carPropertyValue.getAreaId() +  " ; area = " + area);
        if(HvacContent.HVAC_FAN_SPEED == cartPropertyId && areaID == area && HVAC_MIN_BLOWING_VOLUME <= value && hvacSeekBar.getMax() >= value && currentValue != value){
            currentValue = value;
            hvacSeekBar.setProgress(value);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.subtract_icon:
                if(hvacSeekBar.getProgress() > HVAC_MIN_BLOWING_VOLUME) {
                    setHvacStatus(hvacSeekBar.getProgress() - 1);
                }
                break;
            case R.id.plus_icon:
                if(hvacSeekBar.getProgress() < HVAC_MAX_BLOWING_VOLUME) {
                    setHvacStatus(hvacSeekBar.getProgress() + 1);
                }
                break;
                default:
                    break;
        }
    }
}
