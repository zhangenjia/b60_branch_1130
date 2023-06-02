package com.adayo.app.systemui.utils;

import com.adayo.app.systemui.configs.SystemUIContent;
import com.google.gson.Gson;

public class GsonUtils {
    private static Gson gson;

    public static <T>T jsonToObject(String str, Class<T> classOfT){
        if(null == gson){
            gson = new Gson();
        }
        T objectInfo = null;
        try {
            objectInfo = gson.fromJson(str, classOfT);
        }catch (Exception e){
            LogUtil.d(SystemUIContent.TAG, e.getMessage());
        }
        return objectInfo;
    }
}
