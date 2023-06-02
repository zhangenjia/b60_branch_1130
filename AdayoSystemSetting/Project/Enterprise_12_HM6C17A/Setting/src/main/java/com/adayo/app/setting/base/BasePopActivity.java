package com.adayo.app.setting.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.viewbinding.ViewBinding;

import com.adayo.proxy.aaop_hskin.activity.IActivitySkinEventHandler;
import com.adayo.proxy.aaop_hskin.view.IViewCreateListener;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.lt.library.util.context.ContextUtil;

import java.lang.ref.WeakReference;



public abstract class BasePopActivity<V extends ViewBinding> extends AppCompatActivity {
    protected V mViewBinding;
    private IActivitySkinEventHandler mSkinEventHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSkinEventHandler = AAOP_HSkinHelper.initActivity(this, new ActivityViewCreateListener(this));

        super.onCreate(savedInstanceState);
        mViewBinding = bindView();
        setContentView(mViewBinding.getRoot());
        bindData(getIntent().getExtras(), savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        freeEvent();
        freeData();
        freeView();
        mViewBinding = null;
    }

    protected Context getAppContext() {
        return ContextUtil.getInstance().getApplicationContext();
    }

    protected abstract V bindView();protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
    }protected void initView() {
    }protected void initData() {
    }protected void initEvent() {
    }protected void saveState(@NonNull Bundle outState) {
    }protected void freeEvent() {
    }protected void freeData() {
    }protected void freeView() {
    }private static class ActivityViewCreateListener implements IViewCreateListener {
        WeakReference<BasePopActivity> activityWeakReference;

        public ActivityViewCreateListener(BasePopActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public View beforeCreate(View parent, String name, Context context, AttributeSet attrs) {
            BasePopActivity activity = activityWeakReference.get();
            if (null == activity) {
                return null;
            }
            return activity.getDelegate().createView(parent, name, context, attrs);
            }

        @Override
        public void afterCreated(View view, String s, Context context, AttributeSet attributeSet) {

        }
    }
}
