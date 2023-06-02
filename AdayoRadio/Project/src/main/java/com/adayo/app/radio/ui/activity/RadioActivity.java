package com.adayo.app.radio.ui.activity;

import static com.adayo.app.radio.constant.Constants.BAND_AM;
import static com.adayo.app.radio.constant.Constants.BAND_AM_TEXT;
import static com.adayo.app.radio.constant.Constants.BAND_FM;
import static com.adayo.app.radio.constant.Constants.BAND_FM_TEXT;
import static com.adayo.app.radio.constant.Constants.SYSTEM_PROPERTY_SET_MCU_MUTE_KEY;
import static com.adayo.app.radio.constant.Constants.SYSTEM_PROPERTY_SET_RADIO_FOCUS_KEY;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_RADIO_AM;
import static com.adayo.proxy.infrastructure.adayosource.AdayoSource.ADAYO_SOURCE_RADIO_FM;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.app.radio.R;
import com.adayo.app.radio.ui.controller.RadioDataMng;
import com.adayo.app.radio.ui.fragment.RadioInfoFragment;
import com.adayo.app.radio.utils.RadioAPPLog;
import com.adayo.app.radio.utils.SystemPropertiesPresenter;
import com.adayo.app.radio.utils.Utils;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.activity.base.AAOP_HSkinActivity;
import com.adayo.proxy.aaop_hskin.entity.DynamicAttr;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.tuner.radio.RadioManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * base 类
 * @author ADAYO-06
 */
public class RadioActivity extends AAOP_HSkinActivity {
    private static final String TAG = RadioActivity.class.getSimpleName();
    public RadioInfoFragment mRadioInfoFragment;
    private ConstraintLayout linearLayoutBg;
    FragmentManager mFragmentManager;
    private boolean serviceConnectStata;
    /**
     * handler 用于retry连接Radioservice
     */
    private Handler handler = new Handler();
    private boolean keyModelSend = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(RadioAPPLog.TAG, TAG + " onCreate: start savedInstanceState = " + savedInstanceState);
        //防止被系统回收后出现 could not find Fragment constructor问题导致的闪退，强制将outState设置为null，不记录状态。
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        RadioDataMng.getmInstance().init(getApplicationContext());
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                Map map = (HashMap) bundle.get("map");
                if (map != null) {
                    //获取SourceType
                    String sourceType = (String) map.get("SourceType");
                    Log.d(RadioAPPLog.TAG, TAG + " onCreate: sourceType = " + sourceType);
                    RadioDataMng.getmInstance().stringBand = sourceType;
                    //获取From来源
                    String from = (String) map.get("From");
                    Log.d(RadioAPPLog.TAG, TAG + " onCreate: from = " + from);

                }
            }
        }
