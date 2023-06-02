package com.adayo.app.setting.view.popwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.adayo.app.setting.R;
import com.adayo.app.setting.utils.DateTimeUtils;
import com.adayo.app.setting.view.adapter.datetime.tenkeyAdapter;
import com.adayo.app.base.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.haibin.calendarview.CalendarView;

import java.lang.reflect.Method;
import java.util.Map;


public class DateTimeSettingWindow extends PopupWindow {
    private final static String TAG = DateTimeSettingWindow.class.getSimpleName();
    private static final int DEFAULT_START_YEAR = 2008;
    private static final int DEFAULT_END_YEAR = 2037;
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFAULT_END_MONTH = 12;
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFAULT_END_DAY = 31;
    private static final int INPUT_TYPE_HOUR = 1001;
    private static final int INPUT_TYPE_MIN = 1002;
    private View mPopView;
    private Button mClose;
    private Button mSave;
    private Button mAmView;
    private Button mPmView;
    private GridView mTenKeyGrid;
    private EditText mHourView;
    private EditText mMinView;
    private OnSaveButtonListener mOnSaveButtonListener;
    private int mYear, mMonth, day, hour, min;
    private boolean isAm;
    private Button btYearLeft, btYearRight, btMonthLeft, btMonthRight;
    boolean is24HourClockMode;
    private Context mFragmentContext;
    private CalendarView calendarView;
    private TextView mTvYear, mTvMonth;


    public DateTimeSettingWindow(Context context, boolean is24HourClockMode) {
        super(context);
        LogUtil.debugD(TAG, "");
        mFragmentContext = context;
        this.is24HourClockMode = is24HourClockMode;
        initData(context);
        initView(context);
        initEvent(context);
        setPopupWindow(context);
    }


    public Map<String, Integer> getMapTime() {
        Map<String, Integer> map = new ArrayMap<>();
        map.put("year", calendarView.getSelectedCalendar().getYear());
        map.put("month", calendarView.getSelectedCalendar().getMonth());
        map.put("day", calendarView.getSelectedCalendar().getDay());
        LogUtil.debugD(TAG, "year ="+ calendarView.getSelectedCalendar().getYear()+" month ="+ calendarView.getSelectedCalendar().getYear());
        map.put("hour", Integer.parseInt(String.valueOf(mHourView.getText())));
        map.put("minute", Integer.parseInt(String.valueOf(mMinView.getText())));
        if (!this.is24HourClockMode) {
            map.put("isAm", isAm ? 0 : 1);
        } else {
            map.put("isAm", -1);}
        return map;
    }

    public void setOnSaveButtonClickListener(OnSaveButtonListener onSaveButtonListener) {
        mOnSaveButtonListener = onSaveButtonListener;
    }

