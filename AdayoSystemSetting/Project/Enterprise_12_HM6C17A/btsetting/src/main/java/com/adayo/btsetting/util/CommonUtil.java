package com.adayo.btsetting.util;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author Y4134
 */
public class CommonUtil {

    public static boolean isHighConfig() {
        String projectName = "HM6C17A";
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            projectName = (String) method.invoke((Object) null, "ro.project.name", "HM6C17A");
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isHighConfig = "HM6C17A".equals(projectName);
        Log.d("CommonUtil", "isHighConfig = " + isHighConfig);
        return isHighConfig;
    }
}
