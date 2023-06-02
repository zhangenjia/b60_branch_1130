package com.adayo.app.launcher.util;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 此类用来给RecyclerView添加空隙或者分割线
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int itemViewCount;
    private int normalSpace;//非首个item距离，距离左侧间隔
    private int startAndEndSpace;//列表两端距离屏幕间隔

    public SpacesItemDecoration(int startAndEndSpace,int normalSpace,int itemViewCount) {
        this.startAndEndSpace = startAndEndSpace;
        this.normalSpace = normalSpace;
        this.itemViewCount = itemViewCount;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = normalSpace;
//        outRect.right = space;
//        outRect.bottom = space;
        // 添加限定如果是是末尾的子view右边添加空隙
        if (itemViewCount<1){
            return;
        }
        if (parent.getChildPosition(view) == itemViewCount-1) {
            outRect.right = normalSpace;
        }
        if (parent.getChildPosition(view) ==0) {
            outRect.left = startAndEndSpace;
        }
    }
}