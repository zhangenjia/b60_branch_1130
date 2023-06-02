package com.adayo.app.setting.base;

import android.content.Context;

import com.lt.library.util.context.ContextUtil;

public class BaseRequest {

    public Context getAppContext() {
        return ContextUtil.getInstance().getApplicationContext();
    }

}
