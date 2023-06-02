package com.adayo.btsetting.sdk;

import android.content.Context;

/**
 * @author Y4134
 */
public interface ISdkManager {
    /**
     * 初始化
     * @param context 上下文
     */
    void init(Context context);

    /**
     * 解注册
     * @param context 上下文
     */
    void unInit(Context context);
}
