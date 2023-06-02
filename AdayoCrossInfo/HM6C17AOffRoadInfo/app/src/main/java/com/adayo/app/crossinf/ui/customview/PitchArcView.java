package com.adayo.app.crossinf.ui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class PitchArcView extends RelativeLayout {
    private final static String TAG = PitchArcView.class.getSimpleName();
    private Paint paint2;
    private Paint paint;
    private RectF oval;
    private float mRotationAngle = 0;
    private int color = 0xff7bb368;
    private int paint2Color = -1;

    public PitchArcView(Context context) {
        super(context);
    }

    public PitchArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();

        paint.setAntiAlias(true);                       //设置画笔为无锯齿
        paint.setColor(color);
        paint.setStrokeWidth((float) 3.0);              //线宽
        paint.setStyle(Paint.Style.STROKE);
        oval = new RectF();

        paint2 = new Paint();
        DashPathEffect pathEffect = new DashPathEffect(new float[]{10, 10}, 0);//实线  虚线  整体向左向左偏移
        paint2.reset();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(5);
        paint2.setColor(0X4dffffff);
        paint2.setAntiAlias(true);
        paint2.setPathEffect(pathEffect);
        oval = new RectF();

        setWillNotDraw(false);
    }

    public PitchArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        //RectF对象
        oval.left = 0;                              //左边
        oval.top = 0;                                   //上边
        oval.right = 564;                             //右边
        oval.bottom = 564;                                //下边
        canvas.drawArc(oval, 0, mRotationAngle, false, paint);    //绘制圆弧

        Path path = new Path();
        path.moveTo(285, 282);
        path.lineTo(580, 282);
        canvas.drawPath(path, paint2);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(566, 566);
    }

    public void setRotationAngle(float angle) {
        mRotationAngle = angle;
        postInvalidate();
    }

    public void setPaintColor(int color) {
        Log.d(TAG, "setPaintColor: " + color);
        this.color = color;
        paint.setColor(color);
        postInvalidate();
    }

    public void setPaint2Color(int i) {
        Log.d(TAG, "setPaint2Color: " + i);
        if (paint2Color != i) {
            paint2Color = i;
            if (0 == i) {
                paint2.setColor(0X00FFFFFF);
            } else {
                paint2.setColor(0X4dffffff);
            }
            postInvalidate();
        }
    }
}
