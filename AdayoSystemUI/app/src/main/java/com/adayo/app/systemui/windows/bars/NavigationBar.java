package com.adayo.app.systemui.windows.bars;

import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bean.BCMInfo;
import com.adayo.app.systemui.bean.ModeInfo;
import com.adayo.app.systemui.configs.SystemUIConfigs;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.managers.business.CarControllerImpl;
import com.adayo.app.systemui.managers.business.SceneModeManager;
import com.adayo.app.systemui.managers.business.SourceControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.ConfigUtils;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.windows.views.SourceButton;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;

import static com.adayo.app.systemui.configs.HvacContent.AC2_ST_DISP_REQUEST;
import static com.adayo.app.systemui.configs.HvacContent.AREA_GLOBAL;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_HVAC_ALL;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_DEFROSTER;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_DISPLAY_CONTROL_REQ;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_POWER_SWITCH_OFF;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_POWER_SWITCH_ON;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_RECEIVE_DISPLAY_TYPE_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_RECEIVE_DISPLAY_TYPE_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_RECEIVE_DISPLAY_TYPE_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEND_DISPLAY_TYPE_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEND_DISPLAY_TYPE_FRONT;
import static com.adayo.app.systemui.configs.SystemUIConfigs.APA;
import static com.adayo.app.systemui.configs.SystemUIConfigs.APA_TYPE;
import static com.adayo.app.systemui.configs.SystemUIContent.NAVIGATION_BAR;
import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

public class NavigationBar implements View.OnTouchListener, AAOP_HvacManager.AAOP_HvacServiceConnectCallback, AAOP_HvacManager.AAOP_HvacBindCallback {
    private volatile static NavigationBar navigationBar;
    private WindowsControllerImpl windowsController;

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private RelativeLayout mFloatLayout;
    private LinearLayout frontHvacLayout;
    private LinearLayout rearHvacLayout;
    private SourceButton apaView;

    private boolean isAdded = false;

    private NavigationBar() {
        initView();
    }

    public static NavigationBar getInstance() {
        if (navigationBar == null) {
            synchronized (NavigationBar.class) {
                if (navigationBar == null) {
                    navigationBar = new NavigationBar();
                }
            }
        }
        return navigationBar;
    }

    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        mFloatLayout = (RelativeLayout) AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext()).inflate(ConfigUtils.getKey(APA) == APA_TYPE ?R.layout.navigation_bar_apa : R.layout.navigation_bar, null);
//        mFloatLayout = (RelativeLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.navigation_bar, null);
        mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                116,
                SystemUIContent.TYPE_NAVIGATION_BAR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT);
        mLayoutParams.token = new Binder();
        mLayoutParams.gravity = Gravity.BOTTOM;
        mLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        mLayoutParams.setTitle(NAVIGATION_BAR);
        mLayoutParams.packageName = SystemUIApplication.getSystemUIContext().getPackageName();
        mLayoutParams.windowAnimations = R.style.NavigationBarAnimation;
        frontHvacLayout = mFloatLayout.findViewById(R.id.front_hvac);
        rearHvacLayout = mFloatLayout.findViewById(R.id.rear_hvac);
        if(ConfigUtils.getKey(APA) == APA_TYPE) {
            LogUtil.debugD(SystemUIContent.TAG, "ConfigUtils.getKey(APA) = " + ConfigUtils.getKey(APA));
            apaView = mFloatLayout.findViewById(R.id.apa_view);
            CarControllerImpl.getInstance().addCallback((BaseCallback<BCMInfo>) data -> {
                newEngineStatus = data.getNewEngineStatus();
                setAPAEnabled();
            });
            SceneModeManager.getInstance().addCallback(new BaseCallback<ModeInfo>() {
                @Override
                public void onDataChange(ModeInfo data) {
                    dragMode = data.isDrag_mode();
                    setAPAEnabled();
                }
            });
//            apaView.setEnabled(CarControllerImpl.getInstance().getNewEngineStatus() != 0);
        }
