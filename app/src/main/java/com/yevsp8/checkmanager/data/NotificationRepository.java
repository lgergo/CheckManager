package com.yevsp8.checkmanager.data;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Gergo on 2018. 03. 23..
 */

public class NotificationRepository {

    private final NotificationDAO notificationDAO;

    @Inject
    public NotificationRepository(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public LiveData<List<Notification>> getDataList() {
        return notificationDAO.getNotificationList();
    }

    public void deleteNotification(Notification notification) {
        notificationDAO.deleteNotification(notification);
    }

    public void insertNotification(Notification notification) {
        notificationDAO.insertNotification(notification);
    }
}
