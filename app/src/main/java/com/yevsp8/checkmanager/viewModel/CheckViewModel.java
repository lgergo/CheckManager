package com.yevsp8.checkmanager.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.yevsp8.checkmanager.data.Check;
import com.yevsp8.checkmanager.data.CheckRepository;
import com.yevsp8.checkmanager.util.Converter;

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

    public void deleteCheck(Check check) {
        DeleteCheckTask task = new DeleteCheckTask();
        task.execute(check);
    }

    public void deleteCheckById(String id) {
        DeleteCheckById task = new DeleteCheckById();
        task.execute(id);
    }


    public String[] checkDetailsToGoogleRequestFormat(Check check) {
        String[] date = Converter.longDateToString(check.getCreationDate()).split("/");
        int d = Integer.parseInt(date[1]) + 1;
        return new String[]
                {
                        check.getCheckId(),
                        String.valueOf(check.getAmount()),
                        Converter.longDateToString(check.getCreationDate()),
                        check.getPaidTo(),
                        String.valueOf(d)
                };
    }

    private class InsertCheckTask extends AsyncTask<Check, Void, Void> {

        @Override
        protected Void doInBackground(Check... checks) {
            repo.insertCheck(checks[0]);
            return null;
        }
    }

    private class DeleteCheckTask extends AsyncTask<Check, Void, Void> {
        @Override
        protected Void doInBackground(Check... checks) {
            repo.deleteCheck(checks[0]);
            return null;
        }
    }

    private class DeleteCheckById extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            repo.deleteCheckById(strings[0]);
            return null;
        }
    }

}
