package com.adayo.app.systemui.windows.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
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
public class PullDownDumperLayout extends RelativeLayout implements View.OnTouchListener, Animator.AnimatorListener {
    /**
     * 取布局中的第一个子元素为下拉隐藏头部
     */
    private RelativeLayout mHeadLayout;
    private View mNewLayout;

    private boolean isAnimation = false;

    /**
     * 隐藏头部布局的高
     */
    public static final int mHeadLayoutHeight = 934;

    /**
     * 隐藏头部的布局参数
     */
    private MarginLayoutParams mHeadLayoutParams;
    private MarginLayoutParams mNewLayoutParams;

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
    private final int mBoundary = 540;

    private final int duration = 300;
    private boolean fromOutSide = false;
    private float alpha = 0f;

    public PullDownDumperLayout(Context context, AttributeSet attrs) {
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
            //将第一个子元素作为头部移出界面外
            mHeadLayout = (RelativeLayout) this.getChildAt(0);
            mHeadLayoutParams = (MarginLayoutParams) mHeadLayout.getLayoutParams();
            mHeadLayoutParams.topMargin = -mHeadLayoutHeight;
            mHeadLayout.setLayoutParams(mHeadLayoutParams);
            mHeadLayout.setAlpha(alpha);
            mNewLayout = this.getChildAt(1);
            mNewLayoutParams = (MarginLayoutParams) mNewLayout.getLayoutParams();
            mNewLayoutParams.topMargin = -mHeadLayoutHeight;
            mNewLayout.setLayoutParams(mNewLayoutParams);
            //TODO 设置手势监听器，不能触碰的控件需要添加android:clickable="true"
//            mHeadLayout.setOnTouchListener(this);
//            mNewLayout.setOnTouchListener(this);
            //标记已被初始化
            mOnLayoutIsInit = true;
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

    public boolean doAnimation(MotionEvent event, boolean fromOutside){
        if (!mOnLayoutIsInit || null == mHeadLayout) {
            return super.onTouchEvent(event);
        }
        fromOutSide = fromOutside;
        LogUtil.debugD(SystemUIContent.TAG, "fromOutSide == " + fromOutSide);
        LogUtil.debugD(SystemUIContent.TAG, "event.getAction() == " + event.getAction());
        LogUtil.debugD(SystemUIContent.TAG, "x--y onTouchEvent:x=====" + event.getRawX() + "----------y=====" + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getRawY();
                mMoveY = mDownY;
                break;
            case MotionEvent.ACTION_MOVE:
                VelocityTracker mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                yVelocity = mVelocityTracker.getYVelocity();//去绝对值。向左滑，值为负数

                float currY = event.getRawY();
                int vector = (int) (currY - mMoveY);//向量，用于判断手势的上滑和下滑
                mMoveY = currY;
                //判断是否为滑动
                if(isOpen && mHeadLayout.getTranslationY() >= mHeadLayoutHeight && vector > 0){
                    return false;
                }
                if (Math.abs(vector) == 0 || movingDistance >= mHeadLayoutHeight*1.15f) {
                    return false;
                }
                movingDistance = movingDistance + vector;
                if (isOpen) {
                    alpha = (mHeadLayoutHeight + movingDistance)*1.0f/mHeadLayoutHeight;
                    mHeadLayout.setTranslationY(mHeadLayoutHeight + movingDistance);
                    mHeadLayout.setAlpha(alpha);
                    mNewLayout.setTranslationY(mHeadLayoutHeight + movingDistance);
                } else {
                    alpha = movingDistance*1.0f/mHeadLayoutHeight;
                    mHeadLayout.setTranslationY(movingDistance);
                    mHeadLayout.setAlpha(alpha);
                    mNewLayout.setTranslationY(movingDistance);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                LogUtil.debugD(SystemUIContent.TAG, "yVelocity == " + yVelocity);
                if (yVelocity > referenceVelocity) {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                    } else {
                        startAnimation(movingDistance, mHeadLayoutHeight);
                    }
                    isOpen = true;
                    break;
                }

                if (yVelocity < -referenceVelocity) {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, 0);
                    } else {
                        startAnimation(movingDistance, 0);
                    }
                    isOpen = false;
                    break;
                }

                if (Math.abs(movingDistance) >= mBoundary) {
                    if (event.getRawY() - mDownY < 0) {
                        if (isOpen) {
                            startAnimation(mHeadLayoutHeight + movingDistance, 0);
                        } else {
                            startAnimation(movingDistance, 0);
                        }
                        isOpen = false;
                    } else {
                        if (isOpen) {
                            startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                        } else {
                            startAnimation(movingDistance, mHeadLayoutHeight);
                        }
                        isOpen = true;
                    }
                } else {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                    } else {
                        startAnimation(movingDistance, 0);
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
        LogUtil.debugD(SystemUIContent.TAG, "isAnimation == " + isAnimation);
        isAnimation = true;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        if (!isOpen) {
            mHeadLayout.setAlpha(0.0f);
            WindowsManager.dismissQsPanel();
        }
        fromOutSide = false;
        movingDistance = 0;
        isAnimation = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        fromOutSide = false;
        movingDistance = 0;
        isAnimation = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
    }

    public void startOpenAnimation(){
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        startAnimation(0, mHeadLayoutHeight);
        isOpen = true;
    }
    public void startCloseAnimation(){
        LogUtil.debugD(SystemUIContent.TAG, "isOpen == " + isOpen);
        startAnimation(mHeadLayoutHeight, 0);
        isOpen = false;
    }

    private void startAnimation(int from, int to) {
        if(isAnimation){
            return;
        }
        if (null != mHeadLayout) {
            LogUtil.debugD(SystemUIContent.TAG, "to = " + to + " ; from == " + from);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mHeadLayout, "translationY", from, to);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(duration);
            animator.addListener(this);
            animator.start();
            ObjectAnimator alphaAnimator  = ObjectAnimator.ofFloat(mHeadLayout, "alpha", alpha, to == 0 ? 0f : 1f);
            alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnimator.setDuration(duration);
            alphaAnimator.start();
            alpha = to == 0 ? 0f : 1f;
        }
        if (null != mNewLayout) {
            LogUtil.debugD(SystemUIContent.TAG, "to = " + to + " ; from == " + from);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mNewLayout, "translationY", from, to);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(duration);
            animator.addListener(this);
            animator.start();
        }
    }
}