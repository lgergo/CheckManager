package com.yevsp8.checkmanager.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.yevsp8.checkmanager.data.Notification;
import com.yevsp8.checkmanager.data.NotificationRepository;

import java.util.List;

/**
 * Created by Gergo on 2018. 03. 23..
 */

public class NotificationListViewModel extends ViewModel {

    private NotificationRepository repo;

    public NotificationListViewModel(NotificationRepository repo) {
        this.repo = repo;
    }

    public LiveData<List<Notification>> getNotificationList() {
        return repo.getDataList();
    }

    public void deleteNotification(Notification notification) {
        DeleteNotificationTask task = new DeleteNotificationTask();
        task.execute(notification);
    }

    public void insertNotification(Notification notification) {
        InsertNotificationTask task = new InsertNotificationTask();
        task.execute(notification);
    }

    private class DeleteNotificationTask extends AsyncTask<Notification, Void, Void> {

        @Override
        protected Void doInBackground(Notification... notifications) {
            repo.deleteNotification(notifications[0]);
            return null;
        }
    }

    private class InsertNotificationTask extends AsyncTask<Notification, Void, Void> {

        @Override
        protected Void doInBackground(Notification... notifications) {
            repo.insertNotification(notifications[0]);
            return null;
        }
    }
}
