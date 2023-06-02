package com.adayo.app.btphone.manager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RelativeLayout;

import com.adayo.app.btphone.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

public class DialogManager {

    private static final String TAG = DialogManager.class.getSimpleName();

    private volatile static DialogManager mDialogMgr;
    private Dialog mCallDialog;
    private boolean mCallDialogShowState;
    private boolean mKeyboardDialogShowState;
    private Handler mHandler;

    private Dialog mKeyboardDialog;

    public static DialogManager getInstance() {
        if (mDialogMgr == null) {
            synchronized (DialogManager.class) {
                if (mDialogMgr == null) {
                    mDialogMgr = new DialogManager();
                }
            }
        }
        return mDialogMgr;
    }

    private DialogManager(){

    }

    public void setHandler(Handler handler){
        mHandler = handler;
    }


    public Dialog getCallDialog(Context context){
        if(null == mCallDialog){
            mCallDialog = new Dialog(context, R.style.float_view);
        }
        Log.i(TAG,"getCallDialog mCallDialog = "+mCallDialog);
        return mCallDialog;
    }

    public void showCallDialog(){
        Log.i(TAG,"showCallDialog mCallDialog = "+mCallDialog);
        if(null != mCallDialog && !mCallDialog.isShowing()){
            mCallDialog.show();
            mCallDialogShowState = true;
        }

    }

    public void dismissCallDialog(){
        Log.i(TAG,"dismissCallDialog mCallDialog = "+mCallDialog);
        if(null != mCallDialog && mCallDialog.isShowing()){
            mCallDialog.dismiss();
            mCallDialogShowState = false;
        }

    }

    public boolean callDialogIsShow(){
        return mCallDialogShowState;
    }

    public boolean keyboardDialogIsShow(){
        return mKeyboardDialogShowState;
    }

    public Dialog getKeyboardDialog(Context context){
        if(null == mKeyboardDialog){
            mKeyboardDialog = new Dialog(context, R.style.float_view);

        }
        Log.i(TAG,"getCallDialog mKeyboardDialog = "+mKeyboardDialog);
        return mKeyboardDialog;
    }

    public void showKeyboardDialog(){
        Log.i(TAG,"showKeyboardDialog mKeyboardDialog = "+mKeyboardDialog);
        if(null != mKeyboardDialog && !mKeyboardDialog.isShowing()){
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("keyBoardDialog","isShow");
            message.setData(bundle);
            message.what = 1;
            mHandler.sendMessage(message);
            mKeyboardDialog.show();
            RelativeLayout rl = mKeyboardDialog.findViewById(R.id.rl);
            AAOP_HSkin.with(rl)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.bg_letter1)
                    .applySkin(true);
            mKeyboardDialogShowState = true;
        }

    }

    public void dismissKeyboardDialog(){
        Log.i(TAG,"dismissKeyboardDialog mKeyboardDialog = "+mKeyboardDialog);
        if(null != mKeyboardDialog && mKeyboardDialog.isShowing()){
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("keyBoardDialog","isHidden");
            message.setData(bundle);
            message.what = 1;
            mHandler.sendMessage(message);
            mKeyboardDialog.dismiss();
            mKeyboardDialogShowState = false;
        }

    }
}
