package com.adayo.app.crossinf.util;

import android.util.Log;

public class LogUtil {
    private static String className;
    private static String methodName;
    private static int lineNumber;
    public static String TAG = "AdayoCrossLog";

    private LogUtil() {
    }

    public static boolean isDebuggable() {
        return true;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(").append(className).append(":").append(lineNumber).append(")");
        buffer.append(methodName);
        buffer.append("()");
        buffer.append(" " + log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.e(TAG, createLog(message));
        }
    }

    public static void i(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.i(TAG, createLog(message));
        }
    }

    public static void d(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.i(TAG, createLog(message));
        }
    }

    public static void v(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.v(TAG, createLog(message));
        }
    }

    public static void w(String message) {
        if (isDebuggable()) {
            getMethodNames((new Throwable()).getStackTrace());
            Log.w(TAG, createLog(message));
        }
    }
}

