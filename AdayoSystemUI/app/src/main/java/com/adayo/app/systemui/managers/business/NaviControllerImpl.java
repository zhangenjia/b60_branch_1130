package com.adayo.app.systemui.managers.business;

import com.adayo.app.systemui.bases.BaseControllerImpl;
import com.adayo.app.systemui.bean.NaviInfo;

public class NaviControllerImpl extends BaseControllerImpl<NaviInfo> implements NaviController {
    private volatile static NaviControllerImpl naviController;
    private NaviInfo naviInfo;

    private NaviControllerImpl() {
    }

    public static NaviControllerImpl getInstance() {
        if (naviController == null) {
            synchronized (NaviControllerImpl.class) {
                if (naviController == null) {
                    naviController = new NaviControllerImpl();
                }
            }
        }
        return naviController;
    }
    @Override
    public int getNaviStatus() {
        if(null != naviInfo){
            return naviInfo.getNaviStatus();
        }
        return 0;
    }

    @Override
    public void notifyNaviStatus(int status) {
        if(null == naviInfo){
            naviInfo = new NaviInfo();
        }
        naviInfo.setNaviStatus(status);
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    @Override
    protected boolean registerListener() {
        return true;
    }

    @Override
    protected NaviInfo getDataInfo() {
        return naviInfo;
    }
}
