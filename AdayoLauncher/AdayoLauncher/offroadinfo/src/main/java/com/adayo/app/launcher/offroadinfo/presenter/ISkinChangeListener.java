package com.adayo.app.launcher.offroadinfo.presenter;


public interface ISkinChangeListener {
    void onSkinChange(int skin);
    void addCallBack(CallBack callBack);
    interface CallBack {
        void skinChangeCallBack(int skin);
    }

}
