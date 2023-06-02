package com.adayo.app.btphone.manager;

import android.content.Context;

import com.adayo.app.btphone.callback.IBTDialCallback;
import com.adayo.app.btphone.model.BTDialModel;

/**
 * 拨号
 */
public class BTDialManager {

    private BTDialModel mBTDialModel;

    public BTDialManager(Context context){
        mBTDialModel = new BTDialModel(context);
    }

    public int registerBTDial(IBTDialCallback btDial){
        return mBTDialModel.registerBTDial(btDial);
    }

    public int unregisterBTDial(IBTDialCallback btDial){
        return mBTDialModel.unregisterBTDial(btDial);
    }

    public int searchContacts(String number,int type){
        return mBTDialModel.searchContacts(number,type);
    }

    public int dialCall(String number){
        mBTDialModel.dialCall(number);
        return 0;
    }

    public String getDeviceName(){
        return mBTDialModel.getDeviceName();
    }

}
