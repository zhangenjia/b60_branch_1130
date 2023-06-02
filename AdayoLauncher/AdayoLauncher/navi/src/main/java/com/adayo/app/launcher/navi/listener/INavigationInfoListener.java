package com.adayo.app.launcher.navi.listener;

import com.adayo.app.launcher.navi.bean.NaviBean;


public interface INavigationInfoListener {
    boolean onReportGuideInfo(NaviBean navibean);
    void oReportNaviGuideStatus(int status);
}
