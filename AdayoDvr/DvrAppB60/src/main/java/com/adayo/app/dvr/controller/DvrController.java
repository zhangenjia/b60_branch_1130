package com.adayo.app.dvr.controller;

import static com.adayo.app.dvr.constant.Constant.PACKAGE_NAME;
import static com.adayo.app.dvr.constant.TagConstant.APP_TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.proxy.adas.evs.EvsCameraMng;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.deviceservice.IDeviceFuncCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DvrController {

    private static final String TAG = APP_TAG + DvrController.class.getSimpleName();
    private static volatile DvrController mController = null;
    private String mCurrentOperaName;
    private AAOP_DeviceServiceManager mDvrManager;
    public  Bundle setbundle = new Bundle();

    private IDeviceFuncCallBack mDvrNotify = new IDeviceFuncCallBack.Stub() {
        @Override
        public int onChangeListener(Bundle bundle) throws RemoteException {
            Log.d(TAG, "onChangeListener: " + mDvrCallback + "bundle=" + bundle);
            if (mDvrCallback != null) {
                mDvrCallback.notifyChange(bundle);
            } else {
                Log.d(TAG, "onChangeListener: mDvrCallback == null");
            }
            return 0;
        }
    };

    private static final int RETRY_INIT_DVR_WHAT = 1;
    private int mRetryCount = 2;
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: msg.what = " + msg.what);
            switch (msg.what) {
                case RETRY_INIT_DVR_WHAT:
                    initDvrManger();
                    break;
                default:
                    break;
            }
        }
    };

    private void initDvrManger() {
        Log.d(TAG, "initDvrManger: ");
        if (mDvrManager == null) {
            Log.d(TAG, "initDvrManger: mDvrManger == null");
            mDvrManager = AAOP_DeviceServiceManager.getInstance();
        }
        if (mDvrManager != null) {
            if (mDvrManager.getServiceConnection()) {
                Log.d(TAG, "initDvrManger: registerCallback");
              int aa =    mDvrManager.registerDeviceFuncListener(mDvrNotify, PACKAGE_NAME, "DvrDevice");
                Log.d(TAG, "initDvrManger: aa" +aa);
            } else {
                if (mRetryCount > 0) {
                    mMainHandler.sendEmptyMessageDelayed(RETRY_INIT_DVR_WHAT, 100);
                    mRetryCount--;
                }
            }
        }
    }

    public static DvrController getInstance() {
        if (mController == null) {
            synchronized (DvrController.class) {
                if (mController == null) {
                    mController = new DvrController();
                }
            }
        }
        return mController;
    }

    public void init() {
        initDvrManger();
    }

    public interface DvrCallback {
        //        void notifyChange(String funcId, int value);
        void notifyChange(Bundle bundle);
    }

    private DvrCallback mDvrCallback;

    public void registerCallback(DvrCallback dvrCallback) {

        mDvrCallback = dvrCallback;
    }

    public void destroy() {
        mDvrManager.unRegisterDeviceFuncListener(mDvrNotify, "", PACKAGE_NAME);
    }

    /**
     * 初始化当前操作
     */
    public void initCurrentOperation() {
        Log.d(TAG, "Init : initCurrentOperation");
        mCurrentOperaName = "init";
    }

    /**
     * 获取当前操作名
     */
    public String getCurrentOperaName() {
        return mCurrentOperaName;
    }

    /**
     * 下一页
     */
    public void pageDown() {
        mCurrentOperaName = "pageDown";
        Log.d(TAG, "send cmd : pageDown");
        //todo 是否要加Dialog
        Log.d(TAG, "notifyChangeTime: 信号 start =" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        mDvrManager.pageDown();
    }

    /**
     * 上一页
     */
    public void pageUp() {
        mCurrentOperaName = "pageUp";
        Log.d(TAG, "send cmd : pageUp");
        mDvrManager.pageUp();
    }

    /**
     * 删除当前文件
     */
    public void deleteCurrentDoc() {
        mCurrentOperaName = "deleteCurrentDoc";
        Log.d(TAG, "send cmd : delCurDocument");
        mDvrManager.deleteCurrentDoc();
    }

    /**
     * 删除当前选中文件
     */
    public void deleteSelectDoc() {
        mCurrentOperaName = "deleteSelectDoc";
        Log.d(TAG, "send cmd : delSelDocument");
        mDvrManager.deleteSelectDoc();
    }

    /**
     * 移动普通目录内当前文件至紧急目录
     */
    public void moveCurrentToEmergency() {
        mCurrentOperaName = "moveCurrentToEmergency";
        Log.d(TAG, "send cmd : movCurDocument");
        mDvrManager.moveCurrentToEmergency();
    }

    /**
     * 移动普通目录内当前选中文件至紧急目录
     */
    public void moveSelectToEmergency() {
        mCurrentOperaName = "moveSelectToEmergency";
        Log.d(TAG, "send cmd : movSelDocument");
        mDvrManager.moveSelectToEmergency();
    }

    /**
     * 将当前紧急录像文件移至普通录像区
     */
    public void moveCurrentToNormal() {
        mCurrentOperaName = "moveCurrentToNormal";
        Log.d(TAG, "send cmd : moveCurrentToNormal");
        mDvrManager.moveCurrentToNormal();
    }

    /**
     * 将选择紧急录像文件移至普通录像区
     */
    public void moveSelectToNormal() {
        mCurrentOperaName = "moveSelectToNormal";
        Log.d(TAG, "send cmd : moveSelectToNormal");
        mDvrManager.moveSelectToNormal();
    }

    /**
     * 上一个视频或图片
     */
    public void prev() {
        mCurrentOperaName = "prev";
        Log.d(TAG, "send cmd : prev");
        mDvrManager.prev();
    }

    /**
     * 下一个视频或图片
     */
    public void next() {
        mCurrentOperaName = "next";
        Log.d(TAG, "send cmd : next");
        mDvrManager.next();
    }

    /**
     * 播放
     */
    public void play() {
        mCurrentOperaName = "play";
        Log.d(TAG, "send cmd : play");
        mDvrManager.play();
    }

    /**
     * 暂停
     */
    public void pause() {
        mCurrentOperaName = "pause";
        Log.d(TAG, "send cmd : pause");
        mDvrManager.pause();
    }

    /**
     * 退出
     */
    public void quit() {
        mCurrentOperaName = "quit";
        Log.d(TAG, "send cmd : quit");
        mDvrManager.quit();
    }

    /**
     * 快进
     */
    public void fastForward() {
        mCurrentOperaName = "fastForward";
        Log.d(TAG, "send cmd : fastForward");
        mDvrManager.fastForward();
    }

    /**
     * 快退
     */
    public void rewind() {
        mCurrentOperaName = "rewind";
        Log.d(TAG, "send cmd : rewind");
        mDvrManager.rewind();
    }

    /**
     * 循环播放
     */
    public void loop() {
        mCurrentOperaName = "loop";
        Log.d(TAG, "send cmd : loop");
        mDvrManager.loop();
    }

    /**
     * 开机
     */
    public void powerOn() {
        mCurrentOperaName = "powerOn";
        Log.d(TAG, "send cmd : powerOn");
        mDvrManager.powerOn();
    }

    /**
     * 关机
     */
    public void powerOff() {
        mCurrentOperaName = "powerOff";
        Log.d(TAG, "send cmd : powerOff");
        mDvrManager.powerOff();
    }

    /**
     * 执行拍照
     */
    public void photoGraph() {
        mCurrentOperaName = "photoGraph";
        Log.d(TAG, "send cmd : photoGraph");
        mDvrManager.photoGraph();
    }

    /**
     * 回放时进入照片列表
     */
    public void enterGraph() {
        mCurrentOperaName = "enterGraph";
        Log.d(TAG, "send cmd : enterGraph");
        mDvrManager.enterGraph();
    }

    /**
     * 回放时进入普通录像列表
     */


    /**
     * 执行随心拍拍照
     */
    public void photoAtWill() {
        mCurrentOperaName = "photoAtWillGraph";
        Log.d(TAG, "send cmd : photoAtWillGraph");
        mDvrManager.photoAtWill();
    }

    /**
     * 执行随心拍录像
     */
    public void videoAtWill() {
        mCurrentOperaName = "startAtWillVideo";
        Log.d(TAG, "send cmd : startAtWillVideo");
        mDvrManager.videoAtWill();
    }

    /**
     * 点击随心拍图片
     */
    public void freeshotPhotoOrVedio(int type) {
        if (type == 0) {
            mDvrManager.executeEditTakePhotoAtWill(0);
        } else {
            mDvrManager.executeEditTakePhotoAtWill(1);
        }
    }


    /**
     * 随心拍上传
     */
    public void uploadAtWill() {
        mCurrentOperaName = "deleteAtWill";
        Log.d(TAG, "send cmd : deleteAtWill");
        mDvrManager.uploadAtWill();
    }

    /**
     * 执行打点拍照
     */
    public void photoAtPointGraph() {
        mCurrentOperaName = "photoAtPointGraph";
        Log.d(TAG, "send cmd : photoAtPointGraph");
        mDvrManager.photoAtPointGraph();
    }

    /**
     * 执行打点视频开始
     */
    public void startAtPointVideo() {
        mCurrentOperaName = "startAtPointVideo";
        Log.d(TAG, "send cmd : startAtPointVideo");
        mDvrManager.startAtPointVideo();
    }

    /**
     * 取消打点视频开始
     */
    public void cancelAtPointVideo() {
        mCurrentOperaName = "cancelAtPointVideo";
        Log.d(TAG, "send cmd : cancelAtPointVideo");
        mDvrManager.cancelAtPointVideo();
    }

    /**
     * 结束打点视频开始
     */
    public void endAtPointVideo() {
        mCurrentOperaName = "endAtPointVideo";
        Log.d(TAG, "send cmd : endAtPointVideo");
        mDvrManager.endAtPointVideo();
    }

    /**
     * 用户触发执行紧急录像
     */
    public void startEmergencyVideo() {
        mCurrentOperaName = "startEmergencyVideo";
        Log.d(TAG, "send cmd : startEmergencyVideo");
        mDvrManager.startEmergencyVideo();
    }

    /**
     * 回放时进入紧急录像
     */
    public void enterEmergencyVideo() {
        mCurrentOperaName = "enterEmergencyVideo";
        Log.d(TAG, "send cmd : enterEmergencyVideo");
        mDvrManager.enterEmergencyVideo();
    }

    /**
     * 设置普通录制状态，触发普通录像
     * 0:off
     * 1:on
     */
    public void setRecording(int value) {
        mCurrentOperaName = "setRecording";
        Log.d(TAG, "Set : setDVRRecordTime value = " + value);
        mDvrManager.setRecording(value);
    }

    /**
     * 获取普通录制状态
     * 0:off
     * 1:on
     *
     * @return
     */
    public int getRecording() {
        Log.d(TAG, "Get : getDVRRecording");
        return mDvrManager.getRecording();
    }

    /**
     * 回放时进入普通录像
     */
    public void enterVideo() {
        mCurrentOperaName = "enterVideo";
        Log.d(TAG, "send cmd : enterVideo");
        mDvrManager.enterVideo(1);
    }

    /**
     * 设置当前显示模式
     * 0：off
     * 1:预览界面
     * 2：回放界面
     *
     * @param value
     * @return
     */
    public void setDisplayMode(int value) {
        mCurrentOperaName = "setDisplayMode";
        Log.d(TAG, "Set : setDVRDisplayMode value = " + value);
        mDvrManager.setDisplayMode(value);
    }

    /**
     * 获取当前显示模式
     * 1：off
     * 2:预览界面
     * 3：回放界面
     *
     * @return
     */

    public int getDisplayMode() {
        Log.d(TAG, "Get : getDVRDisplayMode");
        return mDvrManager.getDisplayMode();
    }

    /**
     * 进入编辑模式
     */
    public void enterEditMode() {
        mCurrentOperaName = "enterEditMode";
        Log.d(TAG, "send cmd : enterEditMode");
        mDvrManager.enterEditMode();
    }

    /**
     * 退出编辑模式
     */
    public void exitEditMode() {
        mCurrentOperaName = "exitEditMode";
        Log.d(TAG, "send cmd : exitEditMode");
        mDvrManager.exitEditMode();
    }

    /**
     * 选择当前操作区所有文件
     */
    public void selectAll() {
        mCurrentOperaName = "selectAll";
        Log.d(TAG, "send cmd : selAllDocument");
        mDvrManager.selectAll();
    }

    /**
     * 取消选择当前操作区所有文件
     */
    public void cancelSelectAll() {
        mCurrentOperaName = "cancelSelectAll";
        Log.d(TAG, "send cmd : cancelSelDocument");
        mDvrManager.cancelSelectAll();
    }

    /**
     * 选择缩略图
     */
    public void selectThumbnail(int index) {
        mCurrentOperaName = "selectThumbnail";
        Log.d(TAG, "send cmd : setDVRSelectThumbnail  index = " + index);
        mDvrManager.selectThumbnail(index);
    }

    /**
     * 取消选择缩略图
     */
    public void cancelThumbnail(int index) {
        mCurrentOperaName = "cancelThumbnail";
        Log.d(TAG, "send cmd : setDVRCancelThumbnail  index = " + index);
        mDvrManager.cancelThumbnail(index);
    }

    /**
     * 获取录制时间
     * 1 : 1min
     * 2 : 3min
     * 3 : 5min
     *
     * @return
     */
    public int getRecordTime() {
        mCurrentOperaName = "getRecordTime";
        Log.d(TAG, "Get : getDVRRecordTime");
        return mDvrManager.getRecordTime();
    }

    /**
     * 设置录制时时长
     * 1 : 1min
     * 2 : 3min
     * 3 : 5min
     *
     * @param value
     */
    public void setRecordTime(int value) {
        mCurrentOperaName = "setRecordTime";
        Log.d(TAG, "Set : setDVRRecordTime value = " + value);
        mDvrManager.setRecordTime(value);
    }

    /**
     * 设置麦克风状态
     * 0:off
     * 1:on
     */
    public void setMicroPhone() {
        mCurrentOperaName = "setMicroPhone";
        Log.d(TAG, "Set : ");
        mDvrManager.setMicroPhone();
    }

    /**
     * 设置分辨率
     * 0:1920*1080
     * 1:1280*720
     *
     * @param value
     */
    public void setResolution(int value) {
        mCurrentOperaName = "setResolution";
        Log.d(TAG, "Set : setDVRResolution value = " + value);
        mDvrManager.setResolution(value);
    }

    /**
     * 获取分辨率
     * 0:1920*1080
     * 1:1280*720
     */
    public int getResolution() {
        Log.d(TAG, "Get : getDVRResolution");
        return mDvrManager.getResolution();
    }

    /**
     * 设置停车监控灵敏度
     * 0:High
     * 2:middle
     * 3:low
     *
     * @param value
     */
    public void setParKss(int value) {
        mCurrentOperaName = "setParkSS";
        Log.d(TAG, "Set : setDVRParkSS value = " + value);
        mDvrManager.setParkSS(value);
    }

    /**
     * 获取停车监控灵敏度
     * 0:High
     * 2:middle
     * 3:low
     */
    public int getParKss() {
        Log.d(TAG, "Get : getDVRParkSS");
        return mDvrManager.getParkSS();
    }

    /**
     * 设置行车监控灵敏度
     * 1:High
     * 2:middle
     * 3:low
     *
     * @param value
     */
    public void setDrivEss(int value) {
        mCurrentOperaName = "setParkSS";
        Log.d(TAG, "Set : setDVRDriveSS value = " + value);
        mDvrManager.setDriveSS(value);
    }

    /**
     * 获取行车监控灵敏度
     * 0:High
     * 2:middle
     * 3:low
     */
    public int getDrivEss() {
        Log.d(TAG, "Get : getDVRDriveSS");
        return mDvrManager.getDriveSS();
    }

    /**
     * 设置DVR System
     * 0:无应答
     * 1:格式化SD卡
     * 2:系统升级
     * 3:查询版本号
     * 4:恢复出厂设置
     * 5:查询SD卡容量
     */
    public void setDvrSystemOperaType(int state) {
        mCurrentOperaName = "setDvrSystemOperaType";
        Log.d(TAG, "Set : setDvrSystemOperaType state = " + state);
        mDvrManager.setDVRSystemOperaTy(state);
    }

    /**
     * 获取DVR状态
     * 0:no request
     * 1:no SD
     * 2:no update software
     * 3:start
     * 4:success
     * 5:fail
     */
    public int getDvrSystemUpdateState() {
        Log.d(TAG, "Get : getSystemUpdateState");
        return mDvrManager.getDVRSystemUpdateState();
    }

    /**
     * 获取SD卡总容量
     * 0:8G
     * 1:16G
     * 2:32G
     * 3:64G
     * 4:128G
     */
    public int getDvrSdcApacitype() {
        Log.d(TAG, "Get : getDvrSdcApacitype");
        return mDvrManager.getDVRSDCapacity();
    }

    /**
     * 获取SD卡容量区整数部分
     */
    public int getDvrSdInteger() {
        Log.d(TAG, "Get : getDVRSDInteger");
        return mDvrManager.getDVRSDInteger();
    }

    /**
     * 获取SD卡容量区小数部分
     */
    public int getDvrSdDecimal() {
        Log.d(TAG, "Get : getDVRSDDecimal");
        return mDvrManager.getDVRSDDecimal();
    }

    /**
     * 查询SD卡存储类别容量
     * 1:photo
     * 2:video
     * 3:all
     * 4:emergency
     * 5:随心拍
     */
    public void queryDvrArea(int value) {
        mCurrentOperaName = "queryDVRArea";
        Log.d(TAG, "Query : queryDVRArea value = " + value);
        mDvrManager.queryDVRArea(value);
    }

    /**
     * 获取SD卡Format
     */
    public int getDvrSdFormat() {
        Log.d(TAG, "Get : getDVRSDFormat");
        return mDvrManager.getDVRSDFormat();
    }

    /**
     * 进入 setting 模式
     * */
    public void inSetting(){
        Log.d(TAG, "inSetting: ");
        mDvrManager.setDeviceFuncUniversalInterface("DvrDevice", "entrySetting", setbundle);
    }

    /**
     * 退出 setting 模式
     * */
    public void outSetting(){
        Log.d(TAG, "outSetting: ");
        mDvrManager.setDeviceFuncUniversalInterface("DvrDevice", "exitSetting", setbundle);
    }

    /**
     * 语音随心拍视频
     */
//    public void freeshotPhotoOrVedioByVoice(int type) {
//        mCurrentOperaName = "freeshotPhotoOrVedioByVoice";
//        Log.d(TAG, "freeshotPhotoOrVedioByVoice: type = " + type);
//        mDvrManager.executeTakePhotoAtWillByVoice(type);
//    }

}
