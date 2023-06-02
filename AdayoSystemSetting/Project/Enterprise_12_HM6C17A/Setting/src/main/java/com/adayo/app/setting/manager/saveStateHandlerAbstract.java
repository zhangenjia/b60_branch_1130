package com.adayo.app.setting.manager;

import android.os.Bundle;

import com.adayo.app.base.ViewStubBase;

import java.util.List;

public class saveStateHandlerAbstract extends AbstractLifeHandler {
    private Bundle outState;

    public saveStateHandlerAbstract(Bundle outState) {
        this.outState = outState;
    }

    @Override
    protected void handleLeave(List<ViewStubBase> leave) {
        for (ViewStubBase viewStubBase : leave) {
            viewStubBase.saveState(outState);
        }
    }
}
