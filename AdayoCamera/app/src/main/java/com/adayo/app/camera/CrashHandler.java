package com.adayo.app.camera;

import android.util.Log;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

/**
 * @author Yiwen.Huan
 * created at 2021/12/9 15:24
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        SrcMngSwitchManager.getInstance().notifyAppFinished(AdayoSource.ADAYO_SOURCE_AVM, "com.adayo.app.camera");
        Log.e("AdayoCamera", TAG + " - uncaughtException: error", e);
        System.exit(0);
    }
}
