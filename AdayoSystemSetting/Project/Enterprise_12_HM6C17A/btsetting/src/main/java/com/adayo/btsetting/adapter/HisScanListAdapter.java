package com.adayo.btsetting.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.btsetting.R;
import com.adayo.btsetting.constant.Contants;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.common.bluetooth.constant.BtDef;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.List;

/**
 * @author Y4134
 */
public class HisScanListAdapter extends BaseAdapter implements View.OnClickListener {
    private PairedAdapter.AcitonListener deleteListener;

    private final String TAG = "ConnBluetoothFragment";
    private List<BluetoothDevice> datas;
    Context context;

    @Override
    public void onClick(View v) {
    }

    public HisScanListAdapter(Context context, List<BluetoothDevice> list) {
        datas = list;
        this.context = context;
    }

    public void updateConnectState(BusinessConstants.ConnectionStatus type, String address) {
        if (!TextUtils.isEmpty(address) && (datas != null)) {
            //如果连接状态变化通知的蓝牙地址有效，并且可配对列表有数据，则进行匹配
            //遍历可配对列表的设备
            for (BluetoothDevice device : datas) {
                //如果存在通知中的地址
                if (address.equals(device.getAddress())) {
                    device.setState(BtDef.STATE_CONNECTING);
                    break;
                }
            }
            //数据发生了变化，通知list重新描画
            notifyDataSetChanged();
        }
    }

    public void updateData(List<BluetoothDevice> list) {
        Log.i(TAG, "HisScanListAdapter----------updateData" + list.size());
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

    @SuppressLint({"ResourceAsColor"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "HisScanListAdapter----------getView" + position);

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = AAOP_HSkin.getLayoutInflater(context)
                    .inflate(R.layout.bt_item_hispair, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.deleteBtn.setTag(getItem(position).getAddress());
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteListener.onDeleteClick(view.getTag().toString());
            }
        });
        Log.i("HisScanListAdapter", getItem(position).isHfpConnected() + "---" + getItem(position).isA2dpConnected());
        if (getItem(position).isHfpConnected() || getItem(position).isA2dpConnected()) {
            Log.d(TAG, "getView: mmmmmmmmmmmmmmmmm");
            viewHolder.ivConnectRefresh.setVisibility(View.GONE);
            AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.ivConnectRefresh.getDrawable();
            if (animationDrawable != null) {
                animationDrawable.stop();
            }
            viewHolder.ivConnectStatus.setVisibility(View.VISIBLE);
            AAOP_HSkin.with(viewHolder.ivConnectStatus)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.drawable.icon_connected)
                    .applySkin(false);
            AAOP_HSkin.with(viewHolder.nameTv)
                    .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.common_text)
                    .applySkin(false);
            Contants.isConnectBluetooth = true;
        } else if (getItem(position).getState() == (BtDef.STATE_CONNECTING)) {
            Log.d(TAG, "getView: sssssssssssss");

            viewHolder.ivConnectStatus.setVisibility(View.GONE);
            viewHolder.ivConnectRefresh.setVisibility(View.VISIBLE);
            AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.ivConnectRefresh.getDrawable();
            if (animationDrawable != null) {
                animationDrawable.start();
            }
            viewHolder.nameTv.setTextColor(context.getColor(R.color.gray));
        } else {
            Log.d(TAG, "getView: 11111111");
            viewHolder.ivConnectStatus.setVisibility(View.GONE);
            viewHolder.ivConnectRefresh.setVisibility(View.GONE);
            AnimationDrawable animationDrawable = (AnimationDrawable) viewHolder.ivConnectRefresh.getDrawable();
            if (animationDrawable != null) {
                animationDrawable.stop();
            }
            AAOP_HSkin.with(viewHolder.nameTv)
                    .addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.gray)
                    .applySkin(false);
        }
        viewHolder. nameTv.setText(getItem(position).getRemoteDevName());
        return convertView;
    }

    public void setActionListener(PairedAdapter.AcitonListener listener) {
        deleteListener = listener;
    }

    static class ViewHolder {
        TextView nameTv;
        ImageView ivConnectStatus;
        ImageView ivConnectRefresh;
        ImageButton deleteBtn;

        public ViewHolder(View convertView) {
            nameTv = convertView.findViewById(R.id.tv_hispair_name);
            ivConnectStatus = convertView.findViewById(R.id.iv_hispair_status);
            ivConnectRefresh = convertView.findViewById(R.id.iv_refresh);
            deleteBtn = convertView.findViewById(R.id.ib_hispair_delete);
        }
    }

}
