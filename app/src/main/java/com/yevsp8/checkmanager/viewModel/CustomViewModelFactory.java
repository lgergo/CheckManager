package com.yevsp8.checkmanager.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.yevsp8.checkmanager.data.CheckRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Gergo on 2018. 02. 25..
 */

@Singleton
public class CustomViewModelFactory implements ViewModelProvider.Factory {

    private final CheckRepository repository;

    @Inject
    public CustomViewModelFactory(CheckRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckListViewModel.class))
            return (T) new CheckListViewModel(repository);
        else if (modelClass.isAssignableFrom(CheckViewModel.class))
            return (T) new CheckViewModel(repository);
        else
            throw new IllegalArgumentException("Illegal view model type");
    }
}
