package com.adayo.app.launcher.music.btmusic;

public interface IBTMusicFunction {

    void play();

    void pause();

    void playPrev();

    void playNext();

    void playOrPause();

    void init();

    void notifyPlayStatus(long totalTime, long currentTime ,int playStatus);

    void notifyId3Info(String artist, String album, String title);

    void notifyA2dpDisconnect();

    void notifyA2dpConnect();

    void notifyBluetoothDisconnect();

    void registerCallBack(IBTMusicCallBack callBack);
}
