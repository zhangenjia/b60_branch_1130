package com.adayo.app.music.ui.page.fragment.browse.component;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
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
import com.adayo.app.music.ui.page.adapter.rcv.browse.impl.DirEntityAdapter;
import com.adayo.app.music.ui.page.adapter.rcv.browse.impl.DirHeaderAdapter;
import com.adayo.app.music.ui.page.fragment.AbsFragment;
import com.adayo.app.music.ui.state.MainViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.media.bean.FileInfo;
import com.adayo.proxy.media.bean.NodeInfo;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DirFragment extends AbsFragment<FragmentMusicBrowseComponentBinding> {
    private MainViewModel mStateViewModel;
    private DirHeaderAdapter mHeaderAdapter;
    private DirEntityAdapter mEntityAdapter;

    public static DirFragment newInstance() {
        return new DirFragment();
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
        mHeaderAdapter = new DirHeaderAdapter();
        mEntityAdapter = new DirEntityAdapter();
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
        mStateViewModel.getInfoRequest().getFolderAndFileLoadingStateLiveData().observe(getViewLifecycleOwner(), aBoolean -> {
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
        mStateViewModel.getInfoRequest().getDirListLiveData().observe(getViewLifecycleOwner(), listListPair -> {
            if (listListPair == null) {
                mHeaderAdapter.submitList(null);
                mEntityAdapter.submitList(null);
                return;
            }
            List<NodeInfo> folders = listListPair.first;
            List<FileInfo> files = listListPair.second;
            if (!folders.isEmpty()) {
                NodeInfo nodeInfo = folders.get(0);
                Integer nodeParentLevel = nodeInfo.getNodeParentLevel();
                if (nodeParentLevel != null) {
                    mViewBinding.rcvList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (nodeParentLevel > nodeInfo.getDevicePathLevel()) {
                                mHeaderAdapter.submitList(new ArrayList<Pair<String, String>>() {{
                                    add(new Pair<>(nodeInfo.getNodeParentName(), Paths.get(nodeInfo.getNodeParentPath()).getParent().toAbsolutePath().toString()));
                                }});
                            } else {
                                mHeaderAdapter.submitList(null);
                            }
                            mViewBinding.rcvList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            } else if (!files.isEmpty()) {
                FileInfo fileInfo = files.get(0);
                Integer nodeParentLevel = fileInfo.getNodeInfo().getNodeParentLevel();
                if (nodeParentLevel != null) {
                    mViewBinding.rcvList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (nodeParentLevel > fileInfo.getNodeInfo().getDevicePathLevel()) {
                                mHeaderAdapter.submitList(new ArrayList<Pair<String, String>>() {{
                                    add(new Pair<>(fileInfo.getNodeInfo().getNodeParentName(), Paths.get(fileInfo.getNodeInfo().getNodeParentPath()).getParent().toAbsolutePath().toString()));
                                }});
                            } else {
                                mHeaderAdapter.submitList(null);
                            }
                            mViewBinding.rcvList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            } else {
                return;
            }
            List<Object> objects = new ArrayList<>();
            objects.addAll(folders);
            objects.addAll(files);
            mEntityAdapter.submitList(objects);
            NodeInfo nodeInfo = PlayerManager.getInstance().getPlayerLiveData().getNodeInfoLiveData().getValue();
            if (nodeInfo != null) {
                mEntityAdapter.notifyItemSelected(nodeInfo.getNodePath(), mViewBinding.rcvList, true, mHeaderAdapter.getItemCount());
            }
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
            Pair<String, String> pair = mHeaderAdapter.getCurrentList().get(0);
            mStateViewModel.getInfoRequest().requireDirList(pair.second);
        });
        mEntityAdapter.setOnItemClickListener((view, position) -> {
            List<Object> currentList = mEntityAdapter.getCurrentList();
            Object o = currentList.get(position);
            if (o instanceof NodeInfo) {
                mStateViewModel.getInfoRequest().requireDirList(((NodeInfo) o).getNodePath());
            } else if (o instanceof FileInfo) {
                List<FileInfo> fileInfos = new ArrayList<>();
                for (Object o1 : currentList) {
                    if (o1 instanceof FileInfo) {
                        fileInfos.add((FileInfo) o1);
                    }
                }
                int i = currentList.size() - fileInfos.size();
                PlayerManager.getInstance().getPlayerControl().start(MusicConst.AUDIO_ZONE_CODE, fileInfos, fileInfos.get(position - i));
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
