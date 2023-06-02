package com.adayo.app.crossinf;

import static com.adayo.app.crossinf.util.Constant.CROSSINF;
import static com.adayo.app.crossinf.util.Constant.WADINGDETECTION;
import static com.adayo.app.crossinf.util.Constant.setProperty;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType.SourceType.UI;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.adayo.app.crossinf.presenter.CanDataCallbcak;
import com.adayo.app.crossinf.presenter.CarSettingManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application  {
    private static final String TAG = "CrossInfo_" + MyApplication.class.getSimpleName();
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getBaseContext();
        AsyncLayoutInflatePlus asyncLayoutInflatePlus = new AsyncLayoutInflatePlus(getApplicationContext());
        asyncLayoutInflatePlus.inflate(R.layout.activity_main, null, new AsyncLayoutInflatePlus.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent) {
                ActivityContentViewManager.getInstance().setView(view);
                initData();
            }
        });

    }

    private void initData(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                CanDataCallbcak.getControl().init_McuComm();//车轮数据
                CarSettingManager.getInstance();//初始化监听数据
            }
        }).start();

    }

    public static Context getContext() {
        return context;
    }



}
