package com.adayo.app.systemui.utils;


import android.util.Log;

/**
 * Created by sunth on 2017/11/23.
 */
public class LogUtil {

    private static String className;//类名
    private static String methodName;//方法名
    private static int lineNumber;//行数

    private LogUtil() {
        /* Protect from instantiations */
    }

    public static boolean isDebuggable() {
        return true;//BuildConfig.DEBUG;
    }

    private static String createLog(String msg) {
        return methodName + "(" + className + ":" + lineNumber + ")" + msg;
    }

    private static final int index = 1;
    private static void getMethodNames(StackTraceElement[] sElements) {
        if(sElements.length <= index){
            return;
        }
        StackTraceElement stackTraceElement = sElements[index];
        className = stackTraceElement.getFileName();
        methodName = stackTraceElement.getMethodName();
        lineNumber = stackTraceElement.getLineNumber();
    }

    public static void e(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(tag, createLog(message));
    }

    public static void e(String tag, String message, Throwable e) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(tag, createLog(message), e);
    }

    public static void i(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, createLog(message));
    }

    public static void d(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(message));
    }

    public static void v(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.v(tag, createLog(message));
    }

    public static void w(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.w(tag, createLog(message));
    }

    public static void wtf(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(tag, createLog(message));
    }

    public static void debugE(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(tag, createLog(message));
    }

    public static void debugI(String tag, String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, createLog(message));
    }

    public static void debugD(String tag, String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(message));
    }

    public static void debugV(String tag, String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.v(tag, createLog(message));
    }

    public static void debugW(String tag, String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.w(tag, createLog(message));
    }
}
