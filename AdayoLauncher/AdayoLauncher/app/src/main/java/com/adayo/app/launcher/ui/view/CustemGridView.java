package com.adayo.app.launcher.ui.view;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adayo.app.launcher.R;

public class CustemGridView extends GridView {

    private static final String TAG = CustemGridView.class.getSimpleName();
    private final int PRESS_TIME = 250;//长按时间
    private int mDownX;
    private int mDownY;
    private int mMoveX;
    private int mMoveY;
    private int mOffset2Top;
    private int mOffset2Left;
    private int mPointToItemTop;
    private int mPointToItemLeft;
    private int mStatusHeight;
    private boolean isDraging = false;
    private Bitmap mBitmap;
    private int mTouchPostiion;
    private int mDownPostiion;
    private View mTouchItemView;
    private ImageView mDragImageView;//拖曳的View
    private WindowManager mWindowManager;//窗口管理器
    private WindowManager.LayoutParams mWindowLayoutParams;//窗口管理器布局
    private OnChangeListener onChangeListener;//交换事件监听器
    private boolean mOnInterceptTouchEvent = false;//默认不拦截，点击事件可触发
    private Handler mHandler = new Handler();
    private LinearLayout iv_select;
    private int colum = 6;
    private int offsetX;
    private int offsetY;

    public CustemGridView(Context context) {
        this(context, null);
    }

