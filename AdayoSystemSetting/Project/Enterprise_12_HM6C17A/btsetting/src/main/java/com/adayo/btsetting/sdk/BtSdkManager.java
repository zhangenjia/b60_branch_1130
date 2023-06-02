package com.adayo.btsetting.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.adayo.btsetting.model.BtSettingManager;
import com.adayo.btsetting.util.BtSettingLog;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.nforetek.bt.base.jar.NforeBtBaseJar;
import com.nforetek.bt.base.listener.BluetoothServiceConnectedListener;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Y4134
 */
public class BtSdkManager implements ISdkManager {

    private static final String TAG = BtSdkManager.class.getSimpleName();
    private static final String PERSIST_START_ADB = "persist.test.start.adb";

    private int num = 0;
    private Context mContext;
    private static final int BT_CONNECT = 1;
    private static final String DEF_ADB = "0";
    private static final String START_ADB = "1";
    private static final int THREAD_POOL_SIZE = 1;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BT_CONNECT){
                BtSettingManager.getInstance(mContext).init();
            }
        }
    };

    @Override
    public void init(Context context) {
        this.mContext = context;
        if (NforeBtBaseJar.init(mContext)) {
            NforeBtBaseJar.registerBluetoothServiceConnectedListener(new BluetoothServiceConnectedListener() {
                @Override
                public void onServiceConnectedChanged(boolean b) {
                    BtSettingLog.logD(TAG, "BTService启动！");
                    btConnectSuccess();
                }
            });
        } else {
            BtSettingLog.logD(TAG, "BTService未启动！");
            ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("bt_music").build();

            ExecutorService thread = new ThreadPoolExecutor(THREAD_POOL_SIZE,
                    THREAD_POOL_SIZE,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1024),
                    namedThreadFactory,
                    new ThreadPoolExecutor.AbortPolicy());
            thread.execute(new Runnable() {
                @Override
                public void run() {
                    retry();
                }
            });
        }
        if (getStringProperties(PERSIST_START_ADB, DEF_ADB).equals(START_ADB)){
            startAdb();
        }
    }

    private void startAdb() {
        setProperty("sys.usb.config","none");
        setProperty("usb.carplay.state","in");
        setProperty("sys.usb.config","adb");
    }

    private void retry() {
        if (!NforeBtBaseJar.init(mContext)) {
            try {
                num++;
                BtSettingLog.logD(TAG, "run: 正在尝试第" + num + "次连接BT服务");
                Thread.sleep(500);
                retry();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            BtSettingLog.logD(TAG, "BTService启动！");
            btConnectSuccess();
        }
    }

    /**
     * 蓝牙连接成功
     */
    private void btConnectSuccess() {
        Message message = Message.obtain();
        message.what = BT_CONNECT;
        mHandler.sendMessage(message);
    }

    @Override
    public void unInit(Context context) {

    }

    private String getStringProperties(final String key, final String def) {
        try {
            Method method = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            return ((String) method.invoke(null, key, def));
        } catch (Exception e) {
            e.printStackTrace();
            return def;
        }
    }

    private  void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
