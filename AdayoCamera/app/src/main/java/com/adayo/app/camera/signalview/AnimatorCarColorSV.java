package com.adayo.app.camera.signalview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

import java.lang.ref.WeakReference;

/**
 * @author Yiwen.Huan
 * created at 2021/10/15 13:35
 */
public class AnimatorCarColorSV extends BaseSignalView {


    private AnimatorSet animatorPaleGreen;
    private AnimatorSet animatorMistyBlue;
    private AnimatorSet animatorSnowWhite;
    private AnimatorSet animatorExtremeBlack;
    private AnimatorSet animatorLappBlue;
    private AnimatorSet animatorJungleGreen;
    private WeakReference<TextView> wrPaleGreen;
    private WeakReference<TextView> wrMistyBlue;
    private WeakReference<TextView> wrSnowWhite;
    private WeakReference<TextView> wrExtremeBlack;
    private WeakReference<TextView> wrLappBlue;
    private WeakReference<TextView> wrJungleGreen;

    private int[] selectState;

    public AnimatorCarColorSV(TextView paleGreen, TextView mistyBlue, TextView snowWhite, TextView extremeBlack, TextView lappBlue, TextView jungleGreen) {
        wrPaleGreen = new WeakReference<>(paleGreen);
        paleGreen.setSelected(true);
        wrMistyBlue = new WeakReference<>(mistyBlue);
        wrSnowWhite = new WeakReference<>(snowWhite);
        wrExtremeBlack = new WeakReference<>(extremeBlack);
        wrLappBlue = new WeakReference<>(lappBlue);
        wrJungleGreen = new WeakReference<>(jungleGreen);
        selectState = new int[]{android.R.attr.state_enabled,android.R.attr.state_selected};
    }


    private int getSelectColor() {
        View view = getView();
        if (view instanceof TextView) {
            ColorStateList list = ((TextView) view).getTextColors();
            int selectStateValue = list.getColorForState(selectState, Color.BLACK);
            return selectStateValue;
        } else {
            return Color.BLACK;
        }
    }

    private int getNormalColor() {
        View view = getView();
        if (view instanceof TextView) {
            ColorStateList list = ((TextView) view).getTextColors();
            return list.getDefaultColor();
        } else {
            return Color.WHITE;
        }
    }

    public AnimatorSet getAnimatorPaleGreen() {
        if (null != animatorMistyBlue) {
            animatorMistyBlue.cancel();
            animatorMistyBlue = null;
        }
        if (null != animatorSnowWhite) {
            animatorSnowWhite.cancel();
            animatorSnowWhite = null;
        }
        if (null != animatorExtremeBlack) {
            animatorExtremeBlack.cancel();
            animatorExtremeBlack = null;
        }
        if (null != animatorLappBlue) {
            animatorLappBlue.cancel();
            animatorLappBlue = null;
        }
        if (null != animatorJungleGreen) {
            animatorJungleGreen.cancel();
            animatorJungleGreen = null;
        }
        View animatorView = getView();
        if (null == animatorView) {
            Log.e("AdayoCamera", TAG + " - getAnimatorPaleGreen: null == animatorView");
            return null;
        }
        TextView paleGreen = getPaleGreen();
        if (null == paleGreen) {
            Log.e("AdayoCamera", TAG + " - getAnimatorPaleGreen: null == paleGreen");
            return null;
        }

        float x = animatorView.getX();
        Log.d("AdayoCamera", TAG + " - getAnimatorPaleGreen: animatorView.getX() = " + x);
        paleGreen.setSelected(true);
        if (0 == x || 134 == x || 268 == x || 402 == x || 536 == x || 670 == x) {
//            143 == x || 286 == x
            paleGreen.setTextColor(getSelectColor());
            animatorView.setTranslationX(0);
            return null;
        }

        Animator translatePaleGreen = ObjectAnimator.ofFloat(animatorView, "X", animatorView.getX(), paleGreen.getX());
        translatePaleGreen.setInterpolator(new DecelerateInterpolator());
        translatePaleGreen.setDuration(250);

        Animator colorPaleGreen = ObjectAnimator.ofObject(paleGreen, "textColor", new ArgbEvaluator(),
                getNormalColor(), getSelectColor());
        colorPaleGreen.setInterpolator(new DecelerateInterpolator());
        colorPaleGreen.setDuration(350);

        animatorPaleGreen = new AnimatorSet();
        animatorPaleGreen.playTogether(colorPaleGreen, translatePaleGreen);
        return animatorPaleGreen;
    }

