package com.adayo.app.camera.signalview;

import android.animation.Animator;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;

import com.adayo.app.camera.constants.EventIds;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

public class AnimatorBackTrackSV extends BaseSignalView {
    private AnimationDrawable amin;
    public AnimatorBackTrackSV(ImageView animBack){
        amin = (AnimationDrawable) animBack.getBackground();
    }
    @Override
    protected void processEventBehavior(View view, SignalViewState state) {

    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (state.signalValue == 3 || state.signalValue == 5 ||state.signalValue == 6 || state.signalValue == 7 ) {
                amin.start();
        }else {
            amin.stop();
        }
    }
}
