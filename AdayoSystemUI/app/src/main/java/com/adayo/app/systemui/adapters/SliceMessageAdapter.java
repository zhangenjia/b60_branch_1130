package com.adayo.app.systemui.adapters;

import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_DATA_FLOW;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_SYS_UPGRADE;
import static com.adayo.proxy.msg.notify.bean.MessageConst.MSG_TYPE_TSP_NOTIFICATION;

import android.annotation.NonNull;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.systemui.R;
import com.adayo.app.systemui.SystemUIApplication;
import com.adayo.app.systemui.configs.SystemUIContent;
import com.adayo.app.systemui.utils.LogUtil;
import com.adayo.app.systemui.utils.StringUtils;
import com.adayo.proxy.msg.notify.bean.MessageNotification;
import com.android.internal.widget.RecyclerView;

import java.util.List;

public class SliceMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MessageNotification> messageInfoList;
    private final int TYPE_ITEM = 0;//正常的Item

    private final Context mContext;

    private boolean hasMore = true;

    public SliceMessageAdapter(List<MessageNotification> messageInfoList, Context mContext) {
        this.messageInfoList = messageInfoList;
        this.mContext = mContext;
    }

    //第一步 定义接口
    public interface OnItemClickListener {
        void onClick(int position);
        void onClickDelete(int position);
    }

    private OnItemClickListener mListener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    //返回不同布局
    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.message_item_view, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(position >= getItemCount()){
            return;
        }
        if (holder instanceof ItemHolder) {
            switch (messageInfoList.get(position).getmType()) {
                case MSG_TYPE_DATA_FLOW:
                    ((ItemHolder) holder).messageTitle.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_traffic_renewal));
                    ((ItemHolder) holder).messageType.setImageResource(R.drawable.dropbox_icon_system);
                    break;
                case MSG_TYPE_TSP_NOTIFICATION:
                    ((ItemHolder) holder).messageTitle.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_tsp));
                    ((ItemHolder) holder).messageType.setImageResource(R.drawable.dropbox_icon_system);
                    break;
                case MSG_TYPE_SYS_UPGRADE:
                    ((ItemHolder) holder).messageTitle.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.message_ota));
                    ((ItemHolder) holder).messageType.setImageResource(R.drawable.dropbox_icon_ota_upgrade);
                    break;
                default:
                    break;
            }
            LogUtil.d(SystemUIContent.TAG, "type = " + messageInfoList.get(position).getmType()
             + " ; messageContent = " + messageInfoList.get(position).getmContext());
            ((ItemHolder) holder).unreadIcon.setVisibility(messageInfoList.get(position).ismReaded() == 1 ? View.GONE : View.VISIBLE);
            ((ItemHolder) holder).messageTime.setText(StringUtils.timeToString(messageInfoList.get(position).getmTimeStamp(), "MM-dd"));
            ((ItemHolder) holder).messageContent.setText(messageInfoList.get(position).getmContext());
            ((ItemHolder) holder).itemLayout.setOnClickListener(v -> mListener.onClick(position));
            ((ItemHolder) holder).messageDelete.setOnClickListener(v -> mListener.onClickDelete(position));
        }
    }

    //多出尾部刷新的item
    @Override
    public int getItemCount() {
        if(null == messageInfoList){
            return 0;
        }
        return messageInfoList.size();
    }

    //item的holder
    static class ItemHolder extends RecyclerView.ViewHolder {
        public final RelativeLayout itemLayout;
        public final ImageView messageType;
        public final ImageView unreadIcon;
        public final TextView messageTitle;
        public final TextView messageTime;
        public final TextView messageContent;
        public final ImageView messageDelete;

        public ItemHolder(View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.item_layout);
            messageType = itemView.findViewById(R.id.message_type);
            unreadIcon = itemView.findViewById(R.id.unread_icon);
            messageTitle = itemView.findViewById(R.id.message_title);
            messageTime = itemView.findViewById(R.id.message_time);
            messageContent = itemView.findViewById(R.id.message_content);
            messageDelete = itemView.findViewById(R.id.msg_delete);
        }
    }

    //是否有更多数据
    public void hasMore(boolean more) {
        this.hasMore = more;
    }

    //这里清空数据，避免下拉刷新后，还显示上拉加载的数据
    public void reData() {
        messageInfoList.clear();
    }
}