    public AnimatorSet getAnimatorMistyBlue() {
        if (null != animatorPaleGreen) {
            animatorPaleGreen.cancel();
            animatorPaleGreen = null;
        }
        if (null != animatorSnowWhite) {
            animatorSnowWhite.cancel();
            animatorSnowWhite = null;
        }
        if (null != animatorExtremeBlack) {
            animatorExtremeBlack.cancel();
            animatorExtremeBlack = null;
        }
        if (null != animatorLappBlue) {
            animatorLappBlue.cancel();
            animatorLappBlue = null;
        }
        if (null != animatorJungleGreen) {
            animatorJungleGreen.cancel();
            animatorJungleGreen = null;
        }
        View animatorView = getView();
        if (null == animatorView) {
            Log.e("AdayoCamera", TAG + " - getAnimatorMistyBlue: null == animatorView");
            return null;
        }
        TextView mistyBlue = getMistyBlue();
        if (null == mistyBlue) {
            Log.e("AdayoCamera", TAG + " - getAnimatorMistyBlue: null == mistyBlue");
            return null;
        }

        float x = animatorView.getX();
        Log.d("AdayoCamera", TAG + " - getAnimatorMistyBlue: animatorView.getX() = " + x);
        mistyBlue.setSelected(true);
        if (0 == x || 134 == x || 268 == x || 402 == x || 536 == x || 670 == x) {
            mistyBlue.setTextColor(getSelectColor());
            animatorView.setTranslationX(134);
            return null;
        }

        Animator translateMistyBlue = ObjectAnimator.ofFloat(animatorView, "X", animatorView.getX(), mistyBlue.getX());
        translateMistyBlue.setInterpolator(new DecelerateInterpolator());
        translateMistyBlue.setDuration(250);

        Animator colorMistyBlue = ObjectAnimator.ofObject(mistyBlue, "textColor", new ArgbEvaluator(),
                getNormalColor(), getSelectColor());
        colorMistyBlue.setInterpolator(new DecelerateInterpolator());
        colorMistyBlue.setDuration(350);

        animatorMistyBlue = new AnimatorSet();
        animatorMistyBlue.playTogether(colorMistyBlue, translateMistyBlue);
        return animatorMistyBlue;
    }

    public AnimatorSet getAnimatorSnowWhite() {
        if (null != animatorPaleGreen) {
            animatorPaleGreen.cancel();
            animatorPaleGreen = null;
        }
        if (null != animatorMistyBlue) {
            animatorMistyBlue.cancel();
            animatorMistyBlue = null;
        }
        if (null != animatorExtremeBlack) {
            animatorExtremeBlack.cancel();
            animatorExtremeBlack = null;
        }
        if (null != animatorLappBlue) {
            animatorLappBlue.cancel();
            animatorLappBlue = null;
        }
        if (null != animatorJungleGreen) {
            animatorJungleGreen.cancel();
            animatorJungleGreen = null;
        }
        View animatorView = getView();
        if (null == animatorView) {
            Log.e("AdayoCamera", TAG + " - getAnimatorSnowWhite: null == animatorView");
            return null;
        }
        TextView snowWhite = getSnowWhite();
        if (null == snowWhite) {
            Log.e("AdayoCamera", TAG + " - getAnimatorSnowWhite: null == snowWhite");
            return null;
        }

        float x = animatorView.getX();
        Log.d("AdayoCamera", TAG + " - getAnimatorSnowWhite: animatorView.getX() = " + x);
        snowWhite.setSelected(true);
        if (0 == x || 134 == x || 268 == x || 402 == x || 536 == x || 670 == x) {
            snowWhite.setTextColor(getSelectColor());
            animatorView.setTranslationX(268);
            return null;
        }

        Animator translateSnowWhite = ObjectAnimator.ofFloat(animatorView, "X", animatorView.getX(), snowWhite.getX());
        translateSnowWhite.setInterpolator(new DecelerateInterpolator());
        translateSnowWhite.setDuration(250);

        Animator colorSnowWhite = ObjectAnimator.ofObject(snowWhite, "textColor", new ArgbEvaluator(),
                getNormalColor(), getSelectColor());
        colorSnowWhite.setInterpolator(new DecelerateInterpolator());
        colorSnowWhite.setDuration(350);

        animatorSnowWhite = new AnimatorSet();
        animatorSnowWhite.playTogether(colorSnowWhite, translateSnowWhite);
        return animatorSnowWhite;
    }

