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
public class OneWayRingUpState implements ICallState {

    private static final String TAG = OneWayRingUpState.class.getSimpleName();

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

    private TextView mNameTV;
    private TextView mTimeTV;
    private TextView mProfilePhotoTV;

    private BTCallManager mBTCallMgr;

    public OneWayRingUpState(List<View> visibleList, List<TextView> textList){
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
        mShowDialingKeyboard.setVisibility(View.GONE);
        mThirdCallRL.setVisibility(View.GONE);
        mCallInfoRL.setVisibility(View.VISIBLE);
        mThirdCallInfoRL.setVisibility(View.GONE);
        mShowDialingKeyboard.setEnabled(true);
        mCallAnswerSwitch.setEnabled(true);
        mMute.setEnabled(true);
        mProfilePhotoTV.setEnabled(true);
        mNameTV.setTextColor(mContext.getColor(R.color.colorWhite));
        mProfilePhotoTV.setTextColor(mContext.getColor(R.color.colorWhite));
        AAOP_HSkin
                .with(mHangUp)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_phone_hangup)
                .applySkin(false);
    }

    @Override
    public void updateCallerName(String name) {
        if(null != name && !"".equals(name)){
            mProfilePhotoTV.setText(String.valueOf(name.charAt(0)));
        }
        mNameTV.setText(name);
    }

    @Override
    public void updateCallingTime(String time) {
        mTimeTV.setText(time);
    }

    @Override
    public void updateMutiCallerName(String name) {

    }

    @Override
    public void updateMutiCallingTime(String time) {

    }

    public void setBTCallManager(BTCallManager btCallMgr){
        mBTCallMgr = btCallMgr;
    }

    @Override
    public void hangUpCall() {
        mBTCallMgr.handupCall();
    }
}
