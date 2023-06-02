package com.adayo.app.setting.reply;

import android.provider.Settings;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.base.LogUtil;

public class CustomReplyButtonNull extends BaseHandler {
    private final static String TAG = CustomReplyButtonNull.class.getSimpleName();

    public CustomReplyButtonNull() {
        super(BaseHandler.CUSTOM_REPLY_BUTTON_NULL);
    }

    @Override
    protected void handleLeave(ReplyData leave) {
        Settings.Global.putInt(leave.getContentResolver(),
                ParamConstant.REPLY_SWITCH,
                0 );
       boolean b= Settings.Global.putString(leave.getContentResolver(),
                ParamConstant.REPLY_EDIT,
                leave.getReplySting());
        LogUtil.debugD(TAG,"b ="+b);


    }
}
