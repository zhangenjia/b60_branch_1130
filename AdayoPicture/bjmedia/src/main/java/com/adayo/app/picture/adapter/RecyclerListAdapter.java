package com.adayo.app.picture.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.app.picture.R;
import com.adayo.app.picture.ui.base.LogUtil;
import com.adayo.common.picture.bean.ListItem;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.MyViewHolder> {

    private static final String TAG = "Picture" + "RecyclerListAdapter";
    private List<ListItem> mPhotoData = null;
    private int parentHeight;

    private Context context;
    private ItemClickListener mItemClickListener = null;
    private int mPosition = -1;

    public RecyclerListAdapter(Context context,int position) {
        this.context = context;
        this.mPosition=position;
    }

    public void setPhotoData(List<ListItem> list) {
        this.mPhotoData = list;
        notifyDataSetChanged();
    }


    public void setSelected(int pos) {
        mPosition =pos;
        LogUtil.d(TAG, "mPosition: " + mPosition);
     // notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.d(TAG, "onCreateViewHolder: " + viewType);
        View view = AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.photo_video_list_item, parent, false);
        parentHeight = parent.getWidth();
        return new MyViewHolder(view);
    }


    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (null != mPhotoData) {
            String url = mPhotoData.get(position).getFilePath();
            if (null == holder || null == url || "".equals(url)) {
                LogUtil.w(TAG, "onBindViewHolder: Photo null");
                return;
            }

            if (mPhotoData.get(position).getType() == ListItem.IS_FILE) {
                LogUtil.d(TAG, "onBindViewHolder: Photo 1");
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(mPhotoData.get(position).getName());
                Drawable drawable = AAOP_HSkin.getInstance()
                        .getResourceManager()
                        .getDrawable(R.drawable.icon_image_failed_268px);
                Glide.with(context).load(mPhotoData.get(position)
                        .getFilePath())
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(drawable)
                        .into(holder.icon);

            } else if (!TextUtils.isEmpty(mPhotoData.get(position).getThumUrl())) {
                LogUtil.d(TAG, "onBindViewHolder: Photo 2");
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(mPhotoData.get(position).getName() + "--" + mPhotoData.get(position).getHasFileNum());
                Drawable drawable = AAOP_HSkin.getInstance()
                        .getResourceManager()
                        .getDrawable(R.drawable.icon_image_failed_268px);
                Glide.with(context).load(mPhotoData.get(position).getThumUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter()
                        .error(drawable)
                        .into(holder.icon);
            } else {
                LogUtil.d(TAG, "onBindViewHolder: Photo 3");
                AAOP_HSkin.with( holder.icon)
                        .addViewAttrs(AAOP_HSkin.ATTR_SRC,R.drawable.pic_list_file)
                        .applySkin(false);
                holder.title.setVisibility(View.VISIBLE);
                holder.title.setText(mPhotoData.get(position).getName());
            }
            if (mPosition == position && mPhotoData.get(position).getType() == ListItem.IS_FILE) {
                holder.background.setVisibility(View.VISIBLE);
            } else {
                holder.background.setVisibility(View.GONE);
            }

            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPosition = position;
                    mItemClickListener.onItemClick(holder.rl_item, position);
                }
            });
        }
            if (mPosition == position) {
                holder.background.setVisibility(View.VISIBLE);
            } else {
                holder.background.setVisibility(View.GONE);
            }

        }


    @Override
    public void onViewRecycled(MyViewHolder holder) {
        if (holder != null) {
            Glide.clear(holder.icon);
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        if (null != mPhotoData) {
            return mPhotoData.size();
        }
        return 0;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon, item_play, background;
        TextView title;
        RelativeLayout  rl_include;
        CardView rl_item;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.photo_list_img_id);
            title = (TextView) itemView.findViewById(R.id.photo_list_text_id);
            item_play = (ImageView) itemView.findViewById(R.id.item_play);
            rl_item = (CardView) itemView.findViewById(R.id.rl_item);
            rl_include = (RelativeLayout) itemView.findViewById(R.id.rl_include);
            background = (ImageView) itemView.findViewById(R.id.image_background);
        }
    }
}
