package com.adayo.app.setting.manager;

import android.os.Bundle;

import com.adayo.app.base.ViewStubBase;

import java.util.List;

public class bindDataHandlerAbstract extends AbstractLifeHandler {
    private Bundle arguments;
    private Bundle savedInstanceState;

    public bindDataHandlerAbstract(Bundle arguments, Bundle savedInstanceState) {
        this.arguments = arguments;
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected void handleLeave(List<ViewStubBase> leave) {
        for (ViewStubBase viewStubBase : leave) {
            viewStubBase.bindData(arguments, savedInstanceState);
        }
    }
}
