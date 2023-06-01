package com.adayo.app.btphone.utils;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;


public class LengthFilter implements InputFilter {
    private static final String TAG = LengthFilter.class.getSimpleName();
    private final int MAX_EN; // 最大英文/数字长度 一个汉字算两个字母
    private String ellipsis;

    public LengthFilter(int maxEN) {
        this.MAX_EN = maxEN;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Log.d(TAG, "filter__destCount: " + source + " start = "+start+" end = "+end+
                " dest = "+dest+ " dstart = "+dstart+" dend = "+dend);
        // 新输入的字符串为空直接不接收（删除剪切等）
        if (TextUtils.isEmpty(source)) {
            return "";
        }
        // 输入前就存在的字符长度
        int destCount = getCount(dest.toString());
        // 输入前就已满直接不接收
        if (destCount >= MAX_EN) {
            return "";
        }
        // 新输入的字符长度
        int sourceCount = getCount(source.toString());
        // 如果拼接后不超长，直接拼接
        if (destCount + sourceCount <= MAX_EN) {
            return source;
        }
        // 超长时不应该直接拒绝，应在允许范围内尽量拼接
        return getByCount(source.toString(), MAX_EN - destCount);
    }

    /**
     * 超长时根据剩余长度在字符范围内截取字符串
     */
    private String getByCount(String s, int count) {
        Log.d(TAG, "getByCount: if" + " s = " + s + " count = " + count);
        String temp = "";
        int tempCount = 0;
        char[] cs = s.toCharArray();
        for (char c : cs) {
            if (tempCount + getCount(c) <= count) {
                tempCount += getCount(c);
                temp += c;
            } else {
                break;
            }
        }
        Log.d(TAG, "getByCount: " + temp);
        return temp;
    }

    /**
     * 计算字符串占位判定
     */
    private int getCount(String s) {
        if (TextUtils.isEmpty(s)) {
            return 0;
        }
        int count = 0;
        char[] cs = s.toCharArray();
        for (char c : cs) {
            count += getCount(c);
        }
        return count;
    }


    /**
     * 单字符占位判定
     */
    private int getCount(char c) {
        int i = String.valueOf(c).getBytes().length > 2 ? 2 : 1;
        return i;
    }

    /**
     * 添加省略号
     * @return
     */
    private String addEllipsis(){
        String str = new String("...");
        return str;
    }


}