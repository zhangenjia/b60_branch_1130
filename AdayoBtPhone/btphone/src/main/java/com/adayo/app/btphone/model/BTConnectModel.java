package com.adayo.app.btphone.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.adayo.app.btphone.callback.IBTConnectCallback;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.bpresenter.bluetooth.contracts.IBTWndMangerContract;
import com.adayo.bpresenter.bluetooth.presenters.BTWndManagerPresenter;

import java.util.Set;

public class BTConnectModel {

    private static final String TAG = Constant.TAG + BTConnectModel.class.getSimpleName();
    private Context mContext;
    private BTWndManagerPresenter mBTWndManagerPresenter;
    private boolean mHFTState;  //电话
    private boolean mA2DPState; //音频
    private IBTConnectCallback mCallback;

    public BTConnectModel(Context context){
        mContext = context;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mBTWndManagerPresenter = new BTWndManagerPresenter(mContext);
                mBTWndManagerPresenter.setView(mBtWndView);
                mBTWndManagerPresenter.init();
            }
        });

    }

    public boolean getHFPState(){
        Log.i(TAG,"getHFPState mHFTState = "+mHFTState);
        return mHFTState;
    }

    public boolean getA2DPState(){
        Log.i(TAG,"getA2DPState mA2DPState = "+mA2DPState);
        return mA2DPState;
    }

    public int registerBTConnectState(IBTConnectCallback callback){
        mCallback = callback;
        return 0;
    }

    public int unregisterBTConnectState(IBTConnectCallback callback){
        if(null != mCallback){
            mCallback = null;
        }
        return 0;
    }

    IBTWndMangerContract.IBTWndMangerView mBtWndView = new IBTWndMangerContract.IBTWndMangerView() {
        @Override
        public void showOrEnableBottomBar() {

        }

        @Override
        public void hideOrDisableBottomBar() {

        }

        @Override
        public void onTabSelectedChanged(BusinessConstants.FRAGMENT_INDEX fragmentIndex) {

        }

        @Override
        public void showWelcomeLoadingView() {

        }

        @Override
        public void hideWelcomeLoadingView() {

        }

        @Override
        public void finishUI() {

        }


        @Override
        public void updateHfpState(boolean newState) {
            Log.i(TAG,"updateHfpState:" + newState);
            mHFTState = newState;
            if(null == mCallback){
                Log.i(TAG,"mCallback is null , return");
                return;
            }
            mCallback.updateHFPState(mHFTState);
        }


        @Override
        public void updateA2DPState(boolean b) {
            Log.i(TAG,"updateA2DPState:" + b);
            mA2DPState = b;
            if(null == mCallback){
                Log.i(TAG,"mCallback is null , return");
                return;
            }
            mCallback.updateA2DPState(mA2DPState);
        }

        @Override
        public boolean showPage(BusinessConstants.FRAGMENT_INDEX fragment_index, Bundle bundle) {
            return false;
        }

        @Override
        public boolean showPages(Set<BusinessConstants.FRAGMENT_INDEX> set, Bundle bundle) {
            return false;
        }
    };
}
