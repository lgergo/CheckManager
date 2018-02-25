package com.yevsp8.checkmanager.di;

import android.app.Application;

import com.yevsp8.checkmanager.ListCheckFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Singleton   // scope
@Component(modules = {ApplicationModule.class, DatabaseModule.class, ContextModule.class})
public interface ApplicationComponent {

    void inject(ListCheckFragment checkListFragment);

    //TODO többi fragment
    //void inject(CheckFragment createFragment);
    // void inject(DetailFragment detailFragment);

    Application application();
}
