package com.adayo.app.systemui.windows.views;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.HvacContent.AIR_QUALITY_SWITCH_OFF;
import static com.adayo.app.systemui.configs.HvacContent.AIR_QUALITY_SWITCH_ON;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_ALL;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_QUALITY_EXCELLENT_MAX;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_QUALITY_GOOD_MAX;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_QUALITY_LIGHT_POLLUTION_MAX;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_QUALITY_MIN;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_QUALITY_MODERATE_POLLUTION_MAX;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_QUALITY_SERIOUS_POLLUTION_MAX;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_QUALITY_SEVERE_POLLUTION_MAX;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_INSIDE_PM25_VALUE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_PM25_DISPLAY;

/**
 * @author XuYue
 * @description:
 * @date :2021/12/15 20:11
 */
public class HvacAirQualityView extends RelativeLayout implements View.OnClickListener, AAOP_HvacManager.AAOP_HvacServiceConnectCallback, AAOP_HvacManager.AAOP_HvacBindCallback {
    private ImageView iconView;
    private LinearLayout openView;
    private TextView airQualityTitle;
    private TextView airQuality;

    private int airQualitySwitch;
    private int airQualityValue;

    private AAOP_HvacManager hvacManager;

    public HvacAirQualityView(Context context) {
        super(context);
        initView();
    }

    public HvacAirQualityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HvacAirQualityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void bandVehicleID() {
        hvacManager = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext());
        hvacManager.addHvacServiceConnectCallback(this);
    }

    protected void setHvacStatus(int value){
        if(null != hvacManager) {
            hvacManager.setIntProperty(HVAC_PM25_DISPLAY, AREA_SEAT_HVAC_ALL, value);
        }
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.hvac_air_quality_view, this, true);
        iconView = mRootView.findViewById(R.id.icon_view);
        openView = mRootView.findViewById(R.id.open_view);
        airQuality = mRootView.findViewById(R.id.air_quality);
        airQualityTitle = mRootView.findViewById(R.id.air_quality_title);
        setOnClickListener(this);
        bandVehicleID();
    }

    protected void updateViews(int airQualitySwitch, int airQualityValue) {
        if(airQualitySwitch > AIR_QUALITY_SWITCH_ON || airQualitySwitch < AIR_QUALITY_SWITCH_OFF){
            return;
        }
        this.airQualitySwitch = airQualitySwitch;
        openView.setVisibility(airQualitySwitch == AIR_QUALITY_SWITCH_ON ? View.VISIBLE : View.GONE);
        if(airQualitySwitch == AIR_QUALITY_SWITCH_OFF ){
            iconView.setImageResource(R.drawable.icon_pm_close);
        }else {
            if (airQualityValue >= HVAC_AIR_QUALITY_MIN && airQualityValue <= HVAC_AIR_QUALITY_SERIOUS_POLLUTION_MAX) {
                this.airQualityValue = airQualityValue;
                airQuality.setText(airQualityValue + "");
                if (airQualityValue <= HVAC_AIR_QUALITY_EXCELLENT_MAX) {
                    airQuality.setTextColor(getContext().getColor(R.color.air_quality_excellent));
                    airQualityTitle.setTextColor(getContext().getColor(R.color.air_quality_excellent));
                    iconView.setImageResource(R.drawable.icon_pm_open_excellent);
                } else if (airQualityValue <= HVAC_AIR_QUALITY_GOOD_MAX) {
                    airQuality.setTextColor(getContext().getColor(R.color.air_quality_good));
                    airQualityTitle.setTextColor(getContext().getColor(R.color.air_quality_good));
                    iconView.setImageResource(R.drawable.icon_pm_open_good);
                } else if (airQualityValue <= HVAC_AIR_QUALITY_LIGHT_POLLUTION_MAX) {
                    airQuality.setTextColor(getContext().getColor(R.color.air_quality_light_pollution));
                    airQualityTitle.setTextColor(getContext().getColor(R.color.air_quality_light_pollution));
                    iconView.setImageResource(R.drawable.icon_pm_open_mild);
                } else if (airQualityValue <= HVAC_AIR_QUALITY_MODERATE_POLLUTION_MAX) {
                    airQuality.setTextColor(getContext().getColor(R.color.air_quality_moderate_pollution));
                    airQualityTitle.setTextColor(getContext().getColor(R.color.air_quality_moderate_pollution));
                    iconView.setImageResource(R.drawable.icon_pm_open_medium);
                } else if (airQualityValue <= HVAC_AIR_QUALITY_SEVERE_POLLUTION_MAX) {
                    airQuality.setTextColor(getContext().getColor(R.color.air_quality_severe_pollution));
                    airQualityTitle.setTextColor(getContext().getColor(R.color.air_quality_severe_pollution));
                    iconView.setImageResource(R.drawable.icon_pm_open_severe);
                } else if (airQualityValue <= HVAC_AIR_QUALITY_SERIOUS_POLLUTION_MAX) {
                    airQuality.setTextColor(getContext().getColor(R.color.air_quality_serious_pollution));
                    airQualityTitle.setTextColor(getContext().getColor(R.color.air_quality_serious_pollution));
                    iconView.setImageResource(R.drawable.icon_pm_open_serious);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
            LogUtil.debugD("xuyue", "airQualitySwitch = " + airQualitySwitch);
        setHvacStatus(airQualitySwitch == AIR_QUALITY_SWITCH_ON ? (AIR_QUALITY_SWITCH_OFF + 1) : (AIR_QUALITY_SWITCH_ON + 1));
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        hvacManager.registerHvacBindCallback(HVAC_PM25_DISPLAY, this);
        hvacManager.registerHvacBindCallback(HVAC_INSIDE_PM25_VALUE, this);
        updateViews(hvacManager.getIntProperty(HVAC_PM25_DISPLAY, AREA_SEAT_HVAC_ALL), hvacManager.getIntProperty(HVAC_INSIDE_PM25_VALUE, AREA_SEAT_HVAC_ALL));
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if(null == carPropertyValue){
            return;
        }
        switch (carPropertyValue.getPropertyId()){
            case HVAC_PM25_DISPLAY:
                updateViews((int) carPropertyValue.getValue(), airQualityValue);
                break;
            case HVAC_INSIDE_PM25_VALUE:
                updateViews(airQualitySwitch, (int) carPropertyValue.getValue());
                break;
            default:
                break;
        }
    }
}
