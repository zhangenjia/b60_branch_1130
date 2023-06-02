package com.adayo.app.setting.manager;

import com.adayo.app.base.ViewStubBase;

import java.util.List;

public class onCreateAbstractLifeHandler extends AbstractLifeHandler {
    public onCreateAbstractLifeHandler() {
        super();
    }

    @Override
    protected void handleLeave(List<ViewStubBase> leave) {
        for (ViewStubBase viewStubBase : leave) {
            viewStubBase.initView();
            viewStubBase.initData();
            viewStubBase.initEvent();
        }
    }
}
