package com.adayo.app.crossinf;

import android.util.Log;
import android.view.View;

public class ActivityContentViewManager {

    private static final String TAG = ActivityContentViewManager.class.getSimpleName();
    private View mMainActivityView;
    private MainActivity mActivity;

    public void setView(View view) {
        Log.d(TAG, "view = "+view);
        mMainActivityView=view;
        if(mActivity!=null){
            mActivity.initMainInflate(view);
        }
    }

    public void setActivity(MainActivity activity) {
        Log.d(TAG,"mView ="+mMainActivityView);
        mActivity = activity;
        if(mMainActivityView!=null){
            Log.d(TAG,"NOT RIGHY");
            mActivity.initMainInflate(mMainActivityView);
        }
    }

    private static class ConfigurationManagerHolder {
        private static final ActivityContentViewManager INSTANCE = new ActivityContentViewManager();
    }
    public static ActivityContentViewManager getInstance() {
        return ConfigurationManagerHolder.INSTANCE;
    }

}
