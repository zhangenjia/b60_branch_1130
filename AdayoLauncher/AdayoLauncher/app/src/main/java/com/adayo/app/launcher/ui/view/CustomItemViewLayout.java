package com.adayo.app.launcher.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class CustomItemViewLayout extends RelativeLayout {
    private static final String TAG = "CustomItemViewLayout";
    public CustomItemViewLayout(Context context) {
        super(context);
    }

    public CustomItemViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomItemViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "  onTouchEvent ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "  onTouchEvent ACTION_MOVE: ");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "  onTouchEvent ACTION_UP: ");
                break;
        }
        boolean b = super.onTouchEvent(event);
        Log.d(TAG, "onTouchEvent: "+b);
        return b;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "  dispatchTouchEvent ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "  dispatchTouchEvent ACTION_MOVE: ");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "  dispatchTouchEvent ACTION_UP: ");
                break;
        }
        boolean b = super.dispatchTouchEvent(ev);
        Log.d(TAG, "dispatchTouchEvent: "+b);
        return b;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "  onInterceptTouchEvent ACTION_DOWN: ");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "  onInterceptTouchEvent ACTION_MOVE: ");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "  onInterceptTouchEvent ACTION_UP: ");
                break;
        }
        boolean b = super.onInterceptTouchEvent(ev);
        Log.d(TAG, "onInterceptTouchEvent: "+b);
        return b;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        Log.d(TAG, "setOnTouchListener: ");
        super.setOnTouchListener(l);
    }


}
