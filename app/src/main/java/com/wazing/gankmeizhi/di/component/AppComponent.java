package com.wazing.gankmeizhi.di.component;

import android.app.Application;

import com.wazing.gankmeizhi.model.api.ApiService;
import com.wazing.gankmeizhi.di.module.AppModule;
import com.wazing.gankmeizhi.di.module.HttpModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, HttpModule.class})
public interface AppComponent {

    Application getApplication();

    ApiService getApiService();
}
