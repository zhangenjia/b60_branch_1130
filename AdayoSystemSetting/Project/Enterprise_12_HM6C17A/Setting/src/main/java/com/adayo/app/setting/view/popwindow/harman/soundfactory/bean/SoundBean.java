package com.adayo.app.setting.view.popwindow.harman.soundfactory.bean;

public class SoundBean {
    private double mSreen_X;       private double mSreen_Y;       private int    mSound_X;       private int    mSound_Y;       public SoundBean(double mSreen_X, double mSreen_Y, int mSound_X, int mSound_Y) {
        this.mSreen_X = mSreen_X;
        this.mSreen_Y = mSreen_Y;
        this.mSound_X = mSound_X;
        this.mSound_Y = mSound_Y;
    }

    public double getmSreen_X() {
        return mSreen_X;
    }

    public double getmSreen_Y() {
        return mSreen_Y;
    }

    public int getmSound_X() {
        return mSound_X;
    }

    public int getmSound_Y() {
        return mSound_Y;
    }
}
