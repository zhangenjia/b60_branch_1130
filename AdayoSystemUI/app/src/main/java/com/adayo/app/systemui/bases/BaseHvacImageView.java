package com.adayo.app.systemui.bases;

import android.annotation.CallSuper;
import android.annotation.SuppressLint;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_THREE;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/18 13:46
 */
@SuppressLint("AppCompatCustomView")
public class BaseHvacImageView extends ImageView implements AAOP_HvacManager.AAOP_HvacBindCallback, View.OnClickListener, AAOP_HvacManager.AAOP_HvacServiceConnectCallback {
    protected int sendVehicleID;
    protected int receiveVehicleID;
    protected int areaID;
    protected boolean isOnlyShow;
    protected boolean needSetValue;

    protected int status = HVAC_SEAT_HEATING_CLOSE;

    public BaseHvacImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BaseHvacImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BaseHvacImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
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
        isOnlyShow = array.getBoolean(R.styleable.hvacIcon_onlyShow, false);
        needSetValue = array.getBoolean(R.styleable.hvacIcon_needSetValue, false);
        array.recycle();
        LogUtil.d(SystemUIContent.TAG, "sendVehicleID = " + sendVehicleID +
                "receiveVehicleID = " + receiveVehicleID + " ; isOnlyShow = " + isOnlyShow);
        if (!isOnlyShow) {
            this.setOnClickListener(this);
        }
        initOther();
        updateView(AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(receiveVehicleID, areaID));
        bandVehicleID();
    }

    protected void initOther(){

    }

    protected void setHvacStatus(int value) {
        LogUtil.d(SystemUIContent.TAG, "sendVehicleID = " + sendVehicleID +
                "value = " + value);
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(sendVehicleID, areaID, value);
    }

    private void bandVehicleID() {
        LogUtil.d(SystemUIContent.TAG, "bandVehicleID = " );
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).addHvacServiceConnectCallback(this);
    }

    @CallSuper
    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if (null == carPropertyValue) {
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        int value = (int) carPropertyValue.getValue();
        if (cartPropertyId == receiveVehicleID && carPropertyValue.getAreaId() == areaID) {
            status = value;
            updateView(status);
        } else {
            return;
        }
    }

    protected void updateView(int value){

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo serviceConnectedInfo) {
        if(null != serviceConnectedInfo && serviceConnectedInfo.isHasConnected()) {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(receiveVehicleID, this);
            updateView(AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(receiveVehicleID, areaID));
        }
    }
}
