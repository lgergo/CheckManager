package com.yevsp8.checkmanager.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.data.CheckRepository;

import java.util.List;

/**
 * Created by Gergo on 2018. 02. 25..
 */

public class CheckListViewModel extends ViewModel {

    private CheckRepository repo;

    public CheckListViewModel(CheckRepository repo) {
        this.repo = repo;
    }

    public LiveData<List<Check>> getCheckList() {
        return repo.getDataList();
    }
}
