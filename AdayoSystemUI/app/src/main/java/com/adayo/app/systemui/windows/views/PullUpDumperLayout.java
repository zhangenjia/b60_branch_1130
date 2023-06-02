package com.adayo.app.systemui.windows.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.utils.LogUtil;

/**
 * @author XuYue
 * @description:
 * @date :2021/9/23 13:45
 */
public class PullUpDumperLayout extends RelativeLayout implements View.OnTouchListener, Animator.AnimatorListener, Animator.AnimatorPauseListener {
    /**
     * 取布局中的第一个子元素为下拉隐藏头部
     */
    private RelativeLayout mHeadLayout;

    /**
     * 隐藏头部布局的高
     */
    public static final int mHeadLayoutHeight = 976;

    /**
     * 隐藏头部的布局参数
     */
    private MarginLayoutParams mHeadLayoutParams;

    /**
     * 判断是否为第一次初始化，第一次初始化需要把headView移出界面外
     */
    private boolean mOnLayoutIsInit = false;

    /**
     * 按下时的y轴坐标
     */
    private float mDownY;

    /**
     * 移动时，前一个坐标
     */
    private float mMoveY;

    private float yVelocity;
    private final int referenceVelocity = 1000;

    /**
     * 触发动画的分界线
     */
    private final int mBoundary = 10;

    private final int duration = 300;
    private boolean fromOutSide = false;
    private boolean needWaitInit = false;

    public PullUpDumperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 布局开始设置每一个控件
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //只初始化一次
        if (!mOnLayoutIsInit && changed) {
            LogUtil.debugI(SystemUIContent.TAG, "Init current view start");
            //将第一个子元素作为头部移出界面外
            mHeadLayout = (RelativeLayout) this.getChildAt(0);
            mHeadLayoutParams = (MarginLayoutParams) mHeadLayout.getLayoutParams();
            mHeadLayoutParams.topMargin = mHeadLayoutHeight;
            mHeadLayout.setLayoutParams(mHeadLayoutParams);
            mOnLayoutIsInit = true;
            if(needWaitInit){
                needWaitInit = false;
                startOpenAnimation();
            }
            LogUtil.debugI(SystemUIContent.TAG, "Init current view end");
        }
    }

    /**
     * 屏幕触摸操作监听器
     *
     * @return 返回false表示在执行onTouch后会继续执行onTouchEvent
     **/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(fromOutSide){
            return false;
        }
        return doAnimation(event, false);
    }

    private int movingDistance = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(fromOutSide){
            return false;
        }
        return doAnimation(event, false);
    }

