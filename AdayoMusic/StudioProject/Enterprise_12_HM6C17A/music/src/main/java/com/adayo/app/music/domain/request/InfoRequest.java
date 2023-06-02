package com.adayo.app.music.domain.request;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.adayo.app.music.data.repository.DataRepo;
import com.adayo.proxy.media.api.callback.QueryListCallback;
import com.adayo.proxy.media.bean.FileInfo;
import com.adayo.proxy.media.bean.NodeInfo;
import com.adayo.proxy.media.constant.MediaConst;
import com.adayo.proxy.media.manager.AudioFunManager;
import com.adayo.proxy.media.util.QueryUtil;
import com.lt.library.util.LogUtil;

import java.util.List;

public class InfoRequest extends AbsRequest {
    private final MutableLiveData<Boolean> mDeviceStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mLoadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mEmptyStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mBrowseStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mMetadataLoadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mFolderAndFileLoadingStateLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<FileInfo>> mAllListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Pair<List<NodeInfo>, List<FileInfo>>> mDirListLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<?>> mArtistListLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<?>> mAlbumListLiveData = new MutableLiveData<>();

    {
        DataRepo.getInstance().needDeviceInfo(dataResult -> {
            LogUtil.d("deviceInfoResult: " + dataResult);
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
                    mDirListLiveData.postValue(null);
                    mAlbumListLiveData.postValue(null);
                    mArtistListLiveData.postValue(null);
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
                    mDeviceStateLiveData.postValue(true);
                    DataRepo.getInstance().needFileInfos(devicePort, dataResult2 -> {
                        LogUtil.d("fileInfosResult responseInfo: " + dataResult2.getResponseInfo());
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
                            mMetadataLoadingStateLiveData.postValue(true);
                        }
                    });
                    break;
                case MediaConst.DeviceStageDef.PARSE_END:
                    mDeviceStateLiveData.postValue(true);
                    DataRepo.getInstance().needFileInfos(devicePort, dataResult2 -> {
                        LogUtil.d("fileInfosResult responseInfo: " + dataResult2.getResponseInfo());
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
                            mMetadataLoadingStateLiveData.postValue(false);
                        }
                    });
                    break;
                default:
                    break;
            }
        });
    }

    public void requireDirList(String folderPath) {
        mFolderAndFileLoadingStateLiveData.setValue(true);
        mDirListLiveData.setValue(null);
        if (folderPath == null) {
            folderPath = AudioFunManager.getFunction().getDevicePath(MediaConst.DevicePortDef.EXTERNAL_USB_1);
        }
        DataRepo.getInstance().needFolderAndFileInfos(folderPath, dataResult -> {
            LogUtil.d("folderAndFileInfosResult responseInfo: " + dataResult.getResponseInfo());
            if (!dataResult.getResponseInfo().isSuccess()) {
                return;
            }
            Pair<List<NodeInfo>, List<FileInfo>> folderAndFileInfos = dataResult.getResult();
            mDirListLiveData.postValue(folderAndFileInfos);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }//500ms 保证 Loading 显示效果
            mFolderAndFileLoadingStateLiveData.postValue(false);
        });
    }

    public void requireAlbumList(String albumName) {
        if (albumName == null) {
            QueryUtil.queryAlbumNames(mAllListLiveData.getValue(), new QueryListCallback<String>() {
                @Override
                public void onSuccess(List<String> list) {
                    mAlbumListLiveData.postValue(list);
                }

                @Override
                public void onFailure(int code) {
                }
            });
        } else {
            QueryUtil.queryAudiosInAlbum(mAllListLiveData.getValue(), albumName, new QueryListCallback<FileInfo>() {
                @Override
                public void onSuccess(List<FileInfo> list) {
                    mAlbumListLiveData.postValue(list);
                }

                @Override
                public void onFailure(int code) {
                }
            });
        }
    }// TODO: 2022/3/1 待封装至DataRepo

    public void requireArtistList(String artistName) {
        if (artistName == null) {
            QueryUtil.queryArtistNames(mAllListLiveData.getValue(), new QueryListCallback<String>() {
                @Override
                public void onSuccess(List<String> list) {
                    mArtistListLiveData.postValue(list);
                }

                @Override
                public void onFailure(int code) {
                }
            });
        } else {
            QueryUtil.queryAudiosInArtist(mAllListLiveData.getValue(), artistName, new QueryListCallback<FileInfo>() {
                @Override
                public void onSuccess(List<FileInfo> list) {
                    mArtistListLiveData.postValue(list);
                }

                @Override
                public void onFailure(int code) {
                }
            });
        }
    }// TODO: 2022/3/1 待封装至DataRepo

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

    public LiveData<Boolean> getMetadataLoadingStateLiveData() {
        return mMetadataLoadingStateLiveData;
    }

    public LiveData<Boolean> getFolderAndFileLoadingStateLiveData() {
        return mFolderAndFileLoadingStateLiveData;
    }

    public LiveData<List<FileInfo>> getAllListLiveData() {
        return mAllListLiveData;
    }

    public LiveData<Pair<List<NodeInfo>, List<FileInfo>>> getDirListLiveData() {
        return mDirListLiveData;
    }

    public LiveData<List<?>> getArtistListLiveData() {
        return mArtistListLiveData;
    }

    public LiveData<List<?>> getAlbumListLiveData() {
        return mAlbumListLiveData;
    }
}