    public AnimatorSet getAnimatorExtremeBlack() {
        if (null != animatorPaleGreen) {
            animatorPaleGreen.cancel();
            animatorPaleGreen = null;
        }
        if (null != animatorMistyBlue) {
            animatorMistyBlue.cancel();
            animatorMistyBlue = null;
        }
        if (null != animatorSnowWhite) {
            animatorSnowWhite.cancel();
            animatorSnowWhite = null;
        }
        if (null != animatorLappBlue) {
            animatorLappBlue.cancel();
            animatorLappBlue = null;
        }
        if (null != animatorJungleGreen) {
            animatorJungleGreen.cancel();
            animatorJungleGreen = null;
        }
        View animatorView = getView();
        if (null == animatorView) {
            Log.e("AdayoCamera", TAG + " - getAnimatorSnowWhite: null == animatorView");
            return null;
        }
        TextView extremeBlack = getExtremeBlack();
        if (null == extremeBlack) {
            Log.e("AdayoCamera", TAG + " - getAnimatorSnowWhite: null == extremeBlack");
            return null;
        }

        float x = animatorView.getX();
        Log.d("AdayoCamera", TAG + " - getAnimatorSnowWhite: animatorView.getX() = " + x);
        extremeBlack.setSelected(true);
        if (0 == x || 134 == x || 268 == x || 402 == x || 536 == x || 670 == x) {
            extremeBlack.setTextColor(getSelectColor());
            animatorView.setTranslationX(402);
            return null;
        }

        Animator translateExtremeBlack = ObjectAnimator.ofFloat(animatorView, "X", animatorView.getX(), extremeBlack.getX());
        translateExtremeBlack.setInterpolator(new DecelerateInterpolator());
        translateExtremeBlack.setDuration(250);

        Animator colorExtremeBlack = ObjectAnimator.ofObject(extremeBlack, "textColor", new ArgbEvaluator(),
                getNormalColor(), getSelectColor());
        colorExtremeBlack.setInterpolator(new DecelerateInterpolator());
        colorExtremeBlack.setDuration(350);

        animatorExtremeBlack = new AnimatorSet();
        animatorExtremeBlack.playTogether(colorExtremeBlack, translateExtremeBlack);
        return animatorExtremeBlack;
    }

