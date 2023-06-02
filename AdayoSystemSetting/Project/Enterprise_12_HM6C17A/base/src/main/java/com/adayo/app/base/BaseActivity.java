package com.adayo.app.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.viewbinding.ViewBinding;

import com.adayo.proxy.aaop_hskin.activity.IActivitySkinEventHandler;
import com.adayo.proxy.aaop_hskin.activity.ISkinActivity;
import com.adayo.proxy.aaop_hskin.view.IViewCreateListener;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.lt.library.util.context.ContextUtil;

import java.lang.ref.WeakReference;

/**
 * @作者: LinTan
 * @日期: 2020/8/9 11:00
 * @版本: 1.0
 * @描述: //BaseActivity
 * 1.0: Initial Commit
 */

public abstract class BaseActivity<V extends ViewBinding> extends AppCompatActivity implements ISkinActivity {
    protected V mViewBinding;
    private IActivitySkinEventHandler mSkinEventHandler;
    private final static String TAG = BaseActivity.class.getSimpleName();
    private boolean isrun = true;
    private View mView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.debugD(TAG, "");
        mSkinEventHandler = AAOP_HSkinHelper.initActivity(this, new ActivityViewCreateListener(this));
        super.onCreate(savedInstanceState);
        bindData(getIntent().getExtras(), savedInstanceState);
        inflateView(savedInstanceState);

    }

    protected void ActivityonInflateFinished(View view) {
        ViewGroup viewParent = (ViewGroup) view.getParent();
        LogUtil.d(TAG,"viewParent ="+viewParent);
        if (viewParent != null) {
            LogUtil.w(TAG,"viewParent != NULL");
            viewParent.removeAllViews();
        }
        mView = view;
        mViewBinding = bindView(view);
        LogUtil.d(TAG,"mView ="+mView+" mViewBinding ="+mViewBinding);
        setContentView(view);
//        initView();
//        initData();
//        initEvent();
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
//        mSkinEventHandler.onDestroy();
        mViewBinding = null;
    }

    protected Context getAppContext() {
        return ContextUtil.getInstance().getApplicationContext();
    }

    protected abstract V bindView(View view);//绑定视图(eg: ViewBinding)

    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
    }//绑定数据(eg: Bundle, SaveInstanceState, SharedPreferences)

    protected void inflateView(@Nullable Bundle savedInstanceState) {

    }

    protected void initView() {
    }//初始化视图

    protected void initData() {
    }//初始化数据

    protected void initEvent() {
    }//初始化事件(eg: OnClickListener)

    protected void saveState(@NonNull Bundle outState) {
    }//存储临时数据(eg: SaveInstanceState)

    protected void freeEvent() {
    }//释放事件(eg: OnClickListener)

    protected void freeData() {
    }//释放数据

    protected void freeView() {
    }//释放视图

    private static class ActivityViewCreateListener implements IViewCreateListener {
        WeakReference<BaseActivity> activityWeakReference;

        public ActivityViewCreateListener(BaseActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public View beforeCreate(View parent, String name, Context context, AttributeSet attrs) {
            BaseActivity activity = activityWeakReference.get();
            if (null == activity) {
                return null;
            }
            return activity.getDelegate().createView(parent, name, context, attrs);
            //如果上面代码爆红，就把上面的代码删掉，用下面的
            //return activity.onCreateView(parent, name, context, attrs);
        }

        @Override
        public void afterCreated(View view, String s, Context context, AttributeSet attributeSet) {

        }
    }

    @Override
    public boolean isSwitchSkinImmediately() {
        return true;
    }

    @Override
    public boolean isSupportSkinChange() {
        return true;
    }

    @Override
    public boolean isNeedDelegateViewCreate() {
        return true;
    }

    @Override
    public int getWindowBackgroundResource() {
        return 0;
    }

    @Override
    public void handleSkinChange() {

    }

}
