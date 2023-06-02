package com.adayo.app.systemui.windows.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

public class ClockView extends RelativeLayout {

    private boolean mAttached = false;

    private TextView clockTime;
//    private TextView clockTimeUnit;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.clock_view, this, true);
        clockTime = mRootView.findViewById(R.id.clock_time);
//        clockTimeUnit = mRootView.findViewById(R.id.clock_time_unit);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        LogUtil.debugI(TAG, "onConfigurationChanged");
        updateTime();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
            getContext().registerReceiver(receiver, filter);
            mAttached = true;
        }
        try {
            getHandler().post(() -> updateTime());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void updateTime() {
        boolean is24 = DateFormat.is24HourFormat(getContext());
        String time;
        if (is24) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            time = sdf.format(new Date());
//            clockTimeUnit.setVisibility(View.GONE);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
            time = sdf.format(new Date());
//            clockTimeUnit.setVisibility(View.GONE);
        }
        //redmine #1018308
//        if (Calendar.getInstance().get(Calendar.AM_PM) == 0) {
////            clockTimeUnit.setBackgroundResource(R.drawable.com_icon_time_am);
//            clockTimeUnit.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.am));
//        } else {
////            clockTimeUnit.setBackgroundResource(R.drawable.com_icon_time_pm);
//            clockTimeUnit.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.pm));
//        }
        if (null != time && null != clockTime) {
            LogUtil.debugI(TAG, "clockTime = " + clockTime);
            clockTime.setText(time);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(receiver);
            mAttached = false;
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                case Intent.ACTION_TIME_CHANGED:
                case Intent.ACTION_TIMEZONE_CHANGED:
                case Intent.ACTION_CONFIGURATION_CHANGED:
                    try {
                        getHandler().postDelayed(() -> updateTime(), 20);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
