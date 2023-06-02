package com.adayo.app.music.ui.page.fragment.browse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.adayo.app.music.R;
import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.databinding.FragmentMusicBrowseBinding;
import com.adayo.app.music.player.PlayerManager;
import com.adayo.app.music.ui.page.adapter.vp.BrowseFragmentPagerAdapter;
import com.adayo.app.music.ui.page.fragment.AbsFragment;
import com.adayo.app.music.ui.state.MainViewModel;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.media.bean.NodeInfo;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BrowseFragment extends AbsFragment<FragmentMusicBrowseBinding> {
    private static final String FRAG_TAG_SEL_DEF = MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_ALL;
    private MainViewModel mStateViewModel;

    @Override
    protected FragmentMusicBrowseBinding bindView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMusicBrowseBinding.inflate(inflater, container, false);
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Override
    protected void initView() {
        super.initView();
        delTooltip(mViewBinding.tlBrowse);
        mViewBinding.vpBrowse.setAdapter(new BrowseFragmentPagerAdapter(this));
        mViewBinding.vpBrowse.setUserInputEnabled(false);
        ((RecyclerView) mViewBinding.vpBrowse.getChildAt(0)).setItemViewCacheSize(MusicConst.FRAG_TAG_BROWSE_LIST.size());
        new TabLayoutMediator(mViewBinding.tlBrowse, mViewBinding.vpBrowse, true, false, (tab, position) -> {
            switch (MusicConst.FRAG_TAG_BROWSE_LIST.get(position)) {
                case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_ALL:
                    tab.setText(R.string.lo_music_browse_component_tab_title_all);
                    break;
                case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_DIR:
                    tab.setText(R.string.lo_music_browse_component_tab_title_folder);
                    break;
                case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_ALBUM:
                    tab.setText(R.string.lo_music_browse_component_tab_title_album);
                    break;
                case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_ARTIST:
                    tab.setText(R.string.lo_music_browse_component_tab_title_artist);
                    break;
                default:
                    break;
            }
        }).attach();
        mViewBinding.vpBrowse.setCurrentItem(MusicConst.FRAG_TAG_MAIN_LIST.indexOf(FRAG_TAG_SEL_DEF), false);// TODO: 2022/3/3 待测试删除效果
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mViewBinding.vpBrowse.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (MusicConst.FRAG_TAG_BROWSE_LIST.get(position)) {
                    case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_DIR:
                        NodeInfo nodeInfo = PlayerManager.getInstance().getPlayerLiveData().getNodeInfoLiveData().getValue();
                        if (nodeInfo == null) {
                            mStateViewModel.getInfoRequest().requireDirList(null);
                        } else {
                            mStateViewModel.getInfoRequest().requireDirList(nodeInfo.getNodeParentPath());
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AAOP_HSkin.with(mViewBinding.tlBrowse)
                  .addViewAttrs(AAOP_HSkin.ATTR_TAB_INDICATOR_COLOR, R.color.browse_tab_indicator)
                  .applySkin(false);
        PlayerManager.getInstance().getPlayerControl().resume(MusicConst.AUDIO_ZONE_CODE, false);
    }

    private void delTooltip(TabLayout tabLayout) {
        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int bound = tabLayout.getTabCount();
                for (int i = 0; i < bound; i++) {
                    TooltipCompat.setTooltipText(tabLayout.getTabAt(i).view, null);
                }
            }
        });
    }
}
