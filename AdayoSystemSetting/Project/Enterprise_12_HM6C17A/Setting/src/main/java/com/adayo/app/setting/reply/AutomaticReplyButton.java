package com.adayo.app.setting.reply;

import android.provider.Settings;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.base.LogUtil;


public class AutomaticReplyButton extends BaseHandler {
    private final static String TAG = AutomaticReplyButton.class.getSimpleName();

    public AutomaticReplyButton() {
        super(BaseHandler.AUTOMATIC_REPLY_BUTTON);
    }


    @Override
    protected void handleLeave(ReplyData leave) {
        Settings.Global.putInt(leave.getContentResolver(),
                ParamConstant.REPLY_SWITCH,
                1 );
      boolean b=Settings.Global.putString(leave.getContentResolver(),
                ParamConstant.REPLY_EDIT,
                ParamConstant.REPLY_AUTOMATIC);
        LogUtil.debugD(TAG,"b ="+b);
    }
}
