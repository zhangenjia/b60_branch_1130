package com.adayo.app.music.ui.page.fragment.browse.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.databinding.FragmentMusicBrowseComponentBinding;
import com.adayo.app.music.player.PlayerManager;
import com.adayo.app.music.ui.page.adapter.rcv.browse.impl.AllEntityAdapter;
import com.adayo.app.music.ui.page.fragment.AbsFragment;
import com.adayo.app.music.ui.state.MainViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.media.bean.FileInfo;

import java.util.List;
import java.util.Objects;

public class AllFragment extends AbsFragment<FragmentMusicBrowseComponentBinding> {
    private MainViewModel mStateViewModel;
    private AllEntityAdapter mEntityAdapter;

    public static AllFragment newInstance() {
        return new AllFragment();
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
        mEntityAdapter = new AllEntityAdapter();
        mViewBinding.rcvList.setLayoutManager(new LinearLayoutManager(requireContext()));
        mViewBinding.rcvList.setAdapter(mEntityAdapter);
        RecyclerView.ItemAnimator itemAnimator = mViewBinding.rcvList.getItemAnimator();
        if (itemAnimator != null) {
            itemAnimator.setRemoveDuration(0);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mStateViewModel.getInfoRequest().getAllListLiveData().observe(getViewLifecycleOwner(), fileInfos -> {
            if (fileInfos == null) {
                return;
            }
            mEntityAdapter.submitList(fileInfos);
        });
        PlayerManager.getInstance().getPlayerLiveData().getNodeInfoLiveData().observe(getViewLifecycleOwner(), nodeInfo -> {
            if (nodeInfo == null) {
                return;
            }
            String filePath = nodeInfo.getNodePath();
            if (Objects.equals(filePath, mEntityAdapter.getSelectedFilePath())) {
                return;
            }
            mEntityAdapter.notifyItemSelected(filePath, mViewBinding.rcvList, true);
        });
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mEntityAdapter.setOnItemClickListener((view, position) -> {
            List<FileInfo> currentList = mEntityAdapter.getCurrentList();
            PlayerManager.getInstance().getPlayerControl().start(MusicConst.AUDIO_ZONE_CODE, currentList, currentList.get(position));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AAOP_HSkin.with(mViewBinding.rcvList)
                  .applySkin(true);
    }
}
