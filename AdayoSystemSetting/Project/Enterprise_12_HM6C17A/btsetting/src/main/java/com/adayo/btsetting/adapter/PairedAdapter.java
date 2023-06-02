package com.adayo.btsetting.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.adayo.btsetting.R;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.component.log.Trace;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.List;

/**
 * @author Y4134
 */
public class PairedAdapter extends BaseAdapter implements View.OnClickListener {
    private final String TAG = PairedAdapter.class.getSimpleName();
    private List<BluetoothDevice> datas;
    private AcitonListener deleteListener;
    Context context;
    private static final int CONNECT_ACTION = 2;
    private static final int DISCONNECT_ACTION = 1;

    private Intent getIntent(int action, String address) {
        Intent intent = new Intent();
        intent.putExtra("action", action);
        intent.putExtra("address", address);
        return intent;
    }

    @Override
    public void onClick(View v) {
        if (null == deleteListener) {
            Trace.e(TAG, "----onClick() Error----");
        } else {
            int id = v.getId();
            if (id == R.id.connect) {
                Intent i = (Intent) v.getTag();
                int action = i.getIntExtra("action", 2);
                if (action == CONNECT_ACTION) {
                    deleteListener.onConnectClick(i.getStringExtra("address"));
                } else {
                    deleteListener.onDisconnectClick(i.getStringExtra("address"));
                }
            } else if (id == R.id.delete) {
                deleteListener.onDeleteClick(v.getTag().toString());
            }

        }
    }

    public PairedAdapter(Context context, List<BluetoothDevice> list) {
        datas = list;
        this.context = context;
    }

    public void updateData(List<BluetoothDevice> list) {
        datas = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return (BluetoothDevice) datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = AAOP_HSkin.getLayoutInflater(context)
                    .inflate(R.layout.bt_paired_item_layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (!getItem(position).isHfpConnected() && !getItem(position).isA2dpConnected()) {
            viewHolder.connBtn.setText("连接");
            viewHolder.connBtn.setTag(getIntent(CONNECT_ACTION, getItem(position).getAddress()));
        } else {
            viewHolder.connBtn.setText("断开");
            viewHolder.connBtn.setTag(getIntent(DISCONNECT_ACTION, getItem(position).getAddress()));
        }

        viewHolder.deleteBtn.setTag(getItem(position).getAddress());
        viewHolder.deleteBtn.setOnClickListener(this);
        viewHolder.connBtn.setOnClickListener(this);
        viewHolder.nameTv.setText(getItem(position).getRemoteDevName());
        return convertView;
    }

    public void setActionListener(AcitonListener listener) {
        deleteListener = listener;
    }

    public interface AcitonListener {
        /**
         * 连接
         *
         * @param var1 var1
         */
        void onConnectClick(String var1);

        /**
         * 断开
         *
         * @param var1 var1
         */
        void onDisconnectClick(String var1);

        /**
         * 删除
         *
         * @param var1 var1
         */
        void onDeleteClick(String var1);

        /**
         * 连接
         *
         * @param var1 var1
         */
        void onConnected(boolean var1);
    }

    static class ViewHolder {
        TextView nameTv;
        Button deleteBtn;
        Button connBtn;

        public ViewHolder(View convertView) {
            nameTv = convertView.findViewById(R.id.deviceName);
            deleteBtn = convertView.findViewById(R.id.delete);
            connBtn = convertView.findViewById(R.id.connect);
        }
    }
}
