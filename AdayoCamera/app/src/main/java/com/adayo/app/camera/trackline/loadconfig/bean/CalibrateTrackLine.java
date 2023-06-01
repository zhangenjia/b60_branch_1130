package com.adayo.app.camera.trackline.loadconfig.bean;

public class CalibrateTrackLine {
    private int[] yPoss;
    private int angle;
    private int[] xPos;
    private int dense;

    public int[] getYPoss() {
        return this.yPoss;
    }

    public void setYPoss(int[] yPoss) {
        this.yPoss = yPoss;
    }

    public int getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int[] getXPos() {
        return this.xPos;
    }

    public void setXPos(int[] xPos) {
        this.xPos = xPos;
    }

    public int getDense() {
        return this.dense;
    }

    public void setDense(int dense) {
        this.dense = dense;
    }
}
