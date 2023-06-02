package com.adayo.app.radio.ui.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.app.radio.handler.RadioHandler;
import com.adayo.app.radio.ui.fragment.RadioInfoFragment;
import com.adayo.app.radio.utils.RadioAPPLog;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.tuner.radio.RadioCollectionFreq;
import com.adayo.proxy.tuner.radio.RadioInfo;
import com.adayo.proxy.tuner.radio.RadioManager;
import com.adayo.proxy.tuner.radio.aidl.IAudioFocusChangeListener;
import com.adayo.proxy.tuner.radio.aidl.ICollectionChangeListener;
import com.adayo.proxy.tuner.radio.aidl.IRadioInfoChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.app.radio.constant.Constants.BAND_AM;
import static com.adayo.app.radio.constant.Constants.BAND_AM_TEXT;
import static com.adayo.app.radio.constant.Constants.BAND_FM;
import static com.adayo.app.radio.constant.Constants.BAND_FM_TEXT;
import static com.adayo.app.radio.constant.Constants.ISMUTE;
import static com.adayo.app.radio.constant.Constants.RADIO_CALLBACK_SEARCHSTATUS_0;
import static com.adayo.app.radio.constant.Constants.RADIO_CALLBACK_SEARCHSTATUS_1;
import static com.adayo.app.radio.constant.Constants.RADIO_CALLBACK_UPDATEVIEW_AND_LIST;
import static com.adayo.app.radio.constant.Constants.RADIO_CLEAR_LIST;
import static com.adayo.app.radio.constant.Constants.RADIO_CLEAR_LIST_AND_SEARCH;
import static com.adayo.app.radio.constant.Constants.RADIO_THREADGETLIST_UPDATECOLLECTLIST;
import static com.adayo.app.radio.constant.Constants.RADIO_THREADGETLIST_UPDATEVIEW;
import static com.adayo.app.radio.constant.Constants.SEARCHTYPE_MINIMUM;

/**
 * @author ADAYO-06
 */
public class RadioDataMng {
    private static final String TAG = "RadioDataMng";
    private Context mContext;
    private RadioHandler mHandler;
    private volatile static RadioDataMng mInstance = null;
    public int mBand = BAND_FM;
    public String  stringBand = BAND_FM_TEXT;
    public int callBackFreq ;
    public int lastAMFreq = 531;
    public double lastFMFreq = 87.5;
    public List<RadioInfo> mRadioSearchList;
    public List<RadioInfo> mRadioAMSearchList;
    public List<RadioInfo> mRadioFMSearchList;
    public List<RadioCollectionFreq> mRadioCollectionList;
    public int isSearch;
    private int retInfo =1;
    public int isPlay = ISMUTE;
    public boolean isDeleteItem = false;
    private int isFirstSearch =0;
    public boolean isBackStage = false;
    public int setOff = 0;
    private Handler handler = new Handler();

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
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void radioInfoContent(RadioInfo radioInfo) throws RemoteException {
            Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: radioInfo = "+radioInfo);
            int changeBand = radioInfo.getBand();
            if (BAND_AM == radioInfo.getBand()){
                callBackFreq = radioInfo.getFreq();
                lastAMFreq = callBackFreq;
                Log.d(RadioAPPLog.TAG,TAG+  " radioInfoContent: lastAMFreq = "+lastAMFreq);
            }else {
                callBackFreq = radioInfo.getFreq();
                lastFMFreq = (double) callBackFreq/ 100;
                Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: lastFMFreq = "+lastFMFreq);
            }
            int currentPlayStata = radioInfo.getMcuMuteState();
            isSearch = radioInfo.getSearchStatus();
            Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: isSearch = "+isSearch);

            if (isSearch == RADIO_CALLBACK_SEARCHSTATUS_0){
                //非搜索状态
                isFirstSearch = 0;
                RadioInfoFragment.getmInstance().setmIsSearch(false);
            }else if (isSearch == RADIO_CALLBACK_SEARCHSTATUS_1){
                //语音控制搜索电台时，清除电台列表
                isFirstSearch ++;
                RadioInfoFragment.getmInstance().setmIsSearch(true);
                Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: isFirstSearch = "+isFirstSearch);
                if (isFirstSearch == 1 && RadioInfoFragment.getmInstance().mRadioSearchInfoList.size() > 0){
                    clearSearchList();
                    //延迟
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                RadioInfoFragment.getmInstance().setmIsSearch(true);
            }
            Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: mBand = "+mBand);
            Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: changeBand = "+changeBand);
            Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: stringBand = "+stringBand);

            if ((lastAMFreq != RadioInfoFragment.getmInstance().mAMFreq)
                    ||(lastFMFreq != RadioInfoFragment.getmInstance().mFMFreq)
                    || mBand != changeBand
                    || isPlay != currentPlayStata
                    ||!stringBand.equals(changeBand == BAND_FM ? BAND_FM_TEXT:BAND_AM_TEXT)){
                isPlay = currentPlayStata;
                if (mRadioAMSearchList == null ){
                    mRadioAMSearchList = new ArrayList<>();
                }

                if (mRadioFMSearchList == null ){
                    mRadioFMSearchList = new ArrayList<>();
                }

                if (!stringBand.equals(changeBand == BAND_FM ? BAND_FM_TEXT:BAND_AM_TEXT)){
                    RadioInfoFragment.getmInstance().currenBand = (changeBand == BAND_FM ? BAND_FM_TEXT:BAND_AM_TEXT);
                    clearSearchList();
                }
                mBand = changeBand;
                if (changeBand == BAND_AM){
                    stringBand = BAND_AM_TEXT;
                    mRadioAMSearchList = RadioManager.getRadioManager().getPresetList();
                    Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: mRadioAMSearchList = "+mRadioAMSearchList);
                }else {
                    stringBand = BAND_FM_TEXT;
                    mRadioFMSearchList = RadioManager.getRadioManager().getPresetList();
                    Log.d(RadioAPPLog.TAG, TAG+  " radioInfoContent: mRadioFMSearchList = "+mRadioFMSearchList);
                }
                Message msg = mHandler.obtainMessage();
                msg.what = RADIO_CALLBACK_UPDATEVIEW_AND_LIST;
                mHandler.sendMessage(msg);
            }

        }
    };

