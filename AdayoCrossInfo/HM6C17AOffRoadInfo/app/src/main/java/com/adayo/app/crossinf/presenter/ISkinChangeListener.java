package com.adayo.app.crossinf.presenter;


public interface ISkinChangeListener {
    void onSkinChange(int skin);
    void addCallBack(CallBack callBack);
    interface CallBack {
        void callBack(int skin);
    }

}
