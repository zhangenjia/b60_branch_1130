package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;

public class ErrorControl {

    private static final String TAG = APP_TAG + ErrorControl.class.getSimpleName();
    private MainActivity mMainActivity;
    private static volatile ErrorControl mModel = null;

    private ConstraintLayout llRecordOrdinaryErrorVideo;
    private ConstraintLayout llRecordEmergencyErrorVideo;
    private ConstraintLayout llRecordErrorShot;
    private ConstraintLayout llRecordErrorMic;
    private ConstraintLayout llRecordErrorPlayback;
    private ConstraintLayout llRecordErrorSetting;
    private ConstraintLayout llRecordErrorClose;

    private ImageView ivOrdinaryErrorVideo;
    private TextView tvOrdinaryErrorVideo;

    private ErrorControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public static ErrorControl getRecordControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (RecordControl.class) {
                if (mModel == null) {
                    mModel = new ErrorControl(mainActivity);
                }
            }
        }
        return mModel;
    }
    public void initView() {
        Log.d(TAG, "initView: ");
        llRecordOrdinaryErrorVideo = (ConstraintLayout) findViewById(R.id.ll_record_ordinary_error_video);
        llRecordEmergencyErrorVideo = (ConstraintLayout) findViewById(R.id.ll_record_emergency_error_video);
        llRecordErrorShot = (ConstraintLayout) findViewById(R.id.ll_record_error_shot);
        llRecordErrorMic = (ConstraintLayout) findViewById(R.id.ll_record_error_mic);
        llRecordErrorPlayback = (ConstraintLayout) findViewById(R.id.ll_record_error_playback);
        llRecordErrorSetting = (ConstraintLayout) findViewById(R.id.ll_record_error_setting);
        llRecordErrorClose = (ConstraintLayout) findViewById(R.id.ll_record_error_close);

        ivOrdinaryErrorVideo = (ImageView) findViewById(R.id.iv_ordinary_error_video);
        tvOrdinaryErrorVideo = (TextView) findViewById(R.id.tv_ordinary_error_video);
    }

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }


}
