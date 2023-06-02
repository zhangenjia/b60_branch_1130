package com.adayo.app.music.ui.page.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.adayo.app.music.R;
import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.databinding.ActivityMainBinding;
import com.adayo.app.music.ui.event.SharedViewModel;
import com.adayo.app.music.ui.page.adapter.vp.MainFragmentPagerAdapter;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.lt.library.util.LogUtil;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class MainActivity extends AbsActivity<ActivityMainBinding> {
    private SharedViewModel mEventViewModel;
    private TextView mTvSelTab;
    private ViewPager2.OnPageChangeCallback mPageChangeCallback;

    @Override
    protected ActivityMainBinding bindView() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void bindData(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.bindData(arguments, savedInstanceState);
        mEventViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if (arguments != null) {
            mEventViewModel.requireHandleSourceInfo(arguments);
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mViewBinding.vpMain.setAdapter(new MainFragmentPagerAdapter(this));
        mViewBinding.vpMain.setUserInputEnabled(false);
        new TabLayoutMediator(mViewBinding.tlMain, mViewBinding.vpMain, true, false, (tab, position) -> {
            Map<String, Integer> map = new ArrayMap<>();
            map.put(MusicConst.FRAG_TAG_LO_MUSIC, R.string.main_tab_title_lo_music);
            map.put(MusicConst.FRAG_TAG_BT_MUSIC, R.string.main_tab_title_bt_music);
            tab.setText(map.get(MusicConst.FRAG_TAG_MAIN_LIST.get(position)));
        }).attach();
    }

    @Override
    protected void initData() {
        super.initData();
        mEventViewModel.getSourceTypeLiveData().observeSticky(this, s -> {
            int index = MusicConst.FRAG_TAG_MAIN_LIST.indexOf(s);
            LogUtil.d("will be into index: " + index);
            mViewBinding.vpMain.setCurrentItem(index, false);
        });
        // TODO: 2022/2/27 待实现源切回自动播放
        /*mEventViewModel.getAutoPlayLiveData().observeSticky(this, aBoolean -> {
            PlayerManager.getInstance().getPlayerControl().resume(MusicConst.AUDIO_ZONE_CODE);
        });*/
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                LogUtil.d("main page item position: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                LogUtil.d("main page item state: " + state);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                LogUtil.d("main page item position: " + position);
                TabLayout tabLayout = mViewBinding.tlMain;
                int bound = mViewBinding.tlMain.getTabCount();
                IntStream.range(0, bound).forEach(i -> {
                    TabLayout.Tab tabAt = tabLayout.getTabAt(i);
                    if (position != i) {
                        tabAt.setCustomView(null);
                    }
                    TooltipCompat.setTooltipText(tabAt.view, null);
                });
                Map<Integer, Integer> map = new ArrayMap<>();
                map.put(0, R.drawable.img_select_left);
                map.put(1, R.drawable.img_select_right);
                Optional.ofNullable(map.get(position)).ifPresent(integer -> {
                    TabLayout.Tab tab = mViewBinding.tlMain.getTabAt(position);
                    TextView textView = getCustomTab(tab.getText());
                    AAOP_HSkin.with(textView)
                              .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, integer)
                              .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.main_tab_text_sel)
                              .applySkin(false);
                    tab.setCustomView(textView);
                });
            }
        };
        mViewBinding.vpMain.registerOnPageChangeCallback(mPageChangeCallback);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle arguments = intent.getExtras();
        if (arguments != null) {
            mEventViewModel.requireHandleSourceInfo(arguments);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void freeEvent() {
        super.freeEvent();
        mViewBinding.vpMain.unregisterOnPageChangeCallback(mPageChangeCallback);
    }

    @Override
    protected void freeData() {
        super.freeData();
        mEventViewModel.getSourceTypeLiveData().clear();
    }

    private TextView getCustomTab(CharSequence text) {
        if (mTvSelTab == null) {
            mTvSelTab = new TextView(this);
            mTvSelTab.setGravity(Gravity.CENTER);
            mTvSelTab.setMaxLines(1);
            mTvSelTab.setTextSize(getResources().getDimension(R.dimen.main_tab_item_text_size));
            mTvSelTab.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        mTvSelTab.setText(text);
        return mTvSelTab;
    }//自定义选中Tab的效果(原生TabLayout不支持选中Tab字体大小的变化)
}