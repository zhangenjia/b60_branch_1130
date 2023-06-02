package com.adayo.app.btphone;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.adayo.app.btphone.service.BlueToothCallService;
import com.adayo.bpresenter.bluetooth.services.LocalBluetoothService;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;


public class BTApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startBluetoothService();
        startBluetoothCallService();
        AAOP_HSkinHelper
                .init(getApplicationContext(), "AdayoBtPhone");
    }

    private void startBluetoothService(){
        Log.i("BTApplication","startBluetoothService");
        try{
            Intent intent = new Intent(this, LocalBluetoothService.class);
            intent.setAction("com.adayo.bluetooth.hfp.action.LOCAL_BLUETOOTH_SERVICE");
            startService(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startBluetoothCallService(){
        try{
            Intent intent = new Intent(this, BlueToothCallService.class);
            intent.setAction("com.adayo.bluetooth.action.LOCAL_BLUETOOTH_SERVICE");
            startService(intent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
