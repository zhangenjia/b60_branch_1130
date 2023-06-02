package com.adayo.app.btphone.manager;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class SendSMSManager {
    private static final String TAG = SendSMSManager.class.getSimpleName();

    private BTCallManager mCallMgr;
    private Context mContext;
    private String replySwitch = "reply_switch";
    private String replyEdit = "reply_edit";

    public SendSMSManager(Context context, BTCallManager callManager){
        mContext = context;
        mCallMgr = callManager;
    }

    public boolean needSendSMS(){
        int replySwitch = Settings.Global.getInt(mContext.getContentResolver(), this.replySwitch,0);
        Log.i(TAG,"needSendSMS replySwitch = "+replySwitch);
        if(replySwitch == 1){
            return true;
        }else{
            return false;
        }
    }

    public int sendSMS(){
        String replyEdit = Settings.Global.getString(mContext.getContentResolver(), this.replyEdit);
        Log.i(TAG,"sendSMS replyEdit = "+replyEdit);
        mCallMgr.sendMessage(replyEdit);
        return 0;
    }

}
