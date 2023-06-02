package com.adayo.app.setting.utils;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public final class JCLogUtils {

    private JCLogUtils() {
    }

    private static       boolean JUDGE_PRINT_LOG         = false;
    private static       boolean JUDGE_CONTROL_PRINT_LOG = false;
    private static final String  DEFAULT_TAG             = JCLogUtils.class.getSimpleName();

    public static final int INFO  = 0;
    public static final int DEBUG = 1;
    public static final int ERROR = 2;


    public static boolean isPrintLog() {
        return JUDGE_PRINT_LOG;
    }


    public static void setPrintLog(final boolean judgePrintLog) {
        JUDGE_PRINT_LOG = judgePrintLog;
    }


    public static void setControlPrintLog(final boolean judgeControlPrintLog) {
        JUDGE_CONTROL_PRINT_LOG = judgeControlPrintLog;
    }


    private static boolean isEmpty(final String str) {
        return (str == null || str.length() == 0);
    }


    private static void printLog(
            final int logType,
            final String tag,
            final String message
    ) {
        if (JCLogUtils.sPrint != null) {
            JCLogUtils.sPrint.printLog(logType, tag, message);
        }

        if (JUDGE_CONTROL_PRINT_LOG) {
            if (isEmpty(tag)) {
                System.out.println(message);
            } else {
                System.out.println(tag + " : " + message);
            }
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
        eTag(DEFAULT_TAG, throwable);
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

    public static void i(
            final String message,
            final Object... args
    ) {
        iTag(DEFAULT_TAG, message, args);
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
            printLog(DEBUG, tag, createMessage(message, args));
        }
    }

    public static void eTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(ERROR, tag, createMessage(message, args));
        }
    }

    public static void eTag(
            final String tag,
            final Throwable throwable
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(ERROR, tag, concatErrorMessage(throwable, null));
        }
    }

    public static void eTag(
            final String tag,
            final Throwable throwable,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(ERROR, tag, concatErrorMessage(throwable, message, args));
        }
    }

    public static void iTag(
            final String tag,
            final String message,
            final Object... args
    ) {
        if (JUDGE_PRINT_LOG) {
            printLog(INFO, tag, createMessage(message, args));
        }
    }

    public static void xmlTag(
            final String tag,
            final String xml
    ) {
        if (JUDGE_PRINT_LOG) {
            if (isEmpty(xml)) {
                printLog(ERROR, tag, "Empty/Null xml content");
                return;
            }
            try {
                Source       xmlInput    = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput   = new StreamResult(new StringWriter());
                Transformer  transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http:transformer.transform(xmlInput, xmlOutput);
                String message = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
                printLog(DEBUG, tag, message);
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
                printLog(ERROR, tag, errorInfo + DevFinal.SYMBOL.NEW_LINE + xml);
            }
        }
    }

    private static Print sPrint;


    public static void setPrint(final Print print) {
        JCLogUtils.sPrint = print;
    }


    public interface Print {


        void printLog(
                int logType,
                String tag,
                String message
        );
    }
}