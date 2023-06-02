package com.adayo.btsetting.viewmodel;

import android.content.Context;

import com.lt.library.util.context.ContextUtil;

/**
 * @author Y4134
 */
public class BaseRequest {

    public Context getAppContext() {
        return ContextUtil.getInstance().getApplicationContext();
    }

}
