package com.adayo.app.dvr.entity;

import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

public class DvrStatusInfo {

    private static final String TAG = APP_TAG + DvrStatusInfo.class.getSimpleName();
    private static DvrStatusInfo mDvrStatusInfo;

    //各个类型文件存储空间
    //照片占用空间
    private double mPhotoSize;
    //普通录像占用空间
    private double mVideoSize;
    //紧急录像占用空间
    private double mEmergencyVideoSize;
    //随心拍占用空间
    private double mAtWillSize;
    //SD卡已用空间大小
    private double mAllSize;
    //SD卡空间总大小
    private String mAllDiskSize = "0";
    //当播放模式
    private boolean mReplayStatus ;
    //
    private boolean mFreeshotplayStatus  ;

    //当前下发按键名
    private String mCurrentOperaName;
    //按键下发结果
    private int mOperaResult;
    //回放模式当前是图片还是视频类型
    private String mType = "type_video";
    //当前是紧急录像还是普通录像 默认普通录像
    private String mMode = "normal";


    //随心拍模式当前是图片还是视频
    private String mAtWillType = "";

    //随心拍模式
    private boolean mAtWillTypeStatus = false;


    //紧急录像状态
    private boolean mIsEmergencyRecording;
    //编辑模式状态
    private boolean mIsEditMode;
    //有无文件状态
    private boolean mIsNoFile;

    //SD卡是否有插入状态
    private int mSdStatus = 2;

    //当前页位置
    private int mCurrentPageIndex;
    //当前显示模式
    private int mDisplayMode;
    //当前选择的文件
    private int mCurrentThumbnail;

    //当前麦克风状态
    private int mMicStatus;

    // 当前是不是error页面
    private boolean mError = false;

    // 当前是 回放编辑页面
    private boolean mEditStatus = false;

    // 当前是 随心拍编辑页面
    private boolean mEditWillStatus = false;
    // 当前是 页数
    private int currentPage = 0;
    // 当前是 页数
    private int totalPage = 0;



    //系统版本
    private String mSystemVersion;


    //当前录制时长
    private int mRecordingTime;
    //当前停车监控灵敏度状态
    private int mParkingMonitoringSensitivity;
    //当前行车监控灵敏度状态
    private int mDrivingMonitoringSensitivity;
    //当前分辨率状态
    private int mResolutionStatus;

    //当前播放文件类型
    private String mCurrentPlayType;

    //当前播放模式
    private boolean mPlayStatus = false;

    //当前是否是点击状态
    private boolean mClickStatus = false;

    //随心拍播放
    private boolean mPlayBackStatus = false;
    //回放播放
    private boolean mWillPlaystatus = false;

    //行车警告
    private boolean mSpeedError =  false;

    //刷新编辑九宫格
    private boolean mRefreshStatus = false;

    private boolean mSettingStatus = false;

    private boolean mCloseStaus = false;

    private int recordPages ;
    private int recordNum;

    public String getCurrentPlayType() {
        return mCurrentPlayType;
    }

    public void setCurrentPlayType(String currentPlayType) {
        mCurrentPlayType = currentPlayType;
    }

    public static DvrStatusInfo getInstance() {
        if (mDvrStatusInfo == null) {
            synchronized (DvrStatusInfo.class) {
                if (mDvrStatusInfo == null) {
                    mDvrStatusInfo = new DvrStatusInfo();
                }
            }
        }
        return mDvrStatusInfo;
    }

    public double getPhotoSize() {
        return mPhotoSize;
    }

    public void setPhotoSize(double photoSize) {
        mPhotoSize = photoSize;
    }

    public double getVideoSize() {
        return mVideoSize;
    }

    public void setVideoSize(double videoSize) {
        mVideoSize = videoSize;
    }

    public double getEmergencyVideoSize() {
        return mEmergencyVideoSize;
    }

    public void setEmergencyVideoSize(double emergencyVideoSize) {
        mEmergencyVideoSize = emergencyVideoSize;
    }

    public double getAtWillSize() {
        return mAtWillSize;
    }

    public void setAtWillSize(double atWillSize) {
        mAtWillSize = atWillSize;
    }

    public double getAllSize() {
        return mAllSize;
    }

    public void setAllSize(double allSize) {
        mAllSize = allSize;
    }

    public String getAllDiskSize() {
        return mAllDiskSize;
    }

    public void setAllDiskSize(String allDiskSize) {
        mAllDiskSize = allDiskSize;
    }

    public String getCurrentOperaName() {
        return mCurrentOperaName;
    }

    public void setCurrentOperaName(String currentOperaName) {
        mCurrentOperaName = currentOperaName;
    }

    public int getOperaResult() {
        return mOperaResult;
    }

