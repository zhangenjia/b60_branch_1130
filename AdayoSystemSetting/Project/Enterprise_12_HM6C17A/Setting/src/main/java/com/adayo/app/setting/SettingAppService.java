package com.adayo.app.setting;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.model.data.request.sub.SettingAppHotspotState;
import com.adayo.btsetting.receiver.BtHutPairReceiver;
import com.adayo.btsetting.sdk.BtSdkManager;

public class SettingAppService extends Service {

    private static final String TAG = SettingAppService.class.getSimpleName();


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.i(TAG);
        super.onCreate();
        initBroadCast();
        initBTSdk();
        initHotspotSTATE();
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BtHutPairReceiver.BT_HUT_PAIR);
        BtHutPairReceiver receiver = new BtHutPairReceiver();
        registerReceiver(receiver, filter);
    }

    private void initBTSdk() {
        BtSdkManager btsdkManager = new BtSdkManager();
        btsdkManager.init(this);
    }


    private void initHotspotSTATE() {
        LogUtil.i(TAG);
        new SettingAppHotspotState(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG);
    }
}
