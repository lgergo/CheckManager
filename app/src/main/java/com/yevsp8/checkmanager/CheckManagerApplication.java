package com.yevsp8.checkmanager;

import android.app.Application;

import com.yevsp8.checkmanager.di.ApplicationComponent;
import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.DatabaseModule;

/**
 * Created by Gergo on 2018. 02. 25..
 */

public class CheckManagerApplication extends Application {

    //singleton - ok itt legyenek deklarálva az application scope-ban
    private ApplicationComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .build();

    }

    public ApplicationComponent getApplicationComponenet() {
        return appComponent;
    }
}
