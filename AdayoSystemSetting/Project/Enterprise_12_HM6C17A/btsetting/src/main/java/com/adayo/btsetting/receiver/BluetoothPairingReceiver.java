/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adayo.btsetting.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.adayo.component.log.Trace;

/**
 * @author Y4134
 */
public final class BluetoothPairingReceiver extends BroadcastReceiver {

    private final String TAG = "BluetoothPairingReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Trace.i(TAG, "----onReceive() action=" + action);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            int bondState = device.getBondState();
            switch (bondState) {
                //配对失败
                case BluetoothDevice.BOND_NONE:
                    Log.d(TAG, "onReceive: BOND_NONE");
                    break;
                //正在配对
                case BluetoothDevice.BOND_BONDING:
                    Log.d(TAG, "onReceive: BOND_BONDING");
                    break;
                //配对成功
                case BluetoothDevice.BOND_BONDED:
                    Log.d(TAG, "onReceive: BOND_BONDED");
                    break;
                default:break;
            }
        }
    }
}