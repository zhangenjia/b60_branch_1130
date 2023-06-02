package com.adayo.app.setting.utils;

import com.adayo.app.base.LogUtil;

import java.util.List;


public class StringUtils {


    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else if (str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isNotEmpty(String str) {
        if (str == null) {
            return false;
        } else if (str.length() == 0) {
            return false;
        } else {
            return true;
        }
    }



    public static String[] stringToArray(String str, String expr) {
        return str.split(expr);
    }


    public static String arrayToString(String[] arr, String expr) {
        String strInfo = "";
        if (arr != null && arr.length > 0) {
            StringBuffer sf = new StringBuffer();
            for (String str : arr) {
                sf.append(str);
                sf.append(expr);
            }
            strInfo = sf.substring(0, sf.length() - 1);
        }
        return strInfo;
    }



    public static String listToString(List<String> list, String expr) {
        String strInfo = "";
        if (list != null && list.size() > 0) {
            StringBuffer sf = new StringBuffer();
            for (String str : list) {
                sf.append(str);
                sf.append(expr);
            }
            strInfo = sf.substring(0, sf.length() - 1);
        }
        return strInfo;
    }


    public static String getLenStr(String str, int len) {
        String returnStr = "";
        int str_length = 0;
        int str_len = 0;
        String str_cut = new String();
        str_len = str.length();
        for (int i = 0; i < str_len; i++) {
            char a = str.charAt(i);
            str_length++;
            if (escape(a + "").length() > 4) {
                str_length++;
            }
            str_cut = str_cut.concat(a + "");
            if (str_length >= len) {
                str_cut = str_cut.concat("...");
                returnStr = str_cut;
                break;
            }
        }
        if (str_length < len) {
            returnStr = str;
        }
        return returnStr;
    }

    private static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j)) {
                tmp.append(j);
            } else if (j < 256) {
                tmp.append("%");
                if (j < 16) {
                    tmp.append("0");
                }
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String replaceAll(
            final String str,
            final String suffix,
            final String replace
    ) {
        if (!isEmpty(str) && !isEmpty(suffix) && replace != null && !suffix.equals(replace)) {
            try {
                return str.replaceAll(suffix, replace);
            } catch (Exception e) {
                LogUtil.e("StringUtils", e+ "replaceAll");
            }
        }
        return str;
    }
}