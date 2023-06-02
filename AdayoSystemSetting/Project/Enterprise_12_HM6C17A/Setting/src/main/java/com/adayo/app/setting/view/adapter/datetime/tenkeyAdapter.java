package com.adayo.app.setting.view.adapter.datetime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.app.setting.R;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;


public class tenkeyAdapter extends BaseAdapter {
    private Context mContext;

    public tenkeyAdapter(Context context) {
        mContext = context;
    }
    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = AAOP_HSkin.getLayoutInflater(mContext).inflate(R.layout.grid_item_datetime_tenkey, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.textView);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (i == 9){
            view.setVisibility(View.GONE);
        } else if (i==10){
            view.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText("0");
            viewHolder.imageView.setVisibility(View.GONE);
        } else if (i==11){
            view.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.textView.setText(i+1+ "");
            viewHolder.imageView.setVisibility(View.GONE);
        }
        return view;
    }
    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
