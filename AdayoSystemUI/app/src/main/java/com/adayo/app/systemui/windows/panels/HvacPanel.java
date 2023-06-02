package com.adayo.app.systemui.windows.panels;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.AAOP_HvacManager;
import android.car.hardware.hvac.AAOP_ServiceConnectedInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bean.BCMInfo;
import com.adayo.app.systemui.bean.MassageInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.managers.business.CarControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.ConfigUtils;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.windows.dialogs.MassageSetBox;
import com.adayo.app.systemui.windows.dialogs.PromptBox;
import com.adayo.app.systemui.windows.views.HvacBlowingModeView;
import com.adayo.app.systemui.windows.views.HvacViewPager;
import com.adayo.app.systemui.windows.views.PullUpDumperLayout;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.airbnb.lottie.LottieAnimationView;
import com.android.internal.widget.PagerAdapter;
import com.android.internal.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static com.adayo.app.systemui.configs.HvacContent.AREA_GLOBAL;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_DRIVER;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_PASSENGER;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_REAR_LEFT;
import static com.adayo.app.systemui.configs.HvacContent.AREA_SEAT_REAR_RIGHT;
import static com.adayo.app.systemui.configs.HvacContent.BCM_SEAT_MASSAGE_INTENSITY_CTRL;
import static com.adayo.app.systemui.configs.HvacContent.BCM_SEAT_MASSAGE_MODE_CTRL;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_DISPLAY_CONTROL_REQ;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FOUR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_HEATING_THREE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_TEMPERATURE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEAT_VENTILATION;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEND_DISPLAY_TYPE_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEND_DISPLAY_TYPE_FRONT;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_SEND_DISPLAY_TYPE_REAR;
import static com.adayo.app.systemui.configs.HvacContent.HVAC_THREE;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_INTENSITY_CLOSE;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_INTENSITY_HIGH;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_JSON_DRIVER;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_JSON_PASSENGER;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_JSON_REAR_LEFT;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_JSON_REAR_RIGHT;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_PULSE;
import static com.adayo.app.systemui.configs.HvacContent.MASSAGE_MODE_SERPENTINE;
import static com.adayo.app.systemui.configs.HvacContent.SEAT_ROW_1_LEFT;
import static com.adayo.app.systemui.configs.HvacContent.SEAT_ROW_1_RIGHT;
import static com.adayo.app.systemui.configs.HvacContent.SEAT_ROW_2_LEFT;
import static com.adayo.app.systemui.configs.HvacContent.SEAT_ROW_2_RIGHT;
import static com.adayo.app.systemui.configs.SystemUIConfigs.AIR_CONTROL_CONFIG;
import static com.adayo.app.systemui.configs.SystemUIConfigs.FRONT_SEAT_HEATING;
import static com.adayo.app.systemui.configs.SystemUIConfigs.FRONT_SEAT_MASSAGE;
import static com.adayo.app.systemui.configs.SystemUIConfigs.FRONT_SEAT_VENTION;
import static com.adayo.app.systemui.configs.SystemUIConfigs.IN_CAR_AIR_INTELLIGENT;
import static com.adayo.app.systemui.configs.SystemUIConfigs.REAR_SEAT_MASSAGE;
import static com.adayo.app.systemui.configs.SystemUIConfigs.REAR_SEAT_VENTION;
import static com.adayo.app.systemui.configs.SystemUIConfigs.SECOND_SEAT_HEATING;
import static com.adayo.app.systemui.configs.SystemUIConfigs.STEERING_WHEEL;
import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/3 10:59
 */
public class HvacPanel implements ViewPager.OnPageChangeListener, View.OnClickListener, AAOP_HvacManager.AAOP_HvacServiceConnectCallback, AAOP_HvacManager.AAOP_HvacBindCallback, DialogInterface.OnDismissListener, BaseCallback<BCMInfo> {

    private volatile static HvacPanel hvacPanel;
    private WindowManager.LayoutParams layoutParams;
    private RelativeLayout mFloatLayout;
    private WindowManager mWindowManager;

