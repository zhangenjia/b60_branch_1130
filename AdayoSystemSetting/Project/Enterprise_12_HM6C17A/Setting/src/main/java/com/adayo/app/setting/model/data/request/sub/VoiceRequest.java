package com.adayo.app.setting.model.data.request.sub;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.utils.FastJsonUtil;
import com.adayo.app.setting.utils.StringUtils;
import com.adayo.app.setting.utils.timer.DevTimer;
import com.adayo.function.IFunctionNotifyResultBase;
import com.adayo.module.servicecenterproxy.ServiceCenterManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;

import java.util.Map;

import static com.adayo.app.setting.model.constant.ParamConstant.VR_ACTION_GET_DUPLEX_ACTIVE_TIME;
import static com.adayo.app.setting.model.constant.ParamConstant.VR_ACTION_SET_DUPLEX_ACTIVE_TIME;
import static com.adayo.app.setting.model.constant.ParamConstant.VR_ACTION_SET_DUPLEX_ENABLED;


public class VoiceRequest extends BaseRequest {
    private final static String TAG = VoiceRequest.class.getSimpleName();
    private final MutableLiveData<Boolean> mWakeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mWakeWordLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mWakeWordWrongLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> mWakeRoleLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mWakeFreeEleLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsDuplexEnableLiveData = new MutableLiveData<>();private final MutableLiveData<String> mDuplexActiveTimeLiveData = new MutableLiveData<>();private final MutableLiveData<Boolean> mIsShowValidLiveData = new MutableLiveData<>();private final IShareDataListener mIShareDataInterface = (type, content) -> {
        LogUtil.i(TAG, "type = " + type + ", content = " + content);
        if (type == ParamConstant.SHARE_INFO_ID_WAKE) {
            parseWake(content);
            parseWakeRole(content);
            parseWakeWord(content);
            parseWakeFreeEle(content);
        }
    };
    private String mWakeWord;


    private void registerServiceCenter() {
        try {
            IFunctionNotifyResultBase functionNotifyResultBase = new IFunctionNotifyResultBase();
            functionNotifyResultBase.setFunctionNotifyResult((fromModuleName, function, timeStump, bundle) -> {
                if ("notifySetWakeWordResult".equals(function)) {
                    int result = bundle.getInt("result");
                    LogUtil.i(TAG, "WakeWord result = " + result + "bundle =" + bundle);
                    switch (result) {
                        case 0:
                            String errorMessage = bundle.getString("error");
                            errorMessage = StringUtils.replaceAll(errorMessage, "，", "，\n");
                            mWakeWordWrongLiveData.postValue(errorMessage);
                            LogUtil.i(TAG, "errorMessage = " + errorMessage);
                            break;
                        case 1:
                            mWakeWordLiveData.postValue(mWakeWord);
                            break;
                        default:
                            break;
                    }
                }
                return 0;
            });
            ServiceCenterManager.getInstance().registerNotifyHandler(AdayoSource.ADAYO_SOURCE_SETTING, functionNotifyResultBase);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unregisterServiceCenter() {
        LogUtil.i(TAG, "");
        ServiceCenterManager.getInstance().unRegisterNotifyHandler(AdayoSource.ADAYO_SOURCE_SETTING);
    }


    public void parseWake(String context) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(context);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            mWakeLiveData.setValue(false);
            return;
        }
        String Wake = (String) map.get(ParamConstant.SHARE_INFO_KEY_WAKE_SWITCH);
        LogUtil.i(TAG, "Wake =" + Wake);
        if (Wake == null) {
            LogUtil.w(TAG, "SHARE_INFO value is null");
            mWakeLiveData.setValue(false);
        } else if ("true".equals(Wake)) {
            mWakeLiveData.setValue(true);
        } else if ("false".equals(Wake)) {
            mWakeLiveData.setValue(false);
        } else {
            LogUtil.w(TAG, "SHARE_INFO value is illegal parameter");
            mWakeLiveData.setValue(false);
        }

    }


