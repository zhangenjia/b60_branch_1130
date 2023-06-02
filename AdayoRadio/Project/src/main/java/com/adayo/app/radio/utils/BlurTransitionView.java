package com.adayo.app.radio.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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


    Rect srcRect;
    Rect dstRect;

    private void init() {
        srcRect = new Rect();
        dstRect = new Rect();
        roundRadio = BlurBitmapCache.getInstance().getRoundRadio();
        setVisibility(View.GONE);
        initAnimator();
        initPaint();
    }

    private void initAnimator() {
        showAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        hideAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        Log.d(TAG, "initAnimator: hideAnimator = "+hideAnimator+ " showAnimator = "+showAnimator);
    }

    private static final String TAG = "BlurTransitionView";

    private float roundRadio = 20;
    private Paint paint;
    int[] position;
    private PorterDuffXfermode mode;

    private void initPaint() {
        position = new int[2];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);

        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        paint.setColor(Color.RED);
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

    private void getScreenPosition() {
        getLocationOnScreen(position);
        srcRect.left = position[0];
        srcRect.top = position[1];
        srcRect.right = position[0] + getWidth();
        srcRect.bottom = position[1] + getHeight();

        dstRect.left = 0;
        dstRect.top = 0;
        dstRect.right = getWidth();
        dstRect.bottom = getHeight();
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
        if (null == BlurBitmapCache.getInstance().getBlurBitmap()) {
            return;
        }
        RadioAPPLog.i(RadioAPPLog.TAG,TAG+  " - onDraw: failed because ");
        getScreenPosition();
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        canvas.drawRoundRect(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom, roundRadio, roundRadio, paint);
        paint.setXfermode(mode);
        canvas.drawBitmap(BlurBitmapCache.getInstance().getBlurBitmap(), srcRect, dstRect, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);

    }
}
