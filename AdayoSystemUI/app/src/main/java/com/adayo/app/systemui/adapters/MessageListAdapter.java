package com.adayo.app.systemui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.interfaces.ItemTouchHelperAdapter;
import com.adayo.app.systemui.utils.StringUtils;
import com.adayo.proxy.msg.notify.bean.MessageNotification;
import com.android.internal.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_DATA_FLOW;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_SYS_UPGRADE;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_TSP_NOTIFICATION;

/**
 * @author ADAYO-XUYUE
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> implements ItemTouchHelperAdapter {
    private List<MessageNotification> messageInfoList;
    private OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mInflater;

    public MessageListAdapter(List<MessageNotification> messageList){
        messageInfoList = messageList;
        mInflater = LayoutInflater.from(SystemUIApplication.getSystemUIContext());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = mInflater.inflate(R.layout.message_item_view, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder vh, int i) {
        if(i >= getItemCount()){
            return;
        }
        switch (messageInfoList.get(i).getmType()){
            case MSG_TYPE_DATA_FLOW:
                vh.messageTitle.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_traffic_renewal));
                vh.messageType.setImageResource(R.drawable.dropbox_icon_system);
                break;
            case MSG_TYPE_TSP_NOTIFICATION:
                vh.messageTitle.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_tsp));
                vh.messageType.setImageResource(R.drawable.dropbox_icon_system);
                break;
            case MSG_TYPE_SYS_UPGRADE:
                vh.messageTitle.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_ota));
                vh.messageType.setImageResource(R.drawable.dropbox_icon_ota_upgrade);
                break;
            default:
                break;
        }
        vh.unreadIcon.setVisibility(messageInfoList.get(i).ismReaded() == 1 ? View.GONE : View.VISIBLE);
        vh.messageTime.setText(StringUtils.timeToString(messageInfoList.get(i).getmTimeStamp(), "MM-dd"));
        vh.messageContent.setText(messageInfoList.get(i).getmContext());
        vh.itemLayout.setOnClickListener(v -> mOnItemClickListener.onItemClick(i));
    }

    @Override
    public int getItemCount() {
        if(null == messageInfoList){
            return 0;
        }
        return messageInfoList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //交换位置
        Collections.swap(messageInfoList,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        //移除数据
        messageInfoList.remove(position);
        notifyItemRemoved(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public final RelativeLayout itemLayout;
        public final ImageView messageType;
        public final ImageView unreadIcon;
        public final TextView messageTitle;
        public final TextView messageTime;
        public final TextView messageContent;
        public MyViewHolder(View view) {
            super(view);
            itemLayout = view.findViewById(R.id.item_layout);
            messageType = view.findViewById(R.id.message_type);
            unreadIcon = view.findViewById(R.id.unread_icon);
            messageTitle = view.findViewById(R.id.message_title);
            messageTime = view.findViewById(R.id.message_time);
            messageContent = view.findViewById(R.id.message_content);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int index);
    }
}