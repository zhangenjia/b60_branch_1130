package com.adayo.app.music.ui.page.adapter.rcv.browse;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;

import com.adayo.app.music.ui.page.adapter.rcv.browse.base.BaseListAdapter;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

/**
 * @Auth: LinTan
 * @Date: 2021/11/19 15:17
 * @Desc: 封装业务相关的RecyclerView.Adapter基类(e.g., 换肤), 及设置默认DiffUtil.ItemCallback及ViewHolder
 */

public abstract class AbsListAdapter<DS> extends BaseListAdapter<DS, AbsViewHolder> {
    protected static final String PAYLOAD_ITEM_SELECT = "item_select";

    public AbsListAdapter(DiffUtil.ItemCallback<DS> itemCallback) {
        super(itemCallback);
    }

    @Override
    protected AbsViewHolder onCreateItemViewHolder(Context context, ViewGroup viewGroup, int viewType, int layoutRes) {
        View view = AAOP_HSkin.getLayoutInflater(viewGroup.getContext()).inflate(layoutRes, viewGroup, false);
        return new AbsViewHolder(view);
    }
}
