package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.CheckManagerApplication;
import com.yevsp8.checkmanager.ListCheckFragment;
import com.yevsp8.checkmanager.view.BaseActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Component(modules = {GoogleApiModule.class, TessTwoModule.class, DatabaseModule.class})
public interface CheckManagerApplicationComponent {

//    Context getContext();
//    com.google.api.services.sheets.v4.Sheets getGoogleSheetApi();
//    TessBaseAPI getTessBaseApi();
//    CheckDatabase getCheckDatabase();

    void injectApplication(CheckManagerApplication app);

    void injectBaseActivity(BaseActivity baseActivity);

    void injectCheckViewModel(ListCheckFragment listCheckFragment);
}
