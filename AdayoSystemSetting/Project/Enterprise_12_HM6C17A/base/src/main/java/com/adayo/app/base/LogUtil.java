package com.adayo.app.base;


import android.util.Log;

import com.adayo.app.base.JsonFileParseUtil;

import org.json.JSONObject;

/**
 * Created by sunth on 2017/11/23.
 */
public class LogUtil {
    private static final String FRAMEWORK_NET_WIFIAPCONFIGSTOREDEF = "/system/etc/adayo/Settings/AppSettingsDef.json";
    private static String className;//类名
    private static String methodName;//方法名
    private static int lineNumber;//行数
    private static boolean Debuggable = false;
    private static String DebuggableString;

    private LogUtil() {
        /* Protect from instantiations */
    }
    private static String getDebuggable() {
        String DebuggableJsonString = JsonFileParseUtil.mInstance.ReadJsonFile(FRAMEWORK_NET_WIFIAPCONFIGSTOREDEF);
        if (DebuggableJsonString == null) {
            Log.w("LogUtil", "/system/etc/adayo/Settings/AppSettingsDef.json path = null");
            return "false";
        }
        try {
            JSONObject DebuggableJsonObject = new JSONObject(DebuggableJsonString);
            JSONObject DebuggableObject = DebuggableJsonObject.getJSONObject("Debuggable");
            String AppDebuggable = DebuggableObject.getString("AppDebuggable");
            if(AppDebuggable==null){
                Log.w("LogUtil","NOT FIND AppDebuggable x ="+AppDebuggable);
                return "false";
            }
            Log.d("LogUtil", "AppDebuggable =" + AppDebuggable);
            return AppDebuggable;
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("LogUtil", "init Debuggable map error !");
            return "false";
        }

    }

    /**
     * true有debug的log
     *
     * @return
     */
    public static boolean isDebuggable() {
       if(DebuggableString==null){
          DebuggableString=getDebuggable();
         if(DebuggableString.equals("true")){
             Debuggable=true;
         }else if(DebuggableString.equals("false")){
             Debuggable=false;
         }else {
             Log.w("LogUtil","Illegal parameter DebuggableString ="+DebuggableString);
             Debuggable=false;
         }
       }
       return Debuggable;
        //BuildConfig.DEBUG;
    }

    private static String createLog(String msg) {
        return methodName + "(" + className + ":" + lineNumber + ")" + msg;
    }

    private static final int index = 1;

    private static void getMethodNames(StackTraceElement[] sElements) {
        if (sElements.length <= index) {
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
    public static void i(String tag) {
        getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, createLog(""));
    }

    public static void d(String tag, String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(message));
    }

    public static void d(String tag) {
        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(""));
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

    public static void debugD(String tag) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(""));
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