    public CustemGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustemGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mStatusHeight = getStatusHeight(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://获取重要坐标，创建BitMap


                mOnInterceptTouchEvent = false;//复归长按抬手后再次点击需要复归
                mDownX = (int) ev.getX();//触摸点到gridView左端距离
                mDownY = (int) ev.getY();//触摸点到gridView上端距离
                //根据按下的X,Y坐标获取所点击item的position
                mTouchPostiion = pointToPosition(mDownX, mDownY);
                mDownPostiion = pointToPosition(mDownX, mDownY);
                Log.d(TAG, "mTouchPostion: " + mTouchPostiion);
                if (mTouchPostiion == AdapterView.INVALID_POSITION) { //todo
                    Log.d(TAG, "invalid_position: ");
                    return super.dispatchTouchEvent(ev);
                }
                mHandler.postDelayed(mLongClickRunnable, PRESS_TIME);//使用Handler延迟dragResponseMS执行mLongClickRunnable


                //根据position获取该item所对应的View
                mTouchItemView = getChildAt(mTouchPostiion - getFirstVisiblePosition());
                mPointToItemTop = mDownY - mTouchItemView.getTop();//触摸的点到itemView上面距离
                mPointToItemLeft = mDownX - mTouchItemView.getLeft();//触摸的外框到item左边距离
                mOffset2Top = (int) (ev.getRawY() - mDownY);//gridView外框到屏幕上边的距离
                mOffset2Left = (int) (ev.getRawX() - mDownX);//gridView外框到屏幕左边的距离
                //开启mDragItemView绘图缓存
                mTouchItemView.setDrawingCacheEnabled(true);
                //获取mDragItemView在缓存中的Bitmap对象
                iv_select = (LinearLayout) mTouchItemView.findViewById(R.id.iv_select);


                if (iv_select != null) {//todo
                    iv_select.setAlpha(1);
                }


                mBitmap = Bitmap.createBitmap(mTouchItemView.getDrawingCache());
                mTouchItemView.destroyDrawingCache();//这一步很关键，释放绘图缓存，避免出现重复的镜像
                break;
            case MotionEvent.ACTION_MOVE://

                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                if (ev.getY() > getHeight() || ev.getY() < 0) { //拖拽点超出GridView区域则取消拖拽事件
                    onStopDrag();
                }
                //如果我们在按下的item上面移动，只要超过item的边界就移除mRunnable
                if (!isTouchInItem(mTouchItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                break;
            case MotionEvent.ACTION_UP://取消即将隐藏itemView
//                mOnInterceptTouchEvent = false;//复归
                //todo  -------------------
                if (iv_select != null) {
//                    cancelClickAnimation();
                    iv_select.setAlpha(0);
                }
                //todo  -------------------
                mHandler.removeCallbacks(mLongClickRunnable);
                break;
            case MotionEvent.ACTION_CANCEL:
//                mOnInterceptTouchEvent = false;//复归
                if (iv_select != null) {
                    cancelClickAnimation();
//                    iv_select.setAlpha(0);
                }
                if (mLongClickRunnable != null) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                isDraging = false;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            case MotionEvent.ACTION_SCROLL:

                break;
            case MotionEvent.ACTION_POINTER_UP:

                break;
            case MotionEvent.ACTION_MASK:

                break;
        }
        Log.d(TAG, "dispatchTouchEvent: " + super.dispatchTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isDraging && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    isDraging = true;
                    mMoveX = (int) ev.getX();//触摸点到控件触摸的控件左端距离
                    mMoveY = (int) ev.getY();//触摸点到控件触摸的控件上端轴距离
                    //拖动item
                    onDragItem(mMoveX, mMoveY);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "action_up: ");
                    onStopDrag();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG, "action_cancel: ");
                    break;
                case MotionEvent.ACTION_OUTSIDE:
                    Log.d(TAG, "action_outside: ");
                    break;
                case MotionEvent.ACTION_SCROLL:
                    Log.d(TAG, "action_scroll: ");
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    Log.d(TAG, "action_pointer_up: ");
                    break;
                case MotionEvent.ACTION_MASK:
                    Log.d(TAG, "action_mask: ");
                    break;
            }
            Log.d(TAG, "onTouchEvent: " + true);
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
            onStopDrag();
        }
        Log.d(TAG, "onTouchEvent: " + "super  " + isDraging + "  " + mDragImageView);
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {//返回true拦截该事件，false不拦截，
        return mOnInterceptTouchEvent;
    }

    //处理长按事件的线程3w
    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run:----------------------------------------- ");
            isDraging = true; //设置可以拖拽
            mOnInterceptTouchEvent = true;//长按后拦截事件分发，点击事件无效
            if (mTouchItemView != null) {
                mTouchItemView.setVisibility(View.INVISIBLE);//隐藏该ItemView
            }
            //根据我们按下的点显示ItemView镜像
            createDragView(mBitmap, mDownX, mDownY);
        }
    };


    //添加拖动View
    private void createDragView(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        dealPosition();
//        getChildAt(mTouchPostiion).getLeft()
        mWindowLayoutParams.x = getChildAt(mDownPostiion).getLeft() + offsetX;
        Log.d(TAG, "createDragView: " + getChildAt(mTouchPostiion).getLeft());
//        mWindowLayoutParams.y = downY - mPointToItemTop + mOffset2Top - mStatusHeight;
//        mWindowLayoutParams.y = getChildAt(mDownPostiion).getTop();
        mWindowLayoutParams.y = getChildAt(mDownPostiion).getTop()+offsetY;
        mWindowLayoutParams.alpha = 0.6f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    private void removeDragView() {//清空dragView
        if (mDragImageView != null) {
            Log.d(TAG, "removeDragView: ");
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }

    //是否点击在GridView的item上面
    private boolean isTouchInItem(View dragView, int x, int y) {
        Log.d("", "isTouchInItem: " + dragView);
        if (dragView == null) {//todo null
            return false;
        }
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }
        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }
        return true;
    }

    //拖动事件处理
    private void onDragItem(int moveX, int moveY) {//绘制dragView位置，itemView隐藏显示
//        mWindowLayoutParams.x = moveX;
        dealPosition();
        mWindowLayoutParams.x = moveX-(mDownX-getChildAt(mDownPostiion).getLeft()) + offsetX;

//      mWindowLayoutParams.y = moveY - mPointToItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.y = moveY-(mDownY-getChildAt(mDownPostiion).getTop())+offsetY;
        Log.d(TAG, "onDragItem: ndowLayoutParams.y "+getChildAt(mDownPostiion).getTop());
        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新DragView的位置
        onSwapItem(moveX, moveY);//Item的相互交换

//        mDragImageView.setScaleX();
//        mDragImageView.setScaleY();
//        mDragImageView.setPivotX();
//        mDragImageView.setPivotY();
    }

    //交换item,并且控制item之间的显示与隐藏效果
    private void onSwapItem(int moveX, int moveY) {//1 把拖动到view隐藏把之前的显示出来，把正在拖动的位置赋值为拖动到的view
        //获取我们手指移动到的那个item的position
        int tempPosition = pointToPosition(moveX, moveY);
        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mTouchPostiion && tempPosition != AdapterView.INVALID_POSITION) {
            getChildAt(tempPosition - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);//拖动到了新的item,新的item隐藏掉
            getChildAt(mTouchPostiion - getFirstVisiblePosition()).setVisibility(View.VISIBLE);//之前的item显示出来

            if (onChangeListener != null) {
                onChangeListener.onChange(mTouchPostiion, tempPosition);
            }

            mTouchPostiion = tempPosition;
        }
    }

    //停止拖拽我们将之前隐藏的item显示出来，并将DragView移除
    private void onStopDrag() {//1 把拖动的window关了 2 把正在拖动位置的itemView显示出来
        isDraging = false;
        int i = mTouchPostiion - getFirstVisiblePosition();
        Log.d("", "onStopDrag: " + i);
        if (mTouchPostiion - getFirstVisiblePosition() < 0) {
            return;
        }
        getChildAt(mTouchPostiion - getFirstVisiblePosition()).setVisibility(View.VISIBLE);
        removeDragView();
    }

    //Item交换事件监听
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    //获取状态栏高度
    private int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    //当item交换位置的时候回调的方法，我们只需要在该方法中实现数据的交换即可
    public interface OnChangeListener {
        void onChange(int from, int to);
    }

    public boolean getDragstate() {//
        return isDraging;
    }

    /**
     * 滑动至launcher取消点击产生的绿光效果
     */
    private void cancelClickAnimation() {
        ObjectAnimator mobjectAnimator = ObjectAnimator.ofFloat(iv_select, "alpha", 0);
        mobjectAnimator.setDuration(260);
        mobjectAnimator.setInterpolator(new AccelerateInterpolator());
        mobjectAnimator.start();
    }

    private void dealPosition() {

        if (mDownPostiion <= (colum - 1)) {
            if (mDownPostiion <= 2) {
                offsetX = 50;
            } else {
                offsetX = 0;
            }
        } else if ((mDownPostiion > (colum - 1)) && (mDownPostiion <= ((colum * 2) - 1))) {
            if ((mDownPostiion - colum) <= 2) {
                offsetX = 50;
            } else {
                offsetX = 0;
            }
        } else {
            if ((mDownPostiion - (2 * colum)) <= 2) {
                offsetX = 50;
            } else {
                offsetX = 0;
            }
        }
        if (mDownPostiion>5&&mDownPostiion<=11){
            offsetY = -20;
        }else {
            offsetY = 0;
        }

        Log.d(TAG, "dealPosition11111: " + offsetX);

    }
}