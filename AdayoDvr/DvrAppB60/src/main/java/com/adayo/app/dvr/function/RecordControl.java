package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.Constant.*;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;


import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.utils.FastClickCheck;
import com.adayo.app.dvr.utils.SystemPropertiesPresenter;
import com.adayo.proxy.infrastructure.share.ShareDataManager;


import org.json.JSONException;
import org.json.JSONObject;

public class RecordControl implements View.OnClickListener  {

    private static final String TAG = APP_TAG + RecordControl.class.getSimpleName();

    private MainActivity mMainActivity;


    private static volatile RecordControl mModel = null;

    private ConstraintLayout llRecordOrdinaryVideo;
    private ConstraintLayout llRecordEmergencyVideo;
    private ConstraintLayout llRecordShot;
    private ConstraintLayout llRecordMic;
    private ConstraintLayout llRecordPlayback;
    private ConstraintLayout llRecordSetting;
    private ConstraintLayout llRecordClose;

    private ImageView ivOrdinaryVideo;
    private ImageView ivOrdinaryStopVideo;
    private TextView tvOrdinaryVideo;
    private TextView tvOrdinaryStopVideo;
    /*允许同时播放的音频数（为1时会立即结束上一个音频播放当前的音频）*/
    private static final int MAX_STREAMS = 1;
    private boolean isClose;
    private boolean isRecording;
    private boolean isEmergencyRecording;
    private boolean isPhotoGraph;
    private boolean isMicroOn;
    private int mSoundId;
    private SystemPropertiesPresenter mSystemProperties;
    private RecordControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public static RecordControl getRecordControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (RecordControl.class) {
                if (mModel == null) {
                    mModel = new RecordControl(mainActivity);
                }
            }
        }
        return mModel;
    }

    public void initView() {
        Log.d(TAG, "initView:  start");
        llRecordOrdinaryVideo = (ConstraintLayout) findViewById(R.id.ll_record_ordinary_video);
        llRecordEmergencyVideo = (ConstraintLayout) findViewById(R.id.ll_record_emergency_video);
        llRecordShot = (ConstraintLayout) findViewById(R.id.ll_record_shot);
        llRecordMic = (ConstraintLayout) findViewById(R.id.ll_record_mic);
        llRecordPlayback = (ConstraintLayout) findViewById(R.id.ll_record_playback);
        llRecordSetting = (ConstraintLayout) findViewById(R.id.ll_record_setting);
        llRecordClose = (ConstraintLayout) findViewById(R.id.ll_record_close);

        ivOrdinaryVideo = (ImageView) findViewById(R.id.iv_ordinary_video);
        ivOrdinaryStopVideo = (ImageView) findViewById(R.id.iv_ordinary_stop_video);
        tvOrdinaryVideo = (TextView) findViewById(R.id.tv_ordinary_video);
        tvOrdinaryStopVideo  = (TextView) findViewById(R.id.tv_ordinary_stop_video);


        isClose = false;
        isEmergencyRecording = false;
        isPhotoGraph = false;
        isRecording = false;

        Log.d(TAG, "initView:  end");
    }

    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    public void initListener() {
        Log.d(TAG, "initListener: start");
        llRecordOrdinaryVideo.setOnClickListener(this);
        llRecordEmergencyVideo.setOnClickListener(this);
        llRecordShot.setOnClickListener(this);
        llRecordMic.setOnClickListener(this);
        llRecordPlayback.setOnClickListener(this);
        llRecordSetting.setOnClickListener(this);
        llRecordClose.setOnClickListener(this);
        mSystemProperties =  SystemPropertiesPresenter.getInstance();
        Log.d(TAG, "initListener: end");

    }


    @Override
    public void onClick(View v) {
        //TODO 全都防抖
        if (!FastClickCheck.isFastClickTime(ANTI_SHAKE)) {
            return;
        }
        switch (v.getId()) {
            case R.id.ll_record_ordinary_video:
                DvrController.getInstance().setRecording(1);
                break;
            case R.id.ll_record_emergency_video:
                DvrController.getInstance().startEmergencyVideo();
                break;
            case R.id.ll_record_shot:
//                if (!FastClickCheck.isFastClickTime()) {
//                    return;
//                }
                isPhotoGraph = true;
                DvrController.getInstance().photoGraph();
                break;
            case R.id.ll_record_mic:
                DvrController.getInstance().setMicroPhone();
                break;
            case R.id.ll_record_playback:
                DvrController.getInstance().setDisplayMode(PLAYBACK_MODE);
                break;
            case R.id.ll_record_setting:
                Log.d(TAG, "onClick:  ll_record_setting");
                DvrStatusInfo.getInstance().setmSettingStatus(true);
                DvrController.getInstance().inSetting();
                break;
            case R.id.ll_record_close:
                DvrStatusInfo.getInstance().setmCloseStaus(true);
                DvrController.getInstance().powerOff();
                break;
            default:
                break;
        }
    }


    private static final int DVR_RETURN_PREVIEW_MODE = 2000;
    private static final int DVR_POWER_BTN = 2001;
    private static final int DVR_CAMERA_HIDE = 2002;
    private static final int DVR_POWER_OFF = 2003;
    private static final int DVR_POWER_ON = 2004;

    private static final int DVR_RECORDING_STOP = 2005;
    private static final int DVR_RECORDING_OPEN = 2006;

    private static final int DVR_OPEN_SETTING = 2008;

    private Handler mRecordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DVR_POWER_BTN:
                    llRecordClose.setEnabled(true);
                    break;
                case DVR_RECORDING_STOP:
