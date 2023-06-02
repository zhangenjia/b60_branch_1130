package com.adayo.app.setting.reply;

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.provider.Settings;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.base.LogUtil;

public class ReplyRequest extends BaseRequest {
    private final static String TAG = ReplyRequest.class.getSimpleName();
    private final MutableLiveData<Boolean> mreplySwitch = new MutableLiveData<>();
    private final MutableLiveData<String> mreplyEdit = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mreplyButton = new MutableLiveData<>();

    public void init() {
        parseEnableState();
        parseReplyButton();
        parseEdit();
    }

    public void unInit() {


    }

    public void requestEnableState(boolean b) {
        LogUtil.i(TAG, "b = " + b);
        Settings.Global.putInt(getAppContext().getContentResolver(),
                ParamConstant.REPLY_SWITCH_OWE,
                b ? 1 : 0);
        if (b) {
            handlerData(getMreplyButton().getValue(), getMreplyEdit().getValue(), getAppContext().getContentResolver());
        } else {
            Settings.Global.putInt(getAppContext().getContentResolver(),
                    ParamConstant.REPLY_SWITCH,
                    0);
        }
        mreplySwitch.setValue(b);
    }

    private void parseReplyButton() {
        int b = Settings.Global.getInt(getAppContext().getContentResolver(),
                ParamConstant.REPLY_BUTTON,
                1);
        LogUtil.i(TAG, "b = " + b);
        if (b == 1) {
            mreplyButton.setValue(true);
        } else {
            mreplyButton.setValue(false);
        }
    }

    public void requestReplyButton(Boolean b) {
        LogUtil.i(TAG, "b = " + b);
        Settings.Global.putInt(getAppContext().getContentResolver(),
                ParamConstant.REPLY_BUTTON,
                b ? 1 : 0);
        handlerData(b, getMreplyEdit().getValue(), getAppContext().getContentResolver());
        mreplyButton.setValue(b);
    }

    public void requestEdit(String x) {
        LogUtil.i(TAG, "x = " + x);
        Settings.Global.putString(getAppContext().getContentResolver(),
                ParamConstant.REPLY_EDIT_OWE,
                x);
        handlerData(getMreplyButton().getValue(), x, getAppContext().getContentResolver());
        mreplyEdit.setValue(x);
    }

    private void handlerData(boolean b, String s, ContentResolver contentResolver) {
        LogUtil.debugD(TAG, "b = " + b + "s = " + s);
        ReplyData replyData = new ReplyData(b, s, contentResolver);
        AutomaticReplyButton automaticReplyButton = new AutomaticReplyButton();
        CustomReplyButtonNotNull customReplyButtonNotNull = new CustomReplyButtonNotNull();
        CustomReplyButtonNull customReplyButtonNull = new CustomReplyButtonNull();
        automaticReplyButton.setNextBaseHandler(customReplyButtonNotNull);
        customReplyButtonNotNull.setNextBaseHandler(customReplyButtonNull);
        automaticReplyButton.submit(replyData);
    }

    private void parseEdit() {
        String x = Settings.Global.getString(getAppContext().getContentResolver(),
                ParamConstant.REPLY_EDIT_OWE);
        LogUtil.i(TAG, "x ="+x);
        if (x != null) {
            mreplyEdit.setValue(x);
        } else {
            Settings.Global.putString(getAppContext().getContentResolver(),
                    ParamConstant.REPLY_EDIT_OWE,
                    ParamConstant.REPLY_EDIT_MOREN);
            mreplyEdit.setValue(ParamConstant.REPLY_EDIT_MOREN);
        }
    }

    private void parseEnableState() {
        int x = Settings.Global.getInt(getAppContext().getContentResolver(),
                ParamConstant.REPLY_SWITCH,
                0);
        if (x == 1) {
            mreplySwitch.setValue(true);
        } else {
            mreplySwitch.setValue(false);
        }
        LogUtil.i(TAG, "x= " + x);
    }

    public MutableLiveData<String> getMreplyEdit() {
        if (mreplyEdit.getValue() == null) {
            mreplyEdit.setValue("0");
            LogUtil.w(TAG, " mreplyEdit     is null");
        }
        return mreplyEdit;
    }

    public MutableLiveData<Boolean> getMreplySwitch() {
        if (mreplySwitch.getValue() == null) {
            mreplySwitch.setValue(false);
            LogUtil.w(TAG, " mreplySwitch     is null");
        }
        return mreplySwitch;
    }

    public MutableLiveData<Boolean> getMreplyButton() {
        if (mreplyButton.getValue() == null) {
            mreplyButton.setValue(false);
            LogUtil.w(TAG, " mreplyButton     is null");
        }
        return mreplyButton;
    }
}
