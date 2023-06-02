package com.adayo.app.setting.view.popwindow.harman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;

import java.util.List;

public class AnimDrawBean {
    private final static String TAG = AnimDrawBean.class.getSimpleName();
    private boolean isLoop = false;private int mAnimPictureNumber;private List<Bitmap> mAnimPictureList;
    private int iconX;private int iconY;
    private int count = 0;AnimDrawBean.AnimDrawBeanListener mListener;
    private Paint paint;
    private boolean finishStat = true;
    private int startCount = 0;
    private Handler mListenerHandler;
    private Runnable mOnStart;
    private Runnable mOnRepeat;
    private Runnable mOnEnd;


    public AnimDrawBean(List<Bitmap> animPictureList, int iconX, int iconY, Paint paint) {
        mAnimPictureNumber = animPictureList.size();
        mAnimPictureList = animPictureList;
        this.iconX = iconX;
        this.iconY = iconY;
        this.paint = paint;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStartCount() {
        return startCount;
    }


    public void setStartCount(int startCount) {
        this.startCount = startCount;
        count = startCount;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean isLoop() {
        return isLoop;
    }

    public void setLoop(boolean loop) {
        isLoop = loop;
    }

    public int getAnimPictureNumber() {
        return mAnimPictureNumber;
    }

    public void setAnimPictureNumber(int animPictureNumber) {
        mAnimPictureNumber = animPictureNumber;
    }


    public int getIconX() {
        return iconX;
    }

    public void setIconX(int iconX) {
        this.iconX = iconX;
    }

    public int getIconY() {
        return iconY;
    }

    public void setIconY(int iconY) {
        this.iconY = iconY;
    }

    public void setAnimDrawBeanListener(AnimDrawBean.AnimDrawBeanListener listener) {
        mListener = listener;
    }

    public void start() {
        if (mListener != null) {
            mListener.onAnimationStart(this);
        }
    }

    public void end() {
        if (mListener != null) {
            mListener.onAnimationEnd(this);
        }
    }


public void setAnimPictureList(List<Bitmap> animPictureList) {
        mAnimPictureList = animPictureList;
    }

    public void drawBitmapAnim(Canvas canvas) {
canvas.drawBitmap(mAnimPictureList.get(count), iconX - mAnimPictureList.get(count).getWidth() / 2, iconY - mAnimPictureList.get(count).getHeight() / 2, paint);
        if (mAnimPictureList.size() >= count + 1) {
            setCount(count + 1);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }


    public static interface AnimDrawBeanListener {

        void onAnimationStart(AnimDrawBean animDrawBean);


        void onAnimationEnd(AnimDrawBean animDrawBean);


        void onAnimationRepeat(AnimDrawBean animDrawBean);
    }


}
