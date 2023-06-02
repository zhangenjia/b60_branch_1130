package com.adayo.app.systemui.bases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

@SuppressLint("AppCompatCustomView")
public class VerticalSeekBar extends SeekBar {

    private OnSeekBarChangeListener mOnSeekBarChangeListener;
    private int progress;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        //将SeekBar转转90度
        c.rotate(-90);
        //将旋转后的视图移动回来
        c.translate(-getHeight(),0);
        super.onDraw(c);
        setProgress(getProgress());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelected(true);
                refreshBar(event);
                if(null != mOnSeekBarChangeListener){
                    mOnSeekBarChangeListener.onStartTrackingTouch(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                setSelected(true);
                refreshBar(event);
                break;
            case MotionEvent.ACTION_UP:
                setSelected(false);
                refreshBar(event);
                if(null != mOnSeekBarChangeListener){
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                }
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
                setSelected(false);
                if(null != mOnSeekBarChangeListener){
                    mOnSeekBarChangeListener.onStopTrackingTouch(this);
                }
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    private void refreshBar(MotionEvent event){
        int i=getMax() - (int) ((getMax() - getMin()) * event.getY() / getHeight());
        //设置进度
        setProgress(i);
        if(null != mOnSeekBarChangeListener && i != progress && i >= this.getMin() && i <= this.getMax()){
            progress = i;
            mOnSeekBarChangeListener.onProgressChanged(this, i, true);
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
    }
}
