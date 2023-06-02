package com.adayo.app.music.bt.btmusic.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

import java.util.HashMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();

    public static void startBTSetting() {
        Log.d(TAG, "startBTSetting: ");
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("setting_page", "bt_music");
            startApp(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    public void startBTSetting() {
//        Trace.i(TAG, "----startBTSetting()----");
//        Map<String, String> map = new HashMap();
//        map.put("pageType", "btSettings");
//        this.mSrcMngSwitchProxy.requestSwitchApp(new SourceInfo("ADAYO_SOURCE_SETTING", map, AppConfigType.SourceSwitch.APP_ON.getValue(), AppConfigType.SourceType.UI.getValue()));
//    }


    public static void startEQSetting(Context context) {
        Log.d(TAG, "startEQSetting: ");
        try {
            Intent intent = new Intent();
            intent.setPackage("com.adayo.app.setting");
            intent.setAction("adayo.setting.intent.action.EQ");
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startApp(HashMap<String, String> map) {
        try {
            SourceInfo info = new SourceInfo(AdayoSource.ADAYO_SOURCE_SETTING, map,
                    AppConfigType.SourceSwitch.APP_ON.getValue(),
                    AppConfigType.SourceType.UI.getValue());
            SrcMngSwitchManager.getInstance().requestSwitchApp(info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
