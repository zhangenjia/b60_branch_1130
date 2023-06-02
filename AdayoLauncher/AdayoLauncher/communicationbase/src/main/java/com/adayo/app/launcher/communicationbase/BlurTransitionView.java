package com.adayo.app.launcher.communicationbase;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import static com.adayo.app.launcher.communicationbase.ConstantUtil.CURRENT_THEME;

import java.io.File;

/**
 * @author Yiwen.Huan
 * created at 2021/12/6 11:21
 */
public class BlurTransitionView extends View {
    ValueAnimator showAnimator, hideAnimator;
    private boolean isResumeDefault = false;
    private boolean isShow = false;
    private Bitmap bitmap;
    private int blurRadio;
    private float scale;
    private int mLastTheme = -1;

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
        setVisibility(View.GONE);
        initAnimator();
        initPaint();
        WrapperUtil.getInstance().registerBgChangeCallBack(new WrapperUtil.BgChangeCallBack() {
            @Override
            public void setWallPaper(Bitmap bitmap) {
                if (bitmap != null) {//todo 设置壁纸
                    //将图片显示到ImageView中
                    BlurTransitionView.this.bitmap = bitmap;
                    isResumeDefault = false;
                } else {
                    if (CURRENT_THEME == 1) {
                        BlurTransitionView.this.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_bg);
                    } else {
                        BlurTransitionView.this.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_bg_2);
                    }
                    isResumeDefault = true;
                }
            }

            @Override
            public void resumeDefault() {

                isResumeDefault = true;

            }

            @Override
            public void deleteWallPaper() {
//                bitmap = null;
            }

        });
        String fileName = SystemPropertiesUtil.getInstance().getStringMethod("persist.wallpaperType", "");
        Log.d(TAG, "wallpaperType = " + fileName);
        if (!"".equals(fileName)) {
            //判断sdcard是否存在
            Bitmap bm = BitmapFactory.decodeFile(SouceUtil.getLocalResourcePath(getContext()) + "/" + fileName);
            bitmap = bm;

        }
        Log.d(TAG, "init: ====================>");

    }

    private void initAnimator() {
        showAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        hideAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
    }

    private static final String TAG = "BlurTransitionView";

    private Bitmap blurBitmap;
    private Paint paint;
    int[] position;

    private PorterDuffXfermode mode;

    private void initPaint() {
        position = new int[2];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);

        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
//        paint.setColor(Color.RED);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "onScrollChanged() called with: l = [" + l + "], t = [" + t + "], oldl = [" + oldl + "], oldt = [" + oldt + "]");
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout() called with: changed = [" + changed + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "]");
    }


    /**
     * 设置图片
     *
     * @param
     * @param blurRadio 模糊半径，0~25
     * @param scale     缩放参数，0~1
     */
    public void setBitmap(int blurRadio, float scale) {
        this.blurRadio = blurRadio;
        this.scale = scale;
        Log.d(TAG, " setBitmap: " + bitmap + " CURRENT_THEME " + CURRENT_THEME + " isResumeDefault " + isResumeDefault);
        if (isResumeDefault) {
            if (CURRENT_THEME == 1) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_bg);
            } else {
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.app_bg_2);
            }
        }


        mLastTheme = CURRENT_THEME;
        recycleBitmap();

        if (bitmap==null){
            return;
        }
        blurBitmap = getBlurBitmap(bitmap, blurRadio, scale);
        invalidate();
    }

    public void show(long time) {
        if (mLastTheme != CURRENT_THEME) {
            setBitmap(5, 0.5f);
        }

        isShow = true;
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
        isShow = false;
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

        getScreenPosition();

        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);
        canvas.drawRoundRect(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom, 62, 62, paint);
        paint.setXfermode(mode);
        canvas.drawBitmap(blurBitmap, srcRect, dstRect, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    private void getScreenPosition() {
        getLocationOnScreen(position);
        srcRect.left = position[0];
        srcRect.top = position[1];
        srcRect.right = position[0] + getWidth();
        srcRect.bottom = position[1] + getHeight() - 20;

        dstRect.left = 0;
        dstRect.top = 0;
        dstRect.right = getWidth();
        dstRect.bottom = getHeight() - 20;
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycle();
    }

    private void recycleBitmap() {
        if (null != blurBitmap) {
            blurBitmap.recycle();
            blurBitmap = null;
        }
    }

    public void recycle() {
        recycleBitmap();
    }

    public boolean getIsShow() {
        return isShow;
    }
}
