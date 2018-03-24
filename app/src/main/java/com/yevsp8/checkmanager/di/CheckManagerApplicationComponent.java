package com.yevsp8.checkmanager.di;

import com.yevsp8.checkmanager.CheckDetailsFragment;
import com.yevsp8.checkmanager.CheckListFragment;
import com.yevsp8.checkmanager.CheckManagerApplication;
import com.yevsp8.checkmanager.GoogleApiActivity;
import com.yevsp8.checkmanager.NotificationListFragment;
import com.yevsp8.checkmanager.view.CreateNotificationActivity;

import dagger.Component;

/**
 * Created by Gergo on 2018. 02. 25..
 */
@CustomScope
@Component(modules = {GoogleApiModule.class, DatabaseModule.class})
public interface CheckManagerApplicationComponent {

    void injectApplication(CheckManagerApplication app);

    // void injectMainActivity(MainActivity mainActivity);

    void injectCheckListViewModel(CheckListFragment checkListFragment);

    void injectCheckViewModel(CheckDetailsFragment checkDetailsFragment);

    void injectNotificationListViewmodel(NotificationListFragment notificationListFragment);

    void injectGooglaApiActivity(GoogleApiActivity googleApiActivity);

    void injectCreateNotificationActivity(CreateNotificationActivity createNotificationActivity);
}
