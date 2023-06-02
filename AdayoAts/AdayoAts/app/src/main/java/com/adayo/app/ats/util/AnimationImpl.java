package com.adayo.app.ats.util;


public class AnimationImpl implements AnimationListener {

    private static AnimationImpl mAnimationImpl;

    public static AnimationImpl getInstance() {
        if (null == mAnimationImpl) {
            synchronized (AnimationImpl.class) {
                if (null == mAnimationImpl) {
                    mAnimationImpl = new AnimationImpl();
                }
            }
        }
        return mAnimationImpl;
    }

    private AnimationImpl(){

    }

    @Override
    public void circularBackgroundAnim() {

    }

    @Override
    public void circularMenuAnim() {

    }

    @Override
    public void overallBackgroundAnim() {

    }

    @Override
    public void menuCenterContentAnim() {

    }

    @Override
    public void pointerAlphaAnim() {

    }

    @Override
    public void unableToContinueSwitching() {

    }


}
