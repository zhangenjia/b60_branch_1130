package com.adayo.app.camera.signalview;

import android.util.Log;
import android.view.View;

import com.adayo.app.camera.trackline.interfaces.ILineView;
import com.adayo.app.camera.trackline.view.CalibrationView;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;

/**
 * @author Yiwen.Huan
 * created at 2021/11/24 11:32
 */
public class CalibrationTrackLineSignalView extends BaseSignalView {
    //    private static final String CAR_TRACK_CONFIG_PATH = "/system/etc/adayo/crtrack/CarTrackConfig.json";   ///etc/adayo/daemon/SDK_daemon.conf
    //    private static final String CAR_TRACK_BEAN_CONFIG_PATH = "/system/etc/adayo/crtrack/CarTrackConfigDemo.json";

    private int maxSteeringWheelValue = 477;//实际方向盘转角最大值
    private int maxCanSteeringWheelValue = 7800;//can信号方向盘转角最大值
    private int mSteeringWheelValue = 0;

    public void setup(String CAR_TRACK_CONFIG_PATH, ILineView mLineView) {

        Log.d("AdayoCamera", TAG + " - " + "setup() called with: CAR_TRACK_CONFIG_PATH = [" + CAR_TRACK_CONFIG_PATH + "], mLineView = [" + mLineView + "]");
        View view = getView();
        if (view instanceof CalibrationView) {
            ((CalibrationView) view).setup(CAR_TRACK_CONFIG_PATH, mLineView);
        } else {
            Log.d("AdayoCamera", TAG + " - setup: failed because view is not Calibration , view = " + view);
        }
        Log.d("AdayoCamera", TAG + " - setup: this.maxCanSteeringWheelValue = " + this.maxCanSteeringWheelValue + " , this.maxSteeringWheelValue = " + this.maxSteeringWheelValue);
    }

    public void setup(String CAR_TRACK_CONFIG_PATH, ILineView mLineView, int maxCanSteeringWheelValue, int maxSteeringWheelValue) {

        this.maxCanSteeringWheelValue = maxCanSteeringWheelValue;
        this.maxSteeringWheelValue = maxSteeringWheelValue;
        setup(CAR_TRACK_CONFIG_PATH, mLineView);
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

        // 相同角度的数据 不再往下面传
        if (mSteeringWheelValue == mValue) {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior: failed because mSteeringWheelValue = mValue");
            return;
        }
        // 屏蔽无效值
        if (Math.abs(mValue) > maxSteeringWheelValue) {
            Log.d("AdayoCamera", TAG + " - processSignalBehavior: failed because Math.abs(mValue) > maxSteeringWheelValue , mValue = " + mValue);
            return;
        }
        mSteeringWheelValue = mValue;
        if (view instanceof CalibrationView) {
            ((CalibrationView) view).setSteeringWheelValue(mSteeringWheelValue);
        }
    }
}
