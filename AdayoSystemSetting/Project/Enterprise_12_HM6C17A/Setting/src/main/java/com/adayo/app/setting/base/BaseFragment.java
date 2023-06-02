package com.adayo.app.setting.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.viewbinding.ViewBinding;

import com.adayo.app.base.LogUtil;
import com.lt.library.util.context.ContextUtil;



public abstract class BaseFragment<V extends ViewBinding> extends Fragment {
    protected static final String BDL_KEY_PARAM1 = "param1";
    protected static final String BDL_KEY_PARAM2 = "param2";
    protected V mViewBinding;
    protected FragmentActivity mActivity;

    @TargetApi(value = Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.debugD(this.getClass().getName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.debugD(this.getClass().getName());
        mViewBinding = bindView(inflater, container);
        return mViewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.debugD(this.getClass().getName());
        bindData(getArguments(), savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onHiddenChanged(boolean isHidden) {
        super.onHiddenChanged(isHidden);
        LogUtil.debugD(this.getClass().getName());
        if (!isHidden) {
            showView();
        } else {
            hideView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        saveState(outState);
        super.onSaveInstanceState(outState);
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        freeEvent();
        freeData();
        freeView();
        mViewBinding = null;
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.debugD(this.getClass().getName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
        LogUtil.debugD(this.getClass().getName());
    }

    protected Context getAppContext() {
        return ContextUtil.getInstance().getApplicationContext();
    }

    protected abstract V bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
    }protected void initView() {
    }protected void initData() {
    }protected void initEvent() {
    }protected void showView() {
    }

    protected void hideView() {
    }

    protected void saveState(@NonNull Bundle outState) {
    }protected void freeEvent() {
    }protected void freeData() {
    }protected void freeView() {
    }}
