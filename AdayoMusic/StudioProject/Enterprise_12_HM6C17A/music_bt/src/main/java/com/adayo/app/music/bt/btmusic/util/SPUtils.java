package com.adayo.app.music.bt.btmusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.lt.library.util.context.ContextUtil;


/**
 * key-value（键值对）存储类
 */
public class SPUtils {

    private static final String TAG = "BTMusicSPUtils";
    private static final String NEED_PLAY = "bt_music_need_play";


    public static void setNeedPlay(Context context, boolean isPlay) {
        int play = isPlay ? 1 : 0;
        Settings.Global.putInt(context.getContentResolver(), NEED_PLAY, play);
    }

    public static boolean isNeedPlay(Context context) {
        int play = Settings.Global.getInt(context.getContentResolver(), NEED_PLAY, 1);
        boolean isNeedPlay = play == 1;
        Log.d(TAG, "isNeedPlay: " + isNeedPlay);
        return isNeedPlay;
    }

}