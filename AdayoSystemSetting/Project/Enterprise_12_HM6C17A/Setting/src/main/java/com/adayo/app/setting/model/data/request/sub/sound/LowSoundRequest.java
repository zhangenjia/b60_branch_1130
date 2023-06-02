package com.adayo.app.setting.model.data.request.sub.sound;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.utils.FastJsonUtil;
import com.adayo.app.setting.utils.timer.DevTimer;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.setting.system.SettingsSvcIfManager;
import com.alibaba.fastjson.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LowSoundRequest extends BaseRequest {
    private final static String TAG = LowSoundRequest.class.getSimpleName();
    private static final int AUDIO_STREAM_MEDIA = 3;
    private static final int AUDIO_STREAM_PHONE = 6;
    private static final int AUDIO_STREAM_BT_MUSIC = 15;
    private static final int AUDIO_STREAM_BT_PHONE_RING = 2;
    private final MutableLiveData<Boolean> mLoudnessSwitch = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, int[]>> mEqLiveData = new MutableLiveData<>();private final MutableLiveData<int[]> mSoundFiledLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> mDtsModeLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mDtsModeLastLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mBeepEnableLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mPhoneRingLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mSpeedLevelLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mLoudnessMediaLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mLoudnessPhoneLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mLoudnessBtMusicLiveData = new MutableLiveData<>();private final MutableLiveData<Integer> mLoudnessBtPhoneRingLiveData = new MutableLiveData<>();
    private final IShareDataListener mIShareDataInterface = (type, content) -> {
if (type == ParamConstant.SHARE_INFO_ID_EQ_AND_EFFECT) {
            parseEq(content);
            parseSoundFiled(content);
        } else if (type == ParamConstant.SHARE_INFO_ID_LOUDNESS) {
            parseBeepSwitch(content);
            parseSpeedLevel(content);
            parseLoudness(content);
        }
    };




    public void parseEq(String json) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        int eqMode = (int) map.get(ParamConstant.SHARE_INFO_KEY_EQ_MODE);JSONArray jsonArray = (JSONArray) map.get(ParamConstant.SHARE_INFO_KEY_EQ_BAND);int[] eqBand = new int[jsonArray.size()];for (int i = 0; i < jsonArray.size(); i++) {
            eqBand[i] = (int) jsonArray.get(i);
        }
        mEqLiveData.setValue(new HashMap<Integer, int[]>(3) {{put(eqMode, eqBand);
        }});
        LogUtil.i(TAG, "eqMode = " + eqMode + "eqBand = " + eqBand);
    }


    public void parseSoundFiled(String json) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        int x = (int) map.get(ParamConstant.SHARE_INFO_KEY_SOUND_FIELD_X);
        int y = (int) map.get(ParamConstant.SHARE_INFO_KEY_SOUND_FIELD_Y);
        int[] ints = new int[2];
        ints[0] = x;
        ints[1] = y;
        LogUtil.i(TAG, "x = " + ints[0] + "y = " + ints[1]);
        mSoundFiledLiveData.setValue(ints);
    }


    public void parseDtsMode(String json) {
        LogUtil.debugD(TAG, "");
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        int i1 = (int) map.get(ParamConstant.SHARE_INFO_KEY_DTS_MODE);
        mDtsModeLiveData.setValue(i1);
        int i2 = (int) map.get(ParamConstant.SHARE_INFO_KEY_DTS_MODE_LAST);
        mDtsModeLastLiveData.setValue(i2);
    }


    public void parseBeepSwitch(String json) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        boolean b = (boolean) map.get(ParamConstant.SHARE_INFO_KEY_BEEP);
        LogUtil.i(TAG, "b =" + b);
        mBeepEnableLiveData.setValue(b);
    }


    public void parseSpeedLevel(String json) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        int i = (int) map.get(ParamConstant.SHARE_INFO_KEY_LOUDNESS_SPEED_COMPENSATE);mSpeedLevelLiveData.setValue(--i);LogUtil.i(TAG, "i =" + i);
    }


    public void parsePhoneRing() {
        int anInt = ParamConstant.SETTING_GLOBAL_VALUE_PHONE_RING_RUNNING_WATER;try {
            anInt = Settings.Global.getInt(getAppContext().getContentResolver(),ParamConstant.SETTING_GLOBAL_KEY_PHONE_RING);} catch (Settings.SettingNotFoundException e) {
            LogUtil.w(TAG, e.getMessage());
        }
        LogUtil.i(TAG, "anInt =" + anInt);
        mPhoneRingLiveData.setValue(anInt);
    }


    public void parseLoudness(String json) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        JSONArray jsonArray = (JSONArray) map.get(ParamConstant.SHARE_INFO_KEY_LOUDNESS);
        Integer mediaProgress = (Integer) jsonArray.get(AUDIO_STREAM_MEDIA);
        Integer phoneProgress = (Integer) jsonArray.get(AUDIO_STREAM_PHONE);
        Integer btMusicProgress = (Integer) jsonArray.get(AUDIO_STREAM_BT_MUSIC);
        Integer btPhoneRingProgress = (Integer) jsonArray.get(AUDIO_STREAM_BT_PHONE_RING);
        if (!Objects.equals(mediaProgress, getLoudnessMediaLiveData().getValue())) {
            mLoudnessMediaLiveData.setValue(mediaProgress);}
        if (!Objects.equals(phoneProgress, getLoudnessPhoneLiveData().getValue())) {
            mLoudnessPhoneLiveData.setValue(phoneProgress);}

        if (!Objects.equals(btMusicProgress, getLoudnessBtMusicLiveData().getValue())) {
            mLoudnessBtMusicLiveData.setValue(btMusicProgress);}
        if (!Objects.equals(btPhoneRingProgress, getLoudnessBtPhoneRingLiveData().getValue())) {
            mLoudnessBtPhoneRingLiveData.setValue(btPhoneRingProgress);}
        LogUtil.debugD(TAG, "mediaProgress =" + mediaProgress + "phoneProgress =" + phoneProgress + "btMusicProgress =" + btMusicProgress + "btPhoneRingProgress =" + btPhoneRingProgress + "|||" + System.currentTimeMillis());

    }

    public void requestEqMode(int eqMode) {
        LogUtil.debugD(TAG, "");
        SettingsSvcIfManager.getSettingsManager().setEQMode(eqMode);
    }



    public void requestEqBand(int bandPos, int bandProgress) {
        LogUtil.i(TAG, "bandPos =" + bandPos + "bandProgress = " + bandProgress);
        SettingsSvcIfManager.getSettingsManager().setSoundFreqEffect(bandPos, bandProgress);}

    public void requestSoundFiled(int x, int y) {
        LogUtil.i(TAG, "X = " + x + "Y = " + y);
        SettingsSvcIfManager.getSettingsManager().setSoundField(x, y, 10);
    }

    public void requestDtsMode(int dtsMode) {
        SettingsSvcIfManager.getSettingsManager().setBestAudiophoneme(dtsMode);
    }


    public void requestBeepSwitch(boolean enable) {
        LogUtil.i(TAG, "enable = " + enable);
SettingsSvcIfManager.getSettingsManager().setSysBeepSwitch(enable ? 1 : 0);
    }


    public void requestSpeedLevel(int level) {
        LogUtil.i(TAG, "level = " + level);
        SettingsSvcIfManager.getSettingsManager().setSpeedVolumeMode(++level);
    }


    public void requestPhoneRing(int ringMode) {
        LogUtil.i(TAG, "ringMode = " + ringMode);
        boolean b = Settings.Global.putInt(getAppContext().getContentResolver(),
                ParamConstant.SETTING_GLOBAL_KEY_PHONE_RING,
                ringMode);
        mPhoneRingLiveData.setValue(b ? ringMode : getPhoneRingLiveData().getValue());
    }


    public void requestLoudness(int streamType, int volume) {
        LogUtil.i(TAG, "streamType = " + streamType + "volume = " + volume + "|||" + System.currentTimeMillis());
        SettingsSvcIfManager.getSettingsManager().setAudioStreamVolume(streamType, volume);
    }

    public void init() {
        LogUtil.debugD(TAG, "");
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_EQ_AND_EFFECT, mIShareDataInterface);
        if (b) {
            String eqAndEffectJson = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_EQ_AND_EFFECT);
            parseEq(eqAndEffectJson);
            parseSoundFiled(eqAndEffectJson);
        } else {
            initRegisterEQ();
        }
        boolean c = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_LOUDNESS, mIShareDataInterface);
        if (c) {
            String loudnessJson = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_LOUDNESS);parseBeepSwitch(loudnessJson);
            parseSpeedLevel(loudnessJson);
            parseLoudness(loudnessJson);} else {
            initRegisterLOUDNESS();
        }
        parsePhoneRing();
    }

    private void initRegisterEQ() {
        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());
        devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_EQ_AND_EFFECT, mIShareDataInterface);
                if (b) {
                    devTimer.stop();
                    String eqAndEffectJson = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_EQ_AND_EFFECT);
                    parseEq(eqAndEffectJson);
                    parseSoundFiled(eqAndEffectJson);
                }
                if (end) {
                    LogUtil.w(TAG, "register SHARE_INFO_ID_EQ_AND_EFFECT 10 time fail");
                }
            }
        });
        devTimer.start();
    }

    private void initRegisterLOUDNESS() {
        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());
        devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_LOUDNESS, mIShareDataInterface);
                if (b) {
                    devTimer.stop();
                    String loudnessJson = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_LOUDNESS);parseBeepSwitch(loudnessJson);
                    parseSpeedLevel(loudnessJson);
                    parseLoudness(loudnessJson);}
                if (end) {
                    LogUtil.w(TAG, "register SHARE_INFO_ID_LOUDNESS 10 time fail");
                }
            }
        });
        devTimer.start();
    }

    public void unInit() {
        LogUtil.debugD(TAG, "");
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_LOUDNESS, mIShareDataInterface);
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_EQ_AND_EFFECT, mIShareDataInterface);
    }

    public LiveData<Map<Integer, int[]>> getEqLiveData() {
        if (mEqLiveData.getValue() == null) {
            int[] ints = new int[3];
            ints[0] = 0;
            ints[1] = 0;
            ints[2] = 0;
            mEqLiveData.setValue(new HashMap<Integer, int[]>(3) {{put(0, ints);
            }});
        }
        return mEqLiveData;
    }

    public LiveData<int[]> getSoundFiledLiveData() {
        if (mSoundFiledLiveData.getValue() == null) {
            int[] ints = new int[2];
            ints[0] = 0;
            ints[1] = 0;
            mSoundFiledLiveData.setValue(ints);
        }
        return mSoundFiledLiveData;
    }

    public LiveData<Integer> getDtsModeLiveData() {
        if (mDtsModeLiveData.getValue() == null) {
            mDtsModeLiveData.setValue(0);
        }
        return mDtsModeLiveData;
    }

    public LiveData<Integer> getDtsModeLastLiveData() {
        if (mDtsModeLastLiveData.getValue() == null) {
            mDtsModeLastLiveData.setValue(0);
        }
        return mDtsModeLastLiveData;
    }

    public LiveData<Boolean> getBeepEnableLiveData() {
        if (mBeepEnableLiveData.getValue() == null) {
            mBeepEnableLiveData.setValue(false);
        }
        return mBeepEnableLiveData;
    }

    public LiveData<Integer> getPhoneRingLiveData() {
        if (mPhoneRingLiveData.getValue() == null) {
            mPhoneRingLiveData.setValue(0);
        }
        return mPhoneRingLiveData;
    }

    public LiveData<Integer> getSpeedLevelLiveData() {
        if (mSpeedLevelLiveData.getValue() == null) {
            mSpeedLevelLiveData.setValue(0);
        }
        return mSpeedLevelLiveData;
    }

    public LiveData<Integer> getLoudnessMediaLiveData() {
        if (mLoudnessMediaLiveData.getValue() == null) {
            mLoudnessMediaLiveData.setValue(0);
        }
        return mLoudnessMediaLiveData;
    }

    public LiveData<Integer> getLoudnessPhoneLiveData() {
        if (mLoudnessPhoneLiveData.getValue() == null) {
            mLoudnessPhoneLiveData.setValue(0);
        }
        return mLoudnessPhoneLiveData;
    }

    public LiveData<Integer> getLoudnessBtMusicLiveData() {
        if (mLoudnessBtMusicLiveData.getValue() == null) {
            mLoudnessBtMusicLiveData.setValue(0);
        }
        return mLoudnessBtMusicLiveData;
    }

    public LiveData<Integer> getLoudnessBtPhoneRingLiveData() {
        if (mLoudnessBtPhoneRingLiveData.getValue() == null) {
            mLoudnessBtPhoneRingLiveData.setValue(0);
        }
        return mLoudnessBtPhoneRingLiveData;
    }

}
