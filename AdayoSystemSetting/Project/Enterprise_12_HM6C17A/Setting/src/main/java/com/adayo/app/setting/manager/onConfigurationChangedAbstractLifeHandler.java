package com.adayo.app.setting.manager;

import android.content.res.Configuration;

import com.adayo.app.base.ViewStubBase;

import java.util.List;

public class onConfigurationChangedAbstractLifeHandler extends AbstractLifeHandler {

    private Configuration newConfig;

    public onConfigurationChangedAbstractLifeHandler(Configuration newConfig) {
        this.newConfig = newConfig;
    }

    @Override
    protected void handleLeave(List<ViewStubBase> leave) {
        for (ViewStubBase viewStubBase : leave) {
          viewStubBase.onConfigurationChanged(newConfig);
        }

    }
}
