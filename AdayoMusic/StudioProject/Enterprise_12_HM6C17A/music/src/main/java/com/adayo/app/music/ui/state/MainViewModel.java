package com.adayo.app.music.ui.state;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.adayo.app.music.domain.request.InfoRequest;
import com.adayo.app.music.player.PlayerManager;
import com.adayo.proxy.media.constant.MediaConst;
import com.adayo.proxy.media.util.HexStateUtil;

public class MainViewModel extends ViewModel {
    public static final int STATE_NONE = 0x0;
    public static final int STATE_NO_PLAY_CONTENT = 0x1;
    public static final int STATE_PARSE_ERROR = 0x2;
    private final InfoRequest mInfoRequest = new InfoRequest();
    private final MediatorLiveData<Integer> mCtrlBtnEnabledLiveData = new MediatorLiveData<>();
    private int mCurrentState = STATE_NONE;

    {
        mCtrlBtnEnabledLiveData.addSource(PlayerManager.getInstance().getPlayerLiveData().getPlayStateLiveData(), integer -> {
            callOnCtrlBtnEnabledChanged(integer == MediaConst.PlayStateDef.STOPPED, STATE_NO_PLAY_CONTENT);
        });
        mCtrlBtnEnabledLiveData.addSource(PlayerManager.getInstance().getPlayerLiveData().getPlayErrorCauseLiveData(), integer -> {
            callOnCtrlBtnEnabledChanged(integer != MediaConst.PlayErrorCauseDef.NONE, STATE_PARSE_ERROR);
        });
    }

    public InfoRequest getInfoRequest() {
        return mInfoRequest;
    }

    public LiveData<Integer> getCtrlBtnEnabledLiveData() {
        return mCtrlBtnEnabledLiveData;
    }

    private void callOnCtrlBtnEnabledChanged(boolean b, int state) {
        if (b) {
            if (HexStateUtil.has(mCurrentState, state)) {
                return;
            }
            mCurrentState = HexStateUtil.add(mCurrentState, state);
        } else {
            if (!HexStateUtil.has(mCurrentState, state)) {
                return;
            }
            mCurrentState = HexStateUtil.del(mCurrentState, state);
        }
        mCtrlBtnEnabledLiveData.setValue(mCurrentState);
    }
}
