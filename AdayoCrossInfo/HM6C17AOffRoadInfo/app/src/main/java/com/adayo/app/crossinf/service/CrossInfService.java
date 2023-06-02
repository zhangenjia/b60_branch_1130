package com.adayo.app.crossinf.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.adayo.app.crossinf.presenter.CanDataCallbcak;


public class CrossInfService extends Service {
    private static final String TAG = CrossInfService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        CanDataCallbcak.getControl().init_McuComm();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
