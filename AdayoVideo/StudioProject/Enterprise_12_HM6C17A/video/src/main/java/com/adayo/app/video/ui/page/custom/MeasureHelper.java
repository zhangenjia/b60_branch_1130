package com.adayo.app.video.ui.page.custom;

import android.view.View;

public class MeasureHelper {
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoRatio;

    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    public void setVideoRatio(int videoRatio) {
        mVideoRatio = videoRatio;
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     */
    public int[] doMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mVideoHeight == 0 || mVideoWidth == 0) {
            return new int[]{width, height};
        }
        //如果设置了比例
        switch (mVideoRatio) {
            case RenderViewConst.VIDEO_RATIO_DEFAULT:
            default:
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                break;
            case RenderViewConst.VIDEO_RATIO_4_3:
                if (height > width / 4 * 3) {
                    height = width / 4 * 3;
                } else {
                    width = height / 3 * 4;
                }
                break;
            case RenderViewConst.VIDEO_RATIO_16_9:
                if (height > width / 16 * 9) {
                    height = width / 16 * 9;
                } else {
                    width = height / 9 * 16;
                }
                break;
            case RenderViewConst.VIDEO_RATIO_MATCH_PARENT:
                width = widthMeasureSpec;
                height = heightMeasureSpec;
                break;
        }
        return new int[]{width, height};
    }
}
