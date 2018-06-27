package com.wazing.recycleradapter.listener;

import android.view.View;

public interface OnItemClickListener<T> {

    void onItemClick(View view, T item, int position);

    void onItemLongClick(View view, T item, int position);

    class SimpleOnItemClickListener<T> implements OnItemClickListener<T> {

        @Override
        public void onItemClick(View view, T item, int position) {

        }

        @Override
        public void onItemLongClick(View view, T item, int position) {

        }
    }

}
