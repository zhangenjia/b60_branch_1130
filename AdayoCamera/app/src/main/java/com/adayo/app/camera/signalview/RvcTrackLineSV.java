package com.adayo.app.camera.signalview;

import static com.adayo.app.camera.trackline.function.FileUtil.LoggerLine;
import static com.adayo.app.camera.trackline.function.FileUtil.saveData;

import android.util.Log;
import android.view.View;

import com.adayo.app.camera.trackline.function.TrackLinePointImpl;
import com.adayo.app.camera.trackline.loadconfig.LoadConfig;
import com.adayo.app.camera.trackline.loadconfig.bean.CalibrateTrack;
import com.adayo.app.camera.trackline.view.LineView;
import com.adayo.crtrack.FloatPoint;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/11/23 11:05
 */
public class RvcTrackLineSV extends BaseSignalView {
    private int mSteeringWheelValue = 0;

    private CalibrateTrack mCrTrack;

    private int maxSteeringWheelValue = 550;//实际方向盘转角最大值
    private int maxCanSteeringWheelValue = 7800;//can信号方向盘转角最大值

//    private static final String CAR_TRACK_CONFIG_PATH = "/system/etc/adayo/crtrack/CarTrackConfig.json";   ///etc/adayo/daemon/SDK_daemon.conf
//    private static final String CAR_TRACK_BEAN_CONFIG_PATH = "/system/etc/adayo/crtrack/CarTrackConfigDemo.json";

    public void setUp(String CAR_TRACK_BEAN_CONFIG_PATH) {
        Log.d("AdayoCamera", TAG + " - " + "setUp() called with: CAR_TRACK_BEAN_CONFIG_PATH = [" + CAR_TRACK_BEAN_CONFIG_PATH + "] ，this.maxCanSteeringWheelValue = " + this.maxCanSteeringWheelValue + " , this.maxSteeringWheelValue = " + this.maxSteeringWheelValue);
        View view = getView();
        if (view instanceof LineView) {
            mCrTrack = LoadConfig.getInstance().getCRTrack(CAR_TRACK_BEAN_CONFIG_PATH);

            if (mCrTrack == null) {
                Log.d("AdayoCamera", TAG + " - setUp: failed because mCrTrack is null , do nothing");
                return;
            }
            //初始化LineView
            TrackLinePointImpl trackLinePoint = TrackLinePointImpl.getInstance();
            trackLinePoint.setCrTrack(mCrTrack);

            ((LineView) view).setup(trackLinePoint);
            ((LineView) view).onSteeringWheelValueChange(mSteeringWheelValue, true);
//            for (int i=-35;i<36;i++) {
//                FloatPoint[][] mTrackPoints = TrackLinePointImpl.getInstance().getTrackPoints(i);
//                for (int j=0;j<mTrackPoints.length;j++) {
//                    LoggerLine("mTrackPoints" + j, i, mTrackPoints[j]);
//                    Log.d(TAG, "mTrackPoints: "+j +"--"+ mTrackPoints[j]);
//                }
//            }
//            for (int i=-35;i<36;i++) {
//                FloatPoint[][] mWarningLines = TrackLinePointImpl.getInstance().getWarningLines(i);
//                for (int j=0;j<mWarningLines.length;j++) {
//                    Log.d(TAG, "mWarningLines: "+j +"--"+ mWarningLines[j]);
//                    LoggerLine("mWarningLines"+j,i,mWarningLines[j]);
//                }
//            }
//            for (int i=-35;i<36;i++) {
//                FloatPoint[] mCtrlPoints = TrackLinePointImpl.getInstance().getCtrlPoints(i);
//                Log.d(TAG, "mCtrlPoints: "+ mCtrlPoints);
//                LoggerLine("mCtrlPoints",i,mCtrlPoints);
//            }
//            for (int i=-35;i<36;i++) {
//                FloatPoint[][] mTyrePoints = TrackLinePointImpl.getInstance().getTyrePoints(i);
//                for (int j=0;j<mTyrePoints.length;j++) {
//                    Log.d(TAG, "mTyrePoints: "+j +"--"+ mTyrePoints[j]);
//                    LoggerLine("mTyrePoints"+j,i,mTyrePoints[j]);
//                }
//            }
//            saveData();

        }
    }

    public void setUp(String CAR_TRACK_BEAN_CONFIG_PATH, int maxCanSteeringWheelValue, int maxSteeringWheelValue) {
        this.maxCanSteeringWheelValue = maxCanSteeringWheelValue;
        this.maxSteeringWheelValue = maxSteeringWheelValue;
        setUp(CAR_TRACK_BEAN_CONFIG_PATH);
    }


    @Override
    protected void processEventBehavior(View view, SignalViewState state) {

    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        int mValue = 0;
        //方向盘转角转换
        mValue = (int) ((maxCanSteeringWheelValue - state.signalValue) * 0.1); //小数舍掉
        Log.d("AdayoCamera", TAG + " - processSignalBehavior: mValue = " + mValue);

        //相同角度的数据 不再往下面传
        if (mSteeringWheelValue == mValue) {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior: failed because mSteeringWheelValue = mValue , do nothing");
            return;
        }
        //屏蔽无效值
        if (Math.abs(mValue) > maxSteeringWheelValue) {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior: failed because Math.abs(mValue) > maxSteeringWheelValue , mValue = " + mValue);
            return;
        }
        mSteeringWheelValue = mValue;
        if (view instanceof LineView) {
            LineView lineView = (LineView) view;
            lineView.onSteeringWheelValueChange(mSteeringWheelValue, true);
        }
    }
}
