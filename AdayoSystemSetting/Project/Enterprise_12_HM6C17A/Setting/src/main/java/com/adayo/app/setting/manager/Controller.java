package com.adayo.app.setting.manager;

import android.os.Bundle;

import com.adayo.app.base.ViewStubBase;
import com.adayo.app.base.BaseActivity;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.base.JsonFileParseUtil;
import com.adayo.app.base.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.app.setting.model.constant.ParamConstant.BLUETOOTH;
import static com.adayo.app.setting.model.constant.ParamConstant.COMMON;
import static com.adayo.app.setting.model.constant.ParamConstant.DEVNM;
import static com.adayo.app.setting.model.constant.ParamConstant.DRIVE;
import static com.adayo.app.setting.model.constant.ParamConstant.HOTSPOT;
import static com.adayo.app.setting.model.constant.ParamConstant.LANGUAGE;
import static com.adayo.app.setting.model.constant.ParamConstant.LOCAL;
import static com.adayo.app.setting.model.constant.ParamConstant.REPLY;
import static com.adayo.app.setting.model.constant.ParamConstant.UNIT;
import static com.adayo.app.setting.model.constant.ParamConstant.WIFI;
import static com.adayo.proxy.media.mediascanner.utils.ContextUtil.getAppContext;


public class Controller {

    private static final String TAG = Controller.class.getSimpleName();
    private volatile static Controller mController;

    private List<ViewStubBase> mViewStubBases = new ArrayList<>();
    private ServiceStatus DevSvcInitStatus = ServiceStatus.ERROR;

    private BaseActivity mBaseActivity;
    private ViewStubBase mLanguageFragment;
    private ViewStubBase commonLocalFragment;
    private ViewStubBase mConnDevnmFragment;
    private ViewStubBase mReplyFragment;
    private ViewStubBase connHotspotFragment;
    private ViewStubBase connUnitFragment;
    private ViewStubBase connWifiFragment;
    private ViewStubBase directMediaFragment;
    private ViewStubBase mDriveFragment;
    private ViewStubBase mCommonFragment;
    private ViewStubBase mConnBluetoothFragment;
    private IDeviceServiceCallback iDeviceServiceCallback;
    private AbstractLifeHandler mAbstractLifeHandler;
    public boolean isActivityReady = false;
    public boolean isInflateReady = false;


private enum ServiceStatus {
        INITING,
        INIT,
        READY,
        ERROR
    }

    private Controller() {

    }

