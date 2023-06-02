package com.adayo.btsetting.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adayo.btsetting.model.BtSettingManager;

/**
 * @author Y4134
 */
public class BtHutPairReceiver extends BroadcastReceiver {
    private static final String TAG = BtHutPairReceiver.class.getSimpleName();
    public static final String BT_HUT_PAIR = "com.adayo.bt.hut.pair";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "receive broadcast. action=" + action);
        if (BT_HUT_PAIR.equals(action) ) {
            String address = intent.getStringExtra("address");
            BtSettingManager.getInstance(context).reqPair(address);
        }

    }
}
