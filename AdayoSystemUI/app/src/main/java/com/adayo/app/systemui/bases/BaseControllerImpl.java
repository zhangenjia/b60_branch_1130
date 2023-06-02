package com.adayo.app.systemui.bases;

import android.os.Handler;
import android.os.Looper;

import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.interfaces.BaseCallback;
import com.adayo.app.systemui.utils.LogUtil;

import java.util.ArrayList;

/**
 * @author ADAYO-XuYue
 */
public abstract class BaseControllerImpl<T> implements BaseController{
    private static final  Object LOCK = new Object();
    private final ArrayList<BaseCallback<T>> mCallbacks = new ArrayList<>();
    protected final int REGISTER_CALLBACK = 10001;
    protected final int NOTIFY_CALLBACKS = 10002;
    protected final int REGISTER_CONNECT = 10003;

    private T oldData;

    protected abstract boolean registerListener();
    protected abstract T getDataInfo();

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REGISTER_CALLBACK:
                    boolean hasRegistered = registerListener();
                    if (hasRegistered) {
                        mHandler.removeMessages(NOTIFY_CALLBACKS);
                        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    } else {
                        mHandler.removeMessages(REGISTER_CALLBACK);
                        mHandler.sendEmptyMessageDelayed(REGISTER_CALLBACK, SystemUIContent.RETRY_DELAYED);
                    }
                    break;
                case NOTIFY_CALLBACKS:
                    notifyCallbacks();
                    break;
                case REGISTER_CONNECT:
                    boolean hseRegisterConnect = registerConnect();
                    if (!hseRegisterConnect) {
                        mHandler.removeMessages(REGISTER_CONNECT);
                        mHandler.sendEmptyMessageDelayed(REGISTER_CONNECT, SystemUIContent.RETRY_DELAYED);
                    }else{
                        mHandler.removeMessages(REGISTER_CALLBACK);
                        mHandler.sendEmptyMessageDelayed(REGISTER_CALLBACK, 200);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    protected boolean registerConnect(){
        return true;
    }

    private void notifyCallbacks(){
        T dataInfo = getDataInfo();
        if(null != null && oldData.equals(dataInfo)){
            return;
        }
        oldData = dataInfo;
        if (null != dataInfo) {
            synchronized (LOCK) {
                for (BaseCallback callBack : mCallbacks) {
                    notifyCallback(callBack);
                }
            }
        }
    }

    private void notifyCallback(BaseCallback callBack){
        T dataInfo = getDataInfo();
        if (null == dataInfo) {
            return;
        }
        callBack.onDataChange(dataInfo);
    }

    @Override
    public void addCallback(BaseCallback callback) {
        LogUtil.d(SystemUIContent.TAG, null == callback ? "NULL" : callback.toString());
        synchronized (LOCK) {
            if (null != callback && !mCallbacks.contains(callback)) {
                mCallbacks.add(callback);
                notifyCallback(callback);
            }
        }
    }

    @Override
    public void removeCallback(BaseCallback callback) {
        synchronized (LOCK) {
            if (mCallbacks.contains(callback)) {
                mCallbacks.remove(callback);
            }
        }
    }
}