    private List<View> pageViews;
    private HvacViewPager hvacViewPager;

    private PullUpDumperLayout mLayout;
    private TextView titleFront;
    private TextView titleRear;
    private TextView titleThree;
    private TextView titleFour;
    private ImageView lineThree;
    private ImageView lineFour;

    private ImageView seatCopilotBlow;
    private ImageView seatDriverBlow;
    private ImageView seatRightBackBlow;
    private ImageView seatBackLeftBlow;
    private ImageView seatCopilot;
    private ImageView seatDriver;
    private ImageView seatRightBack;
    private ImageView seatBackLeft;

    private HvacBlowingModeView frontBlowingMode;
    private HvacBlowingModeView rearBlowingMode;

    private WindowsControllerImpl windowsController;
    private TextView steeringWheel;

    private LayoutInflater lf;

    public static HvacPanel getInstance() {
        if (hvacPanel == null) {
            synchronized (HvacPanel.class) {
                if (hvacPanel == null) {
                    hvacPanel = new HvacPanel();
                }
            }
        }
        return hvacPanel;
    }

    private HvacPanel() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    private void initView() {
        layoutParams = new WindowManager.LayoutParams(TYPE_APPLICATION_OVERLAY);
        layoutParams.width = 1920;
        layoutParams.height = 1952;
        layoutParams.y = 104;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        layoutParams.setTitle("HvacPanel");
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        layoutParams.gravity = Gravity.END|Gravity.TOP;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        if(ConfigUtils.getKey(AIR_CONTROL_CONFIG) == 1){
            mFloatLayout = (RelativeLayout) AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext()).inflate(R.layout.hvac_panel_high, null);
            hvacViewPager = mFloatLayout.findViewById(R.id.view_pager);
            int mFrontFloatLayoutId;
            if(ConfigUtils.getKey(IN_CAR_AIR_INTELLIGENT) == 2){
                mFrontFloatLayoutId = R.layout.hvac_front_view_high;
            } else {
                mFrontFloatLayoutId =  R.layout.hvac_front_view_middle;
            }
            pageViews = new ArrayList<>();
            lf = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext());
            View mFrontFloatLayout = lf.inflate(mFrontFloatLayoutId, null);
            pageViews.add(mFrontFloatLayout);
            View mRearFloatLayout = lf.inflate(R.layout.hvac_rear_view, null);
            pageViews.add(mRearFloatLayout);
            boolean hasSeatPage = addSeatPage();
            boolean hasMassagePage = addMassagePage();
            hvacViewPager.setAdapter(pagerAdapter);
            hvacViewPager.setOnPageChangeListener(this);

            titleFront = mFloatLayout.findViewById(R.id.title_front);
            titleFront.setOnClickListener(this);
            titleRear = mFloatLayout.findViewById(R.id.title_rear);
            titleRear.setOnClickListener(this);
            titleThree = mFloatLayout.findViewById(R.id.title_three);
            titleThree.setOnClickListener(this);
            titleFour = mFloatLayout.findViewById(R.id.title_four);
            titleFour.setOnClickListener(this);
            lineThree = mFloatLayout.findViewById(R.id.dividing_line_three);
            lineFour = mFloatLayout.findViewById(R.id.dividing_line_four);
            if(hasSeatPage && hasMassagePage){
                lineThree.setVisibility(View.VISIBLE);
                titleThree.setVisibility(View.VISIBLE);
                lineFour.setVisibility(View.VISIBLE);
                titleFour.setVisibility(View.VISIBLE);
                titleThree.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.hvac_seat));
                titleFour.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.hvac_seat_massage));
                AAOP_HSkin.with(titleRear).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_rear_bg).applySkin(false);
                AAOP_HSkin.with(titleThree).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_rear_bg).applySkin(false);
                AAOP_HSkin.with(titleFour).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_seat_bg).applySkin(false);
            }else if(hasSeatPage){
                lineThree.setVisibility(View.VISIBLE);
                titleThree.setVisibility(View.VISIBLE);
                titleThree.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.hvac_seat));
                AAOP_HSkin.with(titleRear).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_rear_bg).applySkin(false);
                AAOP_HSkin.with(titleThree).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_seat_bg).applySkin(false);
            }else if(hasMassagePage){
                lineThree.setVisibility(View.VISIBLE);
                titleThree.setVisibility(View.VISIBLE);
                titleThree.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.hvac_seat_massage));
                AAOP_HSkin.with(titleRear).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_rear_bg).applySkin(false);
                AAOP_HSkin.with(titleThree).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_seat_bg).applySkin(false);
            }else{
                AAOP_HSkin.with(titleRear).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_hvac_title_seat_bg).applySkin(false);
            }

            frontBlowingMode = mFrontFloatLayout.findViewById(R.id.blowing_mode);
            rearBlowingMode = mRearFloatLayout.findViewById(R.id.rear_blowing_mode);
            setPage(hvacViewPager.getCurrentItem(), false);
            bandVehicleID();
        }else {
            mFloatLayout = (RelativeLayout) AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext()).inflate(R.layout.hvac_panel_low, null);
        }
        mLayout = mFloatLayout.findViewById(R.id.layout);
        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, layoutParams);
            mFloatLayout.setVisibility(View.GONE);
            isAdded = true;
        }
        LogUtil.debugD(SystemUIContent.TAG, "init view end " );
