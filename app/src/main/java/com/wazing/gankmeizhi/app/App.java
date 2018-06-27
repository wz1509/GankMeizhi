package com.wazing.gankmeizhi.app;

import android.app.Application;

import com.wazing.gankmeizhi.di.component.AppComponent;
import com.wazing.gankmeizhi.di.component.DaggerAppComponent;
import com.wazing.gankmeizhi.di.module.AppModule;
import com.wazing.gankmeizhi.di.module.HttpModule;

public class App extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public AppComponent getAppComponent() {
        if (mAppComponent == null) {
            mAppComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .httpModule(new HttpModule())
                    .build();
        }
        return mAppComponent;
    }
}
