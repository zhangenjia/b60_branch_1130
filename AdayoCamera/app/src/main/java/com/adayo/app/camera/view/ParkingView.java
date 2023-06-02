package com.adayo.app.camera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.adayo.app.camera.R;

/**
 * 停车位view
 * Created by huan on 2021/9/07.
 */

public class ParkingView extends View {

    private static final String TAG = "ParkingView";

    private Bitmap image, imageN, imageE;
    private int imageWidth;
    private int imageHeight;

    private Bitmap btRotate;
    private int rotateRadius, rotateWidth;

    private Matrix imageMatrix, rotateMatrix;
    private float[] imageSrcPs, imageDstPs, rotateSrcPs, rotateDstPs;
    private RectF imageSrcRect, imageDstRect, rotateSrcRect, rotateDstRect;
    private Paint mPaint;
    private Paint framePaint;
    private boolean isEnable = true;

    private ClickType clickType;
    private boolean isOut = false;
    private GestureDetector gestureDetector;
    private float lastDegree = 0;

    private final float originalImageX = 107 ;
    private final float originalImageY = 276;

    private int validDistanceMin = 24;
    private int validDistanceMax = 132;
    private int HighestMax = 276;
    private int carCenterX = 415;
    private int carCenterY = 442;
    private int carX = 341;
    private int carY = 276;
    private int carWidth = 148;
    private int carHeight = 332;
    private RectF invalidRectF;
    private RectF validRectF;
    private int  offestx = 50;//正前方区域偏移量
    private int carX2 = 490;//右点X轴坐标

    private float preDegree;


//    Paint validePaint;
//    Paint invalidePaint;
//    Paint carPointPaint;

    public ParkingView(Context context) {
        super(context);
        init(context);
    }

    public void setImageN(Bitmap imageN) {
        this.imageN = imageN;
        this.image = imageN;
    }

    private void init(Context context) {

        imageE = BitmapFactory.decodeResource(getResources(), R.mipmap.img_parking_space_error);

        this.imageWidth = imageE.getWidth();
        this.imageHeight = imageE.getHeight();

        initButtons();
        initPs();
        initRect();
        initPaints();
        initMatrix();
        initCanvasPosition();
        lastDegree = computeDegree(new Point((int) rotateDstPs[8], (int) rotateDstPs[9]), new Point((int) imageDstPs[8], (int) imageDstPs[9]));

        gestureDetector = new GestureDetector(context, new ParkingGestureListener());
    }


    public ParkingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ParkingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ParkingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void initCanvasPosition() {

        rotateMatrix.postTranslate(imageWidth / 2 - rotateRadius, -rotateRadius + 4);

        imageMatrix.postTranslate(originalImageX, originalImageY);
        rotateMatrix.postTranslate(originalImageX, originalImageY);

        imageMatrix.mapPoints(imageDstPs, imageSrcPs);
        imageMatrix.mapRect(imageDstRect, imageSrcRect);
        rotateMatrix.mapPoints(rotateDstPs, rotateSrcPs);
        rotateMatrix.mapRect(rotateDstRect, rotateSrcRect);

    }

    private void initMatrix() {
        imageMatrix = new Matrix();
        rotateMatrix = new Matrix();
    }

    private void initPaints() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        framePaint = new Paint();
        framePaint.setAntiAlias(true);
        framePaint.setStrokeWidth(1);
        framePaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));

