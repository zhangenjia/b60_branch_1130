package com.adayo.app.ats;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.adayo.app.ats.view.RoundLightBarView;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

import static com.adayo.app.ats.util.Constants.ATS_VERSION;

public class MyApplication extends Application {
    private static final String TAG = ATS_VERSION + MyApplication.class.getSimpleName();
private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        initSkinChange();
         context = getApplicationContext();
    }

    private void initSkinChange() {

        AAOP_HSkinHelper
                .init(getApplicationContext(), true, "AdayoAts");
    }
    public static Context getAtsAppContext(){

        return context;
    }

}