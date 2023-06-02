package com.adayo.app.camera.trackline.loadconfig.bean;

public class CalibrateTrackWarningLine {
    private int[] yPos;
    private int[] xPoss;
    private int angle;
    private int dense;

    public int[] getYPos() {
        return this.yPos;
    }

    public void setYPos(int[] yPos) {
        this.yPos = yPos;
    }

    public int[] getXPoss() {
        return this.xPoss;
    }

    public void setXPoss(int[] xPoss) {
        this.xPoss = xPoss;
    }

    public int getAngle() {
        return this.angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getDense() {
        return this.dense;
    }

    public void setDense(int dense) {
        this.dense = dense;
    }
}
