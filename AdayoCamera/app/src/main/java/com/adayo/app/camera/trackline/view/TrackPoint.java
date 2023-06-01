package com.adayo.app.camera.trackline.view;

public class TrackPoint {
    public TrackPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float x, y;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(x);
        sb.append(",");
        sb.append(y);
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TrackPoint) {
            return x == ((TrackPoint) o).x && y == ((TrackPoint) o).y;
        }
        return false;
    }


}