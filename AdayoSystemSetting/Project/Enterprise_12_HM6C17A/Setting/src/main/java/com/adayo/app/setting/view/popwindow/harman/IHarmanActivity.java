package com.adayo.app.setting.view.popwindow.harman;

import android.view.MotionEvent;
import android.view.View;

public interface IHarmanActivity<T,V> {
    void registerFragment(T activity,V viewbinding);
    void initView();
    void initData();
    void initEvent();
    boolean onTouch(View view, MotionEvent motionEvent);

}

