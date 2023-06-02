package com.adayo.app.launcher.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView{
    private final static String TAG = "CustomRecyclerView";
    private OnLeftListScrollListener mOnLeftListScrollListener;
    private OnRightListScrollListener mOnRightListScrollListener;
    public CustomRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//大卡列表在左侧，小卡列表在右侧
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mOnLeftListScrollListener != null) {
                    mOnLeftListScrollListener.notMoving();
                }
                if (mOnRightListScrollListener != null) {
                    mOnRightListScrollListener.notMoving();
                }
                break;
        }
        boolean b = super.onTouchEvent(event);
        Log.d(TAG, "onTouchEvent:   " + event.getAction() + "  " + b);
        return b;
    }

    interface OnLeftListScrollListener {
        void notMoving();
    }

    public void addLeftListScrollListener(OnLeftListScrollListener onLeftListScrollListener) {
        this.mOnLeftListScrollListener = onLeftListScrollListener;
    }

    interface OnRightListScrollListener {
        void notMoving();
    }

    public void addRightListScrollListener(OnRightListScrollListener onRightListScrollListener) {
        this.mOnRightListScrollListener = onRightListScrollListener;
    }

}
