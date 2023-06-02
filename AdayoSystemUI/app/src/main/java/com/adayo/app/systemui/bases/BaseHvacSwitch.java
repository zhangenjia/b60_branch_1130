package com.adayo.app.systemui.bases;

import android.annotation.SuppressLint;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.SystemStatusManager;
import com.adayo.app.systemui.managers.business.CarControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.windows.dialogs.PromptBox;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import static com.adayo.app.systemui.configs.HvacContent.AREA_GLOBAL;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_AIR_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_DEFAULT_VALUE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_DEFROSTER;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_DISPLAY_CONTROL_REQ;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FAN_SPEED;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_IDS;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_MIN_BLOWING_VOLUME;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEND_DISPLAY_TYPE_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_STEERING_WHEEL_HEAT;
import static com.adayo.app.systemui.configs.HvacContent.SWITCH_OFF_REFERENCE_VALUE;
import static com.adayo.app.systemui.configs.HvacContent.SWITCH_ON_REFERENCE_VALUE;

/**
 * @author ADAYO-XuYue
 */
@SuppressLint("AppCompatCustomView")
public class BaseHvacSwitch extends TextView implements AAOP_HvacManager.AAOP_HvacBindCallback, AAOP_HvacManager.AAOP_HvacServiceConnectCallback {

    protected int sendVehicleID;
    protected int receiveVehicleID;
    protected int areaID;
    protected boolean isOnlyShow;
    protected boolean needSetValue;
    protected boolean needCheckAccOn;
    protected int sendOffset;
    protected int receiveOffset;

    public BaseHvacSwitch(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BaseHvacSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BaseHvacSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @android.annotation.Nullable AttributeSet attrs, int defStyleAttr){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.hvacIcon);
        int sendVehicleIndex = array.getInt(R.styleable.hvacIcon_sendVehicleIndex, -1);
        int receiveVehicleIndex = array.getInt(R.styleable.hvacIcon_receiveVehicleIndex, -1);
        int areaIndex = array.getInt(R.styleable.hvacIcon_area, 8);
        needCheckAccOn = array.getBoolean(R.styleable.hvacIcon_needCheckAccOn, false);
        sendOffset = array.getInt(R.styleable.hvacIcon_sendOffset, 0);
        receiveOffset = array.getInt(R.styleable.hvacIcon_receiveOffset, 0);
        if(sendVehicleIndex >= 0 && sendVehicleIndex < HVAC_IDS.length) {
            sendVehicleID = HVAC_IDS[sendVehicleIndex];
        }
        if(receiveVehicleIndex >= 0 && receiveVehicleIndex < HVAC_IDS.length) {
            receiveVehicleID = HVAC_IDS[receiveVehicleIndex];
        }
        areaID = HVAC_AIR_IDS[areaIndex];
        isOnlyShow = array.getBoolean(R.styleable.hvacIcon_onlyShow, false);
        needSetValue = array.getBoolean(R.styleable.hvacIcon_needSetValue, false);
        array.recycle();
        if(!isOnlyShow) {
            this.setOnClickListener(v -> {
                onViewClick(v);
            });
        }
        setView(SWITCH_OFF_REFERENCE_VALUE + receiveOffset);
        bandVehicleID();
    }

    protected void onViewClick(View v) {
        LogUtil.d(SystemUIContent.TAG, "sendVehicleID = " + sendVehicleID +
                "receiveVehicleID = " + receiveVehicleID + " ; areaID = " + areaID);
        WindowsManager.resetHvacHandler(true);
        int powerStatus = CarControllerImpl.getInstance().getPowerData();
        if (needCheckAccOn && powerStatus != 2 && powerStatus != 4) {
            PromptBox.getInstance().showPanel(R.drawable.icon_failure, SystemUIApplication.getSystemUIContext().getResources().getString(R.string.need_start_engine));
            return;
        }
        if (sendVehicleID == HVAC_STEERING_WHEEL_HEAT) {
            setHvacStatus(isSelected() ? 2 : 1);
        } else{
            setHvacStatus(isSelected() ? SWITCH_OFF_REFERENCE_VALUE + sendOffset : SWITCH_ON_REFERENCE_VALUE + sendOffset);
        }
    }

    private final int SEND_HVAC_FRONT = 10001;
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SEND_HVAC_FRONT:
                    AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(HVAC_DISPLAY_CONTROL_REQ, AREA_GLOBAL, HVAC_SEND_DISPLAY_TYPE_FRONT);
                    break;
                default:
                    break;
            }
        }
    };

    private void setHvacStatus(int value){
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(sendVehicleID, areaID, value);
        if(HVAC_DEFROSTER == sendVehicleID){
            mHandler.sendEmptyMessageDelayed(SEND_HVAC_FRONT, 60);
        }
    }

    private void bandVehicleID(){
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).addHvacServiceConnectCallback(this);
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if(null == carPropertyValue){
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        int value = (int) carPropertyValue.getValue();
        LogUtil.d(SystemUIContent.TAG, "cartPropertyId = " + cartPropertyId +
                " ; value = " + value + " ; areaID = " + carPropertyValue.getAreaId());
        if(cartPropertyId == receiveVehicleID && carPropertyValue.getAreaId() == areaID){
            if(cartPropertyId == HVAC_STEERING_WHEEL_HEAT){
                this.setSelected(SWITCH_ON_REFERENCE_VALUE == value);
            }else{
                this.setSelected(SWITCH_ON_REFERENCE_VALUE + receiveOffset == value);
            }
            setView(value);
        }
    }

    protected void setView(int value){
        if(needSetValue){
            if(receiveVehicleID == HVAC_FAN_SPEED){
                if(HVAC_MIN_BLOWING_VOLUME == value) {
                    setText("OFF");
                    setTextSize(25);
                }else{
                    if(HVAC_DEFAULT_VALUE != value) {
                        setText(value + "");
                        setTextSize(36);
                    }
                }
            }else {
                setText(value + "");
            }
        }
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        if(null != aaop_serviceConnectedInfo && aaop_serviceConnectedInfo.isHasConnected()) {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(receiveVehicleID, this);
            setView(AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(receiveVehicleID, areaID));
        }
    }
}
