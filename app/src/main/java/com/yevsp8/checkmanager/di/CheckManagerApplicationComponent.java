package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.CheckDetailsFragment;
import com.yevsp8.checkmanager.CheckListFragment;
import com.yevsp8.checkmanager.CheckManagerApplication;
import com.yevsp8.checkmanager.GoogleApiActivity;
import com.yevsp8.checkmanager.view.BaseActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 02. 25..
 */
@CustomScope
@Component(modules = {GoogleApiModule.class, DatabaseModule.class})
public interface CheckManagerApplicationComponent {

    void injectApplication(CheckManagerApplication app);

    void injectBaseActivity(BaseActivity baseActivity);

    void injectCheckListViewModel(CheckListFragment checkListFragment);

    void injectCheckViewModel(CheckDetailsFragment checkDetailsFragment);

    void injectGooglaApiActivity(GoogleApiActivity googleApiActivity);
}
