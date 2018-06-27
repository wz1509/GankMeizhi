package com.wazing.recycleradapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wazing.recycleradapter.listener.OnItemClickListener;
import com.wazing.recycleradapter.listener.OnLoadMoreListener;
import com.wazing.recycleradapter.listener.OnStatusClickListener;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "zz";

    final static int TYPE_ITEM = 10000;
    private final static int TYPE_EMPTY_LOADING = 10001;
    private final static int TYPE_LOAD_MORE = 10003;

    @LayoutRes
    private final static int RECYCLER_LOAD_MORE = R.layout.recycler_load_more;
    @LayoutRes
    private final static int RECYCLER_STATUS_VIEW_ID = R.layout.recycler_status_view;

    protected final Context mContext;
    private final List<T> mList;

    private boolean isOpenLoadMore = false;
    private boolean isLoadingMore = false;
    private boolean isLoadMoreEnd = false;
    private BaseViewHolder mLoadMoreViewHolder;

    private BaseViewHolder mStatusViewHolder;

    private OnItemClickListener<T> mOnItemClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnStatusClickListener mOnStatusClickListener;

    public MultiRecyclerViewAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public MultiRecyclerViewAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
        this.isOpenLoadMore = true;
    }

    public void setOnStatusClickListener(OnStatusClickListener listener) {
        this.mOnStatusClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if (isEmptyLoadingView()) return 1;
        int count = 0;
        if (isOpenLoadMore) {
            count++;
        }
        count += mList.size();
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmptyLoadingView()) {
            return TYPE_EMPTY_LOADING;
        } else if (isOpenLoadMore && getItemCount() == (position + 1)) {
            return TYPE_LOAD_MORE;
        }
        return addItemViewType(position);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_EMPTY_LOADING:
                mStatusViewHolder = BaseViewHolder.create(parent, RECYCLER_STATUS_VIEW_ID);
                mStatusViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnStatusClickListener != null &&
                                mStatusViewHolder.getView(R.id.recycler_status_error)
                                        .getVisibility() == View.VISIBLE) {
                            setStatusViewHolder(RecyclerStatus.LOADING);
                            mOnStatusClickListener.onRetry();
                        }
                    }
                });
                return mStatusViewHolder;
            case TYPE_LOAD_MORE:
                mLoadMoreViewHolder = BaseViewHolder.create(parent, RECYCLER_LOAD_MORE);
                mLoadMoreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isLoadingMore || isLoadMoreEnd) return;
                        startLoadMore();
                    }
                });
                return mLoadMoreViewHolder;
            default:
                final BaseViewHolder holder = onCreateBaseViewHolder(parent, viewType);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null) {
                            final int position = holder.getAdapterPosition();
                            Log.d(TAG, "onClick: position = " + position);
                            mOnItemClickListener.onItemClick(v, mList.get(position), position);
                        }
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (mOnItemClickListener != null) {
                            final int position = holder.getAdapterPosition();
                            Log.d(TAG, "onLongClick: position = " + position);
                            mOnItemClickListener.onItemLongClick(v, mList.get(position), position);
                        }
                        return true;
                    }
                });
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        final int viewType = holder.getItemViewType();
        switch (viewType) {
            case TYPE_LOAD_MORE:
                startLoadMore();
                break;
            case TYPE_EMPTY_LOADING:

                break;
            default:
                convert(holder, mList.get(position), position);
                break;
        }
    }

    private void startLoadMore() {
        if (!isOpenLoadMore || mOnLoadMoreListener == null || mList.isEmpty()
                || isLoadingMore || isLoadMoreEnd) return;
        loadMoreLoading();
        mOnLoadMoreListener.onLoadMore();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null && layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isOpenLoadMore && getItemCount() == (position + 1)) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            final int position = holder.getAdapterPosition();
//            Log.d(TAG, "onViewAttachedToWindow: position = " + position);
            if (isOpenLoadMore && getItemCount() == (position + 1)) {
                StaggeredGridLayoutManager.LayoutParams layoutParams =
                        (StaggeredGridLayoutManager.LayoutParams) lp;
                layoutParams.setFullSpan(true);
            }
        }
    }

