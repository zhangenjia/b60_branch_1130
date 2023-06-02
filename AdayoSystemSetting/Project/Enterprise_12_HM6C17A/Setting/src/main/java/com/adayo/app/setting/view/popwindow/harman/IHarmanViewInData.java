package com.adayo.app.setting.view.popwindow.harman;

import android.view.MotionEvent;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IHarmanViewInData {
    IHarmanViewInData init( IHarmanViewOutData iHarmanViewOutData);

    void initPostion(int left, int top);void initSoundMaxMin(int max,int min);

    void initFastBit(List<FastBitBean> fastBitBeans);

    void initStartPostion(int left, int top);void onTouchHarman(MotionEvent motionEvent);

    void resetView();

    void updataPostion(int x,int y);
}
