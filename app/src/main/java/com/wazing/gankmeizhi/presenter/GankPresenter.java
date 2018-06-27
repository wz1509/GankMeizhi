package com.wazing.gankmeizhi.presenter;

import android.util.Log;

import com.wazing.gankmeizhi.model.api.ApiService;
import com.wazing.gankmeizhi.contract.GankContract;
import com.wazing.gankmeizhi.entity.BaseEntity;
import com.wazing.gankmeizhi.model.ApiCodeException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GankPresenter implements GankContract.Presenter {

    private final static int COUNT = 15;
    private int page;

    private ApiService mApiService;
    private GankContract.View mMvpView;

    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    public GankPresenter(ApiService apiService, GankContract.View mvpView) {
        mApiService = apiService;
        mMvpView = mvpView;
    }

    @Override
    public void getGankList(boolean isRefresh) {
        Log.d("zz", "getGankList: isRefresh = " + isRefresh);
        int index = page;
        if (isRefresh) index = 1;
        Disposable disposable = mApiService.getGankList(COUNT, index)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(BaseEntity::getResults)
                .subscribe(result -> {
                    if (isRefresh) page = 1;
                    page++;
                    mMvpView.onResult(isRefresh, result);
                }, throwable -> mMvpView.onApiFail(ApiCodeException.checkException(throwable)));
        mDisposable.add(disposable);
    }

    @Override
    public void destroy() {
        mDisposable.clear();
    }
}
