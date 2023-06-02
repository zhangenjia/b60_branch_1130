package com.adayo.app.launcher.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.adayo.app.launcher.presenter.manager.WindowsManager;

public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = true;
    private static final String TAG = "Launcher_ViewPager";
    private static CustomViewPager mCustomViewPager;
    private Context mContext;


    public CustomViewPager(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public void setIsScanScroll(boolean isCanScroll) {

//        int i = WindowsManager.getInstance().geBottomDialogVisibility();
//        Log.d(TAG, "setIsScanScroll: "+isCanScroll);

        this.isCanScroll = isCanScroll;
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){

        if (isCanScroll) {
            try {
                boolean value = super.onInterceptTouchEvent(ev);
                Log.d(TAG, "onInterceptTouchEvent: value = " + value);
                return value;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Log.d(TAG, "dispatchTouchEvent: "+e);
            }
        }
        Log.d(TAG, "onInterceptTouchEvent: " + "false");
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return super.onTouchEvent(ev);
    }
}
