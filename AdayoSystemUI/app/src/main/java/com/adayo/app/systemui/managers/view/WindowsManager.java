package com.adayo.app.systemui.managers.view;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.business.SourceControllerImpl;
import com.adayo.app.systemui.windows.bars.NavigationBar;
import com.adayo.app.systemui.windows.bars.StatusBar;
import com.adayo.app.systemui.windows.dialogs.VolumeDialog;
import com.adayo.app.systemui.windows.panels.HvacPanel;
import com.adayo.app.systemui.windows.panels.QsPanel;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;

import static com.adayo.app.systemui.configs.HvacContent.HVAC_FRONT;

public class WindowsManager {
    private static StatusBar statusBar;
    private static NavigationBar navigationBar;
    private static QsPanel qsPanel;
    private static VolumeDialog volumeDialog;
    private static HvacPanel hvacPanel;


    private static final int DISMISS_HVAC_PANEL = 10001;
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DISMISS_HVAC_PANEL:
                    setHvacPanelVisibility(View.GONE, HVAC_FRONT, true, true);
                    break;
                default:
                    break;
            }
        }
    };

    public static void resetHvacHandler(boolean reset) {
        mHandler.removeMessages(DISMISS_HVAC_PANEL);
        if(reset) {
            mHandler.sendEmptyMessageDelayed(DISMISS_HVAC_PANEL, SystemUIContent.HVAC_DISMISS_DELAYED);
        }
    }

    public static void setStatusBarVisibility(int visible) {
        if (null == statusBar) {
            statusBar = StatusBar.getInstance();
//            qsPanel = QsPanel.getInstance();
//            hvacPanel = HvacPanel.getInstance();
        }
        statusBar.setVisibility(visible);
    }

    public static void initPanel() {
            qsPanel = QsPanel.getInstance();
            hvacPanel = HvacPanel.getInstance();
    }

//    public static void setDropdownViewVisibility(int visible) {
//        if (null == statusBar) {
//            statusBar = StatusBar.getInstance();
//        }
//        statusBar.setDropdownViewVisibility(visible);
//    }

    public static void setNavigationBarVisibility(int visible) {
        if (null == navigationBar) {
            navigationBar = NavigationBar.getInstance();
        }
        navigationBar.setVisibility(visible);
    }

    public static void setNavigationBarArea(int area) {
        if (null == navigationBar) {
            navigationBar = NavigationBar.getInstance();
        }
        navigationBar.viewChange(area);
    }

    public static void setDockBarVisibility(int visible) {

    }

    public static boolean showQsPanel(MotionEvent event){
        if (null == qsPanel) {
            qsPanel = QsPanel.getInstance();
        }
        return qsPanel.show(event);
    }
    public static void dismissQsPanel(){
        if (null == qsPanel) {
            qsPanel = QsPanel.getInstance();
        }
        qsPanel.dismiss();
    }

    public static void setQsPanelVisibility(int visible) {
        String currentUISource = SourceControllerImpl.getInstance().getCurrentUISource();
        if(visible == View.VISIBLE && (AdayoSource.ADAYO_SOURCE_AVM.equals(currentUISource) || "ADAYO_SOURCE_APA".equals(currentUISource) || AdayoSource.ADAYO_SOURCE_RVC.equals(currentUISource))){
            return;
        }
        if (null == qsPanel) {
            qsPanel = QsPanel.getInstance();
        }
        qsPanel.setVisibility(visible);
    }

    public static void setHvacPanelVisibility(int visible, int area, boolean isFromUser, boolean needAdmin) {
        String currentUISource = SourceControllerImpl.getInstance().getCurrentUISource();
        if(visible == View.VISIBLE && (AdayoSource.ADAYO_SOURCE_RVC.equals(currentUISource) ||
                AdayoSource.ADAYO_SOURCE_AVM.equals(currentUISource) || AdayoSource.ADAYO_SOURCE_APA.equals(currentUISource))){
            return;
        }
        if(null == hvacPanel){
            hvacPanel = HvacPanel.getInstance();
        }
        hvacPanel.setVisibility(visible, area, isFromUser, needAdmin);
    }

    public static boolean showHvacPanel(MotionEvent event, int area){
        if(null == hvacPanel){
            hvacPanel = HvacPanel.getInstance();
        }
        return hvacPanel.doEvent(event, area);
    }

    public static void setVolumeDialogVisibility(int type, int volume, int visible) {
        if (null == volumeDialog) {
            volumeDialog = VolumeDialog.getInstance();
        }
        if (View.VISIBLE == visible) {
            volumeDialog.showVolumePanel(type, volume);
        } else if (View.GONE == visible) {
            volumeDialog.dismiss();
        }
    }

    public static void setScreenViewVisibility(int visible) {

    }

    public static void setScreenViewVisibility(int visible, int screenType) {

    }
}