    private void initView(Context context) {
        LogUtil.debugD(TAG, "");
        LayoutInflater inflater = AAOP_HSkin.getLayoutInflater(context);
        mPopView = inflater.inflate(R.layout.popup_display_datetime, null);
        AAOP_HSkin.getWindowViewManager().addWindowView(mPopView);
        mClose = (Button) mPopView.findViewById(R.id.close);
        mSave = (Button) mPopView.findViewById(R.id.save);
        mTvYear = mPopView.findViewById(R.id.tv_year);
        mTvMonth = mPopView.findViewById(R.id.tv_month);
        calendarView = mPopView.findViewById(R.id.calendarView);
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                LogUtil.d(TAG, "year =" + year + " month =" + month);
                mYear = year;
                mMonth = month;
                mTvMonth.setText(String.valueOf(month));
                mTvYear.setText(String.valueOf(year));
            }
        });

        initTimeView(context);
        btYearLeft = mPopView.findViewById(R.id.bt_year_left);
        btYearRight = mPopView.findViewById(R.id.bt_year_right);
        btMonthLeft = mPopView.findViewById(R.id.bt_month_left);
        btMonthRight = mPopView.findViewById(R.id.bt_month_right);
    }


    private void initData(Context context) {
        LogUtil.debugD(TAG, "");
        hour = DateTimeUtils.getHour(is24HourClockMode);
        if ((!is24HourClockMode) && (hour == 0)) {
            hour = 12;
        }

        min = DateTimeUtils.getMinute();
        isAm = (DateTimeUtils.getApm() == 0);

    }


    @SuppressLint("ClickableViewAccessibility")
    private void initEvent(Context context) {
        btYearLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isPressed()) {
                    return;
                }
                if (mYear== DEFAULT_START_YEAR) {
                    return;
                }
                LogUtil.debugD(TAG, "mYear =" + mYear);
                mYear = mYear - 1;
                calendarView.scrollToCalendar(mYear, mMonth, calendarView.getSelectedCalendar().getDay());
            }
        });
        btYearRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isPressed()) {
                    return;
                }
                if (mYear == DEFAULT_END_YEAR) {
                    return;
                }
                LogUtil.debugD(TAG, "mYear =" + mYear);
                mYear = mYear + 1;
                calendarView.scrollToCalendar(mYear, mMonth,  calendarView.getSelectedCalendar().getDay());
}
        });
        btMonthLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isPressed()) {
                    return;
                }
                if (mMonth == DEFAULT_START_MONTH) {
                    mYear = mYear - 1;
                    mMonth=12;
                    calendarView.scrollToCalendar(mYear, mMonth, calendarView.getSelectedCalendar().getDay());
                    return;
                }
                mMonth = mMonth - 1;
                calendarView.scrollToCalendar(mYear, mMonth, calendarView.getSelectedCalendar().getDay());
            }
        });
        btMonthRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!view.isPressed()) {
                    return;
                }
                if (mMonth == DEFAULT_END_MONTH) {
                    mYear = mYear + 1;
                    mMonth=1;
                    calendarView.scrollToCalendar(mYear, mMonth, calendarView.getSelectedCalendar().getDay());
                    return;
                }
                mMonth = mMonth + 1;
                calendarView.scrollToCalendar(mYear, mMonth,  calendarView.getSelectedCalendar().getDay());
            }
        });


        mHourView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    LogUtil.debugD(TAG, "");
                    if (!mHourView.hasFocus()) {
                        String text = mMinView.getText().toString();
                        while (text.length() < 2) {
                            text = "0" + text;
                        }
                        mMinView.setText(text);
                        mHourView.setFocusableInTouchMode(true);
                        mHourView.setSelection(mHourView.getText().length());
                        mHourView.requestFocus();
                    }
                }
                return true;
            }
        });

        mMinView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    LogUtil.debugD(TAG, "");
                    if (!mMinView.hasFocus()) {
                        String text = mHourView.getText().toString();
                        while (text.length() < 2) {
                            text = "0" + text;
                        }
                        mHourView.setText(text);

                        mMinView.setFocusableInTouchMode(true);
                        mMinView.setSelection(mMinView.getText().length());
                        mMinView.requestFocus();
                    }
                }
                return true;
            }
        });

        mTenKeyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                LogUtil.debugD(TAG, "position = " + position);
                String text;
                if (mHourView.hasFocus()) {
                    text = mHourView.getText().toString();
                    if (position == 11) {
                        if (text.length() > 0) {
                            mHourView.setText(text.substring(0, text.length() - 1));
                        }
                    } else {
                        int input = 0;
                        if (position < 9) {
                            input = position + 1;
                        } else {
                            input = 0;
                        }
                        String str = adjustInputText(text, input, INPUT_TYPE_HOUR);
                        mHourView.setText(str);
                    }

                    mHourView.setFocusableInTouchMode(true);
                    mHourView.setSelection(mHourView.getText().length());
                    mHourView.requestFocus();

                } else {
                    text = mMinView.getText().toString();
                    if (position == 11) {
                        if (text.length() > 0) {
                            mMinView.setText(text.substring(0, text.length() - 1));
                        }
                    } else {
                        int input = 0;
                        if (position < 9) {
                            input = position + 1;
                        } else {
                            input = 0;
                        }
                        String str = adjustInputText(text, input, INPUT_TYPE_MIN);
                        mMinView.setText(str);
                    }
                    mMinView.setFocusableInTouchMode(true);
                    mMinView.setSelection(mMinView.getText().length());
                    mMinView.requestFocus();
                }
            }
        });
        mAmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "");
                isAm = true;
                updataApmView();
            }
        });
        mPmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "");
                isAm = false;
                updataApmView();
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "");
                new Handler().postDelayed(DateTimeSettingWindow.super::dismiss, context.getResources().getInteger(android.R.integer.config_shortAnimTime));
            }
        });
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.debugD(TAG, "");
                if ((!"".equals(mHourView.getText().toString())) && (!"".equals(mMinView.getText().toString()))) {
                    LogUtil.debugD(TAG, "");
                    mOnSaveButtonListener.onSaveButtonClick(view, getMapTime());
                    new Handler().postDelayed(DateTimeSettingWindow.super::dismiss, context.getResources().getInteger(android.R.integer.config_shortAnimTime));
                } else {
                    if ("".equals(mHourView.getText().toString())) {
                        mHourView.setText("00");
                    }
                    if ("".equals(mMinView.getText().toString())) {
                        mMinView.setText("00");
                    }
                    mOnSaveButtonListener.onSaveButtonClick(view, getMapTime());
                    new Handler().postDelayed(DateTimeSettingWindow.super::dismiss, context.getResources().getInteger(android.R.integer.config_shortAnimTime));

                }
            }
        });
    }


    private void initTimeView(Context context) {
        LogUtil.debugD(TAG, "");
        mYear = calendarView.getCurYear();
        mMonth = calendarView.getCurMonth();
        mTvYear.setText(String.valueOf(calendarView.getCurYear()));
        mTvMonth.setText(String.valueOf(calendarView.getCurMonth()));
        mAmView = mPopView.findViewById(R.id.am);
        mPmView = mPopView.findViewById(R.id.pm);
        if (is24HourClockMode) {
            mAmView.setVisibility(View.INVISIBLE);
            mPmView.setVisibility(View.INVISIBLE);
        } else {
            mAmView.setVisibility(View.VISIBLE);
            mPmView.setVisibility(View.VISIBLE);
            updataApmView();
        }
        mTenKeyGrid = mPopView.findViewById(R.id.tenkey);
        mHourView = mPopView.findViewById(R.id.hourView);
        mHourView.setText(String.format("%02d", hour));mMinView = mPopView.findViewById(R.id.minView);
        mMinView.setText(String.format("%02d", min));

        mMinView.setFocusableInTouchMode(true);
        mMinView.setSelection(mMinView.getText().length());
        mMinView.requestFocus();

        forbidDefaultSoftKeyboard();
        tenkeyAdapter mTenkeyAdapter = new tenkeyAdapter(context);
        mTenKeyGrid.setAdapter(mTenkeyAdapter);
    }

    private String adjustInputText(String text, int input, int type) {
        String temp, ret;
        if (text.length() == 0) {
            ret = input + "";
        } else {
            if (text.length() > 1) {
                temp = text.substring(text.length() - 1);
                temp = temp + input + "";
            } else {
                temp = text + input + "";
            }
            if (type == INPUT_TYPE_MIN) {
                if (Integer.parseInt(temp) > 59) {
                    ret = temp.substring(temp.length() - 1);
                } else {
                    ret = temp;
                }
            } else {
                if (is24HourClockMode) {
                    if (Integer.parseInt(temp) == 24) {
                        ret = "00";
                    } else if (Integer.parseInt(temp) > 23) {
                        ret = temp.substring(temp.length() - 1);
                    } else {
                        ret = temp;
                    }
                } else {
                    if (Integer.parseInt(temp) == 0) {
                        ret = "12";
                    } else if (Integer.parseInt(temp) > 12) {
                        ret = temp.substring(temp.length() - 1);
                    } else {
                        ret = temp;
                    }
                }
            }
        }
        return ret;
    }


    private void setPopupWindow(Context context) {
        LogUtil.debugD(TAG, "");
        this.setContentView(mPopView);this.setWidth((int) (context.getResources().getDimension(R.dimen.popup_big_width)));
        this.setHeight((int) (context.getResources().getDimension(R.dimen.popup_big_height)));

        this.setFocusable(true);this.setClippingEnabled(false);
        this.setBackgroundDrawable(null);
    }

    private void updataApmView() {
        LogUtil.debugD(TAG, "");
        if (isAm) {
            mPmView.setSelected(false);
            mAmView.setSelected(true);
        } else {
            mAmView.setSelected(false);
            mPmView.setSelected(true);
        }
    }


    private void forbidDefaultSoftKeyboard() {
        LogUtil.debugD(TAG, "");
        if (mHourView == null || mMinView == null) {
            return;
        }
        try {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            setShowSoftInputOnFocus.setAccessible(true);
            setShowSoftInputOnFocus.invoke(mHourView, false);
            setShowSoftInputOnFocus.invoke(mMinView, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
