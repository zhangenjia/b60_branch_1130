package com.adayo.app.crossinf;

import android.util.Log;

import androidx.annotation.LongDef;

public class ActivityManager {
    private static final String TAG = "ActivityManager__";
    private static ActivityManager mActivityManager;
    private boolean isVisibility;

    public static ActivityManager getInstance() {
        if (null == mActivityManager) {
            synchronized (ActivityImpController.class) {
                if (null == mActivityManager) {
                    mActivityManager = new ActivityManager();
                }
            }
        }
        return mActivityManager;
    }

    public void setAppVisibility(boolean isVisibility){
        Log.d(TAG, "setAppVisibility: "+isVisibility);
        this.isVisibility = isVisibility;
    }

    public boolean getAppVisibility(){
        Log.d(TAG, "getAppVisibility: "+isVisibility);
        return isVisibility;
    }
}
