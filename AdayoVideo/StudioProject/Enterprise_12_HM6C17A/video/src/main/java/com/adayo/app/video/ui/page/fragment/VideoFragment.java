package com.adayo.app.video.ui.page.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.adayo.app.video.R;
import com.adayo.app.video.data.constant.VideoConst;
import com.adayo.app.video.databinding.FragmentVideoBinding;
import com.adayo.app.video.ui.page.fragment.browse.BrowseFragment;
import com.adayo.app.video.ui.page.fragment.detail.DetailFragment;
import com.adayo.app.video.ui.state.MainViewModel;
import com.google.android.material.animation.AnimationUtils;

public class VideoFragment extends AbsFragment<FragmentVideoBinding> {
    private MainViewModel mStateViewModel;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    protected FragmentVideoBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentVideoBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initView() {
        super.initView();
        mViewBinding.fcvVideoDetail.bringToFront();
    }

    @Override
    protected void initData() {
        super.initData();
        mStateViewModel.getInfoRequest().getDeviceStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            Fragment detailFragment = getChildFragmentManager().findFragmentByTag(VideoConst.FRAG_TAG_LO_VIDEO_DETAIL);
            if (aBoolean) {
                delStateLayout(mViewBinding.flVideoStateHostFull, R.layout.in_state_no_device);
                if (detailFragment == null) {
                    getChildFragmentManager().beginTransaction()
                                             .add(mViewBinding.fcvVideoDetail.getId(), DetailFragment.class, null, VideoConst.FRAG_TAG_LO_VIDEO_DETAIL)
                                             .commitNow();
                }
            } else {
                addStateLayout(mViewBinding.flVideoStateHostFull, R.layout.in_state_no_device);
                if (detailFragment != null) {
                    getChildFragmentManager().beginTransaction()
                                             .remove(detailFragment)
                                             .commitNow();
                }
            }
        });
        mStateViewModel.getInfoRequest().getLoadingStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                addStateLayout(mViewBinding.flVideoStateHost, R.layout.in_state_loading);
                ImageView ivLoading = mViewBinding.flVideoStateHost.findViewById(R.id.iv_loading_ico);
                if (ivLoading != null) {
                    Drawable drawable = ivLoading.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                        if (!animationDrawable.isRunning()) {
                            animationDrawable.start();
                        }
                    }
                }
            } else {
                ImageView ivLoading = mViewBinding.flVideoStateHost.findViewById(R.id.iv_loading_ico);
                if (ivLoading != null) {
                    Drawable drawable = ivLoading.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                        if (animationDrawable.isRunning()) {
                            animationDrawable.stop();
                        }
                    }
                }
                delStateLayout(mViewBinding.flVideoStateHost, R.layout.in_state_loading);
            }
        });
        mStateViewModel.getInfoRequest().getEmptyStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                addStateLayout(mViewBinding.flVideoStateHost, R.layout.in_state_empty);
            } else {
                delStateLayout(mViewBinding.flVideoStateHost, R.layout.in_state_empty);
            }
        });
        mStateViewModel.getInfoRequest().getBrowseStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            Fragment browseFragment = getChildFragmentManager().findFragmentByTag(VideoConst.FRAG_TAG_LO_VIDEO_BROWSE);
            if (aBoolean) {
                if (browseFragment == null) {
                    getChildFragmentManager().beginTransaction()
                                             .add(mViewBinding.fcvVideoBrowse.getId(), BrowseFragment.class, null, VideoConst.FRAG_TAG_LO_VIDEO_BROWSE)
                                             .commitNow();
                }
            } else {
                if (browseFragment != null) {
                    getChildFragmentManager().beginTransaction()
                                             .remove(browseFragment)
                                             .commitNow();
                }
            }
        });
        mStateViewModel.getFillLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(mViewBinding.getRoot());
            if (aBoolean) {
                constraintSet.connect(mViewBinding.fcvVideoDetail.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
            } else {
                constraintSet.connect(mViewBinding.fcvVideoDetail.getId(), ConstraintSet.END, mViewBinding.glVideo0.getId(), ConstraintSet.START);
            }
            constraintSet.applyTo(mViewBinding.getRoot());
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR).setDuration(750L);
            TransitionManager.beginDelayedTransition(mViewBinding.fcvVideoDetail, changeBounds);
        });
    }
}
