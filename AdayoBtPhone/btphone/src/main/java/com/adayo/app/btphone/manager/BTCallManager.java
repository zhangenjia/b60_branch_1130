package com.adayo.app.btphone.manager;

import android.content.Context;

import com.adayo.app.btphone.callback.ICallViewCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.model.BTCallModel;

public class BTCallManager {

    private static final String TAG = Constant.TAG + BTCallManager.class.getSimpleName();

    private BTCallModel mBTCallMode;

    private volatile static BTCallManager mCallMgr;

    private BTCallManager(Context context){
        mBTCallMode = new BTCallModel(context);
    }

    public static BTCallManager getInstance(Context context) {
        if (mCallMgr == null) {
            synchronized (DialogManager.class) {
                if (mCallMgr == null) {
                    mCallMgr = new BTCallManager(context);
                }
            }
        }
        return mCallMgr;
    }

    public int registerCallViewCallback(ICallViewCallback callViewCallback){
        return mBTCallMode.registerCallViewCallback(callViewCallback);
    }

    public int unregisterCallViewCallback(ICallViewCallback callViewCallback){
        return mBTCallMode.unregisterCallViewCallback(callViewCallback);
    }

    /**
     * 静音
     * @return
     */
    public int switchMicMute(){
        return mBTCallMode.switchMicMute();
    }

    /**
     * 切换手机/车机
     * @return
     */
    public int switchAudioChannel(){
        return mBTCallMode.switchAudioChannel();
    }

    /**
     * 接听
     * @return
     */
    public int answerCall(){
        return mBTCallMode.answerCall();
    }

    /**
     * 挂断
     * @return
     */
    public int handupCall(){
        return mBTCallMode.handupCall();
    }

    /**
     * 挂断当前来电，接听新电话
     * @return
     */
    public int handupActiveCallAndAnwserCall(){
        return mBTCallMode.handupActiveCallAndAnwserCall();
    }

    /**
     * 保留当前来电，接听新电话
     * @return
     */
    public int holdActiveCallAndAnwserCall(){
        return mBTCallMode.holdActiveCallAndAnwserCall();
    }

    /**
     *
     * @return
     */
    public int switchCall(){
        return mBTCallMode.switchCall();
    }

    public int keyboardClick(String key){
        return mBTCallMode.keyboardClick(key);
    }

    public boolean isCurrentWeChatCall(){
        return mBTCallMode.isCurrentWeChatCall();
    }

    public int sendMessage(String message){
        return mBTCallMode.sendMessage(message);
    }

}
