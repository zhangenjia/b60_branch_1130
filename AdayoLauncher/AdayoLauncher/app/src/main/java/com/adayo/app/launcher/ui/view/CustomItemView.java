package com.adayo.app.launcher.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CustomItemView extends RelativeLayout {
    private final static String TAG = CustomItemView.class.getSimpleName();
    public CustomItemView(Context context) {
        super(context);
    }

    public CustomItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "dispatchTouchEvent: ");
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "onTouchEvent: ");
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.d(TAG, "onInterceptTouchEvent: ");
//        return super.onInterceptTouchEvent(ev);
//    }
}
