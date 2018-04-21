package com.yevsp8.checkmanager;

import android.app.Application;
import android.content.Context;

import com.yevsp8.checkmanager.di.ApplicationModule;
import com.yevsp8.checkmanager.di.CheckManagerApplicationComponent;
import com.yevsp8.checkmanager.di.ContextModule;
import com.yevsp8.checkmanager.di.DaggerCheckManagerApplicationComponent;

import javax.inject.Inject;


/**
 * Created by Gergo on 2018. 02. 25..
 */

public class CheckManagerApplication extends Application {

    @Inject
    Context context;
//    @Inject
//    com.google.api.services.sheets.v4.Sheets googleSheetApi;

    @Override
    public void onCreate() {
        super.onCreate();

        CheckManagerApplicationComponent component = DaggerCheckManagerApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .applicationModule(new ApplicationModule(this))
                .build();

        component.injectApplication(this);
    }

    public Context getContext() {
        return context;
    }
}
