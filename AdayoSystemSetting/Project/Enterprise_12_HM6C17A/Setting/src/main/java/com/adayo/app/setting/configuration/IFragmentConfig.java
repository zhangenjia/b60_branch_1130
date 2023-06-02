package com.adayo.app.setting.configuration;

import android.content.res.Configuration;

public interface IFragmentConfig<T, V> {
    void registerFragment(T fragment, V viewbinding);

    void bindData();

    void initData();

    void initView();

    void initEvent();

    void hideView();

    void onStart();

    void onResume();

    void onHiddenChanged(boolean isHidden);

    void onDestroy();

   void onConfigurationChanged(Configuration newConfig);
}
