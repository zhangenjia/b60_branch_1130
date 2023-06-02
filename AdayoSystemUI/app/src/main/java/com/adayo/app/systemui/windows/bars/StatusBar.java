package com.adayo.app.systemui.windows.bars;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIConfigs;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.view.WindowsControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.LogUtil;

public class StatusBar implements View.OnTouchListener {
    private volatile static StatusBar mStatusBar;
    private WindowsControllerImpl windowsController;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private RelativeLayout mFloatLayout;

    private boolean isAdded = false;

    private StatusBar() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    public static StatusBar getInstance() {
        if (mStatusBar == null) {
            synchronized (StatusBar.class) {
                if (mStatusBar == null) {
                    mStatusBar = new StatusBar();
                }
            }
        }
        return mStatusBar;
    }

    private void initView() {
        mFloatLayout = (RelativeLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.status_bar, null);
        mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                64,
                WindowManager.LayoutParams.TYPE_STATUS_BAR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT);
        mLayoutParams.token = new Binder();
        mLayoutParams.gravity = Gravity.TOP;
        mLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        mLayoutParams.setTitle(SystemUIContent.STATUS_BAR_SERVICE);
        mLayoutParams.packageName = SystemUIApplication.getSystemUIContext().getPackageName();
        mLayoutParams.windowAnimations = R.style.StatusBarAnimation;
        mFloatLayout.setOnTouchListener(this);
    }

    private void show() {
        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, mLayoutParams);
            isAdded = true;
        }
    }

    public void setVisibility(int visible) {
        if (!SystemUIConfigs.HAS_STATUS_BAR) {
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
        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_STATUS_BAR, visible);
    }

    public int getVisibility() {
        int visible = View.VISIBLE;
        if (null != mFloatLayout) {
            visible = mFloatLayout.getVisibility();
        }
        LogUtil.debugD(SystemUIContent.TAG, visible + "");
        return visible;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.debugD(SystemUIContent.TAG, "x--y onTouchEvent:x=====" + event.getRawX() + "----------y=====" + event.getRawY());
        return WindowsManager.showQsPanel(event);
    }
}
