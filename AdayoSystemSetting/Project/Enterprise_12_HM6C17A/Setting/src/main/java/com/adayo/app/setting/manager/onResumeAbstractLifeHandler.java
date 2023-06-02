package com.adayo.app.setting.manager;

import com.adayo.app.base.ViewStubBase;

import java.util.List;

public class onResumeAbstractLifeHandler extends AbstractLifeHandler {

    @Override
    protected void handleLeave(List<ViewStubBase> leave) {
        for (ViewStubBase viewStubBase : leave) {
            viewStubBase.onResume();
        }
    }
}
