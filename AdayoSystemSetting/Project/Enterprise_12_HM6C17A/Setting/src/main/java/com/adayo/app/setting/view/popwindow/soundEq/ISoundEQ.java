package com.adayo.app.setting.view.popwindow.soundEq;

import android.view.MotionEvent;
import android.view.View;

public interface ISoundEQ<T,V> {
    void registerFragment(T activity,V viewbinding);
    void initView();
    void initData();
    void initEvent();
    void freeEvent();
}
