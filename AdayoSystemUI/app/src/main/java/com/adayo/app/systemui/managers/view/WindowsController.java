package com.adayo.app.systemui.managers.view;

import com.adayo.app.systemui.bean.ViewStateInfo;

public interface WindowsController {
    void notifyVisibility(int mode, int visible);
//    void setStatusBarVisibility(int visible);
//    void setNavigationBarVisibility(int visible);
//    void setDockBarVisibility(int visible);
//    void setQsPanelVisibility(int visible);
//    void setHvacPanelVisibility(int visible);
//    void setVolumeDialogVisibility(int visible);
//    void setScreenViewVisibility(int visible);
////    void setScreenViewVisibility(int visible, int screenType);
//    void hideAllPanel();
//    void setConfigurationChanged();
    ViewStateInfo getViewState();
}
