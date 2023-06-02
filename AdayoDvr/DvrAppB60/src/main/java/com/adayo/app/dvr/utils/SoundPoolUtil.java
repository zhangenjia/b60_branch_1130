package com.adayo.app.dvr.utils;

import android.car.media.CarAudioManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.adayo.proxy.setting.system.SettingsSvcIfManager;

/**
 * 雷达报警策略:
 * 1)P档对任何雷都不报警音；
 * 2)发声如下:
 * 红色: 长音(一直响)
 * 黄色：Tone 3 - 3Hz    (1s响3声)
 * 绿色：Tone 2 - 1.5 Hz (2s响3声)(1s响1.5声)
 */


/**
 * 简短音频播放工具类
 */
public class SoundPoolUtil {
    private volatile static SoundPoolUtil client;
    private SoundPool mSoundPool;
    private AudioManager mAudioManager;
    /*允许同时播放的音频数（为1时会立即结束上一个音频播放当前的音频）*/
    private static final int MAX_STREAMS = 1;
    private int mSoundId;
    private int mResId;
    private Context mainContext;
    private final String TAG = "SoundPoolUtil";

    /**
     * 静态内部类单列模式
     */
   /* private SoundPoolUtil(){
        //this.mainContext = context;
        init();
        mAudioManager = (AudioManager) this.mainContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private static class SoundPoolUtilHolder{
        private static final SoundPoolUtil instance = new SoundPoolUtil();
    }

    public static SoundPoolUtil getInstance(){
        return SoundPoolUtil.SoundPoolUtilHolder.instance;
    }*/

    public static SoundPoolUtil getInstance(Context context) {
        if (client == null) {
            synchronized (SoundPoolUtil.class) {
                if (client == null) {
                    client = new SoundPoolUtil(context);
                }
            }
        }
        return client;
    }

    private SoundPoolUtil(Context context) {
        this.mainContext = context;
        init();
        mAudioManager = (AudioManager) this.mainContext.getSystemService(Context.AUDIO_SERVICE);
        /*mAudioManager = (AudioManager) this.mainContext.getSystemService(Context.AUDIO_SERVICE);
        mainContext.setVolumeControlStream(streamType);
        this.mSoundPool = new SoundPool(MAX_STREAMS, streamType, 0);*/
    }
    private static final int SDK_VERSION = 21;
    private void init(){
        //sdk版本21是SoundPool 的一个分水岭
        if (Build.VERSION.SDK_INT >= SDK_VERSION) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams(MAX_STREAMS);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
        } else {
            /**
             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
             * 第二个参数：int streamType：AudioManager中描述的音频流类型
             *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
             */
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
    }


    //已经测试：调用方式OK

    // 调用方式:关闭雷达报警
    // SoundPoolUtil.getInstance(mContext).setRadarVoiceSwitch(false);

    // 调用方式: 打开雷达报警
    // SoundPoolUtil.getInstance(mContext).setRadarVoiceSwitch(true);
    //  playRadarVoice();

    /**
     * 设置雷达报警音开关(经过测试OK)
     */
    private  volatile boolean  needPlayRadarVoice = false;
    public void setRadarVoiceSwitch(boolean needPlayRadarVoice){
        Log.d("SoundPoolUtil", "setRadarVoiceSwitch:" + needPlayRadarVoice);
        this.needPlayRadarVoice = needPlayRadarVoice;
    }


    /**
     * 停止播放
     */
    public void realease(){
        if(mSoundPool != null) {
            Log.d("SoundPoolUtil", "===realease===");
            mSoundPool.unload(mSoundId);
            mSoundPool.release();
            needInit = true;
            //Log.d("SoundPoolUtil",  "setAudioStreamRepression: 100");
            //在雷达音停止的时候，  设置雷达压低媒体的比例为100%
            //SettingsSvcIfManager.getSettingsManager().setAudioStreamRepression(12, 3, 100);
        }
    }

    /**
     * 初始化
     */
    private boolean needInit = false;
    public void initSoundPool(){
        if(needInit){
            init();
            needInit = false;
        }
    }


    /**
     * unLoad
     */
    public void stopPlay(){
        if(mSoundPool != null) {
            mSoundPool.unload(mSoundId);
        }
    }

    private boolean isShowWindow = false;
    public void setShowWindow(boolean isShowWindow) {
        this.isShowWindow = isShowWindow;
        Log.d("SoundPoolUtil", "setShowWindow-->:" + isShowWindow);
    }

    /**
     * 循环播放
     */
    public void playRecycle(int resId){
        Log.d("SoundPoolUtil", "===playRecycle===");
        this.mResId = resId;
        this.mSoundId = this.mSoundPool.load(this.mainContext, this.mResId, 1);
        this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playSound();
            }
        });
    }


    /**
     * 播放音频SoundPool---call
     * @param resId 本地音频资源
     */
    public void playSoundWithRedId(int resId) {
        this.mResId = resId;
        this.mSoundId = this.mSoundPool.load(this.mainContext, this.mResId, 1);
        Log.d(TAG, "playSoundWithRedId:  mSoundId ====" +mSoundId);
        this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.d(TAG, "onLoadComplete:  start");
                playSound();
            }
        });

    }

    /**
     * 播放音频，但是当前有音频这正播放中时不响应该次音频播放
     * @param resId 本地音频资源
     */
    public synchronized void playSoundUnfinished(int resId) {
        if ( isFmActive()) {
            return;
        }
        this.mResId = resId;
        this.mSoundId = this.mSoundPool.load(this.mainContext, this.mResId, 1);
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playSound();
            }
        });
    }

    /**
     * 播放音频文件
     */
    private void playSound() {
        Log.d("SoundPoolUtil","===playSound===");
        mSoundPool.play(this.mSoundId, 1.0f, 1.0f, 0, 0, 1f);
    }

    /**
     * 判断当前设备是否正在播放音频
     */
    private boolean isFmActive() {
        if (mAudioManager == null) {
            return false;
        }
        return mAudioManager.isMusicActive();
    }
}