//        validePaint = new Paint();
//        validePaint.setAntiAlias(true);
//        validePaint.setStrokeWidth(10);
//        validePaint.setStyle(Paint.Style.STROKE);
//        validePaint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_bright));
//
//        invalidePaint = new Paint();
//        invalidePaint.setAntiAlias(true);
//        invalidePaint.setStrokeWidth(10);
//        invalidePaint.setStyle(Paint.Style.STROKE);
//        invalidePaint.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
//
// carPointPaint = new Paint();
//        carPointPaint.setAntiAlias(true);
//        carPointPaint.setStrokeWidth(30);
//        carPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        carPointPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.black));


    }

    private void initRect() {
        imageSrcRect = new RectF(0, 0, imageWidth, imageHeight);
        imageDstRect = new RectF(0, 0, imageWidth, imageHeight);

        rotateSrcRect = new RectF(0, 0, rotateWidth, rotateWidth);
        rotateDstRect = new RectF(0, 0, rotateWidth, rotateWidth);

        invalidRectF = new RectF(carX - validDistanceMin, carY - HighestMax, carX + carWidth + validDistanceMin, carY + carHeight + HighestMax);
        validRectF = new RectF(carX  - validDistanceMax, carY - HighestMax, carX  + carWidth + validDistanceMax, carY + carHeight + HighestMax);
        Log.d("AdayoCamera", TAG + " - initRect: rotateDstRect = " + rotateDstRect.left);
    }

    private void initPs() {
        imageSrcPs = new float[]{0, 0,
                imageWidth, 0,
                imageWidth, imageHeight,
                0, imageHeight,
                imageWidth / 2, imageHeight / 2};
        imageDstPs = imageSrcPs.clone();

        rotateSrcPs = new float[]{0, 0,
                rotateWidth, 0,
                rotateWidth, rotateWidth,
                0, rotateWidth,
                rotateRadius, rotateRadius};
        rotateDstPs = rotateSrcPs.clone();
    }

    private void initButtons() {
        btRotate = BitmapFactory.decodeResource(getResources(), R.mipmap.button_rotate);
        rotateWidth = btRotate.getWidth();
        rotateRadius = rotateWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        processImage();
        if (null != image) {
            canvas.drawBitmap(image, imageMatrix, mPaint);
        }
        if (isEnable) {
            drawOthers(canvas);
        }
//        canvas.drawRect(validRectF, validePaint);
//        canvas.drawRect(invalidRectF, invalidePaint);
//        canvas.drawPoint(carX - offestx, carY, carPointPaint);
//        canvas.drawPoint(carX+carWidth + offestx, carY, carPointPaint);
        Log.d("AdayoCamera", TAG + " - onDraw: aPint = " + imageDstPs[0] + "---" + imageDstPs[1]);
    }


    private void drawOthers(Canvas canvas) {
        canvas.drawBitmap(btRotate, rotateMatrix, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetector.onTouchEvent(event);
        if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_POINTER_UP == event.getAction() || MotionEvent.ACTION_POINTER_1_UP == event.getAction() || MotionEvent.ACTION_POINTER_2_UP == event.getAction()) {
            lastDegree = computeDegree(new Point((int) rotateDstPs[8], (int) rotateDstPs[9]), new Point((int) imageDstPs[8], (int) imageDstPs[9]));

            processAngle();
            processFront();
            processLessL();
            processGreaterL();
            processLessL();
            processFront();
        }

        return !isOut;
    }

    //当前为无效角度，恢复到有效角度
    private void processAngle() {
        if ((lastDegree >= 6 && lastDegree <= 35) || lastDegree >= -35 && lastDegree <= -6 || lastDegree >=145  && lastDegree <= 174 || lastDegree >= -174 && lastDegree <= -145 ) {
            preDegree = 0;
            if (lastDegree > -90 && lastDegree <= 0 || lastDegree >= 0 && lastDegree < 90){
                if (lastDegree > 14) {
                    preDegree = 36;
                } else if (lastDegree >= 6) {
                    preDegree = 5;
                } else if (lastDegree < -14) {
                    preDegree = -36;
                } else  {
                    preDegree = -5;
                }
            }else {
                if (lastDegree > 163){
                    preDegree = 175;
                }else if(lastDegree >= 145 ){
                    preDegree = 144;
                }else if (lastDegree < -163 ){
                    preDegree = - 175;
                }else {
                    preDegree = -144;
                }
            }

            imageMatrix.postRotate(preDegree - lastDegree, imageDstPs[8], imageDstPs[9]);
            rotateMatrix.postRotate(preDegree - lastDegree, imageDstPs[8], imageDstPs[9]);
            matrixMap();
            lastDegree = preDegree;
        }
    }

    //正前方或正后方，恢复到有效方位
    private void processFront() {
        if (imageDstPs[8] + offestx > carX  && imageDstPs[8] - offestx < carX + carWidth  ) {
            if (imageDstPs[8] > carCenterX) {
                move(carX2 + 17 + 3 + offestx - imageDstPs[8], 0);
            } else {
                move(carX - 17 - 3  - offestx - imageDstPs[8], 0);
            }
        }
    }
    private double  getMinDistanceWithCarCenter(float x1,float x2,float y1,float y2){
        double sum =  Math.sqrt((x1-x2)*(x1 -x2)+(y1-y2)*(y1-y2));
        return sum;
    }
    private double  getMaxDistanceWithCarCenter(float x1,float x2,float y1,float y2){
        double sum =  Math.sqrt((x1-x2)*(x1 -x2)+(y1-y2)*(y1-y2));
        return sum;
    }

    private float getMinLenght(float a,float b){

        float lenght  = Math.abs(Math.abs(a)-b);
        Log.d(TAG, "getMinLenght: lenght === " +lenght);
        return lenght;
    }
    private int retunX = 0;
    private int initValueLeft = 0;
    private int initValueRight = 0;
    private int getXindex(){
        double  x = 0;

        int index = 0;
            for ( int i = 0;i<imageDstPs.length -2;i = i+2 ){
                if (i == 0){
                    x =  getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY);
                    index = i;
                }
                Log.d(TAG, "processGreaterLxxxxxx: i ===" + i);
                if (imageDstPs[8] < carCenterX){
                    if (imageDstPs[i] + offestx >= carX  && imageDstPs[i] < carCenterX){
                        retunX = 1;
                        if (initValueLeft == 0){
                            initValueLeft = initValueLeft+1;
                            x =  getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY);
                            index = i;
                        }
                        if (initValueLeft != 0){
                            if (getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY) < x){
                                x =  getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY);
                                index = i;
                            }
                            initValueLeft = 0;
                            return index ;
                        }
                    } else {
                        if (getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY) < x){
                            x =  getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY);
                            index = i;
                        }
                    }
                }else{
                    if ( imageDstPs[i] > carCenterX && imageDstPs[i] - offestx <= (carX + carWidth ) ){
                        retunX = 1;
                        Log.d(TAG, "processGreaterLxxxxxx: i ===" + i);
                        if (initValueRight == 0){
                            initValueRight = initValueRight+1;
                            x =  getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY);
                            index = i;
                        }
                        if (initValueRight != 0){
                            if (getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY) < x){
                                x =  getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY);
                                index = i;
                            }
                        }
                        initValueRight = 0;
                        return index;
                    } else {
                        if (getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY) < x){
                            x =  getMinDistanceWithCarCenter(imageDstPs[i],carCenterX,imageDstPs[i+1],carCenterY);
                            index = i;
                        }
                    }
                }
            }
        retunX = 0;
        return  index;
    }
    private int retunMaxX = 0;
    private int initValueLeftX= 0;
    private int initValueRightX = 0;
    private int getMaxXindex(){
        double  x = 0;
        int index = 0;
        for ( int i = 0;i<imageDstPs.length -2;i = i+2 ){
            if(invalidRectF.left > imageDstPs[i] ){
                continue;
            }
            if ( invalidRectF.right < imageDstPs[i]){
                continue;
            }
            Log.d(TAG, "getMaxXindex: i ===" + i);
            if (imageDstPs[8] < carCenterX){
                if (i == 0){
                    x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.left,imageDstPs[i+1],imageDstPs[i+1]);
                    index = i;
                }
                if (imageDstPs[i] >= invalidRectF.left ){
                    retunMaxX = 1;
                    if (initValueLeftX == 0){
                        initValueLeftX = initValueLeftX+1;
                        x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.left,imageDstPs[i+1],imageDstPs[i+1]);
                        index = i;
                    }
                    if (initValueLeftX != 0){
                        if (getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.left,imageDstPs[i+1],imageDstPs[i+1]) > x){
                            x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.left,imageDstPs[i+1],imageDstPs[i+1]);
                            index = i;
                        }
                        initValueLeftX = 0;
                        return index ;
                    }
                } else {
                    if (getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.left,imageDstPs[i+1],imageDstPs[i+1]) > x){
                        x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.left,imageDstPs[i+1],imageDstPs[i+1]);
                        index = i;
                    }
                }
            }else{
                if (i == 0){
                    x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.right,imageDstPs[i+1],imageDstPs[i+1]);
                    index = i;
                }
                if (  imageDstPs[i]  <= invalidRectF.right ){
                    retunMaxX = 1;
                    Log.d(TAG, "processGreaterLxxxxxx: i ===" + i);
                    if (initValueRightX == 0){
                        initValueRightX = initValueRightX+1;
                        x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.right,imageDstPs[i+1],imageDstPs[i+1]);
                        index = i;
                    }
                    if (initValueRightX != 0){
                        if (getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.right,imageDstPs[i+1],imageDstPs[i+1]) > x){
                            x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.right,imageDstPs[i+1],imageDstPs[i+1]);
                            index = i;
                        }
                    }
                    initValueRightX = 0;
                    return index;
                } else {
                    if (getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.right,imageDstPs[i+1],imageDstPs[i+1]) > x){
                        x =  getMinDistanceWithCarCenter(imageDstPs[i],invalidRectF.right,imageDstPs[i+1],imageDstPs[i+1]);
                        index = i;
                    }
                }
            }
        }
        retunMaxX = 0;
        return  index;
    }

    //0.5m<L<1.8m,此处计算大于1.8m情况
    private void processGreaterL() {
        Log.d(TAG, "processGreaterL: containsOnePoint(validRectF, imageDstPs)  == " + containsOnePoint(validRectF, imageDstPs));
        if (!containsOnePoint(validRectF, imageDstPs)) {
            int indexX = 0;
            indexX =  getXindex();
            Log.d(TAG, "processGreaterL111111: indexX = === " +indexX);
            if (retunX != 1){
                if (imageDstPs[indexX]  <= carCenterX){
                    if (imageDstPs[indexX] <= validRectF.left){
                        move( 1 + 75 + getMinLenght(imageDstPs[indexX],validRectF.left),0);
                    }
                }else {
                    if (imageDstPs[indexX] >= validRectF.right){
                        move( 1 - 75 - getMinLenght(imageDstPs[indexX],validRectF.right),0);
                    }
                }
            }
              if (retunX == 1){
                if (imageDstPs[8]  <= carCenterX){
                        move( (1 - getMinLenght(imageDstPs[indexX],validRectF.left))/2 ,0);
                }else {
                        move( (1 + getMinLenght(imageDstPs[indexX],validRectF.right))/2 ,0);
                }
              }
//            if (imageDstPs[indexX+1] <= carCenterY ){
//                if (imageDstPs[indexX+1] <= validRectF.top){
//                    move(0, 1 + 40  + getMinLenght(imageDstPs[indexX+1],validRectF.top));
//                }
//            }else{
//                if (imageDstPs[indexX+1] >= validRectF.bottom){
//                    move(0, 1 - 40   - getMinLenght(imageDstPs[indexX+1],validRectF.bottom));
//                }
//            }
    }
    }

    //0.5m<L<1.8m,此处计算小于0.5m情况
    private void processLessL() {
        //0.5m<L<1.8m,此处计算小于0.5m情况
        if (containsOnePoint(invalidRectF, imageDstPs)) {
            Log.d(TAG, "processLessL1222: start  imageDstPs[8] = "+imageDstPs[8]+" imageDstPs[9]  = " +imageDstPs[9]   );
//            float imageCenterX = imageDstPs[8];
//            float imageCenterY = imageDstPs[9];
            //图片中心点在 左侧
            int indexX = 0;
            indexX =  getMaxXindex();
            Log.d(TAG, "processGreaterL111111: indexX = === " +indexX);
            if (retunMaxX != 1){
                if (imageDstPs[indexX]  <= carCenterX){
                    if (imageDstPs[indexX] >= invalidRectF.left){
                        move( 1 - 20 - getMinLenght(imageDstPs[indexX],invalidRectF.left),0);
                    }
                }else {
                    if (imageDstPs[indexX] <= validRectF.right){
                        move( 1 + 20 + getMinLenght(imageDstPs[indexX],invalidRectF.right),0);
                    }
                }
            }
            if (retunMaxX == 1){
                if (imageDstPs[8]  <= carCenterX){
                    move( 1 - 20 - getMinLenght(imageDstPs[indexX],invalidRectF.left) ,0);
                }else {
                    move( 1 + 20 + getMinLenght(imageDstPs[indexX],invalidRectF.right) ,0);
                }
            }
//            if (imageDstPs[indexX+1] <= carCenterY ){
//                if (imageDstPs[indexX+1] >= invalidRectF.top){
//                    move(0, 1 - 20  - getMinLenght(imageDstPs[indexX+1],invalidRectF.top));
//                }
//            }else{
//                if (imageDstPs[indexX+1] <= invalidRectF.bottom){
//                    move(0, 1 + 20 + getMinLenght(imageDstPs[indexX+1],invalidRectF.bottom));
//                }
//            }
        }
    }
