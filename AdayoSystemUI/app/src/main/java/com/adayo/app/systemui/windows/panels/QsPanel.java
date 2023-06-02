package com.adayo.app.systemui.windows.panels;

import android.app.Dialog;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.view.WindowsControllerImpl;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.ConfigUtils;
import com.adayo.app.systemui.windows.views.PullDownDumperLayout;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class QsPanel extends Dialog {
    private volatile static QsPanel mQsPanel;
    private WindowsControllerImpl windowsController;
    private PullDownDumperLayout mLayout;

    private QsPanel() {
        super(SystemUIApplication.getSystemUIContext(), R.style.TransparentStyle);
        initView();
    }

    public static QsPanel getInstance() {
        if (mQsPanel == null) {
            synchronized (QsPanel.class) {
                if (mQsPanel == null) {
                    mQsPanel = new QsPanel();
                }
            }
        }
        return mQsPanel;
    }

    private void initView() {
        AAOP_HSkin.getLayoutInflater(getContext());
        Window window = getWindow();
        window.setGravity(Gravity.TOP);
        window.setType(WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = 1920;
        layoutParams.height = 964;
        window.setAttributes(layoutParams);
        if(ConfigUtils.isLow()) {
            setContentView(R.layout.qs_panel_low);
        }else{
            setContentView(R.layout.qs_panel_high);
        }
        mLayout = findViewById(R.id.layout);
        AAOP_HSkin.getWindowViewManager().addWindowView(mLayout).applySkinForViews(true);
        AAOP_HSkin.with(findViewById(R.id.background_layout)).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.bg_blur).applySkin(false);
    }

    @Override
    public void show() {
        if(null == windowsController){
            windowsController = WindowsControllerImpl.getInstance();
        }
        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_QS_PANEL, View.VISIBLE);
        super.show();
    }

    @Override
    public void dismiss() {
        if(null == windowsController){
            windowsController = WindowsControllerImpl.getInstance();
        }
        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_QS_PANEL, View.GONE);
        super.dismiss();
    }

    public void setVisibility(int visible) {
        if(null != mLayout) {
            if (View.VISIBLE == visible) {
                mLayout.startOpenAnimation();
            } else {
                mLayout.startCloseAnimation();
            }
        }
    }

    public boolean show(MotionEvent event) {
        boolean fromOutside = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(104 >= event.getRawY()) {
                    WindowsManager.setHvacPanelVisibility(View.GONE, 0, true, true);
                    show();
                    fromOutside = true;
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                return false;
        }
        return mLayout.doAnimation(event, fromOutside);
    }
}