//        mFloatLayout.setOnTouchListener(this);
        AAOP_HSkin.getWindowViewManager().addWindowView(mFloatLayout);
    }

    private ImageView frontRightMassage;
    private ImageView frontLeftMassage;
    private ImageView rearRightMassage;
    private ImageView rearLeftMassage;
    private ImageView seatMask;

    private LottieAnimationView frontLeftAnimation;
    private LottieAnimationView frontRightAnimation;
    private LottieAnimationView rearLeftAnimation;
    private LottieAnimationView rearRightAnimation;

    private boolean hasFrontSeatMassage;
    private boolean hasRearSeatMassage;
    private boolean addMassagePage() {
        hasFrontSeatMassage = ConfigUtils.getKey(FRONT_SEAT_MASSAGE) == 1;
        hasRearSeatMassage = ConfigUtils.getKey(REAR_SEAT_MASSAGE) == 1;
        if(hasFrontSeatMassage || hasRearSeatMassage){
            View mSeatFloatLayout = lf.inflate(R.layout.hvac_massage_layout, null);
            pageViews.add(mSeatFloatLayout);
            frontRightMassage = mSeatFloatLayout.findViewById(R.id.front_right_massage);
            frontRightMassage.setVisibility(hasFrontSeatMassage ? View.VISIBLE :View.GONE);
            frontLeftMassage = mSeatFloatLayout.findViewById(R.id.front_left_massage);
            frontLeftMassage.setVisibility(hasFrontSeatMassage ? View.VISIBLE :View.GONE);
            rearRightMassage = mSeatFloatLayout.findViewById(R.id.rear_right_massage);
            rearRightMassage.setVisibility(hasRearSeatMassage ? View.VISIBLE :View.GONE);
            rearLeftMassage = mSeatFloatLayout.findViewById(R.id.rear_left_massage);
            rearLeftMassage.setVisibility(hasRearSeatMassage ? View.VISIBLE :View.GONE);
            seatMask = mSeatFloatLayout.findViewById(R.id.seat_mask);
            frontRightMassage.setOnClickListener(this);
            frontLeftMassage.setOnClickListener(this);
            rearRightMassage.setOnClickListener(this);
            rearLeftMassage.setOnClickListener(this);

            frontLeftAnimation = mSeatFloatLayout.findViewById(R.id.front_left_view);
            frontRightAnimation = mSeatFloatLayout.findViewById(R.id.front_right_view);
            rearLeftAnimation = mSeatFloatLayout.findViewById(R.id.rear_left_view);
            rearRightAnimation = mSeatFloatLayout.findViewById(R.id.rear_right_view);
            AAOP_HSkin.with(frontRightMassage).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_seat_massage).applySkin(false);
            AAOP_HSkin.with(frontLeftMassage).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_seat_massage).applySkin(false);
            AAOP_HSkin.with(rearRightMassage).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_seat_massage).applySkin(false);
            AAOP_HSkin.with(rearLeftMassage).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_seat_massage).applySkin(false);
            return true;
        }
        return false;
    }

    private boolean addSeatPage() {
        boolean hasFrontSeatHeating = ConfigUtils.getKey(FRONT_SEAT_HEATING) == 1;
        boolean hasRearSeatHeating = ConfigUtils.getKey(SECOND_SEAT_HEATING) == 1;
        boolean hasFrontSeatVention = ConfigUtils.getKey(FRONT_SEAT_VENTION) == 1;
        boolean hasRearSeatVention = ConfigUtils.getKey(REAR_SEAT_VENTION) == 1;
        boolean hasSteeringWheel = ConfigUtils.getKey(STEERING_WHEEL) == 1;
        if(hasFrontSeatHeating || hasRearSeatHeating
                || hasFrontSeatVention || hasRearSeatVention
                || hasSteeringWheel) {
            View mSeatFloatLayout = lf.inflate(R.layout.hvac_seat_layout_high, null);
            pageViews.add(mSeatFloatLayout);
            seatDriverBlow = mSeatFloatLayout.findViewById(R.id.seat_driver_blow);
            seatCopilotBlow = mSeatFloatLayout.findViewById(R.id.seat_copilot_blow);
            seatBackLeftBlow = mSeatFloatLayout.findViewById(R.id.seat_back_left_blow);
            seatRightBackBlow = mSeatFloatLayout.findViewById(R.id.seat_right_back_blow);

            seatDriver = mSeatFloatLayout.findViewById(R.id.seat_driver);
            seatCopilot = mSeatFloatLayout.findViewById(R.id.seat_copilot);
            seatBackLeft = mSeatFloatLayout.findViewById(R.id.seat_back_left);
            seatRightBack = mSeatFloatLayout.findViewById(R.id.seat_right_back);
            steeringWheel = mSeatFloatLayout.findViewById(R.id.steering_wheel);
            steeringWheel.setVisibility(hasSteeringWheel ? View.VISIBLE : View.GONE);
            AAOP_HSkin.with(steeringWheel).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.selector_steering_wheel).applySkin(false);
            return true;
        }
        return false;
    }

    private CarControllerImpl carController;
    private void bandVehicleID() {
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).addHvacServiceConnectCallback(this);
        carController = CarControllerImpl.getInstance();
        carController.addCallback(this);
    }

    private void updateBlowView(int areaID, int value) {
        if(value > HVAC_SEAT_HEATING_THREE || value < HVAC_SEAT_HEATING_CLOSE){
            return;
        }
        if(AREA_SEAT_DRIVER == areaID && null != seatDriverBlow){
            seatDriverBlow.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
        if(AREA_SEAT_PASSENGER == areaID && null != seatCopilotBlow){
            seatCopilotBlow.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
        if(AREA_SEAT_REAR_RIGHT == areaID && null != seatRightBackBlow){
            seatRightBackBlow.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
        if(AREA_SEAT_REAR_LEFT == areaID && null != seatBackLeftBlow){
            seatBackLeftBlow.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
    }

    private void updateHeatingView(int areaID, int value) {
        if(value > HVAC_SEAT_HEATING_THREE || value < HVAC_SEAT_HEATING_CLOSE){
            return;
        }
        if(AREA_SEAT_DRIVER == areaID && null != seatDriver){
            seatDriver.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
        if(AREA_SEAT_PASSENGER == areaID && null != seatCopilot){
            seatCopilot.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
        if(AREA_SEAT_REAR_LEFT == areaID && null != seatBackLeft){
            seatBackLeft.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
        if(AREA_SEAT_REAR_RIGHT == areaID && null != seatRightBack){
            seatRightBack.setVisibility(HVAC_SEAT_HEATING_CLOSE == value ? View.GONE : View.VISIBLE);
        }
    }

    private void setPage(int page, boolean isFromUser){
        LogUtil.debugD(SystemUIContent.TAG, "Page = " + page + " ; IsFromUSer = " + isFromUser);
        if(null == titleFront || null == titleRear || null == titleThree || null == titleFour){
            if(isFromUser){
                sendPageStatus(HVAC_SEND_DISPLAY_TYPE_FRONT);
            }
            return;
        }
        titleFront.setSelected(false);
        titleRear.setSelected(false);
        titleThree.setSelected(false);
        titleFour.setSelected(false);
        switch (page){
            case HVAC_FRONT:
                titleFront.setSelected(true);
                if(isFromUser){
                    sendPageStatus(HVAC_SEND_DISPLAY_TYPE_FRONT);
                }
                WindowsManager.resetHvacHandler(false);
                break;
            case HVAC_REAR:
                titleRear.setSelected(true);
                if(isFromUser){
                    sendPageStatus(HVAC_SEND_DISPLAY_TYPE_REAR);
                }
                WindowsManager.resetHvacHandler(false);
                break;
            case HVAC_THREE:
                titleThree.setSelected(true);
                if(isFromUser){
                    sendPageStatus(HVAC_SEND_DISPLAY_TYPE_CLOSE);
                }
                WindowsManager.resetHvacHandler(true);
                break;
            case HVAC_FOUR:
                titleFour.setSelected(true);
                if(isFromUser){
                    sendPageStatus(HVAC_SEND_DISPLAY_TYPE_CLOSE);
                }
                WindowsManager.resetHvacHandler(true);
                break;
            default:
                break;
        }
        hvacViewPager.setCurrentItem(page, false);
        if(null != frontBlowingMode) {
            frontBlowingMode.setAnimationStatus(page);
        }
        if(null != rearBlowingMode) {
            rearBlowingMode.setAnimationStatus(page);
        }
//        WindowsManager.setNavigationBarArea(page);
    }

    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(pageViews.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(pageViews.get(position));
            return pageViews.get(position);
        }
    };

    private void show() {
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(View.VISIBLE);
        }
    }

    private void dismiss() {
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(View.GONE);
        }
        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_HVAC_PANEL, View.GONE);
        WindowsManager.resetHvacHandler(false);
    }

    private boolean isAdded = false;
    public void setVisibility(int visible, int area, boolean isFromUser, boolean needAdmin) {
        LogUtil.debugD(SystemUIContent.TAG, "visible = " + visible + " ; area = " + area + " ; isFromUser = " + isFromUser  + " ; needAdmin = " + needAdmin);
        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, layoutParams);
            isAdded = true;
        }
        if (null == windowsController) {
            windowsController = WindowsControllerImpl.getInstance();
        }
        if (View.VISIBLE == visible) {
            if(windowsController.getViewState().getHvacPanelVisibility() != View.VISIBLE) {
                show();
                if(needAdmin){
                    mLayout.startOpenAnimation();
                }
            }
            windowsController.notifyVisibility(SystemUIContent.TYPE_OF_HVAC_PANEL, View.VISIBLE);
            setPage(area, isFromUser);
        } else {
            if(windowsController.getViewState().getHvacPanelVisibility() == View.VISIBLE) {
                if (!needAdmin) {
                    if (isFromUser) {
                        dismiss();
                        sendPageStatus(HVAC_SEND_DISPLAY_TYPE_CLOSE);
                    } else {
                        if (hvacViewPager == null || (hvacViewPager != null && hvacViewPager.getCurrentItem() != HVAC_THREE && hvacViewPager.getCurrentItem() != HVAC_FOUR)) {
                            dismiss();
                        }
                    }
                } else {
                    if (isFromUser) {
                        mLayout.startCloseAnimation();
                        sendPageStatus(HVAC_SEND_DISPLAY_TYPE_CLOSE);
                    } else {
                        if (hvacViewPager == null || (hvacViewPager != null && hvacViewPager.getCurrentItem() != HVAC_THREE && hvacViewPager.getCurrentItem() != HVAC_FOUR)) {
                            mLayout.startCloseAnimation();
                        }
                    }
                }
            }
        }
    }

    private boolean fromOutside = false;
    public boolean doEvent(MotionEvent event, int area){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (964 <= event.getRawY()) {
                    setVisibility(View.VISIBLE, area, true, false);
                    fromOutside = true;
                } else {
                    fromOutside = false;
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                return false;
        }
        return mLayout.doAnimation(event, fromOutside);
    }

    private void sendPageStatus(int type){
        AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).setIntProperty(HVAC_DISPLAY_CONTROL_REQ, AREA_GLOBAL, type);
    }

    private int getMassageStatus(int vehicleId, int areaId){
        return AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(vehicleId, areaId);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private MassageSetBox massageSetBox;
    private void initMassageBox(){
        if(null == massageSetBox){
            massageSetBox = MassageSetBox.getInstance();
            massageSetBox.setOnDismissListener(this);
        }
    }

    private MassageInfo massageInfo = new MassageInfo();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_front:
                setPage(HVAC_FRONT, true);
                break;
            case R.id.title_rear:
                setPage(HVAC_REAR, true);
                break;
            case R.id.title_three:
                setPage(HVAC_THREE, true);
                break;
            case R.id.title_four:
                setPage(HVAC_FOUR, true);
                break;
            case R.id.front_right_massage:
                initMassageBox();
                massageSetBox.showPanel(R.style.MassageAnimationFrontLeft, 941, 77, SEAT_ROW_1_RIGHT
                        , massageInfo.getCopilotMode(), massageInfo.getCopilotStatus());
                seatMask.setBackgroundResource(R.drawable.mask_copilot);
                seatMask.setVisibility(View.VISIBLE);
                WindowsManager.resetHvacHandler(true);
                break;
            case R.id.front_left_massage:
                initMassageBox();
                massageSetBox.showPanel(R.style.MassageAnimationFrontLeft, 941, 437, SEAT_ROW_1_LEFT
                        , massageInfo.getDriverMode(), massageInfo.getDriverStatus());
                seatMask.setBackgroundResource(R.drawable.mask_driver);
                seatMask.setVisibility(View.VISIBLE);
                WindowsManager.resetHvacHandler(true);
                break;
            case R.id.rear_right_massage:
                initMassageBox();
                massageSetBox.showPanel(R.style.MassageAnimationRearRight, 141, 77, SEAT_ROW_2_RIGHT
                        , massageInfo.getRearRightMode(), massageInfo.getRearRightStatus());
                seatMask.setBackgroundResource(R.drawable.mask_back_right);
                seatMask.setVisibility(View.VISIBLE);
                WindowsManager.resetHvacHandler(true);
                break;
            case R.id.rear_left_massage:
                initMassageBox();
                massageSetBox.showPanel(R.style.MassageAnimationRearLeft, 141, 437, SEAT_ROW_2_LEFT
                        , massageInfo.getRearLeftMode(), massageInfo.getRearLeftStatus());
                seatMask.setBackgroundResource(R.drawable.mask_back_left);
                seatMask.setVisibility(View.VISIBLE);
                WindowsManager.resetHvacHandler(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnectStatusChange(AAOP_ServiceConnectedInfo aaop_serviceConnectedInfo) {
        if(null != aaop_serviceConnectedInfo && aaop_serviceConnectedInfo.isHasConnected()) {
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(HVAC_SEAT_TEMPERATURE, this);
            AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(HVAC_SEAT_VENTILATION, this);
            updateHeatingView(AREA_SEAT_DRIVER, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_TEMPERATURE, AREA_SEAT_DRIVER));
            updateHeatingView(AREA_SEAT_PASSENGER, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_TEMPERATURE, AREA_SEAT_PASSENGER));
            updateHeatingView(AREA_SEAT_REAR_LEFT, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_TEMPERATURE, AREA_SEAT_REAR_LEFT));
            updateHeatingView(AREA_SEAT_REAR_RIGHT, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_TEMPERATURE, AREA_SEAT_REAR_RIGHT));
            updateBlowView(AREA_SEAT_DRIVER, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_VENTILATION, AREA_SEAT_DRIVER));
            updateBlowView(AREA_SEAT_PASSENGER, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_VENTILATION, AREA_SEAT_PASSENGER));
            updateBlowView(AREA_SEAT_REAR_LEFT, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_VENTILATION, AREA_SEAT_REAR_LEFT));
            updateBlowView(AREA_SEAT_REAR_RIGHT, AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).getIntProperty(HVAC_SEAT_VENTILATION, AREA_SEAT_REAR_RIGHT));
            if(hasFrontSeatMassage || hasRearSeatMassage) {
                AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(BCM_SEAT_MASSAGE_MODE_CTRL, this);
                AAOP_HvacManager.getInstance(SystemUIApplication.getSystemUIContext()).registerHvacBindCallback(BCM_SEAT_MASSAGE_INTENSITY_CTRL, this);
                massageInfo.setDriverStatus(getMassageStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, SEAT_ROW_1_LEFT));
                massageInfo.setDriverMode(getMassageStatus(BCM_SEAT_MASSAGE_MODE_CTRL, SEAT_ROW_1_LEFT));
                massageInfo.setCopilotStatus(getMassageStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, SEAT_ROW_1_RIGHT));
                massageInfo.setCopilotMode(getMassageStatus(BCM_SEAT_MASSAGE_MODE_CTRL, SEAT_ROW_1_RIGHT));
                massageInfo.setRearRightStatus(getMassageStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, SEAT_ROW_2_RIGHT));
                massageInfo.setRearRightMode(getMassageStatus(BCM_SEAT_MASSAGE_MODE_CTRL, SEAT_ROW_2_RIGHT));
                massageInfo.setRearLeftStatus(getMassageStatus(BCM_SEAT_MASSAGE_INTENSITY_CTRL, SEAT_ROW_2_LEFT));
                massageInfo.setRearLeftMode(getMassageStatus(BCM_SEAT_MASSAGE_MODE_CTRL, SEAT_ROW_2_LEFT));

            }
        }
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        if (null == carPropertyValue) {
            return;
        }
        int propertyId = carPropertyValue.getPropertyId();
        int value = (int) carPropertyValue.getValue();
        int areaID = carPropertyValue.getAreaId();
        LogUtil.d(SystemUIContent.TAG, "propertyId = " + propertyId +
                " ; value = " + value + " ; areaID = " + areaID);
        switch (propertyId){
            case HVAC_SEAT_TEMPERATURE:
                updateHeatingView(areaID, value);
                break;
            case HVAC_SEAT_VENTILATION:
                updateBlowView(areaID, value);
                break;
            case BCM_SEAT_MASSAGE_MODE_CTRL:
                if(value >= MASSAGE_MODE_PULSE && value <= MASSAGE_MODE_SERPENTINE) {
                    updateMassageModeView(areaID, value);
                }
                break;
            case BCM_SEAT_MASSAGE_INTENSITY_CTRL:
                if(areaID == SEAT_ROW_1_LEFT || areaID == SEAT_ROW_1_RIGHT){
                    value ++;
                }
                if(value >= MASSAGE_INTENSITY_CLOSE && value <= MASSAGE_INTENSITY_HIGH) {
                    updateMassageIntensityView(areaID, value);
                }
                break;
            default:
                break;
        }
    }

    private boolean isFrontLeftMassageOpen = false;
    private boolean isFrontRightMassageOpen = false;
    private boolean isRearLeftMassageOpen = false;
    private boolean isRearRightMassageOpen = false;
    private void updateMassageIntensityView(int areaID, int value) {
        if(hasFrontSeatMassage || hasFrontSeatMassage) {
            if (null != massageSetBox && massageSetBox.isShowing()) {
                massageSetBox.updateLevelView(value, areaID);
            }
            boolean isOpen = value > MASSAGE_INTENSITY_CLOSE;
            switch (areaID) {
                case SEAT_ROW_1_LEFT:
                    massageInfo.setDriverStatus(value);
                    if(isFrontLeftMassageOpen == isOpen && !isOpen){
                        break;
                    }
                    frontLeftMassage.setSelected(isOpen);
                    doAnimation(frontLeftAnimation, MASSAGE_JSON_DRIVER[value - 1], isOpen);
                    isFrontLeftMassageOpen = isOpen;
                    break;
                case SEAT_ROW_1_RIGHT:
                    massageInfo.setCopilotStatus(value);
                    if(isFrontRightMassageOpen == isOpen && !isOpen){
                        break;
                    }
                    frontRightMassage.setSelected(isOpen);
                    doAnimation(frontRightAnimation, MASSAGE_JSON_PASSENGER[value - 1], isOpen);
                    isFrontRightMassageOpen = isOpen;
                    break;
                case SEAT_ROW_2_LEFT:
                    massageInfo.setRearLeftStatus(value);
                    if(isRearLeftMassageOpen == isOpen && !isOpen){
                        break;
                    }
                    rearLeftMassage.setSelected(isOpen);
                    doAnimation(rearLeftAnimation, MASSAGE_JSON_REAR_LEFT[value - 1], isOpen);
                    isRearLeftMassageOpen = isOpen;
                    break;
                case SEAT_ROW_2_RIGHT:
                    massageInfo.setRearRightStatus(value);
                    if(isRearRightMassageOpen == isOpen && !isOpen){
                        break;
                    }
                    rearRightMassage.setSelected(isOpen);
                    doAnimation(rearRightAnimation, MASSAGE_JSON_REAR_RIGHT[value - 1], isOpen);
                    isRearRightMassageOpen = isOpen;
                    break;
                default:
                    break;
            }
        }
    }

    private void doAnimation(LottieAnimationView animationView, String dir, boolean start){
        if(null == animationView){
            return;
        }
        if(start) {
            animationView.setAnimation(dir);
            if(animationView.isAnimating()){
                animationView.playAnimation();
            }else {
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(animationView, "alpha", 0.0f, 1.0f);
                alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                alphaAnimator.setDuration(500);
                alphaAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationView.playAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                alphaAnimator.start();
            }
        }else{
            animationView.pauseAnimation();
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(animationView, "alpha", 1.0f, 0.0f);
            alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnimator.setDuration(500);
            alphaAnimator.start();
        }
    }

    private void notifyWarning(String message){
        PromptBox.getInstance().showPanel(R.drawable.icon_failure, message);
    }

    private void updateMassageModeView(int areaID, int value) {
        if(hasFrontSeatMassage || hasFrontSeatMassage) {
            if (null != massageSetBox && massageSetBox.isShowing()) {
                massageSetBox.updateModeView(value, areaID);
            }
            switch (areaID) {
                case SEAT_ROW_1_LEFT:
                    massageInfo.setDriverMode(value);
                    break;
                case SEAT_ROW_1_RIGHT:
                    massageInfo.setCopilotMode(value);
                    break;
                case SEAT_ROW_2_LEFT:
                    massageInfo.setRearLeftMode(value);
                    break;
                case SEAT_ROW_2_RIGHT:
                    massageInfo.setRearRightMode(value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mHandler.removeMessages(DISMISS_POPUP);
        mHandler.sendEmptyMessageDelayed(DISMISS_POPUP, 500);
    }

    private final int DISMISS_POPUP = 10001;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DISMISS_POPUP:
                    if(null != seatMask && null != massageSetBox && !massageSetBox.isShowing()){
                        seatMask.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDataChange(BCMInfo data) {
        if(isFrontLeftMassageOpen || isFrontRightMassageOpen || isRearLeftMassageOpen || isRearRightMassageOpen) {
            if (data.getCarPowerStatus() == 3){
                notifyWarning(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.massage_is_off));
                return;
            }
            float batteryVoltage = data.getBatteryVoltage();
            LogUtil.debugI(TAG, "batteryVoltage" + batteryVoltage);
            if(batteryVoltage == 0){
                return;
            }
            if(batteryVoltage < 8.5){
                notifyWarning(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.voltage_low));
            }
            if(batteryVoltage > 16.5){
                notifyWarning(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.voltage_high));
            }
        }
    }
}
