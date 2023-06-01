package com.adayo.app.btphone.model;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adayo.app.btphone.callback.IBTCallLogCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTCallLogManager;
import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.bpresenter.bluetooth.contracts.ICallLogContract;
import com.adayo.bpresenter.bluetooth.presenters.BTWndLifeManagerPresenter;
import com.adayo.bpresenter.bluetooth.presenters.BluetoothController;
import com.adayo.bpresenter.bluetooth.presenters.CallLogPresenter;
import com.adayo.common.bluetooth.bean.PeopleInfo;
import com.adayo.common.bluetooth.constant.BtDef;
import com.adayo.commontools.SourceConstantsDef;
import com.adayo.commontools.constants.BasePresenterConstants;

import java.util.List;


public class BTCallLogModel {

    private static final String TAG = Constant.TAG + BTCallLogModel.class.getSimpleName();
    private Context mContext;
    private IBTCallLogCallback mBTCallLogCallback;
    private ICallLogContract.ICallLogPresenter mCallLogPresenter;
    private String mLastRecentCallNumber;
    private boolean mNoCallLogInfo;

    public BTCallLogModel(Context context){
        mContext = context;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mCallLogPresenter = new CallLogPresenter(mContext, BtDef.CALLLOG_SYNC_PHONE, CallLog.Calls.MISSED_TYPE | CallLog.Calls.INCOMING_TYPE | CallLog.Calls.OUTGOING_TYPE);
                mCallLogPresenter.setView(callLogView);
                callLogView.setPresenter(mCallLogPresenter);
                mCallLogPresenter.init();
            }
        });
    }


    public int registerBTCallLog(IBTCallLogCallback btCallLogCallback){
        mBTCallLogCallback = btCallLogCallback;
        return 0;
    }

    public int unregisterBTCallLog(IBTCallLogCallback btCallLogCallback){
        if(null != mBTCallLogCallback){
            mBTCallLogCallback = null;
        }
        return 0;
    }

    public int dialCall(String number){
        if(BluetoothController.getInstance().isOnCalling()){
            Log.i(TAG,"通话中，车机端不允许打三方电话");
            return -1;
        }
        if(null == mCallLogPresenter){
            return -1;
        }
        mCallLogPresenter.dialCall(number);
        return 0;
    }

    public int downloadCallLog(){
        if(null == mCallLogPresenter){
            return -1;
        }
        mCallLogPresenter.downloadCallog();
        return 0;
    }

    public int cancelDownloadCallLog(){
        if(null == mCallLogPresenter){
            return -1;
        }
        mCallLogPresenter.cancelDownloadCallLog();
        return 0;
    }

    public int loadCallLog(String s){
        if(null == mCallLogPresenter){
            return -1;
        }
        mCallLogPresenter.getCallLog(s);
        return 0;
    }

    public String getLastRecentCall(){
        Log.i(TAG,"getLastRecentCall mLastRecentCallNumber = "+mLastRecentCallNumber);
        return mLastRecentCallNumber;
    }

    public void setLastRecentCall(String number){
        Log.i(TAG,"setLastRecentCall number = "+number);
        mLastRecentCallNumber = number;
    }

    public int setSearchMode(boolean isSearch){
        if(null == mCallLogPresenter){
            return -1;
        }
        mCallLogPresenter.setSearchMode(isSearch);
        return 0;
    }

    public boolean getNoCallLogInfo(){
        return mNoCallLogInfo;
    }

    ICallLogContract.ICallLogView callLogView = new ICallLogContract.ICallLogView() {
        /**
         * 返回所有通话记录
         */
        @Override
        public void updateAllCallLog(List<PeopleInfo> list) {
            Log.i(TAG,"peopleInfos.size : " + list.size());
            if (list.size() == 0) {
                return;
            }
            if(null == mBTCallLogCallback){
                Log.i(TAG,"mBTCallLogCallback is null , return");
                return;
            }
            setLastRecentCall(list.get(0).getNumber());
            mBTCallLogCallback.updateAllCallLog(list);
        }

        /**
         * 返回未接通话记录
         */
        @Override
        public void updateMissCallCallLog(List<PeopleInfo> list) {
            Log.i(TAG,"updateMissCallCallLog : " + list.size());
        }

        /**
         * 返回已接通话记录
         */
        @Override
        public void updateReceivedCallCallLog(List<PeopleInfo> list) {
            Log.i(TAG,"updateReceivedCallCallLog : " + list.size());
        }

        /**
         * 返回已拨通话记录
         */
        @Override
        public void updateOutGoingCallCallLog(List<PeopleInfo> list) {
            Log.i(TAG,"updateOutGoingCallCallLog : " + list.size());
        }

        /**
         * 当前没有通话记录
         */
        @Override
        public void showNoCallLogDataAlert() {
            Log.i(TAG,"showNoCallLogDataAlert");
            mNoCallLogInfo = true;
            if(null == mBTCallLogCallback){
                Log.i(TAG,"mBTCallLogCallback is null , return");
                return;
            }
            mBTCallLogCallback.showNoCallLogDataAlert();
        }

        /**
         * 当前有通话记录
         */
        @Override
        public void hideNoCallLogDataAlert() {
            Log.i(TAG,"hideNoCallLogDataAlert");
            mNoCallLogInfo = false;
            if(null == mBTCallLogCallback){
                Log.i(TAG,"mBTCallLogCallback is null , return");
                return;
            }
            mBTCallLogCallback.hideNoCallLogDataAlert();
        }

        @Override
        public void showNotFoundCallLogAlert() {
            Log.i(TAG,"showNotFoundCallLogAlert");
        }

        @Override
        public void hideNotFoundCallLogAlert() {
            Log.i(TAG,"hideNotFoundCallLogAlert");
        }

        /**
         * 通话记录同步状态
         */
        @Override
        public void updateCalllogSyncState(BusinessConstants.SyncState syncState) {
            Log.i(TAG,"updateCalllogSyncState(), status : " + syncState);
            if(null == mBTCallLogCallback){
                Log.i(TAG,"mBTCallLogCallback is null , return");
                return;
            }
            mBTCallLogCallback.updateCallLogSyncState(syncState.toString());
        }

        @Override
        public void updateHfpState(boolean b) {
            Log.i(TAG,"updateHfpState: b=" + b);

        }

        @Override
        public void updateA2DPState(boolean b) {
            Log.i(TAG,"updateA2DPState: b=" + b);

        }

        @Override
        public void setPresenter(ICallLogContract.ICallLogPresenter iCallLogPresenter) {

        }

        @Override
        public void removePresenter(ICallLogContract.ICallLogPresenter iCallLogPresenter) {

        }
    };
}
