package com.adayo.app.video.ui.page.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.adayo.app.video.R;
import com.adayo.app.video.data.constant.VideoConst;
import com.adayo.app.video.databinding.ActivityMainBinding;
import com.adayo.app.video.ui.event.SharedViewModel;
import com.adayo.app.video.ui.page.adapter.vp.MainFragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AbsActivity<ActivityMainBinding> {
    private SharedViewModel mEventViewModel;
    private TextView mTvSelTab;

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
        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView textView = getCustomTab(tab.getText());
                tab.setCustomView(textView);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
        mViewBinding.tlMain.addOnTabSelectedListener(onTabSelectedListener);//必须在TabLayoutMediator.attach()前, 否则首次加载View时无onTabSelected回调
        new TabLayoutMediator(mViewBinding.tlMain, mViewBinding.vpMain, true, false, (tab, position) -> {
            String tag = VideoConst.FRAG_TAG_MAIN_LIST.get(position);
            switch (tag) {
                case VideoConst.FRAG_TAG_LO_VIDEO:
                    tab.setText(R.string.main_tab_title_lo_video);
                    break;
                default:
                    throw new IllegalArgumentException("unexpected value: " + tag);
            }
        }).attach();
    }

    @Override
    protected void initData() {
        super.initData();
        // TODO: 2022/2/27 待实现源切回自动播放
        /*mEventViewModel.getAutoPlayLiveData().observeSticky(this, aBoolean -> {
            PlayerManager.getInstance().getPlayerControl().resume(MusicConst.AUDIO_ZONE_CODE);
        });*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle arguments = intent.getExtras();
        if (arguments != null) {
            mEventViewModel.requireHandleSourceInfo(arguments);
        }
    }

    private TextView getCustomTab(CharSequence text) {
        if (mTvSelTab == null) {
            mTvSelTab = new TextView(this);
            mTvSelTab.setTextColor(0xFFFFFFFF);
            mTvSelTab.setGravity(Gravity.CENTER);
            mTvSelTab.setMaxLines(1);
            mTvSelTab.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            mTvSelTab.setAutoSizeTextTypeUniformWithConfiguration(30, 40, 1, TypedValue.COMPLEX_UNIT_SP);
            mTvSelTab.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        mTvSelTab.setText(text);
        return mTvSelTab;
    }//自定义选中Tab的效果(原生TabLayout不支持选中Tab字体大小的变化)
}