//


//            Log.d(TAG, "processLessL1222: start  invalidRectF.left = "+invalidRectF.left+" invalidRectF.right  = " +invalidRectF.right +" invalidRectF.top = " +invalidRectF.top +" invalidRectF.bottom= " +invalidRectF.bottom   );
//            float toX = invalidRectF.left - 1;
//            Log.d(TAG, "processLessL1222: toX =" +toX);
//            float toY = invalidRectF.top - 1;
//            float toXDistance = Math.abs(imageCenterX - invalidRectF.left);
//            Log.d(TAG, "processLessL1222: toXDistance  left =" +toXDistance);
//            Log.d(TAG, "processLessL1222: toXDistance right =" +Math.abs(imageCenterX - invalidRectF.right));
//            float toYDistance = Math.abs(imageCenterY - invalidRectF.top);
//            Log.d(TAG, "processLessL1222: toYDistance top =" +toYDistance);
//            Log.d(TAG, "processLessL1222: toYDistance bottom =" +Math.abs(imageCenterY - invalidRectF.bottom));
//            if (toXDistance > Math.abs(imageCenterX - invalidRectF.right)) {
//                toX = invalidRectF.right + 1;
//                toXDistance = Math.abs(imageCenterX - invalidRectF.right);
//            }
//            if (toYDistance > Math.abs(imageCenterY - invalidRectF.bottom)) {
//                toY = invalidRectF.bottom + 1;
//                toYDistance = Math.abs(imageCenterY - invalidRectF.bottom);
//            }

