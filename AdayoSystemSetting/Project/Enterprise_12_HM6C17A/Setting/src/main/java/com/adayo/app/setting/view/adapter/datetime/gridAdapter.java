package com.adayo.app.setting.view.adapter.datetime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.app.setting.R;
import com.adayo.app.setting.skin.SkinUtil;
import com.adayo.app.base.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class gridAdapter extends BaseAdapter {
    private String TAG = "gridAdapter";
    private Map<Integer, String> days;
    private Context context;
    private int year;
    private int month;
    private int day;
    private int selPosition;
    private int temp = -1;

    public gridAdapter(Context context, int year, int month, int day, Map<Integer, String> days) {
        this.context = context;
        this.year = year;
        this.month = month;
        this.days = days;
        int dayNum = 0;
        this.day = day;

        selPosition = convertDay2Pos(day);

        }

    public void update(int year, int month, int day, Map<Integer, String> days) {
        LogUtil.debugD(TAG, "year =" + year + "month =" + month);
        int dayNum = 0;
        this.year = year;
        this.month = month;
        this.days = days;
        if (day > days.size()) {
            day = days.size();
        }
        selPosition = convertDay2Pos(day);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 42;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.grid_item_datetime_day, null);
            viewHolder = new ViewHolder();
            viewHolder.date_item = (TextView) view.findViewById(R.id.solar);
            viewHolder.date_item_lunar = (TextView) view.findViewById(R.id.lunar);
            viewHolder.item = (LinearLayout) view.findViewById(R.id.grid_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        int daysOfFirstWeek=getDayofWeek(year+"-"+month+"-"+1);
        LogUtil.debugD(TAG, "daysOfFirstWeek =" + daysOfFirstWeek+"i ="+i+"year ="+year+"month ="+month);
        if (i < daysOfFirstWeek - 1) {
            view.setVisibility(View.GONE);
        } else if (days.containsKey(i - daysOfFirstWeek + 2)) {
            view.setVisibility(View.VISIBLE);

            viewHolder.date_item.setText(i - daysOfFirstWeek + 2 + "");
            viewHolder.date_item_lunar.setText(days.get(i - daysOfFirstWeek + 2) + "");
        } else {

            view.setVisibility(View.GONE);
        }
        if (selPosition == i) {
            selPosition = i;
            LogUtil.debugD(TAG, "liang");
            SkinUtil.setBackground(viewHolder.item,R.drawable.bg_shape_selected);
            } else {
            viewHolder.item.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }
    private int getDayofWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if ("".equals(dateTime)) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    public void setSelectItem(int selectItem) {
        this.selPosition = selectItem;
    }
    public void setDay(int day){
      selPosition = convertDay2Pos(day);
        notifyDataSetChanged();
    }


    class ViewHolder {
        TextView date_item;
        TextView date_item_lunar;
        LinearLayout item;
    }

    private int convertDay2Pos(int day) {
        int position;

        int daysOfFirstWeek=getDayofWeek(year+"-"+month+"-"+1);
        position = day + daysOfFirstWeek - 2;

        return position;
    }

    private int convertPos2Day(int position) {
        int day;

        int daysOfFirstWeek=getDayofWeek(year+"-"+month+"-"+1);
        day = position - daysOfFirstWeek + 2;
        return day;
    }

    public int getCurrentValue() {

        return convertPos2Day(selPosition);
    }


}
