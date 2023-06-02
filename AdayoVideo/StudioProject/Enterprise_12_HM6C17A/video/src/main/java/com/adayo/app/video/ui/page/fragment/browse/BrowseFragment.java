package com.adayo.app.video.ui.page.fragment.browse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.adayo.app.video.data.constant.VideoConst;
import com.adayo.app.video.databinding.FragmentVideoBrowseBinding;
import com.adayo.app.video.player.PlayerManager;
import com.adayo.app.video.ui.page.adapter.rcv.browse.AllAdapter;
import com.adayo.app.video.ui.page.fragment.AbsFragment;
import com.adayo.app.video.ui.state.MainViewModel;
import com.adayo.proxy.media.bean.FileInfo;

import java.util.List;
import java.util.Objects;

public class BrowseFragment extends AbsFragment<FragmentVideoBrowseBinding> {
    private MainViewModel mStateViewModel;
    private AllAdapter mEntityAdapter;

    @Override
    protected FragmentVideoBrowseBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentVideoBrowseBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initView() {
        super.initView();
        mEntityAdapter = new AllAdapter();
        mViewBinding.rcvList.setLayoutManager(new LinearLayoutManager(requireContext()));
        mViewBinding.rcvList.setAdapter(mEntityAdapter);
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
            PlayerManager.getInstance().getPlayerControl().start(VideoConst.AUDIO_ZONE_CODE, currentList, currentList.get(position));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        PlayerManager.getInstance().getPlayerControl().requestAudioFocus(VideoConst.AUDIO_ZONE_CODE);
        PlayerManager.getInstance().getPlayerControl().resume(VideoConst.AUDIO_ZONE_CODE, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        PlayerManager.getInstance().getPlayerControl().pause(VideoConst.AUDIO_ZONE_CODE, false);
    }
}
