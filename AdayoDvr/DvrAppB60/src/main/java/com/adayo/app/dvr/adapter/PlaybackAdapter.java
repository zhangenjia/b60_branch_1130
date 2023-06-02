package com.adayo.app.dvr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.entity.PlaybackEntity;

import java.util.List;

public class PlaybackAdapter extends BaseAdapter {
    private static final String TAG = "PlaybackAdapter";
    private Context mContext;
    private List<PlaybackEntity> mList;
    private boolean isPlay = false;

    public PlaybackAdapter(Context context, List<PlaybackEntity> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PlaybackAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_playback, parent, false);
            viewHolder = new PlaybackAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PlaybackAdapter.ViewHolder) convertView.getTag();
        }
        if (isPlay) {

            viewHolder.ivPlay.setVisibility(View.VISIBLE);

        } else {
            viewHolder.ivPlay.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setPlayStatus(boolean isPlaying) {
        this.isPlay = isPlaying;

        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView ivPlay;

        public ViewHolder(View itemView) {
            ivPlay = itemView.findViewById(R.id.iv_play);
        }
    }

}
