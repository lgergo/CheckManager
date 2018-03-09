package com.yevsp8.checkmanager.di;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
    @CustomScope
    Application provideApplication() {
        return application;
    }

    @Provides
    @CustomScope
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
}
