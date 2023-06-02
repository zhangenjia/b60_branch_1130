package com.adayo.app.systemui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.View;

import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.managers.SystemStatusManager;
import com.adayo.app.systemui.managers.view.CommandQueue;
import com.adayo.app.systemui.managers.view.WindowsManager;
import com.adayo.app.systemui.receivers.InstructionsReceiver;
import com.adayo.app.systemui.utils.LogUtil;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.StatusBarIcon;

import java.util.ArrayList;

import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

public class SystemUIService extends Service implements CommandQueue.Callbacks {

    public SystemUIService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.debugI(TAG, "onCreate begin");
        createAndAddWindows();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initOthers();
        }).start();
        LogUtil.debugI(TAG, "onCreate end");
    }

    private void registerSystemStatus() {
        SystemStatusManager.getInstance();
    }

    private void registerReceivers(){
        BroadcastReceiver instructionsReceiver = new InstructionsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SystemUIContent.ACTION_SHOW_HVAC_PANEL);
        filter.addAction(SystemUIContent.ACTION_UPDATE_QS_PANEL);
        filter.addAction(SystemUIContent.ACTION_SHOW_VOLUME_PANEL);
        filter.addAction(SystemUIContent.ADAYO_NAVI_SERVICE_SEND );
        filter.addAction(SystemUIContent.ACTION_HIDE_PANELS );
        registerReceiver(instructionsReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.debugI(TAG, "onBind begin");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.debugI(TAG, "begin");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.debugI(TAG, "begin");
        super.onConfigurationChanged(newConfig);
        LogUtil.debugI(TAG, "end");
    }

    private void createAndAddWindows() {
        LogUtil.debugI(TAG, "begin");
//        addDockBarWindow();
        addStatusBarWindow();
        addNavigationBarWindow();
        LogUtil.debugI(TAG, "end");
    }

//    private void addDockBarWindow() {
//        LogUtil.debugI(TAG, "begin");
//        WindowsManager.setDockBarVisibility(View.VISIBLE);
//        LogUtil.debugI(TAG, "end");
//    }

    private void addNavigationBarWindow() {
        LogUtil.debugI(TAG, "begin");
        WindowsManager.setNavigationBarVisibility(View.VISIBLE);
        LogUtil.debugI(TAG, "end");
    }

    private void addStatusBarWindow() {
        LogUtil.debugI(TAG, "begin");
        WindowsManager.setStatusBarVisibility(View.VISIBLE);
        LogUtil.debugI(TAG, "end");
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private void initOthers(){
//        WindowsManager.initPanel();
        registerStatusBar();
        registerReceivers();
//        registerSystemStatus();
        mHandler.post(() -> registerSystemStatus());
        mHandler.post(() -> WindowsManager.initPanel());
    }

    private void registerStatusBar() {
        LogUtil.debugI(TAG, "begin");
        IStatusBarService barService = IStatusBarService.Stub.asInterface(
                ServiceManager.getService(SystemUIContent.STATUS_BAR_SERVICE));
        CommandQueue mCommandQueue = new CommandQueue();
        mCommandQueue.addCallbacks(this);
        int[] switches = new int[9];
        ArrayList<IBinder> binders = new ArrayList<>();
        ArrayList<String> iconSlots = new ArrayList<>();
        ArrayList<StatusBarIcon> icons = new ArrayList<>();
        Rect fullscreenStackBounds = new Rect();
        Rect dockedStackBounds = new Rect();
        try {
            barService.registerStatusBar(mCommandQueue, iconSlots, icons, switches, binders,
                    fullscreenStackBounds, dockedStackBounds);
        } catch (RemoteException ex) {
            // If the system process isn't there we're doomed anyway.
        }
        LogUtil.debugI(TAG, "end");
    }

    @Override
    public void setSystemUiVisibility(int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds) {
        LogUtil.debugI(TAG, "vis");
        WindowsManager.setStatusBarVisibility((vis & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0 ? View.VISIBLE : View.GONE);
        WindowsManager.setNavigationBarVisibility((vis & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0 ? View.VISIBLE : View.GONE);
    }
}
