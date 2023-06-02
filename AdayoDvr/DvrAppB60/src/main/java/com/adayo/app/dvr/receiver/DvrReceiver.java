package com.adayo.app.dvr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.adayo.app.dvr.DvrService;


public class DvrReceiver extends BroadcastReceiver {
    private final static String TAG = DvrReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        final String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action))
        {
            Intent intent1 = new Intent(context.getApplicationContext(), DvrService.class);
            context.getApplicationContext().startService(intent1);
        }
    }
}
