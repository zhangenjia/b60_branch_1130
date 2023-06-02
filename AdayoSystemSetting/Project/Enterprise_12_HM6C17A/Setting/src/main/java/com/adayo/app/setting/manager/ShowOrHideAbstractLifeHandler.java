package com.adayo.app.setting.manager;

import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;

import java.util.List;

public class ShowOrHideAbstractLifeHandler extends AbstractLifeHandler {
    boolean isShow;
    public ShowOrHideAbstractLifeHandler(boolean isShow) {
        this.isShow = isShow;
    }
    @Override
    protected void handleLeave(List<ViewStubBase> leave) {
        for (ViewStubBase viewStubBase : leave) {
            viewStubBase.showOrHide(isShow);
            LogUtil.debugD("ShowOrHideLifeHandler",viewStubBase.getClass().getName());
        }
    }
}
