package com.adayo.app.setting.base;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.lt.library.base.recyclerview.holder.BaseViewHolder;
import com.lt.library.base.recyclerview.holder.sub.EntityViewHolder;
import com.lt.library.base.recyclerview.holder.sub.ExtrasViewHolder;
import com.lt.library.base.recyclerview.holder.sub.FooterViewHolder;
import com.lt.library.base.recyclerview.holder.sub.HeaderViewHolder;
import com.lt.library.base.recyclerview.holder.sub.StatusViewHolder;
import com.lt.library.base.recyclerview.listener.OnEntityItemClickListener;
import com.lt.library.base.recyclerview.listener.OnEntityItemLongClickListener;
import com.lt.library.base.recyclerview.listener.OnExtrasItemClickListener;
import com.lt.library.base.recyclerview.listener.OnExtrasItemLongClickListener;
import com.lt.library.base.recyclerview.listener.OnFooterItemClickListener;
import com.lt.library.base.recyclerview.listener.OnFooterItemLongClickListener;
import com.lt.library.base.recyclerview.listener.OnHeaderItemClickListener;
import com.lt.library.base.recyclerview.listener.OnHeaderItemLongClickListener;
import com.lt.library.base.recyclerview.listener.OnStatusItemClickListener;
import com.lt.library.base.recyclerview.listener.OnStatusItemLongClickListener;
import com.adayo.app.base.LogUtil;
import com.lt.library.util.context.ContextUtil;

import java.util.ArrayList;
import java.util.List;



public abstract class BaseAdapter<DS> extends RecyclerView.Adapter<BaseViewHolder> {
    private final static String TAG = BaseAdapter.class.getSimpleName();
    private static final int ITEM_TYPE_HEADER = 1001;
    private static final int ITEM_TYPE_STATUS = 1002;
    private static final int ITEM_TYPE_ENTITY = 1003;
    private static final int ITEM_TYPE_EXTRAS = 1004;
    private static final int ITEM_TYPE_FOOTER = 1005;
    private final List<DS> mEntityList;
    private OnHeaderItemClickListener mOnHeaderItemClickListener;
    private OnHeaderItemLongClickListener mOnHeaderItemLongClickListener;
    private OnStatusItemClickListener mOnStatusItemClickListener;
    private OnStatusItemLongClickListener mOnStatusItemLongClickListener;
    private OnEntityItemClickListener mOnEntityItemClickListener;
    private OnEntityItemLongClickListener mOnEntityItemLongClickListener;
    private OnExtrasItemClickListener mOnExtrasItemClickListener;
    private OnExtrasItemLongClickListener mOnExtrasItemLongClickListener;
    private OnFooterItemClickListener mOnFooterItemClickListener;
    private OnFooterItemLongClickListener mOnFooterItemLongClickListener;
    private RecyclerView mRecyclerView;
    private int mHeaderCount, mStatusCount, mExtrasCount, mFooterCount = 0;
    private int mHeaderViewId, mStatusViewId, mExtrasViewId, mFooterViewId = -1;

