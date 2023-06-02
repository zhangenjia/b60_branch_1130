package com.adayo.app.music.ui.page.adapter.vp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.ui.page.fragment.browse.component.AlbumFragment;
import com.adayo.app.music.ui.page.fragment.browse.component.AllFragment;
import com.adayo.app.music.ui.page.fragment.browse.component.ArtistFragment;
import com.adayo.app.music.ui.page.fragment.browse.component.DirFragment;

public class BrowseFragmentPagerAdapter extends FragmentStateAdapter {
    public BrowseFragmentPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment result;
        String fragmentTag = MusicConst.FRAG_TAG_BROWSE_LIST.get(position);
        switch (fragmentTag) {
            case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_ALL:
                result = AllFragment.newInstance();
                break;
            case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_DIR:
                result = DirFragment.newInstance();
                break;
            case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_ALBUM:
                result = AlbumFragment.newInstance();
                break;
            case MusicConst.FRAG_TAG_LO_MUSIC_BROWSE_ARTIST:
                result = ArtistFragment.newInstance();
                break;
            default:
                throw new IllegalStateException("unexpected value: " + fragmentTag);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return MusicConst.FRAG_TAG_BROWSE_LIST.size();
    }
}
