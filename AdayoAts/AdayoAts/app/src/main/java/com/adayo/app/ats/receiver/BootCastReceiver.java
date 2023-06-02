package com.adayo.app.ats.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adayo.app.ats.AtsService;

import static com.adayo.app.ats.util.Constants.ATS_VERSION;

public class BootCastReceiver extends BroadcastReceiver {
    private static final String TAG = ATS_VERSION + BootCastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent i) {

        if ("android.intent.action.BOOT_COMPLETED".equals(i.getAction())) {
            Log.d(TAG, "android.intent.action.BOOT_COMPLETED, start AtsService ");
            Intent intent = new Intent(context, AtsService.class);
            context.startService(intent);
        }

    }
}