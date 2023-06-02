package com.adayo.app.music.ui.page.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.adayo.app.music.R;
import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.databinding.FragmentMusicBinding;
import com.adayo.app.music.ui.page.fragment.browse.BrowseFragment;
import com.adayo.app.music.ui.page.fragment.detail.DetailFragment;
import com.adayo.app.music.ui.state.MainViewModel;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

public class MusicFragment extends AbsFragment<FragmentMusicBinding> {
    private MainViewModel mStateViewModel;

    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    protected FragmentMusicBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMusicBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initView() {
        super.initView();
        Fragment detailFragment = getChildFragmentManager().findFragmentByTag(MusicConst.FRAG_TAG_LO_MUSIC_DETAIL);
        if (detailFragment == null) {
            getChildFragmentManager().beginTransaction()
                                     .add(mViewBinding.fcvMusicDetail.getId(), DetailFragment.class, null, MusicConst.FRAG_TAG_LO_MUSIC_DETAIL)
                                     .commitNow();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mStateViewModel.getInfoRequest().getDeviceStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                delStateLayout(mViewBinding.flMusicStateHost, R.layout.in_state_no_device);
            } else {
                addStateLayout(mViewBinding.flMusicStateHost, R.layout.in_state_no_device);
            }
        });
        mStateViewModel.getInfoRequest().getLoadingStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                addStateLayout(mViewBinding.flMusicStateHost, R.layout.in_state_loading);
                ImageView ivLoading = mViewBinding.flMusicStateHost.findViewById(R.id.iv_loading_ico);
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
                ImageView ivLoading = mViewBinding.flMusicStateHost.findViewById(R.id.iv_loading_ico);
                if (ivLoading != null) {
                    Drawable drawable = ivLoading.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                        if (animationDrawable.isRunning()) {
                            animationDrawable.stop();
                        }
                    }
                }
                delStateLayout(mViewBinding.flMusicStateHost, R.layout.in_state_loading);
            }
        });
        mStateViewModel.getInfoRequest().getEmptyStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                addStateLayout(mViewBinding.flMusicStateHost, R.layout.in_state_empty);
            } else {
                delStateLayout(mViewBinding.flMusicStateHost, R.layout.in_state_empty);
            }
        });
        mStateViewModel.getInfoRequest().getBrowseStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            Fragment fragment = getChildFragmentManager().findFragmentByTag(MusicConst.FRAG_TAG_LO_MUSIC_BROWSE);
            if (aBoolean) {
                if (fragment == null) {
                    getChildFragmentManager().beginTransaction()
                                             .add(mViewBinding.fcvMusicBrowse.getId(), BrowseFragment.class, null, MusicConst.FRAG_TAG_LO_MUSIC_BROWSE)
                                             .commitNow();
                }
            } else {
                if (fragment != null) {
                    getChildFragmentManager().beginTransaction()
                                             .remove(fragment)
                                             .commitNow();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SrcMngSwitchManager.getInstance().notifyServiceUIChange(AdayoSource.ADAYO_SOURCE_USB, requireContext().getPackageName());
    }
}
