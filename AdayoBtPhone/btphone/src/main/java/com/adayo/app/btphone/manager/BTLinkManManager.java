package com.adayo.app.btphone.manager;

import android.app.Activity;
import android.content.Context;

import com.adayo.app.btphone.callback.IBTLinkManCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.model.BTLinkManModel;


public class BTLinkManManager {

    private static final String TAG = Constant.TAG + BTLinkManManager.class.getSimpleName();
    private Context mContext;
    private BTLinkManModel mBTLinkManModel;

    public BTLinkManManager(Context context, Activity activity){
        mContext = context;
        mBTLinkManModel = new BTLinkManModel(mContext,activity);
    }

    public int registerContactInfo(IBTLinkManCallback btContactsCallback){
        return mBTLinkManModel.registerContactInfo(btContactsCallback);
    }

    public int unregisterContactInfo(IBTLinkManCallback btContactsCallback){
        return mBTLinkManModel.unregisterContactInfo(btContactsCallback);
    }

    public int dialCall(String number){
        return mBTLinkManModel.dialCall(number);
    }

    public int downloadContacts(){
        return mBTLinkManModel.downloadContacts();
    }

    public int cancelDownloadContacts(){
        return mBTLinkManModel.cancelDownloadContacts();
    }

    public int searchContacts(String info){
        return mBTLinkManModel.searchContacts(info);
    }

    public int setSearchMode(boolean isSearch){
        mBTLinkManModel.setSearchMode(isSearch);
        return 0;
    }
}
