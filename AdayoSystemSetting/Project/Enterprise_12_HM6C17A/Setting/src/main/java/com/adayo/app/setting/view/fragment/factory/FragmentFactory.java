package com.adayo.app.setting.view.fragment.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.adayo.app.setting.R;

import com.adayo.app.setting.view.fragment.DisplaySetFragment;
import com.adayo.app.setting.view.fragment.Sound.SoundFragment;
import com.adayo.app.setting.view.fragment.Sys.SysFragment;
import com.adayo.app.setting.view.fragment.VoiceFragment;
import com.lt.library.util.LogUtil;
import com.lt.library.util.fragment.IFragmentFactory;


public class FragmentFactory  implements IFragmentFactory {
    private final static String TAG = FragmentFactory.class.getSimpleName();

    @Override
    public Fragment createProduct(@NonNull String type, @Nullable String param1, @Nullable String param2) {
        LogUtil.d(TAG,"type ="+type);
        Fragment fragment;
        switch (Integer.parseInt(type)){
            case R.string.tab_item_sound_title:
                fragment = SoundFragment.newInstance(param1, param2);
                break;
            case R.string.tab_item_display_title:
                fragment = DisplaySetFragment.newInstance(param1, param2);
                break;
            case R.string.tab_item_voice_title:
                fragment = VoiceFragment.newInstance(param1, param2);
                break;
            case R.string.tab_item_system_title:
                fragment = SysFragment.newInstance(param1, param2);
                break;
            default:
                throw new IllegalArgumentException("unexpected type = " + type);
        }

        return fragment;
    }
}
