package com.adayo.app.camera.trackline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.trackline.function.LineViewUtils;
import com.adayo.app.camera.trackline.interfaces.ILineView;
import com.adayo.app.camera.trackline.interfaces.ITrackLinePoint;
import com.adayo.crtrack.FloatPoint;


public class LineView extends View implements ILineView {

    private final static String TAG = "LineView";

    private static final float TOUCH_TOLERANCE = 3;

    private static final int BLOCK_COUNT = 60;

    private Paint mPaint;
    private Paint mFillPaint;

    private Path mPath;
    private Path mFillPath;

    private Canvas mCanvas;

    private float mX;
    private float mY;
    private FloatPoint[][] mTrackPoints;
    private FloatPoint[][] mWarningLines;
    private int mAngle;
    private static final int BASE_ALPHA = 30;
    float eachAlphaStep = (float) (200.0 / BLOCK_COUNT);
    private static final int LINE_ALPHA = 230;

    private static final int COLOR_ORANGE = Color.rgb(255, 201, 14);
    private FloatPoint[][] mStaticTrackPoints;
    private FloatPoint[] mCtrlPoints;
    private FloatPoint[][] mTyrePoints;

    private Paint mStopPaint;


    private FloatPoint[] mStaticCtrlPoints;

    private FloatPoint[][] mStaticWarningLines;


    private int mMaxSteeringWheel = 720;


    private boolean mIsInitFinish = false;

    private ITrackLinePoint mITrackLinePoint;

    private void initialize() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(5);
        mPaint.setAlpha(128);
        mPaint.setStyle(Style.STROKE);

        mStopPaint = new Paint();
        mStopPaint.setAntiAlias(true);
        mStopPaint.setDither(true);
        mStopPaint.setColor(Color.YELLOW);
        mStopPaint.setStrokeWidth(5);
        mStopPaint.setAlpha(255);
        mStopPaint.setStyle(Style.STROKE);
        mStopPaint.setStrokeJoin(Paint.Join.ROUND);
        mStopPaint.setStrokeCap(Cap.SQUARE);
        mFillPaint = new Paint();
        // mFillPaint.setAntiAlias(true);
        mFillPaint.setDither(true);
        mFillPaint.setColor(Color.rgb(254, 205, 77));
        mFillPaint.setAlpha(150);
        mFillPaint.setStyle(Style.FILL);

