package com.adayo.app.dvr.function;

import static com.adayo.app.dvr.constant.Constant.DVR_EnterEditMode;
import static com.adayo.app.dvr.constant.Constant.DVR_ExitEditMode;
import static com.adayo.app.dvr.constant.Constant.DVR_ReStoreFactory;
import static com.adayo.app.dvr.constant.Constant.DVR_SETTING_SD_INFO;
import static com.adayo.app.dvr.constant.Constant.DVR_SdcardFormat;
import static com.adayo.app.dvr.constant.Constant.DriveMode;
import static com.adayo.app.dvr.constant.Constant.ON_CONFIGURATION_CHANGED;
import static com.adayo.app.dvr.constant.Constant.PAGE_RECORD;
import static com.adayo.app.dvr.constant.Constant.PAGE_SETTING;
import static com.adayo.app.dvr.constant.Constant.ParkMode;
import static com.adayo.app.dvr.constant.Constant.RecordTime;
import static com.adayo.app.dvr.constant.Constant.Resolution;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.widget.CustomDialog;
import com.adayo.app.dvr.widget.UnderCommonDialog;
import com.flyco.customtablayout.SegmentTabLayout;
import com.flyco.customtablayout.listener.OnTabSelectListener;


public class SettingControl implements View.OnClickListener {

    private static final String TAG = APP_TAG + SettingControl.class.getSimpleName();

    private MainActivity mMainActivity;
    private static volatile SettingControl mModel = null;

    private DvrController mDvrController;
    private ConstraintLayout llSettingBack;

    private DvrStatusInfo mDataInfo;
    private TextView tvSdSizeAll,
            tvSdSizeVideo, tvSdSizeEmergency, tvSdSizePhoto, tvSdSizeAtWill, tvSettingVersion;
    private ImageView ivSdFormat, ivDvrFormat;
    private ConstraintLayout clSettingResolution, clSettingDuration, clSettingMovingSensitivity, clSettingStoppingSensitivity;

    private SegmentTabLayout stResolution, stRecordingTime, stDrivingSensitivity, stParkingSensitivity;
    public UnderCommonDialog mUnderCommonDialog;
    public UnderCommonDialog mUnderCommonSuccessDialog;
    public UnderCommonDialog mUnderCommonFailDialog;
    public UnderCommonDialog mFormatDialog;
    public UnderCommonDialog mFormatSuccessDialog;
    public UnderCommonDialog mFormatFailDialog;
    public CustomDialog mFormatCustomDialog;
    public CustomDialog mRestCustomDialog;
    String allDiskSize = "";
    String photoSize = "";
    String videoSize = "";
    String emergencySize = "";
    String mAtWillSize = "";
    String allSize = "";

    private SettingControl(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mDvrController = DvrController.getInstance();
    }

    public static SettingControl getSettingControlInstance(MainActivity mainActivity) {
        if (mModel == null) {
            synchronized (SettingControl.class) {
                if (mModel == null) {
                    mModel = new SettingControl(mainActivity);
                }
            }
        }
        return mModel;
    }

