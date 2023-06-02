package com.adayo.app.btphone.manager;

import android.app.Activity;
import android.content.Context;

import com.adayo.app.btphone.callback.IBTCallLogCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.model.BTCallLogModel;

public class BTCallLogManager {
    private static final String TAG = Constant.TAG + BTConnectManager.class.getSimpleName();
    private Context mContext;
    private BTCallLogModel mBTCallLogModel;

    private volatile static BTCallLogManager mCallLogMgr;

    public BTCallLogManager(Context context){
        mContext = context;
        mBTCallLogModel = new BTCallLogModel(mContext);
    }

    public static BTCallLogManager getInstance(Context context) {
        if (mCallLogMgr == null) {
            synchronized (BTCallLogManager.class) {
                if (mCallLogMgr == null) {
                    mCallLogMgr = new BTCallLogManager(context);
                }
            }
        }
        return mCallLogMgr;
    }

    public int registerBTCallLog(IBTCallLogCallback callback){
        return mBTCallLogModel.registerBTCallLog(callback);
    }

    public int unregisterBTCallLog(IBTCallLogCallback callback){
        return mBTCallLogModel.unregisterBTCallLog(callback);
    }

    public int dialCall(String number){
        return mBTCallLogModel.dialCall(number);
    }

    public int downloadCallLog(){
        return mBTCallLogModel.downloadCallLog();
    }

    public int cancelDownloadCallLog(){
        return mBTCallLogModel.cancelDownloadCallLog();
    }

    public int loadCallLog(String s){
        return mBTCallLogModel.loadCallLog(s);
    }

    public String getLastRecentCall(){
        return mBTCallLogModel.getLastRecentCall();
    }

    public boolean getNoCallLogInfo(){
        return mBTCallLogModel.getNoCallLogInfo();
    }

    public void setLastRecentCall(String number){
        mBTCallLogModel.setLastRecentCall(number);
    }

    public int setSearchMode(boolean isSearch){
        mBTCallLogModel.setSearchMode(isSearch);
        return 0;
    }
}
