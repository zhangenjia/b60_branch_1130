package com.adayo.app.music.ui.event;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import com.adayo.app.music.data.constant.MusicConst;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.kunminx.architecture.ui.callback.ProtectedUnPeekLiveData;
import com.kunminx.architecture.ui.callback.UnPeekLiveData;
import com.lt.library.util.LogUtil;

import java.util.Map;

public class SharedViewModel extends ViewModel {
    private final UnPeekLiveData<String> mSourceTypeLiveData = new UnPeekLiveData.Builder<String>().create();
    private final UnPeekLiveData<Boolean> mAutoPlayLiveData = new UnPeekLiveData.Builder<Boolean>().create();

    @SuppressWarnings("unchecked")
    public void requireHandleSourceInfo(Bundle arguments) {
        Map<String, Object> map = (Map<String, Object>) arguments.get(MusicConst.BDL_KEY_SOURCE_INFO);
        LogUtil.d("sourceInfo: " + map);
        if (map == null) {
            return;
        }
        Object sourceType = map.get(MusicConst.SOURCE_MANAGER_KEY_SOURCE_TYPE);
        if (sourceType != null) {
            switch ((String) sourceType) {
                case AdayoSource.ADAYO_SOURCE_USB:
                    mSourceTypeLiveData.setValue(MusicConst.FRAG_TAG_LO_MUSIC);
                    break;
                case AdayoSource.ADAYO_SOURCE_BT_AUDIO:
                    mSourceTypeLiveData.setValue(MusicConst.FRAG_TAG_BT_MUSIC);
                    break;
                default:
                    break;
            }
        }
        Object needAutoPlay = map.get(MusicConst.SOURCE_MANAGER_KEY_NEED_AUTO_PLAY);
        if (needAutoPlay != null) {
            switch ((String) needAutoPlay) {
                case "YES":
                    mAutoPlayLiveData.setValue(true);
                    break;
                case "NO":
                    mAutoPlayLiveData.setValue(false);
                    break;
                default:
                    break;
            }
        }
    }

    public ProtectedUnPeekLiveData<String> getSourceTypeLiveData() {
        return mSourceTypeLiveData;
    }

    public ProtectedUnPeekLiveData<Boolean> getAutoPlayLiveData() {
        return mAutoPlayLiveData;
    }
}