    private ICollectionChangeListener.Stub mICollectionChangeListener = new ICollectionChangeListener.Stub() {
        @Override
        public void onCollectListChanger(List<RadioCollectionFreq> list) throws RemoteException {
            Log.d(RadioAPPLog.TAG, TAG+  " mICollectionChangeListener: onCollectListChanger = "+list);
            if (mRadioCollectionList == null){
                mRadioCollectionList = new ArrayList<>();
            }
            mRadioCollectionList = list;
            Log.d(RadioAPPLog.TAG, TAG+  " mICollectionChangeListener: mRadioCollectionList"+mRadioCollectionList);
            Message msg = mHandler.obtainMessage();
            msg.what = RADIO_THREADGETLIST_UPDATECOLLECTLIST;
            mHandler.sendMessage(msg);
        }

        @Override
        public void isCollectionSuccess(boolean b) throws RemoteException {
            Log.d(RadioAPPLog.TAG, TAG+  " mICollectionChangeListener: isCollectionSuccess = "+b);
        }

        @Override
        public void isDeleteSuccess(boolean b) throws RemoteException {
            Log.d(RadioAPPLog.TAG, TAG+  " mICollectionChangeListener: isDeleteSuccess = "+b);
        }

        @Override
        public void isUpdateSuccess(boolean b) throws RemoteException {
            Log.d(RadioAPPLog.TAG, TAG+  " mICollectionChangeListener: isUpdateSuccess = "+b);
        }

        @Override
        public void onCurrentCollectionChange(boolean b) throws RemoteException {
            Log.d(RadioAPPLog.TAG, TAG+  " mICollectionChangeListener: onCurrentCollectionChange = "+b);
        }
    };

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

    public void init(Context context){
        mContext = context;
        mHandler = new RadioHandler(mContext);
        registerShareDataListener();
//        mRadioSearchList = new ArrayList<>();
//        mRadioCollectionList = new ArrayList<>();
    }

    public void registerService(){
        try {
            retInfo = RadioManager.getRadioManager().registerRadioInfoChangeListener(mIRadioInfoChangeListener);
            Log.d(RadioAPPLog.TAG, TAG+  " registerService: retInfo = "+retInfo);
            int retCollection = RadioManager.getRadioManager().registerCollectionChangeListener(mICollectionChangeListener);
            //音频焦点
            int retAudioFocus = RadioManager.getRadioManager().registerAudioFocusChangeListener(mIAudioFocusChangeListener);
            Log.d(RadioAPPLog.TAG, TAG+  " registerService: retCollection:"+retCollection +" retAudioFocus:" +retAudioFocus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " registerService: NullPointerException ", e);
        }
    }

