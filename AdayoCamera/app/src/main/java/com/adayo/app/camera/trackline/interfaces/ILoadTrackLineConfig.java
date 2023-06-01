package com.adayo.app.camera.trackline.interfaces;


import com.adayo.app.camera.trackline.loadconfig.bean.CalibrateTrack;
import com.adayo.crtrack.IntergePoint;

public interface ILoadTrackLineConfig {


    /**
     * 获取轨迹线配置信息
     *
     * @param crtackBeanPath 文件存放的路径  如： "/system/etc/adayo/crtrack/CarTrackConfig.json"
     * @return CRTrack
     * @throws
     */
    CalibrateTrack getCRTrack(String crtackBeanPath);

    /**
     * 获取轨迹线配置信息
     *
     * @param path 文件存放的路径   如： "/system/etc/adayo/crtrack/CarTrackConfigDemo.json"
     * @return IntergePoint[]
     * @throws
     */
    IntergePoint[] getWorldPoints(String path);
}
