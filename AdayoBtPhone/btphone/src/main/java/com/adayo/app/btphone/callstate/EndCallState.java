package com.adayo.app.btphone.callstate;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.manager.BTCallManager;
import com.adayo.app.btphone.manager.SendSMSManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.List;

/**
 *单方通话-去电
 */
public class EndCallState implements ICallState {

    private static final String TAG = EndCallState.class.getSimpleName();

    private Context mContext;

    private View mShowDialingKeyboard;   //通话中显示隐藏拨号键盘的按键
    private View mMute;
    private View mCallAnswerSwitch;    //手机车机端切换
    private View mAnswer;    //接听
    private View mHangUp;    //挂断

    private View mThirdCallRL;   //三方通话按键
    private View mCallInfoRL;  //左侧单方通话
    private View mThirdCallInfoRL;    //左侧三方通话

    private TextView mNameTV;
    private TextView mTimeTV;
    private TextView mProfilePhotoTV;

    private BTCallManager mBTCallMgr;

    public EndCallState(Context context, List<View> visibleList, List<TextView> textList){
        mContext = context;
        mAnswer = visibleList.get(0);
        mMute = visibleList.get(1);
        mCallAnswerSwitch = visibleList.get(2);
        mShowDialingKeyboard = visibleList.get(3);
        mThirdCallRL = visibleList.get(4);
        mCallInfoRL = visibleList.get(5);
        mThirdCallInfoRL = visibleList.get(6);
        mHangUp = visibleList.get(7);

        mNameTV = textList.get(0);
        mTimeTV = textList.get(1);
        mProfilePhotoTV = textList.get(2);
    }

    @Override
    public void updateDisplayStatus() {
        Log.i(TAG,"updateShowState");
        mNameTV.setTextColor(mContext.getColor(R.color.end_call_text_color));
        mProfilePhotoTV.setTextColor(mContext.getColor(R.color.end_call_text_color));
        mShowDialingKeyboard.setEnabled(false);
        mCallAnswerSwitch.setEnabled(false);
        mMute.setEnabled(false);
        mProfilePhotoTV.setEnabled(false);
    }

    @Override
    public void updateCallerName(String name) {
    }

    @Override
    public void updateCallingTime(String time) {
        Log.i(TAG,"updateCallingTime time = "+time);
        mTimeTV.setText(time);
    }

    @Override
    public void updateMutiCallerName(String name) {

    }

    @Override
    public void updateMutiCallingTime(String time) {

    }

    @Override
    public void hangUpCall() {

    }

}
