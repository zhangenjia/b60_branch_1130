package com.adayo.app.launcher.music.btmusic;

public interface IBTMusicCallBack {

    void onMusicStop();

    void onMusicPlay();

    void onId3Info(String artist, String album, String title);

    void onPlayTimeStatus(long totalTime, long currentTime );

    void notifyA2dpDisconnect();

    void notifyA2dpConnect();

    void notifyBluetoothDisconnect();
}