    public void unRegisterService(){
        try {
            int retUnRegisterInfo = RadioManager.getRadioManager().unRegisterRadioInfoChangeListener(mIRadioInfoChangeListener);
            int retUnRegisterCollection = RadioManager.getRadioManager().unRegisterCollectionChangeListener(mICollectionChangeListener);
            //音频焦点
            int retUnRegisterAudioFocus = RadioManager.getRadioManager().unRegisterAudioFocusChangeListener(mIAudioFocusChangeListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " unRegisterService: NullPointerException ", e);
        }
    }

    public void getRadioData(String currBand){
        try {
            Log.d(RadioAPPLog.TAG, TAG+  " getRadioData: currBand:"+currBand);
            isPlay = RadioManager.getRadioManager().getMcuMuteState();
            if (AdayoSource.ADAYO_SOURCE_RADIO_AM.equals(currBand)){
                lastAMFreq = getLastAMFreq();
            }else if (AdayoSource.ADAYO_SOURCE_RADIO_FM.equals(currBand)){
                lastFMFreq = getLastFMFreq();
            }else {
                mBand = RadioManager.getRadioManager().getBand();
                Log.d(RadioAPPLog.TAG, TAG+  " getRadioData currBand is not FM/AM getRadioData: mBand:"+mBand);
                if (mBand == BAND_AM){
                    lastAMFreq = getLastAMFreq();
                    Log.d(RadioAPPLog.TAG, TAG+  " getRadioData currBand is not FM/AM getRadioData: lastAMFreq:"+lastAMFreq);
                }else if (mBand == BAND_FM){
                    lastFMFreq = getLastFMFreq();
                    Log.d(RadioAPPLog.TAG, TAG+  " getRadioData currBand is not FM/AM getRadioData: lastFMFreq:"+lastFMFreq);
                }
            }
            GetListRunnable runnable = new GetListRunnable();
            handler.post(runnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " getRadioData: NullPointerException ", e);
        }
    }

    /**
     * 获取List数据源
     */
    private class GetListRunnable implements Runnable{
        @SuppressLint("WrongConstant")
        @Override
        public void run() {
            try {
                if (mBand == BAND_AM){
                    mRadioAMSearchList = RadioManager.getRadioManager().getPresetList();
                    Log.d(RadioAPPLog.TAG, TAG+  " GetListRunnable: mRadioAMSearchList:"+mRadioAMSearchList);
                }else{
                    mRadioFMSearchList = RadioManager.getRadioManager().getPresetList();
                    Log.d(RadioAPPLog.TAG, TAG+  " GetListRunnable: mRadioFMSearchList:"+mRadioFMSearchList);
                }
                mRadioCollectionList = RadioManager.getRadioManager().getCollectionList();
                Log.d(RadioAPPLog.TAG, TAG+  " GetListRunnable: mRadioCollectionList:"+mRadioCollectionList);
                Message msg = mHandler.obtainMessage();
                msg.what = RADIO_THREADGETLIST_UPDATEVIEW;
                mHandler.sendMessage(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public int getLastAMFreq(){
        try {
            lastAMFreq = RadioManager.getRadioManager().getLastAMFreq();
            Log.d(RadioAPPLog.TAG, TAG+  " getLastAMFreq: lastAMFreq:"+lastAMFreq);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " getLastAMFreq: NullPointerException ", e);
        }
        return lastAMFreq;
    }

    public double getLastFMFreq(){
        try {
//            DecimalFormat df = new DecimalFormat("0.0");
            lastFMFreq = (double) RadioManager.getRadioManager().getLastFMFreq()/ 100;
            Log.d(RadioAPPLog.TAG, TAG+  " getLastAMFreq: lastFMFreq:"+lastFMFreq);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " getLastFMFreq: NullPointerException ", e);
        }
        return lastFMFreq;
    }

    public int setInsertCollectionFreq(int band, int freq){
        Log.d(RadioAPPLog.TAG, TAG + " setInsertCollectionFreq: start band:"+band +" freq:"+freq);
        int ret = -1;
        try {
            RadioCollectionFreq insertCollectionFreq = new RadioCollectionFreq();
            insertCollectionFreq.setFreq(freq);
            insertCollectionFreq.setBand(band);
            insertCollectionFreq.setId(1L);
            ret =RadioManager.getRadioManager().insertCollectionFreq(insertCollectionFreq);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " setInsertCollectionFreq: NullPointerException ", e);
        }
        Log.d(RadioAPPLog.TAG, TAG + " setInsertCollectionFreq: end ");
        return ret;
    }

