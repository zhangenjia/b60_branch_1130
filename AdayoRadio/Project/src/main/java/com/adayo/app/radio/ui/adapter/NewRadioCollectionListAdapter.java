package com.adayo.app.radio.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.app.radio.R;
import com.adayo.app.radio.database.User;
import com.adayo.app.radio.database.UserDatabase;
import com.adayo.app.radio.ui.bean.RadioBean;
import com.adayo.app.radio.ui.controller.RadioDataMng;
import com.adayo.app.radio.utils.BlurTransitionView;
import com.adayo.app.radio.utils.MyItemTouchHandler;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.Collections;
import java.util.List;

/**
 * @author ADAYO jwqi
 */
public class NewRadioCollectionListAdapter extends MyItemTouchHandler.BaseItemTouchAdapterImpl {
    private final static String TAG = NewRadioCollectionListAdapter.class.getSimpleName();
    private Context mContext;
    private List<RadioBean> mCollectList;
    private String playStatue;

    private int selectItem = -1;


    public void setPresetLists(List<RadioBean> presetLists, String band) {
        this.mCollectList = presetLists;
    }

    public NewRadioCollectionListAdapter(Context context, List<RadioBean> data) {
        this.mContext = context;
        this.mCollectList = data;
    }

    public String getPlayStatue() {
        return playStatue;
    }

    public void setPlayStatue(String playStatue) {
        this.playStatue = playStatue;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int i) {
        View view = AAOP_HSkin.getLayoutInflater(mContext).from(mContext).inflate(R.layout.item_collections, parent, false);
        return new ViewHolder(view);
    }

    private int maxItemNum = 8;
    private String point = ".";
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int i) {
        if (holder instanceof ViewHolder) {
            RadioBean radioBean = mCollectList.get(i);
            Log.d(TAG, "onBindViewHolder: mCollectList = "+mCollectList);
            if (i<=maxItemNum){
                ((ViewHolder) holder).numTv.setText("0"+(i + 1) + "" + ".");
            }else {
                ((ViewHolder) holder).numTv.setText((i + 1) + "" + ".");
            }

            ((ViewHolder) holder).radioTv.setText(radioBean.getFreq());

            if (radioBean.getFreq().contains(point)) {
                /**
                 * FM
                 */
                ((ViewHolder) holder).unitTv.setText(R.string.tv_list_mhz);
            } else {
                ((ViewHolder) holder).unitTv.setText(R.string.tv_list_khz);
            }

            if(radioBean.getIsPlay()){
                AAOP_HSkin
                        .with(((ViewHolder) holder).collectionItemBg)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.button_sel)
                        .applySkin(false);
            }else{
                AAOP_HSkin
                        .with(((ViewHolder) holder).collectionItemBg)
                        .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.button_bg)
                        .applySkin(false);
            }

            if (removeListener != null) {
                ((ViewHolder) holder).closeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeListener.onRemove(i);
                    }
                });
            }

            if (listener != null) {
                ((ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onClick(i);
                    }
                });
            }

            ((ViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    RadioDataMng.getmInstance().isDeleteItem = false;
                    hideBlur();
                    return false;
                }
            });
        }

        if(shouldShowBlur) {
            ((ViewHolder) holder).blurTransitionView.show(300);
        }else {
            ((ViewHolder) holder).blurTransitionView.hide(0);
        }

    }


    @Override
    public int getItemCount() {
        return mCollectList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "onItemMove: " + fromPosition + toPosition);
        if (fromPosition != mCollectList.size()) {
            //移动时交换位置
            Collections.swap(mCollectList, fromPosition, toPosition);
//            setFlag("1");
            UserDatabase
                    .getInstance(mContext)
                    .getUserDao()
                    .deleteAll();
            for (int i = 0; i < mCollectList.size(); i++) {
                User user = new User();
                user.setName(mCollectList.get(i).getFreq());
                UserDatabase
                        .getInstance(mContext)
                        .getUserDao()
                        .insert(user);
            }
        }
    }

    @Override
    public void onItemRemove(int position) {
        if (mCollectList.size() > 0){

        }
    }

    boolean shouldShowBlur = true;
    public void showBlur() {
        shouldShowBlur = true;
        notifyDataSetChanged();
    }

    public void hideBlur() {
        shouldShowBlur = false;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView numTv, radioTv, unitTv;
        ImageView closeIv;
        public View collectionItemBg;
        public BlurTransitionView blurTransitionView;


        public ViewHolder(View itemView) {
            super(itemView);
            numTv = (TextView) itemView.findViewById(R.id.tv_number_collection);
            radioTv = (TextView) itemView.findViewById(R.id.tv_radio);
            unitTv = (TextView) itemView.findViewById(R.id.tv_radio_unit);
            closeIv = (ImageView) itemView.findViewById(R.id.iv_close);
            collectionItemBg = (View)itemView.findViewById(R.id.collection_item_bg);
            blurTransitionView = (BlurTransitionView) itemView.findViewById(R.id.collection_blurIcon);

        }
    }


    public interface OnItemRemoveListener {
        /**
         * item Remove
         *
         * @param position remove item
         */
        void onRemove(int position);
    }

    public interface OnItemClickListener {
        /**
         * item 点击监听
         *
         * @param position 点击Item值
         */
        void onClick(int position);
    }


    private OnItemRemoveListener removeListener;
    private OnItemClickListener listener;


    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        this.removeListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
