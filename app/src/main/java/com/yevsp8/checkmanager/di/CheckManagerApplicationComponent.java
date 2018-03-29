package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.CheckManagerApplication;
import com.yevsp8.checkmanager.view.CheckDetailsFragment;
import com.yevsp8.checkmanager.view.CheckListFragment;
import com.yevsp8.checkmanager.view.GoogleApiActivity;
import com.yevsp8.checkmanager.view.SettingsActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 02. 25..
 */
@CustomScope
@Component(modules = {GoogleApiModule.class, DatabaseModule.class})
public interface CheckManagerApplicationComponent {

    void injectApplication(CheckManagerApplication app);

    void injectCheckListViewModel(CheckListFragment checkListFragment);

    void injectCheckViewModel(CheckDetailsFragment checkDetailsFragment);

    void injectGooglaApiActivity(GoogleApiActivity googleApiActivity);

    void injectSettingsActivity(SettingsActivity createSettingsActivity);
}
