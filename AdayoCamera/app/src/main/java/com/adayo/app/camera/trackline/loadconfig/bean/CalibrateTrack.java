package com.adayo.app.camera.trackline.loadconfig.bean;

public class CalibrateTrack {
    private CalibrateTrackWarningLine warning_line;
    private CalibrateTrackLine track_line;
    private int[][] points;
    private int max_steering_wheel;

    public CalibrateTrackWarningLine getWarning_line() {
        return this.warning_line;
    }

    public void setWarning_line(CalibrateTrackWarningLine warning_line) {
        this.warning_line = warning_line;
    }

    public CalibrateTrackLine getTrack_line() {
        return this.track_line;
    }

    public void setTrack_line(CalibrateTrackLine track_line) {
        this.track_line = track_line;
    }

    public int[][] getPoints() {
        return this.points;
    }

    public void setPoints(int[][] points) {
        this.points = points;
    }

    public int getMaxSteeringWheel() {
        return this.max_steering_wheel;
    }

    public void setMaxSteeringWheel(int max_steering_wheel) {
        this.max_steering_wheel = max_steering_wheel;
    }
}