//    private void startLoadMore(@NonNull RecyclerView recyclerView) {
//        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//        if (!isOpenLoadMore || mOnLoadMoreListener == null || layoutManager == null) return;
//
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    int lastPosition = findLastVisibleItemPosition(layoutManager);
//                    Log.i(TAG, "onScrollStateChanged: lastPosition = " + lastPosition);
//                    if (!isAutoLoadMore && lastPosition + 1 == getItemCount()) {
//                        isLoadingMore = true;
//                        loadMoreLoading();
//                        mOnLoadMoreListener.onLoadMore();
//                    }
//                }
////                boolean flag = recyclerView.canScrollVertically(1);
////                if (!isLoadingMore && !flag) {
////                    isLoadingMore = true;
////
////                    loadMoreLoading();
////                    mOnLoadMoreListener.onLoadMore();
////                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int lastPosition = findLastVisibleItemPosition(layoutManager);
//                Log.i(TAG, "onScrolled: lastPosition = " + lastPosition);
//                if (lastPosition == 0) return;
//                if (isAutoLoadMore && lastPosition + 1 == getItemCount()) {
//                    isLoadingMore = true;
//                    loadMoreLoading();
//                    mOnLoadMoreListener.onLoadMore();
//                } else if (isAutoLoadMore) {
//                    isAutoLoadMore = false;
//                }
//            }
//        });
//    }
//
//    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
//        if (layoutManager instanceof LinearLayoutManager) {
//            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
//        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
//
//            int max = lastVisibleItemPositions[0];
//            for (int value : lastVisibleItemPositions) {
//                if (value > max) {
//                    max = value;
//                }
//            }
//            return max;
//        }
//        return -1;
//    }

    public T getData(int position) {
        return mList.get(position);
    }

    public List<T> getData() {
        return mList;
    }

    public void addNewData(List<T> list) {
        mList.clear();
        if (list.isEmpty()) {
            setStatusViewHolder(RecyclerStatus.EMPTY);
        } else {
            mList.addAll(list);
            notifyDataSetChanged();
        }
//        addData(list);

        // 所有数据加载完成 = false
        isLoadMoreEnd = false;
        isLoadingMore = false;
    }

    public void addData(List<T> list) {
        if (list.isEmpty()) {
            loadMoreEnd();
        } else {
            mList.addAll(list);
            notifyItemRangeInserted(getItemCount(), list.size());
        }
        isLoadingMore = false;
    }

    public void setStatusViewHolder(RecyclerStatus status) {
        if (isEmptyLoadingView() && mStatusViewHolder != null) {
            View loadingView = mStatusViewHolder.getView(R.id.recycler_status_loading);
            loadingView.setVisibility(View.GONE);

            View errorView = mStatusViewHolder.getView(R.id.recycler_status_error);
            errorView.setVisibility(View.GONE);

            View emptyView = mStatusViewHolder.getView(R.id.recycler_status_empty);
            emptyView.setVisibility(View.GONE);

            switch (status) {
                case LOADING:
                    loadingView.setVisibility(View.VISIBLE);
                    break;
                case EMPTY:
                    emptyView.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    errorView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void loadMoreLoading() {
        isLoadingMore = true;
        loadMoreStatus(true, "正在加载...");
    }

    public void loadMoreFail() {
        loadMoreFail("加载失败，点击重试");
    }

    public void loadMoreFail(String message) {
        isLoadingMore = false;
        loadMoreStatus(false, message);
    }

    public void loadMoreEnd() {
        loadMoreEnd("没有更多了 (=・ω・=)");
    }

    public void loadMoreEnd(String message) {
        isLoadingMore = false;
        isLoadMoreEnd = true;
        loadMoreStatus(false, message);
    }

    private void loadMoreStatus(boolean isVisible, String message) {
        if (mLoadMoreViewHolder == null) return;
        if (mLoadMoreViewHolder.itemView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) mLoadMoreViewHolder.itemView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childView = viewGroup.getChildAt(i);
                if (childView instanceof ProgressBar) {
                    childView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                } else if (childView instanceof TextView) {
                    ((TextView) childView).setText(message);
                }
            }
        }
    }

    private boolean isEmptyLoadingView() {
        return mList.isEmpty() && RECYCLER_STATUS_VIEW_ID != -1;
    }

    protected abstract int addItemViewType(int position);

    protected abstract BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType);

    protected abstract void convert(BaseViewHolder holder, T item, int position);
}
