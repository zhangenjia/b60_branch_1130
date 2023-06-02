package com.adayo.app.camera.signalview;

import static android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.media.CarAudioManager;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.aaop_camera.AAOP_Camera;
import com.adayo.proxy.aaop_camera.signalview.base.BaseSignalView;
import com.adayo.proxy.aaop_camera.signalview.state.SignalViewState;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngAudioSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;

import java.lang.ref.WeakReference;

/**
 * @author Yiwen.Huan
 * created at 2021/9/18 10:53
 */
public class RadarBeepSignalView extends BaseSignalView {
    private Car mCar;
    private CarAudioManager mCarAudioManager;
    private ServiceConnection mConnection;
    private boolean radarBeeoStatus;

    private IAdayoAudioFocusChange mAudioFocusChangeListener = new IAdayoAudioFocusChange() {
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

    @Override
    public void setView(View view, boolean clickable) {
        super.setView(view,clickable);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    mCarAudioManager = (CarAudioManager) mCar.getCarManager(Car.AUDIO_SERVICE);
                } catch (Exception e) {
                    Log.e("AdayoCamera", TAG + " - onServiceConnected: error", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCar.disconnect();//断连
                mCar = Car.createCar(view.getContext().getApplicationContext(), mConnection);//重新创建
                mCar.connect();//重连
            }
        };
        mCar = Car.createCar(view.getContext().getApplicationContext(), mConnection);
        mCar.connect();
        if (!isConnect()) {
            Thread thread = new Thread(new CarConnectRunnable(this));
            thread.start();
        }
    }

    @Override
    protected void processEventBehavior(View view, SignalViewState state) {

    }

    @Override
    protected void processSignalBehavior(View view, SignalViewState state) {
        if (!radarBeeoStatus){
            duckAudioStream(0 != state.signalValue);
        }else{
            unDuckAudioStream(0 != state.signalValue);
        }
    }

    private void duckAudioStream(boolean duck) {
        if (null == mCarAudioManager) {
            Log.e("AdayoCamera", TAG + " - duckAudioStream: failed because null == mCarAudioManager , do nothing");
            return;
        }
        if (duck) {
            try {
                radarBeeoStatus = true;
                SrcMngAudioSwitchManager.getInstance().requestAdayoAudioFocus(12, AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK, AdayoSource.ADAYO_SOURCE_RVC,mAudioFocusChangeListener, getView().getContext());
                mCarAudioManager.duckAudioStream(12);
                Log.d("AdayoCamera", TAG + " - duckAudioStream:  mCarAudioManager.duckAudioStream(12)");
            } catch (CarNotConnectedException e) {
                Log.e("AdayoCamera", TAG + " - duckAudioStream: error", e);
            } finally {
                AAOP_Camera.setSystemProperty("adayo.radar.voice.status", "ON");
            }
        }
    }

    private void unDuckAudioStream(boolean duck){
        if (null == mCarAudioManager) {
            Log.e("AdayoCamera", TAG + " - duckAudioStream: failed because null == mCarAudioManager , do nothing");
            return;
        }
        if (!duck) {
            try {
                radarBeeoStatus = false;
                SrcMngAudioSwitchManager.getInstance().abandonAdayoAudioFocus(mAudioFocusChangeListener);
                mCarAudioManager.unduckAudioStream(12);
                Log.d("AdayoCamera", TAG + " - duckAudioStream:  mCarAudioManager.unduckAudioStream(12)");
            } catch (CarNotConnectedException e) {
                Log.e("AdayoCamera", TAG + " - duckAudioStream: error", e);
            } finally {
                AAOP_Camera.setSystemProperty("adayo.radar.voice.status", "OFF");
            }
        }
    }

    public synchronized boolean isConnect() {
        boolean isConnect = mCar.isConnected() || mCar.isConnecting();
        Log.d("AdayoCamera", TAG + " - isConnect() returned: " + isConnect);
        return isConnect;
    }

    public synchronized void connect() {
        if (mCar.isConnected() || mCar.isConnecting()) {
            Log.d("AdayoCamera", TAG + " - connect: mCar.isConnected() || mCar.isConnecting()");
            return;
        }
        mCar.connect();
    }


    public static class CarConnectRunnable implements Runnable {
        private static final String TAG = "CarConnectRunnable";
        private WeakReference<RadarBeepSignalView> wr;

        public CarConnectRunnable(RadarBeepSignalView signalView) {
            wr = new WeakReference<>(signalView);
        }

        @Override
        public void run() {
            if (null == wr) {
                Log.e("AdayoCamera", TAG + " - run: failed because null == wr");
                return;
            }
            RadarBeepSignalView signalView = wr.get();
            if (null == signalView) {
                Log.e("AdayoCamera", TAG + " - run: failed because RadarBeepSignalView is null");
            }
            while (!signalView.isConnect()) {
                signalView.connect();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Log.e("AdayoCamera", TAG + " - run: error", e);
                }
            }
        }
    }

}
