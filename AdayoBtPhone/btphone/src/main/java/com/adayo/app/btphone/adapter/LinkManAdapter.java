package com.adayo.app.btphone.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.btphone.R;
import com.adayo.app.btphone.bean.LinkManDataBean;
import com.adayo.app.btphone.constant.Constant;
import com.adayo.app.btphone.manager.BTLinkManManager;
import com.adayo.app.btphone.manager.DialogManager;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinkManAdapter extends RecyclerView.Adapter<LinkManAdapter.ViewHolder> {

    private static final String TAG = Constant.TAG + LinkManAdapter.class.getSimpleName();
    private BTLinkManManager mBTLinkManManager;
    private ContactsMoreNumberAdapter mAdapter;
    private Context mContext;
    private Handler mHandler;

    private String matchString;

    public LinkManAdapter(Context context, BTLinkManManager btLinkManManager, Handler handler){
        mContext = context;
        mBTLinkManManager = btLinkManManager;
        mHandler = handler;
        mAdapter = new ContactsMoreNumberAdapter(mBTLinkManManager,mHandler);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(R.layout.linkman_item_layout,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,final int position) {
        Log.i(TAG,"onBindViewHolder");
        LinkManDataBean people = mList.get(position);
        holder.setData(people);
        if(people.getItemType() == LinkManDataBean.TYPE_CHARACTER){
            holder.itemView.setOnClickListener(null);
        }else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHandler.sendEmptyMessage(0);
                    if(DialogManager.getInstance().callDialogIsShow()){
                        return;
                    }
                    mBTLinkManManager.dialCall(mList.get(position).getPeopleInfo().getNumbers().get(0).getPhoneNumber());
                }
            });
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getRootView().getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;//86;
        holder.itemView.getRootView().setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    List<LinkManDataBean> mList = new ArrayList<>();
    class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout mRootRL;
        TextView mNameTV;
        TextView mPhoneNumberTV;
        ImageView mExpandIV;
        RecyclerView mMoreNumberRV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mRootRL = (RelativeLayout) itemView.findViewById(R.id.root_view_rl);
            mNameTV = (TextView) itemView.findViewById(R.id.name);
            mPhoneNumberTV = (TextView) itemView.findViewById(R.id.phone_number);
            mExpandIV = (ImageView) itemView.findViewById(R.id.expand);
        }

        public void setData(final LinkManDataBean people){
            if(people.getItemType() == LinkManDataBean.TYPE_CHARACTER){
                mNameTV.setTextColor(mContext.getColor(R.color.white_alpha_80));
                mNameTV.setText(people.getItemEn());
                mPhoneNumberTV.setVisibility(View.GONE);
                mExpandIV.setVisibility(View.GONE);
                return;
            }
            mPhoneNumberTV.setVisibility(View.VISIBLE);

            Log.i(TAG,"matchString = "+matchString);
//            if(matchString.equals("")){
                mNameTV.setTextColor(mContext.getColor(R.color.white_alpha_80));
                mNameTV.setText(people.getPeopleInfo().getPersonName());
//            }else{
//                mNameTV.setTextColor(mContext.getColor(R.color.white_alpha_60));
//                SpannableString s = new SpannableString(people.getPeopleInfo().getPersonName());
//                Pattern p = Pattern.compile(matchString);
//                Matcher m = p.matcher(s);
//                while (m.find()){
//                    int start = m.start();
//                    int end = m.end();
//                    s.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.white_alpha_80)),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    mNameTV.setText(s);
//                }
//            }


            mPhoneNumberTV.setText(people.getPeopleInfo().getNumbers().get(0).getPhoneNumber());
            Log.i(TAG,"people.getNumbers().size() = "+people.getPeopleInfo().getNumbers().size());
            if(people.getPeopleInfo().getNumbers().size() > 1){
                mExpandIV.setVisibility(View.VISIBLE);
            }else{
                mExpandIV.setVisibility(View.GONE);
            }
            mExpandIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View rl = itemView.findViewById(R.id.root_view_rl);
                    ViewGroup.LayoutParams layoutParams = rl.getLayoutParams();
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    mMoreNumberRV = (RecyclerView) itemView.findViewById(R.id.more_number_rv);
                    mMoreNumberRV.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    mMoreNumberRV.setAdapter(mAdapter);
                    List<String> numbers = new ArrayList<>();
                    for(int i = 1; i<people.getPeopleInfo().getNumbers().size(); i++){
                        numbers.add(people.getPeopleInfo().getNumbers().get(i).getPhoneNumber());
                    }
                    mAdapter.setContactsInfo(numbers);
                    if(mMoreNumberRV.getVisibility() == View.GONE){
                        mExpandIV.setBackground(mContext.getDrawable(R.drawable.selector_expand_down));
                        layoutParams.height = 92 * people.getPeopleInfo().getNumbers().size();
                        rl.setLayoutParams(layoutParams);
                        mMoreNumberRV.setVisibility(View.VISIBLE);
                    }else{
                        mExpandIV.setBackground(mContext.getDrawable(R.drawable.selector_expand_right));
                        layoutParams.height = 92;
                        itemView.getRootView().setLayoutParams(layoutParams);
                        mMoreNumberRV.setVisibility(View.GONE);
                    }
                }
            });
        }

    }


    public void setContactsInfo(List<LinkManDataBean> list,String matchString){
        this.matchString = matchString;
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

    public String getItemInitialByPosition(int position) {
        String initial = "";
        if(mList != null && getItemCount() > 0 && position >= 0 && position < getItemCount()) {
            initial = mList.get(position).getItemEn();
        }
        return initial;
    }

}
