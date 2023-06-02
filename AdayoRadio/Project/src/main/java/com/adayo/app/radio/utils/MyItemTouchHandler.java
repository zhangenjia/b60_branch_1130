package com.adayo.app.radio.utils;

import static com.adayo.app.radio.constant.Constants.RADIO_APP_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;


import com.adayo.app.radio.R;
import com.adayo.app.radio.ui.adapter.NewRadioCollectionListAdapter;

/**
 * @author ADAYO-06
 */
public class MyItemTouchHandler extends ItemTouchHelper.Callback {
    private static final String TAG = "MyItemTouchHandler";

    BaseItemTouchAdapterImpl adapter;
    private String string = "";
    private long lastTime = 0;

    public MyItemTouchHandler(@NonNull BaseItemTouchAdapterImpl adapter) {
        this.adapter = adapter;
    }


    /**
     * 设置 允许拖拽和滑动删除的方向
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 指定可 拖拽方向 和 滑动消失的方向
        int dragFlags = 0, swipeFlags = 0;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if (viewHolder.getItemViewType() == 1) {
            if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager) {
                // 上下左右都可以拖动
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else {
                // 可以上下拖动
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }

//            swipeFlags = ItemTouchHelper.START;//左滑消失
            // 如果某个值传 0 , 表示不支持该功能
//        }
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 拖拽后回调,一般通过接口暴露给adapter, 让adapter去处理数据的交换
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // 相同 viewType 之间才能拖动交换
        Log.i(TAG, "onMove: viewHolder.getItemViewType() " + viewHolder.getItemViewType());
        if (viewHolder.getItemViewType() == target.getItemViewType()) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                //途中所有的item位置都要移动
                for (int i = fromPosition; i < toPosition; i++) {
                    adapter.onItemMove(i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    adapter.onItemMove(i, i - 1);
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        return false;
    }

    /**
     * 滑动删除后回调,一般通过接口暴露给adapter, 让adapter去删除该条数据
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        if (viewHolder.getItemViewType() == 1) {
            // 删除数据
            adapter.onItemRemove(viewHolder.getAdapterPosition());
            // adapter 刷新
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //滑动时改变Item的透明度, 这个项目不需要
            final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
    }

    /**
     * item被选中(长按)
     * 这里改变了 item的背景色, 也可以通过接口暴露, 让adapter去处理逻辑
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        Log.d(TAG, "onSelectedChanged: 121212");
        if (actionState == ItemTouchHelper.LEFT) {
            // 拖拽状态
            viewHolder.itemView.setBackgroundResource(R.drawable.bottom_select);
        }
        else if (actionState == ItemTouchHelper.RIGHT) {
            viewHolder.itemView.setBackgroundResource(R.drawable.bottom_select);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * item取消选中(取消长按)
     * 这里改变了 item的背景色, 也可以通过接口暴露, 让adapter去处理逻辑
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.d(TAG, "clearView: 121212");
        //防止快速长押出现崩溃
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                ((NewRadioCollectionListAdapter) adapter).showBlur();
                adapter.notifyDataSetChanged();
                Log.d(TAG, "clearView: adapter = "+adapter);
            }
        });
        MyReceiverMove  receiverMove = new MyReceiverMove();
        IntentFilter intentFilterMove = new IntentFilter();
        /**
         * 要接收的广播
         */
        intentFilterMove.addAction("com.adayo.app.radio.move");
        /**
         * 注册接收者
         */
        viewHolder.itemView.getContext().registerReceiver(receiverMove, intentFilterMove);

        Intent intent = new Intent("com.adayo.app.radio.move");
        viewHolder.itemView.getContext().sendBroadcast(intent);
        super.clearView(recyclerView, viewHolder);
        //}

    }

    /**
     * 是否支持长按开始拖拽,默认开启
     * 可以不开启,然后在长按 item 的时候,手动 调用 mItemTouchHelper.startDrag(myHolder) 开启,更加灵活
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return adapter.autoOpenDrag();
    }

    /**
     * 是否支持滑动删除,默认开启     * 可以不开启,然后在长按 item 的时候,手动 调用 mItemTouchHelper.startSwipe(myHolder) 开启,更加灵活
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return adapter.autoOpenSwipe();
    }

    /**
     * 建议让 adapter 实现该接口
     */
    public static abstract class BaseItemTouchAdapterImpl extends RecyclerView.Adapter {

        /**
         * item Move
         *
         * @param fromPosition 移动前位置
         * @param toPosition 移动后位置
         */
        public abstract void onItemMove(int fromPosition, int toPosition);

        /**
         * Item Remove
         *
         * @param position 删除item数
         */
        public abstract void onItemRemove(int position);

        /**
         * 是否自动开启拖拽
         *
         * @return true 自动开启拖拽功能
         */
        protected boolean autoOpenDrag() {
            return true;
        }

        /**
         * 是否自动开启滑动删除
         *
         * @return false 不支持滑动删除
         */
        protected boolean autoOpenSwipe() {
            return false;
        }
    }


    public class MyReceiverMove extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (RADIO_APP_ACTION.equals(intent.getAction())) {
                string = intent.getStringExtra("onItemRemove");
                Log.i(TAG, "onReceive: string===" + string);

            }
        }

    }//广播接收器
}