    public BaseAdapter() {
        this(null);
    }public BaseAdapter(List<DS> entityList) {
        mEntityList = new ArrayList<>();
        if (entityList != null) {
            mEntityList.addAll(entityList);
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        BaseViewHolder viewHolder;
        if (viewType == RecyclerView.INVALID_TYPE) {
            throw new IllegalArgumentException("viewType = " + viewType + ", invalid");
        }
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(mHeaderViewId, viewGroup, false);
                viewHolder = new HeaderViewHolder(view, mOnHeaderItemClickListener, mOnHeaderItemLongClickListener);
                break;
            case ITEM_TYPE_STATUS:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(mStatusViewId, viewGroup, false);
                viewHolder = new StatusViewHolder(view, mOnStatusItemClickListener, mOnStatusItemLongClickListener);
                break;
            case ITEM_TYPE_EXTRAS:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(mExtrasViewId, viewGroup, false);
                viewHolder = new ExtrasViewHolder(view, mOnExtrasItemClickListener, mOnExtrasItemLongClickListener);
                break;
            case ITEM_TYPE_FOOTER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(mFooterViewId, viewGroup, false);
                viewHolder = new FooterViewHolder(view, mOnFooterItemClickListener, mOnFooterItemLongClickListener);
                break;
            case ITEM_TYPE_ENTITY:
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(getEntityLayoutRes(viewType), viewGroup, false);
                viewHolder = new EntityViewHolder(view, mOnEntityItemClickListener, mOnEntityItemLongClickListener);
                break;
        }
        return viewHolder;
    }@Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
        if (viewHolder instanceof HeaderViewHolder) {
            onBindHeaderView((HeaderViewHolder) viewHolder);
        } else if (viewHolder instanceof StatusViewHolder) {
            onBindStatusView((StatusViewHolder) viewHolder);
        } else if (viewHolder instanceof EntityViewHolder) {
            int fixEntityPosition = getFixEntityPosition(position);
            viewHolder.itemView.setTag(fixEntityPosition);
            onBindEntityView((EntityViewHolder) viewHolder, getEntity(fixEntityPosition), fixEntityPosition, viewHolder.getItemViewType());
        } else if (viewHolder instanceof ExtrasViewHolder) {
            onBindExtrasView((ExtrasViewHolder) viewHolder);
        } else if (viewHolder instanceof FooterViewHolder) {
            onBindFooterView((FooterViewHolder) viewHolder);
        }
    }@Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payloads);
        }else {
            if (viewHolder instanceof EntityViewHolder) {
                int fixEntityPosition = getFixEntityPosition(position);
                onBindEntityView((EntityViewHolder) viewHolder, getEntity(fixEntityPosition), fixEntityPosition, viewHolder.getItemViewType(), payloads);
            }
        }}@Override
    public int getItemViewType(int position) {
        int result;
        if (isHeader(position)) {
            result = ITEM_TYPE_HEADER;
        } else if (isStatus(position)) {
            result = ITEM_TYPE_STATUS;
        } else if (isEntity(position)) {
            result = getEntityViewType(getFixEntityPosition(position));
        } else if (isExtras(position)) {
            result = ITEM_TYPE_EXTRAS;
        } else if (isFooter(position)) {
            result = ITEM_TYPE_FOOTER;
        } else {
            result = RecyclerView.INVALID_TYPE;
        }
        return result;
    }@Override
    public long getItemId(int position) {
        long result;
        if (hasStableIds() && isEntity(position)) {
            result = getEntity(getFixEntityPosition(position)).hashCode();
        } else {
            result = RecyclerView.NO_ID;
        }
        return result;
    }@Override
    public int getItemCount() {
        int entityViewCount = getEntityListSize();
        return mHeaderCount + mStatusCount + entityViewCount + mExtrasCount + mFooterCount;
    }@Override
    public void onViewRecycled(@NonNull BaseViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        onRecycledView(viewHolder, viewHolder.getItemViewType());
    }@Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        mergeStGridLayoutManagerFullSpan(viewHolder);}@Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder viewHolder) {
        super.onViewDetachedFromWindow(viewHolder);
    }@Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mergeGridLayoutManagerFullSpan(recyclerView);mRecyclerView = recyclerView;
    }@Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }private boolean isHeader(int position) {
        return mHeaderCount > 0
                && position < mHeaderCount;
    }

    private boolean isStatus(int position) {
        return mStatusCount > 0
                && mHeaderCount <= position
                && position < mHeaderCount + mStatusCount;
    }

    private boolean isEntity(int position) {
        int entityViewCount = getEntityListSize();
        return entityViewCount > 0
                && mHeaderCount + mStatusCount <= position
                && position < mHeaderCount + mStatusCount + entityViewCount;
    }

    private boolean isExtras(int position) {
        int entityViewCount = getEntityListSize();
        return mExtrasCount > 0
                && mHeaderCount + mStatusCount + entityViewCount <= position
                && position < mHeaderCount + mStatusCount + entityViewCount + mExtrasCount;
    }

    private boolean isFooter(int position) {
        int entityViewCount = getEntityListSize();
        return mFooterCount > 0
                && mHeaderCount + mStatusCount + entityViewCount + mExtrasCount <= position;
    }

    private int getFixEntityPosition(int rawPosition) {
        return rawPosition - (mHeaderCount + mStatusCount);
    }

    private int getRawEntityPosition(int fixPosition) {
        return fixPosition + (mHeaderCount + mStatusCount);
    }

    private void mergeStGridLayoutManagerFullSpan(@NonNull BaseViewHolder viewHolder) {
        ViewGroup.LayoutParams viewGroupLayoutParams = viewHolder.itemView.getLayoutParams();
        if (viewGroupLayoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams sgLayoutManagerLayoutParams = (StaggeredGridLayoutManager.LayoutParams) viewGroupLayoutParams;
            if (!isEntity(viewHolder.getLayoutPosition())) {
                sgLayoutManagerLayoutParams.setFullSpan(true);
            }
        }
    }

    private void mergeGridLayoutManagerFullSpan(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager rcvLayoutManager = recyclerView.getLayoutManager();
        if (rcvLayoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) rcvLayoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int spanSize = 1;
                    if (!isEntity(position)) {
                        spanSize = gridLayoutManager.getSpanCount();
                    }
                    return spanSize;
                }
            });
        }
    }

    private boolean clickEntity(int rawEntityPosition) {
        boolean result;
        RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(rawEntityPosition);
        if (viewHolder == null) {
            result = false;
        }else {
           LogUtil.debugD(TAG,"programmatic click execute start");
            viewHolder.itemView.callOnClick();
           LogUtil.debugD(TAG,"programmatic click execute end");
            result = true;
        }return result;
    }

    protected Context getAppContext() {
        return ContextUtil.getInstance().getApplicationContext();
    }

    @IntRange(from = 0)
    protected int getEntityViewType(int position) {
        return ITEM_TYPE_ENTITY;
    }

    @LayoutRes
    protected abstract int getEntityLayoutRes(int viewType);

    protected void onBindHeaderView(HeaderViewHolder viewHolder) {
    }

    protected void onBindStatusView(StatusViewHolder viewHolder) {
    }

    protected void onBindEntityView(EntityViewHolder viewHolder, DS dataSource, int position, int viewType) {
    }

    protected void onBindEntityView(EntityViewHolder viewHolder, DS dataSource, int position, int viewType, List<Object> payloads) {
    }

    protected void onBindExtrasView(ExtrasViewHolder viewHolder) {
    }

    protected void onBindFooterView(FooterViewHolder viewHolder) {
    }

    protected void onRecycledView(BaseViewHolder viewHolder, int viewType) {
    }

    public void setHeader(@LayoutRes int layoutResId) {
        delHeader();
        addHeader(layoutResId);
    }public void delHeader() {
        if (mHeaderViewId == -1 || mHeaderCount == 0) {
            LogUtil.w(TAG,"headerView not added, headerViewId = " + mHeaderViewId + ", headerViewCount = " + mHeaderCount);
            return;
        }
        mHeaderViewId = -1;
        mHeaderCount = 0;
        notifyItemRemoved(0);
    }public void addHeader(@LayoutRes int layoutResId) {
        if (mHeaderViewId != -1 || mHeaderCount != 0) {
            LogUtil.w(TAG,"headerView not deleted, headerViewId = " + mHeaderViewId + ", headerViewCount = " + mHeaderCount);
            return;
        }
        mHeaderViewId = layoutResId;
        mHeaderCount = 1;
        notifyItemInserted(0);
    }public void setStatus(@LayoutRes int layoutResId) {
        delStatus();
        addStatus(layoutResId);
    }public void delStatus() {
        if (mStatusViewId == -1 || mStatusCount == 0) {
            LogUtil.w(TAG,"statusView not added, statusViewId = " + mStatusViewId + ", statusViewCount = " + mStatusCount);
            return;
        }
        mStatusViewId = -1;
        mStatusCount = 0;
        notifyItemRemoved(mHeaderCount);
    }public void addStatus(@LayoutRes int layoutResId) {
        if (mStatusViewId != -1 || mStatusCount != 0) {
            LogUtil.w(TAG,"statusView not deleted, statusViewId = " + mStatusViewId + ", statusViewCount = " + mStatusCount);
            return;
        }
        mStatusViewId = layoutResId;
        mStatusCount = 1;
        notifyItemInserted(mHeaderCount);
    }public void setExtras(@LayoutRes int layoutResId) {
        delExtras();
        addExtras(layoutResId);
    }public void delExtras() {
        if (mExtrasViewId == -1 || mExtrasCount == 0) {
            LogUtil.w(TAG,"extrasView not added, extrasViewId = " + mExtrasViewId + ", extrasViewCount = " + mExtrasCount);
            return;
        }
        mExtrasViewId = -1;
        mExtrasCount = 0;
        int entityViewCount = getEntityListSize();
        notifyItemRemoved(mHeaderCount + mStatusCount + entityViewCount);
    }public void addExtras(@LayoutRes int layoutResId) {
        if (mExtrasViewId != -1 || mExtrasCount != 0) {
            LogUtil.w(TAG,"extrasView not deleted, extrasViewId = " + mExtrasViewId + ", extrasViewCount = " + mExtrasCount);
            return;
        }
        mExtrasViewId = layoutResId;
        mExtrasCount = 1;
        int entityViewCount = getEntityListSize();
        notifyItemInserted(mHeaderCount + mStatusCount + entityViewCount);
    }public void setFooter(@LayoutRes int layoutResId) {
        delFooter();
        addFooter(layoutResId);
    }public void delFooter() {
        if (mFooterViewId == -1 || mFooterCount == 0) {
            LogUtil.w(TAG,"footerView not added, footerViewId = " + mFooterViewId + ", footerViewCount = " + mFooterCount);
            return;
        }
        mFooterViewId = -1;
        mFooterCount = 0;
        int entityViewCount = getEntityListSize();
        notifyItemRemoved(mHeaderCount + mStatusCount + entityViewCount + mExtrasCount + mFooterCount);
    }public void addFooter(@LayoutRes int layoutResId) {
        if (mFooterViewId != -1 || mFooterCount != 0) {
            LogUtil.w(TAG,"footerView not deleted, footerViewId = " + mFooterViewId + ", footerViewCount = " + mFooterCount);
            return;
        }
        mFooterViewId = layoutResId;
        mFooterCount = 1;
        int entityViewCount = getEntityListSize();
        notifyItemInserted(mHeaderCount + mStatusCount + entityViewCount + mExtrasCount + mFooterCount);
    }public void notifyEntitySetChanged(List<DS> dataSourceList) {
        if (!mEntityList.isEmpty()) {
            mEntityList.clear();
        }
        mEntityList.addAll(dataSourceList);
        notifyDataSetChanged();
    }public void notifyEntityRefAll(List<DS> dataSourceList) {
        notifyEntityDelAll();
        notifyEntityAddAll(dataSourceList);
    }public void notifyEntityDelAll() {
        if (mEntityList.isEmpty()) {
            LogUtil.w(TAG,"EntityView not added");
            return;
        }
        int entityViewCount = getEntityListSize();
        mEntityList.clear();
        notifyItemRangeRemoved(mHeaderCount + mStatusCount, entityViewCount);
        notifyItemRangeChanged(mHeaderCount + mStatusCount, entityViewCount);
    }public void notifyEntityAddAll(List<DS> dataSourceList) {
        int entityViewCount = getEntityListSize();
        mEntityList.addAll(dataSourceList);
        notifyItemRangeInserted(mHeaderCount + mStatusCount + entityViewCount, dataSourceList.size());
        notifyItemRangeChanged(mHeaderCount + mStatusCount + entityViewCount, dataSourceList.size());
    }public void notifyEntityRef(DS dataSource, int position) {
        mEntityList.set(position, dataSource);
        notifyItemChanged(mHeaderCount + mStatusCount + position);
    }public void notifyEntityRef(DS dataSource, int position, Object payload) {
        mEntityList.set(position, dataSource);
        notifyItemChanged(mHeaderCount + mStatusCount + position, payload);
    }public void notifyEntityDel(int position) {
        int entityViewCount = getEntityListSize();
        mEntityList.remove(position);
        notifyItemRemoved(mHeaderCount + mStatusCount + position);
        notifyItemRangeChanged(mHeaderCount + mStatusCount + position, entityViewCount - position);
    }public void notifyEntityAdd(DS dataSource, int position) {
        int entityViewCount = getEntityListSize();
        mEntityList.add(position, dataSource);
        notifyItemInserted(mHeaderCount + mStatusCount + position);
        notifyItemRangeChanged(mHeaderCount + mStatusCount + position, entityViewCount - position);
    }public void notifyEntityClick(int position) {
        int rawEntityPosition = getRawEntityPosition(position);
        if (isEntity(rawEntityPosition)) {
            if (!clickEntity(rawEntityPosition)) {
               LogUtil.debugD(TAG,"programmatic click no ready, the layout has not been calculated yet");
                mRecyclerView.scrollToPosition(rawEntityPosition);
                mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                       LogUtil.debugD(TAG,"programmatic click is ready");
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);clickEntity(rawEntityPosition);
                    }});}
        } else {
            LogUtil.w(TAG,"position = " + position + ", out of bounds");
        }
    }public DS getEntity(int position) {
        return mEntityList.get(position);
    }public List<DS> getEntityList() {
        return mEntityList;
    }public int getEntityListSize() {
        return mEntityList.size();
    }public void setOnHeaderItemClickListener(OnHeaderItemClickListener onHeaderItemClickListener) {
        mOnHeaderItemClickListener = onHeaderItemClickListener;
    }

    public void setOnHeaderItemLongClickListener(OnHeaderItemLongClickListener onHeaderItemLongClickListener) {
        mOnHeaderItemLongClickListener = onHeaderItemLongClickListener;
    }

    public void setOnStatusItemClickListener(OnStatusItemClickListener onStatusItemClickListener) {
        mOnStatusItemClickListener = onStatusItemClickListener;
    }

    public void setOnStatusItemLongClickListener(OnStatusItemLongClickListener onStatusItemLongClickListener) {
        mOnStatusItemLongClickListener = onStatusItemLongClickListener;
    }

    public void setOnEntityItemClickListener(OnEntityItemClickListener onEntityItemClickListener) {
        mOnEntityItemClickListener = onEntityItemClickListener;
    }

    public void setOnEntityItemLongClickListener(OnEntityItemLongClickListener onEntityItemLongClickListener) {
        mOnEntityItemLongClickListener = onEntityItemLongClickListener;
    }

    public void setOnExtrasItemClickListener(OnExtrasItemClickListener onExtrasItemClickListener) {
        mOnExtrasItemClickListener = onExtrasItemClickListener;
    }

    public void setOnExtrasItemLongClickListener(OnExtrasItemLongClickListener onExtrasItemLongClickListener) {
        mOnExtrasItemLongClickListener = onExtrasItemLongClickListener;
    }

    public void setOnFooterItemClickListener(OnFooterItemClickListener onFooterItemClickListener) {
        mOnFooterItemClickListener = onFooterItemClickListener;
    }

    public void setOnFooterItemLongClickListener(OnFooterItemLongClickListener onFooterItemLongClickListener) {
        mOnFooterItemLongClickListener = onFooterItemLongClickListener;
    }
}
