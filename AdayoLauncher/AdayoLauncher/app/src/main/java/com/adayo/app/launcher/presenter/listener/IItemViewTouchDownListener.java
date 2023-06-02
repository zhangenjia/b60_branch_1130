package com.adayo.app.launcher.presenter.listener;

public interface IItemViewTouchDownListener {

    void onDownEvent(float x);

    void addCallBack(CallBack callBack);

    interface CallBack {

        void callBack(float x);
    }

}