    public void setOperaResult(int operaResult) {
        mOperaResult = operaResult;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public boolean isEmergencyRecording() {
        return mIsEmergencyRecording;
    }

    public void setIsEmergencyRecording(boolean isEmergencyRecording) {
        this.mIsEmergencyRecording = isEmergencyRecording;
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public void setIsEditMode(boolean isEditMode) {
        mIsEditMode = isEditMode;
    }

    public boolean isNoFile() {
        return mIsNoFile;
    }

    public void setIsNoFile(boolean isNoFile) {
        mIsNoFile = isNoFile;
    }

    public int getSdStatus() {
        return mSdStatus;
    }

    public void setSdStatus(int sdStatus) {
        mSdStatus = sdStatus;
    }

    public int getCurrentPageIndex() {
        return mCurrentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        mCurrentPageIndex = currentPageIndex;
    }

    public int getDisplayMode() {
        return mDisplayMode;
    }

    public void setDisplayMode(int displayMode) {
        mDisplayMode = displayMode;
    }

    public int getCurrentThumbnail() {
        return mCurrentThumbnail;
    }

    public void setCurrentThumbnail(int currentThumbnail) {
        mCurrentThumbnail = currentThumbnail;
    }

    public int getMicStatus() {
        return mMicStatus;
    }

    public void setMicStatus(int micStatus) {
        mMicStatus = micStatus;
    }

    public int getParkingMonitoringSensitivity() {
        return mParkingMonitoringSensitivity;
    }

    public void setParkingMonitoringSensitivity(int parkingMonitoringSensitivity) {
        mParkingMonitoringSensitivity = parkingMonitoringSensitivity;
    }

    public int getDrivingMonitoringSensitivity() {
        return mDrivingMonitoringSensitivity;
    }

    public void setDrivingMonitoringSensitivity(int drivingMonitoringSensitivity) {
        mDrivingMonitoringSensitivity = drivingMonitoringSensitivity;
    }

    public int getResolutionStatus() {
        return mResolutionStatus;
    }

    public void setResolutionStatus(int resolutionStatus) {
        mResolutionStatus = resolutionStatus;
    }

    public String getMode() {
        return mMode;
    }

    public void setMode(String mMode) {
        this.mMode = mMode;
    }

    public int getRecordingTime() {
        return mRecordingTime;
    }

    public void setRecordingTime(int mRecordingTime) {
        this.mRecordingTime = mRecordingTime;
    }

    public String getAtWillType() {
        return mAtWillType;
    }

    public void setAtWillType(String mAtWillType) {
        this.mAtWillType = mAtWillType;
    }

    public String getSystemVersion() {
        return mSystemVersion;
    }

    public void setSystemVersion(String mSystemVersion) {
        this.mSystemVersion = mSystemVersion;
    }

    public boolean getmReplayStatus() {
        return mReplayStatus;
    }

    public void setmReplayStatus(boolean mReplayStatus) {
        this.mReplayStatus = mReplayStatus;
    }

    public boolean getError() {
        return mError;
    }

    public void setmError(boolean mError) {
        this.mError = mError;
    }

    public boolean getmEditStatus() {
        return mEditStatus;
    }

    public void setmEditStatus(boolean mEditStatus) {
        this.mEditStatus = mEditStatus;
    }

    public boolean ismAtWillTypeStatus() {
        return mAtWillTypeStatus;
    }

    public void setmAtWillTypeStatus(boolean mAtWillTypeStatus) {
        this.mAtWillTypeStatus = mAtWillTypeStatus;
    }

    public boolean ismFreeshotplayStatus() {
        return mFreeshotplayStatus;
    }

    public void setmFreeshotplayStatus(boolean mFreeshotplayStatus) {
        this.mFreeshotplayStatus = mFreeshotplayStatus;
    }

    public boolean ismEditWillStatus() {
        return mEditWillStatus;
    }

    public void setmEditWillStatus(boolean mEditWillStatus) {
        this.mEditWillStatus = mEditWillStatus;
    }

    public boolean ismPlayStatus() {
        return mPlayStatus;
    }

    public void setmPlayStatus(boolean mPlayStatus) {
        this.mPlayStatus = mPlayStatus;
    }

    public boolean ismClickStatus() {
        return mClickStatus;
    }

    public void setmClickStatus(boolean mClickStatus) {
        this.mClickStatus = mClickStatus;
    }

    public boolean ismPlayBackStatus() {
        return mPlayBackStatus;
    }

    public void setmPlayBackStatus(boolean mPlayBackStatus) {
        this.mPlayBackStatus = mPlayBackStatus;
    }

    public boolean ismWillPlaystatus() {
        return mWillPlaystatus;
    }

    public void setmWillPlaystatus(boolean mWillPlaystatus) {
        this.mWillPlaystatus = mWillPlaystatus;
    }

    public boolean ismSpeedError() {
        return mSpeedError;
    }

    public void setmSpeedError(boolean mSpeedError) {
        this.mSpeedError = mSpeedError;
    }

    public boolean ismRefreshStatus() {
        return mRefreshStatus;
    }

    public void setmRefreshStatus(boolean mRefreshStatus) {
        this.mRefreshStatus = mRefreshStatus;
    }

    public boolean ismSettingStatus() {
        return mSettingStatus;
    }

    public void setmSettingStatus(boolean mSettingStatus) {
        this.mSettingStatus = mSettingStatus;
    }

    public boolean ismCloseStaus() {
        return mCloseStaus;
    }

    public void setmCloseStaus(boolean mCloseStaus) {
        this.mCloseStaus = mCloseStaus;
    }

    public int getRecordPages() {
        return recordPages;
    }

    public void setRecordPages(int recordPages) {
        this.recordPages = recordPages;
    }

    public int getRecordNum() {
        return recordNum;
    }

    public void setRecordNum(int recordNum) {
        this.recordNum = recordNum;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
