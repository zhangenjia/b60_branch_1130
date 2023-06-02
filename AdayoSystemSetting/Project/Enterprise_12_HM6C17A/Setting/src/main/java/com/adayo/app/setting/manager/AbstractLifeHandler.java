package com.adayo.app.setting.manager;

import com.adayo.app.base.LogUtil;
import com.adayo.app.base.ViewStubBase;

import java.util.List;

public abstract class AbstractLifeHandler {
    private AbstractLifeHandler nextHandler;


    public void setNextHandler(AbstractLifeHandler nextHandler) {
        LogUtil.d(this.getClass().getName(),"nextHandler "+nextHandler);
        if (this.nextHandler != null) {
            this.nextHandler.setNextHandler(nextHandler);
        } else {
            this.nextHandler = nextHandler;
        }
    }

    protected abstract void handleLeave(List<ViewStubBase> leave);

    public final void submit(List<ViewStubBase> leave) {
        LogUtil.d(this.getClass().getName(), "nextHandler =" + nextHandler);
        if (this.nextHandler != null) {
            this.handleLeave(leave);
            this.nextHandler.submit(leave);
        } else {
            this.handleLeave(leave);
        }
    }
}

