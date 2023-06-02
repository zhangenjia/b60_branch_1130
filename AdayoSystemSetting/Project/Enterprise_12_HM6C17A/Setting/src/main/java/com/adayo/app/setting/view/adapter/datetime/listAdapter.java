package com.adayo.app.setting.view.adapter.datetime;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adayo.app.setting.R;
import com.adayo.app.base.LogUtil;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;


public class listAdapter extends RecyclerView.Adapter<listAdapter.ViewHolder> {
    private String TAG = "SettingDateTime-listAdapter";
    public static final int LIST_TYPE_YEAR = 1001;
    public static final int LIST_TYPE_MONTH = 1002;
    private Context context;
    private final int minValue;
    private final int maxValue;
    private final int currentValue;
    private final int type;
    private OnItemVisiableListener mOnItemVisiableListener;

    private LayoutInflater layoutInflater;
    private int selPosition;
    private int temp = -1;
    GestureDetector mDetector;

    public void setSelPosition(int selPosition) {
        this.selPosition = selPosition;

    }
    public void notifyItemSelected(RecyclerView recyclerView,int centerPosition, boolean isScroll) {
        Integer oldPosition = null;
        Integer newPosition = null;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            LogUtil.w(TAG,"layoutManager not yet bound");
            return;
        }
        if (isScroll && layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager realLayoutManager = (LinearLayoutManager) layoutManager;
            int firstCompletelyVisibleItemPosition = realLayoutManager.findFirstCompletelyVisibleItemPosition();
            int lastCompletelyVisibleItemPosition = realLayoutManager.findLastCompletelyVisibleItemPosition();
            if (firstCompletelyVisibleItemPosition - 1 <= newPosition && newPosition <= lastCompletelyVisibleItemPosition + 1) {
                recyclerView.scrollToPosition(newPosition);
            } else {
                recyclerView.scrollToPosition(centerPosition);
            }
        }
        if(mOnItemVisiableListener!=null) {
            mOnItemVisiableListener.onItemVisiable(centerPosition);
        }

    }


    public listAdapter(Context context, int minValue, int maxValue, int currentValue, int type) {
        this.context = context;
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = currentValue;
        selPosition = currentValue - minValue;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutInflater = AAOP_HSkin.getLayoutInflater(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list_datetime_year, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String text;
        if (type == LIST_TYPE_MONTH) {
            text = getItem(position).toString();
        } else {
            text = String.valueOf(getItem(position));
        }
        holder.textView.setText(text);
        holder.textView.setTextColor(ContextCompat.getColor(context, R.color.white));

}





    @Override
    public int getItemCount() {
        return maxValue - minValue + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);

        }
    }

    private Object getItem(int index) {
        if (index >= 0 && index < getItemCount()) {
            int value = minValue + index;
            return value;
        }
        return 0;
    }


    public void setOnItemVisiableListener(OnItemVisiableListener onItemVisiableListener) {
        this.mOnItemVisiableListener = onItemVisiableListener;
    }


    public interface OnItemVisiableListener {
        void onItemVisiable(int position);
    }

    public int getCurrentValue() {
        return minValue + selPosition;
    }

}
