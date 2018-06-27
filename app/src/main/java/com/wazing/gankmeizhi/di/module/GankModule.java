package com.wazing.gankmeizhi.di.module;

import com.wazing.gankmeizhi.contract.GankContract;
import com.wazing.gankmeizhi.di.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class GankModule {

    private GankContract.View mGankView;

    public GankModule(GankContract.View view) {
        this.mGankView = view;
    }

    @Provides
    @PerActivity
    public GankContract.View provideGankView() {
        return mGankView;
    }

}
