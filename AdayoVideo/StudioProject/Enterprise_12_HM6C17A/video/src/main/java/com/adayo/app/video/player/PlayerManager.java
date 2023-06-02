package com.adayo.app.video.player;

import com.adayo.app.video.data.constant.VideoConst;
import com.adayo.app.video.player.contract.IPlayerContract;
import com.adayo.app.video.player.contract.impl.PlayerContractImpl;
import com.adayo.proxy.media.bean.PlayerConfig;

import java.util.HashMap;

public class PlayerManager {
    private final PlayerContractImpl mPlayerContract = new PlayerContractImpl();

    private PlayerManager() {
    }

    public static void init() {
        PlayerConfig playerConfig = new PlayerConfig.Builder()
                .setFastRewindCurve(
                        new HashMap<Long, Long>() {{
                            put(0L, 10000L);
                        }})
                .setFastForwardCurve(
                        new HashMap<Long, Long>() {{
                            put(0L, 10000L);
                        }})
                .build();
        getInstance().mPlayerContract.init(VideoConst.AUDIO_ZONE_CODE, playerConfig, false);
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
