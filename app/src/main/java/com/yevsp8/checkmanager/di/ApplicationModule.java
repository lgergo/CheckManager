package com.yevsp8.checkmanager.di;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application app) {
        this.application = app;
    }

    @Provides
    Application provideApplication() {
        return application;
    }
}
