package com.adayo.app.setting.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BtPairOperationReceiver extends BroadcastReceiver {
    private String TAG = BtPairOperationReceiver.class.getSimpleName();
    public String BT_PAIR_OPERATION_BROADCAST = "com.adayo.bt.pair.operation";
    private IPairOperationListener mOperListener = null;

    public BtPairOperationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(this.TAG, "receive broadcast. action=" + action);
        if (action.equals(this.BT_PAIR_OPERATION_BROADCAST) && this.mOperListener != null) {
            int operation = intent.getIntExtra("operation", 0);
            this.mOperListener.notifyPairOperaton(operation);
        }
    }

    public void addPairOperationListener(IPairOperationListener listener) {
        this.mOperListener = listener;
    }

    public interface IPairOperationListener {
        void notifyPairOperaton(int var1);
    }
}
