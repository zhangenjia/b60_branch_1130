package com.adayo.app.systemui.bases;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.adayo.app.systemui.utils.LogUtil;

import static com.adayo.proxy.infrastructure.share.constants.ShareDataConstantsDef.TAG;

/**
 * @author XuYue
 * @description:
 * @date :2021/12/1 20:34
 */
public abstract class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int DEFAULT_FRAME_DURATION_MILLISECOND = 50;
    //用于计算帧数据的线程
    private HandlerThread handlerThread;
    private Handler handler;
    //帧刷新频率
    private int frameDuration = DEFAULT_FRAME_DURATION_MILLISECOND;
    //用于绘制帧的画布
    private Canvas canvas;
    private boolean isAlive;

    public BaseSurfaceView(Context context) {
        super(context);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        getHolder().addCallback(this);
        //设置透明背景，否则SurfaceView背景是黑的
        setBackgroundTransparent();
    }

    protected void setFrameDuration(int duration) {
        frameDuration = duration;
    }

    private void setBackgroundTransparent() {
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.debugD(TAG, "START");
        startDrawThread();
        LogUtil.debugD(TAG, "END");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }

    //停止帧绘制线程
    protected void stopDrawThread() {
        LogUtil.debugD(TAG, "STOP");
        if (null != handlerThread) {
            handlerThread.quit();
        }
        handler = null;
        isAlive = false;
    }

    //启动帧绘制线程
    protected void startDrawThread() {
        LogUtil.debugD(TAG, "START");
        isAlive = true;
        handlerThread = new HandlerThread("SurfaceViewThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(new DrawRunnable());
        LogUtil.debugD(TAG, "END");
    }

    private class DrawRunnable implements Runnable {
        @Override
        public void run() {
            if (!isAlive) {
                return;
            }
            try {
                //1.获取画布
                canvas = getHolder().lockCanvas();
                //2.绘制一帧
                onFrameDraw(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    //3.将帧数据提交
                    getHolder().unlockCanvasAndPost(canvas);
                }
                //4.一帧绘制结束
                onFrameDrawFinish();
            }
            //不停的将自己推送到绘制线程的消息队列以实现帧刷新
            if(null != handler) {
                handler.postDelayed(this, frameDuration);
            }
        }
    }

    protected abstract int getDefaultWidth();

    protected abstract int getDefaultHeight();

    protected abstract void onFrameDrawFinish();

    protected abstract void onFrameDraw(Canvas canvas);
}
