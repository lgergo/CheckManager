package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.CheckManagerApplication;
import com.yevsp8.checkmanager.GoogleApiActivity;
import com.yevsp8.checkmanager.ListCheckFragment;
import com.yevsp8.checkmanager.view.BaseActivity;
import com.yevsp8.checkmanager.view.CheckDetailsActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 02. 25..
 */
@CustomScope
@Component(modules = {GoogleApiModule.class, TessTwoModule.class, DatabaseModule.class})
public interface CheckManagerApplicationComponent {

    void injectApplication(CheckManagerApplication app);

    void injectBaseActivity(BaseActivity baseActivity);

    void injectListCheckViewModel(ListCheckFragment listCheckFragment);

    void injectCheckViewModel(CheckDetailsActivity checkDetailsActivity);

    void injectGooglaApiActivity(GoogleApiActivity googleApiActivity);
}
