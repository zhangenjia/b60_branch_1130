package com.adayo.app.btphone.model;

import android.content.Context;
import android.util.Log;

import com.adayo.app.btphone.callback.ICallViewCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTCallLogManager;
import com.adayo.bpresenter.bluetooth.contracts.ICallContract;
import com.adayo.bpresenter.bluetooth.contracts.IMapsContract;
import com.adayo.bpresenter.bluetooth.presenters.CallPresenter;
import com.adayo.bpresenter.bluetooth.presenters.MapsPresenter;

import java.util.ArrayList;
import java.util.List;

public class BTCallModel {

    private static final String TAG = Constant.TAG + BTCallModel.class.getSimpleName();

    private Context mContext;
    private List<ICallViewCallback> callViewList = new ArrayList<>();
    private ICallContract.ICallPresenter mCallPresenter;
    private IMapsContract.IMapsPresenter mMapPresenter;

    public BTCallModel(Context context){
        mContext = context;
        mCallPresenter = new CallPresenter(mContext);
        mCallPresenter.setInputNumberMaxLength(20);
        mCallPresenter.setView(mCallView);
        mCallView.setPresenter(mCallPresenter);
        mCallPresenter.init();
        mMapPresenter = new MapsPresenter(mContext);
        mMapPresenter.init();
    }

    public int registerCallViewCallback(ICallViewCallback callViewCallback){
        if(!callViewList.contains(callViewCallback)){
            callViewList.add(callViewCallback);
        }
        return 0;
    }

    public int unregisterCallViewCallback(ICallViewCallback callViewCallback){
        if(callViewList.contains(callViewCallback)){
            callViewList.remove(callViewCallback);
        }
        return 0;
    }

    /**
     * 静音
     * @return
     */
    public int switchMicMute(){
        mCallPresenter.switchMicMute();
        return 0;
    }

    /**
     * 切换手机/车机
     * @return
     */
    public int switchAudioChannel(){
        mCallPresenter.switchAudioChannel();
        return 0;
    }

    /**
     * 接听
     * @return
     */
    public int answerCall(){
        mCallPresenter.answerCall();
        return 0;
    }

    /**
     * 挂断
     * @return
     */
    public int handupCall(){
        mCallPresenter.handupCall();
        return 0;
    }

    /**
     * 挂断当前来电，接听新电话
     * @return
     */
    public int handupActiveCallAndAnwserCall(){
        mCallPresenter.handupActiveCallAndAnwserCall();
        return 0;
    }

    /**
     * 保留当前来电，接听新电话
     * @return
     */
    public int holdActiveCallAndAnwserCall(){
        mCallPresenter.holdActiveCallAndAnwserCall();
        return 0;
    }

    /**
     *
     * @return
     */
    public int switchCall(){
        mCallPresenter.switchCall();
        return 0;
    }

    public int keyboardClick(String key){
        mCallPresenter.onKeyboardClick(key);
        return 0;
    }

    public int sendMessage(String message){
        Log.i(TAG,"mCallPresenter.getCurIncomingCallNum() = "+mCallPresenter.getCurIncomingCallNum()+
                " mCallPresenter.getMutiIncomingCallNumber() = "+mCallPresenter.getMutiIncomingCallNumber());
        if(null != mCallPresenter.getCurIncomingCallNum()){
            mMapPresenter.sendMessage(message,mCallPresenter.getCurIncomingCallNum());
        }else if(null != mCallPresenter.getMutiIncomingCallNumber()){
            mMapPresenter.sendMessage(message,mCallPresenter.getMutiIncomingCallNumber());
        }
        return 0;
    }

    public boolean isCurrentWeChatCall(){
        return mCallPresenter.isCurrentWeChatCall();
    }

