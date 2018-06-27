package com.wazing.gankmeizhi.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.wazing.gankmeizhi.R;
import com.wazing.gankmeizhi.app.GlideApp;
import com.wazing.gankmeizhi.entity.GankEntity;
import com.wazing.recycleradapter.BaseViewHolder;
import com.wazing.recycleradapter.SingleRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends SingleRecyclerViewAdapter<GankEntity> {

    private List<String> urlList = new ArrayList<String>();
    private List<ImageView> imageList = new ArrayList<ImageView>();

    public MainAdapter(Context context) {
        super(context, R.layout.item_recycler_gank_image);
    }

    @Override
    protected void convert(BaseViewHolder holder, GankEntity item, final int position) {

        if (!urlList.contains(item.getUrl())) {
            urlList.add(item.getUrl());
            imageList.add(holder.getView(R.id.item_image));
        }

        GlideApp.with(mContext)
                .load(item.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView) holder.getView(R.id.item_image));
        holder.setTextView(R.id.item_desc, item.getDesc());
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public List<ImageView> getImageList() {
        return imageList;
    }

    public void clear() {
        urlList.clear();
        imageList.clear();
    }

}