    public void initView() {
        Log.d(TAG, "initView: ");
        llSettingBack = (ConstraintLayout) findViewById(R.id.ll_setting_back);

        tvSdSizeAll = (TextView) findViewById(R.id.tv_sd_size_all);
        tvSdSizeVideo = (TextView) findViewById(R.id.tv_sd_size_video);
        tvSdSizeEmergency = (TextView) findViewById(R.id.tv_sd_size_emergency);
        tvSdSizePhoto = (TextView) findViewById(R.id.tv_sd_size_photo);
        tvSdSizeAtWill = (TextView) findViewById(R.id.tv_sd_size_at_will);
        ivSdFormat = (ImageView) findViewById(R.id.iv_sd_format);
        ivDvrFormat = (ImageView) findViewById(R.id.iv_dvr_format);

        tvSettingVersion = (TextView) findViewById(R.id.tv_setting_version);

        clSettingResolution = (ConstraintLayout) findViewById(R.id.cl_setting_resolution);
        clSettingDuration = (ConstraintLayout) findViewById(R.id.cl_setting_duration);
        clSettingMovingSensitivity = (ConstraintLayout) findViewById(R.id.cl_setting_moving_sensitivity);
        clSettingStoppingSensitivity = (ConstraintLayout) findViewById(R.id.cl_setting_stopping_sensitivity);

        //初始化左侧item背景
        updateItemBg(0);

        //分辨率
        String[] titlesOne = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_resolution_arr);
        stResolution = (SegmentTabLayout) findViewById(R.id.common1);
        stResolution.setTabData(titlesOne);
        stResolution.setSelectTab(0);
        stResolution.isNeedCallbackUpdateUI(true);;
        stResolution.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i, boolean b) {
                if(b){
                    updateItemBg(1);
                    if (i == 0) {
                        mDvrController.setResolution(0);
                    } else {
                        mDvrController.setResolution(1);
                    }
                }

            }


        });
        //录制时间
        String[] titlesTwo = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_recording_time_arr);
        stRecordingTime = (SegmentTabLayout) findViewById(R.id.common2);
        stRecordingTime.setTabData(titlesTwo);
        stRecordingTime.setSelectTab(1);
        stRecordingTime.isNeedCallbackUpdateUI(true);;
        stRecordingTime.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i, boolean b) {
                Log.d(TAG, "onTabSelect2: " + i);
                if(b) {
                    updateItemBg(2);

                    if (i == 0) {
                        mDvrController.setRecordTime(1);
                    } else if (i == 1) {
                        mDvrController.setRecordTime(2);
                    } else {
                        mDvrController.setRecordTime(3);
                    }
                }
            }


        });
        //行车灵敏度
        String[] titlesThree = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_drive_sensitivity_arr);
        stDrivingSensitivity = (SegmentTabLayout) findViewById(R.id.common3);
        stDrivingSensitivity.setTabData(titlesThree);
        stDrivingSensitivity.setSelectTab(1);
        stDrivingSensitivity.isNeedCallbackUpdateUI(true);;
        stDrivingSensitivity.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i, boolean b) {
                if(b) {
                    updateItemBg(3);

                    if (i == 0) {
                        mDvrController.setDrivEss(3);
                    } else if (i == 1) {
                        mDvrController.setDrivEss(2);
                    } else {
                        mDvrController.setDrivEss(1);
                    }
                }
            }


        });
        //停车灵敏度
        String[] titlesFour = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_park_sensitivity_arr);
        stParkingSensitivity = (SegmentTabLayout) findViewById(R.id.common4);
        stParkingSensitivity.setTabData(titlesFour);
        stParkingSensitivity.setSelectTab(1);
        stParkingSensitivity.isNeedCallbackUpdateUI(true);
        stParkingSensitivity.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i,boolean b) {
                if(b) {
                    updateItemBg(4);
                    if (i == 0) {
                        mDvrController.setParKss(3);
                    } else if (i == 1) {
                        mDvrController.setParKss(2);
                    } else {
                        mDvrController.setParKss(0);
                    }
                }
            }


        });
        mDataInfo = DvrStatusInfo.getInstance();

    }


    private View findViewById(int id) {
        return mMainActivity.findViewById(id);
    }

    private Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DVR_SETTING_SD_INFO:
                    tvSdSizePhoto.setText(photoSize + "GB");
                    tvSdSizeVideo.setText(videoSize + "GB");
                    tvSdSizeEmergency.setText(emergencySize + "GB");
                    tvSdSizeAtWill.setText(mAtWillSize+"GB");
                    tvSdSizeAll.setText(allSize + "GB/" + allDiskSize + "GB");
                    Log.d(TAG, "sdCardInfoUpdate: " + mDataInfo.getSystemVersion());
                    tvSettingVersion.setText(mDataInfo.getSystemVersion());
                    break;
                case 2:
                    mMainActivity.changePage(PAGE_SETTING);
                    break;
                case 3:
                    mMainActivity.changePage(PAGE_RECORD);
                    break;
                case 4:
                    if(mFormatDialog != null){
                        mFormatDialog.dismiss();
                    }
                    if (mFormatSuccessDialog != null){
                        if (mFormatSuccessDialog.isShowing()){
                            break;
                        }
                    }
                    mFormatSuccessDialog =  UnderCommonDialog.getCommonDialog(mMainActivity).initStringImgView(mMainActivity.getString(R.string.text_setting_factory_formatting_success),R.mipmap.icon_success);
                    mFormatSuccessDialog.show();
                   mUiHandler.sendEmptyMessageDelayed( 12,500);
                    break;
                case 5:
                    if(mFormatDialog != null){
                        mFormatDialog.dismiss();
                    }
                    if (mFormatFailDialog != null){
                        if (mFormatFailDialog.isShowing()){
                            break;
                        }
                    }
                    mFormatFailDialog = UnderCommonDialog.getCommonDialog(mMainActivity).initStringImgView(mMainActivity.getString(R.string.text_setting_factory_formatting_fail),R.mipmap.icon_failure);
                    mFormatFailDialog.show();
                    mUiHandler.sendEmptyMessageDelayed( 12,500);
                    break;
                case 6:

                    if(mUnderCommonDialog != null){
                        mUnderCommonDialog.dismiss();
                    }
                    if (mUnderCommonSuccessDialog != null){
                        if (mUnderCommonSuccessDialog.isShowing()){
                            break;
                        }
                    }
