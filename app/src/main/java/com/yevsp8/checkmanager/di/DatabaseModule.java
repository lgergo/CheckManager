package com.yevsp8.checkmanager.di;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yevsp8.checkmanager.data.CheckDAO;
import com.yevsp8.checkmanager.data.CheckDatabase;
import com.yevsp8.checkmanager.data.CheckRepository;
import com.yevsp8.checkmanager.viewModel.CustomViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Gergo on 2018. 02. 25..
 */

/*
* Dependency interface for Room database
* */

@Module
public class DatabaseModule {

    private final CheckDatabase database;

    public DatabaseModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                CheckDatabase.class,
                "Check.db"
        ).build();

    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }
    //TODO google + tesseract + opencv ???

    @Provides
    @Singleton
    CheckRepository provideCheckRepository(CheckDAO checkDAO) {
        return new CheckRepository(checkDAO);
    }

    @Provides
    @Singleton
    CheckDAO provideCheckDao(CheckDatabase database) {
        return database.checkDAO();
    }

    @Provides
    @Singleton
    CheckDatabase provideCheckDatabase(Application application) {
        return database;
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(CheckRepository repository) {
        return new CustomViewModelFactory(repository);
    }
}