    ICallContract.ICallView mCallView = new ICallContract.ICallView() {
        /**
         * 单方通话来电
         */
        @Override
        public void showIncomingCallView() {
            Log.i(TAG,"showIncomingCallView");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).showCallView(Constant.CALL_TYPE_INCOMINT_TELEGRAM);
            }
        }

        /**
         * 单方通话去电
         */
        @Override
        public void showOutgoingCallView() {
            Log.i(TAG,"showOutgoingCallView");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).showCallView(Constant.CALL_TYPE_RING_UP);
            }
        }

        /**
         * 单方通话中
         */
        @Override
        public void showActiveCallView() {
            Log.i(TAG,"showActiveCallView");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).showCallView(Constant.CALL_TYPE_ON_THE_LINE);
            }
        }

        /**
         * 三方通话----来电
         */
        @Override
        public void showMutiIncomingCallView() {
            Log.i(TAG,"showMutiIncomingCallView");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).showCallView(Constant.CALL_TYPE_THIRD_INCOMING);
            }
        }

        /**
         * 三方通话---通话中
         */
        @Override
        public void showMutiCallView() {
            Log.i(TAG,"showMutiCallView");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).showCallView(Constant.CALL_TYPE_THIRD_CALLING);
            }
        }

        /**
         * 三方电话，当前状态。
         * @param i
         */
        @Override
        public void on3WayCallActiveChanged(int state) {
            Log.i(TAG,"on3WayCallActiveChanged state = "+state);
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).update3WayCallActiveChanged(state);
            }
        }

        /**
         * 结束通话
         */
        @Override
        public void showEndCallView() {
            Log.i(TAG,"showEndCallView");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                Log.i(TAG,"showEndCallView 111");
                callViewList.get(i).showCallView(Constant.CALL_TYPE_END_CALL);
            }
        }

        @Override
        public void showHoldCallView() {
            Log.i(TAG,"showHoldCallView");
        }

        /**
         * 通话联系人名称
         * @param s
         */
        @Override
        public void updateCallerName(String s) {
            Log.i(TAG,"updateCallerName s = "+s);
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateCallerName(s);
            }
        }

        /**
         * 通话联系人号码
         * @param s
         */
        @Override
        public void updateCallerNumber(String s) {
            Log.i(TAG,"updateCallerNumber");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            BTCallLogManager.getInstance(mContext).setLastRecentCall(s);
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateCallerNumber(s);
            }
        }

        /**
         * 通话-时间
         * @param s
         */
        @Override
        public void updateCallingTime(String s) {
            Log.i(TAG,"updateCallingTime");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateCallingTime(s);
            }
        }

        /**
         * 三方通话-联系人姓名
         * @param s
         */
        @Override
        public void updateMutiCallerName(String s) {
            Log.i(TAG,"updateMutiCallerName");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateMutiCallerName(s);
            }
        }

        /**
         * 三方通话-电话号码
         * @param s
         */
        @Override
        public void updateMutiCallerNumber(String s) {
            Log.i(TAG,"updateMutiCallerNumber");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateMutiCallerNumber(s);
            }
        }

        /**
         * 三方通话-时间
         * @param s
         */
        @Override
        public void updateMutiCallingTime(String s) {
            Log.i(TAG,"updateMutiCallingTime");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateMutiCallingTime(s);
            }
        }

        /**
         * mic取消静音
         */
        @Override
        public void showMicUnmuteState() {
            Log.i(TAG,"showMicUnmuteState");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateMicMuteOrUnMuteState(1);
            }
        }

        /**
         * mic静音
         */
        @Override
        public void showMicMuteState() {
            Log.i(TAG,"showMicMuteState");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateMicMuteOrUnMuteState(0);
            }
        }

        /**
         * 车机输出
         */
        @Override
        public void showAudioOnCarState() {
            Log.i(TAG,"showAudioOnCarState");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateAudioOnCarOrPhoneState(0);
            }
        }

        /**
         * 手机输出
         */
        @Override
        public void showAudioOnPhoneState() {
            Log.i(TAG,"showAudioOnPhoneState");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateAudioOnCarOrPhoneState(1);
            }
        }

        /**
         * 通话中-输入内容
         * @param s
         */
        @Override
        public void updateInputNumber(String s) {
            Log.i(TAG,"updateInputNumber");
            if(callViewList.size() == 0){
                Log.i(TAG,"callViewList.size is empty , return");
                return;
            }
            for(int i = 0;i<callViewList.size();i++){
                callViewList.get(i).updateInputNumber(s);
            }
        }

        @Override
        public void showSwitchCallButton() {
            Log.i(TAG,"showSwitchCallButton");
        }

        @Override
        public void hideSwitchCallButton() {
            Log.i(TAG,"hideSwitchCallButton");
        }

        @Override
        public void showInvalidCallView() {
            Log.i(TAG,"showInvalidCallView");
        }

        @Override
        public void hideInvalidCallView() {
            Log.i(TAG,"hideInvalidCallView");
        }

        @Override
        public void updateInvalidCallInfo(String s, int i) {
            Log.i(TAG,"updateInvalidCallInfo");
        }

        /**
         * 通话联系人-头像
         * @param s
         */
        @Override
        public void updateCallerImage(String s) {
            Log.i(TAG,"updateCallerImage");
        }

        /**
         * 三方通话--头像
         * @param s
         */
        @Override
        public void updateMutiCallerImage(String s) {
            Log.i(TAG,"updateMutiCallerImage");
        }

        @Override
        public void updateMutiIncomingCallerName(String s) {
            Log.i(TAG,"updateMutiIncomingCallerName s :"+s);
        }

        /**
         * 更新三方来电号码
         * @param s
         */
        @Override
        public void updateMutiIncomingCallerNumber(String s) {
            Log.i(TAG,"updateMutiIncomingCallerNumber s :"+s);
        }

        /**
         * 更新三方来电联系人名字
         * @param iCallPresenter
         */
        @Override
        public void setPresenter(ICallContract.ICallPresenter iCallPresenter) {

        }

        @Override
        public void removePresenter(ICallContract.ICallPresenter iCallPresenter) {

        }
    };
}
