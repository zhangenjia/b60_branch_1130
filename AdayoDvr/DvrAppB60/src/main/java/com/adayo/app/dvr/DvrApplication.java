package com.adayo.app.dvr;

import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adayo.app.dvr.controller.DvrController;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;

public class DvrApplication extends Application {

    private static final String TAG = APP_TAG + DvrApplication.class.getSimpleName();
    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mContext = getApplicationContext();
        CrashHandler carshHandler = CrashHandler.getInstance();
        carshHandler.init(mContext);
        //换肤初始化
        AAOP_HSkinHelper.init(mContext,true,"AdayoDvr");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AAOP_HSkin.getInstance().refreshLanguage();
    }
}
