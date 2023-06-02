package com.adayo.app.video.domain.request;

import android.content.Context;

import com.lt.library.util.context.ContextUtil;

/**
 * @Auth: LinTan
 * @Date: 2021/4/23 12:21
 * @Desc: 分离ViewModel中的业务逻辑, 负责处理数据请求
 */

public abstract class AbsRequest {
    protected Context getAppContext() {
        return ContextUtil.getAppContext();
    }
}
