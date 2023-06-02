package com.adayo.app.music.ui.page.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.activity.IActivitySkinEventHandler;
import com.adayo.proxy.aaop_hskin.activity.ISkinActivity;
import com.adayo.proxy.aaop_hskin.view.IViewCreateListener;
import com.lt.library.base.activity.BaseActivity;

/**
 * @Auth: LinTan
 * @Date: 2021/11/19 15:16
 * @Desc: 封装业务相关的Activity基类(e.g., 换肤)
 */

public abstract class AbsActivity<VB extends ViewBinding> extends BaseActivity<VB> implements ISkinActivity {
    private IActivitySkinEventHandler mSkinEventHandler;
    private boolean mFirstTimeApplySkin = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSkinEventHandler = AAOP_HSkin.newActivitySkinEventHandler()
                                      .setSwitchSkinImmediately(isSwitchSkinImmediately())
                                      .setSupportSkinChange(isSupportSkinChange())
                                      .setWindowBackgroundResource(getWindowBackgroundResource())
                                      .setNeedDelegateViewCreate(isNeedDelegateViewCreate());
        mSkinEventHandler.setViewCreateListener(new IViewCreateListener() {
            @Override
            public View beforeCreate(View parent, String name, Context context, AttributeSet attributeSet) {
                return getDelegate().createView(parent, name, context, attributeSet);
            }

            @Override
            public void afterCreated(View parent, String name, Context context, AttributeSet attributeSet) {
            }
        });
        mSkinEventHandler.onCreate(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSkinEventHandler.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstTimeApplySkin) {
            mSkinEventHandler.onViewCreated();
            mFirstTimeApplySkin = false;
        }
        mSkinEventHandler.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mSkinEventHandler.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSkinEventHandler.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSkinEventHandler.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSkinEventHandler.onDestroy();
    }

    /**
     * 告知当切换皮肤时, 是否立刻刷新当前Activity界面
     *
     * @return false: onResume时刷新; true: 立刻刷新
     */
    @Override
    public boolean isSwitchSkinImmediately() {
        return true;
    }

    /**
     * 告知当前Activity界面是否支持换肤
     *
     * @return false: 不支持; true: 支持
     */
    @Override
    public boolean isSupportSkinChange() {
        return true;
    }

    /**
     * 告知当前Activity界面是否需要在框架内代理View创建
     *
     * @return false: 无需; true: 需要
     */
    @Override
    public boolean isNeedDelegateViewCreate() {
        return true;
    }

    /**
     * 告知当前Activity的Window的background资源, 换肤时会寻找对应的资源替换
     *
     * @return -1为无效值, 即不设置该属性
     */
    @Override
    public int getWindowBackgroundResource() {
        return -1;
    }

    /**
     * 当前Activity界面在换肤时收到的回调, 可以在此回调内做一些其他事情
     * eg: 通知WebView内的页面切换到夜间模式等
     */
    @Override
    public void handleSkinChange() {
    }
}
