package com.adayo.app.setting.drive;

import android.arch.lifecycle.ViewModel;

import com.adayo.app.setting.devm.DevnmRequest;

public class DriveViewModel extends ViewModel {
    public final DriveRequest mDriveRequest=new DriveRequest();
    {
        mDriveRequest.init();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDriveRequest.unInit();
    }
}
