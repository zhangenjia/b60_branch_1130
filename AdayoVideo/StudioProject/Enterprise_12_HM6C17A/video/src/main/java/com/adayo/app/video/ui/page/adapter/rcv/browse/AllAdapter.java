package com.adayo.app.video.ui.page.adapter.rcv.browse;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.app.video.R;
import com.adayo.proxy.media.bean.FileInfo;
import com.lt.library.util.LogUtil;

import java.util.List;
import java.util.Objects;

public class AllAdapter extends AbsListAdapter<FileInfo> {
    private String mSelectedFilePath;

    public AllAdapter() {
        super(new MyDiffCallback());
    }

    @Override
    protected int bindItemLayoutRes(int viewType) {
        return R.layout.item_video_browse_entity;
    }

    @Override
    protected void onBindItemViewHolder(@NonNull AbsViewHolder viewHolder, @NonNull FileInfo dataSource, int position, int viewType) {
        viewHolder.setVisibility(R.id.iv_item_ico, View.GONE);
        viewHolder.setImageDrawable(R.id.iv_item_ico, null);
        viewHolder.setText(R.id.tv_item_title, ++position + ". " + dataSource.getNodeInfo().getNodeName());
        selectItem(viewHolder, Objects.equals(dataSource.getNodeInfo().getNodePath(), mSelectedFilePath));
    }

    @Override
    protected void onBindItemViewHolder(@NonNull AbsViewHolder viewHolder, @NonNull FileInfo dataSource, int position, int viewType, @NonNull List<Object> payloads) {
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

    public void notifyItemSelected(String filePath, RecyclerView recyclerView, boolean isScroll) {
        if (mSelectedFilePath != null && Objects.equals(filePath, mSelectedFilePath)) {
            LogUtil.d("target item has been selected");
            return;
        }
        List<FileInfo> dataSources = getCurrentList();
        int dataSourcesSize = dataSources.size();
        Integer oldPosition = null;
        Integer newPosition = null;
        for (int i = 0; i < dataSourcesSize; i++) {
            FileInfo fileInfo = dataSources.get(i);
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
                recyclerView.scrollToPosition(newPosition);
            } else {
                recyclerView.scrollToPosition(newPosition);
            }
        }
        if (mSelectedFilePath != null) {
            for (int i = 0; i < dataSourcesSize; i++) {
                FileInfo fileInfo = dataSources.get(i);
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

    private void selectItem(@NonNull AbsViewHolder viewHolder, boolean isSelect) {
        viewHolder.getView(R.id.csl_item_root).setSelected(isSelect);
        TextView tvTitle = viewHolder.getView(R.id.tv_item_title, TextView.class);
        tvTitle.setSelected(isSelect);
        tvTitle.setFocusable(isSelect);
        tvTitle.setFocusableInTouchMode(isSelect);
        tvTitle.setEllipsize(isSelect ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.MIDDLE);
    }

    private static class MyDiffCallback extends DiffUtil.ItemCallback<FileInfo> {
        @Override
        public boolean areItemsTheSame(@NonNull FileInfo oldItem, @NonNull FileInfo newItem) {
            return Objects.equals(oldItem.getNodeInfo().getNodePath(), newItem.getNodeInfo().getNodePath());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FileInfo oldItem, @NonNull FileInfo newItem) {
            return true;
        }
    }
}
