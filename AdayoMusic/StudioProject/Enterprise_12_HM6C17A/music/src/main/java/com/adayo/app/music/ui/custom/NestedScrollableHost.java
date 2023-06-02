package com.adayo.app.music.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

/**
 * @Auth: LinTan
 * @Date: 2021/7/20 15:00
 * @Desc: //ViewPager2的父容器, 用以解决其双层嵌套时的滑动冲突
 * 源址: https://www.jianshu.com/p/f467db3da9c0
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2. The scrollable element needs to be the immediate and only child of this host layout.
 * <p>
 * This solution has limitations when using multiple levels of nested scrollable elements
 * (e.g. a horizontal RecyclerView in a vertical RecyclerView in a horizontal ViewPager2).
 */

public class NestedScrollableHost extends FrameLayout {
    private final int touchSlop;
    private float initialX;
    private float initialY;

    public NestedScrollableHost(Context context) {
        super(context);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.getContext());
        this.touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public NestedScrollableHost(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.getContext());
        this.touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    private ViewPager2 getParentViewPager() {
        ViewParent viewParent = this.getParent();
        if (!(viewParent instanceof View)) {
            viewParent = null;
        }
        View view;
        for (view = (View) viewParent; view != null && !(view instanceof ViewPager2); view = (View) viewParent) {
            viewParent = view.getParent();
            if (!(viewParent instanceof View)) {
                viewParent = null;
            }
        }
        View view1 = view;
        if (!(view instanceof ViewPager2)) {
            view1 = null;
        }
        return (ViewPager2) view1;
    }

    private View getChild() {
        return this.getChildCount() > 0 ? this.getChildAt(0) : null;
    }

    private boolean canChildScroll(int orientation, float delta) {
        int direction = -((int) Math.signum(delta));
        View view;
        boolean b = false;
        switch (orientation) {
            case 0:
                view = this.getChild();
                b = view != null && view.canScrollHorizontally(direction);
                break;
            case 1:
                view = this.getChild();
                b = view != null && view.canScrollVertically(direction);
                break;
            default:
                //throw (Throwable)(new IllegalArgumentException());
        }
        return b;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        handleInterceptTouchEvent(e);
        return super.onInterceptTouchEvent(e);
    }

    private void handleInterceptTouchEvent(MotionEvent e) {
        ViewPager2 viewPager2 = this.getParentViewPager();
        if (viewPager2 != null) {
            int orientation = viewPager2.getOrientation();
            if (this.canChildScroll(orientation, -1.0F) || this.canChildScroll(orientation, 1.0F)) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    this.initialX = e.getX();
                    this.initialY = e.getY();
                    this.getParent().requestDisallowInterceptTouchEvent(true);
                } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    float dx = e.getX() - this.initialX;
                    float dy = e.getY() - this.initialY;
                    boolean isVpHorizontal = orientation == 0;
                    float scaledDx = Math.abs(dx) * (isVpHorizontal ? 0.5F : 1.0F);
                    float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1.0F : 0.5F);
                    if (scaledDx > (float) this.touchSlop || scaledDy > (float) this.touchSlop) {
                        if (isVpHorizontal == scaledDy > scaledDx) {
                            this.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                            this.getParent().requestDisallowInterceptTouchEvent(this.canChildScroll(orientation, isVpHorizontal ? dx : dy));
                        }
                    }
                }
            }
        }
    }
}