    public void parseWakeWord(String context) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(context);
        if (map == null) {
            LogUtil.w(TAG, "SHARE_INFO map is null");
            mWakeWordLiveData.setValue("");
            return;
        }
        String WakeWord = (String) map.get(ParamConstant.SHARE_INFO_KEY_WAKE_WORD);
        LogUtil.i(TAG, "WakeWord =" + WakeWord);
        if (WakeWord == null) {
            mWakeWordLiveData.setValue("");
        } else {
            mWakeWordLiveData.setValue(WakeWord);
        }

    }


    public void parseWakeRole(String content) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(content);
        if (map == null) {
            LogUtil.w(TAG, "SHARE_INFO map is null");
            mWakeRoleLiveData.setValue("bjbj");
            return;
        }
        String WakeRole = (String) map.get(ParamConstant.SHARE_INFO_KEY_WAKE_ROLE);
        LogUtil.i(TAG, "WakeRole =" + WakeRole);
        if (WakeRole == null) {
            LogUtil.w(TAG, "SHARE_INFO value is null");
            mWakeRoleLiveData.setValue("bjbj");
        } else {
            mWakeRoleLiveData.setValue(WakeRole);
        }
    }


    public void parseWakeFreeEle(String content) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(content);
        if (map == null) {
            LogUtil.w(TAG, "SHARE_INFO map is null");
            mWakeFreeEleLiveData.setValue(false);
            return;
        }
        String WakeFreeEle = (String) map.get(ParamConstant.SHARE_INFO_KEY_PARTIALLY_WAKEUP);
        LogUtil.i(TAG, "WakeFreeEle =" + WakeFreeEle);
        if (WakeFreeEle == null) {
            LogUtil.w(TAG, "SHARE_INFO value is null");
            mWakeFreeEleLiveData.setValue(false);
        } else if ("true".equals(WakeFreeEle)) {
            mWakeFreeEleLiveData.setValue(true);
        } else if ("false".equals(WakeFreeEle)) {
            mWakeFreeEleLiveData.setValue(false);
        } else {
            LogUtil.w(TAG, "SHARE_INFO value is illegal parameter");
            mWakeFreeEleLiveData.setValue(false);
        }
    }


    public void requestWake(boolean enable) {
        LogUtil.i(TAG, "enable = " + enable);
        Bundle bundle = new Bundle();
        bundle.putBoolean("enable", enable);
        try {
            int result = ServiceCenterManager.getInstance().invokeFunc(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR, "enableWakeup", 0L, bundle);
            LogUtil.i(TAG, "result = " + result);
            if (result == 1) {
                mWakeLiveData.setValue(enable);
            } else {
                mWakeLiveData.setValue(!enable);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void requestWakeWord(String wakeWord) {
        LogUtil.i(TAG, "wakeWord = " + wakeWord);
        Bundle bundle = new Bundle();
        bundle.putString("wakeUpWord", wakeWord);
        try {
            int result = ServiceCenterManager.getInstance().invokeFunc(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR, "makeCustomWakeupWords", 0L, bundle);
            LogUtil.i(TAG, "result = " + result);
            if (result == 1) {
                mWakeWord = wakeWord;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void requestWakeRole(String wakeRole) {
        LogUtil.i(TAG, "wakeRole = " + wakeRole);
        Bundle bundle = new Bundle();
        bundle.putString("roleID", wakeRole);
        try {
            int result = ServiceCenterManager.getInstance().invokeFunc(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR, "setRole", 0L, bundle);
            LogUtil.i(TAG, "result = " + result);
            if (result == 1) {
                mWakeRoleLiveData.setValue(wakeRole);
            } else {
                mWakeWordLiveData.setValue("");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void requestWakeFreeEle(boolean enable) {
        LogUtil.i(TAG, "enable = " + enable);
        Bundle bundle = new Bundle();
        bundle.putBoolean("enable", enable);
        try {
            int result = ServiceCenterManager.getInstance().invokeFunc(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR, "enablePartiallyWakeupWord", 0L, bundle);
            LogUtil.i(TAG, "result = " + result);
            if (result == 1) {
                mWakeFreeEleLiveData.setValue(enable);
            } else {
                mWakeFreeEleLiveData.setValue(!enable);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void initDuplexEnable() {
        boolean isDuplexEnable = false;
        try {
            Bundle bundle = ServiceCenterManager.getInstance().syncRequestGet(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR, ParamConstant.VR_ACTION_IS_DUPLEX_ENABLE, 0, null);
            isDuplexEnable = bundle.getBoolean("result");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if(isDuplexEnable){
            initDuplexActiveTime();
        }
        LogUtil.i(TAG,"isDuplexEnable = "+isDuplexEnable);
        mIsDuplexEnableLiveData.setValue(isDuplexEnable);
    }


    public void requestDuplexEnabled(boolean enable) {
        LogUtil.i(TAG,"enable = "+enable);
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("enable",enable);
            ServiceCenterManager.getInstance().invokeFunc(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR,VR_ACTION_SET_DUPLEX_ENABLED,0, bundle);
            mIsDuplexEnableLiveData.setValue(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




    public void initDuplexActiveTime() {
        String time = "15" ;
        try {
            Bundle bundle = ServiceCenterManager.getInstance().syncRequestGet(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR, VR_ACTION_GET_DUPLEX_ACTIVE_TIME, 0, null);
            time = bundle.getString("result");
            LogUtil.i(TAG,"mDuplexActiveTime = "+time);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mDuplexActiveTimeLiveData.setValue(time);
    }


    public void requestDuplexActiveTime(String time) {
        LogUtil.i(TAG,"time = "+time);
        try {
            Bundle bundle = new Bundle();
            bundle.putString("time",time);
            ServiceCenterManager.getInstance().invokeFunc(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR,VR_ACTION_SET_DUPLEX_ACTIVE_TIME,0, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
            initDuplexActiveTime();

        }

    }


    public void initShowValidResult() {
        boolean isShowValidResult = false;
        try {
            Bundle bundle = ServiceCenterManager.getInstance().syncRequestGet(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR, ParamConstant.VR_ACTION_IS_SHOW_VALID_RESULT, 0, null);
            isShowValidResult = bundle.getBoolean("result");
            LogUtil.i(TAG,"isShowValidResult = "+isShowValidResult);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mIsShowValidLiveData.setValue(isShowValidResult);
    }


    public void requestDuplexValidResult(boolean enable) {
        LogUtil.i(TAG,"enable = "+enable);
        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean("enable",enable);
            ServiceCenterManager.getInstance().invokeFunc(AdayoSource.ADAYO_SOURCE_SETTING, AdayoSource.ADAYO_SOURCE_VR,ParamConstant.VR_ACTION_SET_DUPLEX_VALID_RESULT,0, bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
            initShowValidResult();
        }
    }

    public void init() {
        LogUtil.debugD(TAG, "");
        registerServiceCenter();
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_WAKE, mIShareDataInterface);
        if (!b) {
            initRegisterSharedData();
        } else {
            String content = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_WAKE);
            parseWake(content);
            parseWakeRole(content);
            parseWakeWord(content);
            parseWakeFreeEle(content);
            initDuplexEnable();
            initDuplexActiveTime();
            initShowValidResult();

        }

    }

    private void initRegisterSharedData() {
        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());
        devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_WAKE, mIShareDataInterface);
                if (b) {
                    devTimer.stop();
                    String content = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_WAKE);
                    parseWake(content);
                    parseWakeRole(content);
                    parseWakeWord(content);
                    parseWakeFreeEle(content);
                    initDuplexEnable();
                    initDuplexActiveTime();
                    initShowValidResult();
                }
                if (end) {
                    LogUtil.w(TAG, "register SHARE_INFO_ID_LOUDNESS 10 time fail");
                }
            }
        });
        devTimer.start();
    }

    public void unInit() {
        LogUtil.debugD(TAG, "");
        unregisterServiceCenter();
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_WAKE, mIShareDataInterface);
    }

    public MutableLiveData<Boolean> getWakeLiveData() {
        if (mWakeLiveData.getValue() == null) {
            mWakeLiveData.setValue(false);
            LogUtil.w(TAG, "mWakeLiveData is null");
        }
        return mWakeLiveData;
    }

    public MutableLiveData<String> getWakeWordLiveData() {
        if (mWakeWordLiveData.getValue() == null) {
            mWakeWordLiveData.setValue("");
            LogUtil.w(TAG, "mWakeWordLiveData is null");
        }
        return mWakeWordLiveData;
    }

    public MutableLiveData<String> getWakeRoleLiveData() {
        if (mWakeRoleLiveData.getValue() == null) {
            mWakeRoleLiveData.setValue("bjbj");
            LogUtil.w(TAG, "mWakeRoleLiveData is null");
        }
        return mWakeRoleLiveData;
    }

    public MutableLiveData<Boolean> getWakeFreeEleLiveData() {
        if (mWakeFreeEleLiveData.getValue() == null) {
            mWakeFreeEleLiveData.setValue(false);
            LogUtil.w(TAG, "mWakeFreeEleLiveData is null");
        }
        return mWakeFreeEleLiveData;
    }

    public MutableLiveData<String> getWakeWordWrongLiveData() {
        if (mWakeWordWrongLiveData.getValue() == null) {
            mWakeWordWrongLiveData.setValue(null);
            LogUtil.w(TAG, "mWakeWordWrongLiveData is null");
        }
        return mWakeWordWrongLiveData;
    }

    public MutableLiveData<Boolean> getIsDuplexEnableLiveData() {
        if (mIsDuplexEnableLiveData.getValue() == null) {
            mIsDuplexEnableLiveData.setValue(false);
            LogUtil.w(TAG, "mIsDuplexEnableLiveData is null");
        }
        return mIsDuplexEnableLiveData;
    }

    public MutableLiveData<String> getDuplexActiveTimeLiveData() {
        if (mDuplexActiveTimeLiveData.getValue() == null) {
            mDuplexActiveTimeLiveData.setValue("15");
            LogUtil.w(TAG, "mDuplexActiveTimeLiveData is null");
        }

        return mDuplexActiveTimeLiveData;
    }

        public MutableLiveData<Boolean> getIsShowValidLiveData() {
        if (mIsShowValidLiveData.getValue() == null) {
            mIsShowValidLiveData.setValue(false);
            LogUtil.w(TAG, "mIsShowValidLiveData is null");
        }
        return mIsShowValidLiveData;
    }
}
