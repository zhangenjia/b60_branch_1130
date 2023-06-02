package com.adayo.app.btphone.manager;

import android.content.Context;

import com.adayo.app.btphone.callback.IBTConnectCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.model.BTConnectModel;


public class BTConnectManager {

    private static final String TAG = Constant.TAG + BTConnectManager.class.getSimpleName();
    private Context mContext;
    private BTConnectModel mBTConnModel;

    public BTConnectManager(Context context){
        mContext = context;
        mBTConnModel = new BTConnectModel(mContext);
    }

    public boolean getHFPState(){
        return mBTConnModel.getHFPState();
    }

    public boolean getA2DPState(){
        return mBTConnModel.getA2DPState();
    }

    public int registerBTConnectState(IBTConnectCallback callback){
        return mBTConnModel.registerBTConnectState(callback);
    }

    public int unregisterBTConnectState(IBTConnectCallback callback){
        return mBTConnModel.unregisterBTConnectState(callback);
    }
}
