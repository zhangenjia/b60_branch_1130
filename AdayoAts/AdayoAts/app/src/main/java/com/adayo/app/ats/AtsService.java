package com.adayo.app.ats;

import android.annotation.SuppressLint;
import android.app.Service;
import android.car.VehiclePropertyIds;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.adayo.app.ats.bean.VehicleDataInFo;
import com.adayo.app.ats.dialog.AtsDialog;
import com.adayo.app.ats.factory.ResourcesFactory;
import com.adayo.app.ats.function.SystemStatusImpl;
import com.adayo.proxy.setting.bcm.IClientCallBack;
import com.adayo.proxy.setting.bcm.controller.BcmManager;

import java.util.ArrayList;
import java.util.List;

import static com.adayo.app.ats.util.Constants.ATS_VERSION;


public class AtsService extends Service {

    private static final String TAG = ATS_VERSION + AtsService.class.getSimpleName();
    private static final String PACKGENAME = "com.adayo.app.ats";
    private List<String> list = new ArrayList<>();
    private int report = 1234;
    private int retry = 2234;
    private int targetAtsMod = -1;
    private int currentAtsMod = -1;
    private int lastTargetAtsMod = -1;
    private int lastCurrentAtsMod = -1;
    private BcmManager mBcmManager;
    private AtsDialog atsDialog;
    private ResourcesFactory resourcesFactory;
    private int num;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == retry) {
                boolean b = mBcmManager.registerListener(mCallBack, PACKGENAME, list);
                if (b) {
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                }
            } else if (msg.what == report) {
                VehicleDataInFo info = (VehicleDataInFo) msg.obj;
                if (info.getId() == VehiclePropertyIds.REQUEST_DRIVE_MODE) {
                    Log.d(TAG, "handleMessage: ATS  REQUEST_DRIVE_MODE");
                    Bundle bundle = info.getBundle();
                    Integer requestMod = bundle.getInt("IntValue");
                    if (requestMod != null) {
                        targetAtsMod = resourcesFactory.getRequestModMap().get(requestMod);
                    }
                    atsDialog.setTargetAtsMode(targetAtsMod);
                    atsDialog.startOrUpdateDialogTimer();//开始或重置Timer
                    upDateConfirmAtsMode();
                } else if (info.getId() == VehiclePropertyIds.ESP_THE_ACK_OF_THE_DRIVER_MODE_REQUEST) {
                    bundle = info.getBundle();
                    updateCounter();
                }
                if (info.getId() == VehiclePropertyIds.REQUEST_ATS_MODE) {
                    Bundle bundle = info.getBundle();
                    Integer requestDriveMode = bundle.getInt("IntValue");
                    Log.d(TAG, "handleMessage: ATS  REQUEST_ATS_MODE " + requestDriveMode);
                    if (requestDriveMode == 0) {
                        return;
                    }
                    Log.d(TAG, "aaREQUEST_DRIVE_MODE: request_drive_mode = " + requestDriveMode + " atsDialog.isShowing() " + atsDialog.isShowing() + " currentAtsMod = " + currentAtsMod);
                    if (!atsDialog.isShowing()) {
                        if (currentAtsMod >= 0) {
                            Log.d(TAG, "aaREQUEST_DRIVE_MODE: show");
                            //modify by jcshao 待机状态不可弹出ATS弹窗
                            if (SystemStatusImpl.getInstance().isPowerOff()) {
                                return;
                            }
                            //modify by xfduan 关屏后弹出ATS取消关屏
                            if (SystemStatusImpl.getInstance().isScreenOff()) {
                                SystemStatusImpl.getInstance().setScreenOn();
                            }
                            atsDialog.show();
                        }
                    }
                    if (atsDialog != null) {
                        Log.d(TAG, "handleMessage: num " + num);
                        if (num == 2) {
                            atsDialog.startNoCycleTipsAnim(requestDriveMode);
                        }
                    }

                }
            }
        }
    };
    private Bundle bundle;

    private void upDateConfirmAtsMode() {

        Integer confirmMod = bundle.getInt("IntValue");
        if (confirmMod != null) {
            currentAtsMod = resourcesFactory.getConfirmModMap().get(confirmMod);
            if (lastCurrentAtsMod != currentAtsMod) { //确认信号的上报不和上次一样再处理
                atsDialog.setCurrentAtsMode(currentAtsMod);
                lastCurrentAtsMod = currentAtsMod;
            }
        } else {
        }
        if (atsDialog.isShowing()) {
            atsDialog.startOrUpdateDialogTimer();//开始或重置Timer
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private IClientCallBack mCallBack = new IClientCallBack.Stub() {
        @Override
        public boolean onChangeListener(int id, Bundle bundle) throws RemoteException {//发到主线程更新UI
            Log.d(TAG, "onChangeListener: " + id);
            Message msg = Message.obtain();
            msg.what = report;
            VehicleDataInFo vehicleDataInFo = new VehicleDataInFo(id, bundle);
            msg.obj = vehicleDataInFo;
            handler.sendMessage(msg);
            return false;
        }

        @Override
        public boolean onErrorEvent(int i, int i1) throws RemoteException {
            Log.d(TAG, "onErrorEvent(): i = " + i + " i1 = " + i1);
            return false;
        }

        @Override
        public boolean isConnectCarProperty(boolean b) throws RemoteException {//todo 重连机制 和争哥确认什么时候算注册失败
            Log.d(TAG, "isConnectCarProperty():b = " + b);
//            if (b) {
//                initFinish();
//            }
            return false;
        }
    };

    /**
     * 服务启动
     *
     * @param intent
     * @param startId
     */


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 初始化流程
     *
     * @param
     * @param
     */

    public void init() {
        initSkinChange();
        Log.d(TAG, "init start");
        resourcesFactory = ResourcesFactory.getInstance(getBaseContext());
        atsDialog = new AtsDialog(getApplicationContext());
        atsDialog.setCanceledOnTouchOutside(false);
        registerBcmService();//todo 注册BcmService
        Log.d(TAG, "init end");
    }

    private void initSkinChange() {
//        AAOP_HSkinHelper
//                .init(getApplicationContext(), true, "AdayoATS");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: " + newConfig);
        atsDialog = new AtsDialog(getApplicationContext());
        atsDialog.setTargetAtsMode(targetAtsMod);
        atsDialog.setCurrentAtsMode(currentAtsMod);
        if (atsDialog != null) {
            atsDialog.onConfigurationChanged();
        }
    }

    /**
     * 连接 BcmService
     */

    private void registerBcmService() {
        list.add("CarInfo");
        mBcmManager = BcmManager.getInstance();
        boolean b = mBcmManager.registerListener(mCallBack, PACKGENAME, list);
        Log.d(TAG, "init: register bcm result  " + b);
        if (!b) {
            handler.postDelayed(runnable, 500);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(retry);
        }
    };


    private void updateCounter() {

        if (targetAtsMod == currentAtsMod) {
            if (num!= 2) {
                num++;
            }
        } else {
            num = 0;
        }
    }
}
