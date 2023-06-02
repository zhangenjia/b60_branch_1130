package com.adayo.app.radio.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.app.radio.R;
import com.adayo.app.radio.ui.bean.RadioBean;
import com.adayo.app.radio.utils.BlurTransitionView;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.List;

/**
 * @author ADAYO-06
 */
public class NewRadioSearchListAdapter extends RecyclerView.Adapter<NewRadioSearchListAdapter.ViewHolder> {
    private final static String TAG = NewRadioSearchListAdapter.class.getSimpleName();
    private Context mContext;
    private List<RadioBean> mSearchList;
    private String playStatue;
    AnimationDrawable animationDrawable;

    private int selectItem = -1;


    public void setPresetLists(List<RadioBean> presetLists, String band) {
        this.mSearchList = presetLists;
    }

    public NewRadioSearchListAdapter(Context context, List<RadioBean> data) {
        this.mContext = context;
        this.mSearchList = data;
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = AAOP_HSkin.getLayoutInflater(mContext).from(mContext).inflate(R.layout.item_radios, parent, false);
        return new ViewHolder(view);
    }

    private int maxItemNum = 8;
    private String point = ".";
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
        RadioBean radioBean = mSearchList.get(i);
        if (i<=maxItemNum){
            viewHolder.numTv.setText("0"+(i + 1) + "" + ".");
        }else {
            viewHolder.numTv.setText((i + 1) + "" + ".");
        }
        viewHolder.radioTv.setText(radioBean.getFreq());

        if (radioBean.getFreq().contains(point)) {
            /**
             * FM
             */
            viewHolder.unitTv.setText(R.string.tv_list_mhz);
        } else {
            viewHolder.unitTv.setText(R.string.tv_list_khz);
        }

        if (clickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(i);
                }
            });
        }

        if (collectListener != null) {
            viewHolder.collectIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    collectListener.onCollect(i);
                }
            });
        }

        if (radioBean.getIsCollect()) {
            AAOP_HSkin
                    .with(viewHolder.collectIv)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.icon_collectionsmall_n)
                    .applySkin(false);
        } else {
            AAOP_HSkin
                    .with(viewHolder.collectIv)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.icon_collectionsmall2_p)
                    .applySkin(false);
        }
        animationDrawable = (AnimationDrawable) viewHolder.playIv.getDrawable();
        if (radioBean.getIsPlay()) {
            viewHolder.playIv.setVisibility(View.VISIBLE);
            AAOP_HSkin
                    .with(viewHolder.btBg)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.button_sel)
                    .applySkin(false);

            animationDrawable.start();
        } else {
            viewHolder.playIv.setVisibility(View.GONE);
            AAOP_HSkin
                    .with(viewHolder.btBg)
                    .addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND,R.drawable.button_bg)
                    .applySkin(false);
            animationDrawable.stop();
        }

        if(shouldShowBlur) {
            viewHolder.blurTransitionView.show(300);
        }else {
            viewHolder.blurTransitionView.hide(0);
        }
    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
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
        ImageView collectIv, playIv;
        View btBg;
        public BlurTransitionView blurTransitionView;


        public ViewHolder(View itemView) {
            super(itemView);
            numTv = (TextView) itemView.findViewById(R.id.tv_radio_num);
            radioTv = (TextView) itemView.findViewById(R.id.tv_radio);
            unitTv = (TextView) itemView.findViewById(R.id.tv_radio_unit);
            collectIv = (ImageView) itemView.findViewById(R.id.iv_collection_status);
            playIv = (ImageView) itemView.findViewById(R.id.iv_play_status);
            btBg = (View) itemView.findViewById(R.id.search_item_bg);
            blurTransitionView = (BlurTransitionView) itemView.findViewById(R.id.blurIcon);
        }
    }

    public interface OnItemClickListener {
        /**
         * 点击方法
         *
         * @param position 点击的item值
         */
        void onClick(int position);
    }


    private OnItemClickListener clickListener;


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }


    public interface OnItemCollectClickListener {
        /**
         * 收藏方法
         *
         * @param position 收藏的item值
         */
        void onCollect(int position);
    }


    private OnItemCollectClickListener collectListener;


    public void setOnItemCollectClickListener(OnItemCollectClickListener collectListener) {
        this.collectListener = collectListener;
    }


}
