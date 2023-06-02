package com.adayo.btsetting.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.btsetting.R;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.common.bluetooth.constant.BtDef;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.List;

/**
 * @author Y4134
 */
public class ScanAdapter extends BaseAdapter implements View.OnClickListener {

    private final String TAG = "ConnBluetoothFragment";
    private List<BluetoothDevice> datas;

    Context context;


    @Override
    public void onClick(View v) {

    }

    public ScanAdapter(Context context, List<BluetoothDevice> list) {
        datas = list;
        this.context = context;
    }

    public void updateData(List<BluetoothDevice> list) {
        datas = list;
        notifyDataSetChanged();
    }

    public void updateConnectState(BusinessConstants.ConnectionStatus type, String address) {
        if (!TextUtils.isEmpty(address) && (datas != null)) {
            //如果连接状态变化通知的蓝牙地址有效，并且可配对列表有数据，则进行匹配
            //遍历可配对列表的设备
            for (BluetoothDevice device : datas) {
                //如果存在通知中的地址
                if (address.equals(device.getAddress())) {
                    //如果状态是失败，则标记该设备为连接失败状态
                    if (type == BusinessConstants.ConnectionStatus.FAILED) {
                        device.setState(BtDef.STATE_CONNECT_FAIL);
                    } else {
                        //否则标记为就绪状态
                        device.setState(BtDef.STATE_READY);
                    }
                    break;
                }
            }
            //数据发生了变化，通知list重新描画
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = AAOP_HSkin.getLayoutInflater(context)
                    .inflate(R.layout.bt_item_hispair, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvConnecting.setVisibility(View.GONE);
        viewHolder.deleteBtn.setVisibility(View.GONE);
        viewHolder.nameTv.setText(getItem(position).getRemoteDevName());
        return convertView;
    }


    static class ViewHolder {
        TextView nameTv;
        ImageView tvConnecting;
        ImageButton deleteBtn;

        public ViewHolder(View convertView) {
            nameTv = convertView.findViewById(R.id.tv_hispair_name);
            tvConnecting = convertView.findViewById(R.id.iv_hispair_status);
            deleteBtn = convertView.findViewById(R.id.ib_hispair_delete);
        }
    }
}