//            float maxDistance = 0;
//            float translateFrom = 0;
//            Log.d(TAG, "processLessL1222: invalidRectF.contains(imageDstRect.left, imageDstRect.top) =" +(invalidRectF.contains(imageDstRect.left, imageDstRect.top)));
//            Log.d(TAG, "processLessL1222: invalidRectF.contains(imageDstRect.left, imageDstRect.bottom) =" +(invalidRectF.contains(imageDstRect.left, imageDstRect.bottom)));
//            if (invalidRectF.contains(imageDstRect.left, imageDstRect.top) || invalidRectF.contains(imageDstRect.left, imageDstRect.bottom)) {
//                float distance = Math.abs(imageDstRect.left - toX);
//                Log.d("AdayoCamera", TAG + " - " + "processLessL() called distance = " + distance);
//                if (distance > maxDistance) {
//                    maxDistance = distance;
//                    translateFrom = imageDstRect.left;
//                    Log.d("AdayoCamera", TAG + " - " + "processLessL() called maxDistance = " + maxDistance + " | translateFrom = " + translateFrom);
//                }
//            }
//            Log.d(TAG, "processLessL1222: invalidRectF.contains(imageDstRect.right, imageDstRect.top) =" +(invalidRectF.contains(imageDstRect.right, imageDstRect.top)));
//            Log.d(TAG, "processLessL1222: invalidRectF.contains(imageDstRect.right, imageDstRect.bottom) =" +(invalidRectF.contains(imageDstRect.right, imageDstRect.bottom)));
//
//            if (invalidRectF.contains(imageDstRect.right, imageDstRect.top) || invalidRectF.contains(imageDstRect.right, imageDstRect.bottom)) {
//                float distance = Math.abs(imageDstRect.right - toX);
//                Log.d("AdayoCamera", TAG + " - " + "processLessL() called distance = " + distance);
//                if (distance > maxDistance) {
//                    maxDistance = distance;
//                    translateFrom = imageDstRect.right;
//                    Log.d("AdayoCamera", TAG + " - " + "processLessL() called maxDistance = " + maxDistance + " | translateFrom = " + translateFrom);
//                }
//            }
//
//
//            Log.d("AdayoCamera", TAG + " - " + "processLessL() called toX = " + toX + " | translateFrom = " + translateFrom);
//            Log.d(TAG, "processLessL: end  imageDstPs[0] = "+imageDstPs[0]+"imageDstPs[1]" +imageDstPs[1] +" imageDstPs[2] = " +imageDstPs[2] +" imageDstPs[3]= " +imageDstPs[3] + " imageDstPs[4] =" +imageDstPs[4] + " imageDstPs[5] = " +imageDstPs[5] +"  imageDstPs[6] = "+ imageDstPs[6]  +" imageDstPs[7] " + imageDstPs[7]  +" imageDstPs[8] " + imageDstPs[8]  +" imageDstPs[9] " + imageDstPs[9]  );
//            Log.d(TAG, "processLessL: end imageSrcPs[0] = "+imageSrcPs[0]+"imageSrcPs[1]" +imageSrcPs[1] +" imageSrcPs[2] = " +imageSrcPs[2] +" imageSrcPs[3]= " +imageSrcPs[3] + " imageSrcPs[4] =" +imageSrcPs[4] + " imageSrcPs[5] = " +imageSrcPs[5] +"  imageSrcPs[6] = "+ imageSrcPs[6]  +" imageSrcPs[7] " + imageSrcPs[7]  +" imageSrcPs[8] " + imageSrcPs[8]  +" imageSrcPs[9] " + imageSrcPs[9]  );
//            Log.d(TAG, "processLessL: end  imageDstRect.left = "+imageDstRect.left+" imageDstRect.right  = " +imageDstRect.right +" imageDstRect.top = " +rotateDstRect.top +" imageDstRect.bottom= " +imageDstRect.bottom   );
//            Log.d(TAG, "processLessL: end  imageSrcRect.left = "+imageSrcRect.left+" imageSrcRect.right  = " +imageSrcRect.right +" imageSrcRect.top = " +rotateDstRect.top +" imageSrcRect.bottom= " +imageSrcRect.bottom   );
//            Log.d(TAG, "processLessL: end  rotateDstPs[0] = "+rotateDstPs[0]+"rotateDstPs[1]" +rotateDstPs[1] +" rotateDstPs[2] = " +rotateDstPs[2] +" rotateDstPs[3]= " +rotateDstPs[3] + " rotateDstPs[4] =" +rotateDstPs[4] + " rotateDstPs[5] = " +rotateDstPs[5] +"  rotateDstPs[6] = "+ rotateDstPs[6]  +" rotateDstPs[7] " + imageDstPs[7]  +" rotateDstPs[8] " + rotateDstPs[8]  +" rotateDstPs[9] " + rotateDstPs[9]  );
//            Log.d(TAG, "processLessL: end  rotateSrcPs[0] = "+rotateSrcPs[0]+"rotateSrcPs[1]" +rotateSrcPs[1] +" rotateSrcPs[2] = " +rotateSrcPs[2] +" rotateSrcPs[3]= " +rotateSrcPs[3] + " rotateSrcPs[4] =" +rotateSrcPs[4] + " rotateSrcPs[5] = " +rotateSrcPs[5] +"  rotateSrcPs[6] = "+ rotateSrcPs[6]  +" rotateSrcPs[7] " + imageDstPs[7]  +" rotateSrcPs[8] " + rotateSrcPs[8]  +" rotateSrcPs[9] " + rotateSrcPs[9]  );
//            Log.d(TAG, "processLessL: end  rotateDstRect.left = "+rotateDstRect.left+" rotateDstRect.right  = " +rotateDstRect.right +" rotateDstRect.top = " +rotateDstRect.top +" rotateDstRect.bottom= " +rotateDstRect.bottom   );
//            Log.d(TAG, "processLessL: end  rotateSrcRect.left = "+rotateSrcRect.left+" rotateSrcRect.right  = " +rotateSrcRect.right +" rotateSrcRect.top = " +rotateSrcRect.top +" rotateSrcRect.bottom= " +rotateSrcRect.bottom   );
//
//            move(toX - translateFrom, 0);


    private void processImage() {
        if (preDegree >= 6 && preDegree <= 35 || preDegree >= -35 && preDegree <= -6 || imageDstPs[8] + offestx >= carX && imageDstPs[8] -  offestx<= (carX + carWidth ) || containsOnePoint(invalidRectF, imageDstPs) || !containsOnePoint(validRectF, imageDstPs)) {
            image = imageE;
        } else {
            image = imageN;
        }
    }

    private void calculateClickType(int x, int y) {
        Log.d("AdayoCamera", TAG + " - calculateClickType: rotateDstRect" + rotateDstRect.left);
        Log.d("AdayoCamera", TAG + " - calculateClickType() called with: x = [" + x + "], y = [" + y + "]");
        if (rotateDstRect.contains(x, y)) {
            if (isOut) {
                isOut = false;
            }
            clickType = ClickType.ROTATE;
        } else {
            if (imageDstRect.contains(x, y)) {
                if (isOut) {
                    isOut = false;
                }
                clickType = ClickType.IMAGE;
            } else {
                if (!isOut) {
                    isOut = true;
                }
                clickType = ClickType.OUT;
            }
        }
    }


    private void move(float distanceX, float distanceY) {
        int width = 20 ;
        int height = 160 ;
        int centerX = (int) imageDstPs[8];
        int centerY = (int) imageDstPs[9];
        if (distanceX < 0 && centerX <= width || distanceX > 0 && centerX >= getWidth() - width) {
            distanceX = 0;
        }
        if (distanceY < 0 && centerY <= height || distanceY > 0 && centerY >= getHeight() - height) {
            distanceY = 0;
        }
        Log.d(TAG, "move1111: distancex = " +distanceX + "  distanceY === " +distanceY);
        imageMatrix.postTranslate(distanceX, distanceY);
        rotateMatrix.postTranslate(distanceX, distanceY);
        matrixMap();
    }

