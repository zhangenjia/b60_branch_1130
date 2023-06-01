package com.adayo.app.btphone.adapter;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTLinkManManager;
import com.adayo.app.btphone.manager.DialogManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsMoreNumberAdapter extends RecyclerView.Adapter<ContactsMoreNumberAdapter.ViewHolder> {


    private static final String TAG = Constant.TAG + ContactsMoreNumberAdapter.class.getSimpleName();
    private BTLinkManManager mBTLinkManManager;

    private Handler mHandler;

    public ContactsMoreNumberAdapter(BTLinkManManager btLinkManManager, Handler handler){
        mBTLinkManManager = btLinkManManager;
        mHandler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.linkman_more_number_item_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
        Log.i(TAG,"onBindViewHolder");
        final String number = mList.get(position);
        holder.setData(number);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessage(0);
                if(DialogManager.getInstance().callDialogIsShow()){
                    return;
                }
                mBTLinkManManager.dialCall(number);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    List<String> mList = new ArrayList<>();
    class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout mRootRL;
        TextView mPhoneNumberTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootRL = (RelativeLayout) itemView.findViewById(R.id.root_view_rl);
            mPhoneNumberTV = (TextView) itemView.findViewById(R.id.phone_number);
        }

        public void setData(String number){
            mPhoneNumberTV.setText(number);
        }

    }


    public void setContactsInfo(List<String> list){
        if(null != mList){
            mList.clear();
            mList.addAll(list);
        }

        if(mList.size() != 0){
            notifyDataSetChanged();
        }
    }
}
