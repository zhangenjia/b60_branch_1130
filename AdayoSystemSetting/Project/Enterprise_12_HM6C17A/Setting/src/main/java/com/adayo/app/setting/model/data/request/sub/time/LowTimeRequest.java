package com.adayo.app.setting.model.data.request.sub.time;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.adayo.app.setting.model.constant.ParamConstant;
import com.adayo.app.setting.base.BaseRequest;
import com.adayo.app.setting.utils.timer.DevTimer;
import com.adayo.app.base.LogUtil;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.share.interfaces.IShareDataListener;
import com.adayo.proxy.setting.system.SettingsSvcIfManager;
import com.adayo.app.setting.utils.FastJsonUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;


public class LowTimeRequest extends BaseRequest {
    private final static String TAG = LowTimeRequest.class.getSimpleName();
    private final MutableLiveData<String> mTimeLiveData = new MutableLiveData<>();private final MutableLiveData<Boolean> m24ClockModeLiveData = new MutableLiveData<>();private final Handler mHandler = new Handler();
    private final Runnable mRunnable = this::run;
    private BroadcastReceiver mBroadcastReceiver;

    private final IShareDataListener mIShareDataInterface = (type, content) -> {
        LogUtil.debugD(TAG, "type = " + type + ", content = " + content);
        if (type == ParamConstant.SHARE_INFO_ID_TIME) {
            parse24ClockMode(content);
        }
    };



    @SuppressWarnings("ConstantConditions")
    private void parse24ClockMode(String json) {
        LogUtil.i(TAG, "");
        Map<String, Object> map = FastJsonUtil.jsonToMap(json);
        if (map == null) {
            LogUtil.w(TAG, ".SHARE_INFO map is null");
            return;
        }
        boolean is24ClockMode = (boolean) map.get(ParamConstant.SHARE_INFO_KEY_TIME_24_CLOCK_MODE);
        parseTime();
        m24ClockModeLiveData.setValue(is24ClockMode);
    }

    private void parseTime() {
        LogUtil.debugD(TAG, "");
        mHandler.post(mRunnable);
    }


    public void requestTime(int year, int month, int day, int hour, int minute, int second) {
        LogUtil.i(TAG, "year =" + year + "month =" + month + "day =" + day + "hour =" + hour + "minute =" + minute);
        int[] timeArr = new int[]{year, month, day, hour, minute, 0};SettingsSvcIfManager.getSettingsManager().setDateTime(timeArr);}



    public void request24ClockMode(boolean enable) {
        LogUtil.i(TAG, "enable = " + enable);
        if (enable) {
            SettingsSvcIfManager.getSettingsManager().setTimeMode(1);
        } else {
            SettingsSvcIfManager.getSettingsManager().setTimeMode(2);
        }
    }

    public void init() {
        LogUtil.debugD(TAG, "");
        boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_TIME, mIShareDataInterface);

        if(b){
            String shareData = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_TIME);
            parse24ClockMode(shareData);

        }else {


        DevTimer devTimer = new DevTimer.Builder(0, 1000, 10, "register").build();
        devTimer.setHandler(new Handler());
        devTimer.setCallback(new DevTimer.Callback() {
            @Override
            public void callback(DevTimer timer, int number, boolean end, boolean infinite) {
                LogUtil.d(TAG, "number =" + number);
                boolean b = ShareDataManager.getShareDataManager().registerShareDataListener(ParamConstant.SHARE_INFO_ID_TIME, mIShareDataInterface);
                if (b) {
                    devTimer.stop();
                    String shareData = ShareDataManager.getShareDataManager().getShareData(ParamConstant.SHARE_INFO_ID_TIME);
                    parse24ClockMode(shareData);
                }
                if (end) {
                    LogUtil.w(TAG, "register SHARE_INFO_ID_TIME 10 time fail");
                }
            }
        });
        devTimer.start();}
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        parseTime();
        initBroadcastReceiver();
        getAppContext().registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void initBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.debugD(TAG, "");
                String action = intent.getAction();
                switch (action) {
                    case Intent.ACTION_TIME_TICK:
                    case Intent.ACTION_TIME_CHANGED:
                    case Intent.ACTION_TIMEZONE_CHANGED:
                    case Intent.ACTION_LOCALE_CHANGED:
                        mHandler.post(mRunnable);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void unInit() {
        LogUtil.debugD(TAG, "");
        mHandler.removeCallbacksAndMessages(null);
        getAppContext().unregisterReceiver(mBroadcastReceiver);
        ShareDataManager.getShareDataManager().unregisterShareDataListener(ParamConstant.SHARE_INFO_ID_TIME, mIShareDataInterface);
    }

    public LiveData<String> getTimeLiveData() {
        if (mTimeLiveData == null) {
            mTimeLiveData.setValue("0");
            LogUtil.w(TAG, "mTimeLiveData is null");
        }
        return mTimeLiveData;
    }


    public LiveData<Boolean> get24ClockModeLiveData() {
        if (m24ClockModeLiveData.getValue() == null) {
            m24ClockModeLiveData.setValue(false);
            LogUtil.w(TAG, " m24ClockModeLiveData     is null");
        }
        return m24ClockModeLiveData;
    }


    private void run() {
        String format;
        if (get24ClockModeLiveData().getValue()) {
            DateTimeFormatter formatter24 = DateTimeFormatter.ofPattern(ParamConstant.TIME_FORMAT_24, Locale.getDefault());
            format = LocalDateTime.now().format(formatter24);
        } else {
            DateTimeFormatter formatter12 = DateTimeFormatter.ofPattern(ParamConstant.TIME_FORMAT_12, Locale.getDefault());
            format = LocalDateTime.now().format(formatter12);
        }
        LogUtil.i(TAG, "format =" + format);
        mTimeLiveData.setValue(format);
    }
}