//    public boolean isInitLayout(){
//        LogUtil.debugI(SystemUIContent.TAG, "mOnLayoutIsInit = " + mOnLayoutIsInit + " ; null != mHeadLayout = " + (null != mHeadLayout));
//        return mOnLayoutIsInit && null != mHeadLayout;
//    }

    public boolean doAnimation(MotionEvent event, boolean fromOutside){
        LogUtil.debugD(SystemUIContent.TAG, "mOnLayoutIsInit == " + mOnLayoutIsInit + " ; mHeadLayout = " + mHeadLayout);
        if(null != animator && animator.isRunning()){
            LogUtil.debugD(SystemUIContent.TAG, " animator isRunning ");
            return super.onTouchEvent(event);
        }
        if (!mOnLayoutIsInit || null == mHeadLayout) {
            if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                LogUtil.debugD(SystemUIContent.TAG, " animator isRunning ");
                needWaitInit = true;
            }
            return true;
        }

        fromOutSide = fromOutside;
        LogUtil.debugD(SystemUIContent.TAG, "fromOutSide == " + fromOutSide + " ; isOpen = " + isOpen);
        LogUtil.debugD(SystemUIContent.TAG, "event.getAction() == " + event.getAction());
        LogUtil.debugD(SystemUIContent.TAG, "x--y onTouchEvent:x=====" + event.getRawX() + "----------y=====" + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getRawY();
                mMoveY = mDownY;
                break;
            case MotionEvent.ACTION_MOVE:
                float currY = event.getRawY();
                int vector = (int) (currY - mMoveY);//向量，用于判断手势的上滑和下滑
                mMoveY = currY;
                //判断是否为滑动
                if(isOpen && mHeadLayout.getTranslationY() <= -mHeadLayoutHeight && vector < 0){
                    return false;
                }
                if (Math.abs(vector) == 0 || movingDistance < -mHeadLayoutHeight*1.0f) {
                    return false;
                }
                movingDistance = movingDistance + vector < -mHeadLayoutHeight ? -mHeadLayoutHeight : movingDistance + vector;
//                movingDistance = movingDistance + vector;
                LogUtil.debugD(SystemUIContent.TAG, "movingDistance = " + movingDistance + " ; isOpen = " + isOpen);
//                mHeadLayout.getAnimation().cancel();
                if (isOpen) {
                    mHeadLayout.setTranslationY(-mHeadLayoutHeight + movingDistance);
                } else {
                    mHeadLayout.setTranslationY(movingDistance);
                }
                LogUtil.debugD(SystemUIContent.TAG, "mHeadLayout = " + mHeadLayout.getTranslationY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                VelocityTracker mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                yVelocity = mVelocityTracker.getYVelocity();//去绝对值。向左滑，值为负数
                LogUtil.debugD(SystemUIContent.TAG, "yVelocity == " + yVelocity);
                if (yVelocity < -referenceVelocity) {
                    if (isOpen) {
                        startAnimation(-mHeadLayoutHeight + movingDistance, -mHeadLayoutHeight, true);
                    } else {
                        startAnimation(movingDistance, -mHeadLayoutHeight, true);
                    }
                    break;
                }

                if (yVelocity > referenceVelocity) {
                    if (isOpen) {
                        startAnimation(-mHeadLayoutHeight + movingDistance, 0, false);
                    } else {
                        startAnimation(movingDistance, 0, false);
                    }
                    break;
                }

                if(Math.abs(event.getRawY() - mDownY) < 10 && mDownY > 964){
                    if(isOpen){
                        startAnimation(-mHeadLayoutHeight + movingDistance, 0, false);
                    }else{
                        startAnimation(movingDistance, -mHeadLayoutHeight, true);
                    }
                    break;
                }

                if (Math.abs(movingDistance) >= mBoundary) {
                    if (event.getRawY() < mDownY) {
                        if (isOpen) {
                            startAnimation(-mHeadLayoutHeight + movingDistance, -mHeadLayoutHeight, true);
                        } else {
                            startAnimation(movingDistance, -mHeadLayoutHeight, true);
                        }
                    } else {
                        if (isOpen) {
                            startAnimation(-mHeadLayoutHeight + movingDistance, 0, false);
                        } else {
                            startAnimation(movingDistance, 0, false);
                        }
                    }
                } else {
                    if (isOpen) {
                        startAnimation(-mHeadLayoutHeight + movingDistance,  -mHeadLayoutHeight, true);
                    } else {
                        startAnimation(movingDistance, 0, false);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean isOpen = false;

    @Override
    public void onAnimationStart(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        fromOutSide = false;
        movingDistance = 0;
        if (!isOpen) {
            WindowsManager.setHvacPanelVisibility(View.GONE, 0, true, false);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        fromOutSide = false;
        movingDistance = 0;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
    }

    public void startOpenAnimation(){
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        if(mOnLayoutIsInit) {
            startAnimation((int) mHeadLayout.getTranslationY(), -mHeadLayoutHeight, true);
        }
    }
    public void startCloseAnimation(){
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        if(mOnLayoutIsInit) {
            startAnimation((int) mHeadLayout.getTranslationY(), 0, false);
        }
    }

    @Override
    public void onAnimationPause(Animator animation) {
        if(null != animator){
            animator.end();
        }
        LogUtil.debugD(SystemUIContent.TAG, "onAnimationPause == " + isOpen);
    }

    @Override
    public void onAnimationResume(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "onAnimationResume == " + isOpen);
    }

    private ObjectAnimator animator;
    private void startAnimation(int from, int to, boolean open) {
        if(null != animator && animator.isRunning()){
            LogUtil.debugD(SystemUIContent.TAG, " animator isRunning ");
            return;
        }
        isOpen = open;
        int durationTime;
        if(Math.abs(yVelocity/1000) > 2.4) {
            durationTime = Math.round(Math.abs(from - to) * 1000 / Math.abs(yVelocity));
        }else{
            durationTime = Math.round(Math.abs(from - to)*duration/mHeadLayoutHeight);
        }
        if (null != mHeadLayout) {
            LogUtil.debugD(SystemUIContent.TAG, "to = " + to + " ; from == " + from);
            animator = ObjectAnimator.ofFloat(mHeadLayout, "translationY", from, to);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(durationTime);
            animator.addListener(this);
            animator.addPauseListener(this);
            animator.start();
        }
    }
}