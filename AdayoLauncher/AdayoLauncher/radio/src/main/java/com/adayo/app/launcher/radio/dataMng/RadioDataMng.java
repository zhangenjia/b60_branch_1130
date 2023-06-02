package com.adayo.app.launcher.radio.dataMng;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.app.launcher.radio.RadioCarView;
import com.adayo.app.launcher.radio.constant.Utils;
import com.adayo.proxy.tuner.radio.RadioCollectionFreq;
import com.adayo.proxy.tuner.radio.RadioInfo;
import com.adayo.proxy.tuner.radio.RadioManager;
import com.adayo.proxy.tuner.radio.aidl.IAudioFocusChangeListener;
import com.adayo.proxy.tuner.radio.aidl.IRadioInfoChangeListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.adayo.app.launcher.radio.constant.Constants.BAND_AM;
import static com.adayo.app.launcher.radio.constant.Constants.BAND_FM;
import static com.adayo.app.launcher.radio.constant.Constants.RADIO_CALLBACK_UPDATEVIEW;
import static com.adayo.app.launcher.radio.constant.Constants.RADIO_UPDATEVIEW;
import static com.adayo.app.launcher.radio.constant.Constants.isMute;

public class RadioDataMng {
    private static final String TAG = "RadioDataMng";
    private Context mContext;
    private Handler mHandler;
    private volatile static RadioDataMng mInstance = null;
    public int mBand = BAND_FM;
    public int callBackFreq ;
    public int lastAMFreq = 531;
    public double lastFMFreq = 87.5 ;
    public List<RadioInfo> mRadioSearchList;
    public List<RadioCollectionFreq> mRadioCollectionList;
    public int isSearch;
    private int retInfo =1;
    public int isPlay = isMute;
    private RadioDataMng(){

    }

    public static RadioDataMng getmInstance(){
        if (mInstance == null){
            synchronized (RadioDataMng.class){
                if (mInstance == null){
                    mInstance = new RadioDataMng();
                }
            }
        }
        return mInstance;
    }

    private IRadioInfoChangeListener.Stub mIRadioInfoChangeListener = new IRadioInfoChangeListener.Stub() {
        @Override
        public void radioInfoContent(RadioInfo radioInfo) throws RemoteException {
            Log.d(Utils.TAG, TAG + " radioInfoContent: radioInfo = "+radioInfo);
            if (BAND_AM == radioInfo.getBand()){
                mBand = radioInfo.getBand();
                isSearch = radioInfo.getSearchStatus();
                callBackFreq = radioInfo.getFreq();
                lastAMFreq = callBackFreq;
                isPlay = radioInfo.getMcuMuteState();
                Log.d(TAG, " radioInfoContent: isPlay = "+isPlay);
            }else {
                mBand = radioInfo.getBand();
                callBackFreq = radioInfo.getFreq();
                lastFMFreq = (double) callBackFreq/ 100;
                Log.d(TAG,  " radioInfoContent: lastFMFreq = "+lastFMFreq);
                isSearch = radioInfo.getSearchStatus();
                isPlay = radioInfo.getMcuMuteState();
            }
            Message msg = mHandler.obtainMessage();
            msg.what = RADIO_CALLBACK_UPDATEVIEW;
            mHandler.sendMessage(msg);
        }
    };

    public void init(Context context, Handler handler){
        Log.d(Utils.TAG, TAG + " init: start ");
        mContext = context;
        mHandler =handler;
        Log.d(Utils.TAG, TAG + " init: end ");
    }

    public void registerService(){
        try {
            retInfo = RadioManager.getRadioManager().registerRadioInfoChangeListener(mIRadioInfoChangeListener);
            Log.d(Utils.TAG, TAG + " registerService: retInfo = "+retInfo);
            //如果没有注册成功则重新注册
            if (retInfo != 0){
                final Timer timerRegister = new Timer();
                timerRegister.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i(Utils.TAG, TAG + " registerService: Timer retInfo = " + retInfo);
                        if (retInfo==0) {
                            timerRegister.cancel();
                        } else {
                            try {
                                retInfo = RadioManager.getRadioManager().registerRadioInfoChangeListener(mIRadioInfoChangeListener);
                            } catch (RemoteException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 200, 200);
            }
            //音频焦点
            Thread.sleep(800);
            int retAudioFocus = RadioManager.getRadioManager().registerAudioFocusChangeListener(mIAudioFocusChangeListener);
            Log.d(Utils.TAG, TAG + " registerService: retAudioFocus:"+retAudioFocus);
        } catch (RemoteException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void unRegisterService(){
        try {
            int retUnRegisterInfo = RadioManager.getRadioManager().unRegisterRadioInfoChangeListener(mIRadioInfoChangeListener);
            int retUnRegisterAudioFocus = RadioManager.getRadioManager().unRegisterAudioFocusChangeListener(mIAudioFocusChangeListener);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void getRadioData(){
        try {
            mBand = RadioManager.getRadioManager().getBand();
            Log.d(Utils.TAG, TAG + " getRadioData: mBand:"+mBand);
            if (mBand == BAND_AM){
                lastAMFreq = getLastAMFreq();
                Log.d(Utils.TAG, TAG + " getRadioData: lastAMFreq:"+lastAMFreq);
                //RadioManager.getRadioManager().setBandAndFreq(BAND_AM, lastAMFreq);
            }else{
                lastFMFreq = getLastFMFreq();
                Log.d(Utils.TAG, TAG + " getRadioData: lastFMFreq:"+lastFMFreq);
                //RadioManager.getRadioManager().setBandAndFreq(BAND_FM, (int) (lastFMFreq* 100));
            }
            isPlay = RadioManager.getRadioManager().getMcuMuteState();
            Log.d(Utils.TAG, TAG + " getRadioData: isPlay:"+isPlay);
            Message msg = mHandler.obtainMessage();
            msg.what = RADIO_UPDATEVIEW;
            mHandler.sendMessage(msg);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public int getLastAMFreq(){
        try {
            lastAMFreq = RadioManager.getRadioManager().getLastAMFreq();
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
        return lastAMFreq;
    }

    public double getLastFMFreq(){
        try {
//            DecimalFormat df = new DecimalFormat("0.0");
            lastFMFreq = (double) RadioManager.getRadioManager().getLastFMFreq()/ 100;
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
        }
        return lastFMFreq;
    }

    private IAudioFocusChangeListener.Stub mIAudioFocusChangeListener = new IAudioFocusChangeListener.Stub() {
        @Override
        public void audioFocusGain() throws RemoteException {

        }

        @Override
        public void audioFocusGainTransient() throws RemoteException {

        }

        @Override
        public void audioFocusLoss() throws RemoteException {

        }

        @Override
        public void audioFocusLossTransient() throws RemoteException {

        }
    };
}
