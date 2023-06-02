package com.adayo.app.camera.trackline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.app.camera.trackline.loadconfig.LoadConfig;


public class PointView extends View {
    private final static String TAG = PointView.class.getSimpleName();
    private float currentX = 40;
    private float currentY = 50;
    private LoadConfig.Point point;
    private float xPoint = 0;
    private float yPoint = 0;
    //    private Drawable mThumb;
    private float mLengthOfDiagonal;
    private boolean mHaveFocus = false;

    private Paint mPaint;
    private Paint mFillPaint;

    private Path mPath;
    private Path mFillPath;

    private Canvas mCanvas;

    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
//        mThumb = getResources().getDrawable(R.drawable.point);
    }

    public PointView(Context context) {
        super(context);
        initialize();
//        mThumb = getResources().getDrawable(R.drawable.point);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawPoint(canvas);
//        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point), currentx, currenty, new Paint());
    }

    private void initialize() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5);
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.STROKE);


    }

    private void drawPoint(Canvas canvas) {
        mCanvas = canvas;
        if (mCanvas == null) {
            Log.e("AdayoCamera", TAG + " - drawPoint: failed because canvas is null");
            return;
        }
        mCanvas.drawColor(Color.TRANSPARENT);
        mCanvas.drawLine(xPoint, yPoint - 53, xPoint, yPoint + 53, mPaint);  // 竖向画线
        mCanvas.drawLine(xPoint - 53, yPoint, xPoint + 53, yPoint, mPaint); // 横向画锯齿
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("AdayoCamera", TAG + " - onTouchEvent() called with: event.getAction() = [" + event.getAction() + "]");
        if (mHaveFocus) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("AdayoCamera", TAG + " - onTouchEvent: MotionEvent.ACTION_DOWN | point = " + point);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("AdayoCamera", TAG + " - onTouchEvent: MotionEvent.ACTION_MOVE | point = " + point);

                    break;
                case MotionEvent.ACTION_UP:
                    Log.d("AdayoCamera", TAG + " - onTouchEvent: MotionEvent.ACTION_UP | point = " + point);

                    break;

            }
            this.xPoint = event.getX();
            this.yPoint = event.getY();
            this.currentX = xPoint - 106 / 2;
            this.currentY = yPoint - 106 / 2;
            Log.d("AdayoCamera", TAG + " - onTouchEvent: currentX = " + currentX + " | currentY = " + currentY);
            Log.d("AdayoCamera", TAG + " - onTouchEvent: point = " + point);
            this.invalidate();
            return true;
        } else {
            return false;
        }
    }

    public float getCurrentX() {
        return currentX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
        this.xPoint = currentX + 106 / 2;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
        this.yPoint = currentY + 106 / 2;
    }

    public LoadConfig.Point getPoint() {
        return point;
    }

    public void setPoint(LoadConfig.Point point) {
        Log.d("AdayoCamera", TAG + " - setPoint() called with: point = [" + point + "]");
        this.point = point;
    }

    public float getXPoint() {
        return xPoint;
    }

    public void setXPoint(float xPoint) {
        this.xPoint = xPoint;
    }

    public float getYPoint() {
        return yPoint;
    }

    public void setYPoint(float yPoint) {
        this.yPoint = yPoint;
    }


    public float getLengthOfDiagonal() {
        mLengthOfDiagonal = (float) Math.sqrt(xPoint * xPoint + yPoint * yPoint);
        return mLengthOfDiagonal;
    }

    public boolean ismHaveFocus() {
        return mHaveFocus;
    }

    public void setHaveFocus(boolean mHaveFocus) {
        Log.d("AdayoCamera", TAG + " - setHaveFocus() called with: mHaveFocus = [" + mHaveFocus + "]");
        this.mHaveFocus = mHaveFocus;
    }

    public void moveLeft() {
        Log.d("AdayoCamera", TAG + " - moveLeft: mHaveFocus = " + mHaveFocus);
        if (mHaveFocus) {
            Log.d("AdayoCamera", TAG + " - moveLeft: point = " + point);
            currentX--;
            Log.d("AdayoCamera", TAG + " - moveLeft: currentX = " + currentX);
            this.invalidate();
        }
    }

    public void moveRight() {
        Log.d("AdayoCamera", TAG + " - moveRight: mHaveFocus = " + mHaveFocus);

        if (mHaveFocus) {
            Log.d("AdayoCamera", TAG + " - moveRight: point = " + point);
            currentX++;
            Log.d("AdayoCamera", TAG + " - moveRight: currentX = " + currentX);
            this.invalidate();
        }
    }

    public void moveUp() {
        Log.d("AdayoCamera", TAG + " - moveUp: mHaveFocus = " + mHaveFocus);

        if (mHaveFocus) {
            Log.d("AdayoCamera", TAG + " - moveUp: point = " + point);

            currentY--;
            Log.d("AdayoCamera", TAG + " - moveUp: currentY = " + currentY);

            this.invalidate();
        }
    }

    public void moveDown() {
        Log.d("AdayoCamera", TAG + " - moveDown: mHaveFocus = " + mHaveFocus);

        if (mHaveFocus) {
            Log.d("AdayoCamera", TAG + " - moveDown: point = " + point);

            currentY++;
            Log.d("AdayoCamera", TAG + " - moveDown: currentY = " + currentY);
            this.invalidate();
        }
    }


}
