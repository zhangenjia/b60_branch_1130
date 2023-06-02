package com.adayo.app.music.player;

import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.app.music.player.contract.IPlayerContract;
import com.adayo.app.music.player.contract.impl.PlayerContractImpl;
import com.adayo.proxy.media.bean.PlayerConfig;

import java.util.HashMap;

public class PlayerManager {
    private final PlayerContractImpl mPlayerContract = new PlayerContractImpl();

    private PlayerManager() {
    }

    public static void init() {
        PlayerConfig playerConfig = new PlayerConfig.Builder()
                .setAutoPauseWhenPlayerInvisible(false)
                .setFastRewindCurve(
                        new HashMap<Long, Long>() {{
                            put(0L, 5000L);
                        }})
                .setFastForwardCurve(
                        new HashMap<Long, Long>() {{
                            put(0L, 5000L);
                        }})
                .build();
        getInstance().mPlayerContract.init(MusicConst.AUDIO_ZONE_CODE, playerConfig, false);
    }

    public static PlayerManager getInstance() {
        return PlayerManagerHolder.INSTANCE;
    }

    public IPlayerContract.IPlayerController getPlayerControl() {
        return mPlayerContract;
    }

    public IPlayerContract.IPlayerInfoHolder getPlayerLiveData() {
        return mPlayerContract;
    }

    private static class PlayerManagerHolder {
        private static final PlayerManager INSTANCE = new PlayerManager();
    }
}
