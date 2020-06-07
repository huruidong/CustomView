package com.example.customviewdemo.scrollbarRecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.example.customviewdemo.R;


public class CustomItemTouchCallback extends ItemTouchHelper.Callback {

    private int mContentViewId = R.id.hrd_slidelayout_content;

    private final ItemTouchStatus mItemTouchStatus;

    public CustomItemTouchCallback(ItemTouchStatus itemTouchStatus) {
        mItemTouchStatus = itemTouchStatus;
    }

    public CustomItemTouchCallback(ItemTouchStatus itemTouchStatus, int contentViewId) {
        mItemTouchStatus = itemTouchStatus;
        mContentViewId = contentViewId;
    }

    public void setContentViewId(int contentViewId) {
        this.mContentViewId = contentViewId;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 上下拖动
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        // 左右滑动
        boolean isLayoutRtl = false;
        if (recyclerView instanceof ScrollbarRecyclerView) {
            isLayoutRtl = ((ScrollbarRecyclerView)recyclerView).isLayoutRtl();
        }
        int swipeFlags = 0;
        View contentView = viewHolder.itemView.findViewById(mContentViewId);
        if (null == contentView) {
            throw new IllegalArgumentException(
                    "Slide to delete need contentView(R.id.hrd_slidelayout_content).");
        }
        if (isLayoutRtl) {
            swipeFlags = contentView.getTranslationX() > 0 ? 0 : ItemTouchHelper.LEFT;
        } else {
            swipeFlags = contentView.getTranslationX() < 0 ? 0 : ItemTouchHelper.RIGHT/* | ItemTouchHelper.LEFT*/;
        }
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // 交换在数据源中相应数据源的位置
        return mItemTouchStatus.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // 从数据源中移除相应的数据
        mItemTouchStatus.onItemRemove(viewHolder.getAdapterPosition());
    }

}

