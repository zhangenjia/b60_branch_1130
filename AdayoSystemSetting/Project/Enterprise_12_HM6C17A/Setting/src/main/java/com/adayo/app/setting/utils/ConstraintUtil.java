package com.adayo.app.setting.utils;


import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.transition.TransitionManager;



public class ConstraintUtil {
    private ConstraintLayout constraintLayout;

    private ConstraintBegin begin;

    private ConstraintSet applyConstraintSet = new ConstraintSet();

    private ConstraintSet resetConstraintSet = new ConstraintSet();

    public ConstraintUtil(ConstraintLayout constraintLayout) {
        this.constraintLayout = constraintLayout;

        resetConstraintSet.clone(constraintLayout);

    }



    public ConstraintBegin begin() {
        synchronized (ConstraintBegin.class) {
            if (begin == null) {
                begin = new ConstraintBegin();

            }

        }

        applyConstraintSet.clone(constraintLayout);

        return begin;

    }



    public ConstraintBegin beginWithAnim() {
        TransitionManager.beginDelayedTransition(constraintLayout);

        return begin();

    }



    public void reSet() {
        resetConstraintSet.applyTo(constraintLayout);

    }



    public void reSetWidthAnim() {
        TransitionManager.beginDelayedTransition(constraintLayout);

        resetConstraintSet.applyTo(constraintLayout);

    }

    public class ConstraintBegin {


        public ConstraintBegin clear(@IdRes int... viewIds) {
            for (int viewId : viewIds) {
                applyConstraintSet.clear(viewId);

            }

            return this;

        }



        public ConstraintBegin clear(int viewId, int anchor) {
            applyConstraintSet.clear(viewId, anchor);

            return this;

        }



        public ConstraintBegin setMargin(@IdRes int viewId, int left, int top, int right, int bottom) {
            setMarginLeft(viewId, left);

            setMarginTop(viewId, top);

            setMarginRight(viewId, right);

            setMarginBottom(viewId, bottom);

            return this;

        }



        public ConstraintBegin setMarginLeft(@IdRes int viewId, int left) {
            applyConstraintSet.setMargin(viewId, ConstraintSet.LEFT, left);

            return this;

        }



        public ConstraintBegin setMarginRight(@IdRes int viewId, int right) {
            applyConstraintSet.setMargin(viewId, ConstraintSet.RIGHT, right);

            return this;

        }



        public ConstraintBegin setMarginTop(@IdRes int viewId, int top) {
            applyConstraintSet.setMargin(viewId, ConstraintSet.TOP, top);

            return this;

        }



        public ConstraintBegin setMarginBottom(@IdRes int viewId, int bottom) {
            applyConstraintSet.setMargin(viewId, ConstraintSet.BOTTOM, bottom);

            return this;

        }



        public ConstraintBegin Left_toLeftOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.LEFT, endId, ConstraintSet.LEFT);

            return this;

        }



        public ConstraintBegin Left_toRightOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.LEFT, endId, ConstraintSet.RIGHT);

            return this;

        }



        public ConstraintBegin Top_toTopOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.TOP);

            return this;

        }



        public ConstraintBegin Top_toBottomOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.TOP, endId, ConstraintSet.BOTTOM);

            return this;

        }



        public ConstraintBegin Right_toLeftOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.RIGHT, endId, ConstraintSet.LEFT);

            return this;

        }



        public ConstraintBegin Right_toRightOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.RIGHT, endId, ConstraintSet.RIGHT);

            return this;

        }



        public ConstraintBegin Bottom_toBottomOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.BOTTOM, endId, ConstraintSet.BOTTOM);

            return this;

        }



        public ConstraintBegin Bottom_toTopOf(@IdRes int startId, @IdRes int endId) {
            applyConstraintSet.connect(startId, ConstraintSet.BOTTOM, endId, ConstraintSet.TOP);

            return this;

        }



        public ConstraintBegin setWidth(@IdRes int viewId, int width) {
            applyConstraintSet.constrainWidth(viewId, width);

            return this;

        }



        public ConstraintBegin setHeight(@IdRes int viewId, int height) {
            applyConstraintSet.constrainHeight(viewId, height);

            return this;

        }



        public void commit() {
            applyConstraintSet.applyTo(constraintLayout);

        }

    }

}