    public AnimatorSet getAnimatorLappBlue() {
        if (null != animatorPaleGreen) {
            animatorPaleGreen.cancel();
            animatorPaleGreen = null;
        }
        if (null != animatorMistyBlue) {
            animatorMistyBlue.cancel();
            animatorMistyBlue = null;
        }
        if (null != animatorSnowWhite) {
            animatorSnowWhite.cancel();
            animatorSnowWhite = null;
        }
        if (null != animatorExtremeBlack) {
            animatorExtremeBlack.cancel();
            animatorExtremeBlack = null;
        }
        if (null != animatorJungleGreen) {
            animatorJungleGreen.cancel();
            animatorJungleGreen = null;
        }
        View animatorView = getView();
        if (null == animatorView) {
            Log.e("AdayoCamera", TAG + " - getAnimatorLappBlue: null == animatorView");
            return null;
        }
        TextView lappBlue = getLappBlue();
        if (null == lappBlue) {
            Log.e("AdayoCamera", TAG + " - getAnimatorLappBlue: null == lappBlue");
            return null;
        }

        float x = animatorView.getX();
        Log.d("AdayoCamera", TAG + " - getAnimatorLappBlue: animatorView.getX() = " + x);
        lappBlue.setSelected(true);
        if (0 == x || 134 == x || 268 == x || 402 == x || 536 == x || 670 == x) {
            lappBlue.setTextColor(getSelectColor());
            animatorView.setTranslationX(536);
            return null;
        }

        Animator translateLappBlue = ObjectAnimator.ofFloat(animatorView, "X", animatorView.getX(), lappBlue.getX());
        translateLappBlue.setInterpolator(new DecelerateInterpolator());
        translateLappBlue.setDuration(250);

        Animator colorLappBlue = ObjectAnimator.ofObject(lappBlue, "textColor", new ArgbEvaluator(),
                getNormalColor(), getSelectColor());
        colorLappBlue.setInterpolator(new DecelerateInterpolator());
        colorLappBlue.setDuration(350);

        animatorLappBlue = new AnimatorSet();
        animatorLappBlue.playTogether(colorLappBlue, translateLappBlue);
        return animatorLappBlue;
    }

    public AnimatorSet getAnimatorJungleGreen() {
        if (null != animatorPaleGreen) {
            animatorPaleGreen.cancel();
            animatorPaleGreen = null;
        }
        if (null != animatorMistyBlue) {
            animatorMistyBlue.cancel();
            animatorMistyBlue = null;
        }
        if (null != animatorSnowWhite) {
            animatorSnowWhite.cancel();
            animatorSnowWhite = null;
        }
        if (null != animatorExtremeBlack) {
            animatorExtremeBlack.cancel();
            animatorExtremeBlack = null;
        }
        if (null != animatorLappBlue) {
            animatorLappBlue.cancel();
            animatorLappBlue = null;
        }
        View animatorView = getView();
        if (null == animatorView) {
            Log.e("AdayoCamera", TAG + " - getAnimatorJungleGreen: null == animatorView");
            return null;
        }
        TextView jungleGreen = getJungleGreen();
        if (null == jungleGreen) {
            Log.e("AdayoCamera", TAG + " - getAnimatorJungleGreen: null == jungleGreen");
            return null;
        }

        float x = animatorView.getX();
        Log.d("AdayoCamera", TAG + " - getAnimatorJungleGreen: animatorView.getX() = " + x);
        jungleGreen.setSelected(true);
        if (0 == x || 134 == x || 268 == x || 402 == x || 536 == x || 670 == x) {
            jungleGreen.setTextColor(getSelectColor());
            animatorView.setTranslationX(670);
            return null;
        }
        Log.d("AdayoCamera", TAG + " - getAnimatorJungleGreen: jungleGreen.getX() = " + x);
        Animator translateJungleGreen = ObjectAnimator.ofFloat(animatorView, "X", animatorView.getX(), jungleGreen.getX());
        translateJungleGreen.setInterpolator(new DecelerateInterpolator());
        translateJungleGreen.setDuration(250);

        Animator colorJungleGreen = ObjectAnimator.ofObject(jungleGreen, "textColor", new ArgbEvaluator(),
                getNormalColor(), getSelectColor());
        colorJungleGreen.setInterpolator(new DecelerateInterpolator());
        colorJungleGreen.setDuration(350);

        animatorJungleGreen = new AnimatorSet();
        animatorJungleGreen.playTogether(colorJungleGreen, translateJungleGreen);
        return animatorJungleGreen;
    }

