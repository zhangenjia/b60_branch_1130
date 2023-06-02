package com.adayo.app.systemui.windows.views;

import android.car.hardware.hvac.AAOP_HvacManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.BaseHvacSwitch;

import static com.adayo.app.systemui.configs.HvacContent.AREA_GLOBAL;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_DISPLAY_CONTROL_REQ;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FACE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FACE_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_FOOT_WIND;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_REAR_FACE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_REAR_FACE_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_REAR_FOOT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_DIRECTION_WIND;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT_STATUS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEND_DISPLAY_TYPE_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.SWITCH_ON_REFERENCE_VALUE;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/10 19:33
 */
public class HomeBlowingModeView extends BaseHvacSwitch {
    public HomeBlowingModeView(Context context) {
        super(context);
        setBackgroundResource(R.drawable.com_icon_blowing_face);
    }

    public HomeBlowingModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.com_icon_blowing_face);
    }

    public HomeBlowingModeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.com_icon_blowing_face);
    }

    @Override
    protected void setView(int value) {
        if(AREA_SEAT_HVAC_REAR == areaID){
            switch (value) {
                case HVAC_FAN_DIRECTION_REAR_FACE:
                    setBackgroundResource(R.drawable.com_icon_blowing_face);
                    break;
                case HVAC_FAN_DIRECTION_REAR_FOOT:
                    setBackgroundResource(R.drawable.com_icon_blowing_feet);
                    break;
                case HVAC_FAN_DIRECTION_REAR_FACE_FOOT:
                    setBackgroundResource(R.drawable.com_icon_blowing_face_feet);
                    break;
                default:
                    break;
            }
        }else{
            switch (value) {
                case HVAC_FAN_DIRECTION_FACE:
                    setBackgroundResource(R.drawable.com_icon_blowing_face);
                    break;
                case HVAC_FAN_DIRECTION_FOOT:
                    setBackgroundResource(R.drawable.com_icon_blowing_feet);
                    break;
                case HVAC_FAN_DIRECTION_FACE_FOOT:
                    setBackgroundResource(R.drawable.com_icon_blowing_face_feet);
                    break;
                case HVAC_FAN_DIRECTION_FOOT_WIND:
                    setBackgroundResource(R.drawable.com_icon_blowing_defrost_and_feet);
                    break;
                case HVAC_FAN_DIRECTION_WIND:
                    modeHandler.sendEmptyMessageDelayed(UPDATE_MODE, 50);
                    break;
                default:
                    break;
            }
        }
    }

    private final int UPDATE_MODE = 10002;
    protected Handler modeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_MODE:
                    int frontDefrosting = AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_FRONT_STATUS, 0);
                    if(SWITCH_ON_REFERENCE_VALUE == frontDefrosting) {
                        setBackgroundResource(R.drawable.com_icon_blowing_defrost);
                    }else {
                        setBackgroundResource(R.drawable.com_icon_blowing_windows);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
