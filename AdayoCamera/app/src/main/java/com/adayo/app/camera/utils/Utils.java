package com.adayo.app.camera.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.adayo.crtrack.FloatPoint;
import com.adayo.crtrack.IntergePoint;
import com.adayo.proxy.aaop_camera.signalview.AnimatorSignalView;
import com.adayo.proxy.aaop_camera.signalview.AnimatorVisibilitySignalView;
import com.adayo.proxy.aaop_camera.signalview.base.ISignalView;

import java.lang.ref.WeakReference;

/**
 * @author Yiwen.Huan
 * created at 2021/9/2 17:11
 */
public class Utils {
    private static final String TAG = "Utils";

    public static Animator getAnimatorDialogShow(View view) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleX", 0.7f, 1f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleY", 0.7f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator1, animator2, animator3);
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(333);
        return set;
    }

    public static Animator getAnimatorDialogDismiss(View view) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.7f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.7f);
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new DecelerateInterpolator());
        set.playTogether(animator1, animator2, animator3);
        set.setDuration(333);
        return set;
    }

    public static Animator getAnimatorShadowShow(View view) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1f);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.setDuration(333);
        return animator1;
    }

    public static Animator getAnimatorUpgradeShow(View view) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "alpha", 1f, 1f);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.setDuration(333);
        return animator1;
    }

    public static Animator getAnimatorShadowDismiss(View view) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.0f);
        animator1.setInterpolator(new DecelerateInterpolator());
        animator1.setDuration(333);
        animator1.addListener(new ShadowDismissAnimatorListener(view));
        return animator1;
    }

    public static void bindAnimatorSignalView(ISignalView signalView, Animator animator) {
        if (signalView instanceof AnimatorSignalView) {
            ((AnimatorSignalView) signalView).setAnimator(animator);
        }
    }

    public static void bindAnimatorVisibleSignalView(ISignalView signalView, Animator... animators) {
        if (signalView instanceof AnimatorVisibilitySignalView) {
            for (Animator animator : animators) {
                ((AnimatorVisibilitySignalView) signalView).addVisibleAnimator(animator);
            }
        }
    }

    public static void bindAnimatorGoneSignalView(ISignalView signalView, Animator... animators) {
        if (signalView instanceof AnimatorVisibilitySignalView) {
            for (Animator animator : animators) {
                ((AnimatorVisibilitySignalView) signalView).addGoneAnimator(animator);
            }
        }
    }


    public static void bindDialogAnimatorSignalView(ISignalView signalView, View container, View view) {
        bindAnimatorVisibleSignalView(signalView, Utils.getAnimatorShadowShow(container), Utils.getAnimatorDialogShow(view));
        bindAnimatorGoneSignalView(signalView, Utils.getAnimatorShadowDismiss(container), Utils.getAnimatorDialogDismiss(view));
    }

    public static void bindUpgradeDialogAnimatorSignalView(ISignalView signalView, View container, View view) {
        bindAnimatorVisibleSignalView(signalView, Utils.getAnimatorUpgradeShow(container), Utils.getAnimatorDialogShow(view));
        bindAnimatorGoneSignalView(signalView, Utils.getAnimatorShadowDismiss(container), Utils.getAnimatorDialogDismiss(view));
    }

    private static class ShadowDismissAnimatorListener implements Animator.AnimatorListener {
        private WeakReference<View> wr;

        public ShadowDismissAnimatorListener(View view) {
            wr = new WeakReference<>(view);
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (null != wr) {
                View view = wr.get();
                if (null != view) {
                    view.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }


    public static String getString(FloatPoint[] line) {
        if (line.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < line.length; i++) {
            stringBuilder.append(" [ ")
                    .append(i)
                    .append(" ]( ")
                    .append(line[i].x)
                    .append(" , ")
                    .append(line[i].y)
                    .append(" ) ");
        }
        return stringBuilder.toString();
    }

    public static String getString(IntergePoint[] line) {
        if (line.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < line.length; i++) {
            stringBuilder.append(" [ ")
                    .append(i)
                    .append(" ]( ")
                    .append(line[i].x)
                    .append(" , ")
                    .append(line[i].y)
                    .append(" ) ");
        }
        return stringBuilder.toString();
    }

}
