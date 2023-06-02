package com.adayo.app.crossinf.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.adayo.mcucommproxy.IMcuCommCallback;
import com.adayo.mcucommproxy.McuCommManager;
import com.adayo.mcucommproxy.McuCommMsg0x8300;
import com.adayo.struct.JavaStruct;
import com.adayo.struct.StructException;

public class CanDataCallbcak implements IMcuCommCallback {

    /*常量*/
    public static final int FUNC_INIT_RETRY_CNT = 2;    // 外部功能初始化retry次数
    public static final int FUNC_INIT_RETRY_TIME_INTERVAL = 100;    // 外部功能初始化retry时间间隔
    public static final String TAG = "CanDataCallbcak";
    private boolean m_bIsCon_McuComm = false;
    private volatile static CanDataCallbcak mControl = null;
    private McuCommMsg0x8300 mMcuCmd8300 = new McuCommMsg0x8300();
    private Handler handler = null;
    private static final float coefficient = 0.0551f;//modify by xfduan 0.051->0.0551
    private int steelAngle;

    public static CanDataCallbcak getControl() {
        if (mControl == null) {
            synchronized (CanDataCallbcak.class) {
                if (mControl == null) {
                    mControl = new CanDataCallbcak();
                }
            }
        }
        return mControl;
    }

    private CanDataCallbcak() {

    }

    public void init_McuComm() {

        int nRetryCnt = FUNC_INIT_RETRY_CNT;
        int nRetryTimeInterval = FUNC_INIT_RETRY_TIME_INTERVAL;

        try {
            McuCommManager ins = McuCommManager.getInstance();

            do {
                //判断是否是retry流程，若是，则需要提前延时
                //依据：只有nRetryCnt--过才是retry流程，那么肯定不等于入参retryCnt
                if (nRetryCnt != FUNC_INIT_RETRY_CNT) {
                    Thread.currentThread().sleep(nRetryTimeInterval);
                }

                //1.连接
                if (m_bIsCon_McuComm != true) {
                    if (ins.connectMcuCommServer() == true) {
                        m_bIsCon_McuComm = true;
                    } else {
                        m_bIsCon_McuComm = false;
                        continue;
                    }
                }
                registSysSettingsCallback();

            } while ((--nRetryCnt) >= 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registSysSettingsCallback() {
        McuCommManager.registMcuCommCallback(this, (byte) 0x03);
        McuCommManager.registMcuCommCallback(this, (byte) 0x04);
        McuCommManager.registMcuCommCallback(this, (byte) 0x05);
    }

    public void unregistSysSettingsCallback() {
        McuCommManager.unregistMcuCommCallback(this, (byte) 0x03);
        McuCommManager.unregistMcuCommCallback(this, (byte) 0x04);
        McuCommManager.unregistMcuCommCallback(this, (byte) 0x05);
    }

    @Override
    public void mcuCommCallback(byte[] msgbuf, char len) {
        if (msgbuf[0] == (byte) 0x83 && msgbuf[1] == 0x00) {
            unpack(mMcuCmd8300, msgbuf);
            steelAngle = getSteelAngle();
            float i = ((float) (steelAngle - 7800) / 10) * coefficient;
            int round = Math.round(i);
            Message msg = Message.obtain();
//            if (steelAngle >= 0 && steelAngle <= 15600) {
//                if (round < -40) {
//                    round = -40;
//                } else if (round > 40) {
//                    round = 40;
//                }
//            }
            msg.what = 8300;
            msg.obj = round;
            if (handler != null) {
                handler.sendMessage(msg);
            }
            Log.d(TAG, "MCU数据方向盘转角值 = " + steelAngle + "  车轮转向值 = " + i + " 取整" + round);
        }
    }

    private void unpack(Object o, byte[] msgbuf) {
        try {
            JavaStruct.unpack(o, msgbuf);
        } catch (StructException e) {
            Log.d(TAG, "   MCU数据解析异常");
            e.printStackTrace();
        }
    }

    private int getSteelAngle() {
        if (mMcuCmd8300 == null) {
            return 0;
        }
        return mMcuCmd8300.steelAngle;
    }

    public int getAngle() {
        return steelAngle;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;

    }
}
