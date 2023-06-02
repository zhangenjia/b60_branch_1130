package com.adayo.app.systemui.managers.view;

import android.text.TextUtils;
import android.view.View;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.PhoneInfo;
import com.adayo.app.systemui.bean.SystemUISourceInfo;
import com.adayo.app.systemui.bean.ViewStateInfo;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.managers.SystemStatusManager;
import com.adayo.app.systemui.managers.business.PhoneControllerImpl;
import com.adayo.app.systemui.managers.business.SourceControllerImpl;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;

import static com.adayo.app.systemui.configs.SystemUIContent.SYSTEM_PHONE_FREE;
import static com.adayo.app.systemui.configs.SystemUIContent.TYPE_OF_DOCK_BAR;
import static com.adayo.app.systemui.configs.SystemUIContent.TYPE_OF_HVAC_PANEL;
import static com.adayo.app.systemui.configs.SystemUIContent.TYPE_OF_NAVIGATION_BAR;
import static com.adayo.app.systemui.configs.SystemUIContent.TYPE_OF_QS_PANEL;
import static com.adayo.app.systemui.configs.SystemUIContent.TYPE_OF_SCREENT_OFF;
import static com.adayo.app.systemui.configs.SystemUIContent.TYPE_OF_STATUS_BAR;
import static com.adayo.app.systemui.configs.SystemUIContent.TYPE_OF_VOLUME_DIALOG;

public class WindowsControllerImpl extends BaseControllerImpl<ViewStateInfo> implements WindowsController, BaseCallback<SystemUISourceInfo>{
    private volatile static WindowsControllerImpl windowsController;
    private ViewStateInfo viewStateInfo;

    private SourceControllerImpl sourceController;
    private PhoneControllerImpl phoneController;
    private String  currentUISource;

    private WindowsControllerImpl() {
        sourceController = SourceControllerImpl.getInstance();
        sourceController.addCallback(this);
        currentUISource = sourceController.getCurrentUISource();
        phoneController = PhoneControllerImpl.getInstance();
        phoneController.addCallback((BaseCallback<PhoneInfo>) data -> {
            if(SYSTEM_PHONE_FREE != data.getCallState()){
                WindowsManager.setQsPanelVisibility(View.GONE);
                WindowsManager.setHvacPanelVisibility(View.GONE, 0, true, true);
            }
        });
    }

    public static WindowsControllerImpl getInstance() {
        if (windowsController == null) {
            synchronized (WindowsControllerImpl.class) {
                if (windowsController == null) {
                    windowsController = new WindowsControllerImpl();
                }
            }
        }
        return windowsController;
    }

    @Override
    protected boolean registerListener() {
        return true;
    }

    @Override
    protected ViewStateInfo getDataInfo() {
        return viewStateInfo;
    }

    @Override
    public ViewStateInfo getViewState() {
        return viewStateInfo;
    }

    @Override
    public void notifyVisibility(int mode, int visible) {
        if (null == viewStateInfo) {
            viewStateInfo = new ViewStateInfo();
        }
        switch (mode) {
            case TYPE_OF_DOCK_BAR:
                viewStateInfo.setDockBarVisibility(visible);
                break;
            case TYPE_OF_STATUS_BAR:
                viewStateInfo.setStatusBarVisibility(visible);
                break;
            case TYPE_OF_NAVIGATION_BAR:
                viewStateInfo.setNavigationBarVisibility(visible);
                break;
            case TYPE_OF_QS_PANEL:
                viewStateInfo.setQsPanelVisibility(visible);
                break;
            case TYPE_OF_HVAC_PANEL:
                viewStateInfo.setHvacPanelVisibility(visible);
                if (View.VISIBLE == visible) {
                    WindowsManager.setQsPanelVisibility(View.GONE);
                    if(viewStateInfo.getScreenVisibility() == View.VISIBLE){
                        SystemStatusManager.getInstance().setScreenOn();
                    }
                }
                break;
            case TYPE_OF_VOLUME_DIALOG:
                viewStateInfo.setVolumeDialogVisibility(visible);
                break;
            case TYPE_OF_SCREENT_OFF:
                viewStateInfo.setScreenVisibility(visible);
                break;
            default:
                break;
        }
        LogUtil.debugD(SystemUIContent.TAG, "viewStateInfo.getQsPanelVisibility() = " + viewStateInfo.getQsPanelVisibility());
        LogUtil.debugD(SystemUIContent.TAG, "viewStateInfo.getHvacPanelVisibility() = " + viewStateInfo.getHvacPanelVisibility());
//        WindowsManager.setDropdownViewVisibility(View.VISIBLE == viewStateInfo.getQsPanelVisibility()
//                || View.VISIBLE == viewStateInfo.getHvacPanelVisibility() ? View.GONE : View.VISIBLE);
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    @Override
    public void onDataChange(SystemUISourceInfo data) {
        if(null == data || TextUtils.isEmpty(data.getUiSource())){
            return;
        }
//        if(AdayoSource.ADAYO_SOURCE_RVC.equals(data.getUiSource()) || AdayoSource.ADAYO_SOURCE_AVM.equals(data.getUiSource()) || AdayoSource.ADAYO_SOURCE_APA.equals(data.getUiSource())){
//
//        }
        if(TextUtils.isEmpty(currentUISource) || !currentUISource.equals(data.getUiSource())) {
            currentUISource = data.getUiSource();
            if (null == viewStateInfo) {
                viewStateInfo = new ViewStateInfo();
            }
            if(viewStateInfo.getQsPanelVisibility() == View.VISIBLE) {
                WindowsManager.setQsPanelVisibility(View.GONE);
            }
            if(viewStateInfo.getHvacPanelVisibility() == View.VISIBLE) {
                WindowsManager.setHvacPanelVisibility(View.GONE, 0, true, true);
            }
        }
    }
}
