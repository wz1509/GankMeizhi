package com.wazing.gankmeizhi.model.api;

import com.wazing.gankmeizhi.entity.BaseEntity;
import com.wazing.gankmeizhi.entity.GankEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("data/福利/{count}/{page}")
    Observable<BaseEntity<GankEntity>> getGankList(@Path("count") int count,
                                                         @Path("page") int page);
}
