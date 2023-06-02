package com.adayo.app.setting.drive;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.utils.FastJsonUtil;
import com.adayo.app.setting.utils.timer.DevTimer;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.setting.system.SettingsSvcIfManager;

import java.util.Map;

public class DriveRequest extends BaseRequest {
    private final static String TAG = DriveRequest.class.getSimpleName();
    private final MutableLiveData<Boolean> mDrivingVideoLiveData = new MutableLiveData<>();
    private final IShareDataListener mIShareDataInterface = (type, content) -> {
        LogUtil.debugD(TAG, "type = " + type + ", content = " + content);
        if (type == ParamConstant.SHARE_INFO_ID_GENERAL) {parseDrivingVideo(content);
        }
    };

    public void init() {
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);

        if (b) {
            String shareData = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_GENERAL);
            parseDrivingVideo(shareData);
        } else {
            DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
            devTimer.setHandler(new Handler());devTimer.setCallback(new DevTimer.Callback() {
                @Override
                public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                    LogUtil.d(TAG, "number =" + number);
                    boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);

                    if (b) {
                        devTimer.stop();
                        String shareData = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_GENERAL);
                        parseDrivingVideo(shareData);
                    }
                    if (end) {
                        LogUtil.w(TAG, "register SHARE_INFO_ID_LOUDNESS 10 time fail");
                    }
                }
            });
            devTimer.start();
        }
    }

    public void unInit() {
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_GENERAL, mIShareDataInterface);

    }


    private void parseDrivingVideo(String json) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        boolean b = (boolean) map.get(ParamConstant.SHARE_INFO_KEY_DRIVING_VIDEO);mDrivingVideoLiveData.setValue(!b);
        LogUtil.i(TAG, "mDrivingVideoLiveData = " + !b);
    }


    public void requestDrivingVideoSwitch(boolean enable) {
        LogUtil.i(TAG, "enable = " + enable);
        int[] ints = SettingsSvcIfManager.getSettingsManager().getDrivingWatchVideoSwitch();int speedThreshold = ints[1];SettingsSvcIfManager.getSettingsManager().setDrivingWatchVideoSwitch(enable ? 1 : 0, speedThreshold);}

    public LiveData<Boolean> getDrivingVideoLiveData() {
        if (mDrivingVideoLiveData.getValue() == null) {
            mDrivingVideoLiveData.setValue(false);
        }
        return mDrivingVideoLiveData;
    }
}