//                    iv_ordinary_video.setImageResource(R.mipmap.icon_pause_n);
                    ivOrdinaryVideo.setVisibility(View.GONE);
                    ivOrdinaryStopVideo.setVisibility(View.VISIBLE);
                    tvOrdinaryStopVideo.setVisibility(View.VISIBLE);
                    tvOrdinaryVideo.setVisibility(View.GONE);
                    break;
                case DVR_RECORDING_OPEN:
                    ivOrdinaryStopVideo.setVisibility(View.GONE);
                    ivOrdinaryVideo.setVisibility(View.VISIBLE);
                    tvOrdinaryStopVideo.setVisibility(View.GONE);
                    tvOrdinaryVideo.setVisibility(View.VISIBLE);
                    break;
                case DVR_POWER_ON:
                    mMainActivity.layoutNotConnect.setVisibility(View.GONE);
                    break;
                case DVR_OPEN_SETTING:
                    mMainActivity.changePage(PAGE_SETTING);
                    break;
                case 1:
                    mMainActivity.disconnectDvr();
                    break;
//                case 2:
//                    okSound();
//                    break;
//                case 3:
//                    noSound();
//                    break;
                default:
                    break;


            }
        }
    };


    public void setPowerBtnEnabled() {
        llRecordClose.setEnabled(false);
        mRecordHandler.sendEmptyMessageDelayed(DVR_POWER_BTN, 1000);
    }

    /**
     * 普通录像设置为暂停
     */
    public void setRecordingBtnStop() {
        mRecordHandler.sendEmptyMessage(DVR_RECORDING_STOP);
    }

    /**
     * 普通录像设置为开启
     */
    public void setRecordingBtnOpen() {
        mRecordHandler.sendEmptyMessage(DVR_RECORDING_OPEN);
    }

    /**
     * 回调处理
     *
     * @param funcId
     * @param value
     */
    public void notifyChange(String funcId, int value) {
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value);
        switch (funcId) {
            case DVR_St_Recording:
                if(PREVIEW_MODE == DvrStatusInfo.getInstance().getDisplayMode()){
                    if (value == 1){
                        setRecordingBtnOpen();
                    }else{
                        setRecordingBtnStop();
                    }
                }
            case DVR_EmergencyVideo:

                Log.d(TAG, "notifyChange: DVR_EmergencyVideo");
                if (value == 1) {
                    Log.d(TAG, "notifyChange: DVR_EmergencyVideo success");
                } else {
                    Log.d(TAG, "notifyChange: DVR_EmergencyVideo fault");
                }
                break;
            case DVR_MicSwitch:
                Log.d(TAG, "notifyChange: DVR_MicSwitch");
                if (value == 1) {
                    Log.d(TAG, "notifyChange: DVR_MicSwitch success");
                } else {
                    Log.d(TAG, "notifyChange: DVR_MicSwitch fault");
                }
                break;
            case DVR_Power_OFF:
                Log.d(TAG, "notifyChange: DVR_Power_OFF" + value);
                if (value == 1) {
                    mRecordHandler.sendEmptyMessage(1);
                    mSystemProperties.setProperty("dvr.status","0");
                    Settings.Global.putInt(mMainActivity.getContentResolver(),"DVR_STATUS", 0);
                }

                break;
            case DVR_Power_On:
                Log.d(TAG, "notifyChange: DVR_Power_On" + value);
                if (value == 1) {
                    DvrStatusInfo.getInstance().setmCloseStaus(false);
                    mRecordHandler.sendEmptyMessageDelayed(DVR_POWER_ON, 1000);
                    mSystemProperties.setProperty("dvr.status","1");
                    Settings.Global.putInt(mMainActivity.getContentResolver(),"DVR_STATUS", 1);
                }

                break;
            default:
                break;


        }


    }

}