//    private boolean containsOnePoint(RectF rect, RectF rectF) {
//        Log.d(TAG, "processImage: rectF rectF.top = " +rectF.top +"rectF.bottom = " +rectF.bottom +"rectF.left= " +rectF.left + " rectF.right =" +rectF.right );
//        return rect.contains(rectF.left, rectF.top) || rect.contains(rectF.right, rectF.top) || rect.contains(rectF.right, rectF.bottom) || rect.contains(rectF.left, rectF.bottom);
//    }

    private boolean containsOnePoint(RectF rect, float[] points) {
        Log.d(TAG, "processImage: rectF points[1] = " +points[1] +" points[2] = " +points[2] +" points[3]= " +points[3] + " points[4] =" +points[4] + " points[5] = " +points[5] +"  points[6] = "+ points[6]  +" points[7] " + points[7]  );
        return rect.contains(points[0], points[1]) || rect.contains(points[2], points[3]) || rect.contains(points[4], points[5]) || rect.contains(points[6], points[7]);
    }


    public int getLeftTopX() {
        int x = 1090 + (int) imageDstPs[8];//改成中心点了
        Log.d("AdayoCamera", TAG + " - getLeftTopX() returned: " + x);
        return x;
    }

    public int getLeftTopY() {
        int y = 104 + (int) imageDstPs[9];//改成中心点了
        Log.d("AdayoCamera", TAG + " - getLeftTopY() returned: " + y);
        return y;
    }

    public float getDegree() {
        return lastDegree;
    }


    private void rotate(MotionEvent event) {
        if (null == event) {
            preDegree = 0;
            if (lastDegree >= 0) {
                if (lastDegree < 45) {
                    preDegree = 45;
                } else if (lastDegree < 90) {
                    preDegree = 90;
                } else if (lastDegree < 135) {
                    preDegree = 135;
                } else {
                    preDegree = 180;
                }
            } else {
                if (lastDegree >= -45) {
                    preDegree = 0;
                } else if (lastDegree >= -90) {
                    preDegree = -45;
                } else if (lastDegree >= -135) {
                    preDegree = -90;
                } else {
                    preDegree = -135;
                }
            }
            imageMatrix.postRotate(preDegree - lastDegree, imageDstPs[8], imageDstPs[9]);
            rotateMatrix.postRotate(preDegree - lastDegree, imageDstPs[8], imageDstPs[9]);
            matrixMap();
            lastDegree = preDegree;
            return;
        }
        if (event.getPointerCount() == 1) {
            preDegree = computeDegree(new Point((int) event.getX(), (int) event.getY()), new Point((int) imageDstPs[8], (int) imageDstPs[9]));
            imageMatrix.postRotate(preDegree - lastDegree, imageDstPs[8], imageDstPs[9]);
            rotateMatrix.postRotate(preDegree - lastDegree, imageDstPs[8], imageDstPs[9]);

            matrixMap();
            lastDegree = preDegree;
        }
    }


    private void matrixMap() {
        imageMatrix.mapPoints(imageDstPs, imageSrcPs);
        imageMatrix.mapRect(imageDstRect, imageSrcRect);
        rotateMatrix.mapPoints(rotateDstPs, rotateSrcPs);
        rotateMatrix.mapRect(rotateDstRect, rotateSrcRect);
        Log.d("AdayoCamera", TAG + " - matrixMap: rotateDstRect = " + rotateDstRect.left);
        postInvalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnable = enabled;
        postInvalidate();
    }

    @Override
    public void setVisibility(int visibility) {
        Log.d("AdayoCamera", TAG + " - setVisibility() called with: visibility = [" + visibility + "]");
        if (visibility == View.VISIBLE && getVisibility() != visibility) {
            Log.d("AdayoCamera", TAG + " - setVisibility() called with: getVisibility() = [" + getVisibility() + "]");
            if (lastDegree != 0 || imageDstPs[0] != originalImageX || originalImageY != imageDstPs[1]) {
                Log.d("AdayoCamera", TAG + " - setVisibility() called with: lastDegree =" + lastDegree + " |  originalImageX = " + originalImageX + " | imageDstPs[0]  = " + imageDstPs[0] + " | originalImageY = " + originalImageY + " | imageDstPs[1] = " + imageDstPs[1]);
                imageMatrix.postRotate(-lastDegree, imageDstPs[8], imageDstPs[9]);
                rotateMatrix.postRotate(-lastDegree, imageDstPs[8], imageDstPs[9]);
                matrixMap();
                lastDegree = 0;
                move(originalImageX - imageDstPs[0], originalImageY - imageDstPs[1]);
            }
        }
        super.setVisibility(visibility);
    }

    public float computeDegree(Point p1, Point p2) {
        float tran_x = p1.x - p2.x;
        float tran_y = p1.y - p2.y;
        Log.d(TAG, "computeDegree: tran_x" +tran_x );
        Log.d(TAG, "computeDegree: tran_y" + tran_y);
        float degree = 0.0f;
        float angle = (float) (Math.asin(tran_x / Math.sqrt(tran_x * tran_x + tran_y * tran_y)) * 180 / Math.PI);
        if (!Float.isNaN(angle)) {
            if (tran_x >= 0 && tran_y <= 0) {//第一象限
                degree = angle;
            } else if (tran_x <= 0 && tran_y <= 0) {//第二象限
                degree = angle;
            } else if (tran_x <= 0 && tran_y >= 0) {//第三象限
                degree = -180 - angle;
            } else if (tran_x >= 0 && tran_y >= 0) {//第四象限
                degree = 180 - angle;
            }
        }
        Log.d("AdayoCamera", TAG + " - computeDegree: degree = " + degree + "   angle = " + angle);
        return degree;
    }

    private enum ClickType {
        ROTATE, IMAGE, OUT
    }


    private class ParkingGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isEnable) {
                return true;
            }
            switch (clickType) {
                case ROTATE:
                    rotate(e2);
                    break;
                case IMAGE:
                    if (e2.getPointerCount() == 1) {
                        move(-distanceX, -distanceY);
                    }
                    break;
                case OUT:
                    break;
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (!isEnable) {
                return true;
            }
            calculateClickType((int) e.getX(), (int) e.getY());
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isEnable) {
                return true;
            }
            if (clickType == ClickType.IMAGE) {
                rotate(null);
            }
            return super.onDoubleTap(e);
        }
    }

}
