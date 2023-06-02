package com.adayo.app.video.domain.request;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adayo.app.video.data.repository.DataRepo;
import com.adayo.proxy.media.bean.FileInfo;
import com.adayo.proxy.media.constant.MediaConst;
import com.adayo.proxy.media.util.HexStateUtil;
import com.lt.library.util.LogUtil;

import java.util.List;

public class InfoRequest extends AbsRequest {
    private final MutableLiveData<Boolean> mDrivingWarningStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mDeviceStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mLoadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mEmptyStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mBrowseStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<FileInfo>> mAllListLiveData = new MutableLiveData<>();

    {
        DataRepo.getInstance().needBreakState(dataResult -> {
            LogUtil.d("needBreakState dataResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            mDrivingWarningStateLiveData.postValue(HexStateUtil.has(dataResult.getResult(), MediaConst.BreakStateDef.DRIVING_WARNING));
        });
        DataRepo.getInstance().needDeviceInfo(dataResult -> {
            LogUtil.d("needDeviceInfo dataResult: " + dataResult);
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            Integer devicePort = dataResult.getResult().first;
            Integer deviceStage = dataResult.getResult().second;
            if (MediaConst.DevicePortDef.EXTERNAL_USB_1 != devicePort) {
                return;
            }
            switch (deviceStage) {
                case MediaConst.DeviceStageDef.NULL:
                case MediaConst.DeviceStageDef.FIND_UNMOUNTED:
                    mBrowseStateLiveData.postValue(false);
                    mEmptyStateLiveData.postValue(false);
                    mLoadingStateLiveData.postValue(false);
                    mDeviceStateLiveData.postValue(false);
                    mAllListLiveData.postValue(null);
                    break;
                case MediaConst.DeviceStageDef.FIND_MOUNTED:
                case MediaConst.DeviceStageDef.SCAN_START:
                case MediaConst.DeviceStageDef.SCAN_FIRST:
                    mDeviceStateLiveData.postValue(true);
                    mLoadingStateLiveData.postValue(true);
                    mEmptyStateLiveData.postValue(false);
                    mBrowseStateLiveData.postValue(false);
                    break;
                case MediaConst.DeviceStageDef.SCAN_END:
                case MediaConst.DeviceStageDef.PARSE_END:
                    mDeviceStateLiveData.postValue(true);
                    DataRepo.getInstance().needFileInfos(devicePort, dataResult2 -> {
                        LogUtil.d("needFileInfos responseInfo: " + dataResult2.getResponseInfo());
                        if (!dataResult2.getResponseInfo().isSuccess()) {
                            return;
                        }
                        List<FileInfo> fileInfos = dataResult2.getResult();
                        LogUtil.d("fileInfos size: " + fileInfos.size());
                        mAllListLiveData.postValue(fileInfos);
                        mLoadingStateLiveData.postValue(false);
                        if (fileInfos.isEmpty()) {
                            mEmptyStateLiveData.postValue(true);
                            mBrowseStateLiveData.postValue(false);
                        } else {
                            mEmptyStateLiveData.postValue(false);
                            mBrowseStateLiveData.postValue(true);
                        }
                    });
                    break;
                default:
                    break;
            }
        });
    }

    public LiveData<Boolean> getDrivingWarningStateLiveData() {
        return mDrivingWarningStateLiveData;
    }

    public LiveData<Boolean> getDeviceStateLiveData() {
        return mDeviceStateLiveData;
    }

    public LiveData<Boolean> getLoadingStateLiveData() {
        return mLoadingStateLiveData;
    }

    public LiveData<Boolean> getEmptyStateLiveData() {
        return mEmptyStateLiveData;
    }

    public LiveData<Boolean> getBrowseStateLiveData() {
        return mBrowseStateLiveData;
    }

    public LiveData<List<FileInfo>> getAllListLiveData() {
        return mAllListLiveData;
    }
}
