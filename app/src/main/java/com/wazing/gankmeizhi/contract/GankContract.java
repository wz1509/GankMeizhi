package com.wazing.gankmeizhi.contract;

import com.wazing.gankmeizhi.entity.GankEntity;
import com.wazing.gankmeizhi.presenter.BasePresenter;

import java.util.List;

public interface GankContract {

    interface View {

        void onResult(boolean isRefresh, List<GankEntity> list);

        void onApiFail(String msg);
    }

    interface Presenter extends BasePresenter {
        void getGankList(boolean isRefresh);
    }
}
