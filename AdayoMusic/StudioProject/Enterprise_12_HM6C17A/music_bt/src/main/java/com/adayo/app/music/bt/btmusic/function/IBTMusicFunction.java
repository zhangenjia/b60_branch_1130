package com.adayo.app.music.bt.btmusic.function;

import com.adayo.app.music.bt.btmusic.callback.IBTMusicCallBack;

public interface IBTMusicFunction {

    void play();

    void pause();

    void playPrev();

    void playNext();

    void init();

    void notifyPlayStatus(long totalTime, long currentTime ,int playStatus);

    void notifyId3Info(String artist, String album, String title);

    void notifyA2dpDisconnect();

    void notifyA2dpConnect();

    void notifyBluetoothDisconnect();

    int requestAudioFocus();

    boolean abandonA2dpFocus();

    void registerCallBack(IBTMusicCallBack callBack);
}
