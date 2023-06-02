package com.adayo.app.video.helper;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.lt.library.util.context.ContextUtil;

import java.util.ArrayList;
import java.util.List;

public class CtrlBarVisibilityHelper {
    private final Handler mHandler;
    private final MyGestureListener mGestureListener;
    private final GestureDetector mGestureDetector;
    private final boolean mDefVisible;
    private final long mDefVisibleTimeout;
    private final ViewGroup[] mCtrlBarViewGroups;
    private final View[] mCtrlBarViews;
    private View.OnTouchListener mOnTouchListener;
    private Runnable mRunnable;
    private boolean mShownCtrlBar;
    private boolean mAnchoredCtrlBar;
    private boolean mStared;

    {
        mHandler = new Handler(Looper.getMainLooper());
        mGestureListener = new MyGestureListener();
        mGestureDetector = new GestureDetector(ContextUtil.getAppContext(), mGestureListener);
    }

    public CtrlBarVisibilityHelper(boolean defVisible, long defVisibleTimeout, ViewGroup[] ctrlBarViewGroups, View[] ctrlBarViews) {
        mDefVisible = defVisible;
        mDefVisibleTimeout = defVisibleTimeout;
        mCtrlBarViewGroups = ctrlBarViewGroups;
        mCtrlBarViews = ctrlBarViews;
        addEvent();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addEvent() {
        View.OnTouchListener viewGroupListener = (v, event) -> {
            if (mStared) {
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                }
                if (MotionEvent.ACTION_UP == event.getAction() && mGestureListener.isScrolling()) {
                    return mGestureListener.onScrollFinished();
                }
            }
            if (mOnTouchListener != null) {
                return mOnTouchListener.onTouch(v, event);
            }
            return false;
        };
        for (ViewGroup viewGroup : mCtrlBarViewGroups) {
            viewGroup.setLongClickable(true);
            viewGroup.setOnTouchListener(viewGroupListener);
        }
        View.OnTouchListener viewListener = (v, event) -> {
            if (mStared) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    if (mShownCtrlBar) {
                        mHandler.removeCallbacks(mRunnable);
                    }
                    mAnchoredCtrlBar = true;
                } else if (MotionEvent.ACTION_UP == event.getAction()) {
                    if (mShownCtrlBar) {
                        mHandler.postDelayed(mRunnable, mDefVisibleTimeout);
                    }
                    mAnchoredCtrlBar = false;
                }
            }
            if (mOnTouchListener != null) {
                return mOnTouchListener.onTouch(v, event);
            }
            return false;
        };
        for (View view : mCtrlBarViews) {
            view.setLongClickable(true);
            view.setOnTouchListener(viewListener);
            for (View childView : queryChildViewTree(view, true)) {
                childView.setLongClickable(true);
                childView.setOnTouchListener(viewListener);
            }
        }
    }

    private void delEvent() {
        for (ViewGroup viewGroup : mCtrlBarViewGroups) {
            viewGroup.setLongClickable(false);
            viewGroup.setOnTouchListener(null);
        }
        for (View view : mCtrlBarViews) {
            view.setLongClickable(false);
            view.setOnTouchListener(null);
            for (View childView : queryChildViewTree(view, true)) {
                childView.setLongClickable(false);
                childView.setOnTouchListener(null);
            }
        }
    }

    private List<View> queryChildViewTree(View view, boolean isViewRoot) {
        List<View> result = new ArrayList<>();
        if (view == null) {
            return result;
        }
        if (!isViewRoot) {
            result.add(view);
        }
        if (!(view instanceof ViewGroup)) {
            return result;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            result.addAll(queryChildViewTree(childAt, false));
        }
        return result;
    }

    private void showCtrlBar(boolean isAutoHide) {
        for (View view : mCtrlBarViews) {
            view.setVisibility(View.VISIBLE);
        }
        if (isAutoHide) {
            mHandler.removeCallbacks(mRunnable);
            if (mRunnable == null) {
                mRunnable = this::hideCtrlBar;
            }
            mHandler.postDelayed(mRunnable, mDefVisibleTimeout);
        }
        mShownCtrlBar = true;
    }

    private void hideCtrlBar() {
        for (View view : mCtrlBarViews) {
            view.setVisibility(View.INVISIBLE);
        }
        mHandler.removeCallbacks(mRunnable);
        mShownCtrlBar = false;
    }

    public void start() {
        mStared = true;
        mShownCtrlBar = mDefVisible;
        if (mDefVisible) {
            showCtrlBar(true);
        } else {
            hideCtrlBar();
        }
    }

    public void stop(boolean isRestoreDefault) {
        mStared = false;
        mHandler.removeCallbacks(mRunnable);
        if (!isRestoreDefault) {
            return;
        }
        if (mDefVisible) {
            showCtrlBar(false);
        } else {
            hideCtrlBar();
        }
    }

    public void release() {
        delEvent();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void setOnTouchListener(View.OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean mScrolling;

        private boolean isScrolling() {
            return mScrolling;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (!mAnchoredCtrlBar) {
                if (!mShownCtrlBar) {
                    showCtrlBar(true);
                } else {
                    hideCtrlBar();
                }
            }
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mScrolling = true;
            if (mShownCtrlBar) {
                mHandler.removeCallbacks(mRunnable);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private boolean onScrollFinished() {
            mScrolling = false;
            if (mShownCtrlBar) {
                mHandler.postDelayed(mRunnable, mDefVisibleTimeout);
            }
            return false;
        }
    }
}
