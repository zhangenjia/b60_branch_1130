package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * @author Yiwen.Huan
 * created at 2021/9/27 16:25
 */
public class LineModeSignalView extends AvmBtnSignalView {
    private WeakReference<View> wr;

    public LineModeSignalView(View btnView) {
        wr = new WeakReference<>(btnView);
    }

    @Override
    protected void setSelected(View view, int selectedBehavior) {
        if (null == wr) {
            Log.d("AdayoCamera", TAG + " - setSelected: failed because wr is null , do nothing");
            return;
        }
        View btnView = wr.get();
        if (null == btnView) {
            Log.d("AdayoCamera", TAG + " - setSelected: failed because btnView is null , do nothing");
            return;
        }
        if (!btnView.isEnabled()) {
            view.setSelected(false);
            return;
        }
        super.setSelected(view, selectedBehavior);
    }
}