        mPath = new Path();
        mFillPath = new Path();

    }

    public LineView(Context context) {
        super(context);
        initialize();
    }

    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }


    @Override
    public void setup(ITrackLinePoint iTrackLinePoint) {
        Log.d("AdayoCamera", TAG + " - " + "setup() called with: iTrackLinePoint = [" + iTrackLinePoint + "]");
        long time = System.currentTimeMillis();
        mITrackLinePoint = iTrackLinePoint;
        mITrackLinePoint.setILineView(this);
        mIsInitFinish = true;
        invalidate();
        Log.d("AdayoCamera", TAG + " - setup: spend time " + (System.currentTimeMillis() - time) + " this " + this);
    }


    @Override
    public void updateStaticList() {
        mStaticTrackPoints = mITrackLinePoint.getStaticTrackPoints();
        mStaticWarningLines = mITrackLinePoint.getStaticWarningLines();
        mStaticCtrlPoints = mITrackLinePoint.getStaticCtrlPoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long time = System.currentTimeMillis();
        super.onDraw(canvas);
        Log.d("AdayoCamera", TAG + " - onDraw:" + this);

        mCanvas = canvas;
        if (mCanvas == null) {
            Log.e("AdayoCamera", TAG + " - onDraw: failed because mCanvas is null");
            return;
        }
        mCanvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeCap(Cap.ROUND);

        if (mTrackPoints == null || mCtrlPoints == null) {
            Log.e("AdayoCamera", TAG + " - onDraw: failed because line data is null");
            return;
        }
        drawLineRight(mTrackPoints[0]);
        drawLineLeft(mTrackPoints[1]);
        drawWarningLines(); //中间那条警戒线

//        drawStaticLine();
//        drawTyreLine();  //画蓝色线 （最终需要注掉）=>蓝色轮胎印
        Log.d("AdayoCamera", TAG + " - onDraw:send time " + (System.currentTimeMillis() - time));
    }

    @Override
    public void updateTrackLine(int angle) {
        mTrackPoints = mITrackLinePoint.getTrackPoints(angle);
    }

    private void updateWarningLine(int angle) {
        mWarningLines = mITrackLinePoint.getWarningLines(angle);
    }

    private void updateCtrlPoints(int angle) {
        mCtrlPoints = mITrackLinePoint.getCtrlPoints(angle);
    }

    private void updateTyrePoints(int angle) {
        mTyrePoints = mITrackLinePoint.getTyrePoints(angle);
    }


    private void drawWarningLines() {
        mPaint.setColor(Color.RED);
        drawLine(mWarningLines[0], mPaint);
        mPaint.setColor(Color.RED);
        drawLineWarningLeft(mWarningLines[1], mPaint);
        drawLineWarningRight(mWarningLines[1], mPaint);
        mPaint.setColor(Color.RED);
        drawLineWarningLeft(mWarningLines[2], mPaint);
        drawLineWarningRight(mWarningLines[2], mPaint);
        mPaint.setColor(Color.YELLOW);
        drawLineWarningLeft(mWarningLines[3], mPaint);
        drawLineWarningRight(mWarningLines[3], mPaint);
        mPaint.setColor(Color.GREEN);
        drawLineWarningLeft(mWarningLines[4], mPaint);
        drawLineWarningRight(mWarningLines[4], mPaint);
//        drawLine(mWarningLines[2], mPaint);
//        drawLine(mWarningLines[3], mPaint);
//        mPaint.setColor(Color.GREEN);
//        drawLine(mWarningLines[4], mPaint);
//        drawLine(mWarningLines[5], mPaint);
//		drawLine(mWarningLines[6], mPaint);
//		drawLine(mWarningLines[7], mPaint);
    }

    private void setInvisible() {
        this.setVisibility(View.INVISIBLE);
    }

    private void drawStaticLine() {

        if (mStaticTrackPoints == null || mStaticWarningLines == null || mStaticCtrlPoints == null) {
            Log.e("AdayoCamera", TAG + " - drawStaticLine:static line is NULL");
            return;
        }

        int color[] = new int[]{Color.RED, Color.YELLOW, Color.YELLOW, Color.GREEN};
        int colorIdx = 0;
        float stopY = 0;
        float stopX = 0;
        float startY = 0;
        float startX = 0;
        mPaint.setStrokeWidth(6);

        for (int i = 0; i < mStaticTrackPoints.length; i++) {
            stopX = startX = mStaticTrackPoints[i][0].x;
            stopY = startY = mStaticTrackPoints[i][0].y;
            colorIdx = 0;
            for (int j = 1; j < mStaticTrackPoints[i].length; j++) {
                stopX = mStaticTrackPoints[i][j].x;
                stopY = mStaticTrackPoints[i][j].y;
                if (mStaticCtrlPoints[colorIdx].y > mStaticTrackPoints[i][j].y) {
                    colorIdx++;
                    colorIdx = colorIdx >= color.length ? color.length - 1 : colorIdx;
                }
                mPaint.setColor(color[colorIdx]);

                mCanvas.drawLine(startX, startY, stopX, stopY, mPaint);
                startX = stopX;
                startY = stopY;
            }
        }
        mPaint.setColor(Color.YELLOW);
        drawLine(mStaticWarningLines[0], mPaint);
        drawLine(mStaticWarningLines[1], mPaint);
        drawLine(mStaticWarningLines[2], mPaint);
//        drawLine(mStaticWarningLines[2], mPaint);
//        drawLine(mStaticWarningLines[3], mPaint);
//        mPaint.setColor(Color.YELLOW);
//        drawLine(mStaticWarningLines[4], mPaint);
//        drawLine(mStaticWarningLines[5], mPaint);
//        mPaint.setColor(Color.GREEN);
//        drawLine(mStaticWarningLines[6], mPaint);
//        drawLine(mStaticWarningLines[7], mPaint);
    }

    private void drawTyreLine() {
        LineViewUtils.drawTyreLine(mCanvas, Color.BLUE, mTyrePoints);
    }

    private void drawLineWarningRight(FloatPoint[] points, Paint paint) {
        touchStart(points[0].x, points[0].y);
        for (int i = 1; i < 20; i++) {
            Log.e("MMQQ", "drawLine: X = " + points[i].x + " Y = " + points[i].y);
            touchMove(points[i].x, points[i].y);
        }

//        touchMove(points[0].x - 80, points[0].y);
        touchUp(paint);
//        touchStart(flag == 0 ? points[points.length - 1].x:points[points.length - 1].x, flag == 0 ? points[points.length - 1].y : points[points.length - 1].y);
//        touchMove(flag == 0 ? points[points.length - 1].x + 80:points[points.length - 1].x + 80, flag == 0 ? points[points.length - 1].y  : points[points.length - 1].y);
//        touchUp(paint);

    }

    private void drawLineWarningLeft(FloatPoint[] points, Paint paint) {
        touchStart(points[points.length - 1].x, points[points.length - 1].y);
        for (int i = points.length - 1; i > points.length - 21; i--) {
            touchMove(points[i].x, points[i].y);
        }
        touchUp(paint);

    }

    private void drawLine(FloatPoint[] points, Paint paint) {
        touchStart(points[0].x, points[0].y);
        for (int i = 1; i < points.length; i++) {
            touchMove(points[i].x, points[i].y);
        }
        touchUp(paint);
    }

    private void drawLineLeft(FloatPoint[] points) {
        if (points == null) {
            Log.e("AdayoCamera", TAG + " - drawLine:points is NULL");
            return;
        }
        int color[] = new int[]{Color.RED,Color.RED,Color.RED, Color.YELLOW, Color.GREEN};
        float stopY;
        float stopX;
        float startY;
        float startX;
        mPaint.setStrokeWidth(6);
        stopX = startX = points[0].x;
        stopY = startY = points[0].y;
        for (int j = 1; j < points.length; j++) {
            stopX = points[j].x;
            stopY = points[j].y;

            if (points[j - 1].y > mWarningLines[2][mWarningLines[2].length - 1].y) {
                mPaint.setColor(color[0]);
            } else if (mWarningLines[3][mWarningLines[2].length - 1].y < points[j - 1].y && points[j - 1].y < mWarningLines[2][mWarningLines[2].length - 1].y) {
                mPaint.setColor(color[3]);
            } else {
                mPaint.setColor(color[4]);
            }


            mCanvas.drawLine(startX, startY, stopX, stopY, mPaint);
            startX = stopX;
            startY = stopY;


        }
    }

    private void drawLineRight(FloatPoint[] points) {
        if (points == null) {
            Log.e("AdayoCamera", TAG + " - drawLine:points is NULL");
            return;
        }
        int color[] = new int[]{Color.RED,Color.RED,Color.RED, Color.YELLOW, Color.GREEN};
        int colorIdx = 0;
        float stopY;
        float stopX;
        float startY;
        float startX;
        mPaint.setStrokeWidth(6);
        stopX = startX = points[0].x;
        stopY = startY = points[0].y;
        colorIdx = 0;
        for (int j = 1; j < points.length; j++) {
            stopX = points[j].x;
            stopY = points[j].y;
            if (points[j - 1].y > mWarningLines[2][2].y) {
                mPaint.setColor(color[0]);
            } else if (mWarningLines[3][2].y < points[j - 1].y && points[j - 1].y < mWarningLines[2][2].y) {
                mPaint.setColor(color[3]);
            } else {
                mPaint.setColor(color[4]);
            }

            mCanvas.drawLine(startX, startY, stopX, stopY, mPaint);
            startX = stopX;
            startY = stopY;
        }
    }


    private void updateLine(FloatPoint[][] trackPoints, FloatPoint[][] warningLines, FloatPoint[] ctrlPoints) {
        Log.e("AdayoCamera", TAG + " - updateLine()");
        try {
            mTrackPoints = trackPoints;
            mWarningLines = warningLines;
            mCtrlPoints = ctrlPoints;
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp(Paint paint) {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, paint);
        // kill this so we don't double draw
        mPath.reset();
    }

    private void clear() {
        mPath.reset();
        invalidate();

    }

    /**
     * 刷新动态辅助线画面
     *
     * @param angle
     */
    public int mSteerAngle = -999;

    public void updateLine(int angle, boolean isNeedUpdateLine) {
        if ((angle == mSteerAngle) && (!isNeedUpdateLine)) {
            return;
        }
        Log.d("AdayoCamera", TAG + " - updateLine:angle " + angle);
        mSteerAngle = angle;
        updateTrackLine(angle);
        updateWarningLine(angle);
        updateCtrlPoints(angle);
        updateTyrePoints(angle);
        invalidate();
    }

    @Override
    public void onSteeringWheelValueChange(int value, boolean isNeedUpdateLine) {
        //TODO test start  隐藏辅助线
        /*if(Math.abs(value)>= 0){
            return;
        }*/
        //TODO end
        //Trace.d(TAG, "onSteeringWheelValueChange = " + value);
        if (mITrackLinePoint != null) {
            int angle = mITrackLinePoint.getAngleRange(value);  //画线的时候，方向盘转角方向反了
            updateLine(angle, isNeedUpdateLine);
        } else {
            Log.e("AdayoCamera", TAG + " - onSteeringWheelValueChange: mITrackLinePoint is NULL");
        }
    }

    @Override
    public void onTrackLinePointChange(FloatPoint[][] trackPoints, FloatPoint[][] warningLinePoints, FloatPoint[] ctrlPoints) {
        mTrackPoints = trackPoints;
        mWarningLines = warningLinePoints;
        mCtrlPoints = ctrlPoints;
        invalidate();
    }


    @Override
    protected void onDetachedFromWindow() {
        Log.d("AdayoCamera", TAG + " - onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    /**
     * @return
     */
    public boolean isInitFinish() {
        return mIsInitFinish;
    }


}
