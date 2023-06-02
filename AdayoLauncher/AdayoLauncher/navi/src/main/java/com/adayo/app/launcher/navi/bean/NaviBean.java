package com.adayo.app.launcher.navi.bean;

public class NaviBean {
    private int navArrow ;//转向箭头
    private String nextRoadName ;//下一路名
    private String distanceNextRoad ;//下一路口距离
    private String distanceNextRoadUnit;//下一路口距离单位
    private String eTA ;//剩余时间
    private String dTD ;//剩余距离
    private int navRouteState;//导航状态
    private String currentRoadName;//当前路名
    private String cityInfo;

    public int getNavArrow() {
        return navArrow;
    }

    public void setNavArrow(int navArrow) {
        this.navArrow = navArrow;
    }

    public String getNextRoadName() {
        return nextRoadName;
    }

    public void setNextRoadName(String nextRoadName) {
        this.nextRoadName = nextRoadName;
    }

    public String getDistanceNextRoad() {
        return distanceNextRoad;
    }

    public void setDistanceNextRoad(String distanceNextRoad) {
        this.distanceNextRoad = distanceNextRoad;
    }

    public String geteTA() {
        return eTA;
    }

    public void seteTA(String eTA) {
        this.eTA = eTA;
    }

    public String getdTD() {
        return dTD;
    }

    public void setdTD(String dTD) {
        this.dTD = dTD;
    }

    public int getNavRouteState() {
        return navRouteState;
    }

    public void setNavRouteState(int navRouteState) {
        this.navRouteState = navRouteState;
    }

    public String getCurrentRoadName() {
        return currentRoadName;
    }

    public void setCurrentRoadName(String currentRoadName) {
        this.currentRoadName = currentRoadName;
    }

    public String getDistanceNextRoadUnit() {
        return distanceNextRoadUnit;
    }

    public void setDistanceNextRoadUnit(String distanceNextRoadUnit) {
        this.distanceNextRoadUnit = distanceNextRoadUnit;
    }

    public String getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(String cityInfo) {
        this.cityInfo = cityInfo;
    }
}
