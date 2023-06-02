package com.adayo.app.picture.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.adayo.app.picture.ui.base.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class TimerContainer extends RelativeLayout {
    private final static int MSG_TOUCHED_OVERTIME = 20001;
    private final static int MIN_CLICK_TIME_INTERVAL = 500;//最小点击时间间隔
    private GestureDetector mGestureDetector;
    private int mCallbackTime = 3000;
    private Callback mCallback = null;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TOUCHED_OVERTIME:
                    if (mCallback != null) {
                        mCallback.onTimeOver();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private Timer mTimer = null;
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            resetTimer();
            if (mCallback != null) {
                mCallback.onSingleTapUp();
            }
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            resetTimer();
            if (e2.getX() - e1.getX() > 80 && Math.abs(velocityX) > 800) {
                mCallback.onGestureFlingLeft();
                return true;
            } else if (e2.getX() - e1.getX() < -80 && Math.abs(velocityX) > 800) {
                mCallback.onGestureFlingRight();
                return true;
            }
            return true;
        }
    };

    public TimerContainer(Context context, AttributeSet attrs) {

        super(context, attrs);
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        }
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
        resetTimer();
    }

    public void setCallbackTime(int time) {
        mCallbackTime = time;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        if (mCallback != null) {
            mCallback.onViewTouched(ev);
            resetTimer();
        }
        return super.dispatchTouchEvent(ev);
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void resetTimer() {
        stopTimer();
        mTimer = new Timer();
        mTimer.schedule(new HideCtrlBarTimeTask(mHandler), mCallbackTime);
    }

    public interface Callback {
        void onViewTouched(MotionEvent ev);

        void onTimeOver();

        void onSingleTapUp();

        void onGestureFlingLeft();

        void onGestureFlingRight();
    }

    private static class HideCtrlBarTimeTask extends TimerTask {
        WeakReference<Handler> mHandler = null;

        HideCtrlBarTimeTask(Handler handler) {
            mHandler = new WeakReference<Handler>(handler);
        }

        @Override
        public void run() {
            Handler handler = mHandler.get();
            if (handler != null) {
                handler.sendEmptyMessage(MSG_TOUCHED_OVERTIME);
            } else {
                LogUtil.d(this.getClass().getCanonicalName(), "run: mHandler.get() == null");
            }
        }
    }
}
