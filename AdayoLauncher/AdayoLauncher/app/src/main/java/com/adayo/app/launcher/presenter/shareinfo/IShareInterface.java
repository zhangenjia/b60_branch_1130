package com.adayo.app.launcher.presenter.shareinfo;

import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_BT_AUDIO;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_NULL;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_USB;

import android.util.Log;

import com.adayo.app.launcher.util.MyConstantsUtil;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import warning.LauncherApplication;

public class IShareInterface implements IShareDataListener {

    private static final String TAG = "launcherIShareInterface";
    private boolean isFirst = true;

    @Override
    public void notifyShareData(int dataType, String data) {
        if (dataType == 14) {
            Gson gson = new GsonBuilder().create();
            Map<String, String> pDate = gson.fromJson(data, Map.class);
            Log.d(TAG, "notifyShareData: pDate = "+pDate+"  data = "+data);
            if ((data == null) || (null == gson) || (null == pDate)) {
                return;
            }
            String audioID = pDate.get("AudioID");
            if (isFirst) {
                if (audioID.equals(ADAYO_SOURCE_USB) || audioID.equals(ADAYO_SOURCE_BT_AUDIO)) {
                    MyConstantsUtil.CURRENT_AUDIOID = audioID;
                } else if (audioID.equals(ADAYO_SOURCE_NULL)) {
                    MyConstantsUtil.CURRENT_AUDIOID = ADAYO_SOURCE_BT_AUDIO;
                }
                isFirst = false;
            } else {
                if (audioID.equals(ADAYO_SOURCE_USB) || audioID.equals(ADAYO_SOURCE_BT_AUDIO)) {
                    MyConstantsUtil.CURRENT_AUDIOID = audioID;
                }
            }
            Log.d(TAG, "notifyShareData: " + audioID);
        }
    }
}