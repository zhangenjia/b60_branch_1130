package com.adayo.app.radio.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * 用于做高斯模糊的图片缓存以提升性能，减少内存
 *
 * @author Yiwen.Huan
 * created at 2021/12/17 19:44
 */
public class BlurBitmapCache {

    public static BlurBitmapCache instance;

    private Bitmap blurBitmap;
    private float roundRadio = 0;


    private BlurBitmapCache() {
    }

    public static BlurBitmapCache getInstance() {
        if (null == instance) {
            synchronized (BlurBitmapCache.class) {
                if (null == instance) {
                    instance = new BlurBitmapCache();
                }
            }
        }
        return instance;
    }

    public float getRoundRadio() {
        return roundRadio;
    }

    public Bitmap getBlurBitmap() {
        return blurBitmap;
    }

    /**
     * 在Application调用，或在setContentView之前调用
     *
     * @param context       上下文
     * @param source        要被高斯模糊的原图
     * @param blurRadius    高斯模糊的半径
     * @param scale         缩放比例，为了加速高斯模糊，值越小越快，但会不清晰
     * @param roundRadio    圆角半径
     * @param shouldRecycle 是否要回收source，如果外部不再使用该Bitmap，则设置为true
     */
    public void setBitmap(Context context, Bitmap source, int blurRadius, float scale,float roundRadio, boolean shouldRecycle) {

//        if (null != blurBitmap) {
//            recycleBitmap();
//        }

        this.roundRadio = roundRadio;

        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        int width = Math.round(srcWidth * scale);
        int height = Math.round(srcHeight * scale);

        Bitmap bitmap = source.copy(source.getConfig(),true);

        Bitmap inputBmp = Bitmap.createScaledBitmap(bitmap, width, height, false);

        RenderScript renderScript = RenderScript.create(context);

        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);

        scriptIntrinsicBlur.setRadius(blurRadius);

        scriptIntrinsicBlur.forEach(output);

        output.copyTo(inputBmp);

        renderScript.destroy();

        if (shouldRecycle) {
            source.recycle();
        }

        if (scale == 1) {
            this.blurBitmap = inputBmp;
        } else {
            this.blurBitmap = Bitmap.createScaledBitmap(inputBmp, srcWidth, srcHeight, false);
            if(shouldRecycle) {
                inputBmp.recycle();
            }
        }
    }

    /**
     * 在不需要的时候释放缓存的高斯模糊图片
     */
    public void recycleBitmap() {
        if (null != blurBitmap) {
            blurBitmap.recycle();
            blurBitmap = null;
        }
    }


}
