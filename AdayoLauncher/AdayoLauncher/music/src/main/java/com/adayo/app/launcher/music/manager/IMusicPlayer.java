package com.adayo.app.launcher.music.manager;

import android.graphics.Bitmap;
import android.util.Pair;

import androidx.lifecycle.LiveData;

public interface IMusicPlayer {
    int PLAY_STATE_STOPPED = 0;
    int PLAY_STATE_PLAYED = 1;
    int PLAY_STATE_PAUSED = 2;

    void init();

    void toggleResumeOrPause();

    void prev();

    void next();

    LiveData<Pair<String, String>> getTitleLiveData();

    LiveData<Integer> getPlayStateLiveData();

    LiveData<Long> getDurationLiveData();

    LiveData<Long> getProgressLiveData();

    LiveData<Bitmap> getCoverLiveData();
}
