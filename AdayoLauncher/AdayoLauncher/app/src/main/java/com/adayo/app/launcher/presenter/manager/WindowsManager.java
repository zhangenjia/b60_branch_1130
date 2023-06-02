package com.adayo.app.launcher.presenter.manager;

import android.util.Log;
import com.adayo.app.launcher.ui.view.CustomDialog;
import warning.LauncherApplication;

public class WindowsManager {
    private static final String TAG = "WindowsManager111";
    private volatile static WindowsManager mWindowsManager;
    private int isVisibility = -1;

    public static WindowsManager getInstance() {
        if (mWindowsManager == null) {
            synchronized (WindowsManager.class) {
                if (mWindowsManager == null) {
                    mWindowsManager = new WindowsManager();
                }
            }
        }
        return mWindowsManager;
    }


    public  void seBottomDialogVisibility(int isVisibility) {
        this.isVisibility = isVisibility;
    }

    public  int  geBottomDialogVisibility() {
        Log.d(TAG, "geBottomDialogVisibility: "+isVisibility);
        return isVisibility;
    }
}
