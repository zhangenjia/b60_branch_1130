package com.adayo.app.launcher.presenter.listener;

import android.view.MotionEvent;

public interface ITouchListener {
    boolean onTouchEvent(MotionEvent event);
    void addCallBack(CallBack callBack);
    interface CallBack {
        boolean callBack();
    }

}
