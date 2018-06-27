package com.wazing.gankmeizhi.di.component;

import com.wazing.gankmeizhi.ui.activity.MainActivity;
import com.wazing.gankmeizhi.di.PerActivity;
import com.wazing.gankmeizhi.di.module.GankModule;

import dagger.Component;

@PerActivity
@Component(modules = GankModule.class, dependencies = AppComponent.class)
public interface GankComponent {

    void inject(MainActivity activity);
}
