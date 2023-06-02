package com.adayo.app.setting;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.configuration.ConfigurationManager;
import com.adayo.app.setting.base.AsyncLayoutInflatePlus;
import com.adayo.app.setting.base.HarmanDrawableAttrHandler;
import com.adayo.app.setting.base.kswBackDrawable;
import com.adayo.app.setting.skin.SegmentTabLayoutSkin;
import com.adayo.btsetting.ContextManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;


public class BaseApplication extends Application {
    private final static String TAG = BaseApplication.class.getSimpleName();
    private Context mContext;


    @Override
    public void onCreate() {
        LogUtil.d(TAG, "profiler");
        mContext = this;
LogUtil.debugD(TAG, "");
        super.onCreate();
        ConfigurationManager.getInstance().init(this);
        AAOP_HSkin.getInstance().registerSkinAttrHandler("kswBackDrawable", new kswBackDrawable());
        AAOP_HSkin.getInstance().registerSkinAttrHandler("harmanDrawable", new HarmanDrawableAttrHandler());
        SegmentTabLayoutSkin tlthumbdrawable =  new SegmentTabLayoutSkin();
        AAOP_HSkin.getInstance().registerSkinAttrHandler("thumb_drawable", tlthumbdrawable);
        AAOP_HSkin.getInstance().registerSkinAttrHandler("textSelectColor", tlthumbdrawable);


        AAOP_HSkinHelper
                .init(getApplicationContext(), "AdayoSystemSetting");

        AsyncLayoutInflatePlus asyncLayoutInflatePlus = new AsyncLayoutInflatePlus(getApplicationContext());
        AAOP_HSkin.getLayoutInflater(asyncLayoutInflatePlus.getInflater());
        asyncLayoutInflatePlus.inflate(R.layout.activity_main, null, new AsyncLayoutInflatePlus.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent) {
                LogUtil.d(TAG, "AsyncLayoutInflater activity_main");
                ConfigurationManager.getInstance().setView(view);
            }
        });

        ConfigurationManager.getInstance().getConfiguration();

        ContextManager.getInstance().getResponse().response(mContext);
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.adayo.app.setting", "com.adayo.app.setting.SettingAppService");
        intent.setComponent(componentName);
        boolean bBindSuccess = bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, " onCreate() End bBindSuccess = " + bBindSuccess);


}


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected()");
        }
    };

}
