package com.adayo.app.systemui.windows.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.adayo.app.systemui.bases.BaseSurfaceView;
import com.adayo.app.systemui.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.proxy.infrastructure.share.constants.ShareDataConstantsDef.TAG;

/**
 * @author XuYue
 * @description:
 * @date :2021/12/1 20:36
 */
public class FrameSurfaceView extends BaseSurfaceView {
    public static final int INVALID_BITMAP_INDEX = Integer.MAX_VALUE;
    private List<Integer> bitmaps = new ArrayList<>();
    private Bitmap frameBitmap;
    private int bitmapIndex = INVALID_BITMAP_INDEX;
    private Paint paint = new Paint();
    private BitmapFactory.Options options;
    private Rect srcRect;
    private Rect dstRect = new Rect();
    private int defaultWidth;
    private int defaultHeight;

    public FrameSurfaceView(Context context) {
        super(context);
    }

    public FrameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDuration(int duration) {
        setFrameDuration(duration);
    }

    public void setBitmaps(List<Integer> bitmaps) {
        if (bitmaps == null || bitmaps.size() == 0) {
            return;
        }
        this.bitmaps = bitmaps;
        getBitmapDimension(bitmaps.get(0));
    }

    private void getBitmapDimension(Integer integer) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), integer, options);
        defaultWidth = options.outWidth;
        defaultHeight = options.outHeight;
        srcRect = new Rect(0, 0, defaultWidth, defaultHeight);
    }

    @Override
    protected void init() {
        super.init();
        //定义解析Bitmap参数为可变类型，这样才能复用Bitmap
        options = new BitmapFactory.Options();
        options.inMutable = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        dstRect.set(0, 0, getWidth(), getHeight());
    }

    @Override
    protected int getDefaultWidth() {
        return defaultWidth;
    }

    @Override
    protected int getDefaultHeight() {
        return defaultHeight;
    }

    @Override
    protected void onFrameDrawFinish() {
        //每帧绘制完毕后不再回收
//        recycle();
    }

    public void recycle() {
        if (frameBitmap != null) {
            frameBitmap.recycle();
            frameBitmap = null;
        }
    }

    @Override
    protected void onFrameDraw(Canvas canvas) {
        clearCanvas(canvas);
        if (!isStart()) {
            return;
        }
        if (!isFinish()) {
            drawOneFrame(canvas);
        } else {
            onFrameAnimationEnd();
        }
    }

    private void drawOneFrame(Canvas canvas) {
        if(null == bitmaps || bitmaps.size() == 0){
            return;
        }
        frameBitmap =  BitmapFactory.decodeResource(getResources(), bitmaps.get(bitmapIndex), options);
//        frameBitmap = BitmapUtil.decodeOriginBitmap(getResources(), bitmaps.get(bitmapIndex), options);
        //复用上一帧Bitmap的内存
        options.inBitmap = frameBitmap;
        canvas.drawBitmap(frameBitmap, srcRect, dstRect, paint);
        bitmapIndex++;
        if(bitmapIndex >= bitmaps.size() && !isStop()){
            bitmapIndex = 0;
        }
    }

    private void onFrameAnimationEnd() {
        reset();
    }

    private void reset() {
        bitmapIndex = INVALID_BITMAP_INDEX;
    }

    private boolean isFinish() {
        return bitmapIndex > bitmaps.size();
    }
    private boolean isStop() {
        return bitmapIndex == INVALID_BITMAP_INDEX;
    }

    private boolean isStart() {
        return bitmapIndex != INVALID_BITMAP_INDEX;
    }

    public void start() {
        LogUtil.debugD(TAG, "START");
        bitmapIndex = 0;
    }
    public void stop() {
        bitmapIndex = INVALID_BITMAP_INDEX;
    }

    private void clearCanvas(Canvas canvas) {
        if(canvas != null) {
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
    }
}
