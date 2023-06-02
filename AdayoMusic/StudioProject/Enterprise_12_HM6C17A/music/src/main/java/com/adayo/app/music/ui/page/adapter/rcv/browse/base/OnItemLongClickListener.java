package com.adayo.app.music.ui.page.adapter.rcv.browse.base;

import android.view.View;

/**
 * @Auth: LinTan
 * @Date: 2021/11/19 15:26
 * @Desc: RecyclerView Item的长点击事件接口
 */

public interface OnItemLongClickListener {
    boolean onLongClick(View view, int position);
}
