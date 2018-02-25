package com.yevsp8.checkmanager.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

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

    public void deleteCheck(Check check) {
        DeleteCheckTask task = new DeleteCheckTask();
        task.execute(check);
    }

    private class DeleteCheckTask extends AsyncTask<Check, Void, Void> {

        @Override
        protected Void doInBackground(Check... checks) {
            repo.deleteCheck(checks[0]);
            return null;
        }
    }
}
