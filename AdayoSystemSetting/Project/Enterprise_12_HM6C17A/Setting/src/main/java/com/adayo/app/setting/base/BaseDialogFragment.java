package com.adayo.app.setting.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Dimension;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.viewbinding.ViewBinding;

import com.adayo.app.base.LogUtil;
import com.lt.library.base.dialogfragment.listenter.OnNegativeButtonClickListener;
import com.lt.library.base.dialogfragment.listenter.OnNeutralButtonClickListener;
import com.lt.library.base.dialogfragment.listenter.OnPositiveButtonClickListener;
import com.lt.library.util.DensityUtil;
import com.lt.library.util.ScreenUtil;
import com.lt.library.util.context.ContextUtil;



public abstract class BaseDialogFragment<V extends ViewBinding> extends DialogFragment {

    protected V mViewBinding;
    protected FragmentActivity mActivity;
    protected OnPositiveButtonClickListener mOnPositiveButtonClickListener;
    protected OnNegativeButtonClickListener mOnNegativeButtonClickListener;
    protected OnNeutralButtonClickListener mOnNeutralButtonClickListener;
    private Integer mWindowType;
    private Integer mLayoutWidth;
    private Integer mLayoutHeight;
    private Integer mOffsetX;
    private Integer mOffsetY;
    private Integer mGravity;
    private Integer mWindowAnimation;
    private Float mDimAmount;
    private Boolean mIsOutCancel;
    private boolean mIsKeepSystemUiState;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinding = bindView(inflater, container);
        return mViewBinding.getRoot();
    }@Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData(getArguments(), savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mIsOutCancel != null) {
            getDialog().setCanceledOnTouchOutside(mIsOutCancel);
        }
        if (mWindowType != null) {
            getDialog().getWindow().setType(mWindowType);
        }
        initView();
        initData();
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Window aW = mActivity.getWindow();
        Window dW = getDialog().getWindow();
        if (aW != null && dW != null) {
            if (mIsKeepSystemUiState) {
                keepSystemUiState(aW, dW);
            } else {
                super.onStart();
            }
            initParam(dW);
        } else {
            LogUtil.w("","activity getWindow = " + aW + ", dialog getWindow = " + dW);
            super.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        freeEvent();
        freeData();
        freeView();
        mViewBinding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            super.show(manager, tag);
        }else {
            LogUtil.w("BaseDialogFragment","dialogFragment added, tag = " + tag);
        }
    }

    @Override
    public void dismiss() {
        if (isAdded()) {
            super.dismiss();
        } else {
            LogUtil.w("BaseDialogFragment","dialogFragment not added");
        }
    }

    private void initParam(@NonNull Window dW) {
        dW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (mLayoutWidth != null && mLayoutHeight != null) {
            dW.setLayout(DensityUtil.dp2px(mLayoutWidth),
                         DensityUtil.dp2px(mLayoutHeight));
        } else {
            int screenWidth = ScreenUtil.getScreenWidth();
            int screenHeight = ScreenUtil.getScreenHeight();
            boolean b = screenHeight > screenWidth;
            dW.setLayout((int) (b ? screenWidth * 0.75 : screenWidth * 0.50),
                         (int) (b ? screenHeight * 0.25 : screenHeight * 0.50));
        }
        if (mOffsetX != null) {
            final WindowManager.LayoutParams attrs = dW.getAttributes();
            attrs.x = DensityUtil.dp2px(mOffsetX);
            dW.setAttributes(attrs);
        }
        if (mOffsetY != null) {
            final WindowManager.LayoutParams attrs = dW.getAttributes();
            attrs.y = DensityUtil.dp2px(mOffsetY);
            dW.setAttributes(attrs);
        }
        if (mGravity != null) {
            dW.setGravity(mGravity);
        }
        if (mWindowAnimation != null) {
            dW.setWindowAnimations(mWindowAnimation);
        }
        if (mDimAmount != null) {
            dW.setDimAmount(mDimAmount);
        }
    }

    private void keepSystemUiState(@NonNull Window aW, @NonNull Window dW) {
        int aSystemUiVisibility = aW.getDecorView().getSystemUiVisibility();
        int dSystemUiVisibility = dW.getDecorView().getSystemUiVisibility();
        boolean isDiffVisibility = aSystemUiVisibility != dSystemUiVisibility;
        if (isDiffVisibility) {
            dW.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }super.onStart();
        if (isDiffVisibility) {
            dW.getDecorView().setSystemUiVisibility(aSystemUiVisibility);
            dW.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }}protected void callOnPositiveButtonClick(View view, Object object) {
        if (mOnPositiveButtonClickListener != null) {
            mOnPositiveButtonClickListener.onPositiveButtonClick(this, view, object);
        }
    }

    protected void callOnNegativeButtonClick(View view, Object object) {
        if (mOnNegativeButtonClickListener != null) {
            mOnNegativeButtonClickListener.onNegativeButtonClick(this, view, object);
        }
    }

    protected void callOnNeutralButtonClick(View view, Object object) {
        if (mOnNeutralButtonClickListener != null) {
            mOnNeutralButtonClickListener.onNeutralButtonClick(this, view, object);
        }
    }

    protected Context getAppContext() {
        return ContextUtil.getInstance().getApplicationContext();
    }

    protected abstract V bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
    }protected void initView() {
    }protected void initData() {
    }protected void initEvent() {
    }protected void saveState(@NonNull Bundle outState) {
    }protected void freeEvent() {
    }protected void freeData() {
    }protected void freeView() {
    }public BaseDialogFragment<V> setWindowType(Integer windowType) {
        mWindowType = windowType;
        return this;
    }public BaseDialogFragment<V> setLayout(@Dimension(unit = Dimension.DP) int width, int height) {
        mLayoutWidth = width;
        mLayoutHeight = height;
        return this;
    }public BaseDialogFragment<V> setOffsetX(@Dimension(unit = Dimension.DP) int x) {
        mOffsetX = x;
        return this;
    }public BaseDialogFragment<V> setOffsetY(@Dimension(unit = Dimension.DP) int y) {
        mOffsetY = y;
        return this;
    }public BaseDialogFragment<V> setGravity(int gravity) {
        mGravity = gravity;
        return this;
    }public BaseDialogFragment<V> setWindowAnimation(@StyleRes int windowAnimation) {
        mWindowAnimation = windowAnimation;
        return this;
    }public BaseDialogFragment<V> setDimAmount(@FloatRange(from = 0, to = 1) float dimAmount) {
        mDimAmount = dimAmount;
        return this;
    }public BaseDialogFragment<V> setOutCancel(boolean isOutCancel) {
        mIsOutCancel = isOutCancel;
        return this;
    }public BaseDialogFragment<V> setKeepSystemUiState(boolean isKeepSystemUiState) {
        mIsKeepSystemUiState = isKeepSystemUiState;
        return this;
    }public BaseDialogFragment<V> setOnPositiveButtonClickListener(OnPositiveButtonClickListener onPositiveButtonClickListener) {
        mOnPositiveButtonClickListener = onPositiveButtonClickListener;
        return this;
    }

    public BaseDialogFragment<V> setOnNegativeButtonClickListener(OnNegativeButtonClickListener onNegativeButtonClickListener) {
        mOnNegativeButtonClickListener = onNegativeButtonClickListener;
        return this;
    }

    public BaseDialogFragment<V> setOnNeutralButtonClickListener(OnNeutralButtonClickListener onNeutralButtonClickListener) {
        mOnNeutralButtonClickListener = onNeutralButtonClickListener;
        return this;
    }
}
