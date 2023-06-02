package com.adayo.app.music.ui.page.fragment.browse.component;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.app.music.R;
import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.databinding.FragmentMusicBrowseComponentBinding;
import com.adayo.app.music.player.PlayerManager;
import com.adayo.app.music.ui.page.adapter.rcv.browse.impl.Id3EntityAdapter;
import com.adayo.app.music.ui.page.adapter.rcv.browse.impl.Id3HeaderAdapter;
import com.adayo.app.music.ui.page.fragment.AbsFragment;
import com.adayo.app.music.ui.state.MainViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.media.bean.FileInfo;
import com.adayo.proxy.media.bean.MetadataInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlbumFragment extends AbsFragment<FragmentMusicBrowseComponentBinding> {
    private MainViewModel mStateViewModel;
    private Id3HeaderAdapter mHeaderAdapter;
    private Id3EntityAdapter mEntityAdapter;

    public static AlbumFragment newInstance() {
        return new AlbumFragment();
    }

    @Override
    protected FragmentMusicBrowseComponentBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMusicBrowseComponentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initView() {
        super.initView();
        mHeaderAdapter = new Id3HeaderAdapter();
        mEntityAdapter = new Id3EntityAdapter();
        ConcatAdapter concatAdapter = new ConcatAdapter(mHeaderAdapter, mEntityAdapter);
        mViewBinding.rcvList.setLayoutManager(new LinearLayoutManager(requireContext()));
        mViewBinding.rcvList.setAdapter(concatAdapter);
        RecyclerView.ItemAnimator itemAnimator = mViewBinding.rcvList.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setRemoveDuration(0);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mStateViewModel.getInfoRequest().getMetadataLoadingStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                addStateLayout(mViewBinding.flStateHost, R.layout.in_state_loading);
                ImageView ivLoading = mViewBinding.flStateHost.findViewById(R.id.iv_loading_ico);
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
                MetadataInfo metadataInfo = PlayerManager.getInstance().getPlayerLiveData().getMetadataLiveData().getValue();
                if (metadataInfo == null) {
                    mStateViewModel.getInfoRequest().requireAlbumList(null);
                } else {
                    mStateViewModel.getInfoRequest().requireAlbumList(metadataInfo.getId3Info().getAlbumName());
                }
                ImageView ivLoading = mViewBinding.flStateHost.findViewById(R.id.iv_loading_ico);
                if (ivLoading != null) {
                    Drawable drawable = ivLoading.getDrawable();
                    if (drawable instanceof AnimationDrawable) {
                        AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                        if (animationDrawable.isRunning()) {
                            animationDrawable.stop();
                        }
                    }
                }
                delStateLayout(mViewBinding.flStateHost, R.layout.in_state_loading);
            }
        });
        mStateViewModel.getInfoRequest().getAlbumListLiveData().observe(getViewLifecycleOwner(), objects -> {
            if (objects == null || objects.isEmpty()) {
                return;
            }
            Object o = objects.get(0);
            mViewBinding.rcvList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (o instanceof FileInfo) {
                        FileInfo fileInfo = (FileInfo) o;
                        mHeaderAdapter.submitList(new ArrayList<String>() {{
                            add(fileInfo.getMetadataInfo().getId3Info().getAlbumName());
                        }});
                    } else {
                        mHeaderAdapter.submitList(null);
                    }
                    mViewBinding.rcvList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            mEntityAdapter.submitList((List<Object>) objects);
        });
        PlayerManager.getInstance().getPlayerLiveData().getNodeInfoLiveData().observe(getViewLifecycleOwner(), nodeInfo -> {
            if (nodeInfo == null) {
                return;
            }
            String filePath = nodeInfo.getNodePath();
            if (Objects.equals(filePath, mEntityAdapter.getSelectedFilePath())) {
                return;
            }// TODO: 2022/3/1 待修复切换Tab页, 未高亮新Tab页下列表中目标歌曲的问题
            mEntityAdapter.notifyItemSelected(filePath, mViewBinding.rcvList, true, mHeaderAdapter.getItemCount());
        });
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mHeaderAdapter.setOnItemClickListener((view, position) -> {
            mStateViewModel.getInfoRequest().requireAlbumList(null);
        });
        mEntityAdapter.setOnItemClickListener((view, position) -> {
            List<Object> currentList = mEntityAdapter.getCurrentList();
            Object o = currentList.get(position);
            if (o instanceof String) {
                mStateViewModel.getInfoRequest().requireAlbumList((String) o);
            } else if (o instanceof FileInfo) {
                List<FileInfo> fileInfos = new ArrayList<>();
                for (Object o1 : currentList) {
                    if (o1 instanceof FileInfo) {
                        fileInfos.add((FileInfo) o1);
                    }
                }
                PlayerManager.getInstance().getPlayerControl().start(MusicConst.AUDIO_ZONE_CODE, fileInfos, fileInfos.get(position));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AAOP_HSkin.with(mViewBinding.rcvList)
                  .applySkin(true);
    }
}
