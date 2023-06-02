package com.adayo.app.setting.reply;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.base.LogUtil;

public class ReplyViewModel extends ViewModel {
    private final static String TAG = ReplyViewModel.class.getSimpleName();
    public final ReplyRequest mReplyRequest=new ReplyRequest();
    {
        mReplyRequest.init();
    }

    @Override
    protected void onCleared() {
       LogUtil.debugD(TAG);
        mReplyRequest.unInit();
    }}
