package com.adayo.app.setting.model.data.request.sub.Ver;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;

import com.adayo.app.base.LogUtil;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.utils.FastJsonUtil;
import com.adayo.app.setting.utils.timer.DevTimer;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.upgrade.IUpgradeCallback;
import com.adayo.proxy.upgrade.UpgradeManager;

import java.util.Map;
import java.util.Objects;


public class LowVerRequest extends BaseRequest {
    private final static String TAG = LowVerRequest.class.getSimpleName();
    private final MutableLiveData<String> mVerLiveData = new MutableLiveData<>();
    private final Handler mHandler = new Handler();
    private final MutableLiveData<Boolean> mHasUsbFileLiveData = new MutableLiveData<>();

    private final IShareDataListener mIShareDataInterface = (type, content) -> {
        LogUtil.debugD(TAG, "type = " + type + ", content = " + content);
        if (type == ParamConstant.SHARE_INFO_ID_USB_UPGRADE) {
            parseHasUsbFile(content);
        }
    };


    public void requestCurrentVersion() {
        UpgradeManager.getInstance().getServiceConnection();        String currentVersion = SystemProperties.get("persist.adayo.mpu.version");
        LogUtil.i(TAG, "currentVersion = " + currentVersion);
        mVerLiveData.setValue(currentVersion);
    }

    public void init() {
        UpgradeManager.getInstance().registerUpgrade(mUpgradeCallback, getAppContext().getPackageName());
        requestCurrentVersion();
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_USB_UPGRADE, mIShareDataInterface);
        if (b) {
            String isUsbUpgrade = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_USB_UPGRADE);
            parseHasUsbFile(isUsbUpgrade);
        }else {
            initRegisterShareData();
        }
    }

    private void initRegisterShareData() {
        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());
        devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_USB_UPGRADE, mIShareDataInterface);

                if (b) {
                    devTimer.stop();
                    String isUsbUpgrade = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_USB_UPGRADE);
                    parseHasUsbFile(isUsbUpgrade);
                }
                if (end) {
                    LogUtil.w(TAG, "register SHARE_INFO_ID_USB_UPGRADE 10 time fail");
                }
            }
        });
        devTimer.start();
    }

    public void unInit() {
        mHandler.removeCallbacksAndMessages(null);
        UpgradeManager.getInstance().unregisterUpgrade(mUpgradeCallback, getAppContext().getPackageName());
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_USB_UPGRADE, mIShareDataInterface);

    }

    private void parseHasUsbFile(String isUsbUpgrade) {
        Map<String, Object> map = FastJsonUtil.jsonToMap(isUsbUpgrade);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            mHasUsbFileLiveData.setValue(false);
            return;
        }
        LogUtil.d(TAG, "isUsbUpgrade =" + map);
        String i = (String) map.get(ParamConstant.SHARE_INFO_KEY_HAS_UPGRADE_FILE);
        LogUtil.i(TAG, "I =" + i);
        if (i == null) {
            mHasUsbFileLiveData.setValue(false);
        }
        if ("1".equals(i)) {
            mHasUsbFileLiveData.setValue(true);
        } else if ("0".equals(i)) {
            mHasUsbFileLiveData.setValue(false);
        } else {
            LogUtil.w(TAG, "SHARE_INFO data is wrong");
            mHasUsbFileLiveData.setValue(false);
        }


    }


    private final IUpgradeCallback.Stub mUpgradeCallback = new IUpgradeCallback.Stub() {

        @Override
        public void notifyLocalNewVersion(String latestVersion) throws RemoteException {
        }
        @Override
        public void notifyIsFotaNewVersion(boolean isExistNewVersion) throws RemoteException {
        }
        @Override
        public void notifyUSBConnectStatus(boolean isConnected) throws RemoteException {
        }

        @Override
        public void notifyFotaLoginTSPStatus(int tspStatus) throws RemoteException {

        }

        @Override
        public void notifyFotaNewVersion(String latestVersion) throws RemoteException {
        }
        @Override
        public void notifyFotaNewVersionSize(long latestVersionPackageSize) throws RemoteException {
        }
        @Override
        public void notifyFotaDownloadProgress(int downloadProgress) throws RemoteException {

        }

        @Override
        public void notifyFotaDownloadStatus(int downloadStatus) throws RemoteException {

        }


        @Override
        public void notifyNewVersionInfo(String latestVersionInfo) throws RemoteException {
            Map<String, Object> map = FastJsonUtil.jsonToMap(latestVersionInfo);
            if (map == null) {
                LogUtil.w(TAG, ".SHARE_INFO map is null");
                return;
            }
            String updateType = String.valueOf(map.get("UpdateType"));            String newVersion = String.valueOf(map.get("new_version"));            int size = Integer.parseInt(String.valueOf(map.get("size")));
            String description = String.valueOf(map.get("description"));
            if ("USB".equals(updateType)) {
                LogUtil.debugD(TAG, "updateType =" + updateType);


            }
        }
    };


    private static boolean isExistNewVersion(String currentVersion, String latestVersion) {
        if (Objects.isNull(currentVersion)) {
            currentVersion = "";
        }
        if (Objects.isNull(latestVersion)) {
            latestVersion = "";
        }
        return currentVersion.compareTo(latestVersion) < 0;
    }


    public void requestLoUpgrade() {
        LogUtil.i(TAG, "");
        UpgradeManager.getInstance().onUpgrade(0);
    }

    public MutableLiveData<Boolean> getHasUsbFileLiveData() {
        if (mHasUsbFileLiveData.getValue() == null) {
            mHasUsbFileLiveData.setValue(false);
        }
        return mHasUsbFileLiveData;
    }

    public LiveData<String> getVerLiveData() {
        if (mVerLiveData.getValue() == null) {
            mVerLiveData.setValue("0");
        }
        return mVerLiveData;
    }
}
