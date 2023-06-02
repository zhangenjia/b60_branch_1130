package com.adayo.app.launcher.presenter.listener;

import android.view.MotionEvent;

public class TouchImpl implements ITouchListener {

    private static TouchImpl mTouchImpl;

    public static TouchImpl getInstance() {
        if (null == mTouchImpl) {
            synchronized (TouchImpl.class) {
                if (null == mTouchImpl) {
                    mTouchImpl = new TouchImpl();
                }
            }
        }
        return mTouchImpl;
    }

    private CallBack mCallBack;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return  mCallBack.callBack();

    }

    @Override
    public void addCallBack(CallBack callBack) {

        mCallBack = callBack;
    }

}
