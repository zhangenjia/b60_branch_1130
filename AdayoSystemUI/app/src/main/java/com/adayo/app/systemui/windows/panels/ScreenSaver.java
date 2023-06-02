package com.adayo.app.systemui.windows.panels;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.bases.ScreenCallback;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.SystemStatusManager;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.windows.views.ScreenClockView;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;

import static com.adayo.app.systemui.configs.SystemUIContent.POWER_TYPE_CLOCK;
import static com.adayo.app.systemui.configs.SystemUIContent.POWER_TYPE_LOGO;

public class ScreenSaver implements View.OnTouchListener {
    private volatile static ScreenSaver screenSaver;
    private ScreenCallback screenCallback;
    private int systemStatus;
    private RelativeLayout mFloatLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private ImageView logo;
    private ScreenClockView clockView;

    private boolean isAdded = false;

    private ScreenSaver() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    public static ScreenSaver getInstance() {
        if (screenSaver == null) {
            synchronized (ScreenSaver.class) {
                if (screenSaver == null) {
                    screenSaver = new ScreenSaver();
                }
            }
        }
        return screenSaver;
    }

    private void initView() {
        mFloatLayout = (RelativeLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.screen_view, null);
        mLayoutParams = new WindowManager.LayoutParams(2081);
        mLayoutParams.width = 1920;
        mLayoutParams.height = 1080;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        mLayoutParams.setTitle("ScreenOff");
        mLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        mLayoutParams.gravity = Gravity.END;
        mFloatLayout.setOnTouchListener(this);
        logo = mFloatLayout.findViewById(R.id.logo_icon);
        clockView = mFloatLayout.findViewById(R.id.clock_time);
    }

    public void show(int type) {
        LogUtil.debugD(SystemUIContent.TAG, "System Status = " + type);
        systemStatus = type;
        if (systemStatus == SystemUIContent.SCREEN_POWER_OFF) {
            switch (getStandbyDisplayMode()) {
                case POWER_TYPE_CLOCK:
                    logo.setVisibility(View.GONE);
                    clockView.setVisibility(View.VISIBLE);
                    clockView.setShowing(true);
                    break;
                case POWER_TYPE_LOGO:
                    logo.setVisibility(View.VISIBLE);
                    clockView.setVisibility(View.GONE);
                    clockView.setShowing(false);
                    break;
                default:
                    logo.setVisibility(View.GONE);
                    clockView.setVisibility(View.GONE);
                    break;
            }
        } else {
            logo.setVisibility(View.GONE);
            clockView.setVisibility(View.GONE);
        }
        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, mLayoutParams);
            isAdded = true;
        }
        setVisibility(true);
    }

    public void dismiss() {
        LogUtil.debugD(SystemUIContent.TAG, "dismiss");
        setVisibility(false);
    }

    private void setVisibility(boolean visibility) {
        LogUtil.debugD(SystemUIContent.TAG, visibility + "");
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    public void setScreenCallback(ScreenCallback screenCallback) {
        this.screenCallback = screenCallback;
    }

    private int getStandbyDisplayMode() {
        return AAOP_DeviceServiceManager.getInstance().getStandbyDisplayMode();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (SystemUIContent.SCREEN_OFF == systemStatus) {
            if (null != screenCallback) {
                screenCallback.setScreenOn();
            }
            dismiss();
            return true;
        }
//        if (SystemUIContent.SCREEN_POWER_OFF == systemStatus) {
//            SystemStatusManager.getInstance().setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS.AAOP_SCREEN_ON);
//            dismiss();
//            return true;
//        }
        return false;
    }
}
