package com.adayo.app.launcher.wecarflow.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author Yiwen.Huan
 * created at 2021/12/6 11:21
 */
public class BlurTransitionView extends View {

    ValueAnimator showAnimator, hideAnimator;

    public BlurTransitionView(Context context) {
        super(context);
        init();
    }

    public BlurTransitionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurTransitionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BlurTransitionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        setVisibility(View.GONE);
        initAnimator();
        initPaint();
    }

    private void initAnimator() {
        showAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        hideAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
    }

    private static final String TAG = "BlurTransitionView";

    private Bitmap blurBitmap;
    private Paint paint;
    int[] position;

    private void initPaint() {
        position = new int[2];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);

        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(TAG, "onLayoutChange() called with: v = [" + v + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "], oldLeft = [" + oldLeft + "], oldTop = [" + oldTop + "], oldRight = [" + oldRight + "], oldBottom = [" + oldBottom + "]");
            }
        });

        setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d(TAG, "onScrollChange() called with: v = [" + v + "], scrollX = [" + scrollX + "], scrollY = [" + scrollY + "], oldScrollX = [" + oldScrollX + "], oldScrollY = [" + oldScrollY + "]");
            }
        });

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "onScrollChanged() called with: l = [" + l + "], t = [" + t + "], oldl = [" + oldl + "], oldt = [" + oldt + "]");
    }



    /**
     * 设置图片
     *
     * @param bitmap    要被高斯模糊的图
     * @param blurRadio 模糊半径，0~25
     * @param scale     缩放参数，0~1
     */
    public void setBitmap(Bitmap bitmap, int blurRadio, float scale) {
        recycleBitmap();
        blurBitmap = getBlurBitmap(bitmap, blurRadio, scale);
        invalidate();
    }

    public void show(long time) {
        if (hideAnimator.isRunning()) {
            hideAnimator.cancel();
        }
        if (showAnimator.isRunning()) {
            return;
        }
        if (getAlpha() == 1 && View.VISIBLE == getVisibility()) {
            return;
        }
        setVisibility(VISIBLE);
        showAnimator.setDuration(time);
        showAnimator.start();
    }

    public void hide(long time) {
        if (showAnimator.isRunning()) {
            showAnimator.cancel();
        }
        if (hideAnimator.isRunning()) {
            return;
        }
        if (0 == time) {
            setVisibility(GONE);
            return;
        }
        if (getAlpha() == 0) {
            return;
        }
        hideAnimator.setDuration(time);
        hideAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == blurBitmap) {
            return;
        }

        getLocationOnScreen(position);
        Log.d(TAG, "onDraw() called with: " + position[0] + " | " + position[1]);
        Log.d(TAG, "onDraw() called with: " + getWidth() + " | " + getHeight());

        Rect srcRect = new Rect(position[0], position[1], position[0] + getWidth(), position[1] + getHeight());
        Rect dstRect = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(blurBitmap, srcRect, dstRect, paint);
    }


    private Bitmap getBlurBitmap(Bitmap source, int radius, float scale) {

        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        int width = Math.round(srcWidth * scale);
        int height = Math.round(srcHeight * scale);

        Bitmap inputBmp = Bitmap.createScaledBitmap(source, width, height, false);

        RenderScript renderScript = RenderScript.create(getContext());


        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);

        scriptIntrinsicBlur.setRadius(radius);

        scriptIntrinsicBlur.forEach(output);

        output.copyTo(inputBmp);

        renderScript.destroy();

        if (scale != 1) {
            inputBmp = Bitmap.createScaledBitmap(inputBmp, srcWidth, srcHeight, false);
        }
        return inputBmp;
    }


    private void recycleBitmap() {
        if (null != blurBitmap) {
            blurBitmap.recycle();
            blurBitmap = null;
        }
    }


}
