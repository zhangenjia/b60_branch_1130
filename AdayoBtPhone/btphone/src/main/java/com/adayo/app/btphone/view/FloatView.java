package com.adayo.app.btphone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.callback.ICallViewCallback;
import com.adayo.app.btphone.callstate.EndCallState;
import com.adayo.app.btphone.callstate.ICallState;
import com.adayo.app.btphone.callstate.OneWaySpeakingState;
import com.adayo.app.btphone.callstate.OneWayInCommingState;
import com.adayo.app.btphone.callstate.OneWayRingUpState;
import com.adayo.app.btphone.callstate.ThirdWayGoState;
import com.adayo.app.btphone.callstate.ThirdWayHoldState;
import com.adayo.app.btphone.callstate.ThirdWayInCommingState;
import com.adayo.app.btphone.callstate.ThirdWaySpeakingState;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTCallManager;
import com.adayo.app.btphone.manager.SendSMSManager;
import com.adayo.app.btphone.service.BlueToothCallService;
import com.adayo.app.btphone.utils.EditUtils;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class FloatView extends ConstraintLayout implements View.OnClickListener{

    private static final String TAG = Constant.TAG + FloatView.class.getSimpleName();

    private Context mContext;
    private BTCallManager mBTCallManager;
    private SendSMSManager mSendSMSMgr;
    private BlueToothCallService.OnUpdateViewListener mFloatViewListener;

    private View rootView;
    private View mFloatRoot;
    private View mKeyboardCL;
    private View mCallCL;

    private TextView mNameTV1;
    private TextView mTimeTV1;
    private TextView mProfilePhotoTV;
    private ImageView mShowDialingKeyboard;   //通话中显示隐藏拨号键盘的按键
    private ImageView mMute;
    private ImageView mCallAnswerSwitch;    //手机车机端切换
    private ImageView mHandUp;    //挂断
    private ImageView mAnswer;    //接听

    private RelativeLayout mThirdCallRL;   //三方通话按键
    private ImageView mThirdAnswerIV;    //三方通话接听按键
    private ImageView mThirdHoldIV;      //三方通话保持按键

    private RelativeLayout mCallInfoRL;  //左侧单方通话
    private RelativeLayout mThirdCallInfoRL;    //左侧三方通话
    private RelativeLayout mThirdCallInfoRL2;
    private TextView mThirdCallNameTV1;
    private TextView mThirdCallTimeTV1;
    private TextView mThirdCallNameTV2;
    private TextView mThirdCallTimeTV2;

    private View mIncludeKeyboard;
    private EditText mKeyboardET;
    private ImageView mKey0;
    private ImageView mKey1;
    private ImageView mKey2;
    private ImageView mKey3;
    private ImageView mKey4;
    private ImageView mKey5;
    private ImageView mKey6;
    private ImageView mKey7;
    private ImageView mKey8;
    private ImageView mKey9;
    private ImageView mKeyStar;
    private ImageView mKeyPound;

    private int mStartTouchX;
    private int mStartTouchY;
    private int mStartViewX;
    private int mStartViewY;

    private String mOldNumber = "";
    private String mNewNumber = "";

    private List<View> visibleList = new ArrayList<>();
    private List<TextView> oneWayTextList = new ArrayList<>();
    private List<TextView> thirdWayTextList = new ArrayList<>();
    private ICallState mCallStatus;
    private OneWayInCommingState mOneWayInCommingState;
    private OneWayRingUpState mOneWayRingUpState;
    private OneWaySpeakingState mOneWaySpeakingState;
    private ThirdWayInCommingState mThirdWayInCommingState;
    private ThirdWaySpeakingState mThirdWaySpeakingState;
    private ThirdWayHoldState mThirdWayHoldState;
    private ThirdWayGoState mThirdWayGoState;
    private EndCallState mEndCallState;

    private static final int OFFSET = 3;

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Log.i(TAG,"init");
        mContext = context;
        rootView = AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.float_view_layout,this);
        mFloatRoot = rootView.findViewById(R.id.float_root);
        mKeyboardCL = rootView.findViewById(R.id.keyboard_cl);
        mKeyboardCL.setVisibility(View.GONE);
        mCallCL = rootView.findViewById(R.id.call_cl);
        AAOP_HSkin
                .with(mFloatRoot)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.frame_incoming_call)
                .applySkin(false);

        mIncludeKeyboard = rootView.findViewById(R.id.include_call_keyboard);
        mKeyboardET = mIncludeKeyboard.findViewById(R.id.keyborad_et);
        mKey0 = mIncludeKeyboard.findViewById(R.id.key_0_iv);
        mKey0.setOnClickListener(this);
        mKey0.setmClickSound(1000);
        mKey1 = mIncludeKeyboard.findViewById(R.id.key_1_iv);
        mKey1.setOnClickListener(this);
        mKey1.setmClickSound(1001);
        mKey2 = mIncludeKeyboard.findViewById(R.id.key_2_iv);
        mKey2.setOnClickListener(this);
        mKey2.setmClickSound(1002);
        mKey3 = mIncludeKeyboard.findViewById(R.id.key_3_iv);
        mKey3.setOnClickListener(this);
        mKey3.setmClickSound(1003);
        mKey4 = mIncludeKeyboard.findViewById(R.id.key_4_iv);
        mKey4.setOnClickListener(this);
        mKey4.setmClickSound(1004);
        mKey5 = mIncludeKeyboard.findViewById(R.id.key_5_iv);
        mKey5.setOnClickListener(this);
        mKey5.setmClickSound(1005);
        mKey6 = mIncludeKeyboard.findViewById(R.id.key_6_iv);
        mKey6.setOnClickListener(this);
        mKey6.setmClickSound(1006);
        mKey7 = mIncludeKeyboard.findViewById(R.id.key_7_iv);
        mKey7.setOnClickListener(this);
        mKey7.setmClickSound(1007);
        mKey8 = mIncludeKeyboard.findViewById(R.id.key_8_iv);
        mKey8.setOnClickListener(this);
        mKey8.setmClickSound(1008);
        mKey9 = mIncludeKeyboard.findViewById(R.id.key_9_iv);
        mKey9.setOnClickListener(this);
        mKey9.setmClickSound(1009);
        mKeyStar = mIncludeKeyboard.findViewById(R.id.key_star_iv);
        mKeyStar.setOnClickListener(this);
        mKeyStar.setmClickSound(1000);
        mKeyPound = mIncludeKeyboard.findViewById(R.id.key_pound_iv);
        mKeyPound.setOnClickListener(this);
        mKeyPound.setmClickSound(1000);

        mShowDialingKeyboard = rootView.findViewById(R.id.icon_dialingKeyboard);
        mShowDialingKeyboard.setOnClickListener(this);
        mMute = rootView.findViewById(R.id.icon_mute);
        mMute.setOnClickListener(this);
        mCallAnswerSwitch = rootView.findViewById(R.id.phone_answerSwitch);
        mCallAnswerSwitch.setOnClickListener(this);
        mAnswer = rootView.findViewById(R.id.phone_answer);
        mAnswer.setOnClickListener(this);
        mHandUp = rootView.findViewById(R.id.phone_hangup);
        mHandUp.setOnClickListener(this);
        mThirdCallRL = rootView.findViewById(R.id.phone_third_call_rl);
        mThirdAnswerIV = rootView.findViewById(R.id.phone_third_answer_iv);
        mThirdAnswerIV.setOnClickListener(this);
        mThirdHoldIV = rootView.findViewById(R.id.phone_third_hold_iv);
        mThirdHoldIV.setOnClickListener(this);
        mCallInfoRL = rootView.findViewById(R.id.call_info_rl);
        mThirdCallInfoRL = rootView.findViewById(R.id.third_call_info_rl);
        mThirdCallInfoRL2 = rootView.findViewById(R.id.third_call_info_rl_2);
        mThirdCallInfoRL2.setOnClickListener(this);

        mNameTV1 = rootView.findViewById(R.id.name_1);
        mTimeTV1 = rootView.findViewById(R.id.time_1);
        mProfilePhotoTV = rootView.findViewById(R.id.profile_photo_tv);
        mThirdCallNameTV1 = rootView.findViewById(R.id.third_call_name_1);
        mThirdCallNameTV2 = rootView.findViewById(R.id.third_call_name_2);
        mThirdCallTimeTV1 = rootView.findViewById(R.id.third_call_time_1);
        mThirdCallTimeTV2 = rootView.findViewById(R.id.third_call_time_2);

        visibleList.add(mAnswer);
        visibleList.add(mMute);
        visibleList.add(mCallAnswerSwitch);
        visibleList.add(mShowDialingKeyboard);
        visibleList.add(mThirdCallRL);
        visibleList.add(mCallInfoRL);
        visibleList.add(mThirdCallInfoRL);
        visibleList.add(mHandUp);

        oneWayTextList.add(mNameTV1);
        oneWayTextList.add(mTimeTV1);
        oneWayTextList.add(mProfilePhotoTV);

        thirdWayTextList.add(mThirdCallNameTV1);
        thirdWayTextList.add(mThirdCallTimeTV1);
        thirdWayTextList.add(mThirdCallNameTV2);
        thirdWayTextList.add(mThirdCallTimeTV2);

        mBTCallManager = BTCallManager.getInstance(context);
        mSendSMSMgr = new SendSMSManager(context,mBTCallManager);

        mOneWayInCommingState = new OneWayInCommingState(visibleList,oneWayTextList);
        mOneWayInCommingState.setSendSMSManager(mContext,mSendSMSMgr);
        mOneWayInCommingState.setBTCallManager(mBTCallManager);

        mOneWayRingUpState = new OneWayRingUpState(visibleList,oneWayTextList);
        mOneWayRingUpState.setSendSMSManager(mContext,mSendSMSMgr);
        mOneWayRingUpState.setBTCallManager(mBTCallManager);

        mOneWaySpeakingState = new OneWaySpeakingState(visibleList,oneWayTextList);
        mOneWaySpeakingState.setSendSMSManager(mContext,mSendSMSMgr);
        mOneWaySpeakingState.setBTCallManager(mBTCallManager);

        mThirdWayInCommingState = new ThirdWayInCommingState(visibleList,thirdWayTextList);
        mThirdWayInCommingState.setSendSMSManager(mContext,mSendSMSMgr);
        mThirdWayInCommingState.setBTCallManager(mBTCallManager);

        mThirdWaySpeakingState = new ThirdWaySpeakingState(visibleList,thirdWayTextList);
        mThirdWaySpeakingState.setSendSMSManager(mContext,mSendSMSMgr);
        mThirdWaySpeakingState.setBTCallManager(mBTCallManager);

        mThirdWayHoldState = new ThirdWayHoldState(visibleList,thirdWayTextList);
        mThirdWayHoldState.setSendSMSManager(mContext,mSendSMSMgr);
        mThirdWayHoldState.setBTCallManager(mBTCallManager);

        mThirdWayGoState = new ThirdWayGoState(visibleList,thirdWayTextList);
        mThirdWayGoState.setSendSMSManager(mContext,mSendSMSMgr);
        mThirdWayGoState.setBTCallManager(mBTCallManager);

        mEndCallState = new EndCallState(getContext(),visibleList,oneWayTextList);

        mBTCallManager.registerCallViewCallback(callback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_dialingKeyboard:
                showOrHideKeyboard();
                break;
            case R.id.icon_mute:
                mBTCallManager.switchMicMute();
                break;
            case R.id.phone_answerSwitch:
                mBTCallManager.switchAudioChannel();
                break;
            case R.id.phone_hangup:
                mCallStatus.hangUpCall();
                break;
            case R.id.phone_answer:
                mBTCallManager.answerCall();
                break;
            case R.id.third_call_info_rl_2:
                mBTCallManager.switchCall();
                break;
                //挂断当前通话，接听新电话
            case R.id.phone_third_answer_iv:
                mBTCallManager.handupActiveCallAndAnwserCall();
                break;
                //保留当前通话并接听第三方来电
            case R.id.phone_third_hold_iv:
                mBTCallManager.holdActiveCallAndAnwserCall();
                break;
            case R.id.key_0_iv:
                keyboardClick("0");
                break;
            case R.id.key_1_iv:
                keyboardClick("1");
                break;
            case R.id.key_2_iv:
                keyboardClick("2");
                break;
            case R.id.key_3_iv:
                keyboardClick("3");
                break;
            case R.id.key_4_iv:
                keyboardClick("4");
                break;
            case R.id.key_5_iv:
                keyboardClick("5");
                break;
            case R.id.key_6_iv:
                keyboardClick("6");
                break;
            case R.id.key_7_iv:
                keyboardClick("7");
                break;
            case R.id.key_8_iv:
                keyboardClick("8");
                break;
            case R.id.key_9_iv:
                keyboardClick("9");
                break;
            case R.id.key_star_iv:
                keyboardClick("*");
                break;
            case R.id.key_pound_iv:
                keyboardClick("#");
                break;
            default:break;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.i(TAG,"dispatchTouchEvent event.getAction() = "+event.getAction());
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        int[] location = new int[2];
        rootView.getLocationOnScreen(location);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartTouchX = x;
                mStartTouchY = y;
                mStartViewX = location[0];
                mStartViewY = location[1];
                break;

            case MotionEvent.ACTION_MOVE:
                if(Math.abs(x - mStartTouchX) > OFFSET || Math.abs(y - mStartTouchY) > OFFSET) {
                    if (mFloatViewListener != null) {
                        mFloatViewListener.onMoveView(mStartViewX + x - mStartTouchX, mStartViewY + y - mStartTouchY);
                    }
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public void setOnUpdateViewListener(BlueToothCallService.OnUpdateViewListener listener){
        mFloatViewListener = listener;
    }

    ICallViewCallback callback = new ICallViewCallback() {

        /**
         * 只判断三方通话状态
         * @param type
         */
        @Override
        public void showCallView(int type) {
            Log.i(TAG,"showCallView type = "+type);
            switch(type){
                //单方通话来电
                case Constant.CALL_TYPE_INCOMINT_TELEGRAM:
                    mCallStatus = mOneWayInCommingState;
                    mCallStatus.updateDisplayStatus();
                    mCallStatus.updateCallingTime(mContext.getString(R.string.incomming));
                    break;
                //单方通话去电
                case Constant.CALL_TYPE_RING_UP:
                    mCallStatus = mOneWayRingUpState;
                    mCallStatus.updateDisplayStatus();
                    mCallStatus.updateCallingTime(mContext.getString(R.string.dialing));
                    break;
                //单方通话中
                case Constant.CALL_TYPE_ON_THE_LINE:
                    mCallStatus = mOneWaySpeakingState;
                    mCallStatus.updateDisplayStatus();
                    break;
                case Constant.CALL_TYPE_END_CALL:
                    mCallStatus = mEndCallState;
                    mCallStatus.updateDisplayStatus();
                    mCallStatus.updateCallingTime(mContext.getString(R.string.end_call));
                    break;
                default:break;
            }
        }

        @Override
        public void updateMicMuteOrUnMuteState(int state) {
            Log.i(TAG,"UpdateMicMuteOrUnMuteState state = "+state);
            if (state == 0) {  //静音
                AAOP_HSkin
                        .with(mMute)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_mute)
                        .applySkin(false);
            }else if(state == 1){   //非静音
                AAOP_HSkin
                        .with(mMute)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_voices)
                        .applySkin(false);
            }
        }

        @Override
        public void updateAudioOnCarOrPhoneState(int state) {
            Log.i(TAG,"updateAudioOnCarOrPhoneState state = "+state);
            if (state == 0) {  //车机端
                AAOP_HSkin
                        .with(mCallAnswerSwitch)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_car_answer)
                        .applySkin(false);
            }else if(state == 1){   //手机端
                AAOP_HSkin
                        .with(mCallAnswerSwitch)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_phone_answer)
                        .applySkin(false);
            }
        }

        @Override
        public void updateCallerName(String name) {
            Log.i(TAG,"updateCallerName name = "+name);
            if (mBTCallManager.isCurrentWeChatCall()) {
                name = mContext.getString(R.string.wechat);
            }
            if(null != mCallStatus){
                mCallStatus.updateCallerName(name == null || "".equals(name) ? mOldNumber : name);
            }
        }

        @Override
        public void updateCallerNumber(String number) {
            Log.i(TAG,"updateCallerNumber number = "+number);
            if (mBTCallManager.isCurrentWeChatCall()) {
                number = "";
            }
            mOldNumber = number;
        }

        @Override
        public void updateCallingTime(String time) {
            Log.i(TAG,"updateCallingTime time = "+time);
            mCallStatus.updateCallingTime(time);
        }

        @Override
        public void updateInputNumber(String number) {
            Log.i(TAG,"updateInputNumber number = "+number);
        }

        @Override
        public void update3WayCallActiveChanged(int state) {
            Log.i(TAG,"update3WayCallActiveChanged state = "+state);
            updateThirdCallView(state);
        }

        @Override
        public void updateMutiCallerName(String name) {
            mCallStatus.updateMutiCallerName(name == null || "".equals(name) ? mNewNumber : name);
        }

        @Override
        public void updateMutiCallerNumber(String number) {
            Log.i(TAG,"updateMutiCallerNumber number = "+number);
            mNewNumber = number;
        }

        @Override
        public void updateMutiCallingTime(String time) {
            Log.i(TAG,"updateMutiCallingTime time = "+time);
            mCallStatus.updateMutiCallingTime(time);
        }
    };

    private void showOrHideKeyboard(){
        if (mKeyboardCL.getVisibility() == View.VISIBLE) {
            mFloatViewListener.onKeyboardOpen(false);
            mKeyboardCL.setVisibility(View.GONE);
            AAOP_HSkin
                    .with(mFloatRoot)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.frame_incoming_call)
                    .applySkin(false);
            AAOP_HSkin
                    .with(mShowDialingKeyboard)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_dialing_keyboard)
                    .applySkin(false);
        }else{
            mFloatViewListener.onKeyboardOpen(true);
            mKeyboardCL.setVisibility(View.VISIBLE);
            AAOP_HSkin
                    .with(mFloatRoot)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.frame_keyboardbox)
                    .applySkin(false);
            AAOP_HSkin
                    .with(mShowDialingKeyboard)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.selector_call_dialing_keyboard_hidden)
                    .applySkin(false);
        }
    }

    private void keyboardClick(String key){
        Log.i(TAG,"keyboardClick key = " + key);
        EditUtils.addChar(mKeyboardET, key);
        mBTCallManager.keyboardClick(key);
    }

    private void updateThirdCallView(int state) {
        switch (state){
            //三方来电-来电中
            case Constant.THIRD_STATUS_COME_FOUR:
            case Constant.THIRD_STATUS_COME:
                mCallStatus = mThirdWayInCommingState;
                mCallStatus.updateDisplayStatus();
                mCallStatus.updateMutiCallingTime(mContext.getString(R.string.incomming));
                break;
            //三方来电-通话中
            case Constant.THIRD_STATUS_SPEAK:
                mCallStatus = mThirdWaySpeakingState;
                mCallStatus.updateDisplayStatus();
                break;
            case Constant.THIRD_STATUS_HOLD:
                mCallStatus = mThirdWayHoldState;
                mCallStatus.updateDisplayStatus();
                break;
            case Constant.THIRD_STATUS_DIAL:
            case Constant.THIRD_STATUS_GO:
                mCallStatus = mThirdWayGoState;
                mCallStatus.updateDisplayStatus();
                mCallStatus.updateMutiCallingTime(mContext.getString(R.string.dialing));
                break;
            default:break;
        }
    }

    public View getFloatView(){
        return mFloatRoot;
    }

    public void notifyAnswerByKey(String key){
        if(key.equals("0")){
            if(null != mBTCallManager){
                mBTCallManager.answerCall();
            }
        }else if(key.equals("3")){
            if(null != mBTCallManager){
                mBTCallManager.holdActiveCallAndAnwserCall();
            }
        }else if(key.equals("4")){
            if(null != mBTCallManager){
                mBTCallManager.handupActiveCallAndAnwserCall();
            }
        }
    }

}
