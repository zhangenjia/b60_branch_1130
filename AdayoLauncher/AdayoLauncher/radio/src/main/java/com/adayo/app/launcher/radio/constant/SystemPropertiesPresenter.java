package com.adayo.app.launcher.radio.constant;


import android.util.Log;

import java.lang.reflect.Method;

public class SystemPropertiesPresenter {

    private static final String TAG = "SystemPropertiesPresenter";
    private volatile static SystemPropertiesPresenter mIstance = null;

    private SystemPropertiesPresenter(){

    }

    public static SystemPropertiesPresenter getInstance(){
        if (null == mIstance){
            synchronized (SystemPropertiesPresenter.class)
            {
                if (null == mIstance)
                {
                    mIstance = new SystemPropertiesPresenter();
                }
            }
        }
        return mIstance;
    }

    //利用反射机制获取系统属性
    public String getProperty(String key, String defaultValue)
    {

        String value = defaultValue;
        try
        {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, defaultValue));
        } catch (Exception e)
        {
            e.printStackTrace();
        }finally
        {
            Log.d(TAG, " getProperty: value = "+value);
            return value;
        }
    }

    //利用反射机制设置系统属性
    public void setProperty(String key, String value)
    {
        try
        {
            Log.d(TAG, " setProperty key = "+key+" value = "+value);
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
