package com.adayo.app.music.player;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.domain.request.InfoRequest;
import com.lt.library.util.LogUtil;

public class PlayerService extends LifecycleService {
    private final InfoRequest mInfoRequest = new InfoRequest();
    private Boolean mLastSource;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d();
        initData();
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtil.d();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        LogUtil.d("intent: " + intent + ", flags: " + flags + ", startId: " + startId);
        if (intent != null) {
            String lastMode = intent.getStringExtra("lastMode");
            LogUtil.d("lastMode: " + lastMode);
            mLastSource = "Start".equals(lastMode);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d();
    }

    private void initData() {
        mInfoRequest.getBrowseStateLiveData().observe(this, aBoolean -> {
            if (mLastSource == null || !mLastSource) {
                return;
            }
            if (!aBoolean) {
                return;
            }
            LogUtil.d("will be start lastSource");
            PlayerManager.getInstance().getPlayerControl().requestAudioFocus(MusicConst.AUDIO_ZONE_CODE);
            PlayerManager.getInstance().getPlayerControl().resume(MusicConst.AUDIO_ZONE_CODE, false);
            mLastSource = null;
        });
    }
}