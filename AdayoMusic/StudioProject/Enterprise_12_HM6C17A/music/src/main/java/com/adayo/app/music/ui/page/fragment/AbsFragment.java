package com.adayo.app.music.ui.page.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.lt.library.base.fragment.BaseFragment;
import com.lt.library.util.LogUtil;

/**
 * @Auth: LinTan
 * @Date: 2021/11/19 15:16
 * @Desc: 封装业务相关的Fragment基类(e.g., 换肤)
 */

public abstract class AbsFragment<VB extends ViewBinding> extends BaseFragment<VB> {
    @SuppressWarnings("unchecked")
    protected <T extends Fragment> Fragment findFragment(@NonNull Fragment fragment, @NonNull Class<T> cls) {
        LogUtil.d("find fragment: " + fragment.getClass().getSimpleName());
        if (cls.isInstance(fragment)) {
            return (T) fragment;
        }
        return findFragment(fragment.requireParentFragment(), cls);
    }

    protected void addStateLayout(ViewGroup viewGroup, int layoutId) {
        View view = viewGroup.findViewWithTag(layoutId);
        if (view == null) {
            view = View.inflate(requireContext(), layoutId, null);
            view.setTag(layoutId);
            viewGroup.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        }
    }

    protected void delStateLayout(ViewGroup viewGroup, int layoutId) {
        View view = viewGroup.findViewWithTag(layoutId);
        if (view != null) {
            viewGroup.removeView(view);
        }
    }
}
