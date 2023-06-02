package com.adayo.app.camera.signalview;

import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;

import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_VR;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

import com.adayo.app.camera.MainService;
import com.adayo.app.camera.R;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngAudioSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Yiwen.Huan
 * created at 2021/10/9 14:52
 */
public class TtsSignalView extends BaseSignalView  {

    private Context cxt;
    /**
     * TTS播报-广播发送
     */
    private static final String APS_TTSSEND_ACTION = "ADAYO_NAVI_TXMAP_SEND";
    private static final String APS_TTS_ACTION = "ADAYO_NAVI_TXMAP_RECV";
    private static final String APS_TTS_KEY_CMDTYPE = "cmdType";
    private static final String APS_TTS_CMDTYPE_PLAY = "playTts";
    private static final String APS_TTS_KEY_TEXT= "text";
    private static final String APS_TTS_KEY_STREAMTYPE = "streamType";
    private static final String APS_TTS_KEY_PKGNAME= "packageName";
    private static final int STREAM_ALARM = 9;
    private static ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    private List<String> mlist;

    private IAdayoAudioFocusChange mTtsChangeListener = new IAdayoAudioFocusChange() {
        @Override
        public void onAdayoAudioOnGain() {

        }

        @Override
        public void onAdayoAudioOnLoss() {

        }

        @Override
        public void onAdayoAudioLossTransient() {

        }

        @Override
        public void onAdayoAudioLossTransientCanDuck() {

        }
    };
    public TtsSignalView(Context cxt) {
        this.cxt = cxt;
        mlist = new ArrayList<>();
        IntentFilter intent = new IntentFilter();
        intent.addAction(APS_TTSSEND_ACTION);
        cxt.registerReceiver(mTtsReceiver,intent);
    }

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {
        singleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                playTts(cxt.getString(state.eventTriggeredText));
            }
        });

    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {

    }

//    @Override
//    protected void processSignalBehavior(View view, SignalViewState state) {
//        singleThreadPool.execute(new Runnable() {
//            @Override
//            public void run() {
//                playTts(cxt.getString(state.eventTriggeredText));
//            }
//        });
//    }

    //开始播报-腾讯随行TTS接口
    private void playTts(String ttsText) {
        if (!"tts".equals(cxt.getString(R.string.tts))) {
            Log.d("AdayoCamera", TAG + " - playTts: failed because !tts.equals(cxt.getString(R.string.tts))");
            return;
        }
        mlist.add(ttsText);
        Log.d("AdayoCamera", TAG +"onReceive:  Grabbing the audio spotlight  ");
        SrcMngAudioSwitchManager.getInstance().requestAdayoAudioFocus(STREAM_ALARM, AUDIOFOCUS_GAIN_TRANSIENT, ADAYO_SOURCE_VR, mTtsChangeListener, cxt);
        Intent intent = new Intent();
        intent.setAction(APS_TTS_ACTION);
        intent.putExtra(APS_TTS_KEY_CMDTYPE, APS_TTS_CMDTYPE_PLAY);         // String 命令类型 播报-playTts0 ,停止播报- stopTts0
        intent.putExtra(APS_TTS_KEY_TEXT, ttsText);                          // String 播报文本
        intent.putExtra(APS_TTS_KEY_STREAMTYPE, STREAM_ALARM);              // int 使用音频流类型
        intent.putExtra(APS_TTS_KEY_PKGNAME, cxt.getPackageName());    // String 哪个应用要调用TTS接口，判别身份
        cxt.sendBroadcast(intent);
//
    }
    //停止播报-腾讯随行TTS接口
    private void stopTts() {
        Intent intent = new Intent("ADAYO_NAVI_TXMAP_RECV");
        intent.putExtra("cmdType", "stopTts");
        Log.d("AdayoCamera", TAG + " - stopTts: ");
        cxt.sendBroadcast(intent);
    }
    private BroadcastReceiver mTtsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                if (APS_TTSSEND_ACTION == intent.getAction() ){
                   String  ttsStatuis =  intent.getStringExtra("ttsStatus");
                   if ("playCompleted".equals(ttsStatuis)){
                       Log.d("AdayoCamera", TAG + "onReceive: mlist.size()  == " + mlist.size());
                       singleThreadPool.execute(new Runnable() {
                           @Override
                           public void run() {
                               if (mlist.size() == 1){
                                   SrcMngAudioSwitchManager.getInstance().abandonAdayoAudioFocus(mTtsChangeListener);
                                   mlist.remove(0);
                                   Log.d("AdayoCamera", TAG + "onReceive:  Release ");
                               }else {
                                   for (int i = 0;i < (mlist.size()-1);i++){
                                       mlist.remove(i);
                                   }
                               }
                           }
                       });
                       Log.d("AdayoCamera", TAG +" onReceive: remove mlist.size()  == " + mlist.size());
                   }
                }

        }
    };
}
