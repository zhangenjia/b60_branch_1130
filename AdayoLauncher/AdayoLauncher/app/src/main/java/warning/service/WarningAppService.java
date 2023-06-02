package warning.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;

import com.adayo.app.launcher.util.SystemPropertiesUtil;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceConstants;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;

import warning.WarningDialog;
import warning.listener.ConfirmClickImpl;
import warning.listener.IConfirmClickListener;

import warning.util.SystemPropertyUtil;

import static com.adayo.app.launcher.util.MyConstantsUtil.WARNING_SKIP;
import static com.adayo.app.launcher.util.MyConstantsUtil.WARNING_STATE;


public class WarningAppService extends Service implements IConfirmClickListener.CallBack {

    private static final String TAG = "WarningAppService";
    private static final String SYSTEM_PROPERTY_KEY_BOOTWARNING_NEVER_SHOW = "persist.warning.neverShow";//是否永不显示
    private static final String SYSTEM_PROPERTY_LAUNCH_COUNT_RECORD_KEY = "launchcountrecord";
    public static final String ISNEVERSHOW = "1";//开机警告永不显示
    public static final String ISNOMALSHOW = "0";//开机警告正常显示

    @Override
    public void onCreate() {
        super.onCreate();
        ConfirmClickImpl.getInstance().addCallBack(this);
        String systemPropertyNeverShow = SystemPropertyUtil.getSystemProperty(SYSTEM_PROPERTY_KEY_BOOTWARNING_NEVER_SHOW,
                ISNOMALSHOW);
        Log.d(TAG, "onCreate: systemPropertyNeverShow" + systemPropertyNeverShow);
        if (getLaunchCountProperties().equals("once")) {
            WARNING_STATE = WARNING_SKIP;
//            startLastSource();
            return;
        }
        if (ISNOMALSHOW.equals(systemPropertyNeverShow)) {
            initTask();
        } else {
            //如果已经在上次点击了不再提醒，本次直接调用last源，但不弹警告
            WARNING_STATE = WARNING_SKIP;
            startLastSource();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:=====> ");
        return null;
    }


    private void initTask() {

        WarningDialog warningDialog = new WarningDialog(getApplicationContext());
        warningDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//      warningDialog.initView();
        warningDialog.show();
    }

    @SuppressLint("PrivateApi")
    private void startLastSource() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int result = SrcMngSwitchManager.getInstance().startLastSource();
                Log.d(TAG, "sourceMng startLastSource result = " + result);
                while (result != SourceConstants.SUCCESS) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    result = SrcMngSwitchManager.getInstance().startLastSource();
                    Log.d(TAG, "sourceMng startLastSource result = " + result);
                }
            }
        }).start();
    }


    /**
     * 点击警告画面同意按钮的回调
     */
    @Override
    public void callBack() {
        startLastSource();
    }

    private String getLaunchCountProperties() {
        String string = SystemPropertiesUtil.getInstance().getStringMethod(SYSTEM_PROPERTY_LAUNCH_COUNT_RECORD_KEY, "");
        return string;
    }
}
