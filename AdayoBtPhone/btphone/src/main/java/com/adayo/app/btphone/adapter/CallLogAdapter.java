package com.adayo.app.btphone.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTCallLogManager;
import com.adayo.app.btphone.manager.DialogManager;
import com.adayo.app.btphone.utils.TimeUtils;
import com.adayo.common.bluetooth.bean.PeopleInfo;
import com.adayo.common.bluetooth.constant.BtDef;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {


    private static final String TAG = Constant.TAG + CallLogAdapter.class.getSimpleName();
    private Context mContext;
    private BTCallLogManager mBTCallLogManager;

    public CallLogAdapter(Context context,BTCallLogManager btCallLogManager){
        mContext = context;
        mBTCallLogManager = btCallLogManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.calllog_item_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i(TAG,"onBindViewHolder");
        PeopleInfo people = mList.get(position);
        holder.setData(people);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DialogManager.getInstance().callDialogIsShow()){
                    return;
                }
                mBTCallLogManager.dialCall(mList.get(position).getNumber());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    List<PeopleInfo> mList = new ArrayList<>();
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mPhoneStateIV;
        TextView mPhoneNumberTV;
        TextView mTimeTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPhoneStateIV = (ImageView) itemView.findViewById(R.id.phone_state);
            mPhoneNumberTV = (TextView) itemView.findViewById(R.id.phone_number);
            mTimeTV = (TextView) itemView.findViewById(R.id.time);
        }

        public void setData(PeopleInfo people){
            switch (people.getType()) {
                case BtDef.PBAP_STORAGE_MISSED_CALLS://未接听
                    mPhoneStateIV.setImageDrawable(mContext.getDrawable(R.drawable.icon_missed));
                    break;
                case BtDef.PBAP_STORAGE_RECEIVED_CALLS://来电
                    mPhoneStateIV.setImageDrawable(mContext.getDrawable(R.drawable.icon_answer));
                    break;
                case BtDef.PBAP_STORAGE_DIALED_CALLS://去电
                    mPhoneStateIV.setImageDrawable(mContext.getDrawable(R.drawable.icon_pullout));
                    break;
                default:
                    break;
            }
            mPhoneNumberTV.setText(getLimitSubstring(people.getPersonName()));
            if(TimeUtils.isYesterday(people.getCallDataTime())){
                mTimeTV.setText(mContext.getString(R.string.yesterday));
            }else{
                mTimeTV.setText(TimeUtils.getCallTime(people.getCallDataTime()));
            }
        }

    }


    public void setCallLogInfo(List<PeopleInfo> list){

        if(null != mList){
            mList.clear();
            mList.addAll(list);
        }

        if(mList.size() != 0){
            notifyDataSetChanged();
        }
    }

    public void playLayoutAnimation(RecyclerView recycleView){
        Context context = recycleView.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context,R.anim.animation_layoutanimation);
        recycleView.setLayoutAnimation(controller);
        recycleView.scheduleLayoutAnimation();
    }

    private String getLimitSubstring(String inputStr) {
        int orignLen = inputStr.length();
        int resultLen = 0;
        String temp = null;
        for (int i = 0; i < orignLen; i++) {
            temp = inputStr.substring(i, i + 1);
            try {// 3 bytes to indicate chinese word,1 byte to indicate english
                // word ,in utf-8 encode
                if (temp.getBytes("utf-8").length == 3) {
                    resultLen += 2;
                } else {
                    resultLen++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (resultLen > 36) {
                return inputStr.substring(0, i)+"…";
            }
        }
        return inputStr;
    }
}
