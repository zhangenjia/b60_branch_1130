package com.adayo.app.launcher.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomOverScrollLayout extends LinearLayout {

    private static final String TAG = "OverScrollLayout";
    private static final int ANIM_TIME = 250;
    private CustomRecyclerView childView;
    private Rect original = new Rect();
    private boolean isMoved = false;
    private boolean isJump = false;
    private float startXpos;
    private static final int mTabLayoutLeft = 0;
    private static final int mTabLayoutRight = 1;
    /**
     * 阻尼系数
     */
    private static final float DAMPING_COEFFICIENT = 0.5f;
    private boolean isSuccess = false;
    private int scrollXpos;
    private OverScrollLayoutListener overScrollLayout;
    private boolean isDraggingItem = false;
    private int position;


    public CustomOverScrollLayout(Context context) {
        this(context, null);
    }

    public CustomOverScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomOverScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        childView = (CustomRecyclerView) getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        original.set(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());//recyclerView在父布局中位置
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float touchXpos = ev.getX();
        if (touchXpos >= original.right || touchXpos <= original.left) {//如果手指在Layout外面
            if (isMoved) {
                recoverLayout();
            }
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startXpos = ev.getX();
            case MotionEvent.ACTION_MOVE:
                scrollXpos = (int) (ev.getX() - startXpos);
                boolean pullRight = scrollXpos > 0 && canPullRight();
                boolean pullLeft = scrollXpos < 0 && canPullLeft();
                if (isDraggingItem) {//isDraggingItem true 表示正在换卡
                    return super.dispatchTouchEvent(ev);
                }
                if (pullRight || pullLeft) {
                    if (isJump) {//如果已经在一档
                        if (overScrollLayout != null) {
                            overScrollLayout.jump();//跳转到另一个列表
                            isJump = false;
                            isMoved = false;
                            Log.d(TAG, "dispatchTouchEvent: jump to another");
                        }
                        boolean b = super.dispatchTouchEvent(ev);
                        Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE    -" + b);
                        return b;
                    }
                    Log.d(TAG, "dispatchTouchEvent isDraggingItem: " + isDraggingItem);
                    cancelChild(ev);//如果在拖动OverScrollLayout取消item卡片生成
                    int offset = (int) (scrollXpos * DAMPING_COEFFICIENT);
                    childView.layout(original.left + offset, original.top, original.right + offset, original.bottom);
                    isMoved = true;
                    isSuccess = false;
                    Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE    2=true");
                    return true;
                } else {
                    if (startXpos != ev.getX()) {
                        Log.d(TAG, "dispatchTouchEvent: reset ");
                        isJump = false;//复归
                    }
                    startXpos = ev.getX();
                    isMoved = false;
                    isSuccess = true;
                    boolean b = super.dispatchTouchEvent(ev);
                    Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE    -" + b);
                    return b;
                }
            case MotionEvent.ACTION_UP:
                if (isMoved) {
                    if (position == mTabLayoutLeft && scrollXpos < 0) {
                        Log.d(TAG, "dispatchTouchEvent: record once ");
                        isJump = true;//记一次

                    } else if (position == mTabLayoutRight && scrollXpos > 0) {
                        Log.d(TAG, "dispatchTouchEvent: record once ");
                        isJump = true;//记一次

                    }
                    recoverLayout();
                }
                boolean b = !isSuccess || super.dispatchTouchEvent(ev);
                Log.d(TAG, "dispatchTouchEvent: ACTION_UP    -" + b);
                return b;
            default:
                return true;
        }


    }


    /**
     * 取消子view已经处理的事件
     *
     * @param ev event
     */
    private void cancelChild(MotionEvent ev) {
        ev.setAction(MotionEvent.ACTION_CANCEL);
        boolean b = super.dispatchTouchEvent(ev);
        Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE    2--" + b);
    }

    /**
     * 位置还原
     */
    private void recoverLayout() {
        if (childView.getVisibility() != VISIBLE) {
            return;
        }
        TranslateAnimation anim = new TranslateAnimation(childView.getLeft() - original.left, 0, 0, 0);
        anim.setDuration(ANIM_TIME);
        childView.startAnimation(anim);
        childView.layout(original.left, original.top, original.right, original.bottom);
        isMoved = false;
    }


    /**
     * 判断是否可以右拉
     *
     * @return true：可以，false:不可以
     */
    private boolean canPullRight() {

        final int firstVisiblePosition = ((LinearLayoutManager) childView.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisiblePosition != 0 && childView.getAdapter().getItemCount() != 0) {
            Log.d(TAG, "canPullRight: false");
            return false;
        }
        int mostLeft = (childView.getChildCount() > 0) ? childView.getChildAt(0).getLeft() : 0;
        Log.d(TAG, "canPullRight: " + (mostLeft >= 0));
        return mostLeft >= 0;
    }

    /**
     * 判断是否可以左拉
     *
     * @return true：可以，false:不可以
     */
    private boolean canPullLeft() {
        final int lastItemPosition = childView.getAdapter().getItemCount() - 1;
        final int lastVisiblePosition = ((LinearLayoutManager) childView.getLayoutManager()).findLastVisibleItemPosition();
        if (lastVisiblePosition >= lastItemPosition) {
            final int childIndex = lastVisiblePosition - ((LinearLayoutManager) childView.getLayoutManager()).findFirstVisibleItemPosition();
            final int childCount = childView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = childView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getRight() <= childView.getRight() - childView.getLeft();
            }
        }
        return false;
    }


    public interface OverScrollLayoutListener {
        void jump();
    }

    public void addListener(OverScrollLayoutListener overScrollLayout) {
        this.overScrollLayout = overScrollLayout;
    }

    public void init(int position) {
        Log.d(TAG, "init: "+position);
        this.isJump = false;
        this.isMoved = false;
        this.position = position;
    }

    public void setDragItemState(boolean isDraggingItem) {
        Log.d(TAG, "setDragItemState: " + isDraggingItem);
        this.isDraggingItem = isDraggingItem;
    }
}