package com.adayo.app.radio.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author ADAYO-06
 */
public class SimpleSave {

    private final String TAG = this.getClass().getSimpleName();
    private Context mContext = null;
    private SharedPreferences mPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private final String DBNAME = "launcherSharePreference";

    public SimpleSave(Context context) {
        try {
            mContext = context;
            mPreferences = mContext.getSharedPreferences(DBNAME, Context.MODE_MULTI_PROCESS);
            mEditor = mPreferences.edit();

        } catch (Exception e) {
            Log.e(TAG, "Create e=" + e.getMessage());
        }
    }

    public SimpleSave(Context context, String dbName) {
        try {
            mContext = context;
            mPreferences = mContext.getSharedPreferences(dbName, Context.MODE_MULTI_PROCESS);
            mEditor = mPreferences.edit();

        } catch (Exception e) {
            Log.e(TAG, "Create e=" + e.getMessage());
        }
    }

    /**
     * 获取对应字段的字符串值
     *
     * @param name    字段名
     * @param dfValue 默认值（若字段不存在，返回默认值）
     */
    public String getString(String name, String dfValue) {
        return mPreferences.getString(name, dfValue);
    }

    /**
     * 获取对应字段的整型值
     *
     * @param name    字段名
     * @param dfValue 默认值（若字段不存在，返回默认值）
     */
    public int getInt(String name, int dfValue) {
        return mPreferences.getInt(name, dfValue);
    }

    /**
     * 获取对应字段的整型值
     *
     * @param name    字段名
     * @param dfValue 默认值（若字段不存在，返回默认值）
     */
    public boolean getBoolean(String name, boolean dfValue) {
        return mPreferences.getBoolean(name, dfValue);
    }

    /**
     * 设置对应字段的字符串值
     *
     * @param name  字段名
     * @param value 值
     */
    public void putString(String name, String value) {
        try {
            mEditor.putString(name, value);
            mEditor.commit();

        } catch (Exception e) {
            Log.e(TAG, "PutData e=" + e.getMessage());
        }
    }

    /**
     * 设置对应字段的整型值
     *
     * @param name  字段名
     * @param value 值
     */
    public void putInt(String name, int value) {
        try {
            mEditor.putInt(name, value);
            mEditor.commit();

        } catch (Exception e) {
            Log.e(TAG, "PutData e=" + e.getMessage());
        }
    }

    /**
     * 设置对应字段的整型值
     *
     * @param name  字段名
     * @param value 值
     */
    public void putBoolean(String name, boolean value) {
        try {
            mEditor.putBoolean(name, value);
            mEditor.commit();

        } catch (Exception e) {
            Log.e(TAG, "PutData e=" + e.getMessage());
        }
    }
}
