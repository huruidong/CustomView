package com.example.customviewdemo.scrollbarRecyclerview;

public interface ItemTouchStatus {

    boolean onItemMove(int fromPosition, int toPosition);

    boolean onItemRemove(int position);
}
