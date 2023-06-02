package com.adayo.app.launcher.model.adapter;

import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import java.util.List;

public class LauncherViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "LauncherViewPagerAdapter";
    private List<Fragment> list;

    public LauncherViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
        Log.d(TAG, "destroyItem: " + position);
    }
}