package com.adayo.app.music.ui.page.adapter.rcv.browse.impl;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.app.music.R;
import com.adayo.app.music.ui.page.adapter.rcv.browse.AbsListAdapter;
import com.adayo.app.music.ui.page.adapter.rcv.browse.AbsViewHolder;
import com.adayo.proxy.media.bean.FileInfo;
import com.lt.library.util.LogUtil;
import com.lt.library.util.context.ContextUtil;

import java.util.List;
import java.util.Objects;

public class Id3EntityAdapter extends AbsListAdapter<Object> {
    private String mSelectedFilePath;

    public Id3EntityAdapter() {
        super(new MyDiffCallback());
    }

    @Override
    protected int bindItemLayoutRes(int viewType) {
        return R.layout.item_music_browse_component_entity;
    }

    @Override
    protected void onBindItemViewHolder(@NonNull AbsViewHolder viewHolder, @NonNull Object dataSource, int position, int viewType) {
        viewHolder.setVisibility(R.id.iv_item_ico, View.VISIBLE);
        viewHolder.setImageDrawable(R.id.iv_item_ico, R.drawable.icon_category_music);
        if (dataSource instanceof String) {
            String id3Name = (String) dataSource;
            viewHolder.setText(R.id.tv_item_title, TextUtils.isEmpty(id3Name) ? ContextUtil.getAppContext().getString(R.string.lo_music_browse_component_list_album_unk) : id3Name);
            selectItem(viewHolder, false);
        } else if (dataSource instanceof FileInfo) {
            FileInfo file = (FileInfo) dataSource;
            viewHolder.setText(R.id.tv_item_title, ++position + ". " + file.getNodeInfo().getNodeName());
            selectItem(viewHolder, Objects.equals(file.getNodeInfo().getNodePath(), mSelectedFilePath));
        }
    }

    @Override
    protected void onBindItemViewHolder(@NonNull AbsViewHolder viewHolder, @NonNull Object dataSource, int position, int viewType, @NonNull List<Object> payloads) {
        super.onBindItemViewHolder(viewHolder, dataSource, position, viewType, payloads);
        Object payload = payloads.get(payloads.size() - 1);
        if (!(payload instanceof Bundle)) {
            throw new IllegalArgumentException("please use Bundle to store data");
        }
        Bundle args = (Bundle) payload;
        if (args.containsKey(PAYLOAD_ITEM_SELECT)) {
            selectItem(viewHolder, args.getBoolean(PAYLOAD_ITEM_SELECT));
        }
    }

    private void selectItem(@NonNull AbsViewHolder viewHolder, boolean isSelect) {
        viewHolder.getView(R.id.csl_item_root).setSelected(isSelect);
        TextView tvTitle = viewHolder.getView(R.id.tv_item_title, TextView.class);
        tvTitle.setSelected(isSelect);
        tvTitle.setFocusable(isSelect);
        tvTitle.setFocusableInTouchMode(isSelect);
        tvTitle.setEllipsize(isSelect ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.MIDDLE);
    }

    public void notifyItemSelected(String filePath, RecyclerView recyclerView, boolean isScroll, int scrollOffset) {
        if (mSelectedFilePath != null && Objects.equals(filePath, mSelectedFilePath)) {
            LogUtil.d("target item has been selected");
            return;
        }
        List<Object> dataSources = getCurrentList();
        if (dataSources.isEmpty()) {
            mSelectedFilePath = filePath;
            LogUtil.w("dataSources not submitted");
            return;
        }
        int dataSourcesSize = dataSources.size();
        Integer oldPosition = null;
        Integer newPosition = null;
        for (int i = 0; i < dataSourcesSize; i++) {
            Object obj = dataSources.get(i);
            if (!(obj instanceof FileInfo)) {
                continue;
            }
            FileInfo fileInfo = (FileInfo) obj;
            if (Objects.equals(fileInfo.getNodeInfo().getNodePath(), filePath)) {
                newPosition = i;
                break;
            }
        }
        if (newPosition == null) {
            LogUtil.w("dataSources not exist filePath: " + filePath);
            return;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            LogUtil.w("layoutManager not yet bound");
            return;
        }
        if (isScroll && layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager realLayoutManager = (LinearLayoutManager) layoutManager;
            int firstCompletelyVisibleItemPosition = realLayoutManager.findFirstCompletelyVisibleItemPosition();
            int lastCompletelyVisibleItemPosition = realLayoutManager.findLastCompletelyVisibleItemPosition();
            if (firstCompletelyVisibleItemPosition - 1 <= newPosition && newPosition <= lastCompletelyVisibleItemPosition + 1) {
                recyclerView.scrollToPosition(newPosition + scrollOffset);
            } else {
                recyclerView.scrollToPosition(newPosition + scrollOffset);
            }
        }
        if (mSelectedFilePath != null) {
            for (int i = 0; i < dataSourcesSize; i++) {
                Object obj = dataSources.get(i);
                if (!(obj instanceof FileInfo)) {
                    continue;
                }
                FileInfo fileInfo = (FileInfo) obj;
                if (Objects.equals(fileInfo.getNodeInfo().getNodePath(), mSelectedFilePath)) {
                    oldPosition = i;
                    break;
                }
            }
            if (oldPosition != null) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(PAYLOAD_ITEM_SELECT, false);
                notifyItemChanged(oldPosition, bundle);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(PAYLOAD_ITEM_SELECT, true);
        notifyItemChanged(newPosition, bundle);
        mSelectedFilePath = filePath;
    }

    public String getSelectedFilePath() {
        return mSelectedFilePath;
    }

    private static class MyDiffCallback extends DiffUtil.ItemCallback<Object> {
        @Override
        public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            if (oldItem instanceof String && newItem instanceof String) {
                return Objects.equals(oldItem, newItem);
            } else if (oldItem instanceof FileInfo && newItem instanceof FileInfo) {
                FileInfo oldFile = (FileInfo) oldItem;
                FileInfo newFile = (FileInfo) newItem;
                return Objects.equals(oldFile.getNodeInfo().getNodePath(), newFile.getNodeInfo().getNodePath());
            } else {
                return false;
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            return true;
        }
    }
}
