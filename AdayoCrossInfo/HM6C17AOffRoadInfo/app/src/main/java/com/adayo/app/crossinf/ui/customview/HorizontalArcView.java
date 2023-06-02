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

public class HorizontalArcView extends RelativeLayout {
private final static String TAG = HorizontalArcView.class.getSimpleName();
    private  Paint paint1;
    private RectF oval;
    private float mRotationAngle = 0;
    private int color = 0xff7bb368;//默认绿色
    private Paint paint2;
    private int paint2Color = -1;
    public HorizontalArcView(Context context) {
        super(context);

    }

    public HorizontalArcView(Context context, AttributeSet attrs) {
        super(context, attrs);


        paint1 = new Paint();
        this.paint1.setAntiAlias(true);
        this.paint1.setColor(color);
        this.paint1.setStrokeWidth((float) 3.0);
        this.paint1.setStyle(Paint.Style.STROKE);

        paint2 = new Paint();
        DashPathEffect pathEffect = new DashPathEffect(new float[] { 10,10}, 0);//实线  虚线  整体向左向左偏移
        paint2.reset();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(5);
        paint2.setColor(0X4dffffff);
        paint2.setAntiAlias(true);
        paint2.setPathEffect(pathEffect);
        oval = new RectF();
        setWillNotDraw(false);

    }

    public HorizontalArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        oval.left=0;
        oval.top=0;
        oval.right=460;
        oval.bottom=460;
        canvas.drawArc(oval, 0, mRotationAngle, false, paint1);//绘制圆弧

        Path path = new Path();
        path.moveTo(235, 230);
        path.lineTo(550, 230);
        canvas.drawPath(path, paint2);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(462,462);
    }

    public void setRotationAngle(float angle){
        mRotationAngle = angle;
        postInvalidate();
    }

    public void setPaintColor(int color) {
        Log.d(TAG, "setPaintColor: "+color);
        this.color = color;
        paint1.setColor(color);
        invalidate();
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
