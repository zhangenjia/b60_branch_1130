package com.adayo.app.camera.trackline.interfaces;

import com.adayo.crtrack.FloatPoint;

public interface ITrackLinePoint {

    /**
     * 获取对应角度的轮胎线
     *
     * @param angle
     * @return FloatPoint[][]
     * @throws
     */
    FloatPoint[][] getTyrePoints(int angle);

    /**
     * 获取对应角度的倒车轨迹线
     *
     * @param angle
     * @return FloatPoint[][]
     * @throws
     */
    FloatPoint[][] getTrackPoints(int angle);

    /**
     * 获取静态倒车轨迹线
     *
     * @param
     * @return FloatPoint[][]
     * @throws
     */
    FloatPoint[][] getStaticTrackPoints();

    /**
     * 获取对应角度的警告线
     *
     * @param angle
     * @return FloatPoint[][]
     * @throws
     */
    FloatPoint[][] getWarningLines(int angle);

    /**
     * 获取静态警告线
     *
     * @param
     * @return FloatPoint[][]
     * @throws
     */
    FloatPoint[][] getStaticWarningLines();

    /**
     * 获取对应角度CtrlPoints
     *
     * @param angle
     * @return FloatPoint[][]
     * @throws
     */
    FloatPoint[] getCtrlPoints(int angle);

    /**
     * 获取静态角度CtrlPoints
     *
     * @param
     * @return FloatPoint[][]
     * @throws
     */
    FloatPoint[] getStaticCtrlPoints();

    /**
     * 获取静态角度CtrlPoints
     *
     * @param value
     * @return int
     * @throws
     */
    int getAngleRange(int value);

    /**
     * 注册ILineView
     *
     * @param iLineView
     * @return
     * @throws
     */
    void setILineView(ILineView iLineView);
}
