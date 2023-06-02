package com.adayo.app.dvr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.app.dvr.R;
import com.adayo.app.dvr.entity.EditEntity;
import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;

public class EditAdapter extends BaseAdapter {

    private static final String TAG = "EditAdapter";
    private Context mContext;
    private List<EditEntity> mList;
    private boolean mEditMode = false;
    private boolean mEditPlay = false;
    private boolean mIsSelected = false;

    public EditAdapter(Context context, List<EditEntity> list) {
        this.mContext = context;
        Log.d(TAG, "EditAdapter: list.size" + list.size());
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_edit, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
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
