package com.adayo.app.systemui.windows.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.bases.BaseHvacImageView;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.ConfigUtils;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_DRIVER;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_PASSENGER;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_REAR_LEFT;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_REAR_RIGHT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_ONE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_THREE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_TWO;
import static com.adayo.app.systemui.configs.SystemUIConfigs.FRONT_SEAT_HEATING;
import static com.adayo.app.systemui.configs.SystemUIConfigs.FRONT_SEAT_VENTION;
import static com.adayo.app.systemui.configs.SystemUIConfigs.REAR_SEAT_VENTION;
import static com.adayo.app.systemui.configs.SystemUIConfigs.SECOND_SEAT_HEATING;
import static com.adayo.app.systemui.configs.SystemUIConfigs.STEERING_WHEEL;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/18 10:02
 */
public class HvacSeatBlowView extends BaseHvacImageView {
    public HvacSeatBlowView(Context context) {
        super(context);
    }

    public HvacSeatBlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HvacSeatBlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initOther() {
        super.initOther();
        switch (areaID){
            case AREA_SEAT_DRIVER:
            case AREA_SEAT_PASSENGER:
                setVisibility(ConfigUtils.getKey(FRONT_SEAT_VENTION) == 1 ? View.VISIBLE : View.GONE);
                break;
            case AREA_SEAT_REAR_LEFT:
            case AREA_SEAT_REAR_RIGHT:
                setVisibility(ConfigUtils.getKey(REAR_SEAT_VENTION) == 1 ? View.VISIBLE : View.GONE);
                break;
        }
    }

    @Override
    protected void updateView(int value) {
        LogUtil.debugI("updateView" , "va;ie = " + value);
        switch (status) {
            case HVAC_SEAT_HEATING_CLOSE:
                AAOP_HSkin.with(this).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.icon_blow_close).applySkin(false);
                break;
            case HVAC_SEAT_HEATING_ONE:
                AAOP_HSkin.with(this).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.icon_blow_one).applySkin(false);
                break;
            case HVAC_SEAT_HEATING_TWO:
                AAOP_HSkin.with(this).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.icon_blow_two).applySkin(false);
                break;
            case HVAC_SEAT_HEATING_THREE:
                AAOP_HSkin.with(this).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.icon_blow_thr).applySkin(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (status){
            case HVAC_SEAT_HEATING_CLOSE:
                setHvacStatus(HVAC_SEAT_HEATING_THREE);
                break;
            case HVAC_SEAT_HEATING_ONE:
                setHvacStatus(HVAC_SEAT_HEATING_CLOSE);
                break;
            case HVAC_SEAT_HEATING_TWO:
                setHvacStatus(HVAC_SEAT_HEATING_ONE);
                break;
            case HVAC_SEAT_HEATING_THREE:
                setHvacStatus(HVAC_SEAT_HEATING_TWO);
                break;
            default:
                break;
        }
        WindowsManager.resetHvacHandler(true);
    }
}
