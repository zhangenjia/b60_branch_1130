package com.adayo.app.crossinf.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.adayo.app.crossinf.service.CrossInfService;
import java.lang.reflect.Method;

public class BroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = BroadCastReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent ){
//        接收开机广播，启动meterservice
        Log.d(TAG, "onReceive: ");
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
//            setProperty("sys.usb.config","none");
//            setProperty("usb.carplay.state","in");
//            setProperty("sys.usb.config","adb");
            startService(context);
        }

    }
    private void startService(Context context){
        Log.d(TAG, "startService: ");
        Intent intent = new Intent(context, CrossInfService.class);
        intent.setAction("com.adayo.app.crossinf.service.CrossInfService");
        context.startService(intent);
    }
    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
