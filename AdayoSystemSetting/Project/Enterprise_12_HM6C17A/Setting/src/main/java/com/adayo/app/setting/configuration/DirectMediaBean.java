package com.adayo.app.setting.configuration;

public class DirectMediaBean {
    private int titlesId;
    private int infosId;
    private int value;

    public DirectMediaBean(int titlesId, int infosId, int value) {
        this.titlesId = titlesId;
        this.infosId = infosId;
        this.value = value;
    }

    public int getTitlesId() {
        return titlesId;
    }

    public int getInfosId() {
        return infosId;
    }

    public int getValue() {
        return value;
    }
}
