package com.adayo.app.launcher.model.bean;

public class AllAppBean {
    private String mId;
    private Integer mResource;
    private String mName;
    private boolean isEnable;
    public String getmName() {
        return mName;
    }

    public AllAppBean(String id, Integer resource, String mName,boolean isEnable) {
        mId = id;
        mResource = resource;
        this.mName = mName;
        this.isEnable = isEnable;
    }

    public String getmId() {
        return mId;
    }

    public Integer getmResource() {
        return mResource;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
