package com.adayo.app.btphone.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTDialManager;
import com.adayo.app.btphone.manager.DialogManager;
import com.adayo.common.bluetooth.bean.PeopleInfo;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class KeyboardQueryResultAdapter extends RecyclerView.Adapter<KeyboardQueryResultAdapter.ViewHolder> {


    private static final String TAG = Constant.TAG + KeyboardQueryResultAdapter.class.getSimpleName();
    private Context mContext;
    private BTDialManager mBTDialMgr;

    public KeyboardQueryResultAdapter(Context context,BTDialManager btDialManager){
        mContext = context;
        mBTDialMgr = btDialManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.keyboard_query_item_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i(TAG,"onBindViewHolder");
        if(position >= mList.size()){
            Log.e(TAG,"error position = "+position+" mList.size() = "+mList.size());
            return;
        }
        PeopleInfo people = mList.get(position);
        holder.setData(people);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bug983822,mList.size为0的具体原因不明，暂时先屏蔽处理
                if(mList.size() == 0){
                    Log.e(TAG,"mList.size() = 0");
                    return;
                }
                mBTDialMgr.dialCall(mList.get(position).getNumber());
                DialogManager.getInstance().dismissKeyboardDialog();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    List<PeopleInfo> mList = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView mNameTV;
        TextView mNumberTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTV = (TextView) itemView.findViewById(R.id.name);
            mNumberTV = (TextView) itemView.findViewById(R.id.phone_number);
        }

        public void setData(PeopleInfo people){
            mNameTV.setText(people.getPersonName());
            mNumberTV.setText(people.getNumber());
        }

    }


    public void setQueryResultInfo(List<PeopleInfo> list){
        Log.i(TAG,"setData list.size = "+list.size());

        if(null != mList && list.size() > 0){
            mList.clear();
            mList.addAll(list);
            if(mList.size() != 0){
                Log.i(TAG,"notifyDataSetChanged");
                notifyDataSetChanged();
            }
        }
    }

    public void clearData(){
        if(null != mList){
            mList.clear();
            notifyDataSetChanged();
        }
    }
}
