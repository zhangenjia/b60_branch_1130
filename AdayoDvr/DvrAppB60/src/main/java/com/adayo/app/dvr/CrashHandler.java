package com.adayo.app.dvr;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adayo.app.dvr.constant.Constant;
import com.adayo.app.dvr.controller.DvrController;

public class  CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG =CrashHandler.class.getSimpleName() ;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    private static CrashHandler INSTANCE = new CrashHandler();

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }
    public void init(Context context){
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Log.d(TAG, "uncaughtException: ");
            //如果用户没有处理则让系统默认的异常处理器来处理
        if(e != null && mDefaultHandler != null){
            mDefaultHandler.uncaughtException(t, e);

        }
            DvrController.getInstance().setDisplayMode(Constant.DISPLAY_MODE_SET_PREVIEW);
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
}
