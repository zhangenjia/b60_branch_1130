package com.adayo.app.video.ui.page.custom;

import android.view.Surface;
import android.view.View;

public interface IRenderView {
    View getView();

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoRatio(int videoRatio);

    void release();

    void setOnSurfaceChanged(OnSurfaceChanged onSurfaceChanged);

    interface OnSurfaceChanged {
        void onChanged(Surface surface);
    }
}
