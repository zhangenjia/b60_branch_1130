package com.adayo.app.video.ui.page.adapter.rcv.browse.base;

import android.widget.CompoundButton;

/**
 * @Auth: LinTan
 * @Date: 2021/11/19 15:25
 * @Desc: RecyclerView Item中CompoundButton及其子类的选中状态变化事件接口
 */

public interface OnItemChildCheckedChangeListener {
    void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position);
}
