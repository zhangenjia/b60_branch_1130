package com.adayo.app.systemui.utils;

import android.media.AudioManager;

import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngAudioSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;

import java.util.HashMap;

/**
 * @author XuYue
 * @description:
 * @date :2021/10/22 10:13
 */
public class AudioFocusUtils {
    private static HashMap<String, IAdayoAudioFocusChange> listenerMap = new HashMap();
    private static SrcMngAudioSwitchManager srcMngAudioSwitchManager = SrcMngAudioSwitchManager.getInstance();

    public static void abandonAdayoAudioFocus(String audioFocus) {
        LogUtil.debugD(SystemUIContent.TAG, audioFocus);
        IAdayoAudioFocusChange iAdayoAudioFocusChange = listenerMap.get(audioFocus);
        if (null != iAdayoAudioFocusChange) {
            synchronized (srcMngAudioSwitchManager) {
                srcMngAudioSwitchManager.abandonAdayoAudioFocus(iAdayoAudioFocusChange);
            }
            listenerMap.remove(audioFocus);
        }
    }

    public static void requestAdayoAudioFocus(String audioFocus) {
        LogUtil.debugD(SystemUIContent.TAG, audioFocus);
        IAdayoAudioFocusChange iAdayoAudioFocusChange = listenerMap.get(audioFocus);
        if (null == iAdayoAudioFocusChange) {
            iAdayoAudioFocusChange = new IAdayoAudioFocusChange() {
                @Override
                public void onAdayoAudioOnGain() {
                }

                @Override
                public void onAdayoAudioOnLoss() {
                }

                @Override
                public void onAdayoAudioLossTransient() {
                }

                @Override
                public void onAdayoAudioLossTransientCanDuck() {
                }
            };
            listenerMap.put(audioFocus, iAdayoAudioFocusChange);
        }
        synchronized (srcMngAudioSwitchManager) {
            srcMngAudioSwitchManager.requestAdayoAudioFocus(6,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT, audioFocus, iAdayoAudioFocusChange,
                    SystemUIApplication.getSystemUIContext());
        }
    }
}
