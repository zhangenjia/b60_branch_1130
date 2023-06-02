package com.adayo.app.setting.utils.timer;

import android.util.Log;

import com.adayo.app.setting.utils.DevFinal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public final class LogPrintUtils {

    private LogPrintUtils() {
    }

    private static final int     JSON_INDENT     = 4;
    private static       boolean JUDGE_PRINT_LOG = false;
    private static final String  DEFAULT_TAG     = LogPrintUtils.class.getSimpleName();


    public static boolean isPrintLog() {
        return JUDGE_PRINT_LOG;
    }


    public static void setPrintLog(final boolean judgePrintLog) {
        JUDGE_PRINT_LOG = judgePrintLog;
    }


    private static boolean isEmpty(final String str) {
        return (str == null || str.length() == 0);
    }


    private static void printLog(
            final int logType,
            final String tag,
            final String message
    ) {
        if (LogPrintUtils.sPrint != null) {
            LogPrintUtils.sPrint.printLog(logType, tag, message);
        }
    }


    private static String createMessage(
            final String message,
            final Object... args
    ) {
        String result;
        try {
            if (message != null) {
                if (args == null) {
                    result = "params is null";
                } else {
                    result = (args.length == 0 ? message : String.format(message, args));
                }
            } else {
                result = "message is null";
            }
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }


    private static String concatErrorMessage(
            final Throwable throwable,
            final String message,
            final Object... args
    ) {
        String result;
        try {
            if (throwable != null) {
                if (message != null) {
                    result = createMessage(message, args) + " : " + throwable.toString();
                } else {
                    result = throwable.toString();
                }
            } else {
                result = createMessage(message, args);
            }
        } catch (Exception e) {
            result = e.toString();
        }
        return result;
    }

    public static void d(
            final String message,
            final Object... args
    ) {
        dTag(DEFAULT_TAG, message, args);
    }

    public static void e(final Throwable throwable) {
        eTag(DEFAULT_TAG, throwable, null);
    }

    public static void e(
            final String message,
            final Object... args
    ) {
        e(null, message, args);
    }

    public static void e(
            final Throwable throwable,
            final String message,
            final Object... args
    ) {
        eTag(DEFAULT_TAG, throwable, message, args);
    }

    public static void w(
            final String message,
            final Object... args
    ) {
        wTag(DEFAULT_TAG, message, args);
    }

    public static void i(
            final String message,
            final Object... args
    ) {
        iTag(DEFAULT_TAG, message, args);
    }

    public static void v(
            final String message,
            final Object... args
    ) {
        vTag(DEFAULT_TAG, message, args);
    }

    public static void wtf(
            final String message,
            final Object... args
    ) {
        wtfTag(DEFAULT_TAG, message, args);
    }

    public static void json(final String json) {
        jsonTag(DEFAULT_TAG, json);
    }

    public static void xml(final String xml) {
        xmlTag(DEFAULT_TAG, xml);
    }

    public static void dTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.DEBUG, tag, createMessage(message, args));
        }
    }

    public static void eTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.ERROR, tag, createMessage(message, args));
        }
    }

    public static void eTag(
            final String tag,
            final Throwable throwable
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.ERROR, tag, concatErrorMessage(throwable, null));
        }
    }

    public static void eTag(
            final String tag,
            final Throwable throwable,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.ERROR, tag, concatErrorMessage(throwable, message, args));
        }
    }

    public static void wTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.WARN, tag, createMessage(message, args));
        }
    }

    public static void iTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.INFO, tag, createMessage(message, args));
        }
    }

    public static void vTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.VERBOSE, tag, createMessage(message, args));
        }
    }

    public static void wtfTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(Log.ASSERT, tag, createMessage(message, args));
        }
    }

    public static void jsonTag(
            final String tag,
            final String json
    ) {
        if (JUDGE_PRINT_LOG) {
            if (isEmpty(json)) {
                printLog(Log.ERROR, tag, "Empty/Null json content");
                return;
            }
            try {
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    String message = jsonObject.toString(JSON_INDENT);
                    printLog(Log.DEBUG, tag, message);
                } else if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    String message = jsonArray.toString(JSON_INDENT);
                    printLog(Log.DEBUG, tag, message);
                } else {
                    printLog(Log.DEBUG, tag, "json content format error");
                }
            } catch (Exception e) {
                String    errorInfo;
                Throwable throwable = e.getCause();
                if (throwable != null) {
                    errorInfo = throwable.toString();
                } else {
                    try {
                        errorInfo = e.toString();
                    } catch (Exception e1) {
                        errorInfo = e1.toString();
                    }
                }
                printLog(Log.ERROR, tag, errorInfo + DevFinal.SYMBOL.NEW_LINE + json);
            }
        }
    }

    public static void xmlTag(
            final String tag,
            final String xml
    ) {
        if (JUDGE_PRINT_LOG) {
            if (isEmpty(xml)) {
                printLog(Log.ERROR, tag, "Empty/Null xml content");
                return;
            }
            try {
                Source       xmlInput    = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput   = new StreamResult(new StringWriter());
                Transformer  transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http:transformer.transform(xmlInput, xmlOutput);
                String message = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
                printLog(Log.DEBUG, tag, message);
            } catch (Exception e) {
                String    errorInfo;
                Throwable throwable = e.getCause();
                if (throwable != null) {
                    errorInfo = throwable.toString();
                } else {
                    try {
                        errorInfo = e.toString();
                    } catch (Exception e1) {
                        errorInfo = e1.toString();
                    }
                }
                printLog(Log.ERROR, tag, errorInfo + DevFinal.SYMBOL.NEW_LINE + xml);
            }
        }
    }

    private static Print sPrint = (logType, tag, message) -> {
        if (message == null) {
            return;
        }
        switch (logType) {
            case Log.VERBOSE:
                Log.v(tag, message);
                break;
            case Log.DEBUG:
                Log.d(tag, message);
                break;
            case Log.INFO:
                Log.i(tag, message);
                break;
            case Log.WARN:
                Log.w(tag, message);
                break;
            case Log.ERROR:
                Log.e(tag, message);
                break;
            case Log.ASSERT:
            default:
                Log.wtf(tag, message);
                break;
        }
    };


    public static void setPrint(final Print print) {
        LogPrintUtils.sPrint = print;
    }


    public interface Print {


        void printLog(
                int logType,
                String tag,
                String message
        );
    }
}