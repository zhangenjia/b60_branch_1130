package com.adayo.app.systemui.windows.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.adayo.app.systemui.configs.SystemUIContent.SCREEN_NUMBERS;
import static com.adayo.app.systemui.configs.SystemUIContent.TAG;

/**
 * @author XuYue
 * @description:
 * @date :2021/12/6 14:04
 */
public class ScreenClockView extends RelativeLayout {

    private boolean mAttached = false;
    private boolean isShowing = false;
    private ImageView firstHour;
    private ImageView secondHour;
    private ImageView firstMinute;
    private ImageView secondMinute;

    public ScreenClockView(Context context) {
        this(context, null);
    }

    public ScreenClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScreenClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.screen_clock_view, this, true);
        firstHour = mRootView.findViewById(R.id.first_hour);
        secondHour = mRootView.findViewById(R.id.second_hour);
        firstMinute = mRootView.findViewById(R.id.first_minute);
        secondMinute = mRootView.findViewById(R.id.second_minute);
    }

    public void setShowing(boolean showStatus){
        isShowing = showStatus;
        try {
            getHandler().post(() -> updateTime());
        } catch (Exception e) {
            // TODO: handle exception
        }
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
        if(!isShowing){
            return;
        }
        boolean is24 = DateFormat.is24HourFormat(getContext());
        String hourString;
        String minuteString;
        if (is24) {
            SimpleDateFormat hourSimpleDateFormat = new SimpleDateFormat("HH", Locale.getDefault());
            hourString = hourSimpleDateFormat.format(new Date());
        } else {
            SimpleDateFormat hourSimpleDateFormat = new SimpleDateFormat("hh", Locale.getDefault());
            hourString = hourSimpleDateFormat.format(new Date());
        }
        SimpleDateFormat minuteSimpleDateFormat = new SimpleDateFormat("mm", Locale.getDefault());
        minuteString = minuteSimpleDateFormat.format(new Date());
        if (null != firstHour && null != secondHour && null != firstMinute && null != secondMinute) {
            LogUtil.debugI(TAG, "hour = " + hourString + " ; minute" + minuteString);
            firstHour.setBackgroundResource(SCREEN_NUMBERS[Integer.parseInt(hourString)/10]);
            secondHour.setBackgroundResource(SCREEN_NUMBERS[Integer.parseInt(hourString)%10]);
            firstMinute.setBackgroundResource(SCREEN_NUMBERS[Integer.parseInt(minuteString)/10]);
            secondMinute.setBackgroundResource(SCREEN_NUMBERS[Integer.parseInt(minuteString)%10]);
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
                        getHandler().post(() -> updateTime());
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
