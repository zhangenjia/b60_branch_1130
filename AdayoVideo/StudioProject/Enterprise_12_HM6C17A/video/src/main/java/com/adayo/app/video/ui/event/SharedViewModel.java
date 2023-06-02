package com.adayo.app.video.ui.event;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import com.adayo.app.video.data.constant.VideoConst;
import com.kunminx.architecture.ui.callback.ProtectedUnPeekLiveData;
import com.kunminx.architecture.ui.callback.UnPeekLiveData;
import com.lt.library.util.LogUtil;

import java.util.Map;

public class SharedViewModel extends ViewModel {
    private final UnPeekLiveData<Boolean> mAutoPlayLiveData = new UnPeekLiveData.Builder<Boolean>().create();

    @SuppressWarnings("unchecked")
    public void requireHandleSourceInfo(Bundle arguments) {
        Map<String, Object> map = (Map<String, Object>) arguments.get(VideoConst.BDL_KEY_SOURCE_INFO);
        LogUtil.d("sourceInfo: " + map);
        if (map == null) {
            return;
        }
        Object needAutoPlay = map.get(VideoConst.SOURCE_MANAGER_KEY_NEED_AUTO_PLAY);
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

    public ProtectedUnPeekLiveData<Boolean> getAutoPlayLiveData() {
        return mAutoPlayLiveData;
    }
}
