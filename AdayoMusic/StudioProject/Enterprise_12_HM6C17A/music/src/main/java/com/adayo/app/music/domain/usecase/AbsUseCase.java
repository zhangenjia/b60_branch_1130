package com.adayo.app.music.domain.usecase;

import android.content.Context;

import androidx.lifecycle.DefaultLifecycleObserver;

import com.lt.library.util.context.ContextUtil;

/**
 * @Auth: LinTan
 * @Date: 2021/4/22 9:38
 * @Desc: 分离ViewModel中的业务逻辑, 负责界面生命周期相关的业务
 */

public abstract class AbsUseCase implements DefaultLifecycleObserver {
    protected Context getAppContext() {
        return ContextUtil.getAppContext();
    }
}
