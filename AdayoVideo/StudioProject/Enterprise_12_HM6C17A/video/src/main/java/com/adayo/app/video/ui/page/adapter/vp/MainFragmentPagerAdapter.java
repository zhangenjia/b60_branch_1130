package com.adayo.app.video.ui.page.adapter.vp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.adayo.app.video.data.constant.VideoConst;
import com.adayo.app.video.ui.page.fragment.VideoFragment;

public class MainFragmentPagerAdapter extends FragmentStateAdapter {
    public MainFragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment result;
        String fragmentTag = VideoConst.FRAG_TAG_MAIN_LIST.get(position);
        switch (fragmentTag) {
            case VideoConst.FRAG_TAG_LO_VIDEO:
                result = VideoFragment.newInstance();
                break;
            default:
                throw new IllegalStateException("unexpected value: " + fragmentTag);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return VideoConst.FRAG_TAG_MAIN_LIST.size();
    }
}
