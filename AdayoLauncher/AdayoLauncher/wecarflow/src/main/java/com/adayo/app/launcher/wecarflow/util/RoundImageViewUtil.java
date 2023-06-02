package com.adayo.app.launcher.wecarflow.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class RoundImageViewUtil extends androidx.appcompat.widget.AppCompatImageView {

    float width, height;
    private Paint paint;

    public RoundImageViewUtil(Context context) {
        this(context, null);
    }

    public RoundImageViewUtil(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageViewUtil(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF mRectF = new RectF(0,0, getWidth(),getHeight());
        Path path = new Path();
        //path划出一个圆角矩形，容纳图片,图片矩形区域设置比红色外框小，否则会覆盖住外框，随意控制
        path.addRoundRect(new RectF(7, 7, mRectF.right-7,mRectF.bottom-7), 40, 40, Path.Direction.CW);
        canvas.drawRoundRect(mRectF, 50, 50, paint); //画出红色外框圆角矩形
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}