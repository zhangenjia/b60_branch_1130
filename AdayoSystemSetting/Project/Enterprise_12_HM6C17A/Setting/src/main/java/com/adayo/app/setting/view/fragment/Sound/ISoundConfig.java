package com.adayo.app.setting.view.fragment.Sound;

import android.content.res.Configuration;

public interface ISoundConfig <T, V>{
    void registerFragment(T fragment, V viewbinding);
    void operationFragment();
    void onStart();
    void onResume();
    void initView();
    void initData();
    void initEvent();
    void onHiddenChanged(boolean isHidden);
    void onConfigurationChanged(Configuration newConfig);

}
