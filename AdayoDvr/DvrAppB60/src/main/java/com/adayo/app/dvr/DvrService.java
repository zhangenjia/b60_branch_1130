package com.adayo.app.dvr;

import static com.adayo.app.dvr.constant.Constant.DVR_Common_Cb_Func;
import static com.adayo.app.dvr.constant.Constant.DVR_Photograph;
import static com.adayo.app.dvr.constant.Constant.DVR_CB_KEY_PHOTOKEYOPERA;
import static com.adayo.app.dvr.constant.Constant.DVR_Power_OFF;
import static com.adayo.app.dvr.constant.Constant.DVR_Power_On;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adayo.app.dvr.activity.MainActivity;
import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;
import com.adayo.app.dvr.utils.FastClickCheck;
import com.adayo.app.dvr.utils.SoundPoolUtil;
import com.adayo.app.dvr.utils.SystemPropertiesPresenter;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class DvrService extends Service {
    private static final String TAG = DvrService.class.getSimpleName();
    private DvrController mDvrController;
    private AAOP_DeviceServiceManager mDvrManager;
    BackCarBinder binder = new BackCarBinder();
    private static final int  soundType = 3;
    private static final int  photoSuccess = 1;
    private static final int  willPhotoFail = 2 ;
    private static final int  photoFail = 0 ;
    private static final int  videoStart = 2 ;
    private static final int  videoEnd = 3 ;
    private static final int  FkPhotoFail = 4 ;
    private SystemPropertiesPresenter mSystemProperties;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        mDvrController = DvrController.getInstance();
        addServiceToServiceManager("locat_dvr");
        initListener();
        mDvrController.init();
        initData();
    }

    private void initData() {
        String value =  mSystemProperties.getProperty("dvr.status","1");
        Settings.Global.putInt(getApplicationContext().getContentResolver(),"DVR_STATUS", Integer.parseInt(value));
        setsharDataDvr(value);
    }
    private void setsharDataDvr(String value){

        JSONObject obj = new JSONObject();
        try {
            obj.put("dvr_state", value);
            //通过shareinfo 给systemui发送dvr状态
            ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
            shareDataManager.sendShareData(Constant.SRC_SHAREINFO_NUM, obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    private class BackCarBinder extends Binder {
        public DvrService getService() {
            return DvrService.this;
        }
    }
    private static final int DVR_TIME = 1000;
    private static final int mTime =500;
    DvrController.DvrCallback mDvrCallback = new DvrController.DvrCallback() {
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
                if (bundle.getInt(Constant.DVR_St_DisplayMode) == 1){
                    if (bundle.containsKey(DVR_Photograph)) {
                        Log.d(TAG, "notifyChange: DVR_Photograph ");
                        if (bundle.getInt(Constant.DVR_Photograph) == photoSuccess){
                            if (!FastClickCheck.isFastClickTime(mTime)) {
                                return;
                            }
                            Log.d(TAG, "notifyChange: DVR_Photograph sucess");
                            serviceHandler.sendEmptyMessage(DVR_PHOTO);
                        }else {
                            Log.d(TAG, "notifyChange: DVR_Photograph fail");
                        }
                    }
                }

                if (bundle.getInt(Constant.DVR_TakePhotosAtWillByVoice) == photoSuccess){
                    if (!FastClickCheck.isFastClickTime(mTime)) {
                        return;
                    }
                    Log.d(TAG, "notifyChange: DVR_TakePhotosAtWillByVoice sucess");
                    serviceHandler.sendEmptyMessage(DVR_PHOTO);
                }else if(bundle.getInt(Constant.DVR_TakePhotosAtWillByVoice) == willPhotoFail){
                    if (!FastClickCheck.isFastClickTime(mTime)) {
                        return;
                    }
                    Log.d(TAG, "notifyChange: DVR_TakePhotosAtWillByVoice DVR_PHOTO_FAIL fail");
                    serviceHandler.sendEmptyMessage(DVR_PHOTO_FAIL);
                } else{

                    Log.d(TAG, "notifyChange: DVR_Photograph fail");
                }
                if (bundle.getInt(Constant.DVR_TakeVideoAtWillByVoice) == videoEnd){
                    if (!FastClickCheck.isFastClickTime(mTime)) {
                        return;
                    }
                    Log.d(TAG, "notifyChange: DVR_TakePhotosAtWillByVoice DVR_VIDEO sucess");
                    serviceHandler.sendEmptyMessage(DVR_VIDEO);
                }else {
                    Log.d(TAG, "notifyChange: DVR_Photograph fail");
                }

               if (bundle.containsKey(DVR_CB_KEY_PHOTOKEYOPERA)) {
                        Log.d(TAG, "notifyChange: 11111DVR_CB_KEY_PHOTOKEYOPERA ");
                        if (bundle.getInt(Constant.DVR_CB_KEY_PHOTOKEYOPERA) == photoSuccess){
                            if (!FastClickCheck.isFastClickTime(mTime)) {
                                return;
                            }
                            Log.d(TAG, "notifyChange: 11111DVR_CB_KEY_PHOTOKEYOPERA photo sucess");
                            serviceHandler.sendEmptyMessage(DVR_PHOTO);
                        }else if (bundle.getInt(Constant.DVR_CB_KEY_PHOTOKEYOPERA) == videoStart){
                            if (!FastClickCheck.isFastClickTime(mTime)) {
                                return;
                            }
                            Log.d(TAG, "notifyChange: DVR_CB_KEY_PHOTOKEYOPERA video star sucess");
                            serviceHandler.sendEmptyMessage(DVR_VIDEO);
                        }else if(bundle.getInt(Constant.DVR_CB_KEY_PHOTOKEYOPERA) == videoEnd){
                            if (!FastClickCheck.isFastClickTime(mTime)) {
                                return;
                            }
                            Log.d(TAG, "notifyChange: DVR_CB_KEY_PHOTOKEYOPERA video send sucess");
                            serviceHandler.sendEmptyMessage(DVR_VIDEO);
                        }else  if (bundle.getInt(Constant.DVR_CB_KEY_PHOTOKEYOPERA) == FkPhotoFail){
                            if (!FastClickCheck.isFastClickTime(mTime)) {
                                return;
                            }
                            Log.d(TAG, "notifyChange: DVR_CB_KEY_PHOTOKEYOPERA videofail sucess");
                            serviceHandler.sendEmptyMessage(DVR_PHOTO_FAIL);
                        }else {
                            Log.d(TAG, "notifyChange: 11111DVR_CB_KEY_PHOTOKEYOPERA fail");
                    }

                }

            }

        }
    };

    private void initListener(){
        mDvrController.registerCallback(mDvrCallback);
        mSystemProperties =  SystemPropertiesPresenter.getInstance();
        registerContentObserver();
    }
    private void registerContentObserver(){
        ModelObserver mModelObserver=new ModelObserver(new Handler());
        getApplication().getContentResolver().registerContentObserver(android.provider.Settings.Global.getUriFor("DVR_STATUS"),false,mModelObserver);

    }

    private class ModelObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public ModelObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            int  dvrStats = Settings.Global.getInt(getApplication().getApplicationContext().getContentResolver(),"DVR_STATUS", 0);
            setsharDataDvr(String.valueOf(dvrStats));
        }
    }
    /**
     * 将该服务添加到ServiceManager中
     */
    private void addServiceToServiceManager(String serviceName)
    {
        Log.d(TAG, "addServiceToServiceManager: begin serviceName = " + serviceName);
        try
        {
            Object object = new Object();
            Method addService;
            addService = Class.forName("android.os.ServiceManager").getMethod("addService", String.class,IBinder.class);
            addService.invoke(object, serviceName, binder);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d(TAG, "addServiceToServiceManager:  end");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDvrController.destroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void okSound(){
        AudioManager am = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(mAudioFocusChangeListener, soundType, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        SoundPoolUtil.getInstance(this.getApplication()).initSoundPool();
        SoundPoolUtil.getInstance(this.getApplication()).stopPlay();
        SoundPoolUtil.getInstance(this.getApplication()).playSoundWithRedId(R.raw.photobeep);
        serviceHandler.sendEmptyMessageDelayed(DVR_PHOTO_CLOSE,DVR_TIME);
    }

    private void noSound(){
        AudioManager manager = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        manager.abandonAudioFocus(mAudioFocusChangeListener);
    }

    private void okVideoSound(){
        AudioManager am = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(mAudioFocusChangeListener, soundType, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        SoundPoolUtil.getInstance(this.getApplication()).initSoundPool();
        SoundPoolUtil.getInstance(this.getApplication()).stopPlay();
        SoundPoolUtil.getInstance(this.getApplication()).playSoundWithRedId(R.raw.videobeep);
        serviceHandler.sendEmptyMessageDelayed(DVR_VIDEO_CLOSE,DVR_TIME);
    }

    private void noVideoSound(){
        AudioManager manager = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        manager.abandonAudioFocus(mAudioFocusChangeListener);
    }

    private void okphotoFailSound(){
        AudioManager am = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(mAudioFocusChangeListener, soundType, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        SoundPoolUtil.getInstance(this.getApplication()).initSoundPool();
        SoundPoolUtil.getInstance(this.getApplication()).stopPlay();
        SoundPoolUtil.getInstance(this.getApplication()).playSoundWithRedId(R.raw.photobeepfail);
        serviceHandler.sendEmptyMessageDelayed(DVR_PHOTO_FAIL_CLOSER,DVR_TIME);
    }

    private void nophotoFailSound(){
        AudioManager manager = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        manager.abandonAudioFocus(mAudioFocusChangeListener);
    }

    private void okVideoFailSound(){
        AudioManager am = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        am.requestAudioFocus(mAudioFocusChangeListener, soundType, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        SoundPoolUtil.getInstance(this.getApplication()).initSoundPool();
        SoundPoolUtil.getInstance(this.getApplication()).stopPlay();
        SoundPoolUtil.getInstance(this.getApplication()).playSoundWithRedId(R.raw.videobeepfail);
        serviceHandler.sendEmptyMessageDelayed(DVR_VIDEO_FAIL_CLOSE,DVR_TIME);
    }

    private void noVideooFailSound(){
        AudioManager manager = (AudioManager) this.getApplication().getSystemService(AUDIO_SERVICE);
        manager.abandonAudioFocus(mAudioFocusChangeListener);
    }

    private static final int DVR_PHOTO = 100;
    private static final int DVR_PHOTO_CLOSE = 101;
    private static final int DVR_PHOTO_FAIL = 102;
    private static final int DVR_PHOTO_FAIL_CLOSER = 103;
    private static final int DVR_VIDEO = 104;
    private static final int DVR_VIDEO_CLOSE = 105;
    private static final int DVR_VIDEO_FAIL = 106;
    private static final int DVR_VIDEO_FAIL_CLOSE = 107;
    private Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DVR_PHOTO:
                    okSound();
                    break;
                case DVR_PHOTO_CLOSE:
                    noSound();
                    break;
                case DVR_VIDEO:
                    okVideoSound();
                    break;
                case DVR_VIDEO_CLOSE:
                    noVideoSound();
                    break;
                case DVR_PHOTO_FAIL:
                    okphotoFailSound();
                    break;
                case DVR_PHOTO_FAIL_CLOSER:
                    nophotoFailSound();
                    break;
                default:
                    break;
            }
        }
    };
}
