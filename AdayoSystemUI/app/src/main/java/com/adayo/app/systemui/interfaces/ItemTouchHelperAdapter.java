package com.adayo.app.systemui.interfaces;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/24 13:57
 */
public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDismiss(int position);
}
