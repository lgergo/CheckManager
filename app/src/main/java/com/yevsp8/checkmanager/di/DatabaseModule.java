package com.yevsp8.checkmanager.di;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;

import com.yevsp8.checkmanager.CustomNotificationManager;
import com.yevsp8.checkmanager.data.CheckDAO;
import com.yevsp8.checkmanager.data.CheckDatabase;
import com.yevsp8.checkmanager.data.CheckRepository;
import com.yevsp8.checkmanager.viewModel.CustomViewModelFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Module(includes = ApplicationModule.class)
public class DatabaseModule {

    @Provides
    @CustomScope
    public CheckDatabase provideCheckDatabase(Application application) {
        return Room.databaseBuilder(
                application,
                CheckDatabase.class,
                "Check.db"
        ).build();
    }

    @Provides
    @CustomScope
    CheckRepository provideCheckRepository(CheckDAO checkDAO) {
        return new CheckRepository(checkDAO);
    }

    @Provides
    @CustomScope
    CheckDAO provideCheckDao(CheckDatabase database) {
        return database.checkDAO();
    }

    @Provides
    @CustomScope
    ViewModelProvider.Factory provideViewModelFactory(CheckRepository checkRepository) {
        return new CustomViewModelFactory(checkRepository);
    }

    @Provides
    @CustomScope
    CustomNotificationManager provideCustomNotificationManager() {
        return new CustomNotificationManager();
    }
}
