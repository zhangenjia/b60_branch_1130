package com.adayo.app.music.ui.page.adapter.rcv.browse;

import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.app.music.ui.page.adapter.rcv.browse.base.BaseViewHolder;
import com.adayo.app.music.ui.page.adapter.rcv.browse.base.OnItemChildCheckedChangeListener;
import com.lt.library.util.LogUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @Auth: LinTan
 * @Date: 2021/11/19 15:16
 * @Desc: 封装业务相关的ViewHolder基类
 */

public class AbsViewHolder extends BaseViewHolder {
    private final ArrayMap<View, OnItemChildCheckedChangeListener> mChildViewCheckedListenerArray;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    {
        mChildViewCheckedListenerArray = new ArrayMap<>();
    }

    public AbsViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public AbsViewHolder setText(int viewId, CharSequence text) {
        getView(viewId, TextView.class).setText(text);
        return this;
    }

    public AbsViewHolder setText(int viewId, @StringRes int stringId) {
        getView(viewId, TextView.class).setText(stringId);
        return this;
    }

    public String getText(int viewId) {
        return getView(viewId, TextView.class).getText().toString();
    }

    public AbsViewHolder setTextSize(int viewId, @DimenRes int dimenId) {
        return setTextSize(viewId, dimenId, TypedValue.COMPLEX_UNIT_SP);
    }

    public AbsViewHolder setTextSize(int viewId, @DimenRes int dimenId, int unit) {
        getView(viewId, TextView.class).setTextSize(unit, getAppContext().getResources().getDimension(dimenId));
        return this;
    }

    public AbsViewHolder setTextColor(int viewId, @ColorRes int colorId) {
        getView(viewId, TextView.class).setTextColor(ContextCompat.getColor(getAppContext(), colorId));
        return this;
    }

    public AbsViewHolder setTextColorStateList(int viewId, @ColorRes int colorId) {
        getView(viewId, TextView.class).setTextColor(ContextCompat.getColorStateList(getAppContext(), colorId));
        return this;
    }

    public AbsViewHolder setImageDrawable(int viewId, @DrawableRes @Nullable Integer drawableId) {
        ImageView view = getView(viewId, ImageView.class);
        if (drawableId == null) {
            view.setImageDrawable(null);
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(getAppContext(), drawableId));
        }
        return this;
    }

    public AbsViewHolder setImageDrawableTint(int viewId, @DrawableRes int drawableId, @ColorRes int colorId) {
        ImageView view = getView(viewId, ImageView.class);
        Drawable drawable = ContextCompat.getDrawable(getAppContext(), drawableId);
        DrawableCompat.setTint(Objects.requireNonNull(drawable), ContextCompat.getColor(getAppContext(), colorId));
        view.setImageDrawable(drawable);
        return this;
    }

    public AbsViewHolder setImageDrawableTintList(int viewId, @DrawableRes int drawableId, @ColorRes int colorId) {
        ImageView view = getView(viewId, ImageView.class);
        Drawable drawable = ContextCompat.getDrawable(getAppContext(), drawableId);
        DrawableCompat.setTintList(Objects.requireNonNull(drawable), ContextCompat.getColorStateList(getAppContext(), colorId));
        view.setImageDrawable(drawable);
        return this;
    }

    public AbsViewHolder setBackground(int viewId, @DrawableRes @Nullable Integer drawableId) {
        View view = getView(viewId);
        if (drawableId == null) {
            view.setBackground(null);
        } else {
            view.setBackground(ContextCompat.getDrawable(getAppContext(), drawableId));
        }
        return this;
    }

    public AbsViewHolder setVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public AbsViewHolder setChecked(int viewId, boolean isChecked) {
        CheckBox view = getView(viewId, CheckBox.class);
        view.setChecked(isChecked);
        return this;
    }

    public AbsViewHolder addOnCheckedChangeListener(int viewId, OnItemChildCheckedChangeListener onItemChildViewCheckListener) {
        if (mOnCheckedChangeListener == null) {
            mOnCheckedChangeListener = (buttonView, isChecked) -> {
                int position = getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    LogUtil.w("view is changing");
                    return;
                }
                for (Map.Entry<View, OnItemChildCheckedChangeListener> entry : mChildViewCheckedListenerArray.entrySet()) {
                    OnItemChildCheckedChangeListener value = entry.getValue();
                    value.onCheckedChanged(buttonView, isChecked, position);
                }
            };
        }
        CheckBox view = getView(viewId, CheckBox.class);
        mChildViewCheckedListenerArray.put(view, onItemChildViewCheckListener);
        view.setOnCheckedChangeListener(mOnCheckedChangeListener);
        return this;
    }

    public AbsViewHolder delOnCheckedChangeListener(int viewId) {
        CheckBox view = getView(viewId, CheckBox.class);
        view.setOnCheckedChangeListener(null);
        mChildViewCheckedListenerArray.remove(view);
        return this;
    }
}
