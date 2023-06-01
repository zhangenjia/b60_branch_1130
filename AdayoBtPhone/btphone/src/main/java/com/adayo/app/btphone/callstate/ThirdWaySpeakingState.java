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
 *三方通话-通话中
 */
public class ThirdWaySpeakingState implements ICallState {

    private static final String TAG = ThirdWaySpeakingState.class.getSimpleName();

    private SendSMSManager mSendSMSMgr;
    private Context mContext;

    private View mShowDialingKeyboard;   //通话中显示隐藏拨号键盘的按键
    private View mMute;
    private View mCallAnswerSwitch;    //手机车机端切换
    private View mAnswer;    //接听
    private View mHangUp;    //挂断

    private View mThirdCallRL;   //三方通话按键
    private View mCallInfoRL;  //左侧单方通话
    private View mThirdCallInfoRL;    //左侧三方通话

    private TextView mThirdCallNameTV1;
    private TextView mThirdCallTimeTV1;
    private TextView mThirdCallNameTV2;
    private TextView mThirdCallTimeTV2;

    private BTCallManager mBTCallMgr;

    public ThirdWaySpeakingState(List<View> visibleList, List<TextView> textList){
        mAnswer = visibleList.get(0);
        mMute = visibleList.get(1);
        mCallAnswerSwitch = visibleList.get(2);
        mShowDialingKeyboard = visibleList.get(3);
        mThirdCallRL = visibleList.get(4);
        mCallInfoRL = visibleList.get(5);
        mThirdCallInfoRL = visibleList.get(6);
        mHangUp = visibleList.get(7);

        mThirdCallNameTV1 = textList.get(0);
        mThirdCallTimeTV1 = textList.get(1);
        mThirdCallNameTV2 = textList.get(2);
        mThirdCallTimeTV2 = textList.get(3);
    }

    public void setSendSMSManager(Context context, SendSMSManager sendSMSManager){
        mContext = context;
        mSendSMSMgr = sendSMSManager;
    }

    @Override
    public void updateDisplayStatus() {
        Log.i(TAG,"updateShowState");
        mAnswer.setVisibility(View.GONE);
        mMute.setVisibility(View.VISIBLE);
        mCallAnswerSwitch.setVisibility(View.VISIBLE);
        mShowDialingKeyboard.setVisibility(View.VISIBLE);
        mThirdCallRL.setVisibility(View.GONE);
        mCallInfoRL.setVisibility(View.GONE);
        mThirdCallInfoRL.setVisibility(View.VISIBLE);
        AAOP_HSkin
                .with(mHangUp)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_phone_hangup)
                .applySkin(false);
    }

    @Override
    public void updateCallerName(String name) {
        mThirdCallNameTV2.setText(name);
    }

    @Override
    public void updateCallingTime(String time) {
        mThirdCallTimeTV2.setText(time);
    }

    @Override
    public void updateMutiCallerName(String name) {
        mThirdCallNameTV1.setText(name);
    }

    @Override
    public void updateMutiCallingTime(String time) {
        mThirdCallTimeTV1.setText(time);
    }

    public void setBTCallManager(BTCallManager btCallMgr){
        mBTCallMgr = btCallMgr;
    }

    @Override
    public void hangUpCall() {
        mBTCallMgr.handupCall();
    }
}
