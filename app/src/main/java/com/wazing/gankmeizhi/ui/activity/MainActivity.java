package com.wazing.gankmeizhi.ui.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.wazing.gankmeizhi.R;
import com.wazing.gankmeizhi.app.App;
import com.wazing.gankmeizhi.app.GlideApp;
import com.wazing.gankmeizhi.contract.GankContract;
import com.wazing.gankmeizhi.di.component.DaggerGankComponent;
import com.wazing.gankmeizhi.di.module.GankModule;
import com.wazing.gankmeizhi.entity.GankEntity;
import com.wazing.gankmeizhi.presenter.GankPresenter;
import com.wazing.gankmeizhi.ui.adapter.MainAdapter;
import com.wazing.gankmeizhi.util.DensityUtils;
import com.wazing.gankmeizhi.util.SpaceItemDecoration;
import com.wazing.imagewatcher.ImageWatcher;
import com.wazing.recycleradapter.RecyclerStatus;
import com.wazing.recycleradapter.listener.OnItemClickListener;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements GankContract.View, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    public GankPresenter mPresenter;

    private MainAdapter mAdapter;

    private ImageWatcher mImageWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerGankComponent.builder()
                .appComponent(((App) getApplication()).getAppComponent())
                .gankModule(new GankModule(this))
                .build()
                .inject(this);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
//        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

//        GridLayoutManager manager = new GridLayoutManager(this, 2);

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(2,
                DensityUtils.dip2px(this, 2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter = new MainAdapter(this));

        mImageWatcher = getImageWatcher();

        mAdapter.setOnStatusClickListener(this::onRefresh);
        mAdapter.setOnLoadMoreListener(() -> {
            mPresenter.getGankList(false);
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener.SimpleOnItemClickListener<GankEntity>() {
            @Override
            public void onItemClick(View view, GankEntity item, int position) {
                mImageWatcher.show(position, mAdapter.getImageList(), mAdapter.getUrlList());
            }
        });
        mAdapter.setStatusViewHolder(RecyclerStatus.LOADING);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        mPresenter.getGankList(true);
    }

    @Override
    public void onResult(boolean isRefresh, List<GankEntity> list) {
        closeSwipeRefresh();
        if (isRefresh) {
            mAdapter.clear();
            mAdapter.addNewData(list);
        } else {
            mAdapter.addData(list);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onApiFail(String msg) {
        closeSwipeRefresh();
        if (mAdapter.getData().isEmpty()) {
            mAdapter.setStatusViewHolder(RecyclerStatus.ERROR);
        } else {
            mAdapter.loadMoreFail();
        }
    }

    void closeSwipeRefresh() {
        if (mSwipeRefreshLayout.isShown()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mImageWatcher.handleBackPressed()) {
            super.onBackPressed();
        }
    }

    private ImageWatcher getImageWatcher() {
        return ImageWatcher.Helper.with(this)
                .setLoader((context, url, lc) -> GlideApp.with(this)
                        .load(url)
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                lc.onResourceReady(resource);
                            }
                        }))
                .create();
    }

    @Override
    protected void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
    }
}
