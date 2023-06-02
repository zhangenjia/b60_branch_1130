package com.adayo.app.ats.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.appcompat.widget.AppCompatImageView;

import static com.adayo.app.ats.util.Constants.ATS_VERSION;


public class RoundLightBarView extends AppCompatImageView {
    private static final String TAG = ATS_VERSION + RoundLightBarView.class.getSimpleName();

    //进度条画笔
    private Paint mProgressPaint;


    public RoundLightBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public RoundLightBarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //初始化画笔
        initPaint();

    }

    private void initPaint() {
        //画彩色圆弧的画笔
        mProgressPaint = new Paint();
        //抗锯齿
        mProgressPaint.setAntiAlias(true);
        // 防抖动
        mProgressPaint.setDither(true);
        // 开启图像过滤，对位图进行滤波处理。
        mProgressPaint.setFilterBitmap(true);
        mProgressPaint.setColor(Color.RED);
        //空心圆
        mProgressPaint.setStyle(Paint.Style.STROKE);
        //设置笔刷样式为原型
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        //设置圆弧粗
        mProgressPaint.setStrokeWidth(5);
        PathEffect effects = new DashPathEffect(new float[]{20, 20}, 20);
        mProgressPaint.setPathEffect(effects);
        //将绘制的内容显示在第一次绘制内容之上
        mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cx = getWidth()/2; // x-coordinate of center of the screen
        int cy = getHeight()/2; //
        canvas.drawCircle(cx, cy, 75, mProgressPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        createRotateAnimation();
    }

    private void createRotateAnimation() {
        RotateAnimation   anim =  new RotateAnimation(0, 36000, 80, 80);
        LinearInterpolator ll = new LinearInterpolator();
        anim.setInterpolator(ll);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(200000);
        startAnimation(anim);
    }


}