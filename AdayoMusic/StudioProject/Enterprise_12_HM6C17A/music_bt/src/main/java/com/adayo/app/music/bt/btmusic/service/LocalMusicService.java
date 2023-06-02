package com.adayo.app.music.bt.btmusic.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.adayo.app.music.bt.btmusic.manager.BTMusicLastSourceManager;
import com.adayo.app.music.bt.btmusic.util.SPUtils;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;


public class LocalMusicService extends Service {

    private static final String TAG = LocalMusicService.class.getSimpleName();
    private Context mContext;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        if (intent != null) {
            String retStr = intent.getStringExtra("lastMode");
            String modeName = intent.getStringExtra("lastModeName");
            boolean isNeedPlay = SPUtils.isNeedPlay(mContext);
            Log.d(TAG, "onStartCommand: retStr = " + retStr + " modeName = " + modeName + " isNeedPlay = " + isNeedPlay);
            if ("Start".equals(retStr)) {
                if (AdayoSource.ADAYO_SOURCE_BT_AUDIO.equals(modeName) && isNeedPlay) {
                    BTMusicLastSourceManager.getInstance(getApplicationContext());
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
