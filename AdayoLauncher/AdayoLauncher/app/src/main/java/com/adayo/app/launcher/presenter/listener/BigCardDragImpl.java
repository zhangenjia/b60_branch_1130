package com.adayo.app.launcher.presenter.listener;

import android.util.Log;
import android.view.View;

public class BigCardDragImpl implements IBigCardDragListener {


    private BigCardCallBack mBigCardCallBack;
    private BottomBigCardCallBack mBottomBigCardCallBack;
    private static BigCardDragImpl mListener;

    public static BigCardDragImpl getInstance() {
        if (null == mListener) {
            synchronized (BigCardDragImpl.class) {
                if (null == mListener) {
                    mListener = new BigCardDragImpl();
                }
            }
        }
        return mListener;
    }

    @Override
    public void InitView(String id, int position) {
        if (mBigCardCallBack!=null){
            mBigCardCallBack.bigCardIdChangeToInitView(id,position);
        }
    }

    @Override
    public View AddView(String id, int position) {
        if (mBigCardCallBack!=null){
            View view = mBigCardCallBack.bigCardIdChangeToAddView(id, position);
            return view;
        }
        return null;
    }

    @Override
    public void ChangeInfoBigCard(String id, int position) {
        Log.d("qaz", "ChangeIdFromBottomBigCardToBigCard:  "+id);
        mBigCardCallBack.bigCardIdChange(id,position);
    }

    @Override
    public void addCallBackToBigCard(BigCardCallBack bigcardcallBack) {
        mBigCardCallBack = bigcardcallBack;
    }

    @Override
    public void removeBigCardCallBack() {

    }

    @Override
    public void ChangeInfoToBottomBigCard(String id, int position) {
        mBottomBigCardCallBack.bigCardIdChange(id,position);
    }

    @Override
    public void addCallBackToBottomBigCard(BottomBigCardCallBack bottomBigCardCallBack) {
        mBottomBigCardCallBack = bottomBigCardCallBack;
    }

    @Override
    public void removeBottomBigCardCallBack() {

    }


}

