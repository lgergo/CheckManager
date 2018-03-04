package com.yevsp8.checkmanager.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.data.CheckRepository;

/**
 * Created by Gergo on 2018. 02. 25..
 */

public class CheckViewModel extends ViewModel {

    private CheckRepository repo;

    public CheckViewModel(CheckRepository repo) {
        this.repo = repo;
    }

    public LiveData<Check> getCheckById(String id) {
        return repo.getCheck(id);
    }

    public void insertCheck(Check check) {
        InsertCheckTask task = new InsertCheckTask();
        task.execute(check);
    }

    private class InsertCheckTask extends AsyncTask<Check, Void, Void> {

        @Override
        protected Void doInBackground(Check... checks) {
            repo.insertCheck(checks[0]);
            return null;
        }
    }

}
