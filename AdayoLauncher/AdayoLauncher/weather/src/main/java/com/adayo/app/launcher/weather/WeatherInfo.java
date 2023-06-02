package com.adayo.app.launcher.weather;

public class WeatherInfo {

    private final int resId;
    private int weatherId;
    private String videoId;

    public WeatherInfo(int weatherId,String videoId,int resId) {

        this.weatherId = weatherId;
        this.videoId = videoId;
        this.resId = resId;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public String getVideoId() {
        return videoId;
    }

    public int getResId() {
        return resId;
    }
}
