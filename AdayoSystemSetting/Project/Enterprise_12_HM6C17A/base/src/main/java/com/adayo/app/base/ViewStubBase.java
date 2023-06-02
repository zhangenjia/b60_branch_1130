package com.adayo.app.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.viewbinding.ViewBinding;

public abstract class ViewStubBase {
    protected BaseActivity mBaseActivity;
    protected ViewBinding ViewBinding;
    protected View mView;

    public ViewStubBase() {
    }

    public void setBaseActivity(BaseActivity BaseActivity) {
        mBaseActivity = BaseActivity;
    }

    public void initViewModel() {

    }

    public void setViewBinding(ViewBinding viewBinding) {
        ViewBinding = viewBinding;
    }

    public void setView(View view) {
        mView = view;
    }

    public void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {

    }

    public void initView() {

    }//初始化视图

    public void initData() {

    }//初始化数据

    public void initEvent() {

    }//初始化事件(eg: OnClickListener)

    public void onResume() {
    }

    public void onStart() {
    }

    public void onPause() {

    }

    public void onConfigurationChanged(Configuration newConfig) {
    }


    public void showOrHide(boolean isShow) {

    }

    public void saveState(@NonNull Bundle outState) {
    }


    public void onDestroy() {
    }
}
