package com.adayo.app.setting.wifi;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.WorkerThread;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.R;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.bpresenter.settings.contracts.IWLANContract;
import com.adayo.bpresenter.settings.presenters.WLANPresenter;
import com.adayo.common.settings.bean.WifiInfoBean;
import com.adayo.common.settings.constant.EnumConstant;
import com.lt.library.base.livedata.UnPeekLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_NO_SAVE_ITEM;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_NO_WLAN_ITEM;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_SAVE_ITEM;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI_UI_STATE_WLAN_ITEM;
import static com.adayo.common.settings.constant.EnumConstant.WIFI_STATE.CONNECT;
import static com.adayo.common.settings.constant.EnumConstant.WIFI_STATE.CONNECTING;
import static com.adayo.common.settings.constant.EnumConstant.WIFI_STATE.SAVED;


public class WifiRequest extends BaseRequest {
    private final static String TAG = WifiRequest.class.getSimpleName();
    private final MutableLiveData<Integer> mWifiEnableStateLiveData = new MutableLiveData<>();private final MutableLiveData<List<WifiInfoBean>> mWifiInfoListLiveData = new MutableLiveData<>();private final MutableLiveData<List<WifiInfoBean>> mWifiInfoLSaveistLiveData = new MutableLiveData<>();private List<WifiInfoBean> mSaveList = new ArrayList();
    private List<WifiInfoBean> mWifiList = new ArrayList();
    private final MutableLiveData<String> mpasswordError = new MutableLiveData<>();
    private final MutableLiveData<Integer> mWifiUISAVEState = new MutableLiveData<>();private final MutableLiveData<Integer> mWifiUIWLANState = new MutableLiveData<>();private final MutableLiveData<Boolean> mWifiInfoListSearchLiveData = new MutableLiveData<>();private final MutableLiveData<Boolean> mWifiConnectFail = new MutableLiveData<>();private final UnPeekLiveData<WifiInfoBean> mWifiConnectingLiveData = new UnPeekLiveData.Builder<WifiInfoBean>().create();private final UnPeekLiveData<WifiInfoBean> mWifiConnCfmLiveData = new UnPeekLiveData.Builder<WifiInfoBean>().create();private final UnPeekLiveData<WifiInfoBean> mWifiReconnCfmLiveData = new UnPeekLiveData.Builder<WifiInfoBean>().create();private final UnPeekLiveData<WifiInfoBean> mWifiDisconnCfmLiveData = new UnPeekLiveData.Builder<WifiInfoBean>().create();private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            mWifiConnectFail.setValue(true);
        }
    };


    private final IWLANContract.IWLANView mIWLANView = new IWLANContract.IWLANView() {

        @Override
        public void updateWifiState(int wifiState) {
            LogUtil.d(TAG, "wifiState = " + wifiState);
            Integer value = getWifiEnableStateLiveData().getValue();if (Objects.isNull(value) || value != wifiState) {
                switch (value) {
                    case WifiManager.WIFI_STATE_DISABLING:case WifiManager.WIFI_STATE_DISABLED:mSaveList.clear();
                        mWifiList.clear();
                        mWifiInfoLSaveistLiveData.setValue(mSaveList);
                        mWifiInfoListLiveData.setValue(mWifiList);
                        mWifiEnableStateLiveData.setValue(wifiState);
                        mWifiUISAVEState.setValue(WIFI_UI_STATE_NO_SAVE_ITEM);
                        mWifiUIWLANState.setValue(WIFI_UI_STATE_NO_WLAN_ITEM);
                        return;
                    default:
                        break;
                }
                mWifiEnableStateLiveData.setValue(wifiState);
            }
        }


        @Override
        public void updateAvailableNetworks(List<WifiInfoBean> list) {
            LogUtil.d(TAG, "list = " + list);
            mSaveList.clear();
            mWifiList.clear();
            for (WifiInfoBean nlist : list) {
                EnumConstant.WIFI_STATE wifiState = nlist.getState();
                LogUtil.debugD(TAG, "WIFI_STATE = " + wifiState);
                if (wifiState == SAVED || wifiState == CONNECT || wifiState == CONNECTING) {
                    LogUtil.debugD(TAG, "nlist = " + nlist);
                    mSaveList.add(nlist);
                } else {
                    mWifiList.add(nlist);
                }
            }
            if (mSaveList.size() == 0) {
                mWifiUISAVEState.setValue(WIFI_UI_STATE_NO_SAVE_ITEM);
            } else {
                mWifiUISAVEState.setValue(WIFI_UI_STATE_SAVE_ITEM);
            }
            if (mWifiList.size() == 0) {
                mWifiUIWLANState.setValue(WIFI_UI_STATE_NO_WLAN_ITEM);
            } else {
                mWifiUIWLANState.setValue(WIFI_UI_STATE_WLAN_ITEM);
            }

            mWifiInfoLSaveistLiveData.setValue(mSaveList);
            mWifiInfoListLiveData.setValue(mWifiList);
        }


        @Override
        public void showHasConnectedDialog(WifiInfoBean wifiInfoBean) {
            LogUtil.debugD(TAG, "wifiInfoBean = " + wifiInfoBean);
            mWifiReconnCfmLiveData.setValue(wifiInfoBean);
        }



        @Override
        public void showNotHasConnectedDialog(WifiInfoBean wifiInfoBean, boolean isNeedPwd) {
            LogUtil.i(TAG, "wifiInfoBean = " + wifiInfoBean + ", isNeedPwd = " + isNeedPwd);
            if (isNeedPwd) {
                mWifiConnCfmLiveData.setValue(wifiInfoBean);
            } else {
                WLANPresenter.getInstance(getAppContext()).connectWiFi(wifiInfoBean);}
        }


        @Override
        public void showCancelConnectDialog(WifiInfoBean wifiInfoBean) {
            LogUtil.i(TAG, "wifiInfoBean = " + wifiInfoBean);
            mWifiDisconnCfmLiveData.setValue(wifiInfoBean);
        }

        @WorkerThread
        @Override
        public void onWifiConnecting() {

        }


        @Override
        public void onPasswordError(WifiInfoBean wifiInfoBean) {
            LogUtil.i(TAG, "onPasswordError wifiInfoBean = " + wifiInfoBean.getScanResult().SSID);
            mWifiConnectingLiveData.setValue(null);
            mpasswordError.setValue("\"" + getLenStr(wifiInfoBean.getScanResult().SSID, 6) + "\"" + getAppContext().getString(R.string.toast_hint_wifi_password_err));
            WLANPresenter.getInstance(getAppContext()).forget(wifiInfoBean);
            mWifiConnectingLiveData.postValue(null);
            mHandler.removeMessages(1);
        }

        public String getLenStr(String str, int len) {
            String returnStr = "";
            int str_length = 0;
            int str_len = 0;
            String str_cut = new String();
            str_len = str.length();
            for (int i = 0; i < str_len; i++) {
                char a = str.charAt(i);
                str_length++;
                if (escape(a + "").length() > 4) {
                    str_length++;
                }
                str_cut = str_cut.concat(a + "");
                if (str_length >= len) {
                    str_cut = str_cut.concat("...");
                    returnStr = str_cut;
                    break;
                }
            }
            if (str_length < len) {
                returnStr = str;
            }
            return returnStr;
        }

        private String escape(String src) {
            int i;
            char j;
            StringBuffer tmp = new StringBuffer();
            tmp.ensureCapacity(src.length() * 6);
            for (i = 0; i < src.length(); i++) {
                j = src.charAt(i);
                if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j)) {
                    tmp.append(j);
                } else if (j < 256) {
                    tmp.append("%");
                    if (j < 16) {
                        tmp.append("0");
                    }
                    tmp.append(Integer.toString(j, 16));
                } else {
                    tmp.append("%u");
                    tmp.append(Integer.toString(j, 16));
                }
            }
            return tmp.toString();
        }


        @Override
        public void onWifiConnectingFailed() {
            LogUtil.i(TAG, "");
            mWifiConnectingLiveData.setValue(null);
        }


        @Override
        public void onWifiConnectSuccess(String ssid, String bssid) {
            LogUtil.i(TAG, "ssid = " + ssid + ", bssid = " + bssid);
            mWifiConnectingLiveData.postValue(null);
            mHandler.removeMessages(1);
        }

        @WorkerThread
        @Override

        public void onWifiSearching() {
            LogUtil.debugD(TAG, "");
            mWifiInfoListSearchLiveData.postValue(true);
        }


        @Override
        public void onWifiSearchFinish() {
            LogUtil.debugD(TAG, "");
            mWifiInfoListSearchLiveData.setValue(false);
        }
    };



    public void requestEnableState(boolean enable) {
        LogUtil.i(TAG, "enable =" + enable);
        WifiManager wifiManager = (WifiManager) getAppContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState();switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLED:if (enable) {
                    WLANPresenter.getInstance(getAppContext()).setWiFiSwitch(true);
                }
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                if (!enable) {
                    WLANPresenter.getInstance(getAppContext()).setWiFiSwitch(false);
                }
                break;
            default:
                break;
        }
    }


    public void requestSearch() {
        LogUtil.debugD(TAG, "");
        WLANPresenter.getInstance(getAppContext()).searchAvailableNetworks();
    }


    public void requestConnect(WifiInfoBean wifiInfoBean) {
        LogUtil.d(TAG, "wifiInfoBean =" + wifiInfoBean);
        mHandler.removeMessages(1);
        Message message = new Message();
        message.what = 1;
        mHandler.sendMessageDelayed(message, 10000);
        mWifiConnectingLiveData.setValue(wifiInfoBean);
        WLANPresenter.getInstance(getAppContext()).connectWiFi(wifiInfoBean);

    }


    public void requestConnect(String password, WifiInfoBean wifiInfoBean) {
        LogUtil.i(TAG, "password =" + password);
        mHandler.removeMessages(1);
        Message message = new Message();
        message.what = 1;
        mHandler.sendMessageDelayed(message, 10000);
        mWifiConnectingLiveData.setValue(wifiInfoBean);
        WLANPresenter.getInstance(getAppContext()).connectWiFi(password, wifiInfoBean);
    }


    public void requestDisconnect() {
        LogUtil.debugD(TAG, "");
        WLANPresenter.getInstance(getAppContext()).disconnectWifi();
    }


    public void requestForget(WifiInfoBean wifiInfoBean) {
        LogUtil.debugD(TAG, "");
        WLANPresenter.getInstance(getAppContext()).forget(wifiInfoBean);
    }


    public void requestSaveSelect(WifiInfoBean wifiInfoBean) {
        for (int i = 0; i < mSaveList.size(); i++) {
            WifiInfoBean bean = mSaveList.get(i);
            if (bean == wifiInfoBean) {
                LogUtil.d(TAG, "I =" + i);
                WLANPresenter.getInstance(getAppContext()).selectWiFi(i);
            }
        }
    }


    public void requestSelect(int position) {
        LogUtil.debugD(TAG, "position =" + (position + mSaveList.size()));
        WLANPresenter.getInstance(getAppContext()).selectWiFi(position + mSaveList.size());
    }



    public void init() {
        LogUtil.debugD(TAG, "");
        WLANPresenter.getInstance(getAppContext()).setView(mIWLANView);
        WLANPresenter.getInstance(getAppContext()).init();
        WLANPresenter.getInstance(getAppContext()).update();


    }

    public void unInit() {
        LogUtil.debugD(TAG, "");
        WLANPresenter.getInstance(getAppContext()).removeView(mIWLANView);
        mHandler.removeMessages(1);
        mHandler = null;
    }

    public LiveData<Integer> getWifiEnableStateLiveData() {
        if (mWifiEnableStateLiveData.getValue() == null) {
            mWifiEnableStateLiveData.setValue(0);
        }
        return mWifiEnableStateLiveData;
    }

    public LiveData<List<WifiInfoBean>> getWifiInfoListLiveData() {
        if (mWifiInfoListLiveData.getValue() == null) {
            mWifiInfoListLiveData.setValue(null);
        }
        return mWifiInfoListLiveData;
    }

    public LiveData<List<WifiInfoBean>> getWifiInfoSaveListLiveData() {
        if (mWifiInfoLSaveistLiveData.getValue() == null) {
            mWifiInfoLSaveistLiveData.setValue(null);
        }
        return mWifiInfoLSaveistLiveData;
    }

    public LiveData<Boolean> getWifiInfoListSearchLiveData() {
        if (mWifiInfoListSearchLiveData.getValue() == null) {
            mWifiInfoListSearchLiveData.setValue(false);
        }
        return mWifiInfoListSearchLiveData;
    }


    public LiveData<WifiInfoBean> getWifiConnectingLiveData() {
        if (mWifiConnectingLiveData.getValue() == null) {
            mWifiConnectingLiveData.setValue(null);
        }
        return mWifiConnectingLiveData;
    }


    public LiveData<WifiInfoBean> getWifiConnCfmLiveData() {
        if (mWifiConnCfmLiveData.getValue() == null) {
            mWifiConnCfmLiveData.setValue(null);
        }
        return mWifiConnCfmLiveData;
    }

    public LiveData<WifiInfoBean> getWifiReconnCfmLiveData() {
        if (mWifiReconnCfmLiveData.getValue() == null) {
            mWifiReconnCfmLiveData.setValue(null);
        }
        return mWifiReconnCfmLiveData;
    }


    public LiveData<WifiInfoBean> getWifiDisconnCfmLiveData() {
        if (mWifiDisconnCfmLiveData.getValue() == null) {
            mWifiDisconnCfmLiveData.setValue(null);
        }
        return mWifiDisconnCfmLiveData;
    }


    public MutableLiveData<String> getMpasswordError() {
        if (mpasswordError.getValue() == null) {
            LogUtil.w(TAG, "mpasswordError = null");
            mpasswordError.setValue(null);
        }
        return mpasswordError;
    }

    public MutableLiveData<Integer> getWifiUISAVEState() {
        if (mWifiUISAVEState.getValue() == null) {
            mWifiUISAVEState.setValue(0);
        }
        return mWifiUISAVEState;
    }

    public MutableLiveData<Integer> getWifiUIWLANState() {
        if (mWifiUIWLANState.getValue() == null) {
            mWifiUIWLANState.setValue(0);
        }
        return mWifiUIWLANState;
    }

    public MutableLiveData<Boolean> getWifiConnectFail() {
        if (mWifiConnectFail.getValue() == null) {
            mWifiConnectFail.setValue(false);
        }
        return mWifiConnectFail;
    }

    public Handler getHandler() {
        return mHandler;
    }
}
