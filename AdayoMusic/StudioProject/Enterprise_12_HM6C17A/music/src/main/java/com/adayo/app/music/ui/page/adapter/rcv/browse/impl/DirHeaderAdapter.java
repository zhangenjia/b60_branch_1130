package com.adayo.app.music.ui.page.adapter.rcv.browse.impl;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.adayo.app.music.R;
import com.adayo.app.music.ui.page.adapter.rcv.browse.AbsListAdapter;
import com.adayo.app.music.ui.page.adapter.rcv.browse.AbsViewHolder;

import java.util.Objects;

public class DirHeaderAdapter extends AbsListAdapter<Pair<String, String>> {//first: 当前文件夹的名称, second: 父级文件夹的路径

    public DirHeaderAdapter() {
        super(new MyDiffCallback());
    }

    @Override
    protected int bindItemLayoutRes(int viewType) {
        return R.layout.item_music_browse_component_header;
    }

    @Override
    protected void onBindItemViewHolder(@NonNull AbsViewHolder viewHolder, @NonNull Pair<String, String> dataSource, int position, int viewType) {
        viewHolder.setText(R.id.tv_item_title, dataSource.first);
        viewHolder.setImageDrawable(R.id.iv_item_ico, R.drawable.icon_return);
    }

    private static class MyDiffCallback extends DiffUtil.ItemCallback<Pair<String, String>> {
        @Override
        public boolean areItemsTheSame(@NonNull Pair<String, String> oldItem, @NonNull Pair<String, String> newItem) {
            return Objects.equals(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Pair<String, String> oldItem, @NonNull Pair<String, String> newItem) {
            return true;
        }
    }
}