//        cleanMuteFlag();

        //获取RadioService状态
        try {
            serviceConnectStata = RadioManager.getRadioManager().getServiceConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(RadioAPPLog.TAG, TAG + " onCreate: serviceConnectStata = " + serviceConnectStata);
        if (serviceConnectStata) {
            RadioDataMng.getmInstance().registerService();
            RadioDataMng.getmInstance().getRadioData(RadioDataMng.getmInstance().stringBand);
        } else {
            RetryRunnable retryRunnable = new RetryRunnable();
            handler.post(retryRunnable);
        }

        Log.d(RadioAPPLog.TAG,TAG+  " onCreate: end");
    }

    private int value = 10;
    /**
     * retry机制，RadioService连接
     */
    private class RetryRunnable implements Runnable{
        private int mIndex;
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            serviceConnectStata = RadioManager.getRadioManager().getServiceConnection();
            Log.i(RadioAPPLog.TAG, TAG + " RetryRunnable:  " + serviceConnectStata);
            if(serviceConnectStata){
                RadioDataMng.getmInstance().registerService();
                RadioDataMng.getmInstance().getRadioData(RadioDataMng.getmInstance().stringBand);
            }else{
                if (mIndex % value == 0){
                    mIndex = 0;
                    Log.e(RadioAPPLog.TAG, TAG + "Can't connect RadioService ,reconnect");
                }
                mIndex++;
                handler.removeCallbacks(this);
                handler.postDelayed(this,200);
            }
        }
    }

    public void initView() {
        linearLayoutBg=findViewById(R.id.activity_bg);

        AAOP_HSkin
                .with(linearLayoutBg)
                .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.com_bg)
                .addViewAttrs(new DynamicAttr(Utils.ATTR_BLUR_IMAGE, R.drawable.com_bg))
                .applySkin(false);

    }

    public void initFragments() {
        Log.d(RadioAPPLog.TAG,TAG+  " initFragments: mRadioInfoFragment = "+mRadioInfoFragment);
        if (mRadioInfoFragment == null) {
            mFragmentManager= getFragmentManager();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            mRadioInfoFragment = RadioInfoFragment.getmInstance();
            fragmentTransaction.add(R.id.layout_play,mRadioInfoFragment);
            fragmentTransaction.commit();
        }else {
            mRadioInfoFragment.updateView();
            mRadioInfoFragment.updateListView();
        }
    }

    private boolean isSendService = true;
    private static final String RADIO_UN_MUTE_FLAG = "0";
    @Override
    public synchronized void onResume() {
        super.onResume();
        Log.d(RadioAPPLog.TAG,TAG+  " onResume: start ");
        RadioDataMng.getmInstance().isBackStage = false;
        if (RADIO_UN_MUTE_FLAG.equals(SystemPropertiesPresenter.getInstance().getProperty(SYSTEM_PROPERTY_SET_MCU_MUTE_KEY,RADIO_UN_MUTE_FLAG))) {
            try {
                Log.d(RadioAPPLog.TAG, TAG + " onResume: isSendService =  " + isSendService+ " RadioDataMng.getmInstance().stringBand"+ RadioDataMng.getmInstance().stringBand);
                if (isSendService) {
                    RadioDataMng.getmInstance().setMcuBandAndFreq(RadioDataMng.getmInstance().stringBand);
                }else {
                    notifyUI();
                }
                RadioManager.getRadioManager().requestPlay();
            } catch (RemoteException | NullPointerException e) {
                Log.e(RadioAPPLog.TAG, TAG + " onResume: e :" + e.toString());
            }
        }else {
            notifyUI();
        }
        initFragments();
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(RadioAPPLog.TAG,TAG+  " onStart: ");
        cleanMuteFlag();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        overridePendingTransition(0,0);
        super.onPause();
        Log.d(RadioAPPLog.TAG,TAG+  " onPause: ");
    }

    private static final String MAP = "map";
    private static final String SOURCE_TYPE = "SourceType";
    private static final String FROM = "From";
    private static final String KEY_MODE = "KEY_MODE";
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(RadioAPPLog.TAG,TAG+  " onNewIntent: intent = "+intent);
        if (intent != null)
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                Map map = (HashMap)bundle.get(MAP);
                if (map != null)
                {
                    isSendService = false;
                    /**
                     * 获取SourceType
                     */
                    String sourceType = (String)map.get(SOURCE_TYPE);
                    Log.d(RadioAPPLog.TAG,TAG+  " onNewIntent: sourceType = "+sourceType);
                    /**
                     * 获取From来源
                     */
                    String from = (String)map.get(FROM);
                    Log.d(RadioAPPLog.TAG,TAG+  " onNewIntent: from = "+from);
                    if (KEY_MODE.equals(from)){
                        setChangeBand(sourceType);
                        keyModelSend = true;
                    }
                    Log.d(RadioAPPLog.TAG,TAG+  " onNewIntent: isSendService =  "+isSendService);
                }
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        RadioDataMng.getmInstance().isBackStage = true;
        /**
         * 恢复默认值
         */
        isSendService = true;
        keyModelSend = false;
        Log.i(RadioAPPLog.TAG,TAG+  " onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RadioDataMng.getmInstance().unRegisterService();
        RadioDataMng.getmInstance().unregisterShareDataListener();
        Log.d(RadioAPPLog.TAG,TAG+  " onDestroy: ");
    }

    private void setChangeBand(String value){
        Log.d(RadioAPPLog.TAG,TAG+  " setChangeBand: value = "+value);
        int ret = -1;
        switch (value) {
            case AdayoSource.ADAYO_SOURCE_RADIO_FM:
                try {
                    ret = RadioManager.getRadioManager().setBandAndFreq(BAND_FM, (int) (RadioDataMng.getmInstance().getLastFMFreq() * 100));
                    Log.d(RadioAPPLog.TAG,TAG+  " setChangeBand: ADAYO_SOURCE_RADIO_FM ret = "+ret);
                    RadioDataMng.getmInstance().mBand = BAND_FM;
                } catch (RemoteException | NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case ADAYO_SOURCE_RADIO_AM:
                try {
                    ret = RadioManager.getRadioManager().setBandAndFreq(BAND_AM, RadioDataMng.getmInstance().getLastAMFreq());
                    Log.d(RadioAPPLog.TAG,TAG+  " setChangeBand: ADAYO_SOURCE_RADIO_AM ret = "+ret);
                    RadioDataMng.getmInstance().mBand = BAND_AM;
                } catch (RemoteException | NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private static final String STREAM_TYPE = "StreamType";
    private static final int MEDIA_NUM = 3;
    private static final int BT_NUM = 15;
    private void cleanMuteFlag(){
        /**
         * 通过ShareInfo获取当前媒体源,解决1145971
         */
        String data = ShareDataManager.getShareDataManager().getShareData(14);
        Log.d(RadioAPPLog.TAG, TAG + " cleanMuteFlag: data = "+data);
        JSONObject obj = null;
        try {
            obj = new JSONObject(data);
            Log.d(RadioAPPLog.TAG, TAG + " cleanMuteFlag: obj = "+obj);
            if (obj != null && obj.has(STREAM_TYPE)){
                if (MEDIA_NUM == obj.getInt(STREAM_TYPE)
                        || BT_NUM == obj.getInt(STREAM_TYPE)){
                    SystemPropertiesPresenter.getInstance().setProperty(SYSTEM_PROPERTY_SET_MCU_MUTE_KEY,"0");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notifyUI(){
        String currentUID = SrcMngSwitchManager.getInstance().getCurrentUID();
        Log.d(RadioAPPLog.TAG, TAG + " notifyUI: isSendService = "+ isSendService+" mBand = "+RadioDataMng.getmInstance().mBand+" getCurrentUID = "+currentUID);
        Log.d(RadioAPPLog.TAG, TAG + " notifyUI: keyModelSend = "+ keyModelSend);
        if (isSendService || !keyModelSend) {
            if (RadioDataMng.getmInstance().mBand == BAND_FM
                    && !ADAYO_SOURCE_RADIO_FM.equals(currentUID)) {
                SrcMngSwitchManager.getInstance().notifyServiceUIChange(ADAYO_SOURCE_RADIO_FM, getApplicationContext().getApplicationContext().getPackageName());
                SystemPropertiesPresenter.getInstance().setProperty(SYSTEM_PROPERTY_SET_RADIO_FOCUS_KEY,ADAYO_SOURCE_RADIO_FM);
                RadioDataMng.getmInstance().stringBand = BAND_FM_TEXT;
            } else if (RadioDataMng.getmInstance().mBand == BAND_AM
                    && !ADAYO_SOURCE_RADIO_AM.equals(currentUID)) {
                SrcMngSwitchManager.getInstance().notifyServiceUIChange(ADAYO_SOURCE_RADIO_AM, getApplicationContext().getApplicationContext().getPackageName());
                SystemPropertiesPresenter.getInstance().setProperty(SYSTEM_PROPERTY_SET_RADIO_FOCUS_KEY,ADAYO_SOURCE_RADIO_AM);
                RadioDataMng.getmInstance().stringBand = BAND_AM_TEXT;
            } else {
                Log.d(RadioAPPLog.TAG, TAG + " notifyUI: do Nothing");
            }
        }
    }
}
