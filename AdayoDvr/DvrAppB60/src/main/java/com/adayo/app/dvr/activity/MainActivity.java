
package com.adayo.app.dvr.activity;

import static com.adayo.app.dvr.constant.Constant.*;
import static com.adayo.app.dvr.constant.Constant.PAGE_EDIT;
import static com.adayo.app.dvr.constant.Constant.PAGE_PLAYBACK;
import static com.adayo.app.dvr.constant.Constant.PAGE_RECORD;
import static com.adayo.app.dvr.constant.Constant.PAGE_REPLAY;
import static com.adayo.app.dvr.constant.Constant.PAGE_SETTING;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import static java.lang.StrictMath.abs;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.entity.DvrStatusInfo;
import com.adayo.app.dvr.function.EditControl;
import com.adayo.app.dvr.function.EditWillControl;
import com.adayo.app.dvr.function.ErrorControl;
import com.adayo.app.dvr.function.FreeshotPlayControl;
import com.adayo.app.dvr.function.NoticeControl;
import com.adayo.app.dvr.function.PlaybackControl;
import com.adayo.app.dvr.function.RecordControl;
import com.adayo.app.dvr.function.ReplayControl;
import com.adayo.app.dvr.function.SettingControl;
import com.adayo.app.dvr.utils.SystemPropertiesPresenter;
import com.adayo.camera_proxy.CameraAuthentication;
import com.adayo.proxy.aaop_hskin.activity.base.AAOP_HSkinActivity;
import com.adayo.proxy.adas.evs.EvsCameraMng;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.alibaba.fastjson.JSON;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MainActivity extends AAOP_HSkinActivity  {

    private static final String TAG = APP_TAG + MainActivity.class.getSimpleName();

    private Context mContext;
    static Runnable runnable;
    public Boolean mDrivingWarningFlag = null;
    public boolean showFlag;
    public boolean errorFlag;
    public boolean mNasFocus;
    public boolean stopShowFlag;
    public int evsValue   ;
    public int noEvsNum = 0  ;
    public int okEvsNum = 0  ;
    IShareDataListener listener = new ShareInfoListener();
    IShareDataListener rvclistener = new SharervcInfoListener();
    //UI相关
    private TextureView mTextureView;
    private View colseDvrView;
    private ConstraintLayout clRecord;
    private ConstraintLayout clPlayback;
    private ConstraintLayout clEdit;
    private ConstraintLayout clReplay;
    private ConstraintLayout clSetting;
    public ConstraintLayout layoutNotConnect;
    public ConstraintLayout clCameraError;
    private ConstraintLayout clNotice;
    private ConstraintLayout clAtWillTot;
    private ConstraintLayout clEditWill;
    private SystemPropertiesPresenter mSystemProperties;

    //Camera相关
    private byte mCamera = 1;
    private int mStatus = 1;
    private int num = 10;
    private int successNum = 0;
    private int errorNum = 0;
    private Surface mSurface;
    private CameraAuthentication mCameraAuthentication;
    private int mWidth, mHeight;
    private volatile boolean mOpenCamera = false;
    private volatile boolean mOpenCameraFinish = false;
    private volatile boolean mCloseCamera = false;

    //工具类
    private DvrStatusInfo mDvrStatusInfo;
    private DvrController mDvrController;
    private RecordControl mRecordControl;
    private PlaybackControl mPlaybackControl;
    private EditControl mEditControl;
    private ReplayControl mReplayControl;
    private SettingControl mSettingControl;
    private ErrorControl mErrorControl;
    private EditWillControl mEditWillControl;
    private FreeshotPlayControl mFreeshotPlayControl;
    private NoticeControl mNoticeControl;
    //是否接受DVR_Common_Cb_Func信号的回调
    private boolean isAcceptCommon = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mDvrStatusInfo = DvrStatusInfo.getInstance();
        mDvrController = DvrController.getInstance();
        initView();
        initListener();
        mDvrController.init();
        mDvrController.setDisplayMode(Constant.DISPLAY_MODE_SET_PREVIEW);
        //初始化
//        DvrController.getInstance().setRecording(1);

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        initEvs();
        registerCameraListener();
        if (stopShowFlag){
            stopShowFlag = false;
            mMainUiHandler.sendEmptyMessageDelayed(DELAY_TO_SHOW,1000);
        }
        Settings.Global.putInt(getApplicationContext().getContentResolver(),"dvrShow",1);
        Log.d(TAG, "onResume: current type===" + DvrStatusInfo.getInstance().getType());
        Log.d(TAG, "onResume: current mode===" + DvrStatusInfo.getInstance().getMode());
    }

    private void setSystemUiVisibility(boolean visibility) {
        Log.d(TAG, "onSystemUIVisibility: ");
        View decorView = getWindow().getDecorView();
        if (visibility) {
            // 全屏
            Log.d(TAG, "SystemUI Hide");
            //状态栏透明，可覆盖
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //导航栏透明，可覆盖
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            // 非全屏
            Log.d(TAG, "SystemUI Show");
            // 隐藏导航栏、状态栏
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mNasFocus = true;
        showFlag = true;
        getDrivingWarning(ShareDataManager.getShareDataManager().getShareData(SHARER_ID_GENERAL));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case (MotionEvent.ACTION_DOWN):
                if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE){
                    if (mPlaybackControl.timer != null) {
                        mPlaybackControl.timer.cancel();
                    }
                    closeDialog();
                }
                if (mDvrStatusInfo.getDisplayMode() == PREVIEW_MODE  && mDvrStatusInfo.ismSettingStatus() ){
                    closeDialog();
                }
                break;
            case (MotionEvent.ACTION_UP):
                if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE){
                    mPlaybackControl.timer.start();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDvrStatusInfo.getDisplayMode() != PLAYBACK_MODE){
            colseDvrView.setVisibility(View.VISIBLE);
        }
        stopShowFlag = true;
        mNasFocus = false;
        showFlag = false;
    }
    /**
     * 初始化view
     */
    private void initView() {
        Log.d(TAG, "initView: start");
        setSystemUiVisibility(true);

        mTextureView = findViewById(R.id.textureview_dvr);
        colseDvrView = findViewById(R.id.textureview_dvr_close);
        clRecord = findViewById(R.id.cl_record);
        clPlayback = findViewById(R.id.cl_playback);
        clEdit = findViewById(R.id.cl_edit);
        clReplay = findViewById(R.id.cl_replay);
        clSetting = findViewById(R.id.cl_setting);
        clNotice = findViewById(R.id.cl_notice);
        clCameraError = findViewById(R.id.cl_camera_error);
        layoutNotConnect = findViewById(R.id.layout_not_connect);
        clAtWillTot = findViewById(R.id.cl_at_will_tot);
        clEditWill = findViewById(R.id.cl_edit_will);

        mRecordControl = RecordControl.getRecordControlInstance(this);
        mRecordControl.initView();
        mPlaybackControl = PlaybackControl.getPlaybackControlInstance(this);
        mPlaybackControl.initView();
        mEditControl = EditControl.getEditControlInstance(this);
        mEditControl.initView();
        mReplayControl = ReplayControl.getReplayControlInstance(this);
        mReplayControl.initView();
        mSettingControl = SettingControl.getSettingControlInstance(this);
        mSettingControl.initView();
        mErrorControl = ErrorControl.getRecordControlInstance(this);
        mErrorControl.initView();
        mFreeshotPlayControl = FreeshotPlayControl.getFreeshotPlayControlInstance(this);
        mFreeshotPlayControl.initView();
        mEditWillControl = EditWillControl.getEditControlInstance(this);
        mEditWillControl.initView();
        mNoticeControl = NoticeControl.getNoticeControlInstance(this);
        mNoticeControl.initView();
        mCameraAuthentication = CameraAuthentication.getInstance(mContext, "dvr");
        Log.d(TAG, "initView: end");

    }

    private static final int DVR_SET_MIC_VAL = 3002;
    private static final int DVR_SET_PMS_VAL = 3003;
    private static final int DVR_SET_DMS_VAL = 3004;
    private static final int DVR_SET_RES_VAL = 3005;
    private static final int DVR_SET_REC_VAL = 3006;
    private static final int DVR_SET_CONNECT_STS_ON = 3007;
    private static final int DVR_SET_CONNECT_STS_OFF = 3008;
    private static final int EVS_CALLBACK = 10001;
    private static final int EVS_NO_VALUE = 10002;
    private static final int EVS_OK_VALUE = 10003;
    private static final int AGAIN_CAMERA = 10004;
    private static final int DELAY_TO_SHOW = 10005;
    private static final int cycles_Count = 5 ;
    public Handler mMainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "mMainUiHandler"+" handleMessage. what = " + msg.what);
            switch (msg.what) {
                case DVR_SET_MIC_VAL:
                    mDvrController.setMicroPhone();
                    break;
                /*case DVR_SET_RES_VAL:
                    mDvrController.setResolution(mDVRStatusInfo.getResolutionStatus());
                    break;
                case DVR_SET_
                REC_VAL:
                    mDvrController.setRecording(Constant.COMMON_RECORDING_ON);
                    break;*/
                case DVR_SET_CONNECT_STS_OFF:
                    layoutNotConnect.setVisibility(View.VISIBLE);
                    changePage(PAGE_RECORD);
                    break;
                case DVR_SET_CONNECT_STS_ON:
                    layoutNotConnect.setVisibility(View.GONE);
                    break;
                case EVS_CALLBACK:
                    initEvs();
                    break;
                case EVS_NO_VALUE:
                    if (0 >= evsValue){
                        if (noEvsNum >= cycles_Count){
                            Log.d(TAG, "handleMessage: ADAYO_AVM_CAMERA_SIGNAL_STATE true = "+ evsValue);
                            errorFlag = true;
                            clCameraError.setVisibility(View.VISIBLE);
                            noEvsNum = 0;
                            mMainUiHandler.removeMessages(EVS_NO_VALUE);
                        }else{
                            noEvsNum = noEvsNum + 1;
                            Log.d(TAG, "handleMessage: ADAYO_AVM_CAMERA_SIGNAL_STATE noEvsNum = "+ noEvsNum);
                            mMainUiHandler.sendEmptyMessageDelayed(EVS_NO_VALUE, 1000);
                        }
                    }else {
                        noEvsNum = 0;
                        mMainUiHandler.removeMessages(EVS_NO_VALUE);
                    }
                    break;
                case EVS_OK_VALUE:
                    if (0 < evsValue){
                        if (okEvsNum >= cycles_Count){
                            Log.d(TAG, "handleMessage: ADAYO_AVM_CAMERA_SIGNAL_STATE fail = "+ evsValue);
                            errorFlag = false;
                            clCameraError.setVisibility(View.GONE);
                            okEvsNum = 0;
                            if (colseDvrView.getVisibility() == View.VISIBLE){
                                mMainUiHandler.sendEmptyMessageDelayed(DELAY_TO_SHOW,1000);
                            }
                            mMainUiHandler.removeMessages(EVS_OK_VALUE);
                        }else{
                            okEvsNum = okEvsNum + 1;
                            mMainUiHandler.sendEmptyMessageDelayed(EVS_OK_VALUE,1000);
                        }
                    }else {
                        okEvsNum = 0;
                        mMainUiHandler.removeMessages(EVS_OK_VALUE);
                    }
                    break;
                case 3:
                    if (!errorFlag) {
                        mDvrStatusInfo.setmSpeedError(false);
                        mDvrStatusInfo.setmReplayStatus(false);
                        mDvrStatusInfo.setmFreeshotplayStatus(false);
                        mDvrStatusInfo.setmPlayBackStatus(false);
                        mDvrStatusInfo.setmAtWillTypeStatus(false);
                        mDvrStatusInfo.setmPlayStatus(false);
                        mDvrStatusInfo.setmEditStatus(false);
                        mDvrStatusInfo.setmClickStatus(false);
                        mDvrStatusInfo.setmEditWillStatus(false);
                        mDvrStatusInfo.setmRefreshStatus(false);
                        mPlaybackControl.replyVideo();
                        changePage(PAGE_RECORD);
                        closeDialog();
                    }
                    break;
                case AGAIN_CAMERA:
                    registerCameraListener();
                    mMainUiHandler.sendEmptyMessageDelayed(DELAY_TO_SHOW,1000);
                    break;
                case DELAY_TO_SHOW:
                    if (!errorFlag){
                    colseDvrView.setVisibility(View.GONE);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public final EvsCameraMng.IEvsServiceCallBack mEvsServiceCallBack = new EvsCameraMng.IEvsServiceCallBack() {
        @Override
        public synchronized void notifyDeviceValue(String s, int i) {
            Log.d(TAG, "notifyDeviceValue:  s = " + s + " i = " + i);
            if (ADAYO_AVM_CAMERA_SIGNAL_STATE.equals(s)) {
                if (0 >= i) {
                    okEvsNum = 0;
                    mMainUiHandler.removeMessages(EVS_OK_VALUE);
                    if (clCameraError.getVisibility() == View.VISIBLE){
                        noEvsNum = 0;
                        return;
                    }
                    evsValue = i;
                    if (noEvsNum == 0 ){
                        noEvsNum = noEvsNum + 1;
                        mMainUiHandler.sendEmptyMessageDelayed(EVS_NO_VALUE,1000);
                    }

                } else {
                    noEvsNum = 0;
                    mMainUiHandler.removeMessages(EVS_NO_VALUE);
                    if (clCameraError.getVisibility() == View.GONE){
                        okEvsNum = 0 ;
                        return;
                    }
                    evsValue = i;
                    if (okEvsNum == 0 ){
                        okEvsNum = okEvsNum + 1;
                        mMainUiHandler.sendEmptyMessageDelayed(EVS_OK_VALUE,1000);
                    }
                }
            }

        }
    };

    private void initEvs() {
//        connectAndRegisterEvs();
        boolean canConnectRet = EvsCameraMng.connectEvsService();
        if (!canConnectRet) {
            Log.d(TAG, "initEvs: Evs register canConnectRet = "+ canConnectRet );
            mMainUiHandler.removeMessages(EVS_CALLBACK);
            mMainUiHandler.sendEmptyMessageDelayed(EVS_CALLBACK, 50);
        }else {
            Log.d(TAG, "initEvs: Evs packageName  " + PACKAGE_NAME);
            boolean regStatus = EvsCameraMng.registEvsServiceCallback(mEvsServiceCallBack, PACKAGE_NAME);
            Log.d(TAG, "initEvs: regStatus  " + regStatus);
            Log.d(TAG, "initEvs: Evs registEvsServiceCallback  evsServiceCallBack" + mEvsServiceCallBack);
        }
    }


    private int devConnectSts = Constant.DVR_CONNECT_STS_INIT;
    //初始化为三种状态以外
    public int mDisplayMode = Constant.DISPLAY_MODE_SET_OTHER;
    private boolean isAccOff = true;
    private boolean isClose = false;
    private boolean isSdCard = true;
    private boolean isAppComm = false;
    private static  final  int DVR_St_ThumbnailOpera_Value_two = 2;
    private static  final  int DVR_St_ThumbnailOpera_Value_three = 3;
    private static  final  int  Total_fcapacity_two = 2;
    private static  final  int  Total_fcapacity_three = 3;
    private static  final  int  Total_fcapacity_four = 4;
    private DvrController.DvrCallback mDvrCallback = new DvrController.DvrCallback() {

        @Override
        public void notifyChange(Bundle bundle) {
            String funcId = "";
            int value = -1;
            try {
                funcId = bundle.getString("funcId");

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            Log.d(TAG, "notifyChange: funcId = " + funcId);
            if (DVR_Common_Cb_Func.equals(funcId)) {
                Log.d(TAG, "notifyChange: getDisplayMode = " + bundle.getInt(Constant.DVR_St_DisplayMode) );
                Log.d(TAG, "notifyChange: DVR_St_Recording == " +bundle.getInt(Constant.DVR_St_Recording));
                //选择或取消选中缩略图
                Log.d(TAG, "notifyChange: DVR_St_ThumbnailSel DVR_St_ThumbnailOpera = " + bundle.getInt(DVR_St_ThumbnailOpera));
                if (bundle.containsKey(DVR_St_ThumbnailOpera)) {
                    if (bundle.getInt(DVR_St_ThumbnailOpera) == 1) {
                        Log.d(TAG, "" + "notifyChange: DVR_St_ThumbnailSel " + bundle.getInt(DVR_St_ThumbnailSel));
                        if (mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.notifyChange(DVR_St_Cancel_ThumbnailSel, bundle.getInt(DVR_St_ThumbnailSel));
                        }else{
                            mEditControl.notifyChange(DVR_St_Cancel_ThumbnailSel, bundle.getInt(DVR_St_ThumbnailSel));
                        }
                    } else if (bundle.getInt(DVR_St_ThumbnailOpera) == DVR_St_ThumbnailOpera_Value_two) {
                        Log.d(TAG, "notifyChange: DVR_St_ThumbnailOpera operation success");
                        if(DvrStatusInfo.getInstance().getmEditStatus()){
                            mEditControl.notifyChange(DVR_St_Select_ThumbnailSel, bundle.getInt(DVR_St_ThumbnailSel));
                        }else if (mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.notifyChange(DVR_St_Select_ThumbnailSel, bundle.getInt(DVR_St_ThumbnailSel));
                        }
                        else if (DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                            mFreeshotPlayControl.notifyChange(DVR_St_ThumbnailOpera, bundle.getInt(DVR_St_ThumbnailOpera));
                        }else
                        {
                            Log.d(TAG, "notifyChange: mReplayControl DVR_St_ThumbnailOpera " + bundle.getInt(DVR_St_ThumbnailOpera));
                            mReplayControl.notifyChange(DVR_St_ThumbnailOpera, bundle.getInt(DVR_St_ThumbnailOpera));
                        }

                    } else if (bundle.getInt(DVR_St_ThumbnailOpera) == DVR_St_ThumbnailOpera_Value_three) {
                        Log.d(TAG, "notifyChange: DVR_St_ThumbnailOpera operation fail");
                    } else {
                        Log.d(TAG, "notifyChange: DVR_St_ThumbnailOpera norequest");
                    }
                }
                if (bundle.containsKey(DVR_St_CurrentThumbnail)) {
                    //todo 设置缩略图当前选中状态
                    Log.d(TAG, "notifyChange: DVR_St_CurrentThumbnail = " + bundle.getInt(DVR_St_CurrentThumbnail));
                        mPlaybackControl.notifyChange(DVR_St_CurrentThumbnail, bundle.getInt(DVR_St_CurrentThumbnail));
                        if(mDvrStatusInfo.ismEditWillStatus()){
                        if (mDvrStatusInfo.ismRefreshStatus() && !mDvrStatusInfo.ismClickStatus()){
                            mEditWillControl.notifyChange(DVR_St_CurrentThumbnail, bundle.getInt(DVR_St_CurrentThumbnail));
                        }else if (!mDvrStatusInfo.ismClickStatus()){
                            mEditWillControl.notifyChange(DVR_St_CurrentThumbnail, bundle.getInt(DVR_St_CurrentThumbnail));
                        }
                    }else if (mDvrStatusInfo.getmEditStatus()){
                        if (mDvrStatusInfo.ismRefreshStatus() && !mDvrStatusInfo.ismClickStatus()){
                            mEditControl.notifyChange(DVR_St_CurrentThumbnail, bundle.getInt(DVR_St_CurrentThumbnail));
                        }else if (!mDvrStatusInfo.ismClickStatus()){
                            mEditControl.notifyChange(DVR_St_CurrentThumbnail, bundle.getInt(DVR_St_CurrentThumbnail));
                        }
                    }
                }
                //当前播放模式
                if (bundle.getInt(Constant.DVR_St_DisplayMode) == 1) {
                    Log.d(TAG, "displaymode = preview");
                    DvrStatusInfo.getInstance().setDisplayMode(PREVIEW_MODE);
                    if(!mDvrStatusInfo.ismSettingStatus() && !mDvrStatusInfo.ismCloseStaus()){
                        mMainUiHandler.removeMessages(3);
                        mMainUiHandler.sendEmptyMessage(3);
                    }
                } else if (bundle.getInt(Constant.DVR_St_DisplayMode) == PLAYBACK_MODE) {
                    Log.d(TAG, "displaymode = playback");
                    if(!DvrStatusInfo.getInstance().getmEditStatus() && !DvrStatusInfo.getInstance().ismEditWillStatus()){
                        if (mDvrStatusInfo.getDisplayMode() != bundle.getInt(Constant.DVR_St_DisplayMode) ){
                            DvrStatusInfo.getInstance().setDisplayMode(PLAYBACK_MODE);
                            Log.d(TAG, "notifyChange:1233333 mPlayStatus() = " + (!mDvrStatusInfo.ismPlayStatus()));
                            if(!mDvrStatusInfo.ismPlayStatus()){
                                mPlaybackControl.notifyChange(DVR_St_DisplayMode,bundle.getInt(DVR_St_DisplayMode) );
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "displaymode = no request");
                }

                //Log.d(TAG, "notifyChange: DVR_SystemInfo");
                //todo 系统设置


                //SD卡总内存
                if (bundle.getInt(TotalTfcapacity) == 0) {
                    mDvrStatusInfo.setAllDiskSize(DVR_VALUE_TFCAPACITY_RESULT_8G);
                } else if (bundle.getInt(TotalTfcapacity) == 1) {
                    mDvrStatusInfo.setAllDiskSize(DVR_VALUE_TFCAPACITY_RESULT_16G);
                } else if (bundle.getInt(TotalTfcapacity) == Total_fcapacity_two) {
                    mDvrStatusInfo.setAllDiskSize(DVR_VALUE_TFCAPACITY_RESULT_32G);
                } else if (bundle.getInt(TotalTfcapacity) == Total_fcapacity_three) {
                    mDvrStatusInfo.setAllDiskSize(DVR_VALUE_TFCAPACITY_RESULT_64G);
                } else if (bundle.getInt(TotalTfcapacity) == Total_fcapacity_four) {
                    mDvrStatusInfo.setAllDiskSize(DVR_VALUE_TFCAPACITY_RESULT_128G);
                }
                Log.d(TAG, "notifyChange: StorageCapacity=" + bundle.getDouble(StorageCapacity));
                mDvrStatusInfo.setAllSize(bundle.getDouble(StorageCapacity));
                //照片容量
                Log.d(TAG, "notifyChange: photosize=" + bundle.getDouble(PhotoCapacity));
                mDvrStatusInfo.setPhotoSize(bundle.getDouble(PhotoCapacity));
                //普通视频容量
                Log.d(TAG, "notifyChange: videosize=" + bundle.getDouble(VideoCapacity));
                mDvrStatusInfo.setVideoSize(bundle.getDouble(VideoCapacity));
                //紧急视频容量
                Log.d(TAG, "notifyChange: Emergencyvideosize=" + bundle.getDouble(EmergencyVideoCapacity));
                mDvrStatusInfo.setEmergencyVideoSize(bundle.getDouble(EmergencyVideoCapacity));
                //随心拍容量
                Log.d(TAG, "notifyChange: atwillsize=" + bundle.getDouble(TakePhotoatwillCapacity));
                mDvrStatusInfo.setAtWillSize(bundle.getDouble(TakePhotoatwillCapacity));
                //系统版本
                mDvrStatusInfo.setSystemVersion("V" + bundle.getString(SystemVersion));


                //获取当前页和总页数
                Log.d(TAG, "notifyChange: DVR_St_TotalPage:" + bundle.getInt(DVR_St_TotalPage));
                mDvrStatusInfo.setTotalPage(bundle.getInt(DVR_St_TotalPage));
                Log.d(TAG, "notifyChange: getDVR_St_TotalPage:" +  mDvrStatusInfo.getTotalPage());
                mPlaybackControl.notifyChange(DVR_St_TotalPage, bundle.getInt(DVR_St_TotalPage));
                mEditControl.notifyChange(DVR_St_TotalPage, bundle.getInt(DVR_St_TotalPage));
                mEditWillControl.notifyChange(DVR_St_TotalPage, bundle.getInt(DVR_St_TotalPage));
                Log.d(TAG, "notifyChange: DVR_St_CurrentPage:" + bundle.getInt(DVR_St_CurrentPage));
                mDvrStatusInfo.setCurrentPage(bundle.getInt(DVR_St_CurrentPage));
                mPlaybackControl.notifyChange(DVR_St_CurrentPage, bundle.getInt(DVR_St_CurrentPage));
                mEditControl.notifyChange(DVR_St_CurrentPage, bundle.getInt(DVR_St_CurrentPage));
                mEditWillControl.notifyChange(DVR_St_CurrentPage, bundle.getInt(DVR_St_CurrentPage));
                //点击预览模式-普通录像
                if (bundle.containsKey(DVR_St_Recording)) {
                    if (mDvrStatusInfo.getDisplayMode() == PREVIEW_MODE) {
                        mRecordControl.notifyChange(DVR_St_Recording, bundle.getInt(DVR_St_Recording));
                    }
                }
                //点击预览模式-紧急录像 除拍照其他按钮不可用
                if (bundle.containsKey(DVR_EmergencyVideo)) {
                    Log.d(TAG, "notifyChange: DVR_EmergencyVideo ");
                    if (mDvrStatusInfo.getDisplayMode() == PREVIEW_MODE) {
                        mRecordControl.notifyChange(DVR_EmergencyVideo, bundle.getInt(Constant.DVR_EmergencyVideo));
                    }
                }
                //播放视频
                if (bundle.containsKey(DVR_Play)) {
                    Log.d(TAG, "notifyChange: DVR_Play");
                    if (bundle.getInt(DVR_Play) == 1){
                        if (mDvrStatusInfo.ismPlayBackStatus() || mDvrStatusInfo.ismWillPlaystatus()){
                            getDrivingWarning(ShareDataManager.getShareDataManager().getShareData(SHARER_ID_GENERAL));
                        }
                    }
                    if (DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                        mFreeshotPlayControl.notifyChange(DVR_Play,bundle.getInt(DVR_Play));
                    }else{
                        mReplayControl.notifyChange(DVR_Play, bundle.getInt(DVR_Play));
                    }

                }

                if (bundle.containsKey(DVR_Pause)) {
                    Log.d(TAG, "notifyChange: DVR_Pause");
                    if (DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                        mFreeshotPlayControl.notifyChange(DVR_Pause,bundle.getInt(DVR_Pause));
                    }else{
                        mReplayControl.notifyChange(DVR_Pause, bundle.getInt(DVR_Pause));
                    }

                }
                if (bundle.containsKey(DVR_NextVideoOrPhoto)) {
                    Log.d(TAG, "notifyChange: DVR_NextVideoOrPhoto");
                    if (DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                        mFreeshotPlayControl.notifyChange(DVR_NextVideoOrPhoto,bundle.getInt(DVR_NextVideoOrPhoto));
                    }else{
                        mReplayControl.notifyChange(DVR_NextVideoOrPhoto, bundle.getInt(DVR_NextVideoOrPhoto));
                    }

                }
                if (bundle.containsKey(DVR_LastVideoOrPhoto)) {
                    Log.d(TAG, "notifyChange: DVR_LastVideoOrPhoto");
                    if (DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                        mFreeshotPlayControl.notifyChange(DVR_LastVideoOrPhoto,bundle.getInt(DVR_LastVideoOrPhoto));
                    }else{
                        mReplayControl.notifyChange(DVR_LastVideoOrPhoto, bundle.getInt(DVR_LastVideoOrPhoto));
                    }

                }
//                //点击预览模式-拍照
//                if (bundle.containsKey(DVR_Photograph)) {
//                    Log.d(TAG, "notifyChange: DVR_Photograph ");
//                    if (mDVRStatusInfo.getDisplayMode() == PREVIEW_MODE) {
//                        mRecordControl.notifyChange(DVR_Photograph, bundle.getInt(Constant.DVR_Photograph));
//                    }
//                }
                //点击预览模式-麦克风
                if (bundle.containsKey(DVR_MicSwitch)) {
                    Log.d(TAG, "notifyChange: DVR_MicSwitch ");
                    mRecordControl.notifyChange(DVR_MicSwitch, bundle.getInt(Constant.DVR_MicSwitch));
                }
                //点击预览模式-关闭dvr
                if (bundle.containsKey(DVR_Power_OFF)) {
                    Log.d(TAG, "notifyChange: DVR_Power_OFF ");
                    mRecordControl.notifyChange(DVR_Power_OFF, bundle.getInt(Constant.DVR_Power_OFF));
                }
                //点击预览模式-打开dvr
                if (bundle.containsKey(DVR_Power_On)) {
                    Log.d(TAG, "notifyChange: DVR_Power_On ");
                    mRecordControl.notifyChange(DVR_Power_On, bundle.getInt(Constant.DVR_Power_On));
                }
                //点击回放-普通录像-编辑
                if (bundle.containsKey(DVR_EnterEditMode)) {
                    Log.d(TAG, "notifyChange: DVR_EnterEditMode ");
                    if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE) {
                        if(mDvrStatusInfo.ismEditWillStatus()){
                            mPlaybackControl.notifyChange(DVR_Will_EnterEditMode, bundle.getInt(DVR_EnterEditMode));
                        }else{
                            mPlaybackControl.notifyChange(DVR_EnterEditMode, bundle.getInt(DVR_EnterEditMode));
                        }
                    }else if(mDvrStatusInfo.getDisplayMode() == PREVIEW_MODE){
                        mSettingControl.notifyChange(DVR_EnterEditMode,bundle.getInt(DVR_EnterEditMode));
                    }
                }
                //点击回放-编辑-返回
                if (bundle.containsKey(DVR_ExitEditMode)) {
                    Log.d(TAG, "notifyChange: DVR_ExitEditMode ");
                    if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE) {
                        if(mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.notifyChange(DVR_ExitEditMode, bundle.getInt(DVR_ExitEditMode));
                        }else{
                            mEditControl.notifyChange(DVR_ExitEditMode, bundle.getInt(DVR_ExitEditMode));
                        }
                    }else if(mDvrStatusInfo.getDisplayMode() == PREVIEW_MODE){
                        mSettingControl.notifyChange(DVR_ExitEditMode,bundle.getInt(DVR_ExitEditMode));
                    }
                }
                //点击回放-编辑-全选
                if (bundle.containsKey(DVR_SelectAllDocCorDocStore)) {
                    Log.d(TAG, "notifyChange: DVR_SelectAllDocCorDocStore ");
                    if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE) {
                        if(mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.notifyChange(DVR_SelectAllDocCorDocStore, bundle.getInt(DVR_SelectAllDocCorDocStore));
                        }else{
                            mEditControl.notifyChange(DVR_SelectAllDocCorDocStore, bundle.getInt(DVR_SelectAllDocCorDocStore));
                        }
                    }
                }
                //点击回放-编辑 取消全选
                if (bundle.containsKey(DVR_CancelSelAllSelectedDoc)) {
                    Log.d(TAG, "notifyChange: DVR_CancelSelAllSelectedDoc");
                    if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE) {
                        if(mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.notifyChange(DVR_CancelSelAllSelectedDoc, bundle.getInt(DVR_CancelSelAllSelectedDoc));
                        }else{
                            mEditControl.notifyChange(DVR_CancelSelAllSelectedDoc, bundle.getInt(DVR_CancelSelAllSelectedDoc));
                        }

                    }
                }
                // 点击随心拍-进入随心拍
                if(bundle.containsKey(DVR_TakeVideoAtWill)){
                    Log.d(TAG, "notifyChange: DVR_TakeVideoAtWill");
                    if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE) {
                        mPlaybackControl.notifyChange(DVR_TakeVideoAtWill, bundle.getInt(Constant.DVR_TakeVideoAtWill));
                    }
                }
                if(bundle.containsKey(DVR_TakePhotosAtWill)){
                    Log.d(TAG, "notifyChange: DVR_TakePhotosAtWill");
                    if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE) {
                        mPlaybackControl.notifyChange(DVR_TakePhotosAtWill, bundle.getInt(Constant.DVR_TakePhotosAtWill));
                    }
                }

                //上一页
                if (bundle.containsKey(DVR_PageUpCbFunc)) {
                    Log.d(TAG, "notifyChange: DVR_PageUpCbFunc");
                    if (bundle.getInt(DVR_PageUpCbFunc) == 1) {
                        if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE ){
                            mPlaybackControl.notifyChange(DVR_PageUpCbFunc,bundle.getInt(DVR_St_CurrentPage));
                        }else  if (mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.notifyChange(DVR_PageUpCbFunc, bundle.getInt(DVR_St_CurrentPage));
                        }else{
                            mEditControl.notifyChange(DVR_PageUpCbFunc, bundle.getInt(DVR_St_CurrentPage));
                        }
                    } else {
                        Log.d(TAG, "notifyChange: DVR_St_CurrentPage fault");
                    }
                }
                //下一页
                if (bundle.containsKey(DVR_PageDownCbFunc)) {
                    Log.d(TAG, "notifyChange: DVR_PageDownCbFunc");
                    if (bundle.getInt(DVR_PageDownCbFunc) == 1) {
                        if (mDvrStatusInfo.getDisplayMode() == PLAYBACK_MODE){
                            mPlaybackControl.notifyChange(DVR_PageDownCbFunc,bundle.getInt(DVR_PageDownCbFunc));
                        }else
                        if (mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.notifyChange(DVR_PageDownCbFunc, bundle.getInt(DVR_St_CurrentPage));
                        }else {
                            mEditControl.notifyChange(DVR_PageDownCbFunc, bundle.getInt(DVR_St_CurrentPage));
                        }
                    } else {
                        Log.d(TAG, "notifyChange: DVR_PageDownCbFunc fault");
                    }
                }
                //删除当前文件
                if (bundle.containsKey(DVR_DeleteCurDocument)) {
                    Log.d(TAG, "notifyChange: DVR_DeleteCurDocument ");
                    if (bundle.getInt(DVR_DeleteCurDocument) == 1) {
                        if(DvrStatusInfo.getInstance().getmReplayStatus()){
                            mReplayControl.notifyChange(DVR_DeleteCurDocument, bundle.getInt(DVR_DeleteCurDocument));
                        }else if(DvrStatusInfo.getInstance().ismFreeshotplayStatus()){
                            mFreeshotPlayControl.notifyChange(DVR_DeleteCurDocument, bundle.getInt(DVR_DeleteCurDocument));
                        }else if (mDvrStatusInfo.ismEditWillStatus()){
                            mEditWillControl.mDeleteWillDialog.dismissDialog();
                        }else  {
                            mEditControl.mDeleteDialog.dismissDialog();
                        }
                    } else {
                        Log.d(TAG, "notifyChange: DVR_DeleteCurDocument fault");
                    }
                }
                //删除选中文件
                if (bundle.containsKey(DVR_DeleteTheSelectedDocument)) {
                    Log.d(TAG, "notifyChange: DVR_DeleteTheSelectedDocument");

                    if (mDvrStatusInfo.ismEditWillStatus()){
                        mEditWillControl.notifyChange(DVR_DeleteTheSelectedDocument, bundle.getInt(DVR_DeleteTheSelectedDocument));
                    }else{
                        mEditControl.notifyChange(DVR_DeleteTheSelectedDocument, bundle.getInt(DVR_DeleteTheSelectedDocument));
                    }


                }
                //移动当前文件到紧急录像
                if (bundle.containsKey(DVR_MoveCurDocumentToNoneDeleteArea)) {
                    Log.d(TAG, "notifyChange: DVR_MoveCurDocumentToNoneDeleteArea");
                    mReplayControl.notifyChange(DVR_MoveCurDocumentToNoneDeleteArea, bundle.getInt(DVR_MoveCurDocumentToNoneDeleteArea));
                }
                //移动选中文件到紧急录像
                if (bundle.containsKey(DVR_MoveSelectedDocToDeleteArea)) {
                    Log.d(TAG, "notifyChange: DVR_MoveSelectedDocToDeleteArea");
                    if (bundle.getInt(DVR_MoveSelectedDocToDeleteArea) == 1) {
                        mEditControl.notifyChange(DVR_MoveSelectedDocToDeleteArea,bundle.getInt(DVR_MoveSelectedDocToDeleteArea));
                    } else {
                        Log.d(TAG, "notifyChange: DVR_MoveSelectedDocToDeleteArea fault");
                    }
                }
                //移动当前文件到普通录像
                if (bundle.containsKey(DVR_MoveCurDocuToTheNorArea)) {
                    Log.d(TAG, "notifyChange: DVR_MoveCurDocuToTheNorArea");

                }
                //移动选中文件到普通录像
                if (bundle.containsKey(DVR_MoveSelectedDocToNorArea)) {
                    Log.d(TAG, "notifyChange: DVR_MoveSelectedDocToNorArea ");
                    if (bundle.getInt(DVR_MoveSelectedDocToNorArea) == 1) {
                        mEditControl.notifyChange(DVR_MoveSelectedDocToNorArea,bundle.getInt(DVR_MoveSelectedDocToNorArea));
                    } else {
                        Log.d(TAG, "notifyChange: DVR_MoveSelectedDocToNorArea fault");
                    }
                }
                // 播放视频暂停
                if (bundle.containsKey(DVR_CB_KEY_ST_PLAYVIDEO)){
                    Log.d(TAG, "notifyChange: DVR_CB_KEY_ST_PLAYVIDEO " +bundle.getInt(DVR_CB_KEY_ST_PLAYVIDEO));
                    if (DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                        mFreeshotPlayControl.notifyChange(DVR_CB_KEY_ST_PLAYVIDEO,bundle.getInt(DVR_CB_KEY_ST_PLAYVIDEO));
                    }else{
                        mReplayControl.notifyChange(DVR_CB_KEY_ST_PLAYVIDEO, bundle.getInt(DVR_CB_KEY_ST_PLAYVIDEO));
                    }
                }
                // 格式化回调
                if (bundle.containsKey(DVR_SdcardFormat)){
                    Log.d(TAG, "notifyChange: DVR_SdcardFormat " +bundle.getInt(DVR_SdcardFormat));
                    mSettingControl.notifyChange(DVR_SdcardFormat,bundle.getInt(DVR_SdcardFormat));
                }
                //恢复出厂设置回调
                if (bundle.containsKey(DVR_ReStoreFactory)){
                    Log.d(TAG, "notifyChange: DVR_ReStoreFactory " +bundle.getInt(DVR_ReStoreFactory));
                    mSettingControl.notifyChange(DVR_ReStoreFactory,bundle.getInt(DVR_ReStoreFactory));
                }
                // 退出
                if (bundle.containsKey(DVR_Quit)){
                    Log.d(TAG, "notifyChange: DVR_Quit " +bundle.getInt(DVR_Quit));
                    if (mDvrStatusInfo.ismSpeedError()){
                        mNoticeControl.notifyChange(DVR_Quit,bundle.getInt(DVR_Quit));
                    }else
                    if (DvrStatusInfo.getInstance().ismAtWillTypeStatus()){
                        mFreeshotPlayControl.notifyChange(DVR_Quit,bundle.getInt(DVR_Quit));
                    }else{
                        mReplayControl.notifyChange(DVR_Quit, bundle.getInt(DVR_Quit));
                    }
                }

                if (bundle.containsKey(Resolution)){
                    Log.d(TAG, "notifyChange11111: Resolution " +bundle.getInt(Resolution));
                        mSettingControl.notifyChange(Resolution,bundle.getInt(Resolution));

                }
                if (bundle.containsKey(RecordTime)){
                    Log.d(TAG, "notifyChange11111: RecordTime " +bundle.getInt(RecordTime));
                    mSettingControl.notifyChange(RecordTime,bundle.getInt(RecordTime));
                }
                if (bundle.containsKey(DriveMode)){
                    Log.d(TAG, "notifyChange11111: DriveMode " +bundle.getInt(DriveMode));
                    mSettingControl.notifyChange(DriveMode,bundle.getInt(DriveMode));
                }
                if (bundle.containsKey(ParkMode)){
                    Log.d(TAG, "notifyChange11111: ParkMode " +bundle.getInt(ParkMode));
                    mSettingControl.notifyChange(ParkMode,bundle.getInt(ParkMode));
                }

            }
            notifyDvrInfo();

        }
    };


    private void sdCardInfoUpdate() {
        DvrStatusInfo.getInstance().setVideoSize(-1);
        //查询总大小
        mDvrController.getDvrSdcApacitype();
        //依次查询
        mDvrController.queryDvrArea(DVR_FUNC_VALUE_QUERYDVRAREA_PHOTOAREA);
    }

    private void notifyDvrInfo() {
        //设置DVRStatusInfo里的值到setting画面
        mSettingControl.sdCardInfoUpdate();
    }

    private void initListener() {
        Log.d(TAG, "initListener: start");

        mRecordControl.initListener();
        mPlaybackControl.initListener();
        mEditControl.initListener();
        mReplayControl.initListener();
        mSettingControl.initListener();
        mFreeshotPlayControl.initListener();
        mEditWillControl.initListener();
        mNoticeControl.initListener();
        mCameraAuthentication.registerCameraListener(mCameraAuthenticationCallBack);
        mDvrController.registerCallback(mDvrCallback);
        ShareDataManager.getShareDataManager().registerShareDataListener(Constant.SHARER_ID_GENERAL ,listener);
        ShareDataManager.getShareDataManager().registerShareDataListener(Constant.SHARER_AVM_RVC_SOURCE ,rvclistener);
        mSystemProperties =  SystemPropertiesPresenter.getInstance();
        Log.d(TAG, "initListener: end");
    }

    public class ShareInfoListener implements IShareDataListener {

        @Override
        public void notifyShareData(int dataType, String data) {
            Log.d(TAG, "notifyShareData: dataType: " + dataType + ", data: " + data);
            handleDrivingWarning(dataType,data);
        }
    }
    public class SharervcInfoListener implements IShareDataListener{
        @Override
        public void notifyShareData(int i, String s) {
            Log.d(TAG, "notifyShareData: dataType: " + i + ", data: " + s);
            handleRvcAvmStart(s);
        }
    }
    private static final String  enterAvm = "enterAvm";
    private void handleRvcAvmStart(final String s) {
        if (!mNasFocus){
            return;
        }
        if (s == null)
        {
            return;
        }

        JSONObject obj = null;
        try
        {
            obj = new JSONObject(s);
            if (obj != null && obj.has(enterAvm))
            {
                boolean status = obj.getBoolean(enterAvm);
                Log.d(TAG, "handleRvcAvmStart: status" +status);
              if (!status ){
                  Log.d(TAG, "handleRvcAvmStart: Enter dvr了");
                  Settings.Global.putInt(getApplicationContext().getContentResolver(),"dvrShow",1);
                  mMainUiHandler.sendEmptyMessageDelayed(AGAIN_CAMERA,300);
                  if (errorFlag){
                      int a = EvsCameraMng.getDeviceValue(ADAYO_AVM_CAMERA_SIGNAL_STATE);
                      if (a == 0){
                          clCameraError.setVisibility(View.VISIBLE);
                      }
                  }
              }else{
                  Log.d(TAG, "handleRvcAvmStart: Enter avm");
                  mMainUiHandler.removeMessages(AGAIN_CAMERA);
//                  moveTaskToBack(true);
                  if (!errorFlag){
                      colseDvrView.setVisibility(View.VISIBLE);
                  }
              }
            }
        }catch (JSONException e) {
            Log.d(TAG, "handleRvcAvmStart: fail  " + e);
        }


    }
    private  boolean  onTouchListener(View view){
        return false;
    };

    /**
     * 关闭所有 弹窗
     * */

    public  void closeDialog(){
        if (mEditControl.mDeleteDialog != null) {
            mEditControl.mDeleteDialog.dismissDialog();
        }
        if (mEditControl.mMoveDialog != null) {
            mEditControl.mMoveDialog.dismissDialog();
        }
        if (mEditWillControl.mDeleteWillDialog != null) {
            mEditWillControl.mDeleteWillDialog.dismissDialog();
        }
        if (mEditWillControl.mUploadDialog != null) {
            mEditWillControl.mUploadDialog.dismissDialog();
        }
        if (mSettingControl.mFormatCustomDialog != null) {
            mSettingControl.mFormatCustomDialog.dismissDialog();
        }
        if (mSettingControl.mRestCustomDialog != null) {
            mSettingControl.mRestCustomDialog.dismissDialog();
        }
    }


    /**
     * 切换页
     */
    private static final  int sumPage = 8;
    public void changePage(int page) {
        Log.d(TAG, "changePage: page = " + page);
        if (0 <= page && page <= sumPage) {
            mDvrStatusInfo.setCurrentPageIndex(page);
        }
        switch (page) {
            case PAGE_RECORD:
                clRecord.setVisibility(View.VISIBLE);
                clPlayback.setVisibility(View.GONE);
                clEdit.setVisibility(View.GONE);
                clReplay.setVisibility(View.GONE);
                clSetting.setVisibility(View.GONE);
                clNotice.setVisibility(View.GONE);
                clAtWillTot.setVisibility(View.GONE);
                clEditWill.setVisibility(View.GONE);
                break;
            case PAGE_PLAYBACK:
                //回放页面5分钟倒计时-开始倒计时
                if (mPlaybackControl.timer != null) {
                    mPlaybackControl.timer.cancel();
                    mPlaybackControl.timer.start();
                }
                clRecord.setVisibility(View.GONE);
                clPlayback.setVisibility(View.VISIBLE);
                clEdit.setVisibility(View.GONE);
                clReplay.setVisibility(View.GONE);
                clSetting.setVisibility(View.GONE);
                clNotice.setVisibility(View.GONE);
                clAtWillTot.setVisibility(View.GONE);
                clEditWill.setVisibility(View.GONE);
                break;
            case PAGE_EDIT:
                //更新编辑页面的条目
                mEditControl.updateEdit();
                //更新普通录像和紧急录像 编辑页面的移动图标
                mEditControl.updateMoveTab();


                clRecord.setVisibility(View.GONE);
                clPlayback.setVisibility(View.GONE);
                clEdit.setVisibility(View.VISIBLE);
                clReplay.setVisibility(View.GONE);
                clSetting.setVisibility(View.GONE);
                clNotice.setVisibility(View.GONE);
                clAtWillTot.setVisibility(View.GONE);
                clEditWill.setVisibility(View.GONE);
                break;
            case PAGE_EDIT_WILL:
                //更新编辑页面的条目
                mEditWillControl.updateEditAtWill();

                clRecord.setVisibility(View.GONE);
                clPlayback.setVisibility(View.GONE);
                clEdit.setVisibility(View.GONE);
                clReplay.setVisibility(View.GONE);
                clSetting.setVisibility(View.GONE);
                clNotice.setVisibility(View.GONE);
                clAtWillTot.setVisibility(View.GONE);
                clEditWill.setVisibility(View.VISIBLE);
                break;
            case PAGE_REPLAY:
                clRecord.setVisibility(View.GONE);
                clPlayback.setVisibility(View.GONE);
                clEdit.setVisibility(View.GONE);
                clReplay.setVisibility(View.VISIBLE);
                clSetting.setVisibility(View.GONE);
                clNotice.setVisibility(View.GONE);
                clAtWillTot.setVisibility(View.GONE);
                clEditWill.setVisibility(View.GONE);
                break;
            case PAGE_SETTING:
                clRecord.setVisibility(View.GONE);
                clPlayback.setVisibility(View.GONE);
                clEdit.setVisibility(View.GONE);
                clReplay.setVisibility(View.GONE);
                clSetting.setVisibility(View.VISIBLE);
                clNotice.setVisibility(View.GONE);
                clAtWillTot.setVisibility(View.GONE);
                clEditWill.setVisibility(View.GONE);
                break;
            case PAGE_NOTICE:
                clRecord.setVisibility(View.GONE);
                clPlayback.setVisibility(View.GONE);
                clEdit.setVisibility(View.GONE);
                clReplay.setVisibility(View.GONE);
                clSetting.setVisibility(View.GONE);
                clNotice.setVisibility(View.VISIBLE);
                clAtWillTot.setVisibility(View.GONE);
                clEditWill.setVisibility(View.GONE);
                break;
            case PAGE_PLAPWILL:
                clRecord.setVisibility(View.GONE);
                clPlayback.setVisibility(View.GONE);
                clEdit.setVisibility(View.GONE);
                clReplay.setVisibility(View.GONE);
                clSetting.setVisibility(View.GONE);
                clNotice.setVisibility(View.GONE);
                clAtWillTot.setVisibility(View.VISIBLE);
                clEditWill.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        layoutNotConnect.setVisibility(View.GONE);
        if (!errorFlag){
            clCameraError.setVisibility(View.GONE);
        }
    }

    /**
     * 关闭DVR
     */
    public void disconnectDvr() {
        Log.d(TAG, "disconnectDvr: start");
        layoutNotConnect.setVisibility(View.VISIBLE);
        TextView btnOpen = findViewById(R.id.btn_turn_on);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DvrController.getInstance().powerOn();
            }
        });


    }

    private void handleDrivingWarning(int dataType, String content){
        if (!showFlag){
            return;
        }
        if (dataType != Constant.SHARER_ID_GENERAL){
            return;
        }
        if (content == null) {
            Log.w(TAG, "initListener: deviceService not yet shared");
            return;
        }
        Map<String, Object> map =  JSON.parseObject(content);
        boolean isEnableDrivingWarning;
        Object o = map.get(Constant.SHARER_KEY_DRIVING_WARNING_SWITCH_OF_GENERAL);
        if (o == null) {
            Log.w(TAG,"deviceService not yet shared, key: " + Constant.SHARER_KEY_DRIVING_WARNING_SWITCH_OF_GENERAL);
            return;
        }
        isEnableDrivingWarning = (boolean) o;
        boolean isOverSpeed;
        Object o1 = map.get(Constant.SHARER_KEY_OVER_SPEED_OF_GENERAL);
        if (o1 == null) {
            Log.w(TAG,"deviceService not yet shared, key: " + Constant.SHARER_KEY_OVER_SPEED_OF_GENERAL);
            return;
        }
        isOverSpeed = (boolean) o1;
        if (isEnableDrivingWarning && isOverSpeed) {
            if (mDrivingWarningFlag != null && !mDrivingWarningFlag) {
                mDrivingWarningFlag = null;
            }
            if (mDrivingWarningFlag != null) {
                return;
            }
            mDrivingWarningFlag = true;
            //doSomething
            if(mDvrStatusInfo.ismPlayStatus()){
                if (DvrStatusInfo.getInstance().ismWillPlaystatus()){
                    mFreeshotPlayControl.playOrPause(true);
                }else if (DvrStatusInfo.getInstance().ismPlayBackStatus()){
                    mReplayControl.playOrPause(true);
                }
                mNoticeControl.openNotice();
            }
        } else {
            if (mDrivingWarningFlag != null && mDrivingWarningFlag) {
                mDrivingWarningFlag = null;
            }
            if (mDrivingWarningFlag != null) {
                return;
            }
            //doSomething
            mDrivingWarningFlag = false;
            if(mDvrStatusInfo.ismPlayStatus()){
                int closeType = 0;
                if (DvrStatusInfo.getInstance().ismWillPlaystatus()){
                    closeType =1;
                    mFreeshotPlayControl.playOrPause(false);
                }else if (DvrStatusInfo.getInstance().ismPlayBackStatus()){
                    closeType =2;
                    mReplayControl.playOrPause(false);
                }
                mNoticeControl.closeNotice(closeType);
            }

        }
    }
    private void getDrivingWarning(String content){
        if (content == null) {
            Log.w(TAG, "initListener: deviceService not yet shared");
            return;
        }
        Map<String, Object> map =  JSON.parseObject(content);
        boolean isEnableDrivingWarning;
        Object o = map.get(Constant.SHARER_KEY_DRIVING_WARNING_SWITCH_OF_GENERAL);
        if (o == null) {
            Log.w(TAG,"deviceService not yet shared, key: " + Constant.SHARER_KEY_DRIVING_WARNING_SWITCH_OF_GENERAL);
            return;
        }
        isEnableDrivingWarning = (boolean) o;
        boolean isOverSpeed;
        Object o1 = map.get(Constant.SHARER_KEY_OVER_SPEED_OF_GENERAL);
        if (o1 == null) {
            Log.w(TAG,"deviceService not yet shared, key: " + Constant.SHARER_KEY_OVER_SPEED_OF_GENERAL);
            return;
        }
        isOverSpeed = (boolean) o1;
        if (isEnableDrivingWarning && isOverSpeed) {
            //doSomething
            Log.d(TAG, "getDrivingWarning: DVR_Play +++++++++");
            if(mDvrStatusInfo.ismPlayStatus()){
                if (DvrStatusInfo.getInstance().ismWillPlaystatus()){
                    mFreeshotPlayControl.playOrPause(true);
                }else if (DvrStatusInfo.getInstance().ismPlayBackStatus()){
                    mReplayControl.playOrPause(true);
                }
                Log.d(TAG, "getDrivingWarning: DVR_Play ++++++++2222+");
                mNoticeControl.openNotice();
            }
        }else{
            if(mDvrStatusInfo.ismPlayStatus()){
                int closeType = 0;
                if (DvrStatusInfo.getInstance().ismWillPlaystatus()){
                    closeType =1;
                    mFreeshotPlayControl.playOrPause(false);
                }else if (DvrStatusInfo.getInstance().ismPlayBackStatus()){
                    closeType =2;
                    mReplayControl.playOrPause(false);
                }
                mNoticeControl.closeNotice(closeType);
            }
        }
    }

    private CameraAuthentication.ICameraAuthenticationListener mCameraAuthenticationCallBack
            = new CameraAuthentication.ICameraAuthenticationListener() {
        @Override
        public void closeCameraCallback() {
            Log.d(TAG, "closeCameraCallback start");
            closeCamera();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mCameraAuthentication.setCloseCameraOver();
            Log.d(TAG, "closeCameraCallback end");
        }

        @Override
        public void otherReleaseCamera() {
            Log.d(TAG, "otherReleaseCamera start");
            registerCameraListener();
            Log.d(TAG, "otherReleaseCamera end");
        }
    };

    /**
     * 打开摄像头
     */
    private boolean openCamera() {
        boolean textureViewAvailable = mTextureView.isAvailable();
        if (textureViewAvailable) {
            Log.d(TAG, "openCamera textureViewAvailable = " + textureViewAvailable);
            Log.d(TAG, "openCamera width = " + mTextureView.getWidth() + "height = " + mTextureView.getHeight());
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    Log.e(TAG, "SurfaceTextureListener onSurfaceTextureAvailable width = "
                            + width + ";height = " + height);
                    openCamera(width, height);

                    Log.d(TAG, "SurfaceTextureListener end");
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                    Log.d(TAG, "SurfaceTextureListener onSurfaceTextureSizeChanged width = "
                            + width + ";height = " + height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    Log.d(TAG, "SurfaceTextureListener onSurfaceTextureDestroyed");
                    closeCamera();
                    mCameraAuthentication.setCloseCameraOver();
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            });
        }
        return true;
    }

    /**
     * 开启摄像头
     */
    private boolean openCamera(final int width, final int height) {
        Log.d(TAG, "openCamera begin");
        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if (texture == null) {
            return false;
        }
        // 将默认缓冲区的大小配置为我们想要的相机预览的大小。 设置分辨率
        texture.setDefaultBufferSize(mTextureView.getWidth(), mTextureView.getHeight());

        mWidth = width;
        mHeight = height;
        mSurface = new Surface(texture);
        mOpenCamera = true;
        mOpenCameraFinish = false;
        operateCameraThread();

        Log.d(TAG, "openCamera end");
        return true;
    }

    /**
     * 关闭摄像头
     */

    private boolean closeCamera() {
        if (!mOpenCamera) {
            return true;
        }
        Log.d(TAG, "closeCamera begin");
        boolean bRet = true;
        long startTimer = System.currentTimeMillis();
        long endTimer = startTimer;
        mSurface.release();
        mCloseCamera = true;
        while (mCloseCamera) {
            //wait for camera close ok
            endTimer = System.currentTimeMillis();
            if (Math.abs(endTimer - startTimer) > 2000) {
                mCloseCamera = false;
                mOpenCamera = false;
                bRet = false;
            }
        }
        mOpenCameraFinish = false;
        Log.d(TAG, "closeCamera end" + abs(endTimer - startTimer));
        return bRet;
    }

    /**
     * 打开摄像头线程
     */

    private void operateCameraThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "EvsCameraMng openCamera start");
                boolean isOpen = true;
                EvsCameraMng.openCamera(mCamera);
                EvsCameraMng.setSurface(mSurface, mWidth, mHeight);
                Log.d(TAG, "EvsCameraMng openCamera end");
                mOpenCameraFinish = true;
                while (isOpen) {
                    if (mCloseCamera) {
                        Log.d(TAG, "EvsCameraMng closeCamera start");
                        EvsCameraMng.closeCamera(mCamera);
//                        mSurface.release();
                        mCloseCamera = false;
                        mOpenCamera = false;
                        mOpenCameraFinish = false;
                        isOpen = false;

                        Log.d(TAG, "EvsCameraMng closeCamera end");
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void registerCameraListener() {
        if (mCameraAuthentication.reqCameraAuthentication()) {
//            if (EvsCameraMng.connectEvsService()) {
//                Log.d(TAG, "connectEvsService1111 success");
//            } else {
//                Log.d(TAG, "connectEvsService1111 fail");
//            }
            openCamera();
        } else {
            Log.d(TAG, "reqCameraAuthentication1111 fail");
        }
    }

    private void unregisterCameraListener() {
        mCameraAuthentication.unregisterCameraListener();
    }


    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        overridePendingTransition(0,0);
        Settings.Global.putInt(getApplicationContext().getContentResolver(),"dvrShow",0);
        mCameraAuthentication.setCloseCameraOver();
        unregisterCameraListener();

        if (mPlaybackControl.timer != null) {
            mPlaybackControl.timer.cancel();


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        closeCamera();
        mDvrController.destroy();
        EvsCameraMng.unRegistEvsServiceCallback(mEvsServiceCallBack,PACKAGE_NAME);
        ShareDataManager.getShareDataManager().unregisterShareDataListener(Constant.SHARER_ID_GENERAL,listener);
        ShareDataManager.getShareDataManager().unregisterShareDataListener(Constant.SHARER_AVM_RVC_SOURCE,rvclistener);


    }



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSettingControl.notifyChange(ON_CONFIGURATION_CHANGED,1);
    }
}