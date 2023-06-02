package com.adayo.app.camera.trackline.interfaces;


import com.adayo.app.camera.trackline.loadconfig.LoadConfig;
import com.adayo.crtrack.IntergePoint;


public interface ICalibrationPoint {

    /**
     * 获取标定状态
     *
     * @param
     * @return
     * @throws
     */
    boolean getCalibrationStatus();

    /**
     * 保存标定点在屏幕的坐标
     *
     * @param
     * @return
     * @throws
     */
    void saveCalibrationPoint();

    /**
     * 获取标定点在屏幕的坐标
     *
     * @param
     * @return
     * @throws
     */
    IntergePoint[] getScreenPoints();

    /**
     * 左移1个像素
     *
     * @param
     * @return
     * @throws
     */
    void moveLeft();

    /**
     * 右移1个像素
     *
     * @param
     * @return
     * @throws
     */
    void moveRight();

    /**
     * 上移1个像素
     *
     * @param
     * @return
     * @throws
     */
    void moveUp();

    /**
     * 下移1个像素
     *
     * @param
     * @return
     * @throws
     */
    void moveDown();

    /**
     * 进入标定模式
     *
     * @param
     * @return Point  当前正处于活动状态的点
     * @throws
     */
    LoadConfig.Point calibration();

    /**
     * 切换活动状态点  A - B - C -D
     *
     * @param
     * @return Point  当前活动的点
     * @throws
     */

    LoadConfig.Point switchPoint();

    /**
     * 完成标定
     *
     * @param
     * @return
     * @throws
     */

    void finishCalibration();

    /**
     * 取消标定
     *
     * @param
     * @return
     * @throws
     */
    void cancelCalibration();
}