    public int setDeleteCollectionFreq(int band, int freq){
        Log.d(RadioAPPLog.TAG, TAG + " setDeleteCollectionFreq: start band:"+band +" freq:"+freq);
        int ret = -1;
        try {
            RadioCollectionFreq deleteCollectionFreq = new RadioCollectionFreq();
            deleteCollectionFreq.setFreq(freq);
            deleteCollectionFreq.setBand(band);
            deleteCollectionFreq.setId(1L);
            ret =RadioManager.getRadioManager().deleteCollectionRadioInfo(deleteCollectionFreq);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " setDeleteCollectionFreq: NullPointerException ", e);
        }
        Log.d(RadioAPPLog.TAG, TAG + " setDeleteCollectionFreq: end ");
        return ret;
    }

    private void clearSearchList(){
        Log.d(RadioAPPLog.TAG, TAG+  " clearSearchList: start");
        mRadioAMSearchList.clear();
        mRadioFMSearchList.clear();
        Message msg = mHandler.obtainMessage();
        msg.what = RADIO_CLEAR_LIST;
        mHandler.sendMessage(msg);
        try {
            //为了更新UI的动画效果
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearSearchListAndAnime(){
        Log.d(RadioAPPLog.TAG, TAG+  " clearSearchListAndAnime: start");
        mRadioAMSearchList.clear();
        mRadioFMSearchList.clear();
        Message msg = mHandler.obtainMessage();
        msg.what = RADIO_CLEAR_LIST;
        mHandler.sendMessage(msg);
    }

    public void setMcuBandAndFreq(String currBand){
        int ret = -1;
        try {
            if (AdayoSource.ADAYO_SOURCE_RADIO_AM.equals(currBand)){
                ret = RadioManager.getRadioManager().setBandAndFreq(BAND_AM, lastAMFreq);
                Log.d(RadioAPPLog.TAG, TAG+  " setMcuBandAndFreq: AM ret:"+ret);
            }else if (AdayoSource.ADAYO_SOURCE_RADIO_FM.equals(currBand)){
                ret = RadioManager.getRadioManager().setBandAndFreq(BAND_FM, (int) (lastFMFreq* 100));
                Log.d(RadioAPPLog.TAG, TAG+  " setMcuBandAndFreq: FM ret:"+ret);
            }else {
                Log.d(RadioAPPLog.TAG, TAG+  " setMcuBandAndFreq currBand is not FM/AM getRadioData: mBand:"+mBand);
                if (mBand == BAND_AM){
                    Log.d(RadioAPPLog.TAG, TAG+  " setMcuBandAndFreq currBand is not FM/AM getRadioData: lastAMFreq:"+lastAMFreq);
                    ret = RadioManager.getRadioManager().setBandAndFreq(BAND_AM, lastAMFreq);
                    Log.d(RadioAPPLog.TAG, TAG+  " setMcuBandAndFreq currBand is not FM/AM getRadioData: AM ret:"+ret);
                }else if (mBand == BAND_FM){
                    Log.d(RadioAPPLog.TAG, TAG+  " setMcuBandAndFreq currBand is not FM/AM getRadioData: lastFMFreq:"+lastFMFreq);
                    ret =RadioManager.getRadioManager().setBandAndFreq(BAND_FM, (int) (lastFMFreq* 100));
                    Log.d(RadioAPPLog.TAG, TAG+  " setMcuBandAndFreq currBand is not FM/AM getRadioData: FM ret:"+ret);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.e(RadioAPPLog.TAG, TAG+ " setMcuBandAndFreq: NullPointerException ", e);
        }

    }

    private IShareDataListener listener = null;
    private static final int USB_ATTACH_STATE = 17;
    private static final String IS_DEVICE = "isDevice";
    private void registerShareDataListener(){
        ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
        listener = new ShareInfoListener();
        shareDataManager.registerShareDataListener(USB_ATTACH_STATE, listener);
    }

    public void unregisterShareDataListener(){
        ShareDataManager shareDataManager = ShareDataManager.getShareDataManager();
        shareDataManager.unregisterShareDataListener(USB_ATTACH_STATE, listener);
    }

    public class ShareInfoListener implements IShareDataListener {
        public ShareInfoListener() {
        }
        @Override
        public void notifyShareData(int dataType, String data) {
            Log.d(RadioAPPLog.TAG, TAG+  " dataType: " + dataType + ", data: " + data);
            if (dataType == USB_ATTACH_STATE ){
                try {
                    JSONObject obj = new JSONObject(data);
                    if (obj == null){
                        return;
                    }
                    if (obj.getInt(IS_DEVICE) > 0 && isSearch != RADIO_CALLBACK_SEARCHSTATUS_0){
                        Log.d(RadioAPPLog.TAG, TAG+  " to stop Search");
                        RadioManager.getRadioManager().stopSearch();
                    }
                } catch (JSONException | RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
