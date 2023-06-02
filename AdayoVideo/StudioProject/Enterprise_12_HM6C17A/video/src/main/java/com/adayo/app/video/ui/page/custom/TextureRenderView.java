package com.adayo.app.video.ui.page.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.lt.library.util.LogUtil;

@SuppressLint("ViewConstructor")
public class TextureRenderView extends TextureView implements TextureView.SurfaceTextureListener, IRenderView {
    private final MeasureHelper mMeasureHelper;
    private OnSurfaceChanged mOnSurfaceChanged;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;

    {
        mMeasureHelper = new MeasureHelper();
        setSurfaceTextureListener(this);
    }

    public TextureRenderView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] measuredSize = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measuredSize[0], measuredSize[1]);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtil.d("surfaceTexture: " + surfaceTexture + ", width: " + width + ", height: " + height);
        if (mSurfaceTexture != null) {
            setSurfaceTexture(mSurfaceTexture);
        } else {
            mSurfaceTexture = surfaceTexture;
            mSurface = new Surface(surfaceTexture);
            if (mOnSurfaceChanged != null) {
                mOnSurfaceChanged.onChanged(mSurface);
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtil.d("surfaceTexture: " + surfaceTexture + ", width: " + width + ", height: " + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        LogUtil.d("surfaceTexture: " + surfaceTexture);
        if (mOnSurfaceChanged != null) {
            mOnSurfaceChanged.onChanged(null);
        }
        release();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setVideoRatio(int videoRatio) {
        mMeasureHelper.setVideoRatio(videoRatio);
        requestLayout();
    }

    @Override
    public void release() {
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }

    @Override
    public void setOnSurfaceChanged(OnSurfaceChanged onSurfaceChanged) {
        mOnSurfaceChanged = onSurfaceChanged;
    }
}