    public TextView getPaleGreen() {
        if (null == wrPaleGreen) {
            return null;
        }
        return wrPaleGreen.get();
    }

    public TextView getMistyBlue() {
        if (null == wrMistyBlue) {
            return null;
        }
        return wrMistyBlue.get();
    }

    public TextView getSnowWhite() {
        if (null == wrSnowWhite) {
            return null;
        }
        return wrSnowWhite.get();
    }

    public TextView getExtremeBlack() {
        if (null == wrExtremeBlack) {
            return null;
        }
        return wrExtremeBlack.get();
    }

    public TextView getLappBlue() {
        if (null == wrLappBlue) {
            return null;
        }
        return wrLappBlue.get();
    }

    public TextView getJungleGreen() {
        if (null == wrJungleGreen) {
            return null;
        }
        return wrJungleGreen.get();
    }

    @Override
    public void setView(View view, boolean clickable) {
        super.setView(view, clickable);
    }


    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
    }

    private void startUnselectAnimator(TextView view) {

        Animator animator = ObjectAnimator.ofObject(view, "textColor", new ArgbEvaluator(), view.getCurrentTextColor(), getNormalColor());
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(100);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setSelected(false);
            }
        });
        animator.start();
    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        Log.d(TAG, "5processSignalBehavior: SignalViewState == " + state.signalValue);
        if (state.signalValue == 1) {
            TextView mistyBlue = getMistyBlue();
            if (null != mistyBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: mistyBlue " + mistyBlue.getX());
                startUnselectAnimator(mistyBlue);
            }
            TextView snowWhite = getSnowWhite();
            if (null != snowWhite) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: snowWhite " + snowWhite.getX());
                startUnselectAnimator(snowWhite);
            }
            TextView extremeBlack = getExtremeBlack();
            if (null != extremeBlack) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + extremeBlack.getX());
                startUnselectAnimator(extremeBlack);
            }
            TextView lappBlue = getLappBlue();
            if (null != lappBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: lappBlue " + lappBlue.getX());
                startUnselectAnimator(lappBlue);
            }
            TextView jungleGreen = getJungleGreen();
            if (null != jungleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: jungleGreen " + jungleGreen.getX());
                startUnselectAnimator(jungleGreen);
            }

            Animator animatorSetPaleGreen = getAnimatorPaleGreen();
            if (null != animatorSetPaleGreen) {
                animatorSetPaleGreen.start();
            }
            return;
        }
        if (state.signalValue == 2) {
            TextView paleGreen = getPaleGreen();
            if (null != paleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + paleGreen.getX());
                startUnselectAnimator(paleGreen);
            }
            TextView snowWhite = getSnowWhite();
            if (null != snowWhite) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: snowWhite " + snowWhite.getX());
                startUnselectAnimator(snowWhite);
            }
            TextView extremeBlack = getExtremeBlack();
            if (null != extremeBlack) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + extremeBlack.getX());
                startUnselectAnimator(extremeBlack);
            }
            TextView lappBlue = getLappBlue();
            if (null != lappBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: lappBlue " + lappBlue.getX());
                startUnselectAnimator(lappBlue);
            }
            TextView jungleGreen = getJungleGreen();
            if (null != jungleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: jungleGreen " + jungleGreen.getX());
                startUnselectAnimator(jungleGreen);
            }

            Animator animatorSetMistyBlue = getAnimatorMistyBlue();
            if (null != animatorSetMistyBlue) {
                animatorSetMistyBlue.start();
            }
            return;
        }
        if (state.signalValue == 0) {
            TextView paleGreen = getPaleGreen();
            if (null != paleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + paleGreen.getX());
                startUnselectAnimator(paleGreen);
            }
            TextView mistyBlue = getMistyBlue();
            if (null != mistyBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: mistyBlue " + mistyBlue.getX());
                startUnselectAnimator(mistyBlue);
            }
            TextView extremeBlack = getExtremeBlack();
            if (null != extremeBlack) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + extremeBlack.getX());
                startUnselectAnimator(extremeBlack);
            }
            TextView lappBlue = getLappBlue();
            if (null != lappBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: lappBlue " + lappBlue.getX());
                startUnselectAnimator(lappBlue);
            }
            TextView jungleGreen = getJungleGreen();
            if (null != jungleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: jungleGreen " + jungleGreen.getX());
                startUnselectAnimator(jungleGreen);
            }

            Animator animatorSetSnowWhite = getAnimatorSnowWhite();
            if (null != animatorSetSnowWhite) {
                animatorSetSnowWhite.start();
            }
            return;
        }
        if (state.signalValue == 3) {
            TextView paleGreen = getPaleGreen();
            if (null != paleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + paleGreen.getX());
                startUnselectAnimator(paleGreen);
            }
            TextView mistyBlue = getMistyBlue();
            if (null != mistyBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: mistyBlue " + mistyBlue.getX());
                startUnselectAnimator(mistyBlue);
            }
            TextView snowWhite = getSnowWhite();
            if (null != snowWhite) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: snowWhite " + snowWhite.getX());
                startUnselectAnimator(snowWhite);
            }
            TextView lappBlue = getLappBlue();
            if (null != lappBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: lappBlue " + lappBlue.getX());
                startUnselectAnimator(lappBlue);
            }
            TextView jungleGreen = getJungleGreen();
            if (null != jungleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: jungleGreen " + jungleGreen.getX());
                startUnselectAnimator(jungleGreen);
            }

            Animator animatorSetExtremeBlack = getAnimatorExtremeBlack();
            if (null != animatorSetExtremeBlack) {
                animatorSetExtremeBlack.start();
            }
            return;
        }
        if (state.signalValue == 4) {
            TextView paleGreen = getPaleGreen();
            if (null != paleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + paleGreen.getX());
                startUnselectAnimator(paleGreen);
            }
            TextView mistyBlue = getMistyBlue();
            if (null != mistyBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: mistyBlue " + mistyBlue.getX());
                startUnselectAnimator(mistyBlue);
            }
            TextView snowWhite = getSnowWhite();
            if (null != snowWhite) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: snowWhite " + snowWhite.getX());
                startUnselectAnimator(snowWhite);
            }
            TextView extremeBlack = getExtremeBlack();
            if (null != extremeBlack) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: extremeBlack " + extremeBlack.getX());
                startUnselectAnimator(extremeBlack);
            }
            TextView jungleGreen = getJungleGreen();
            if (null != jungleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: jungleGreen " + jungleGreen.getX());
                startUnselectAnimator(jungleGreen);
            }

            Animator animatorSetLappBlue = getAnimatorLappBlue();
            if (null != animatorSetLappBlue) {
                animatorSetLappBlue.start();
            }
            return;
        }
        if (state.signalValue == 5) {
            TextView paleGreen = getPaleGreen();
            if (null != paleGreen) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: paleGreen " + paleGreen.getX());
                startUnselectAnimator(paleGreen);
            }
            TextView mistyBlue = getMistyBlue();
            if (null != mistyBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: mistyBlue " + mistyBlue.getX());
                startUnselectAnimator(mistyBlue);
            }
            TextView snowWhite = getSnowWhite();
            if (null != snowWhite) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: snowWhite " + snowWhite.getX());
                startUnselectAnimator(snowWhite);
            }
            TextView extremeBlack = getExtremeBlack();
            if (null != extremeBlack) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: extremeBlack " + extremeBlack.getX());
                startUnselectAnimator(extremeBlack);
            }
            TextView lappBlue = getLappBlue();
            if (null != lappBlue) {
                Log.d("AdayoCamera", TAG + " - processSignalBehavior: lappBlue " + lappBlue.getX());
                startUnselectAnimator(lappBlue);
            }

            Animator animatorSetjungleGreen = getAnimatorJungleGreen();
            if (null != animatorSetjungleGreen) {
                animatorSetjungleGreen.start();
            }
            return;
        }
    }
}
