package com.adayo.app.music.ui.page.adapter.vp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.adayo.app.music.bt.btmusic.BTMusicFragment;
import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.ui.page.fragment.MusicFragment;

public class MainFragmentPagerAdapter extends FragmentStateAdapter {
    public MainFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment result;
        String fragmentTag = MusicConst.FRAG_TAG_MAIN_LIST.get(position);
        switch (fragmentTag) {
            case MusicConst.FRAG_TAG_LO_MUSIC:
                result = MusicFragment.newInstance();
                break;
            case MusicConst.FRAG_TAG_BT_MUSIC:
                result = BTMusicFragment.newInstance();
                break;
            default:
                throw new IllegalStateException("unexpected value: " + fragmentTag);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return MusicConst.FRAG_TAG_MAIN_LIST.size();
    }
}
