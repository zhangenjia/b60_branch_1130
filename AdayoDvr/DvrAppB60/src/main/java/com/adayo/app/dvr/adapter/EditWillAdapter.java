package com.adayo.app.dvr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.entity.EditEntity;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.List;

public class EditWillAdapter extends BaseAdapter {

    private static final String TAG = "EditWillAdapter";
    private Context mContext;
    private List<EditEntity> mList;
    private boolean mEditMode = false;
    private boolean mEditPlay = false;
    private boolean mIsSelected = false;

    public EditWillAdapter(Context context, List<EditEntity> list) {
        this.mContext = context;
        Log.d(TAG, "EditWillAdapter: list.size" + list.size());
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EditWillAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_edit, parent, false);
            viewHolder = new EditWillAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (EditWillAdapter.ViewHolder) convertView.getTag();
        }

        EditEntity entity = mList.get(position);
        if (entity.isCheck()) {
            AAOP_HSkin
                    .with(viewHolder.ivEditItemSelect)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.button_check_box_sel)
                    .applySkin(false);
        } else {
            AAOP_HSkin
                    .with(viewHolder.ivEditItemSelect)
                    .addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.button_check_box_dis)
                    .applySkin(false);
        }

        if (mEditMode) {
            viewHolder.ivEditItemSelect.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivEditItemSelect.setVisibility(View.GONE);
        }

        if (mEditPlay) {
            viewHolder.ivEditItemPlay.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivEditItemPlay.setVisibility(View.GONE);
        }


        return convertView;
    }

    private class ViewHolder {
        private ImageView ivEditItemSelect;
        private ImageView ivEditItemPlay;

        public ViewHolder(View itemView) {
            ivEditItemSelect = itemView.findViewById(R.id.iv_edit_item_select);
            ivEditItemPlay = itemView.findViewById(R.id.iv_edit_play);
        }
    }

    public void setEditMode(boolean isEdit) {
        this.mEditMode = isEdit;
        notifyDataSetChanged();
    }

    public void setEditPlay(boolean isPlay) {
        this.mEditPlay = isPlay;
        notifyDataSetChanged();
    }



    public boolean getEditMode() {
        return this.mEditMode;
    }
}
