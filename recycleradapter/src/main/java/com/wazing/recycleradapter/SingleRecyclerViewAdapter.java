package com.wazing.recycleradapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class SingleRecyclerViewAdapter<T> extends MultiRecyclerViewAdapter<T> {

    private final int itemLayoutRes;

    public SingleRecyclerViewAdapter(Context context, @LayoutRes int itemLayoutRes) {
        this(context, new ArrayList<T>(), itemLayoutRes);
    }

    public SingleRecyclerViewAdapter(Context context, List<T> list, @LayoutRes int itemLayoutRes) {
        super(context, list);
        this.itemLayoutRes = itemLayoutRes;
    }

    @Override
    protected int addItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    protected BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.create(parent, itemLayoutRes);
    }
}
