package com.adayo.app.picture.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.adayo.proxy.system.systemservice.ISystemServiceCallback;
import com.adayo.proxy.system.systemservice.SystemServiceManager;

import java.util.ArrayList;

public class SystemStatusControllerImpl implements SystemStatusController, ISystemServiceCallback {

    private volatile static SystemStatusControllerImpl mSystemStatusControllerImpl;
    private final ArrayList<SystemStatusChangeCallback> mCallbacks = new ArrayList<>();
    private final int REGISTER_CALLBACK = 10001;
    private final int STATUS_CHANGE_NOTIFY = 10002;
    private SystemServiceManager mSystemServiceManager;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REGISTER_CALLBACK:
                    registerSystemServiceCallback();
                    break;
                case STATUS_CHANGE_NOTIFY:
                    notifyCallbacks((byte) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private SystemStatusControllerImpl() {
        mSystemServiceManager = SystemServiceManager.getInstance();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static SystemStatusControllerImpl getInstance() {
        if (mSystemStatusControllerImpl == null) {
            synchronized (SystemStatusControllerImpl.class) {
                if (mSystemStatusControllerImpl == null) {
                    mSystemStatusControllerImpl = new SystemStatusControllerImpl();
                }
            }
        }
        return mSystemStatusControllerImpl;
    }

    public void registerSystemServiceCallback() {
        if (null != mSystemServiceManager) {
            if (mSystemServiceManager.conectsystemService() && mSystemServiceManager.registSystemServiceCallback(this, SystemUIContent.FUNCTION_ID)) {
                return;
            }
            mHandler.removeMessages(REGISTER_CALLBACK);
            mHandler.sendEmptyMessage(REGISTER_CALLBACK);
        }
    }

    private void notifyCallback(SystemStatusChangeCallback callback) {
        if (null != callback) {
            callback.onSystemStatusChanged(getSystemStatus());
        }
    }

    private void notifyCallbacks(byte status) {
        synchronized (mCallbacks) {
            for (SystemStatusChangeCallback callback : mCallbacks) {
                callback.onSystemStatusChanged(status);
            }
        }
    }

    @Override
    public boolean isSystemServiceConnected() {
        if (null != mSystemServiceManager) {
            return mSystemServiceManager.conectsystemService();
        }
        return false;
    }

    @Override
    public void addCallback(SystemStatusChangeCallback callback) {
        synchronized (mCallbacks) {
            if (null != callback && !mCallbacks.contains(callback)) {
                mCallbacks.add(callback);
                notifyCallback(callback);
            }
        }
    }

    @Override
    public void removeCallback(SystemStatusChangeCallback callback) {
        synchronized (mCallbacks) {
            if (mCallbacks.contains(callback)) {
                mCallbacks.remove(callback);
            }
        }
    }

    @Override
    public void requestPowerAction() {
        if (null != mSystemServiceManager) {
            mSystemServiceManager.requestPowerAction();
        }
    }

    @Override
    public byte getSystemStatus() {
        if (null != mSystemServiceManager) {
            return mSystemServiceManager.getSystemStatus();
        }
        return 0;
    }

    @Override
    public void systemStatusNotify(byte b) {
        Message message = Message.obtain();
        message.what = STATUS_CHANGE_NOTIFY;
        message.obj = b;
        mHandler.sendMessage(message);
    }
}
