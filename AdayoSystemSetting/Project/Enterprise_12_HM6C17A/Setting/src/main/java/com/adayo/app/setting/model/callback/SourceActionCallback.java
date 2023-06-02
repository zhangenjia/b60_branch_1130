package com.adayo.app.setting.model.callback;

import android.app.Activity;
import android.os.RemoteException;

import com.adayo.proxy.infrastructure.sourcemng.ISourceActionCallBack;
import com.adayo.app.base.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class SourceActionCallback extends ISourceActionCallBack.Stub {
    private final WeakReference<Activity> mActivityWeakReference;
    private final static String TAG = SourceActionCallback.class.getSimpleName();

    public SourceActionCallback(Activity activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void SourceOff() throws RemoteException {
        Activity activity = mActivityWeakReference.get();
        if (Objects.nonNull(activity)) {
            activity.finish();
        } else {
            LogUtil.w(TAG,"object already GC recycle");
        }
    }
}
