package com.adayo.app.camera.trackline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.adayo.app.camera.R;
import com.adayo.app.camera.trackline.function.CalibrationPointImpl;
import com.adayo.app.camera.trackline.interfaces.ILineView;
import com.adayo.app.camera.trackline.loadconfig.LoadConfig;
import com.adayo.app.camera.utils.Utils;
import com.adayo.crtrack.IntergePoint;
import com.adayo.crtrack.ProxyCover;

/**
 * @author Yiwen.Huan
 * created at 2021/11/23 15:46
 */
public class CalibrationView extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "CalibrationView";
//    private static final String CAR_TRACK_CONFIG_PATH = "/system/etc/adayo/crtrack/CarTrackConfig.json";   ///etc/adayo/daemon/SDK_daemon.conf
//    private static final String CAR_TRACK_BEAN_CONFIG_PATH = "/system/etc/adayo/crtrack/CarTrackConfigDemo.json";

    private static final String CURRENT_POINT = "当前点";
    private static final String SWITCH_POINT = "切换";

    private LinearLayout containerCalibrationStep1;
    private LinearLayout containerCalibrationStep2;
    private Button mBtnSwitchPoint;
    private Button mBtnTurnLeft;
    private Button mBtnTurnRight;
    private Button mBtnPreView;
    private Button mBtnCalibrate;
    private Button mBtnCanCel;
    private Button mBtnLeft;
    private Button mBtnRight;
    private Button mBtnDown;
    private Button mBtnUp;
    private Button mBtnFinish;
    private Button mBtnClearSP;

    private int mSteeringWheelValue = 0;

    private ILineView mLineView;

    private IntergePoint[] mWorldPoints;
    private ProxyCover calibrationTrackProxy;
    private CalibrationPointImpl mCalibrationPointImpl;

    public CalibrationView(Context context) {
        super(context);
        init();
    }

    public CalibrationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalibrationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CalibrationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.aaop_camera_view_calibration, this, true);
    }

    public void setup(String CAR_TRACK_CONFIG_PATH, ILineView mLineView) {
        Log.d("AdayoCamera", TAG + " - " + "setup() called with: CAR_TRACK_CONFIG_PATH = [" + CAR_TRACK_CONFIG_PATH + "], mLineView = [" + mLineView + "]");
        this.mLineView = mLineView;
        calibrationTrackProxy = ProxyCover.getInstance();
        calibrationTrackProxy.init(CAR_TRACK_CONFIG_PATH);
        mCalibrationPointImpl = CalibrationPointImpl.getInstance(getContext(), findViewById(R.id.container_calibration_point)); //标定区域
        mWorldPoints = LoadConfig.getInstance().getWorldPoints(CAR_TRACK_CONFIG_PATH);
        IntergePoint[] screenPoints = LoadConfig.getInstance().getScreenPoints(CAR_TRACK_CONFIG_PATH);//确定这4个点
//        更新轨迹线坐标 (只有标定的时候，才调用该接口，标定后不需要调用，直接从配置文件读取坐标)
        calibrationTrackProxy.updateCalibrate(mWorldPoints, screenPoints);

        initCalibrationView();
        initCalibrationListener();
    }

    public void setSteeringWheelValue(int value) {
        this.mSteeringWheelValue = value;
    }

    private void initCalibrationView() {
        containerCalibrationStep1 = findViewById(R.id.container_calibration_step1);
        containerCalibrationStep2 = findViewById(R.id.container_calibration_step2);

        mBtnTurnLeft = findViewById(R.id.btn_turn_left);
        mBtnTurnRight = findViewById(R.id.btn_turn_right);
        mBtnSwitchPoint = findViewById(R.id.btn_switch_point);
        mBtnCalibrate = findViewById(R.id.btn_calibration);
        mBtnCanCel = findViewById(R.id.btn_cancel);
        mBtnLeft = findViewById(R.id.btn_left);
        mBtnRight = findViewById(R.id.btn_right);
        mBtnDown = findViewById(R.id.btn_down);
        mBtnUp = findViewById(R.id.btn_up);
        mBtnFinish = findViewById(R.id.btn_finish);
        mBtnPreView = findViewById(R.id.btn_preview);
        mBtnClearSP = findViewById(R.id.btn_clear_sp);
    }

    private void initCalibrationListener() {
        mBtnSwitchPoint.setOnClickListener(this);
        mBtnCalibrate.setOnClickListener(this);
        mBtnCanCel.setOnClickListener(this);
        mBtnLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
        mBtnDown.setOnClickListener(this);
        mBtnUp.setOnClickListener(this);
        mBtnFinish.setOnClickListener(this);
        mBtnPreView.setOnClickListener(this);
        mBtnTurnLeft.setOnClickListener(this);
        mBtnTurnRight.setOnClickListener(this);
        mBtnClearSP.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (null == mLineView) {
            Log.d("AdayoCamera", TAG + " - onClick: failed because mLineView is null , do nothing");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_clear_sp://清除SP
                mCalibrationPointImpl.clearSP();
                break;
            case R.id.btn_switch_point:  //采点方法: 点击屏幕出现+ ，切换B点，点击屏幕，又会出现新的+，依次切换C D，最后完成。
                //切换标定点
                LoadConfig.Point point = mCalibrationPointImpl.switchPoint();
                mBtnSwitchPoint.setText(CURRENT_POINT + point.name() + SWITCH_POINT);
                break;

            case R.id.btn_cancel:
                containerCalibrationStep1.setVisibility(View.VISIBLE);
                containerCalibrationStep2.setVisibility(View.INVISIBLE);
                mCalibrationPointImpl.cancelCalibration();
                setVisibility(GONE);
                break;

            case R.id.btn_left:
                mCalibrationPointImpl.moveLeft();
                break;

            case R.id.btn_right:
                mCalibrationPointImpl.moveRight();
                break;

            case R.id.btn_down:
                mCalibrationPointImpl.moveDown();
                break;

            case R.id.btn_up:
                mCalibrationPointImpl.moveUp();
                break;

            case R.id.btn_preview:
                //获取当前4个标定点在屏幕的坐标
                IntergePoint[] screenPoints = mCalibrationPointImpl.getScreenPoints();
                Log.d("AdayoCamera", TAG + " - onClick: screenPoints : " + Utils.getString(screenPoints));
                //更新轨迹线
                calibrationTrackProxy.updateCalibrate(mWorldPoints, screenPoints);
                Log.d("AdayoCamera", TAG + " - onClick: preview");
                mLineView.onSteeringWheelValueChange(mSteeringWheelValue, true);
                mLineView.updateStaticList();
                break;

            case R.id.btn_finish:
                //确定标定完成，保存4标定点的坐标
                mCalibrationPointImpl.finishCalibration();
                containerCalibrationStep1.setVisibility(View.VISIBLE);
                containerCalibrationStep2.setVisibility(View.INVISIBLE);
                break;

            case R.id.btn_calibration:
                //进入标定模式
                LoadConfig.Point currentPoint = mCalibrationPointImpl.calibration();
                containerCalibrationStep1.setVisibility(View.INVISIBLE);
                containerCalibrationStep2.setVisibility(View.VISIBLE);
                mBtnSwitchPoint.setText(CURRENT_POINT + currentPoint.name() + SWITCH_POINT);
                break;

            case R.id.btn_turn_left:
                //方向盘角度变化
                mSteeringWheelValue = mSteeringWheelValue - 100;
                mLineView.onSteeringWheelValueChange(mSteeringWheelValue, true);
                break;

            case R.id.btn_turn_right:
                mSteeringWheelValue = mSteeringWheelValue + 100;
                mLineView.onSteeringWheelValueChange(mSteeringWheelValue, true);
                break;
            default:
                break;
        }
    }
}