    public void setBaseActivity(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    public static Controller getInstance() {
        if (mController == null) {
            synchronized (Controller.class) {
                if (mController == null) {
                    mController = new Controller();
                }
            }
        }
        return mController;
    }


    public void createFunction(String file) {
        String json = JsonFileParseUtil.mInstance.readStringFromAssets(getAppContext(), file);
        DevSvcInitStatus = ServiceStatus.INITING;
        LogUtil.d(TAG, "  DevSvcInitStatus() " + DevSvcInitStatus);
        try {
            JSONObject jso = new JSONObject(json);if (!jso.isNull(COMMON)) {String classname = jso.getString(COMMON);try {
                    mCommonFragment = (ViewStubBase) Class.forName(classname).newInstance();mCommonFragment.setBaseActivity(mBaseActivity);mViewStubBases.add(mCommonFragment);LogUtil.d(TAG, "  createDevice() mCommonFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (!jso.isNull(DEVNM)) {String classname = jso.getString(DEVNM);try {
                    mConnDevnmFragment = (ViewStubBase) Class.forName(classname).newInstance();mConnDevnmFragment.setBaseActivity(mBaseActivity);mViewStubBases.add(mConnDevnmFragment);LogUtil.d(TAG, "  createDevice() mConnDevnmFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (!jso.isNull(LOCAL)) {
                String classname = jso.getString(LOCAL);
                try {
                    commonLocalFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    commonLocalFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(commonLocalFragment);
                    LogUtil.d(TAG, "  createDevice() commonLocalFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (!jso.isNull(REPLY)) {
                String classname = jso.getString(REPLY);
                try {
                    mReplyFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    mReplyFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(mReplyFragment);
LogUtil.d(TAG, "  createDevice() mReplyFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (!jso.isNull(HOTSPOT)) {
                String classname = jso.getString(HOTSPOT);
                try {
                    connHotspotFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    connHotspotFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(connHotspotFragment);
LogUtil.d(TAG, "  createDevice() connHotspotFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (!jso.isNull(UNIT)) {
                String classname = jso.getString(UNIT);
                try {
                    connUnitFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    connUnitFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(connUnitFragment);
LogUtil.d(TAG, "  createDevice() connUnitFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            if (!jso.isNull(WIFI)) {
                String classname = jso.getString(WIFI);
                try {
                    connWifiFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    connWifiFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(connWifiFragment);
LogUtil.d(TAG, "  createDevice() connWifiFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (!jso.isNull(ParamConstant.DIRECT_MEDIA)) {
                String classname = jso.getString(ParamConstant.DIRECT_MEDIA);
                try {
                    directMediaFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    directMediaFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(directMediaFragment);
LogUtil.d(TAG, "  createDevice() directMediaFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (!jso.isNull(DRIVE)) {
                String classname = jso.getString(DRIVE);
                try {
                    mDriveFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    mDriveFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(mDriveFragment);
LogUtil.d(TAG, "  createDevice() mDriveFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (!jso.isNull(LANGUAGE)) {
                String classname = jso.getString(LANGUAGE);
                try {
                    mLanguageFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    mLanguageFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(mLanguageFragment);
LogUtil.d(TAG, "  createDevice() mLanguageFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (!jso.isNull(BLUETOOTH)) {
                String classname = jso.getString(BLUETOOTH);
                try {
                    mConnBluetoothFragment = (ViewStubBase) Class.forName(classname).newInstance();
                    mConnBluetoothFragment.setBaseActivity(mBaseActivity);
                    mViewStubBases.add(mConnBluetoothFragment);
LogUtil.d(TAG, "  createDevice() mConnBluetoothFragment");
                } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            DevSvcInitStatus = ServiceStatus.INIT;
            LogUtil.d(TAG, "  DevSvcInitStatus() " + DevSvcInitStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

public void onNotifyLifeState(AbstractLifeHandler handler) {
        LogUtil.d(TAG,"handler="+handler.getClass().getName());
        if (handler != null) {
            if (mAbstractLifeHandler == null) {
                mAbstractLifeHandler = handler;
            } else {
                mAbstractLifeHandler.setNextHandler(handler);
            }
        }
        LogUtil.d(TAG,"mLifeHandler="+ mAbstractLifeHandler.getClass().getName());
    }

    public void onNotifyInitViewModel() {
        for (ViewStubBase db : mViewStubBases) {
            db.initViewModel();
        }
    }

    public synchronized void onNotifySystemState(boolean main, boolean Inflate) {
        LogUtil.d(TAG,"main ="+main+"Inflate ="+Inflate);
        isActivityReady = main;
        isInflateReady = Inflate;
        if (isActivityReady && isInflateReady) {
            startSumbit();
        }
    }

    public void startSumbit() {
        if (mAbstractLifeHandler != null) {
            LogUtil.d(TAG,"mLifeHandler ="+ mAbstractLifeHandler.getClass().getName());
            mAbstractLifeHandler.submit(mViewStubBases);
            mAbstractLifeHandler = null;
        }
    }
public void sendDeviceCallBackData(String deviceId, Bundle param) {
        ViewStubBase devInstance = getDevice(deviceId);
        LogUtil.d(TAG, " sendDeviceCallBackData: deviceId = " + deviceId + ", param = " + param.toString());
        if (devInstance != null) {
}
    }

    public ViewStubBase getDevice(String device) {
        switch (device) {
            case DEVNM:
                return mConnDevnmFragment;
            case LOCAL:
                return commonLocalFragment;
            case REPLY:
                return mReplyFragment;
            case HOTSPOT:
                return connHotspotFragment;
            case UNIT:
                return connUnitFragment;
            case WIFI:
                return connWifiFragment;
            case ParamConstant.DIRECT_MEDIA:
                return directMediaFragment;
            case DRIVE:
                return mDriveFragment;
            case LANGUAGE:
                return mLanguageFragment;
            case ParamConstant.COMMON:
                return mCommonFragment;
            case BLUETOOTH:
                return mConnBluetoothFragment;
            default:
                return null;
        }
    }
}
