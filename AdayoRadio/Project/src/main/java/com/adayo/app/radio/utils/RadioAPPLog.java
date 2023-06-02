package com.adayo.app.radio.utils;

import android.util.Log;

import com.adayo.app.utils.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils.mLogEnable;

/**
 * @author ADAYO-06
 */
public class RadioAPPLog {
    public static final String TAG = "RadioAPPLog";

    private static String mClassname;
    private static ArrayList<String> mMethods;

    static {
        mClassname = LogUtils.class.getName();
        mMethods = new ArrayList<>();

        Method[] ms = LogUtils.class.getDeclaredMethods();
        for (Method m : ms) {
            mMethods.add(m.getName());
        }
    }

    public static void init(boolean logEnable) {
        mLogEnable = logEnable;
    }

    public static void d(String tag, String msg) {
        if (mLogEnable) {
            Log.d(TAG, getMsgWithLineNumber(msg));
        }
    }

    public static void e(String tag, String msg) {
        if (mLogEnable) {
            Log.e(TAG, getMsgWithLineNumber(msg));
        }
    }

    public static void i(String tag, String msg) {
        if (mLogEnable) {
            Log.i(TAG, getMsgWithLineNumber(msg));
        }
    }

    public static void w(String tag, String msg) {
        if (mLogEnable) {
            Log.w(TAG, getMsgWithLineNumber(msg));
        }
    }

    public static void v(String tag, String msg) {
        if (mLogEnable) {
            Log.v(TAG, getMsgWithLineNumber(msg));
        }
    }


    public static void d(String msg) {
        if (mLogEnable) {
            String[] content = getMsgAndTagWithLineNumber(msg);
            Log.d(content[0], content[1]);
        }
    }

    public static void e(String msg) {
        if (mLogEnable) {
            String[] content = getMsgAndTagWithLineNumber(msg);
            Log.e(content[0], content[1]);
        }
    }

    public static void i(String msg) {
        if (mLogEnable) {
            String[] content = getMsgAndTagWithLineNumber(msg);
            Log.i(content[0], content[1]);
        }
    }

    public static void i() {
        if (mLogEnable) {
            String[] content = getMsgAndTagWithLineNumber("");
            Log.i(content[0], content[1]);
        }
    }

    public static void w(String msg) {
        if (mLogEnable) {
            String[] content = getMsgAndTagWithLineNumber(msg);
            Log.w(content[0], content[1]);
        }
    }

    public static void v(String msg) {
        if (mLogEnable) {
            String[] content = getMsgAndTagWithLineNumber(msg);
            Log.v(content[0], content[1]);
        }
    }

    public static String getMsgWithLineNumber(String msg) {
        try {
            for (StackTraceElement st : (new Throwable()).getStackTrace()) {
                if (mClassname.equals(st.getClassName()) || mMethods.contains(st.getMethodName())) {
                    continue;
                } else {
                    int b = st.getClassName().lastIndexOf(".") + 1;
                    String message = new StringBuilder(st.getClassName().substring(b)).append("->").append(st.getMethodName())
                            .append("():").append(st.getLineNumber()).append(msg).toString();
                    return message;
                }

            }
        } catch (Exception e) {

        }
        return msg;
    }

    public static String[] getMsgAndTagWithLineNumber(String msg) {
        try {
            for (StackTraceElement st : (new Throwable()).getStackTrace()) {
                if (mClassname.equals(st.getClassName()) || mMethods.contains(st.getMethodName())) {
                    continue;
                } else {
                    int b = st.getClassName().lastIndexOf(".") + 1;
                    String tag = st.getClassName().substring(b);
                    String message = st.getMethodName() + "():" + st.getLineNumber() + "->" + msg;
                    String[] content = new String[]{tag, message};
                    return content;
                }

            }
        } catch (Exception e) {

        }
        return new String[]{"universal tag", msg};
    }
}
