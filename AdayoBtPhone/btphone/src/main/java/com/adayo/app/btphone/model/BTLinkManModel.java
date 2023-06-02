package com.adayo.app.btphone.model;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.adayo.app.btphone.callback.IBTLinkManCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.bpresenter.bluetooth.contracts.IPhoneContactsContract;
import com.adayo.bpresenter.bluetooth.presenters.BluetoothController;
import com.adayo.bpresenter.bluetooth.presenters.PhoneContactsPresenter;
import com.adayo.common.bluetooth.bean.PeopleInfo;

import java.util.List;

public class BTLinkManModel {

    private static final String TAG = Constant.TAG + BTLinkManModel.class.getSimpleName();
    private Context mContext;
    private IPhoneContactsContract.IPhoneContactsPresenter mPhoneContactsPresenter;
    private IBTLinkManCallback mBTContactsCallback;

    public BTLinkManModel(Context context, final Activity activity){
        mContext = context;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPhoneContactsPresenter = new PhoneContactsPresenter(mContext);
                mPhoneContactsPresenter.setResumeActivity(activity);
                mPhoneContactsPresenter.setView(mPhoneContactsView);
                mPhoneContactsView.setPresenter(mPhoneContactsPresenter);
                mPhoneContactsPresenter.init();
            }
        });

    }


    public int registerContactInfo(IBTLinkManCallback btContactsCallback){
        mBTContactsCallback = btContactsCallback;
        return 0;
    }

    public int unregisterContactInfo(IBTLinkManCallback btContactsCallback){
        if(null != mBTContactsCallback){
            mBTContactsCallback = null;
        }
        return 0;
    }

    public int dialCall(String number){
        if(BluetoothController.getInstance().isOnCalling()){
            Log.i(TAG,"通话中，车机端不允许打三方电话");
            return -1;
        }
        if(null == mPhoneContactsPresenter){
            return -1;
        }
        mPhoneContactsPresenter.dialCall(number);
        return 0;
    }

    public int downloadContacts(){
        if(null == mPhoneContactsPresenter){
            return -1;
        }
        mPhoneContactsPresenter.downloadContacts();
        return 0;
    }

    public int cancelDownloadContacts(){
        if(null == mPhoneContactsPresenter){
            return -1;
        }
        mPhoneContactsPresenter.cancelDownloadContacts();
        return 0;
    }

    public int searchContacts(String info){
        if(null == mPhoneContactsPresenter){
            return -1;
        }
        mPhoneContactsPresenter.searchContacts(info);
        return 0;
    }

    public int setSearchMode(boolean isSearch){
        if(null == mPhoneContactsPresenter){
            return -1;
        }
        mPhoneContactsPresenter.setSearchMode(isSearch);
        return 0;
    }


    IPhoneContactsContract.IPhoneContactsView mPhoneContactsView = new IPhoneContactsContract.IPhoneContactsView() {

        @Override
        public void showAsk2LoadContacts() {
            Log.i(TAG,"showAsk2LoadContacts");
        }

        /**
         *
         * @param syncState SYNCHRONIZING,
         *         SUCCESSED,
         *         FAILED,
         *         INTERRUPTED,
         *         READY;
         */
        @Override
        public void updateContactsSyncState(BusinessConstants.SyncState syncState) {
            Log.i(TAG, "updateContactsSyncState(), status : " + syncState);
            if(null == mBTContactsCallback){
                Log.i(TAG,"mBTContactsCallback is null , return");
                return;
            }
            mBTContactsCallback.updateContactsSyncState(syncState.toString());
        }

        /**
         * 更新联系人列表和首字母
         * @param list
         * @param list1
         */
        @Override
        public void updateContactsList(List<PeopleInfo> list, List<String> list1) {
            if (list == null || list.size() == 0){
                if (list == null){
                    Log.d(TAG, "updateContactsList: list == null");
                }else {
                    Log.d(TAG, "updateContactsList: size() == 0");
                }
                return;
            }
            Log.i(TAG, "updateContactsList(), list.size : " + list.size());
            if(null == mBTContactsCallback){
                Log.i(TAG,"mBTContactsCallback is null , return");
                return;
            }
            mBTContactsCallback.updateContactsList(list,list1);
        }

        /**
         * 显示没有联系人的提示
         */
        @Override
        public void showNoContactsAlert() {
            mBTContactsCallback.updateNoContactsAlertState(true);
            Log.i(TAG,"showNoContactsAlert");
        }

        /**
         * 隐藏没有联系人的提示
         */
        @Override
        public void hideNoContactsAlert() {
            mBTContactsCallback.updateNoContactsAlertState(false);
            Log.i(TAG,"hideNoContactsAlert");
        }

        /**
         * 搜索联系人时，搜索不到时
         */
        @Override
        public void showNotFoundContactsAlert(String s) {
            Log.i(TAG,"showNotFoundContactsAlert");
        }

        @Override
        public void hideNotFoundContactsAlert() {
            Log.i(TAG,"hideNotFoundContactsAlert");
        }


        /**
         * 更新联系人列表
         * @param list
         */
        @Override
        public void updateContactsList(List<PeopleInfo> list) {
            Log.i(TAG,"updateContactsList");
        }

        @Override
        public void onPbapDownloadNotify(int i, int i1, int i2, int i3) {
            Log.i(TAG,"onPbapDownloadNotify");
        }

        @Override
        public void updateSyncButtonState(boolean b) {
            Log.i(TAG,"updateSyncButtonState");
        }

        @Override
        public void updateFatchContactsState(int i) {
            Log.i(TAG,"updateFatchContactsState");
        }

        @Override
        public void updateHfpState(boolean b) {
            Log.i(TAG,"updateHfpState");
            if(null == mBTContactsCallback){
                Log.i(TAG,"mBTContactsCallback is null , return");
                return;
            }
            mBTContactsCallback.updateHfpState(b);
        }

        @Override
        public void updateA2DPState(boolean b) {
            Log.i(TAG,"updateA2DPState");
            if(null == mBTContactsCallback){
                Log.i(TAG,"mBTContactsCallback is null , return");
                return;
            }
            mBTContactsCallback.updateA2DPState(b);
        }

        @Override
        public void setPresenter(IPhoneContactsContract.IPhoneContactsPresenter iPhoneContactsPresenter) {
            Log.i(TAG,"setPresenter");
        }

        @Override
        public void removePresenter(IPhoneContactsContract.IPhoneContactsPresenter iPhoneContactsPresenter) {
            Log.i(TAG,"removePresenter");
        }
    };

}
