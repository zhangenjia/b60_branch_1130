package com.adayo.app.music.player.contract;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.adayo.proxy.media.bean.FileInfo;
import com.adayo.proxy.media.bean.MetadataInfo;
import com.adayo.proxy.media.bean.NodeInfo;
import com.adayo.proxy.media.bean.PlayerConfig;
import com.adayo.proxy.media.constant.MediaConst;

import java.util.List;

public interface IPlayerContract {
    interface IPlayerController {
        void init(int audioZoneCode, PlayerConfig playerConfig, boolean isForceUpdate);

        void start(int audioZoneCode, List<FileInfo> playlist, FileInfo fileInfo);

        void resume(int audioZoneCode, boolean fromUser);

        void pause(int audioZoneCode, boolean fromUser);

        void stop(int audioZoneCode, boolean fromUser);

        void toggleResumeOrPause(int audioZoneCode);

        void prev(int audioZoneCode);

        void next(int audioZoneCode);

        void fastRewindStart(int audioZoneCode);

        void fastRewindEnd(int audioZoneCode);

        void fastForwardStart(int audioZoneCode);

        void fastForwardEnd(int audioZoneCode);

        void setProgress(int audioZoneCode, long progress);

        void setRepeatMode(int audioZoneCode, @MediaConst.RepeatModeDef int repeatMode);

        void toggleRepeatMode(int audioZoneCode);

        void requestAudioFocus(int audioZoneCode);
    }

    interface IPlayerInfoHolder {
        LiveData<Integer> getRepeatModeLiveData();

        LiveData<NodeInfo> getNodeInfoLiveData();

        LiveData<MetadataInfo> getMetadataLiveData();

        LiveData<Integer> getPlayStateLiveData();

        LiveData<Integer> getPlayErrorCauseLiveData();

        LiveData<Long> getDurationLiveData();

        LiveData<Long> getProgressLiveData();

        LiveData<Bitmap> getCoverLiveData();
    }
}
