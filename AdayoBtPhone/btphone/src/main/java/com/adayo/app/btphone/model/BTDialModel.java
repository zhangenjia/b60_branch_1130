package com.adayo.app.btphone.model;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.adayo.app.btphone.callback.IBTDialCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.bpresenter.bluetooth.contracts.IDialContract;
import com.adayo.bpresenter.bluetooth.presenters.BluetoothController;
import com.adayo.bpresenter.bluetooth.presenters.DialPresenter;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.common.bluetooth.bean.PeopleInfo;

import java.util.List;

public class BTDialModel {
    private static final String TAG = Constant.TAG + BTDialModel.class.getSimpleName();

    private Context mContext;
    private IDialContract.IDialPresenter mDialPresenter;
    private IBTDialCallback mBTDialCallback;

    private String deviceName;

    public BTDialModel(Context context) {
        mContext = context;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mDialPresenter = new DialPresenter(mContext);
                mDialPresenter.setView(mDialView);
                mDialView.setPresenter(mDialPresenter);
                mDialPresenter.init();
            }
        });

    }

    public int registerBTDial(IBTDialCallback btDial) {
        Log.i(TAG, "registerBTDial btDial = " + btDial);
        mBTDialCallback = btDial;
        return 0;
    }

    public int unregisterBTDial(IBTDialCallback btDial) {
        return 0;
    }

    public int dialCall(String number) {
        if (BluetoothController.getInstance().isOnCalling()) {
            Log.i(TAG, "通话中，车机端不允许打三方电话");
            return -1;
        }
        if (null == number || "".equals(number)) {
            return -1;
        }
        if(null == mDialPresenter){
            return -1;
        }
        mDialPresenter.dialCall(number);
        return 0;
    }

    public int searchContacts(String number,int type) {
        if(null == mDialPresenter){
            return -1;
        }
        Log.i(TAG, "searchContacts number = " + number);
        mDialPresenter.searchContacts(number,type);
        return 0;
    }

    public String getDeviceName() {
        Log.i(TAG, "getDeviceName deviceName = " + deviceName);
        return deviceName;
    }

    IDialContract.IDialView mDialView = new IDialContract.IDialView() {
        @Override
        public void updateContacts(List<PeopleInfo> list) {
            Log.i(TAG, "updateContacts-List<PeopleInfo>.size == " + list.size());
            if (null == mBTDialCallback) {
                Log.i(TAG, "mBTDialCallback is null , return");
                return;
            }
            mBTDialCallback.updateContacts(list);
        }

        @Override
        public void updateInputNumber(String s) {
            Log.i(TAG, "updateInputNumber == " + s);
        }

        @Override
        public void showInputMaxWarning() {
            Log.i(TAG, "showInputMaxWarning");
        }

        @Override
        public void hideInputMaxWarning() {
            Log.i(TAG, "hideInputMaxWarning");
        }

        @Override
        public void updateSearchMode(boolean b) {
            Log.i(TAG, "updateSearchMode");
        }

        @Override
        public void updateHfpConnectedDevices(List<BluetoothDevice> list) {
            Log.i(TAG, "updateHfpConnectedDevices  = " + list.size());
        }

        @Override
        public void updateActiveDeviceChanged(String address, String name, boolean isMutiDev) {
            Log.i(TAG, "updateActiveDeviceChanged  address = " + address + ",name = " + name + ",isMutiDev = " + isMutiDev);
            deviceName = name;
            if (null == mBTDialCallback) {
                Log.i(TAG, "mBTDialCallback is null , return");
                return;
            }
            mBTDialCallback.updateActiveDeviceChanged(address, name, isMutiDev);
        }

        @Override
        public void updateHfpState(boolean b) {
            Log.i(TAG, "updateHfpState  = " + b);
        }

        @Override
        public void updateA2DPState(boolean b) {
            Log.i(TAG, "updateA2DPState  = " + b);
        }

        @Override
        public void setPresenter(IDialContract.IDialPresenter iDialPresenter) {

        }

        @Override
        public void removePresenter(IDialContract.IDialPresenter iDialPresenter) {

        }
    };
}