//        allAPPView = mFloatLayout.findViewById(R.id.all_app_view);
//        apaView.setVisibility(ConfigUtils.getKey(APA) == APA_TYPE ? View.VISIBLE : View.GONE);
//        allAPPView.setVisibility(ConfigUtils.getKey(APA) == APA_TYPE ? View.GONE : View.VISIBLE);
        frontHvacLayout.setOnTouchListener(this);
        rearHvacLayout.setOnTouchListener(this);
        AAOP_HSkin.getWindowViewManager().addWindowView(mFloatLayout);
        bandVehicleID();
    }

    private int newEngineStatus;
    private boolean dragMode;
    private void setAPAEnabled(){
        if(null != apaView){
            apaView.setEnabled(newEngineStatus != 0 && !dragMode);
        }
    }

    private void bandVehicleID() {
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).addHvacServiceConnectCallback(this);
    }

    private void show() {
        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, mLayoutParams);
            isAdded = true;
        }
    }

    public void viewChange(int area){
        if(area == HVAC_REAR){
            rearHvacLayout.setVisibility(View.VISIBLE);
            frontHvacLayout.setVisibility(View.GONE);
        }else{
            rearHvacLayout.setVisibility(View.GONE);
            frontHvacLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setVisibility(int visible) {
        if (!SystemUIConfigs.HAS_NAVIGATION_BAR) {
            return;
        }
        LogUtil.debugD(SystemUIContent.TAG, visible + "");
        show();
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(visible);
        }
        if (null == windowsController) {
            windowsController = WindowsControllerImpl.getInstance();
        }
        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_NAVIGATION_BAR, visible);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String currentUISource = SourceControllerImpl.getInstance().getCurrentUISource();
        if(AdayoSource.ADAYO_SOURCE_RVC.equals(currentUISource) || AdayoSource.ADAYO_SOURCE_AVM.equals(currentUISource) || AdayoSource.ADAYO_SOURCE_APA.equals(currentUISource)){
            return false;
        }
        return WindowsManager.showHvacPanel(event, v.getId() == R.id.front_hvac ? HVAC_FRONT : HVAC_REAR);
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        if(null != aaop_serviceConnectedInfo && aaop_serviceConnectedInfo.isHasConnected()) {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(AC2_ST_DISP_REQUEST, this);
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(HVAC_DEFROSTER, this);
        }
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if (null == carPropertyValue) {
            return;
        }
        int cartPropertyId = carPropertyValue.getPropertyId();
        int value = (int) carPropertyValue.getValue();
        int areaId = (int) carPropertyValue.getAreaId();
        LogUtil.debugD(TAG, "cartPropertyId = " + cartPropertyId + " ; value = " + value);
        if (cartPropertyId == AC2_ST_DISP_REQUEST && carPropertyValue.getAreaId() == AREA_SEAT_HVAC_ALL) {
            Message msgVolume = Message.obtain();
            msgVolume.what = UPDATE_HVAC_PANEL;
            msgVolume.arg1 = value;
            mHandler.sendMessage(msgVolume);
        } else if(HVAC_DEFROSTER == cartPropertyId){
            if((value == HVAC_POWER_SWITCH_ON || value == HVAC_POWER_SWITCH_OFF) && AREA_SEAT_HVAC_ALL == areaId
                    && CarControllerImpl.getInstance().getPowerData() == HVAC_POWER_SWITCH_ON) {
                if (defrosterValue != -1 && defrosterValue != value) {
                    Message msgVolume = Message.obtain();
                    msgVolume.what = UPDATE_HVAC_PANEL;
                    msgVolume.arg1 = 2;
                    mHandler.sendMessage(msgVolume);
                    AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(HVAC_DISPLAY_CONTROL_REQ, AREA_GLOBAL, HVAC_SEND_DISPLAY_TYPE_FRONT);
                }
                defrosterValue = value;
            }
        } else {
            return;
        }
    }

    private int defrosterValue = -1;
    private static final int UPDATE_HVAC_PANEL = 10001;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_HVAC_PANEL:
                    updateHvacPanel(msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

    private void updateHvacPanel(int type){
        int panelType = type%16;
        int navigationBarType = type/16;

        LogUtil.debugD(TAG, "panelType = " + panelType + " ; navigationBarType = " + navigationBarType + " ; type = " + type + " ; HvacPanelVisibility = " + WindowsControllerImpl.getInstance().getViewState().getHvacPanelVisibility());
        switch (panelType){
            case HVAC_RECEIVE_DISPLAY_TYPE_FRONT:
                WindowsManager.setHvacPanelVisibility(View.VISIBLE, HVAC_FRONT, false, true);
                break;
            case HVAC_RECEIVE_DISPLAY_TYPE_REAR:
                WindowsManager.setHvacPanelVisibility(View.VISIBLE, HVAC_REAR, false, true);
                break;
            case HVAC_RECEIVE_DISPLAY_TYPE_CLOSE:
                if(WindowsControllerImpl.getInstance().getViewState().getHvacPanelVisibility() == View.VISIBLE) {
                    WindowsManager.setHvacPanelVisibility(View.GONE, HVAC_FRONT, false, true);
                }
                break;
            default:
                break;
        }
        switch (navigationBarType){
            case HVAC_RECEIVE_DISPLAY_TYPE_FRONT:
            case HVAC_RECEIVE_DISPLAY_TYPE_CLOSE:
                viewChange(HVAC_FRONT);
                break;
            case HVAC_RECEIVE_DISPLAY_TYPE_REAR:
                viewChange(HVAC_REAR);
                break;
            default:
                break;
        }
    }
}
