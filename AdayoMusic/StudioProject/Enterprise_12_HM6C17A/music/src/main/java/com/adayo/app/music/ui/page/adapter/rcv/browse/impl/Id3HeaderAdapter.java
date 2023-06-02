package com.adayo.app.music.ui.page.adapter.rcv.browse.impl;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.adayo.app.music.R;
import com.adayo.app.music.ui.page.adapter.rcv.browse.AbsListAdapter;
import com.adayo.app.music.ui.page.adapter.rcv.browse.AbsViewHolder;
import com.lt.library.util.context.ContextUtil;

import java.util.Objects;

public class Id3HeaderAdapter extends AbsListAdapter<String> {
    public Id3HeaderAdapter() {
        super(new MyDiffCallback());
    }

    @Override
    protected int bindItemLayoutRes(int viewType) {
        return R.layout.item_music_browse_component_header;
    }

    @Override
    protected void onBindItemViewHolder(@NonNull AbsViewHolder viewHolder, @NonNull String dataSource, int position, int viewType) {
        viewHolder.setText(R.id.tv_item_title, TextUtils.isEmpty(dataSource) ? ContextUtil.getAppContext().getString(R.string.lo_music_browse_component_list_album_unk) : dataSource);
        viewHolder.setImageDrawable(R.id.iv_item_ico, R.drawable.icon_return);
    }

    private static class MyDiffCallback extends DiffUtil.ItemCallback<String> {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return true;
        }
    }
}
