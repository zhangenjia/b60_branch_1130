package com.adayo.app.launcher.communicationbase;

import android.util.Log;

import java.lang.reflect.Method;


public class SystemPropertiesUtil {
    private static final String TAG = "SystemPropertiesUtil";
    private static SystemPropertiesUtil mSystemPropertiesUtil;

    public static SystemPropertiesUtil getInstance() {
        if (null == mSystemPropertiesUtil) {
            synchronized (SystemPropertiesUtil.class) {
                if (null == mSystemPropertiesUtil) {
                    mSystemPropertiesUtil = new SystemPropertiesUtil();
                }
            }
        }
        return mSystemPropertiesUtil;
    }
    private Method getLongMethod = null;
    private Method getStringMethod = null;
    private Method getIntMethod = null;
    private Method getBooleanMethod = null;

    public long getLong(final String key, final long def) {
        try {
            if (getLongMethod == null) {
                getLongMethod = Class.forName("android.os.SystemProperties").getMethod("getLong", String.class, long.class);
            }
            return ((Long) getLongMethod.invoke(null, key, def)).longValue();
        } catch (Exception e) {
            return def;
        }
    }

    public String getStringMethod(final String key, final String def) {
        try {
            if (getStringMethod == null) {
                getStringMethod = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            }
            return ((String) getStringMethod.invoke(null, key, def)).toString();
        } catch (Exception e) {
            return def;
        }
    }

    public int getIntMethod(final String key, final int def) {
        try {
            if (getIntMethod == null) {
                getIntMethod = Class.forName("android.os.SystemProperties")
                        .getMethod("getInt", String.class, int.class);
            }
            return ((Integer) getIntMethod.invoke(null, key, def)).intValue();
        } catch (Exception e) {
            return def;
        }
    }

    public boolean getBooleanMethod(final String key, final boolean def) {
        try {
            if (getBooleanMethod == null) {
                getBooleanMethod = Class.forName("android.os.SystemProperties").getMethod("getBoolean", String.class, boolean.class);
            }
            return ((Boolean) getBooleanMethod.invoke(null, key, def)).booleanValue();
        } catch (Exception e) {
            return def;
        }
    }
    //通过反射设置系统属性
    public  void setProperty(String key, String value) {
        Log.d(TAG, "setProperty: "+value);
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
