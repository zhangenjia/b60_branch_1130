package com.adayo.app.setting.view.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class DirectMediaAdapter extends SimpleAdapter {
private int mpostion=-1;
private String positionString = "-1";
    private final static String TAG = DirectMediaAdapter.class.getSimpleName();

    public int getMpostion() {
        return mpostion;
    }

    public void setMpostion(int mpostion) {
        this.mpostion = mpostion;
        Map<String, String> map = (Map<String, String>) getItem(mpostion);
        positionString =  map.get("title");
        notifyDataSetChanged();
    }

    public DirectMediaAdapter(Context context, List<? extends Map<String, ?>> list, int i, String[] strings, int[] ints) {
        super(context, list, i, strings, ints);
    }

    @Override
    public void setViewText(TextView textView, String s) {
        textView.setText(s);
        boolean selected = positionString.equals(s);
        Log.d(TAG, "setViewText: selected = " + selected);
        if(selected) {
            textView.setSelected(true);
            return;
        }
        textView.setSelected(false);
    }
}
