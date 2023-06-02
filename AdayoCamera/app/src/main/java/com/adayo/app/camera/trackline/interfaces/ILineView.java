package com.adayo.app.camera.trackline.interfaces;

import com.adayo.crtrack.FloatPoint;

public interface ILineView {

    /**
     * 安状初始化参数
     *
     * @param carMode，iTrackLinePoint
     * @return
     * @throws
     */

    void setup(ITrackLinePoint iTrackLinePoint);

    /**
     * 更新轨迹线
     *
     * @param angle 角度
     * @return
     * @throws
     */
    void updateTrackLine(int angle);

    /**
     * 当屏幕坐标有变化时，调用更新静态轨迹线
     *
     * @param angle 角度
     * @return
     * @throws
     */
    void updateStaticList();

    /**
     * 更新方向盘转角
     *
     * @param value 角度
     * @return
     * @throws
     */
    void onSteeringWheelValueChange(int value, boolean onSteeringWheelValueChange);

    /**
     * 显示隐藏轨迹线
     *
     * @param Visibility View.Visibility Gone INVisibility
     * @return
     * @throws
     */
    void setVisibility(int Visibility);

    /**
     * 轨迹线更新
     *
     * @param trackPoints，warningLinePoints，ctrlPoints
     * @return
     * @throws
     */
    void onTrackLinePointChange(FloatPoint[][] trackPoints, FloatPoint[][] warningLinePoints, FloatPoint[] ctrlPoints);


}