//                    st_recording_time.setSelectTab(1);
//                    st_parking_sensitivity.setSelectTab(1);
//                    st_driving_sensitivity.setSelectTab(1);
//                    st_resolution.setSelectTab(0);
                    mUnderCommonSuccessDialog =  UnderCommonDialog.getCommonDialog(mMainActivity).initStringImgView(mMainActivity.getString(R.string.text_setting_factory_setting_success),R.mipmap.icon_success);
                    mUnderCommonSuccessDialog.show();
                    mUiHandler.sendEmptyMessageDelayed( 13,200);
                    break;
                case 7:
                    if(mUnderCommonDialog != null){
                        mUnderCommonDialog.dismiss();
                    }
                    if (mUnderCommonFailDialog != null){
                        if (mUnderCommonFailDialog.isShowing()){
                            break;
                        }
                    }
                    mUnderCommonFailDialog = UnderCommonDialog.getCommonDialog(mMainActivity).initStringImgView(mMainActivity.getString(R.string.text_setting_factory_setting_fail),R.mipmap.icon_failure);
                    mUnderCommonFailDialog.show();
                    mUiHandler.sendEmptyMessageDelayed( 13,200);
                    break;
                case 8:
                    stResolution.setSelectTab((int)msg.obj);
                    break;
                case 9:
                    stRecordingTime.setSelectTab((int)msg.obj);
                    break;
                case 10:
                    stDrivingSensitivity.setSelectTab((int)msg.obj);
                    break;
                case 11:
                    stParkingSensitivity.setSelectTab((int)msg.obj);
                    break;
                case 12:
                    ivSdFormat.setEnabled(true);
                    break;
                case 13:
                    ivDvrFormat.setEnabled(true);
                    break;
                default:
                    break;

            }
        }
    };

    public void sdCardInfoUpdate() {

        allDiskSize = mDataInfo.getAllDiskSize();
        photoSize = String.format("%.1f", mDataInfo.getPhotoSize() );
        videoSize = String.format("%.1f", mDataInfo.getVideoSize() );
        emergencySize = String.format("%.1f", mDataInfo.getEmergencyVideoSize());
        mAtWillSize = String.format("%.1f", mDataInfo.getAtWillSize() );
        allSize = String.format("%.1f", mDataInfo.getAllSize() );
        int sdSize = Integer.parseInt(allDiskSize);

        mUiHandler.sendEmptyMessageDelayed(DVR_SETTING_SD_INFO, 1000);
    }

    public void initListener() {
        Log.d(TAG, "initListener: ");
        llSettingBack.setOnClickListener(this);
        ivSdFormat.setOnClickListener(this);
        ivDvrFormat.setOnClickListener(this);

        sdCardInfoUpdate();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.ll_setting_back:
                mDataInfo.setmSettingStatus(false);
                DvrController.getInstance().outSetting();
                break;
            case R.id.iv_sd_format:

                mFormatCustomDialog =  CustomDialog.getInstance(mMainActivity)
                        .initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog_mid)
                        .setText(R.string.text_setting_sdcard_format_title,
                                R.string.text_setting_sdcard_format_tips, 0)
                        .setOkText(R.string.text_setting_sdcard_format_ok)
                        .setCancelText(R.string.text_cancel)
                        .setImageView(R.mipmap.icon_singlerow_format)
                        .initClickListener(new CustomDialog.OnClickListener() {
                            @Override
                            public void ok() {
                                mDvrController.setDvrSystemOperaType(1);
                                mFormatDialog = UnderCommonDialog.getCommonDialog(mMainActivity).initStringAnimView(mMainActivity.getString(R.string.text_setting_factory_formatting),R.drawable.anim_loading);
                                mFormatDialog.show();
                                ivSdFormat.setEnabled(false);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                mFormatCustomDialog.showDialog();
                break;
            case R.id.iv_dvr_format:

                mRestCustomDialog =  CustomDialog.getInstance(mMainActivity)
                        .initView(CustomDialog.TYPE_NORMAL, R.layout.layout_custom_dialog_mid)
                        .setText(R.string.text_setting_factory_setting,
                                R.string.text_setting_factory_reset_tips, 0)
                        .setOkText(R.string.text_setting_factory_reset_ok)
                        .setCancelText(R.string.text_cancel)
                        .setImageView(R.mipmap.icon_singlerow_reset)
                        .initClickListener(new CustomDialog.OnClickListener() {
                            @Override
                            public void ok() {
                                mDvrController.setDvrSystemOperaType(2);
                                mUnderCommonDialog =   UnderCommonDialog.getCommonDialog(mMainActivity).initStringAnimView(mMainActivity.getString(R.string.text_setting_factory_restoring),R.drawable.anim_loading);
                                mUnderCommonDialog.show();
                                ivDvrFormat.setEnabled(false);
                            }

                            @Override
                            public void cancel() {

                            }
                        });
                mRestCustomDialog.showDialog();
                break;
            default:
                break;
        }
    }
    private static final int DVR_SDCARDFORMAT_ONE = 1;
     private static final int DVR_SDCARDFORMAT_TWO = 2;
     private static final int DVR_RESTOREFACTORY_ONE =1;
     private static final int DVR_RESTOREFACTORY_TWO =2;
     private static final int RECORDTIME_THREE = 3 ;
     private static final int RECORDTIME_TWO = 2;
     private static final int RECORDTIME_ONE = 1;
     private static final int DRIVEMODE_ZERO = 0;
     private static final int DRIVEMODE_TWO = 2;
     private static final int DRIVEMODE_THREE = 3;
     private static final int PARKMODE_ZERO = 0;
     private static final int PARKMODE_TWO= 2;
     private static final int PARKMODE_THREE =3;
    public void notifyChange(String funcId, int value) {
        String current = mDvrController.getCurrentOperaName();
        Log.d(TAG, "notifyChange: funcId = " + funcId + ";value = " + value
                + ";current = " + current);
        switch (funcId){
            case DVR_EnterEditMode:
                if(value ==1){
                    mUiHandler.sendEmptyMessage(2);
                }else{
                    Log.d(TAG, "notifyChange: DVR_EnterEditMode  ==fail");
                }
                break;
            case DVR_ExitEditMode:
                if(value ==1 ){
                    mUiHandler.sendEmptyMessage(3);
                }else {
                    Log.d(TAG, "notifyChange: DVR_ExitEditMode  ==fail");
                }
                break;
            case DVR_SdcardFormat:
                Message msgFormat = new Message();
                if(value ==DVR_SDCARDFORMAT_ONE){
                    msgFormat.what = 4;
                    mUiHandler.sendMessageDelayed(msgFormat,1000);
                }else if (value ==DVR_SDCARDFORMAT_TWO){
                    msgFormat.what = 5;
                    mUiHandler.sendMessageDelayed(msgFormat,1000);
                }
                break;
            case DVR_ReStoreFactory:
                Message msgFactory = new Message();
                if(value ==DVR_RESTOREFACTORY_ONE){
                    msgFactory.what = 6;
                    mUiHandler.sendMessageDelayed(msgFactory,1500);
                }else if (value ==DVR_RESTOREFACTORY_TWO){
                    msgFactory.what = 7;
                    mUiHandler.sendMessageDelayed(msgFactory,1500);
                }
                break;
            case ON_CONFIGURATION_CHANGED:
                String[] mTitlesTwo = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_recording_time_arr);
                stRecordingTime.setTabData(mTitlesTwo);
                String[] mTitlesThree = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_drive_sensitivity_arr);
                stDrivingSensitivity.setTabData(mTitlesThree);
                String[] mTitlesFour = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_park_sensitivity_arr);
                stParkingSensitivity.setTabData(mTitlesFour);
                String[] mTitlesOne = mMainActivity.getResources().getStringArray(R.array.text_setting_dvr_resolution_arr);
                stResolution.setTabData(mTitlesOne);
                break;
            case Resolution:
                Message msgResolution = new Message();
                msgResolution.what = 8;
                msgResolution.obj = value;
                mUiHandler.sendMessage(msgResolution);
                break;
            case RecordTime:
                Message msgRecordTime = new Message();
                msgRecordTime.what = 9;
                if (value == RECORDTIME_THREE){
                    value  = 2;
                }else if (value == RECORDTIME_TWO){
                    value = 1;
                }else if (value == RECORDTIME_ONE){
                    value = 0 ;
                }
                msgRecordTime.obj = value;
                mUiHandler.sendMessage(msgRecordTime);
                break;
            case DriveMode:
                Message msgDriveMode = new Message();
                msgDriveMode.what = 10;
                if (value == DRIVEMODE_ZERO){
                    value  = 2;
                }else if (value == DRIVEMODE_TWO){
                    value = 1;
                }else if (value == DRIVEMODE_THREE){
                    value = 0 ;
                }
                msgDriveMode.obj = value;
                mUiHandler.sendMessage(msgDriveMode);
                break;
            case ParkMode:
                Message msgParkMode = new Message();
                msgParkMode.what = 11;
                if (value == PARKMODE_ZERO){
                    value  = 2;
                }else if (value == PARKMODE_TWO){
                    value = 1;
                }else if (value == PARKMODE_THREE){
                    value = 0 ;
                }
                msgParkMode.obj = value;
                mUiHandler.sendMessage(msgParkMode);
                break;
            default:
                break;
        }

    }




    /**
     * 更新左侧条目样式
     */
    private static final int UPDATE_ITEMBG_VALUE_ONE = 1;
    private static final int UPDATE_ITEMBG_VALUE_TWO = 2;
    private static final int UPDATE_ITEMBG_VALUE_THREE = 3;
    private static final int UPDATE_ITEMBG_VALUE_FOUR = 4;
    private void updateItemBg(int i) {
        if (i == UPDATE_ITEMBG_VALUE_ONE) {
            clSettingResolution.setBackgroundResource(R.mipmap.frame_site_left_last_sel);
            clSettingDuration.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingMovingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingStoppingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
        } else if (i == UPDATE_ITEMBG_VALUE_TWO) {
            clSettingResolution.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingDuration.setBackgroundResource(R.mipmap.frame_site_left_last_sel);
            clSettingMovingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingStoppingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
        } else if (i == UPDATE_ITEMBG_VALUE_THREE) {
            clSettingResolution.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingDuration.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingMovingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last_sel);
            clSettingStoppingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
        } else if (i == UPDATE_ITEMBG_VALUE_FOUR) {
            clSettingResolution.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingDuration.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingMovingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingStoppingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last_sel);
        } else {
            clSettingResolution.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingDuration.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingMovingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
            clSettingStoppingSensitivity.setBackgroundResource(R.mipmap.frame_site_left_last);
        }


    }

}
